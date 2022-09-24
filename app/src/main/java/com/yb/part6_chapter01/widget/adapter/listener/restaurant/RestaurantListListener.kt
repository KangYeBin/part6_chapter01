package com.yb.part6_chapter01.widget.adapter.listener.restaurant

import com.yb.part6_chapter01.model.restaurant.RestaurantModel
import com.yb.part6_chapter01.widget.adapter.listener.AdapterListener

interface  RestaurantListListener: AdapterListener {

    fun onItemClick(model: RestaurantModel)
}