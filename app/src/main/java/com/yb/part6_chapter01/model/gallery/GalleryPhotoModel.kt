package com.yb.part6_chapter01.model.gallery

import android.net.Uri
import com.yb.part6_chapter01.model.CellType
import com.yb.part6_chapter01.model.Model

data class GalleryPhotoModel(
    override val id: Long,
    override val type: CellType = CellType.GALLERY_PHOTO_CELL,
    val photoId: Long,
    val uri: Uri,
    val name: String,
    val date: String,
    val size: Int,
    var isSelected: Boolean = false,
) : Model(id, type)
