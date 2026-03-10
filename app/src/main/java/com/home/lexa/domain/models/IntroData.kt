package com.home.lexa.domain.models
import kotlinx.serialization.Serializable

@Serializable
data class IntroData(
    val title: String,
    val description: String,
    val buttonText: String
)
