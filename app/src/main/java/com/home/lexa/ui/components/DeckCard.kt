package com.home.lexa.ui.components

import android.content.Context

import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import coil.load
import com.home.lexa.R
import com.home.lexa.core.Constants
import com.home.lexa.databinding.CardDeckBinding
import com.home.lexa.domain.models.ShortCourseDto



class DeckCard @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = CardDeckBinding.inflate(LayoutInflater.from(context), this, true)


    fun setDeckCardData(
        data: ShortCourseDto,
        onCardClick: () -> Unit,
        onOptionsClick: () -> Unit
    ) {

        binding.tvCourseTitle.text = data.title
        binding.tvCourseDesc.text = data.description
        binding.tvAuthorName.text = data.creator_name
        binding.tvUserCount.text = data.studying_user_count.toString()
        binding.tvHeartCount.text = data.favorite_user_count.toString()
        binding.tvWordCount.text = "${data.vocabNumber} từ"


        binding.tagCategory.setTagData(
            text = data.topic.name,
            colorHex = data.topic.colorHex,
            hasBorder = false
        )


        binding.ivThumbnail.load(if (data.thumbnail_url.isNullOrBlank()) Constants.DEFAULT_COURSE_IMAGE_URL else data.thumbnail_url) {
            crossfade(true)
        }

        binding.ivAuthorAvatar.load(if (data.creator_avatar_url.isNullOrBlank()) Constants.DEFAULT_AVATAR_URL else data.creator_avatar_url) {
            crossfade(true)
        }


        binding.ivOptions.setOnClickListener {
            onOptionsClick.invoke()
        }


        binding.root.setOnClickListener {
            onCardClick.invoke()
        }
    }
}