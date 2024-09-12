import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.sql.Connection
import java.sql.ResultSet
import java.sql.Statement


class DataSource {
    private val dataSource: HikariDataSource

    init {
        val config = HikariConfig().apply {
            jdbcUrl = "jdbc:mysql://10.200.70.124:3306/StudentCourse"
            username = "rootUser"
            password = "12345"
            maximumPoolSize = 10
            connectionTimeout = 30000
            idleTimeout = 600000
            maxLifetime = 1800000
        }

        dataSource = HikariDataSource(config)
    }

    fun getConnection(): Connection {
        return dataSource.connection
    }

    fun close() {
        dataSource.close()
    }

    fun trackCourse() {
        val con: Connection = getConnection()
        val q = """UPDATE StudentCourse sc
JOIN Course c ON sc.Courseid = c.id
SET 
    sc.courseStatus = CASE
        WHEN NOW() < sc.registeredAt THEN 'YET TO START'
        WHEN NOW() BETWEEN sc.registeredAt AND DATE_ADD(sc.registeredAt, INTERVAL c.duration DAY) THEN 'ONGOING'
        WHEN NOW() > DATE_ADD(sc.registeredAt, INTERVAL c.duration DAY) THEN 'COMPLETED'
        ELSE 'TERMINATED'
    END;"""
        val stmt: Statement = con.createStatement()
        val result: ResultSet = stmt.executeQuery(q)
        result.close()
    }
}


