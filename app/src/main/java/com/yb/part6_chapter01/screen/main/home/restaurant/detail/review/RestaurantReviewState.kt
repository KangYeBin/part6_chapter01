package com.yb.part6_chapter01.screen.main.home.restaurant.detail.review

import com.yb.part6_chapter01.data.entity.RestaurantReviewEntity

sealed class RestaurantReviewState {

    object Uninitialized : RestaurantReviewState()

    object Loading: RestaurantReviewState()

    data class Success(
        val reviewList: List<RestaurantReviewEntity>
    ): RestaurantReviewState()

}
