package com.yb.part6_chapter01.model.preview

import android.net.Uri
import com.yb.part6_chapter01.model.CellType
import com.yb.part6_chapter01.model.Model

data class PreviewImageModel(
    override val id: Long,
    override val type: CellType = CellType.PREVIEW_IMAGE_CELL,
    val uri: Uri
) : Model(id, type)
