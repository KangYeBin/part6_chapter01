package com.yb.part6_chapter01.di

import com.yb.part6_chapter01.data.repository.DefaultRestaurantRepository
import com.yb.part6_chapter01.data.repository.RestaurantRepository
import com.yb.part6_chapter01.screen.base.BaseViewModel
import com.yb.part6_chapter01.screen.main.home.HomeViewModel
import com.yb.part6_chapter01.screen.main.home.restaurant.RestaurantCategory
import com.yb.part6_chapter01.screen.main.home.restaurant.RestaurantListViewModel
import com.yb.part6_chapter01.screen.main.my.MyViewModel
import com.yb.part6_chapter01.util.provider.DefaultResourcesProvider
import com.yb.part6_chapter01.util.provider.ResourcesProvider
import kotlinx.coroutines.Dispatchers
import org.koin.android.experimental.dsl.viewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    viewModel { HomeViewModel() }
    viewModel { MyViewModel() }
    viewModel { (restaurantCategory: RestaurantCategory) -> RestaurantListViewModel(restaurantCategory, get()) }

    single<RestaurantRepository> { DefaultRestaurantRepository(get(), get()) }

    single { provideGsonConvertFactory() }
    single { buildOkHttpClient() }

    single { provideRetrofit(get(), get()) }

    single<ResourcesProvider> { DefaultResourcesProvider(androidApplication()) }

    single { Dispatchers.IO }
    single { Dispatchers.Main }
}