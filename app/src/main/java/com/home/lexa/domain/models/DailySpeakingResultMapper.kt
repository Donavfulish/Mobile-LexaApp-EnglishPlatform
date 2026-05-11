package com.home.lexa.domain.models

private fun statusToColor(status: String): String = when (status.uppercase()) {
    "GOOD" -> "green"
    "MEDIUM", "ACCEPTED" -> "yellow"
    "BAD" -> "red"
    else -> "red"
}

/**
 * Maps API speaking-day payload (paragraphs + evaluations) to summary for the daily result screen.
 */
fun ShortParagraphSpeakingDayDto.toDailyResultSummary(): DailyResultSummary {
    val sorted = list_paragraphs.sortedBy { it.paragraph_order ?: 0L }

    val paragraphs = sorted.mapIndexed { index, p ->
        val paragraphWords: List<ParagraphWord> = when {
            !p.wordEvaluation.isNullOrEmpty() -> p.wordEvaluation.map { eval ->
                ParagraphWord(eval.word, statusToColor(eval.status))
            }
            !p.paragraph.isNullOrBlank() ->
                p.paragraph.split(Regex("\\s+")).filter { it.isNotBlank() }
                    .map { token -> ParagraphWord(token, "green") }
            else -> emptyList()
        }
        val orderStr = (p.paragraph_order?.toInt() ?: (index + 1)).toString()
        ParagraphResult(
            id = p.id.toInt(),
            original = p.paragraph ?: "",
            paragraph = paragraphWords,
            order = orderStr,
            audioUrl = p.audioUrl ?: "",
            userUrl = p.userAudioUrl ?: ""
        )
    }

    var totalGood = sorted.sumOf { it.goodCount ?: 0 }
    var totalAccepted = sorted.sumOf { it.mediumCount ?: 0 }
    var totalBad = sorted.sumOf { it.badCount ?: 0 }

    if (totalGood + totalAccepted + totalBad == 0) {
        totalGood = paragraphs.sumOf { para -> para.paragraph.count { it.s == "green" } }
        totalAccepted = paragraphs.sumOf { para -> para.paragraph.count { it.s == "yellow" } }
        totalBad = paragraphs.sumOf { para -> para.paragraph.count { it.s == "red" } }
    }

    return DailyResultSummary(totalGood, totalAccepted, totalBad, paragraphs)
}
