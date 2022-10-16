package com.yb.part6_chapter01.screen.order

import androidx.annotation.StringRes
import com.yb.part6_chapter01.model.restaurant.food.FoodModel

sealed class OrderMenuListState {

    object UnInitialized : OrderMenuListState()

    object Loading : OrderMenuListState()

    data class Success(
        val restaurantFoodModelList: List<FoodModel>? = null
    ) : OrderMenuListState()

    object Order : OrderMenuListState()

    data class Error(
        @StringRes val messageId: Int,
        val e: Throwable,
    ) : OrderMenuListState()
}
