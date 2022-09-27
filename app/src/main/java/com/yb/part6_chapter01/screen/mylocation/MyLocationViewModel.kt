package com.yb.part6_chapter01.screen.mylocation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.yb.part6_chapter01.R
import com.yb.part6_chapter01.data.entity.LocationLatLngEntity
import com.yb.part6_chapter01.data.entity.MapSearchInfoEntity
import com.yb.part6_chapter01.data.repository.map.MapRepository
import com.yb.part6_chapter01.screen.base.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MyLocationViewModel(
    private val mapSearchInfoEntity: MapSearchInfoEntity,
    private val mapRepository: MapRepository,
) : BaseViewModel() {

    val myLocationStateLiveData = MutableLiveData<MyLocationState>(MyLocationState.Uninitialized)

    override fun fetchData(): Job = viewModelScope.launch {
        myLocationStateLiveData.value = MyLocationState.Loading
        myLocationStateLiveData.value = MyLocationState.Success(mapSearchInfoEntity)
    }

    fun changeLocationInfo(
        locationLatLngEntity: LocationLatLngEntity,
    ) = viewModelScope.launch {
        myLocationStateLiveData.value = MyLocationState.Loading
        val addressInfo = mapRepository.getReverseGeoInformation(locationLatLngEntity)
        addressInfo?.let { addressInfo ->
            myLocationStateLiveData.value = MyLocationState.Success(
                addressInfo.toSearchInfoEntity(locationLatLngEntity)
            )
        } ?: kotlin.run {
            myLocationStateLiveData.value = MyLocationState.Error(
                R.string.can_not_load_address_info
            )
        }
    }

    fun confirmSelectLocation() = viewModelScope.launch {
        when (val value = myLocationStateLiveData.value) {
            is MyLocationState.Success -> {

            }
        }
    }
}