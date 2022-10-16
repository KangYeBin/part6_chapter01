package com.yb.part6_chapter01.widget.adapter.listener.order

import com.yb.part6_chapter01.model.restaurant.food.FoodModel
import com.yb.part6_chapter01.widget.adapter.listener.AdapterListener

interface OrderMenuListListener : AdapterListener {

    fun onRemoveItem(foodModel: FoodModel)

}