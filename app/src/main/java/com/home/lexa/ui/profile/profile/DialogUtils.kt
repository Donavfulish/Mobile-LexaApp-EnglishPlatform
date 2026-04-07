package com.home.lexa.ui.profile.profile

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object DialogUtils {

}

fun Context.showConfirmDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    cancelLabel: String? = "Hủy",
    acceptLabel: String? = "Đồng ý"
) {
    MaterialAlertDialogBuilder(this)
        .setTitle(title)
        .setMessage(message)
        .setNegativeButton(cancelLabel, null)
        .setPositiveButton(acceptLabel) { _, _ -> onConfirm() }
        .show()
}