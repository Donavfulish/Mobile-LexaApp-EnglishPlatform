package com.home.lexa.domain.repository

import com.home.lexa.domain.models.CreateSpeakingDayRequest
import com.home.lexa.domain.models.EditSpeakingDayRequest
import com.home.lexa.domain.models.ReorderParagraphsRequest
import com.home.lexa.domain.models.ShortParagraphSpeakingDayDto
import com.home.lexa.domain.models.SpeakingDayPagination

interface SpeakingDayRepository {
    suspend fun getSpeakingDays(courseId: Long, nextOrder: Long?): Result<SpeakingDayPagination>
    suspend fun getParagraphSpeakingDay(speakingDayId: Long, skipCache: Boolean = false) : Result<ShortParagraphSpeakingDayDto?>
    suspend fun createSpeakingDay(request: CreateSpeakingDayRequest): Result<Long>
    suspend fun editSpeakingDay(courseId: Long, speakingDayId: Long, request: EditSpeakingDayRequest): Result<Boolean>
    suspend fun reorderParagraphs(courseId: Long, speakingDayId: Long, request: ReorderParagraphsRequest): Result<Boolean>
    suspend fun deleteSpeakingDay(courseId: Long, speakingDayId: Long): Result<Boolean>
}