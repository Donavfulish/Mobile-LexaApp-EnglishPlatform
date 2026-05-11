package com.home.lexa.ui.components

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import androidx.core.content.ContextCompat
import com.home.lexa.R
import com.home.lexa.databinding.ViewPopupBinding


class Popup @JvmOverloads constructor(
    context: Context,
    themeResId: Int = 0
) : Dialog(context, themeResId) {

    private lateinit var binding: ViewPopupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding = ViewPopupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window?.setLayout(
            (context.resources.displayMetrics.widthPixels * 0.9).toInt(),
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }


    fun showDialog(
        title: String,
        subTitle: String,
        isWarning: Boolean = false,
        confirmText: String = "Xác nhận",
        onConfirm: () -> Unit,
        onCancel: () -> Unit = {}
    ) {

        show()
        binding.tvTitle.text = title
        binding.tvSubTitle.text = subTitle
        binding.btnConfirm.text = confirmText

        if (isWarning) {
            binding.btnConfirm.backgroundTintList = context.getColorStateList(android.R.color.holo_red_dark)
            binding.tvTitle.setTextColor(context.getColor(android.R.color.holo_red_dark))
        } else {
            binding.btnConfirm.backgroundTintList = android.content.res.ColorStateList.valueOf(ContextCompat.getColor(context, R.color.c_6a65e9))
            binding.tvTitle.setTextColor(ContextCompat.getColor(context, R.color.c_202124))
        }


        binding.btnCancel.setOnClickListener {
            onCancel.invoke()
            dismiss()
        }

        binding.btnConfirm.setOnClickListener {
            onConfirm.invoke()
            dismiss()
        }
    }
}