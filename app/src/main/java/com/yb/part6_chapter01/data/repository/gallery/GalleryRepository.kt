package com.yb.part6_chapter01.data.repository.gallery

import com.yb.part6_chapter01.data.entity.GalleryPhotoEntity

interface GalleryRepository {

    suspend fun getAllPhotos(): MutableList<GalleryPhotoEntity>

}