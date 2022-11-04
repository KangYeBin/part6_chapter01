package com.yb.part6_chapter01.screen.review.camera.preview

import android.net.Uri
import com.yb.part6_chapter01.model.preview.PreviewImageModel


sealed class PreviewImageListState {
    object Uninitialized : PreviewImageListState()

    object Loading : PreviewImageListState()

    data class Success(
        val previewImageList: List<PreviewImageModel>
    ) : PreviewImageListState()

    data class Confirm(
        val previewImageList: List<PreviewImageModel>
    ) : PreviewImageListState()

}