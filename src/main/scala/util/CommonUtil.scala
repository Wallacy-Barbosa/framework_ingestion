package util

object CommonUtil {
    // Defines the size per file
    // NOTE: our HDFS block size is 256 MB (how to check in the cluster: "hdfs getconf -confKey dfs.blocksize")
    val sizePerFile: Int = 256 * 1024 * 1024

    // Variable to describe step of process
    var stepDesc: String = ""
}
