package com.home.lexa.data.repository

import androidx.core.content.ContextCompat
import com.home.lexa.R
import com.home.lexa.domain.models.CourseProgress
import com.home.lexa.domain.models.CourseTest
import com.home.lexa.domain.models.ParagraphResult
import com.home.lexa.domain.models.ParagraphWord

val mockCourses = listOf(
    CourseTest(
        id = 1,
        title = "IELTS Speaking Mastery",
        teacher = "Teacher Lexa",
        topic = "Giao tiếp",
        isFavorite = false,
        imageRes = R.drawable.background_1,
        member = "200",
        favorite = "500"
    ),
    CourseTest(
        id = 2,
        title = "English for Beginners",
        teacher = "John Doe",
        topic = "Ielts",
        isFavorite = true,
        imageRes = R.drawable.background_2,
        member = "1248",
        favorite = "200"
    )
)

val mockStudyingCourses = listOf(
    CourseProgress(1, "Ngữ Pháp Nền Tảng", "Grammar", 75, R.drawable.background_1),
    CourseProgress(2, "Từ Vựng Giao Tiếp", "Vocabulary", 30, R.drawable.background_2)
)

val mockParagraphData = ParagraphResult(
    id = 1,
    paragraph = listOf(
        ParagraphWord("This", "green"),
        ParagraphWord("is", "green"),
        ParagraphWord("a", "yellow"),
        ParagraphWord("sample", "red"),
        ParagraphWord("paragraph", "green"),
        ParagraphWord("for", "yellow"),
        ParagraphWord("Lexa", "green"),
        ParagraphWord("App", "green"),
        ParagraphWord("a", "yellow"),
        ParagraphWord("sample", "red"),
        ParagraphWord("paragraph", "green"),
        ParagraphWord("for", "yellow"),
        ParagraphWord("Lexa", "green"),
        ParagraphWord("App", "green"),
        ParagraphWord("a", "yellow"),
        ParagraphWord("sample", "red"),
        ParagraphWord("paragraph", "green"),
        ParagraphWord("for", "yellow"),
        ParagraphWord("Lexa", "green"),
        ParagraphWord("App", "green")
    ),
    order = "1",
    audioUrl = "",
    userUrl = ""
)
