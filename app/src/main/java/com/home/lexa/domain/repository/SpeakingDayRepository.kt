package com.home.lexa.domain.repository

import com.home.lexa.domain.models.CreateSpeakingDayRequest
import com.home.lexa.domain.models.EditSpeakingDayRequest

interface SpeakingDayRepository {
    suspend fun createSpeakingDay(request: CreateSpeakingDayRequest): Result<Long>
    suspend fun editSpeakingDay(speakingDayId: Long, request: EditSpeakingDayRequest): Result<Boolean>
    suspend fun deleteSpeakingDay(speakingDayId: Long): Result<Boolean>
}