package com.yb.part6_chapter01.di

import com.google.firebase.auth.FirebaseAuth
import com.yb.part6_chapter01.data.TestOrderRepository
import com.yb.part6_chapter01.data.TestRestaurantFoodRepository
import com.yb.part6_chapter01.data.TestRestaurantRepository
import com.yb.part6_chapter01.data.entity.LocationLatLngEntity
import com.yb.part6_chapter01.data.repository.order.OrderRepository
import com.yb.part6_chapter01.data.repository.restaurant.RestaurantRepository
import com.yb.part6_chapter01.data.repository.restaurant.food.RestaurantFoodRepository
import com.yb.part6_chapter01.screen.main.home.restaurant.RestaurantCategory
import com.yb.part6_chapter01.screen.main.home.restaurant.RestaurantListViewModel
import com.yb.part6_chapter01.screen.order.OrderMenuListViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

internal val appTestModule = module {

    viewModel { (restaurantCategory: RestaurantCategory, locationLatLngEntity: LocationLatLngEntity) ->
        RestaurantListViewModel(restaurantCategory, locationLatLngEntity, get())
    }
    viewModel { (firebaseAuth: FirebaseAuth) ->
        OrderMenuListViewModel(firebaseAuth, get(), get())
    }

    single<RestaurantRepository> { TestRestaurantRepository() }
    single<RestaurantFoodRepository> { TestRestaurantFoodRepository() }
    single<OrderRepository> { TestOrderRepository() }
}