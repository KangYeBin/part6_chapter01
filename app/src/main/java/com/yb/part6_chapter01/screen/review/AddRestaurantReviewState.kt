package com.yb.part6_chapter01.screen.review

import androidx.annotation.StringRes
import com.yb.part6_chapter01.model.restaurant.review.ReviewPhotoModel

sealed class AddRestaurantReviewState {

    object Uninitialized : AddRestaurantReviewState()

    object Loading : AddRestaurantReviewState()

    data class Success(
        val reviewPhotoList: List<ReviewPhotoModel>? = null,
    ) : AddRestaurantReviewState()

    object Confirm : AddRestaurantReviewState()

    data class Error(
        @StringRes val messageId: Int,
        val e: Throwable,
    ) : AddRestaurantReviewState()
}