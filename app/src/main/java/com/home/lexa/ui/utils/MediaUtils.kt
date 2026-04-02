package com.home.lexa.ui.utils

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns

object MediaUtils {
    fun getFileName(context: Context, uri: Uri): String {
        var name = "Unknown"
        // Gọi thông qua context
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) name = it.getString(nameIndex)
            }
        }
        return name
    }
}