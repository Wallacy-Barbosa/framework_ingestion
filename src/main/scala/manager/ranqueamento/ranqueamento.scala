package manager.ranqueamento

import org.apache.spark.sql.types.{StringType, StructField, StructType}
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.expressions._
import service.CommonService._
import util.CommonUtil._

object ranqueamento extends App {

    // TODO: Include logs and test ranking

    println("--------------------------------------------------")
    stepDesc = "Validando preenchimento dos parâmetros"
    println(stepDesc)
    println("--------------------------------------------------")
    println(" ")

    //Checks if the parameters were entered correctly

    // Initializing variables
    var errorMessage: String = ""
    var databaseName: String = ""
    var tableName: String = ""
    var partitionTable: String = ""
    var partitionFields: List[String] = List("")
    var sortingFields: List[String] = List("")
    var sortingType: String = ""

    println("--------------------------------------------------")
    println(" ")

    // Getting parameters by name

    for (arg <- args) {

        val lowerArg = arg.toLowerCase()

        if (lowerArg.contains("database_name") && lowerArg.split("=").length == 2) databaseName = lowerArg.split("=")(1).trim()
        if (lowerArg.contains("table_name") && lowerArg.split("=").length == 2) tableName = lowerArg.split("=")(1).trim()
        if (lowerArg.contains("partition_table") && lowerArg.split("=").length == 2) partitionTable = lowerArg.split("=")(1).trim()

        if (lowerArg.contains("partition_fields") && lowerArg.split("=").length == 2) {
            if (lowerArg.split("=")(1).split(",").length > 0) {
                partitionFields = lowerArg.split("=")(1).split(",").toList.map(_.trim())
            }
        }

        if (lowerArg.contains("sorting_fields") && lowerArg.split("=").length == 2) {
            if (lowerArg.split("=")(1).split(",").length > 0) {
                sortingFields = lowerArg.split("=")(1).split(",").toList.map(_.trim())
            }
        }

        if (lowerArg.contains("sorting_type") && lowerArg.split("=").length == 2) {
            if (List("asc", "desc").contains(lowerArg.split("=")(1))) {
                sortingType = lowerArg.split("=")(1).trim()
            }
        }
    }

    //Check that all parameters have been entered

    if (databaseName.isEmpty) errorMessage = "O Parâmetro databaseName está vazio."
    if (tableName.isEmpty) errorMessage = "O Parâmetro tableName está vazio."
    if (partitionTable.isEmpty) errorMessage = "O Parâmetro partitionTable está vazio."

    if (partitionFields(0).isEmpty) errorMessage = "O Parâmetro partitionFields está vazio."
    if (sortingFields(0).isEmpty) errorMessage = "O Parâmetro sortingFields está vazio."

    if (sortingType.isEmpty) errorMessage = "O Parâmetro sortingType deve ser preenchido com asc ou desc."

    // Throw exception if something wrong with parameters
    if (errorMessage.nonEmpty) throw new Exception ("ERRO: " + errorMessage)

    stepDesc = "Parâmetros validados com sucesso"
    println(stepDesc)

    stepDesc = "Iniciando Ranqueamento da tabela: " + tableName
    println(stepDesc)

    // Creating the SparkSession
    val sparkSession:SparkSession = createSparkSession(sizePerFile)

    var dataFrame:DataFrame = sparkSession.read.table(databaseName + "." + tableName)

    //Sort by oldest record
    if (sortingType == "asc")
        dataFrame = dataFrame.withColumn("row_number", row_number().over(Window.partitionBy(partitionFields.map(col):_*).orderBy(sortingFields.map(col):_*)))

    //Sort by most recent record
    if (sortingType == "desc")
        dataFrame = dataFrame.withColumn("row_number", row_number().over(Window.partitionBy(partitionFields.map(col(_)):_*).orderBy(sortingFields.map(col(_).desc):_*)))

    val dataFrameRanked:DataFrame = dataFrame.filter(col("row_number") === 1).drop(col("row_number"))

    dataFrameRanked.
      write.
      partitionBy(partitionTable).
      format("parquet").
      option("compression", "snappy").
      mode("overwrite"). // TODO: Pending check table downtime
      saveAsTable(tableName + "_rank_wb")

    stepDesc = "Tabela ranqueada com sucesso"
    println(stepDesc)

    //Closing the sparkSession
    closeSparkSession(sparkSession)

}