package com.home.lexa.domain.repository

import com.home.lexa.domain.models.IntroData

interface IntroRepository {
    suspend fun fetchIntroContent(): IntroData
}