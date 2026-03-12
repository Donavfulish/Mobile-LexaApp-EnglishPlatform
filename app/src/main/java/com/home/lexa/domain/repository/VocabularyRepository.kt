package com.home.lexa.data.repository
import com.home.lexa.domain.models.ColorLabel
import com.home.lexa.domain.models.Course
import com.home.lexa.domain.models.Vocabulary
import kotlinx.coroutines.delay

class VocabularyRepository {
    // suspend function là async/await của Kotlin
    suspend fun fetchOneExampleVocabulary(): Vocabulary {
        //delay(1000) // Giả lập gọi API mất 1 giây
        return Vocabulary(
            ColorLabel("C1", "#6366F1"),
            1,
            "Ephemeral",
            "",
            "/əˈfem(ə)rəl/",
            ColorLabel("adjective", "#6366F1"),
            "lasting for a very short time.",
            "The autumnal colors are beautiful but ephemeral."
        )
    }
}