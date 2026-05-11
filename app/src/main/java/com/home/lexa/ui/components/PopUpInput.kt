package com.home.lexa.ui.components

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import com.home.lexa.databinding.InputPopUpBinding


class PopUpInput @JvmOverloads constructor(
    context: Context,
    themeResId: Int = 0
) : Dialog(context, themeResId) {

    // Khởi tạo binding ngay lập tức
    private val binding: InputPopUpBinding = InputPopUpBinding.inflate(LayoutInflater.from(context))
    private var customViewIdList: MutableList<Int> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

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

    fun insertNormalInput(view: NormalInput){
        if (view.id == View.NO_ID) {
            view.id = View.generateViewId()
        }
        
        if(!customViewIdList.contains(view.id)){
            customViewIdList.add(view.id)
            binding.inputLayout.addView(view)
        }
    }

    fun showDialog(
        dialogTitle: String,
        confirmText: String = "Xác nhận",
        onConfirm: (List<String>) -> Unit,
        onCancel: () -> Unit = {}
    ) {
        show()
        binding.tvTitle.text = dialogTitle
        binding.btnConfirm.text = confirmText
        
        // Clear text cũ
        customViewIdList.forEach { viewId ->
            val item = binding.inputLayout.findViewById<NormalInput>(viewId)
            item?.setText("")
        }

        binding.btnCancel.setOnClickListener {
            onCancel.invoke()
            dismiss()
        }

        binding.btnConfirm.setOnClickListener {
            val returnList = mutableListOf<String>()
            var isAllValid = true

            customViewIdList.forEach { viewId ->
                val item = binding.inputLayout.findViewById<NormalInput>(viewId)
                val textValue = item?.getText()?.trim() ?: ""
                
                if (textValue.isEmpty()) {
                    isAllValid = false
                }
                returnList.add(textValue)
            }

            if (isAllValid) {
                onConfirm.invoke(returnList)
                dismiss()
            } else {
                Toast.makeText(context, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
