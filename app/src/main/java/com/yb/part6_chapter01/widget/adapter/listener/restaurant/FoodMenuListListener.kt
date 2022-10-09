package com.yb.part6_chapter01.widget.adapter.listener.restaurant

import com.yb.part6_chapter01.model.restaurant.food.FoodModel
import com.yb.part6_chapter01.widget.adapter.listener.AdapterListener

interface FoodMenuListListener : AdapterListener {
    fun onItemClick(model: FoodModel)
}