package com.yb.part6_chapter01.model.restaurant.order

import com.yb.part6_chapter01.data.entity.OrderEntity
import com.yb.part6_chapter01.data.entity.RestaurantFoodEntity
import com.yb.part6_chapter01.model.CellType
import com.yb.part6_chapter01.model.Model

data class OrderModel(
    override val id: Long,
    override val type: CellType = CellType.ORDER_CELL,
    val orderId: String,
    val userId: String,
    val restaurantId: Long,
    val foodMenuList: List<RestaurantFoodEntity>,
    val restaurantTitle: String
) : Model(id, type) {

    fun toEntity() {
        OrderEntity(
            id = orderId,
            userId = userId,
            restaurantId = restaurantId,
            foodMenuList = foodMenuList,
            restaurantTitle = restaurantTitle
        )
    }

}
