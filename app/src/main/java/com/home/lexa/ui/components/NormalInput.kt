package com.home.lexa.ui.components

import android.content.Context
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import androidx.core.graphics.toColorInt
import androidx.core.widget.doOnTextChanged
import com.home.lexa.databinding.InputNormalBinding

class NormalInput @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = InputNormalBinding.inflate(LayoutInflater.from(context), this, true)

    fun setLabel(text: String?) {
        if (text.isNullOrEmpty()) {
            binding.tvLabel.visibility = View.GONE
        } else {
            binding.tvLabel.text = text
            binding.tvLabel.visibility = View.VISIBLE
        }
    }

    fun setPlaceHolderText(text: String?) {
        if (text.isNullOrEmpty()) return
        binding.etInput.hint = text
    }

    fun setIcon(@DrawableRes resId: Int?, colorHex: String? = "#575E6B") {
        if (resId == null || resId == -1) {
            binding.ivIcon.visibility = View.GONE
        } else {
            binding.ivIcon.setImageResource(resId)
            binding.ivIcon.setColorFilter(colorHex!!.toColorInt(), PorterDuff.Mode.SRC_IN)
            binding.ivIcon.visibility = View.VISIBLE
        }
    }

    fun setEndIcon(@DrawableRes resId: Int?, colorHex: String? = "#575E6B") {
        if (resId == null || resId == -1) {
            binding.ivEndIcon.visibility = View.GONE
        } else {
            binding.ivEndIcon.setImageResource(resId)
            binding.ivEndIcon.setColorFilter(colorHex!!.toColorInt(), PorterDuff.Mode.SRC_IN)
            binding.ivEndIcon.visibility = View.VISIBLE
        }
    }

    fun setOnEndIconClickListener(onClick: () -> Unit) {
        binding.ivEndIcon.setOnClickListener {
            onClick.invoke()
        }
    }

    fun setOnInputClickListener(onClick: () -> Unit) {
        binding.etInput.isFocusable = false
        binding.etInput.isClickable = true
        binding.etInput.setOnClickListener { onClick() }
        binding.containerInput.setOnClickListener { onClick() }
    }

    fun getText(): String = binding.etInput.text.toString()

    fun setText(text: String?) {
        binding.etInput.setText(text)
    }

    fun onTextChanged(action: (String) -> Unit) {
        binding.etInput.doOnTextChanged { text, _, _, _ ->
            action(text.toString())
        }
    }

    fun setInputHeight(heightInDp: Int) {
        val params = binding.containerInput.layoutParams
        params.height = (heightInDp * context.resources.displayMetrics.density).toInt()
        binding.containerInput.layoutParams = params
        
        binding.containerInput.gravity = Gravity.TOP
        binding.etInput.gravity = Gravity.TOP
            // Thêm padding top nếu cần để chữ không dính sát viền trên
        binding.etInput.setPadding(0, (4 * context.resources.displayMetrics.density).toInt(), 0, 0)
    }

    fun setEnable(isEnable: Boolean) {
        binding.etInput.isEnabled = isEnable
    }
}