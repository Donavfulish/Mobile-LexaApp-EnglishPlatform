package com.home.lexa.domain.models
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class Profile(
    val id: Int,
    val fullName: String?,
    val DoB: Date?,
    val address: String?,
    val avatarUrl: String?,
    val email: String?,
    // Dữ liệu mock thêm vào để hiển thị UI
    val activeCourses: Int = 12,
    val vocabularies: Int = 450,
    val vocabSets: Int = 8
)
