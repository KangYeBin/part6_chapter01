package com.yb.part6_chapter01.screen.review.gallery

import com.yb.part6_chapter01.model.gallery.GalleryPhotoModel

sealed class GalleryState {

    object UnInitialized : GalleryState()

    object Loading : GalleryState()

    data class Success(
        val photoList: List<GalleryPhotoModel>,
    ) : GalleryState()

    data class Confirm(
        val photoList: List<GalleryPhotoModel>,
    ) : GalleryState()
}
