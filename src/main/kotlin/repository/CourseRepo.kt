package org.example.repository

import DataSource
import kotlinx.serialization.json.*
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet


class CourseRepo {
    val dbManager = DataSource()

    fun getCourses(): JsonArray {
        val jsonArray = buildJsonArray {
            dbManager.getConnection().use { conn ->
                val statement = conn.createStatement()
                val resultSet = statement.executeQuery("SELECT * FROM Course;")

                while (resultSet.next()) {
                    val studentId = resultSet.getInt("id")
                    val studentName = resultSet.getString("name")
                    val credit = resultSet.getString("credit")
                    val duration = resultSet.getString("duration")


                    val jsonStudent = buildJsonObject {
                        put("id", studentId)
                        put("name", studentName)
                        put("credit", credit)
                        put("duration", duration)
                    }
                    add(jsonStudent)
                }
                resultSet.close()
                statement.close()
            }
        }
        return jsonArray
    }

    fun createCourse(name: String, credit: Int, duration: Int): String {
        val insertQuery = "INSERT INTO Course(name,credit,duration) VALUES(?,?,?)"

        dbManager.getConnection().use { conn ->
            val ps: PreparedStatement = conn.prepareStatement(insertQuery);
            ps.setString(1, name)
            ps.setInt(2, credit)
            ps.setInt(3, duration)
            val rowsAffected: Int = ps.executeUpdate()
            ps.close()

            return "Rows affected: $rowsAffected";
        }
    }

    fun updateCourse(cid: Int, credit: Int, duration: Int): String {
        val query = "update Course set credit = ?, duration=? where id=?;"
        dbManager.getConnection().use { conn ->
            val ps = conn.prepareStatement(query)
            ps.setInt(1, credit)
            ps.setInt(2, duration)
            ps.setInt(3, cid)

            val rowsAffected: Int = ps.executeUpdate()
            ps.close()
            dbManager.trackCourse()
            return "Rows affected: $rowsAffected";
        }
    }

    fun delete(id:Int):String
    {
        val query = "Delete from Course where id=?"
        dbManager.getConnection().use { conn ->
            val ps: PreparedStatement = conn.prepareStatement(query);
            ps.setInt(1, id)

            val rowsAffected: Int = ps.executeUpdate()
            ps.close()

            return "Rows affected: $rowsAffected";
        }
    }

    fun getCourseStudentwithStatus(course: String, status: String): JsonArray {
        val query = """
        SELECT s.name as StudentName, s.id as Studentid, 
               sc.registeredAt as registeredTime 
        FROM StudentCourse sc
        JOIN Student s ON s.id = sc.Studentid
        JOIN Course c ON c.id = sc.Courseid
        WHERE c.name = ? AND sc.courseStatus = ?
    """
        val out = buildJsonArray {
            dbManager.getConnection().use { conn: Connection ->
                conn.prepareStatement(query).use { ps ->
                    ps.setString(1, course)
                    ps.setString(2, status)
                    val result: ResultSet = ps.executeQuery()

                        while (result.next()) {
                            val studentId = result.getInt("Studentid")
                            val studentName = result.getString("StudentName")
                            val registeredTime = result.getString("registeredTime")

                            add(buildJsonObject {
                                put("id", studentId)
                                put("name", studentName)
                                put("Registered At", registeredTime)
                            })
                        }
                        result.close()
                    }
                }
            }
        return out;
            }

    fun getCourseStudent(): JsonArray {
        val query = "SELECT c.name AS course, " +
                "GROUP_CONCAT(DISTINCT s.name ORDER BY s.name ASC) AS students, " +
                "COUNT(DISTINCT s.name) AS studentCount " +
                "FROM StudentCourse sc " +
                "JOIN Student s ON s.id = sc.StudentId " +
                "JOIN Course c ON c.id = sc.CourseId " +
                "GROUP BY c.name, c.id;"

        val out = buildJsonArray {
            dbManager.getConnection().use { conn ->
                val ps = conn.prepareStatement(query)
                val res = ps.executeQuery();

                while (res.next()) {
                    val course = res.getString("course")
                    val count = res.getInt("studentCount")
                    val studs = res.getString("students")
                    val jsonDetails = buildJsonObject {
                        put("CourseName", course)
                        put("Students Registered", studs)
                        put("Student Count", count)
                    }
                    add(jsonDetails)
                }
            }
        }
        return out;
    }


}