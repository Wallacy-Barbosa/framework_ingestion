package manager.ranqueamento

import org.apache.spark.sql.types.{StringType, StructField, StructType}
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.expressions._
import service.CommonService._
import util.CommonUtil._

object ranqueamento extends App {

    stepDesc = "Validando preenchimento dos parametros"
    println(stepDesc)

    //Check if the parameters were entered correctly

    var errorMessage: String = ""
    val tableName: String = args(0).toLowerCase
    val partitionTable: String = args(1).toLowerCase

    // TODO: Check if the split is correct
    val partitionFields: List[String] = args(2).split(",").toList.map(_.trim.toLowerCase)
    val ordenationFields: List[String] = args(3).split(",").toList.map(_.trim.toLowerCase)
    val ordenationType: String = args(4).toLowerCase

    if (tableName.isEmpty) errorMessage = "O Parâmetro tableName está vazio."
    if (partitionTable.isEmpty) errorMessage = "O Parâmetro partitionTable está vazio."

    // TODO: Check if isEmpty works in a list type variable
    if (partitionFields.isEmpty) errorMessage = "O Parâmetro partitionFields está vazio."
    if (ordenationFields.isEmpty) errorMessage = "O Parâmetro ordenationFields está vazio."
    if (ordenationType.isEmpty || !List("asc", "desc").contains(ordenationType))
        errorMessage = "O Parâmetro ordenationType deve ser preenchido com asc ou desc."

    // Throw exception if something wrong with parameters
    if (errorMessage.nonEmpty) throw new Exception ("Erro ao importar parâmetros: " + errorMessage)

    stepDesc = "Parâmetros validado com sucesso"
    println(stepDesc)

    stepDesc = "Iniciando Ranqueamento da tabela: " + tableName
    println(stepDesc)

    // Creating the SparkSession
    val sparkSession: SparkSession = createSparkSession(sizePerFile)

    val schema = StructType(
        StructField("id", StringType, nullable = true) ::
          StructField("cpf", StringType, nullable = true) ::
          StructField("nome", StringType, nullable = true) ::
          StructField("data_nascimento", StringType, nullable = true) :: Nil
    )

    var dataFrame: DataFrame = sparkSession.createDataFrame(sparkSession.emptyDataFrame.toJavaRDD, schema)

    if (ordenationType == "asc")
        dataFrame = dataFrame.withColumn("row_number", row_number().over(Window.partitionBy(partitionFields.map(col):_*).orderBy(ordenationFields.map(col):_*)))

    if (ordenationType == "desc")
        dataFrame = dataFrame.withColumn("row_number", row_number().over(Window.partitionBy(partitionFields.map(col(_)):_*).orderBy(ordenationFields.map(col(_).desc):_*)))

    val dataFrameRanked: DataFrame = dataFrame.filter(col("row_number") === 1).drop(col("row_number"))

    dataFrameRanked.
      write.
      partitionBy("reference_date").
      format("parquet").
      option("compression", "snappy").
      mode("overwrite"). // TODO: Pending check table downtime
      saveAsTable(tableName + "_rank")

    stepDesc = "Tabela ranqueada com sucesso"
    println(stepDesc)

}
