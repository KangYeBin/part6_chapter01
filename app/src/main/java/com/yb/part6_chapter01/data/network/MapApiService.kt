package com.yb.part6_chapter01.data.network

import com.yb.part6_chapter01.BuildConfig
import com.yb.part6_chapter01.data.response.address.AddressInfoResponse
import com.yb.part6_chapter01.data.url.TmapUrl
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface MapApiService {

    @GET(TmapUrl.GET_TMAP_REVERSE_GEO_CODE)
    suspend fun getReverseGeoCode(
        @Header("appKey") appKey: String = BuildConfig.TMAP_API_KEY,
        @Query("version") version: Int = 1,
        @Query("callback") callback: String? = null,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("coordType") coordType: String? = null,
        @Query("addressType") addressType: String? = null
    ): Response<AddressInfoResponse>
}