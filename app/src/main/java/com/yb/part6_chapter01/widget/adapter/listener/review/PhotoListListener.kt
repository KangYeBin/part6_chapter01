package com.yb.part6_chapter01.widget.adapter.listener.review

import com.yb.part6_chapter01.model.restaurant.review.ReviewPhotoModel
import com.yb.part6_chapter01.widget.adapter.listener.AdapterListener

interface PhotoListListener : AdapterListener {

    fun onItemClickDelete(model: ReviewPhotoModel)

}