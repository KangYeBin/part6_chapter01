package com.yb.part6_chapter01.data.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LocationLatLngEntity(
    val latitude: Double,
    val longitude: Double,
    override val id: Long = -1,
) : Entity, Parcelable
