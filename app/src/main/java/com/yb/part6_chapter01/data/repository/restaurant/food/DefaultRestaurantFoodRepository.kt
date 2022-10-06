package com.yb.part6_chapter01.data.repository.restaurant.food

import com.yb.part6_chapter01.data.entity.RestaurantFoodEntity
import com.yb.part6_chapter01.data.network.FoodApiService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class DefaultRestaurantFoodRepository(
    private val foodApiService: FoodApiService,
    private val ioDispatcher: CoroutineDispatcher,
) : RestaurantFoodRepository {
    override suspend fun getFoods(restaurantId: Long): List<RestaurantFoodEntity> =
        withContext(ioDispatcher) {
            val response = foodApiService.getRestaurantFoods(restaurantId)
            if (response.isSuccessful) {
                response.body()?.map {
                    it.toEntity(restaurantId)
                } ?: listOf()
            } else {
                listOf()
            }
        }
}