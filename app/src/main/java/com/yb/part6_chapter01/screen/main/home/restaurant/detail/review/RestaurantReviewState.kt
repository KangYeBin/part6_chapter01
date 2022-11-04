package com.yb.part6_chapter01.screen.main.home.restaurant.detail.review

import androidx.annotation.StringRes
import com.yb.part6_chapter01.data.entity.RestaurantReviewEntity
import com.yb.part6_chapter01.model.restaurant.review.RestaurantReviewModel

sealed class RestaurantReviewState {

    object Uninitialized : RestaurantReviewState()

    object Loading : RestaurantReviewState()

    data class Success(
        val reviewList: List<RestaurantReviewModel>,
    ) : RestaurantReviewState()

    data class Error(
        @StringRes val messageId: Int,
        val e: Throwable
    ): RestaurantReviewState()
}
