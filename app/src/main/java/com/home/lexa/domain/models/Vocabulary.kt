package com.home.lexa.domain.models
import kotlinx.serialization.Serializable

data class ColorLabel(
    val label: String,
    val colorString: String // Mã màu HEX như "#6366F1"
)

@Serializable
data class Vocabulary(
    val level: ColorLabel,
    val image: Int,
    val word: String,
    val pronunciation_url: String,
    val transciption: String,
    val part_of_speech: ColorLabel,
    val definition: String,
    val example: String
)

var mockVocabularyData = Vocabulary(
    ColorLabel("C1", "#6366F1"),
    0,
    "Ephemeral",
    "",
    "/əˈfem(ə)rəl/",
    ColorLabel("adjective", "#6366F1"),
    "lasting for a very short time and until dawn.",
    "The autumnal colors are beautiful but ephemeral, are beautiful but ephemeral."
)
