package com.yb.part6_chapter01.di

import com.yb.part6_chapter01.data.TestRestaurantRepository
import com.yb.part6_chapter01.data.entity.LocationLatLngEntity
import com.yb.part6_chapter01.data.repository.restaurant.RestaurantRepository
import com.yb.part6_chapter01.screen.main.home.restaurant.RestaurantCategory
import com.yb.part6_chapter01.screen.main.home.restaurant.RestaurantListViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

internal val appTestModule = module {

    viewModel { (restaurantCategory: RestaurantCategory, locationLatLngEntity: LocationLatLngEntity) ->
        RestaurantListViewModel(restaurantCategory, locationLatLngEntity, get())
    }

    single<RestaurantRepository> { TestRestaurantRepository() }

}