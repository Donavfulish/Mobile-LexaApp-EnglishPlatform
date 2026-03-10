package com.home.lexa.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.FrameLayout
import androidx.core.widget.doOnTextChanged
import com.home.lexa.R
import com.home.lexa.databinding.SearchbarFilterBinding
import com.home.lexa.databinding.ViewLexaButtonBinding

// Kế thừa FrameLayout để bọc component XML lại
class SearchbarFilter @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = SearchbarFilterBinding.inflate(LayoutInflater.from(context), this, true)

    // 1. Khai báo Callback cho nút Filter (vì mỗi trang sẽ có giao diện/chức năng bộ lọc khác nhau)
    private var onFilterClickListener: (() -> Unit)? = null

    init {
        // Lắng nghe sự kiện click vào nút Filter
        binding.btnFilter.setOnClickListener {
            onFilterClickListener?.invoke()
        }
    }

    // 2. Định nghĩa hàm thiết lập Callback cho nút Filter
    fun setOnFilterClickListener(action: () -> Unit) {
        this.onFilterClickListener = action
    }

    // Ẩn nút Filter (cần thiết cho những trang chỉ được search)
    fun hideFilter() {
        binding.btnFilter.visibility = View.GONE
    }

    // Cài đặt nội dung hint
    fun setPlaceHolderText(text: String?) {
        if (text.isNullOrEmpty()) return

        binding.etSearch.hint = text
    }

    fun getText(): String = binding.etSearch.text.toString()

    // Lắng nghe được sự thay đổi của text
    fun onTextChanged(action: (String) -> Unit) {
        binding.etSearch.doOnTextChanged { text, start, before, count ->
            action(text.toString())
        }
    }

    // Set bộ từ khóa gợi ý tìm kiếm
    fun setSuggestions(suggestions: List<String>) {
        val adapter = ArrayAdapter(
            context,
            R.layout.item_suggestion, // File layout bạn vừa tạo
            R.id.tvSuggestion,        // ID của TextView trong layout đó
            suggestions
        )
        binding.etSearch.setAdapter(adapter)

        // Ép Dropdown hiện ra ngay lập tức với list mới
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