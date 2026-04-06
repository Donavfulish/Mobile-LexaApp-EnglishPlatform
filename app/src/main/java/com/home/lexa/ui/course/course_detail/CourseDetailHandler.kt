package com.home.lexa.ui.course.course_detail

import com.home.lexa.domain.models.SpeakingCourseDetailDto
import com.home.lexa.ui.components.FlashcardMini

interface CourseDetailHandler {
    fun setupViews()
    fun bindCourseData(course: SpeakingCourseDetailDto)
    fun bindSpeakingData(course: SpeakingCourseDetailDto)
    fun bindFlashcardData(card: FlashcardMini)
    fun observerViewModel()
}
