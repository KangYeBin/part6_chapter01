package com.yb.part6_chapter01.data.repository.restaurant.review

import com.yb.part6_chapter01.data.entity.RestaurantReviewEntity

interface RestaurantReviewRepository {

    suspend fun getReviews(restaurantTitle: String): List<RestaurantReviewEntity>
}