package com.home.lexa.ui.course.course_detail

import com.home.lexa.domain.models.DetailFlashcard
import com.home.lexa.domain.models.CourseDetailDto
import com.home.lexa.ui.components.FlashcardMini

interface CourseDetailHandler {
    fun setupViews()
    fun bindCourseData(course: CourseDetailDto)
    fun bindSpeakingData(course: CourseDetailDto)
    fun bindFlashcardData(item: DetailFlashcard, card: FlashcardMini)
    fun observerViewModel()
}
