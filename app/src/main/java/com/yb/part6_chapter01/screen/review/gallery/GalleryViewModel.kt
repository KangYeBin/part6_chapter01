package com.yb.part6_chapter01.screen.review.gallery

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.yb.part6_chapter01.data.repository.gallery.GalleryRepository
import com.yb.part6_chapter01.model.gallery.GalleryPhotoModel
import com.yb.part6_chapter01.screen.base.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class GalleryViewModel(
    private val galleryRepository: GalleryRepository,
) : BaseViewModel() {

    private lateinit var photoList: MutableList<GalleryPhotoModel>

    val galleryStateLiveData = MutableLiveData<GalleryState>(GalleryState.UnInitialized)

    override fun fetchData(): Job = viewModelScope.launch {
        galleryStateLiveData.value = GalleryState.Loading
        photoList = galleryRepository.getAllPhotos().map {
            GalleryPhotoModel(
                id = hashCode().toLong(),
                photoId = it.id,
                uri = it.uri,
                name = it.name,
                date =  it.date,
                size =it.size,
                isSelected = it.isSelected
            )
        }.toMutableList()
        Log.d("photoList", photoList.toString())
        galleryStateLiveData.value = GalleryState.Success(photoList)
    }

    fun selectPhoto(galleryPhotoModel: GalleryPhotoModel) {
        val findGalleryPhoto = photoList.find {  photo ->
            photo.photoId == galleryPhotoModel.photoId }

        findGalleryPhoto?.let { photo ->
            photoList[photoList.indexOf(photo)] =
                photo.copy(isSelected = photo.isSelected.not())
        }
        galleryStateLiveData.value = GalleryState.Success(photoList)
    }

    fun confirmCheckedPhotos() {
        galleryStateLiveData.value = GalleryState.Loading
        galleryStateLiveData.value = GalleryState.Confirm(photoList.filter { it.isSelected })
    }
}