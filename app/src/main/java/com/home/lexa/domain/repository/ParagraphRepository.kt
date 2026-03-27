package com.home.lexa.domain.repository

import com.home.lexa.domain.models.CreateParagraphRequest
import com.home.lexa.domain.models.ParagraphResponseDto
import com.home.lexa.domain.models.ParagraphResultResponseDto
import com.home.lexa.domain.models.UpdateParagraphRequest
import com.home.lexa.domain.models.UpdateParagraphResultRequest

interface ParagraphRepository {
    suspend fun createParagraph(request: CreateParagraphRequest): Result<ParagraphResponseDto>
    suspend fun updateParagraph(paragraphId: Long, request: UpdateParagraphRequest): Result<ParagraphResponseDto>
    suspend fun deleteParagraph(paragraphId: Long): Result<Unit>
    suspend fun updateParagraphResult(request: UpdateParagraphResultRequest): Result<ParagraphResultResponseDto>
}