package com.yb.part6_chapter01.widget.adapter.listener.gallery

import com.yb.part6_chapter01.model.gallery.GalleryPhotoModel
import com.yb.part6_chapter01.widget.adapter.listener.AdapterListener

interface GalleryPhotoListListener : AdapterListener {

    fun onItemClick(model: GalleryPhotoModel)

}