package com.yb.part6_chapter01.screen.review

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.yb.part6_chapter01.R
import com.yb.part6_chapter01.data.entity.RestaurantReviewEntity
import com.yb.part6_chapter01.data.repository.restaurant.review.DefaultRestaurantReviewRepository
import com.yb.part6_chapter01.data.repository.restaurant.review.RestaurantReviewRepository
import com.yb.part6_chapter01.model.restaurant.review.ReviewPhotoModel
import com.yb.part6_chapter01.screen.base.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AddRestaurantReviewViewModel(
    private val imageUriList: List<Uri>,
    private val restaurantReviewRepository: RestaurantReviewRepository,
) : BaseViewModel() {

    private var uriList = imageUriList.toMutableList()
    private var uploadPhotoList: List<String>? = null

    val uploadReviewStateLiveData =
        MutableLiveData<AddRestaurantReviewState>(AddRestaurantReviewState.Uninitialized)

    override fun fetchData(): Job = viewModelScope.launch {
        uploadReviewStateLiveData.value = AddRestaurantReviewState.Loading
        uploadReviewStateLiveData.value = AddRestaurantReviewState.Success(uriList.map {
            ReviewPhotoModel(
                id = hashCode().toLong(),
                uri = it
            )
        })
    }

    fun addPhoto(imageUriList: List<Uri>) {
        uriList = imageUriList.toMutableList()
        fetchData()
    }

    fun deletePhoto(model: ReviewPhotoModel) {
        val deleteModel = uriList.find {
            it == model.uri
        }
        uriList.remove(deleteModel)
        fetchData()
    }

    fun uploadReview(reviewEntity: RestaurantReviewEntity) = viewModelScope.launch {
        uploadReviewStateLiveData.value = AddRestaurantReviewState.Loading
        if (reviewEntity.imageUrlList?.isNotEmpty() == true) {
            when (val uploadUriResult = restaurantReviewRepository.uploadPhoto(uriList, reviewEntity.id)) {
                is DefaultRestaurantReviewRepository.Result.Success<*> -> {
                    uploadPhotoList = uploadUriResult.data as List<String>
                    restaurantReviewRepository.uploadReview(
                        reviewEntity.copy(
                            imageUrlList = uploadPhotoList
                        )
                    )
                    uploadReviewStateLiveData.value = AddRestaurantReviewState.Confirm
                }
                is DefaultRestaurantReviewRepository.Result.Error -> {
                    uploadReviewStateLiveData.value =
                        AddRestaurantReviewState.Error(
                            R.string.request_error,
                            uploadUriResult.e
                        )
                }
            }
        } else {
            restaurantReviewRepository.uploadReview(reviewEntity)
            uploadReviewStateLiveData.value = AddRestaurantReviewState.Confirm
        }
    }
}