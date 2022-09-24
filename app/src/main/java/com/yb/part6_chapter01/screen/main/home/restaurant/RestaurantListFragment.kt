package com.yb.part6_chapter01.screen.main.home.restaurant

import androidx.core.os.bundleOf
import androidx.lifecycle.LifecycleOwner
import com.yb.part6_chapter01.databinding.FragmentRestaurantListBinding
import com.yb.part6_chapter01.screen.base.BaseFragment
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class RestaurantListFragment :
    BaseFragment<RestaurantListViewModel, FragmentRestaurantListBinding>() {

    private val restaurantCategory by lazy {
        arguments?.getSerializable(RESTAURANT_CATEGORY_KEY) as RestaurantCategory
    }

    override val viewModel by viewModel<RestaurantListViewModel>() { parametersOf(restaurantCategory) }

    override fun getViewBinding(): FragmentRestaurantListBinding =
        FragmentRestaurantListBinding.inflate(layoutInflater)

    override fun observeData() = viewModel.restaurantListLiveData.observe(viewLifecycleOwner) {

    }

    companion object {
        const val RESTAURANT_CATEGORY_KEY = "restaurantCategory"
        fun newInstance(restaurantCategory: RestaurantCategory): RestaurantListFragment {
            return RestaurantListFragment().apply {
                arguments = bundleOf(RESTAURANT_CATEGORY_KEY to restaurantCategory)
            }
        }
    }
}