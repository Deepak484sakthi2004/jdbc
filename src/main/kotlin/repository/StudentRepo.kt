package org.example.repository

import DataSource

import kotlinx.serialization.json.*
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet



class StudentRepo {
    val dbManager = DataSource()


    fun getStudents(): JsonArray {
        val jsonArray = buildJsonArray {
            dbManager.getConnection().use { conn ->
                val statement = conn.createStatement()
                val resultSet = statement.executeQuery("SELECT * FROM Student;")

                while (resultSet.next()) {
                    val studentId = resultSet.getInt("id")
                    val studentName = resultSet.getString("name")

                    val jsonStudent = buildJsonObject {
                        put("id", studentId)
                        put("name", studentName)
                    }
                    add(jsonStudent)
                }
                resultSet.close()
                statement.close()
            }
        }
        return jsonArray
    }

    fun createStudent(name: String): String {
        val insertQuery = "INSERT INTO Student(name) VALUES(?)"

        dbManager.getConnection().use { conn ->
            val ps: PreparedStatement = conn.prepareStatement(insertQuery);
            ps.setString(1, name)
            val rowsAffected: Int = ps.executeUpdate()
            ps.close()

            return "Rows affected: $rowsAffected";

        }
    }

    fun updateStudent(sid: Int, name: String): String {
        val query = "update Student set name = ? where id=?;"
        dbManager.getConnection().use { conn ->
            val ps = conn.prepareStatement(query)
            ps.setString(1, name)
            ps.setInt(2, sid)
            val rowsAffected: Int = ps.executeUpdate()
            ps.close()

            return "Rows affected: $rowsAffected";
        }
    }

    fun deleteStudent(sid: Int): String {
        val query = "Delete from Student where id=?"

        dbManager.getConnection().use { conn ->
            val ps = conn.prepareStatement(query)
            ps.setInt(1, sid)

            val rowsAffected: Int = ps.executeUpdate()
            ps.close()

            return "Rows affected: $rowsAffected";
        }
    }

    fun getStudentGrade(id: Int): JsonArray {
        val query: String =
            " SELECT c.name AS CourseName, grade " +
                    " FROM StudentCourse sc " +
                    "JOIN Course c ON c.id = sc.Courseid " +
                    "JOIN Student s ON s.id = sc.Studentid " +
                    "WHERE s.id=?;"

        val out: JsonArray = buildJsonArray {
            dbManager.getConnection().use { conn ->
                val ps: PreparedStatement = conn.prepareStatement(query);
                ps.setInt(1, id);
                val res: ResultSet = ps.executeQuery();
                while (res.next()) {
                    val courseName: String = res.getString("CourseName")
                    val grade: Int = res.getInt("grade")

                    val course_grade: JsonObject = buildJsonObject {
                        put("CourseName", courseName)
                        put("Grade", grade)
                    }
                    add(course_grade);
                }
                res.close()
                ps.close()
            }
        }
        return out;
    }

    fun register(sid: Int, cid: Int): String {
        val query: String = "INSERT INTO StudentCourse(Studentid,Courseid) values (?,?)"

        dbManager.getConnection().use { conn ->
            val ps = conn.prepareStatement(query)
            ps.setInt(1, sid)
            ps.setInt(2, cid)

            val rowsAffected: Int = ps.executeUpdate()
            ps.close()
            return "Rows affected: $rowsAffected";
        }
    }

