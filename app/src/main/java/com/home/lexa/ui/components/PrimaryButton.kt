package com.home.lexa.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.home.lexa.databinding.ViewBtnPrimaryBinding

class PrimaryButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding = ViewBtnPrimaryBinding.inflate(LayoutInflater.from(context), this)

    fun setText(text: String) {
        binding.tvTitle.text = text
    }

    fun setOnClickAction(action: () -> Unit) {
        binding.root.setOnClickListener { action.invoke() }
    }
}





