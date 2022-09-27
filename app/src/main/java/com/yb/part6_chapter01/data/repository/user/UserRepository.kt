package com.yb.part6_chapter01.data.repository.user

import com.yb.part6_chapter01.data.entity.LocationLatLngEntity

interface UserRepository {

    suspend fun getUserLocation(): LocationLatLngEntity?

    suspend fun insertUserLocation(locationLatLngEntity: LocationLatLngEntity)
}