package com.yb.part6_chapter01.data.entity

import android.os.Parcelable
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@androidx.room.Entity
data class RestaurantFoodEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val price: Int,
    val imageUrl: String,
    val restaurantId: Long,
    val restaurantTitle: String
) : Parcelable