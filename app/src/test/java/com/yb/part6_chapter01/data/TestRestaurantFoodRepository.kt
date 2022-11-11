package com.yb.part6_chapter01.data

import com.yb.part6_chapter01.data.entity.RestaurantFoodEntity
import com.yb.part6_chapter01.data.repository.restaurant.food.RestaurantFoodRepository

class TestRestaurantFoodRepository: RestaurantFoodRepository {

    private val foodMenuInBasket = mutableListOf<RestaurantFoodEntity>()

    override suspend fun getFoods(
        restaurantId: Long,
        restaurantTitle: String,
    ): List<RestaurantFoodEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllFoodMenuListInBasket(): List<RestaurantFoodEntity> {
        return foodMenuInBasket
    }

    override suspend fun getFoodMenuListInBasket(restaurantId: Long): List<RestaurantFoodEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun insertFoodMenuInBasket(restaurantFoodEntity: RestaurantFoodEntity) {
        foodMenuInBasket.add(restaurantFoodEntity)
    }

    override suspend fun removeFoodMenuInBasket(foodId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun clearFoodMenuListInBasket() {
        foodMenuInBasket.clear()
    }
}