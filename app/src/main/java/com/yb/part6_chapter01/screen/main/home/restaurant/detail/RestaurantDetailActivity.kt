package com.yb.part6_chapter01.screen.main.home.restaurant.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import com.yb.part6_chapter01.data.entity.RestaurantEntity
import com.yb.part6_chapter01.databinding.ActivityRestaurantDetailBinding
import com.yb.part6_chapter01.screen.base.BaseActivity
import com.yb.part6_chapter01.screen.main.home.restaurant.RestaurantListFragment
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class RestaurantDetailActivity :
    BaseActivity<RestaurantDetailViewModel, ActivityRestaurantDetailBinding>() {

    companion object {
        fun newIntent(context: Context, restaurantEntity: RestaurantEntity) =
            Intent(context, RestaurantDetailActivity::class.java).apply {
                putExtra(RestaurantListFragment.RESTAURANT_KEY, restaurantEntity)
            }
    }

    override val viewModel by viewModel<RestaurantDetailViewModel>() {
        parametersOf(
            intent.getParcelableExtra<RestaurantEntity>(RestaurantListFragment.RESTAURANT_KEY)
        )
    }

    override fun getViewBinding(): ActivityRestaurantDetailBinding =
        ActivityRestaurantDetailBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }



    override fun observeData() = viewModel.restaurantDetailStateLiveData.observe(this) { state ->
        when (state) {
            is RestaurantDetailState.Uninitialized -> {

            }
            is RestaurantDetailState.Loading -> {

            }
            is RestaurantDetailState.Success -> {

            }
        }
    }
}