package com.yb.part6_chapter01.screen.main.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.yb.part6_chapter01.R
import com.yb.part6_chapter01.data.entity.LocationLatLngEntity
import com.yb.part6_chapter01.data.entity.MapSearchInfoEntity
import com.yb.part6_chapter01.data.entity.RestaurantFoodEntity
import com.yb.part6_chapter01.data.repository.map.MapRepository
import com.yb.part6_chapter01.data.repository.restaurant.food.RestaurantFoodRepository
import com.yb.part6_chapter01.data.repository.user.UserRepository
import com.yb.part6_chapter01.screen.base.BaseViewModel
import kotlinx.coroutines.launch

class HomeViewModel(
    private val mapRepository: MapRepository,
    private val userRepository: UserRepository,
    private val restaurantFoodRepository: RestaurantFoodRepository,
) : BaseViewModel() {

    companion object {
        const val MY_LOCATION_KEY = "MyLocation"
    }

    val homeStateLiveData = MutableLiveData<HomeState>(HomeState.Uninitialized)
    val foodMenuBasketLiveData = MutableLiveData<List<RestaurantFoodEntity>>()

    fun loadReverseGeoInformation(
        locationLatLngEntity: LocationLatLngEntity,
    ) = viewModelScope.launch {
        homeStateLiveData.value = HomeState.Loading
        val userLocationLatLngEntity = userRepository.getUserLocation()
        val currentLocationLatLngEntity = userLocationLatLngEntity ?: locationLatLngEntity
        val addressInfo = mapRepository.getReverseGeoInformation(currentLocationLatLngEntity)
        addressInfo?.let { addressInfo ->
            homeStateLiveData.value = HomeState.Success(
                mapSearchInfo = addressInfo.toSearchInfoEntity(locationLatLngEntity),
                isLocationSame = currentLocationLatLngEntity == locationLatLngEntity
            )
        } ?: kotlin.run {
            homeStateLiveData.value = HomeState.Error(
                R.string.can_not_load_address_info
            )
        }
    }

    fun getMapSearchInfo(): MapSearchInfoEntity? {
        when (val data = homeStateLiveData.value) {
            is HomeState.Success -> return data.mapSearchInfo
        }
        return null
    }

    fun checkMyBasket() = viewModelScope.launch {
        foodMenuBasketLiveData.value = restaurantFoodRepository.getAllFoodMenuListInBasket()
    }
}