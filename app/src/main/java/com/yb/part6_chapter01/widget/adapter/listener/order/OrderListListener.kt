package com.yb.part6_chapter01.widget.adapter.listener.order

import com.yb.part6_chapter01.widget.adapter.listener.AdapterListener

interface OrderListListener : AdapterListener {

    fun writeRestaurantReview(restaurantTitle: String, orderId: String)

}