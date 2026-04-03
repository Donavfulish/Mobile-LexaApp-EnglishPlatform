package com.home.lexa.ui.utils

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

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

    fun prepareFilePart(context: Context, partName: String, fileUri: Uri): MultipartBody.Part? {
        return try {
            val inputStream = context.contentResolver.openInputStream(fileUri)
            val bytes = inputStream?.readBytes() ?: return null
            inputStream.close()

            val requestFile = bytes.toRequestBody(
                context.contentResolver.getType(fileUri)?.toMediaTypeOrNull(),
                0, bytes.size
            )

            // Lấy tên file thật để server lưu đúng extension (.png, .jpg)
            val fileName = getFileName(context, fileUri)

            MultipartBody.Part.createFormData(partName, fileName, requestFile)
        } catch (e: Exception) {
            null
        }
    }
}