package com.yb.part6_chapter01.data.repository.order

import com.yb.part6_chapter01.data.entity.RestaurantFoodEntity

interface OrderRepository {

    suspend fun orderMenu(
        restaurantId: Long,
        userId: String,
        foodMenuList: List<RestaurantFoodEntity>,
    ): DefaultOrderRepository.Result

    suspend fun getAllOrderMenus(
        userId: String
    ): DefaultOrderRepository.Result

}