package com.home.lexa.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.PopupMenu
import com.home.lexa.databinding.ComponentDropdownInputBinding

class DropDownInput @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): LinearLayout(context, attrs, defStyleAttr) {
    private val binding = ComponentDropdownInputBinding.inflate(LayoutInflater.from(context), this)

    var onItemSelected: ((String) -> Unit) ? = null

    init {
        orientation = VERTICAL

        // Đọc các thuộc tính từ XML nếu cần (Title, Hint...)
        val typedArray = context.obtainStyledAttributes(attrs, com.home.lexa.R.styleable.DropdownInput)
        val title = typedArray.getString(com.home.lexa.R.styleable.DropdownInput_dropdownTitle)
        binding.tvTitle.text = title
        typedArray.recycle()
    }

    fun setTile(title: String) {
        binding.tvTitle.text = title
    }

    fun setSelection(value: String) {
        binding.tvSelectedValue.text = value
    }

    fun setUpOptions(options: List<String>) {
        binding.containerDropdown.setOnClickListener{
            val popup = PopupMenu(context, binding.containerDropdown)
            options.forEach{popup.menu.add(it)}

            popup.setOnMenuItemClickListener { item->
                val selected = item.title.toString()
                binding.tvSelectedValue.text = selected
                onItemSelected?.invoke(selected)
                true
            }
            popup.show()
        }
    }
}