package com.yb.part6_chapter01.screen.main.home.restaurant.detail.review

import androidx.core.os.bundleOf
import com.yb.part6_chapter01.data.entity.RestaurantFoodEntity
import com.yb.part6_chapter01.databinding.FragmentListBinding
import com.yb.part6_chapter01.screen.base.BaseFragment
import org.koin.android.viewmodel.ext.android.viewModel

class RestaurantReviewListFragment :
    BaseFragment<RestaurantReviewListViewModel, FragmentListBinding>() {

    override val viewModel by viewModel<RestaurantReviewListViewModel>()

    override fun getViewBinding(): FragmentListBinding = FragmentListBinding.inflate(layoutInflater)

    override fun observeData() {

    }

    companion object {
        val RESTAURANT_ID_KEY = "restaurantId"

        fun newInstance(
            restaurantId: Long
        ): RestaurantReviewListFragment {
            val bundle = bundleOf(
                RESTAURANT_ID_KEY to restaurantId,
            )
            return RestaurantReviewListFragment().apply {
                arguments = bundle
            }
        }
    }
}