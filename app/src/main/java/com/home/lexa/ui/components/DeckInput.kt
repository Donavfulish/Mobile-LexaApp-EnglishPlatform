package com.home.lexa.ui.components

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import com.home.lexa.R
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
        val displayMetrics = context.resources.displayMetrics
        val marginPx = (16 * displayMetrics.density).toInt()
        val width = displayMetrics.widthPixels - (2 * marginPx)

        window?.setLayout(
            width,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    fun showDialog(
        title: String,
        confirmText: String = "Xác nhận",
        onConfirm: (String) -> Unit,
        onCancel: () -> Unit = {}
    ) {
        show()

        binding.tvTitle.text = title
        binding.btnConfirm.text = confirmText
        binding.edtVocabName.text?.clear()
        
        binding.btnCancel.setOnClickListener {
            onCancel.invoke()
            dismiss()
        }
        
        binding.btnConfirm.setOnClickListener {
            val inputText = binding.edtVocabName.text.toString().trim()

            if (inputText.isNotEmpty()) {
                onConfirm.invoke(inputText)
                dismiss()
            } else {
                Toast.makeText(context,
                    context.getString(R.string.enter_name_deck), Toast.LENGTH_SHORT).show()
            }
        }
    }
}
