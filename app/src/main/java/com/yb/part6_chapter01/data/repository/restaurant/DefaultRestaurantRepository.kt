package com.yb.part6_chapter01.data.repository.restaurant

import com.yb.part6_chapter01.data.entity.RestaurantEntity
import com.yb.part6_chapter01.screen.main.home.restaurant.RestaurantCategory
import com.yb.part6_chapter01.util.provider.ResourcesProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class DefaultRestaurantRepository(
    private val resourcesProvider: ResourcesProvider,
    private val ioDispatcher: CoroutineDispatcher,
) : RestaurantRepository {
    override suspend fun getList(
        restaurantCategory: RestaurantCategory,
    ): List<RestaurantEntity> = withContext(ioDispatcher) {
        // TODO API를 통해 리스트 가져오기

        listOf(
            RestaurantEntity(
                id = 0,
                restaurantInfoId = 0,
                restaurantCategory = RestaurantCategory.ALL,
                restaurantTitle = "마포화로집",
                restaurantImageUrl = "https://picsum.photos/200",
                grade = (1 until 5).random() + ((0..10).random() / 10f),
                reviewCount = (0 until 200).random(),
                deliveryTimeRange = Pair(0, 20),
                deliveryTipRange = Pair(0, 2000)
            ),
            RestaurantEntity(
                id = 1,
                restaurantInfoId = 1,
                restaurantCategory = RestaurantCategory.ALL,
                restaurantTitle = "옛날 우동 & 덮밥",
                restaurantImageUrl = "https://picsum.photos/200",
                grade = (1 until 5).random() + ((0..10).random() / 10f),
                reviewCount = (0 until 200).random(),
                deliveryTimeRange = Pair(0, 20),
                deliveryTipRange = Pair(0, 2000)
            )

        )
    }
}