package com.home.lexa.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.home.lexa.databinding.ButtonLexaBinding

// Kế thừa FrameLayout để bọc component XML lại
class LexaButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = ButtonLexaBinding.inflate(LayoutInflater.from(context), this, true)

    fun setText(text: String) {
        binding.txtButtonTitle.text = text
    }

    fun setOnLexaClickListener(onClick: () -> Unit) {
        binding.root.setOnClickListener {
            onClick.invoke()
        }
    }
}