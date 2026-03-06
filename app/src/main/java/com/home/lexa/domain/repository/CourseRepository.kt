package com.home.lexa.data.repository
import com.home.lexa.domain.models.Course
import kotlinx.coroutines.delay

class CourseRepository {
    // suspend function là async/await của Kotlin
    suspend fun fetchCourses(): List<Course> {
        delay(1000) // Giả lập gọi API mất 1 giây
        return listOf(Course(1, "IELTS Speaking"), Course(2, "TOEIC 800+"))
    }
}