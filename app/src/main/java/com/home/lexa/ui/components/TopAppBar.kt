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
    private var customViewId: Int = View.NO_ID
    fun setText(text: String){
        binding.title.text = text
    }
    fun setBottomBorderVisible(isVisible: Boolean) {
        binding.bottomBorder.visibility = if (isVisible) View.VISIBLE else View.GONE
    }
    fun setBackButtonVisible(isVisible: Boolean){
        binding.leftBtn.visibility = if (isVisible) android.view.View.VISIBLE else android.view.View.GONE
    }

    fun insertCustomeView(view: View) {
        if (customViewId != View.NO_ID) {
            val oldView = binding.topBarLayout.findViewById<View>(customViewId)
            if (oldView != null) {
                binding.topBarLayout.removeView(oldView)
            }
        }

        if (view.id == View.NO_ID) {
            view.id = View.generateViewId()
        }
        customViewId = view.id

        binding.topBarLayout.addView(view)

        val constraintSet = ConstraintSet()
        constraintSet.clone(binding.topBarLayout)


        constraintSet.constrainWidth(view.id, 0)
        constraintSet.constrainHeight(view.id, ConstraintSet.WRAP_CONTENT)


        constraintSet.connect(view.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 20)
        constraintSet.connect(view.id, ConstraintSet.END, binding.rightBtn.id, ConstraintSet.START, 16)

        constraintSet.connect(view.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
        constraintSet.connect(view.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)

        constraintSet.applyTo(binding.topBarLayout)

        binding.title.visibility = View.GONE
    }

    fun insertCustomeViewRight(view: View) {
        if (customViewId != View.NO_ID) {
            val oldView = binding.topBarLayout.findViewById<View>(customViewId)
            if (oldView != null) {
                binding.topBarLayout.removeView(oldView)
            }
        }

        if (view.id == View.NO_ID) {
            view.id = View.generateViewId()
        }
        customViewId = view.id

        binding.topBarLayout.addView(view)
        val constraintSet = ConstraintSet()
        constraintSet.clone(binding.topBarLayout)

        constraintSet.constrainWidth(view.id, ConstraintSet.WRAP_CONTENT)
        constraintSet.constrainHeight(view.id, ConstraintSet.WRAP_CONTENT)

        constraintSet.connect(
            view.id, ConstraintSet.END,
            if (binding.rightBtn.visibility == View.VISIBLE) binding.rightBtn.id else ConstraintSet.PARENT_ID,
            if (binding.rightBtn.visibility == View.VISIBLE) ConstraintSet.START else ConstraintSet.END,
            40
        )

        constraintSet.connect(view.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
        constraintSet.connect(view.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)

        constraintSet.applyTo(binding.topBarLayout)

        binding.title.visibility = View.VISIBLE
    }

    // Hàm MỚI: Gọi hàm này khi chuyển sang trang khác (như Thư viện) để xóa sạch cái Logo đi
    fun removeCustomView() {
        if (customViewId != View.NO_ID) {
            val oldView = binding.topBarLayout.findViewById<View>(customViewId)
            if (oldView != null) {
                binding.topBarLayout.removeView(oldView)
            }
            customViewId = View.NO_ID
            binding.title.visibility = View.VISIBLE // Trả lại chữ cho Title
        }
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