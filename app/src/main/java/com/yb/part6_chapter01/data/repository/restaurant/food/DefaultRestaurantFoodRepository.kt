package com.yb.part6_chapter01.data.repository.restaurant.food

import com.yb.part6_chapter01.data.db.dao.FoodMenuInBasketDao
import com.yb.part6_chapter01.data.entity.RestaurantFoodEntity
import com.yb.part6_chapter01.data.network.FoodApiService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class DefaultRestaurantFoodRepository(
    private val foodApiService: FoodApiService,
    private val foodMenuInBasketDao: FoodMenuInBasketDao,
    private val ioDispatcher: CoroutineDispatcher,
) : RestaurantFoodRepository {
    override suspend fun getFoods(restaurantId: Long, restaurantTitle: String): List<RestaurantFoodEntity> =
        withContext(ioDispatcher) {
            val response = foodApiService.getRestaurantFoods(restaurantId)
            if (response.isSuccessful) {
                response.body()?.map {
                    it.toEntity(restaurantId, restaurantTitle)
                } ?: listOf()
            } else {
                listOf()
            }
        }

    override suspend fun getAllFoodMenuListInBasket(): List<RestaurantFoodEntity> =
        withContext(ioDispatcher) {
            foodMenuInBasketDao.getAll()
        }

    override suspend fun getFoodMenuListInBasket(restaurantId: Long): List<RestaurantFoodEntity> =
        withContext(ioDispatcher) {
            foodMenuInBasketDao.getAllByRestaurantId(restaurantId)
        }

    override suspend fun insertFoodMenuInBasket(restaurantFoodEntity: RestaurantFoodEntity) =
        withContext(ioDispatcher) {
            foodMenuInBasketDao.insert(restaurantFoodEntity)
        }

    override suspend fun removeFoodMenuInBasket(foodId: String) =
        withContext(ioDispatcher) {
            foodMenuInBasketDao.delete(foodId)
        }

    override suspend fun clearFoodMenuListInBasket() =
        withContext(ioDispatcher) {
            foodMenuInBasketDao.deleteAll()
        }
}