package com.yb.part6_chapter01.di

import com.yb.part6_chapter01.util.provider.DefaultResourcesProvider
import com.yb.part6_chapter01.util.provider.ResourcesProvider
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val appModule = module {

    single { provideGsonConvertFactory() }
    single { buildOkHttpClient() }

    single { provideRetrofit(get(), get())}

    single<ResourcesProvider> { DefaultResourcesProvider(androidApplication()) }

    single { Dispatchers.IO }
    single { Dispatchers.Main }
}