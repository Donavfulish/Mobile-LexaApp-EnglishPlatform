package com.home.lexa.ui.components

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import com.home.lexa.databinding.InputDeckBinding




class DeckInput @JvmOverloads constructor(
    context: Context,
    themeResId: Int = 0
) : Dialog(context, themeResId) {

    private lateinit var binding: InputDeckBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding = InputDeckBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
    override fun onStart() {
        super.onStart()
        // Ép chiều rộng window full màn hình, chiều cao bọc nội dung
        window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
    /**
     * Hàm hiển thị Popup nhập liệu
     * @param onConfirm trả về chuỗi String là tên bộ từ vựng người dùng nhập
     */
    fun showDialog(
        title: String,
        confirmText: String = "Xác nhận",
        onConfirm: (String) -> Unit, // Thay đổi quan trọng: truyền String ra ngoài
        onCancel: () -> Unit = {}
    ) {
        show()

        binding.tvTitle.text = title
        binding.btnConfirm.text = confirmText

        // Xóa text cũ nếu mở lại popup
        binding.edtVocabName.text?.clear()

        binding.btnCancel.setOnClickListener {
            onCancel.invoke()
            dismiss()
        }

        binding.btnConfirm.setOnClickListener {
            val inputText = binding.edtVocabName.text.toString().trim()

            // Validate: Bắt buộc phải nhập gì đó mới cho tạo
            if (inputText.isNotEmpty()) {
                onConfirm.invoke(inputText)
                dismiss()
            } else {
                Toast.makeText(context, "Vui lòng nhập tên bộ từ vựng", Toast.LENGTH_SHORT).show()

            }
        }
    }
}