package com.home.lexa.domain.models

import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class UpdateProfileRequest (
    val id: Int,
    val fullName: String,
    val DoB: String,
    val address: String
)
@Serializable
data class GetProfileResponse  (
    val id: Int,
    val fullName: String?,
    @Serializable(with = DateSerializer::class)
    val DoB: Date?,
    val address: String?
)

@Serializable
data class Profile(
    val id: Int,
    val fullName: String?,
    val DoB: Date?,
    val address: String?,
    val avatarUrl: String?,
    val email: String?,
    val activeCourses: Int ,
    val vocabularies: Int ,
    val vocabSets: Int
)
@Serializable
data class UpdateFcmTokenRequest(
    val fcmToken: String
)
@Serializable
data class GetAchievementResponse (
    val countStudent: Int,
    val countFavorite: Int
)

