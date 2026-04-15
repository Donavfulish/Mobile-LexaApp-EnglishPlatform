package com.home.lexa.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.home.lexa.databinding.CardPersonalDeckBinding
import com.home.lexa.domain.models.DeckDto




class PersonalDeckCard @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {


    private val binding = CardPersonalDeckBinding.inflate(LayoutInflater.from(context), this, true)

    fun setDeckCardData(
        data: DeckDto,
        onItemClick: () -> Unit,
        onOptionsClick: () -> Unit = {}
    ) {

        binding.tvTitle.text = data.title
        binding.tvWordCount.text = "${data.vocabNumber ?: 0} từ"
        binding.tvTime.text = data.createdAt
        binding.tagCategory.setTagData(
            text = data.topic?.name ?: "non-topic",
            colorHex = data.topic?.colorHex ?: "#000000",
            hasBorder = false
        )
        binding.ivOptions.setOnClickListener {
            onOptionsClick.invoke()
        }

        binding.root.setOnClickListener {
            onItemClick.invoke()
        }
    }
}