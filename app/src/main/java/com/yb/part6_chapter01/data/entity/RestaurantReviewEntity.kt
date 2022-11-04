package com.yb.part6_chapter01.data.entity

data class RestaurantReviewEntity(
    override val id: Long,
    val userId: String,
    val title: String,
    val createdAt: Long,
    val content: String,
    val rating: Float,
    val imageUrlList: List<String>? = null,
    val orderId: String,
    val restaurantTitle: String,
) : Entity
