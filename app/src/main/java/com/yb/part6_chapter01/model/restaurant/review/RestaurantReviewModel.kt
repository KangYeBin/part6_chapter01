package com.yb.part6_chapter01.model.restaurant.review

import android.net.Uri
import com.yb.part6_chapter01.model.CellType
import com.yb.part6_chapter01.model.Model

data class RestaurantReviewModel(
    override val id: Long,
    override val type: CellType = CellType.REVIEW_CELL,
    val title: String,
    val description: String,
    val grade: Int,
    val thumbnailImageUri: Uri? = null,
) : Model(id, type)
