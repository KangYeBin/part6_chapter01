package com.yb.part6_chapter01.di

import android.content.Context
import androidx.room.Room
import com.yb.part6_chapter01.data.db.ApplicationDatabase

fun provideDB(context: Context): ApplicationDatabase =
    Room.databaseBuilder(context, ApplicationDatabase::class.java, ApplicationDatabase.DB_NAME)
        .build()

fun provideLocationDao(applicationDatabase: ApplicationDatabase) =
    applicationDatabase.LocationDao()

fun provideRestaurantDao(applicationDatabase: ApplicationDatabase) =
    applicationDatabase.RestaurantDao()

fun provideFoodMenuInBasketDao(applicationDatabase: ApplicationDatabase) =
    applicationDatabase.FoodMenuInBasketDao()