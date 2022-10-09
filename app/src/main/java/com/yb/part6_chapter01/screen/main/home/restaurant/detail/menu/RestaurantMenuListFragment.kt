package com.yb.part6_chapter01.screen.main.home.restaurant.detail.menu

import android.widget.Toast
import androidx.core.os.bundleOf
import com.yb.part6_chapter01.data.entity.RestaurantFoodEntity
import com.yb.part6_chapter01.databinding.FragmentListBinding
import com.yb.part6_chapter01.model.restaurant.food.FoodModel
import com.yb.part6_chapter01.screen.base.BaseFragment
import com.yb.part6_chapter01.screen.main.home.restaurant.detail.RestaurantDetailViewModel
import com.yb.part6_chapter01.util.provider.ResourcesProvider
import com.yb.part6_chapter01.widget.adapter.ModelRecyclerAdapter
import com.yb.part6_chapter01.widget.adapter.listener.AdapterListener
import com.yb.part6_chapter01.widget.adapter.listener.restaurant.FoodMenuListListener
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class RestaurantMenuListFragment :
    BaseFragment<RestaurantMenuListViewModel, FragmentListBinding>() {

    override fun getViewBinding(): FragmentListBinding = FragmentListBinding.inflate(layoutInflater)

    private val resourcesProvider by inject<ResourcesProvider>()

    private val restaurantId by lazy {
        arguments?.getLong(RESTAURANT_ID_KEY, -1)
    }
    private val restaurantFoodList by lazy {
        arguments?.getParcelableArrayList<RestaurantFoodEntity>(FOOD_LIST_KEY)
    }

    override val viewModel by viewModel<RestaurantMenuListViewModel>() {
        parametersOf(restaurantId, restaurantFoodList)
    }

    private val restaurantDetailViewModel by sharedViewModel<RestaurantDetailViewModel>()

    private val adapter by lazy {
        ModelRecyclerAdapter<FoodModel, RestaurantMenuListViewModel>(
            listOf(),
            viewModel,
            resourcesProvider,
            adapterListener = object : FoodMenuListListener {
                override fun onItemClick(model: FoodModel) {
                    viewModel.insertMenuInBasket(model)
                }
            }
        )
    }

    override fun initViews() = with(binding) {
        recyclerView.adapter = adapter
    }

    override fun observeData() {
        viewModel.restaurantFoodListLiveData.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
        viewModel.menuBasketLiveData.observe(viewLifecycleOwner) { restaurantFoodEntity ->
            Toast.makeText(requireContext(),
                "장바구니에 ${restaurantFoodEntity.title}이(가) 담겼습니다",
                Toast.LENGTH_SHORT).show()

            restaurantDetailViewModel.notifyFoodMenuListInBasket(restaurantFoodEntity)
        }
        viewModel.isClearNeedInBasketLiveData.observe(viewLifecycleOwner) { (isClearNeed, afterAction) ->
            if (isClearNeed) {
                restaurantDetailViewModel.notifyClearNeedAlertInBasket(isClearNeed, afterAction)
            }

        }
    }

    companion object {
        val RESTAURANT_ID_KEY = "restaurantId"

        val FOOD_LIST_KEY = "foodList"

        fun newInstance(
            restaurantId: Long,
            restaurantFoodList: ArrayList<RestaurantFoodEntity>,
        ): RestaurantMenuListFragment {
            val bundle = bundleOf(
                RESTAURANT_ID_KEY to restaurantId,
                FOOD_LIST_KEY to restaurantFoodList
            )
            return RestaurantMenuListFragment().apply {
                arguments = bundle
            }
        }
    }
}