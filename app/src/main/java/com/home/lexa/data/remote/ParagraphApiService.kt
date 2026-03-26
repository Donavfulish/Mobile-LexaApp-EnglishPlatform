package com.home.lexa.data.remote

import com.home.lexa.core.network.ApiResponse
import com.home.lexa.domain.models.CreateParagraphRequest
import com.home.lexa.domain.models.ParagraphResponseDto
import com.home.lexa.domain.models.UpdateParagraphRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ParagraphApiService {
    @POST("api/paragraph")
    suspend fun createParagraph(
        @Body request: CreateParagraphRequest
    ): Response<ApiResponse<ParagraphResponseDto>>

    @PATCH("api/paragraph/{paragraphId}/info")
    suspend fun updateParagraphInfo(
        @Path("paragraphId") paragraphId: Long,
        @Body request: UpdateParagraphRequest
    ): Response<ApiResponse<ParagraphResponseDto>>

    @DELETE("api/paragraph/{paragraphId}")
    suspend fun deleteParagraph(
        @Path("paragraphId") paragraphId: Long
    ): Response<ApiResponse<Unit>>
}