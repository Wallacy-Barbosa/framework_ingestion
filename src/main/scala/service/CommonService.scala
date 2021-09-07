package service

import org.apache.spark.sql.SparkSession

object CommonService {
    def createSparkSession(sizePerFile: Int): SparkSession = SparkSession
      .builder()
      .config("spark.sql.files.maxPartitionBytes", sizePerFile)
      .getOrCreate()

    def closeSparkSession(sparkSession: SparkSession): Unit = sparkSession.close()

    def checkFileHeader(officialHeader: List[String], fileHeader: List[String]): Int = {
        if (officialHeader.equals(fileHeader)) {
            return 1
        }

        0
    }
}
