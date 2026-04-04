package com.home.lexa.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class DeckDto(
    val id: Long,
    val title: String,
    val topic: Topic? = null,
    val vocabNumber: Int,
    val createdAt: String
)

@Serializable
data class DeckResult(
    val deckId: Long,
    val userId: Int,
    val rememberedCount: Int?,
    val forgottenCount: Int?
)

@Serializable
data class CreateDeckRequest(
    val title: String,
    val creatorId: Int,
)

@Serializable
data class UpdateDeckRequest(
    val deckId: Long,
    val title: String? = null,
    val topicName: String? = null,
    val privacy: String? = null
)

@Serializable
data class InsertDeckResultRequest(
    val deckId: Long,
    val userId: Int,
    val rememberedCount: Int,
    val forgottenCount: Int
)


@Serializable
data class CreateDeckResultRequest(
    val deckId: Long,
    val userId: Int,
    val rememberedCount: Int,
    val forgottenCount: Int
)

@Serializable
data class UpdateDeckResultRequest(
    val deckId: Long,
    val rememberedCount: Int,
    val forgottenCount: Int
)