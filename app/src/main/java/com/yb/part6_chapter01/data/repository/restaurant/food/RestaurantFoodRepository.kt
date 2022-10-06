package com.yb.part6_chapter01.data.repository.restaurant.food

import com.yb.part6_chapter01.data.entity.RestaurantFoodEntity

interface RestaurantFoodRepository {

    suspend fun getFoods(restaurantId: Long): List<RestaurantFoodEntity>

}