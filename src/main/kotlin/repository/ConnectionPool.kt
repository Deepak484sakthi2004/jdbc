package org.example.repository
//
//import java.sql.Connection
//import java.sql.DriverManager
//import java.util.concurrent.ArrayBlockingQueue
//import kotlin.jvm.Throws
//
//class ConnectionPool(
//    private val url: String,
//    private val user: String,
//    private val password: String,
//    poolSize: Int
//) : AutoCloseable {
//
//    // A blocking queue to manage the connection pool
//    private val connectionPool: ArrayBlockingQueue<Connection> = ArrayBlockingQueue(poolSize)
//
//    init {
//        // Pre-create connections and add them to the pool
//        repeat(poolSize) {
//            connectionPool.add(createConnection())
//        }
//    }
//
//    private fun createConnection(): Connection {
//        return DriverManager.getConnection(url, user, password)
//    }
//
//    @Throws(InterruptedException::class)
//    fun acquireConnection(): Connection {
//        return connectionPool.take()
//    }
//
//    fun releaseConnection(connection: Connection) {
//        connectionPool.put(connection)
//    }
//
//    override fun close() {
//        while (!connectionPool.isEmpty()) {
//            connectionPool.poll()?.close()
//        }
//    }
//}
//
////fun main() {
////    val url = "jdbc:mysql://localhost:3306/StudentCourse"
////    val user = "rootUser"
////    val password = "12345"
////    val poolSize = 5
////
////    ConnectionPool(url, user, password, poolSize).use { pool ->
////        val connection = pool.acquireConnection()
////
////        val query = "SELECT * FROM Student;"
////        connection.createStatement().use { statement ->
////            val resultSet = statement.executeQuery(query)
////            while (resultSet.next()) {
////                println("Student Name: ${resultSet.getString("name")}")
////            }
////        }
////
////        pool.releaseConnection(connection)
////    }
////}
