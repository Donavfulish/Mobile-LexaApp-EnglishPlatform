package com.home.lexa.data.remote

import com.home.lexa.core.network.ApiResponse
import com.home.lexa.domain.models.CreateSpeakingDayRequest
import com.home.lexa.domain.models.EditSpeakingDayRequest
import com.home.lexa.domain.models.ReorderParagraphsRequest
import com.home.lexa.domain.models.ShortParagraphSpeakingDayDto
import com.home.lexa.domain.models.SpeakingDayPagination
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface SpeakingDayApiService {
    @GET("api/speaking-day/{speakingDayId}")
    suspend fun getParagraphSpeakingDay(
        @Path("speakingDayId")
        speakingDayId: Long
    ): Response<ApiResponse<ShortParagraphSpeakingDayDto>>

    @GET("api/courses/{courseId}/speaking-days")
    suspend fun getSpeakingDays(
        @Path("courseId") courseId: Long,
        @Query("next_order") nextOrder: Long?
    ): Response<ApiResponse<SpeakingDayPagination>>

    @POST("api/users/me/speaking-day")
    suspend fun createSpeakingDay(@Body request: CreateSpeakingDayRequest): Response<ApiResponse<Long>>

    @PATCH("api/users/me/speaking-day/{speakingDayId}")
    suspend fun editSpeakingDay(@Path("speakingDayId") speakingDayId: Long, @Body request: EditSpeakingDayRequest): Response<ApiResponse<Any>>

    @PATCH("api/users/me/speaking-day/{speakingDayId}/paragraphs/reorder")
    suspend fun reorderParagraphs(
        @Path("speakingDayId") speakingDayId: Long,
        @Body request: ReorderParagraphsRequest
    ): Response<ApiResponse<Any>>

    @DELETE("api/users/me/speaking-day/{speakingDayId}")
    suspend fun deleteSpeakingDay(@Path("speakingDayId") speakingDayId: Long): Response<ApiResponse<Any>>

}
