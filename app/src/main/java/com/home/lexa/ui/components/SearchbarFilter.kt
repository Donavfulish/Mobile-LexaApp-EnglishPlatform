package com.home.lexa.ui.components

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import androidx.core.widget.doOnTextChanged
import com.google.android.material.chip.Chip
import com.home.lexa.R
import com.home.lexa.databinding.ViewSearchbarFilterBinding

// Kế thừa FrameLayout để bọc component XML lại
class SearchbarFilter @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    data class FilterOptions(
        val sortBy: String?,
        val order: String?
    )

    private val binding = ViewSearchbarFilterBinding.inflate(LayoutInflater.from(context), this, true)

    private var onFilterClickListener: (() -> Unit)? = null
    private var onSortChangedListener: ((FilterOptions) -> Unit)? = null

    private val activeColor = ContextCompat.getColor(context, R.color.purple_paragraph)
    private val inactiveStrokeColor = Color.parseColor("#E0E0E0")
    private val inactiveIconColor = Color.parseColor("#202124")

    init {
        setupFilterToggle()
        setupChipsStyling()
    }

    private fun setupFilterToggle() {
        binding.btnFilter.setOnClickListener {
            val isVisible = binding.filterContainer.visibility == View.VISIBLE
            if (isVisible) {
                hideFilterContainer()
            } else {
                showFilterContainer()
            }
            onFilterClickListener?.invoke()
        }
    }

    private fun showFilterContainer() {
        binding.filterContainer.visibility = View.VISIBLE
        updateFilterButtonStyle(true)
    }

    private fun hideFilterContainer() {
        binding.filterContainer.visibility = View.GONE
        updateFilterButtonStyle(false)
    }

    private fun updateFilterButtonStyle(isActive: Boolean) {
        val background = binding.btnFilter.background as? GradientDrawable
        background?.let {
            it.setStroke(2, if (isActive) activeColor else inactiveStrokeColor)
        }

        binding.iconFilter.setColorFilter(if (isActive) activeColor else inactiveIconColor)

        val chipGroups = listOf(binding.cgSortCriteria, binding.cgSortOrder)
        chipGroups.forEach { group ->
            group.forEach { item ->
                (item as? Chip)?.let { chip ->
                    updateChipStyle(chip, false)
                }
            }
        }
    }

    private fun setupChipsStyling() {
        val chipGroups = listOf(binding.cgSortCriteria, binding.cgSortOrder)

        chipGroups.forEach { group ->
            group.setOnCheckedStateChangeListener { _, checkedIds ->
                for (i in 0 until group.childCount) {
                    val chip = group.getChildAt(i) as? Chip
                    chip?.let {
                        val isSelected = checkedIds.contains(it.id)
                        updateChipStyle(it, isSelected)
                    }
                }
                onSortChangedListener?.invoke(getFilterOptions())
            }
        }
    }

    private fun updateChipStyle(chip: Chip, isSelected: Boolean) {
        val color = if (isSelected) activeColor else inactiveStrokeColor
        val textColor = if (isSelected) activeColor else Color.parseColor("#757575")

        chip.chipStrokeColor = ColorStateList.valueOf(color)
        chip.chipStrokeWidth = if (isSelected) 4f else 2f
        chip.setTextColor(textColor)
    }

    fun getFilterOptions(): FilterOptions {
        val sortBy = when (binding.cgSortCriteria.checkedChipId) {
            R.id.chipSortDate -> "created_at"
            R.id.chipSortTitle -> "title"
            else -> null
        }

        val order = when (binding.cgSortOrder.checkedChipId) {
            R.id.chipSortAsc -> "asc"
            R.id.chipSortDesc -> "desc"
            else -> null
        }

        return FilterOptions(sortBy, order)
    }

    fun setIconColor(@ColorInt color: Int){
        binding.iconFilter.setColorFilter(color)
    }

    fun setTextSearch(text: String){
        binding.etSearch.hint = text
    }

    fun setOnFilterClickListener(action: () -> Unit) {
        this.onFilterClickListener = action
    }

    fun setOnSortOptionChanged(listener: (FilterOptions) -> Unit) {
        this.onSortChangedListener = listener
    }

    fun hideFilter() {
        binding.btnFilter.visibility = View.GONE
    }

    fun setPlaceHolderText(text: String?) {
        if (text.isNullOrEmpty()) return

        binding.etSearch.hint = text
    }

    fun getText(): String = binding.etSearch.text.toString()

    fun onTextChanged(action: (String) -> Unit) {
        binding.etSearch.doOnTextChanged { text, start, before, count ->
            action(text.toString())
        }
    }

    fun onSearchAction(action: (String) -> Unit) {
        binding.etSearch.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                action(v.text.toString())
                false
            } else {
                false
            }
        }
    }

    fun setSuggestions(suggestions: List<String>) {
        val adapter = ArrayAdapter(
            context,
            R.layout.view_item_suggestion,
            R.id.tvSuggestion,
            suggestions
        )
        binding.etSearch.setAdapter(adapter)

        if (suggestions.isNotEmpty()) {
            binding.etSearch.showDropDown()
        }
    }

    inner class NoFilterAdapter(context: Context, layout: Int, var items: List<String>) :
        ArrayAdapter<String>(context, layout, items) {

        private val noFilter = object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val results = FilterResults()
                results.values = items
                results.count = items.size
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                notifyDataSetChanged()
            }
        }

        override fun getFilter(): Filter = noFilter
    }
}
