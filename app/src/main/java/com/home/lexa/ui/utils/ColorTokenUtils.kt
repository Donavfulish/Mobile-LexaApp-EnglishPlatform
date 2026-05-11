package com.home.lexa.ui.utils

import android.content.Context
import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.home.lexa.R

object ColorTokenUtils {
    /**
     * Resolve a color token to a @ColorInt.
     *
     * Supported formats:
     * - "#RRGGBB" / "#AARRGGBB"
     * - "@color/<name>" (e.g. "@color/brand_primary")
     */
    @ColorInt
    fun resolve(context: Context, token: String?): Int {
        val value = token?.trim().orEmpty()
        if (value.isEmpty()) {
            return ContextCompat.getColor(context, R.color.text_muted)
        }

        return try {
            when {
                value.startsWith("#") -> Color.parseColor(value)

                value.startsWith("@color/") -> {
                    val colorName = value.removePrefix("@color/")
                    val resId = context.resources.getIdentifier(colorName, "color", context.packageName)
                    if (resId != 0) ContextCompat.getColor(context, resId)
                    else ContextCompat.getColor(context, R.color.text_muted)
                }

                else -> Color.parseColor(value)
            }
        } catch (_: Exception) {
            ContextCompat.getColor(context, R.color.text_muted)
        }
    }
}
