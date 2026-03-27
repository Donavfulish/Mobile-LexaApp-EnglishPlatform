package com.home.lexa.data.remote

import com.home.lexa.core.network.ApiResponse
import com.home.lexa.domain.models.CreateCourseRequest
import com.home.lexa.domain.models.CreateSpeakingDayRequest
import com.home.lexa.domain.models.EditCourseRequest
import com.home.lexa.domain.models.EditSpeakingDayRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface SpeakingDayApiService {

    @POST("api/users/me/speaking-day")
    suspend fun createSpeakingDay(@Body request: CreateSpeakingDayRequest): Response<ApiResponse<Map<String, Long>>>

    @PATCH("api/users/me/speaking-day/{speakingDayId}")
    suspend fun editSpeakingDay(@Path("speakingDayId") speakingDayId: Long, @Body request: EditSpeakingDayRequest): Response<ApiResponse<Map<String, Any>>>

    @DELETE("api/users/me/speaking-day/{speakingDayId}")
    suspend fun deleteSpeakingDay(@Path("speakingDayId") speakingDayId: Long): Response<ApiResponse<Map<String, Any>>>

}