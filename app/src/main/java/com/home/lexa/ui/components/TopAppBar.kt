package com.home.lexa.ui.components

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.home.lexa.databinding.ViewTopAppBarBinding
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet


class TopAppBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private val binding = ViewTopAppBarBinding.inflate(LayoutInflater.from(context), this, true)

    fun setText(text: String){
        binding.title.text = text
    }

    fun setBackButtonVisible(isVisible: Boolean){
        binding.leftBtn.visibility = if (isVisible) android.view.View.VISIBLE else android.view.View.GONE
    }

    fun insertCustomeView(view: View){
        if (view.id == View.NO_ID) {
            view.id = View.generateViewId()
        }
        binding.topBarLayout.addView(view)

        val constraintSet = ConstraintSet()
        constraintSet.clone(binding.topBarLayout)

        constraintSet.constrainWidth(view.id, ConstraintSet.WRAP_CONTENT)
        constraintSet.constrainHeight(view.id, ConstraintSet.WRAP_CONTENT)

        constraintSet.connect(view.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 20)
        constraintSet.connect(view.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
        constraintSet.connect(view.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)

        constraintSet.applyTo(binding.topBarLayout)
    }

    fun setIconRightButton(icon: Drawable){
        binding.rightBtn.visibility = android.view.View.VISIBLE
        binding.rightBtn.setImageDrawable(icon)
    }

    fun setRightButtonSelected(isSelected: Boolean) {
        binding.rightBtn.isSelected = isSelected
    }

    fun setOnClickToggleRightButton(onToggle: (Boolean) -> Unit){
        binding.rightBtn.setOnClickListener {
            val selected = !binding.rightBtn.isSelected
            binding.rightBtn.isSelected = selected
            onToggle(selected)
        }
    }

    fun setOnClickBack(){
        binding.leftBtn.setOnClickListener {
            (context as? AppCompatActivity)?.onBackPressedDispatcher?.onBackPressed()
        }
    }
}