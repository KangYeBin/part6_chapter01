package com.yb.part6_chapter01.model.restaurant.review

import android.net.Uri
import com.yb.part6_chapter01.model.CellType
import com.yb.part6_chapter01.model.Model

data class ReviewPhotoModel(
    override val id: Long,
    override val type: CellType = CellType.PHOTO_CELL,
    val uri: Uri,
) : Model(id, type)
