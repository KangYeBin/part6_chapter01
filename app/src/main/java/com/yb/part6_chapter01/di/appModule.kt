package com.yb.part6_chapter01.di

import com.yb.part6_chapter01.data.entity.LocationLatLngEntity
import com.yb.part6_chapter01.data.entity.MapSearchInfoEntity
import com.yb.part6_chapter01.data.repository.map.DefaultMapRepository
import com.yb.part6_chapter01.data.repository.map.MapRepository
import com.yb.part6_chapter01.data.repository.restaurant.DefaultRestaurantRepository
import com.yb.part6_chapter01.data.repository.restaurant.RestaurantRepository
import com.yb.part6_chapter01.data.repository.user.DefaultUserRepository
import com.yb.part6_chapter01.data.repository.user.UserRepository
import com.yb.part6_chapter01.screen.main.home.HomeViewModel
import com.yb.part6_chapter01.screen.main.home.restaurant.RestaurantCategory
import com.yb.part6_chapter01.screen.main.home.restaurant.RestaurantListViewModel
import com.yb.part6_chapter01.screen.main.my.MyViewModel
import com.yb.part6_chapter01.screen.mylocation.MyLocationViewModel
import com.yb.part6_chapter01.util.provider.DefaultResourcesProvider
import com.yb.part6_chapter01.util.provider.ResourcesProvider
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidApplication
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    // viewModel
    viewModel { HomeViewModel(get(), get()) }
    viewModel { MyViewModel() }
    viewModel { (restaurantCategory: RestaurantCategory, locationLatLngEntity: LocationLatLngEntity) ->
        RestaurantListViewModel(restaurantCategory, locationLatLngEntity, get())
    }
    viewModel { (mapSearchInfoEntity: MapSearchInfoEntity) ->
        MyLocationViewModel(mapSearchInfoEntity, get(), get())
    }

    // Repository
    single<RestaurantRepository> { DefaultRestaurantRepository(get(), get(), get()) }
    single<MapRepository> { DefaultMapRepository(get(), get()) }
    single<UserRepository> { DefaultUserRepository(get(), get()) }

    single { provideGsonConvertFactory() }
    single { buildOkHttpClient() }

    single { provideMapRetrofit(get(), get()) }

    single { provideDB(androidApplication()) }
    single { provideLocationDao(get()) }

    // Api
    single { provideMapApiService(get()) }

    single<ResourcesProvider> { DefaultResourcesProvider(androidApplication()) }

    single { Dispatchers.IO }
    single { Dispatchers.Main }
}