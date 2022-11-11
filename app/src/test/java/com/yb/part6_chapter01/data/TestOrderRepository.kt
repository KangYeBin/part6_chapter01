package com.yb.part6_chapter01.data

import com.yb.part6_chapter01.data.entity.OrderEntity
import com.yb.part6_chapter01.data.entity.RestaurantFoodEntity
import com.yb.part6_chapter01.data.repository.order.DefaultOrderRepository
import com.yb.part6_chapter01.data.repository.order.OrderRepository

class TestOrderRepository : OrderRepository {

    private var orderEntities = mutableListOf<OrderEntity>()

    override suspend fun orderMenu(
        restaurantId: Long,
        userId: String,
        foodMenuList: List<RestaurantFoodEntity>,
        restaurantTitle: String,
    ): DefaultOrderRepository.Result {
        orderEntities.add(
            OrderEntity(
                id = orderEntities.size.toString(),
                userId,
                restaurantId,
                foodMenuList.map { it.copy() },
                restaurantTitle
            )
        )
        return DefaultOrderRepository.Result.Success<Any>()
    }

    override suspend fun getAllOrderMenus(userId: String): DefaultOrderRepository.Result {
        return DefaultOrderRepository.Result.Success(orderEntities)
    }
}