    fun getOngoingListForStudent(id: Int): JsonArray {
        val query = "SELECT c.name AS CourseName, " +
                "c.id AS CourseId, " +
                "TIMESTAMPDIFF(DAY, NOW(), DATE_ADD(sc.registeredAt, INTERVAL c.duration DAY)) AS DaysRemaining " +
                "FROM StudentCourse sc " +
                "JOIN Course c ON c.id = sc.Courseid " +
                "JOIN Student s ON s.id = sc.Studentid " +
                "WHERE sc.courseStatus = \"ONGOING\" AND s.id = ?"

        val out = buildJsonArray {
            dbManager.getConnection().use { conn: Connection ->

                val ps = conn.prepareStatement(query);
                ps.setInt(1, id);

                val res = ps.executeQuery();
                while (res.next()) {
                    val cname = res.getString("CourseName")
                    val cid = res.getInt("CourseId")
                    val days = res.getInt("DaysRemaining");

                    val curCourse = buildJsonObject {
                        put("CourseId", cid)
                        put("CourseName", cname)
                        put("DaysRemaining", days)
                    }
                    add(curCourse)
                }
                res.close();
                ps.close();
            }
        }
        return out;
    }

    fun getCompletedListForStudent(id: Int): JsonArray {
        val query =
            "SELECT c.name AS CourseName, " +
                    "c.id AS CourseId, " +
                    "sc.registeredAt as StartedAt " +
                    "FROM StudentCourse sc " +
                    "JOIN Course c ON c.id = sc.Courseid " +
                    "JOIN Student s ON s.id = sc.Studentid " +
                    "WHERE sc.courseStatus = \"COMPLETED\" AND s.id = ?"

        val out = buildJsonArray {
            dbManager.getConnection().use { conn: Connection ->

                val ps = conn.prepareStatement(query);
                ps.setInt(1, id);

                val res = ps.executeQuery();
                while (res.next()) {
                    val cname = res.getString("CourseName")
                    val cid = res.getInt("CourseId")
                    val days = res.getString("StartedAt");

                    val curCourse = buildJsonObject {
                        put("CourseId", cid)
                        put("CourseName", cname)
                        put("StartedAt", days)
                    }
                    add(curCourse)
                }
                res.close();
                ps.close();
            }
        }
        return out;
    }

    fun getListOfStudentCourses(studentIds: ArrayList<Int>): JsonArray {
        val query = """
        SELECT sc.Studentid, c.name AS CourseName
        FROM StudentCourse sc
        JOIN Course c ON c.id = sc.Courseid
        WHERE sc.Studentid IN (${studentIds.joinToString(",")})
    """

        val out = buildJsonArray {
            dbManager.getConnection().use { conn: Connection ->
                val ps = conn.prepareStatement(query)
                ps.use {
                    val res = ps.executeQuery()
                    res.use { resultSet ->
                        while (resultSet.next()) {
                            val courseName = resultSet.getString("CourseName")
                            val studentId = resultSet.getInt("Studentid")

                            val curCourse = buildJsonObject {
                                put("StudentId", JsonPrimitive(studentId))
                                put("CourseName", JsonPrimitive(courseName))
                            }
                            add(curCourse)
                        }
                    }
                }
            }
        }
        return out;
    }

    fun getStudentsCourseList(studentIds: List<Int>): JsonArray {
        val query = """
        SELECT sc.Studentid, c.name AS CourseName
        FROM StudentCourse sc
        JOIN Course c ON c.id = sc.Courseid
        WHERE sc.Studentid IN (${studentIds.joinToString(",")})
    """

        val coursesByStudentId = mutableMapOf<Int, MutableList<String>>()

        dbManager.getConnection().use { conn ->
            val ps = conn.prepareStatement(query)
            val res = ps.executeQuery()

            while (res.next()) {
                val studentId = res.getInt("Studentid")
                val courseName = res.getString("CourseName")

                if (studentId !in coursesByStudentId) {
                    coursesByStudentId[studentId] = mutableListOf()
                }
                coursesByStudentId[studentId]?.add(courseName)
            }
            res.close()
            ps.close()
        }

        return buildJsonArray {
            coursesByStudentId.forEach { (studentId, courses) ->
                add(buildJsonObject {
                    put("StudentId", studentId)
                    put("Courses", buildJsonArray {
                        courses.forEach { courseName ->
                            add(JsonPrimitive(courseName))
                        }
                    })
                })
            }
        }
    }
}
