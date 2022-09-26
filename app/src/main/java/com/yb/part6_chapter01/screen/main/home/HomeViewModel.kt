package com.yb.part6_chapter01.screen.main.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.yb.part6_chapter01.R
import com.yb.part6_chapter01.data.entity.LocationLatLngEntity
import com.yb.part6_chapter01.data.repository.map.MapRepository
import com.yb.part6_chapter01.screen.base.BaseViewModel
import kotlinx.coroutines.launch

class HomeViewModel(
    private val mapRepository: MapRepository,
) : BaseViewModel() {

    val homeStateLiveData = MutableLiveData<HomeState>(HomeState.Uninitialized)

    fun loadReverseGeoInformation(
        locationLatLngEntity: LocationLatLngEntity,
    ) = viewModelScope.launch {
        homeStateLiveData.value = HomeState.Loading
        val addressInfo = mapRepository.getReverseGeoInformation(locationLatLngEntity)
        addressInfo?.let { addressInfo ->
            homeStateLiveData.value = HomeState.Success(
                mapSearchInfo = addressInfo.toSearchInfoEntity(locationLatLngEntity)
            )
        } ?: kotlin.run {
            homeStateLiveData.value = HomeState.Error(
                R.string.can_not_load_address_info
            )
        }
    }
}