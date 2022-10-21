package com.yb.part6_chapter01.screen.review

import com.yb.part6_chapter01.databinding.ActivityAddRestaurantReviewBinding
import com.yb.part6_chapter01.screen.base.BaseActivity
import org.koin.android.viewmodel.ext.android.viewModel

class AddRestaurantReviewActivity :
    BaseActivity<AddRestaurantReviewViewModel, ActivityAddRestaurantReviewBinding>() {

    override val viewModel by viewModel<AddRestaurantReviewViewModel>()

    override fun getViewBinding(): ActivityAddRestaurantReviewBinding =
        ActivityAddRestaurantReviewBinding.inflate(layoutInflater)



}