package com.home.lexa.domain.repository

import com.home.lexa.domain.models.CreateSpeakingDayRequest
import com.home.lexa.domain.models.EditSpeakingDayRequest
import com.home.lexa.domain.models.ReorderParagraphsRequest
import com.home.lexa.domain.models.ShortParagraphSpeakingDayDto

interface SpeakingDayRepository {
    suspend fun getParagraphSpeakingDay(speakingDayId: Long) : Result<ShortParagraphSpeakingDayDto?>
    suspend fun createSpeakingDay(request: CreateSpeakingDayRequest): Result<Long>
    suspend fun editSpeakingDay(speakingDayId: Long, request: EditSpeakingDayRequest): Result<Boolean>
    suspend fun reorderParagraphs(speakingDayId: Long, request: ReorderParagraphsRequest): Result<Boolean>
    suspend fun deleteSpeakingDay(speakingDayId: Long): Result<Boolean>
}