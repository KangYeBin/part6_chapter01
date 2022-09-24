package com.yb.part6_chapter01.screen.main.home.restaurant

import androidx.annotation.StringRes
import com.yb.part6_chapter01.R

enum class RestaurantCategory(
    @StringRes val categoryNameId: Int,
    @StringRes val categoryTypeId: Int,
) {

    ALL(R.string.all, R.string.all_type)

}