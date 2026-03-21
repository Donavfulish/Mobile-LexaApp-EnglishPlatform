package com.home.lexa.domain.models
import kotlinx.serialization.Serializable
import java.util.Date

data class UpdateProfileRequest (
    val id: Int,
    val fullName: String,
    @Serializable(with = DateSerializer::class)
    val DoB: Date,
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
