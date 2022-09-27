package com.yb.part6_chapter01.data.repository.restaurant

import com.yb.part6_chapter01.data.entity.LocationLatLngEntity
import com.yb.part6_chapter01.data.entity.RestaurantEntity
import com.yb.part6_chapter01.screen.main.home.restaurant.RestaurantCategory

interface RestaurantRepository {
    suspend fun getList(
        restaurantCategory: RestaurantCategory,
        locationLatLngEntity: LocationLatLngEntity
    ): List<RestaurantEntity>
}