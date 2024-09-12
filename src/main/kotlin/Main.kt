package org.example

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import org.example.repository.CourseRepo
import org.example.repository.StudentRepo
import java.sql.SQLException
import java.util.*

    fun main(args: Array<String>) {
        val scanner = Scanner(System.`in`)
        while (true) {
            println("Select an option:")
            println("1. Get Students")
            println("2. Get Courses")
            println("3. Get Courses with Students")
            println("4. get Course/'s students list")
            println("5. getOngoingListForStudent")
            println("6. getCompletedListForStudent")
            println("7. studentGrade")
            println("8. Insert Student")
            println("9. Insert Course")
            println("10. Register Course")
            println("11. Update Course")
            println("12. Delete Course")
            println("13. Update Student")
            println("14. Delete Student")
            println("15. Get List of Students Details")
            println("16. TO EXIT")

            val choice = scanner.nextInt()
            scanner.nextLine()
            val s = StudentRepo()
            val c = CourseRepo()
            when (choice) {
                1 -> toString(s.getStudents())
                2 -> toString(c.getCourses())
                3 -> try {
                    val out  = c.getCourseStudent()
                    toString(out)

                } catch (e: SQLException) {
                    e.printStackTrace()
                }

                4 -> try {
                    print("Provide the name of the Course: ")
                    val course = scanner.next()
                    print("Provide the course status: ")
                    val status = scanner.next()
                    val out = c.getCourseStudentwithStatus(course, status)
                    toString(out)
                } catch (e: SQLException) {
                    e.printStackTrace()
                }

                5 -> try {
                    println()
                    print("Provide the Student id: ")
                    val id = scanner.nextInt()
                    val out = s.getOngoingListForStudent(id)
                    toString(out)
                } catch (e: SQLException) {
                    e.printStackTrace()
                }

                6 -> try {
                    println()
                    print("Provide the Student id: ")
                    val id = scanner.nextInt()
                    val out = s.getCompletedListForStudent(id)
                    toString(out)
                } catch (e: SQLException) {
                    e.printStackTrace()
                }

                7 -> {
                    val sc = Scanner(System.`in`)
                    println()
                    print("Provide the Student id: ")
                    val id = sc.nextInt()
                    val out = s.getStudentGrade(id)
                    toString(out)
                }

                8 -> {
                    println("Enter student name:")
                    val studentName = scanner.nextLine()
                    println(s.createStudent(studentName))
                }

                9 -> {
                    println("Enter course name:")
                    val courseName = scanner.nextLine()
                    println("Enter credit:")
                    val credit = scanner.nextInt()
                    println("Enter duration:")
                    val duration = scanner.nextInt()
                        println(c.createCourse(courseName, credit, duration))

                }

                10 -> {
                    println("Enter course id:")
                    val idC = scanner.nextInt()
                    println("Enter Student id:")
                    val idS = scanner.nextInt()
                    println(s.register(idS, idC))
                }

                12 -> {
                    println("Enter course id:")
                    val idc = scanner.nextInt()
                    println(c.delete(idc))
                }

                13 -> {
                    println("ENter the Student id")
                    val idst = scanner.nextInt()
                    println("Enter the new name")
                    val name = scanner.next()
                    println(s.updateStudent(idst, name))
                }

                14 -> {
                    println("Enter Student id:")
                    val ids = scanner.nextInt()
                    println(s.deleteStudent(ids))
                }

                15 -> {
                    val array = ArrayList<Int>()
                    println("Size of the input")
                    val n = scanner.nextInt()
                    var i = 0
                    while (i < n) {
                        println("enter studentid: ")
                        array.add(scanner.nextInt())
                        i++
                    }
                    toString(s.getStudentsCourseList(array))
                }

                16 -> {
                    println("Exiting...")
                    scanner.close()
                    System.exit(0)
                }

                11 -> {
                    println("ENter Course id:")
                    val id = scanner.nextInt()
                    println("credit :")
                    val credit = scanner.nextInt()
                    print("duration :")
                    val duration = scanner.nextInt()
                    println(c.updateCourse(id,credit,duration))
                }
                else -> println("Invalid choice, please try again.")
            }
        }
    }

fun toString(j:JsonArray)
{
    for(arr in j)
    {
        if(arr is JsonObject) {
            for ((key, value) in arr) {
                    print(" ${key} : ${value} ")
            }
            println(" ")
        }
    }
}
