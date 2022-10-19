package com.yb.part6_chapter01.di

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.yb.part6_chapter01.data.entity.LocationLatLngEntity
import com.yb.part6_chapter01.data.entity.MapSearchInfoEntity
import com.yb.part6_chapter01.data.entity.RestaurantEntity
import com.yb.part6_chapter01.data.entity.RestaurantFoodEntity
import com.yb.part6_chapter01.data.preference.PreferenceManager
import com.yb.part6_chapter01.data.repository.map.DefaultMapRepository
import com.yb.part6_chapter01.data.repository.map.MapRepository
import com.yb.part6_chapter01.data.repository.order.DefaultOrderRepository
import com.yb.part6_chapter01.data.repository.order.OrderRepository
import com.yb.part6_chapter01.data.repository.restaurant.DefaultRestaurantRepository
import com.yb.part6_chapter01.data.repository.restaurant.RestaurantRepository
import com.yb.part6_chapter01.data.repository.restaurant.food.DefaultRestaurantFoodRepository
import com.yb.part6_chapter01.data.repository.restaurant.food.RestaurantFoodRepository
import com.yb.part6_chapter01.data.repository.restaurant.review.DefaultRestaurantReviewRepository
import com.yb.part6_chapter01.data.repository.restaurant.review.RestaurantReviewRepository
import com.yb.part6_chapter01.data.repository.user.DefaultUserRepository
import com.yb.part6_chapter01.data.repository.user.UserRepository
import com.yb.part6_chapter01.screen.main.home.HomeViewModel
import com.yb.part6_chapter01.screen.main.home.restaurant.RestaurantCategory
import com.yb.part6_chapter01.screen.main.home.restaurant.RestaurantListViewModel
import com.yb.part6_chapter01.screen.main.home.restaurant.detail.RestaurantDetailViewModel
import com.yb.part6_chapter01.screen.main.home.restaurant.detail.menu.RestaurantMenuListViewModel
import com.yb.part6_chapter01.screen.main.home.restaurant.detail.review.RestaurantReviewListViewModel
import com.yb.part6_chapter01.screen.main.like.RestaurantLikeListViewModel
import com.yb.part6_chapter01.screen.main.my.MyViewModel
import com.yb.part6_chapter01.screen.mylocation.MyLocationViewModel
import com.yb.part6_chapter01.screen.order.OrderMenuListViewModel
import com.yb.part6_chapter01.util.event.MenuChangeEventBus
import com.yb.part6_chapter01.util.provider.DefaultResourcesProvider
import com.yb.part6_chapter01.util.provider.ResourcesProvider
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidApplication
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {

    // viewModel
    viewModel { HomeViewModel(get(), get(), get()) }
    viewModel { RestaurantLikeListViewModel(get()) }
    viewModel { MyViewModel(get()) }
    viewModel { (restaurantCategory: RestaurantCategory, locationLatLngEntity: LocationLatLngEntity) ->
        RestaurantListViewModel(restaurantCategory, locationLatLngEntity, get())
    }
    viewModel { (mapSearchInfoEntity: MapSearchInfoEntity) ->
        MyLocationViewModel(mapSearchInfoEntity, get(), get())
    }
    viewModel { (restaurantEntity: RestaurantEntity) ->
        RestaurantDetailViewModel(restaurantEntity, get(), get())
    }
    viewModel { (restaurantId: Long, foodEntityList: List<RestaurantFoodEntity>) ->
        RestaurantMenuListViewModel(restaurantId, foodEntityList, get())
    }
    viewModel { (restaurantTitle: String) -> RestaurantReviewListViewModel(restaurantTitle, get()) }
    viewModel { OrderMenuListViewModel(get(), get()) }

    // Repository
    single<RestaurantRepository> { DefaultRestaurantRepository(get(), get(), get()) }
    single<MapRepository> { DefaultMapRepository(get(), get()) }
    single<UserRepository> { DefaultUserRepository(get(), get(), get()) }
    single<RestaurantFoodRepository> { DefaultRestaurantFoodRepository(get(), get(), get()) }
    single<RestaurantReviewRepository> { DefaultRestaurantReviewRepository(get()) }
    single<OrderRepository> { DefaultOrderRepository(get(), get()) }

    single { provideDB(androidApplication()) }
    single { provideLocationDao(get()) }
    single { provideRestaurantDao(get()) }
    single { provideFoodMenuInBasketDao(get()) }

    // Api
    single { provideGsonConvertFactory() }
    single { buildOkHttpClient() }

    single(named("map")) { provideMapRetrofit(get(), get()) }
    single(named("food")) { provideFoodRetrofit(get(), get()) }

    single { provideMapApiService(get(qualifier = named("map"))) }
    single { provideFoodApiService(get(qualifier = named("food"))) }

    single<ResourcesProvider> { DefaultResourcesProvider(androidApplication()) }
    single { PreferenceManager(androidApplication()) }

    single { Dispatchers.IO }
    single { Dispatchers.Main }

    single { MenuChangeEventBus() }

    single { Firebase.firestore }
}