package com.yb.part6_chapter01.data.repository.restaurant.review

import android.net.Uri
import com.yb.part6_chapter01.data.entity.RestaurantReviewEntity

interface RestaurantReviewRepository {

    suspend fun uploadPhoto(imageUriList: List<Uri>, id: Long): DefaultRestaurantReviewRepository.Result

    suspend fun uploadReview(reviewEntity: RestaurantReviewEntity): DefaultRestaurantReviewRepository.Result

    suspend fun getReviews(restaurantTitle: String): DefaultRestaurantReviewRepository.Result
}