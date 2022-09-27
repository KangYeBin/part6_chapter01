package com.yb.part6_chapter01.data.response.search

data class SearchPoiInfo(
    val count: String,
    val page: String,
    val pois: Pois,
    val totalCount: String,
)