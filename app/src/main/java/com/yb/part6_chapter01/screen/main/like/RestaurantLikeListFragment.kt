package com.yb.part6_chapter01.screen.main.like

import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.yb.part6_chapter01.databinding.FragmentRestaurantLikeListBinding
import com.yb.part6_chapter01.model.restaurant.RestaurantModel
import com.yb.part6_chapter01.screen.base.BaseFragment
import com.yb.part6_chapter01.screen.main.home.restaurant.detail.RestaurantDetailActivity
import com.yb.part6_chapter01.util.provider.ResourcesProvider
import com.yb.part6_chapter01.widget.adapter.ModelRecyclerAdapter
import com.yb.part6_chapter01.widget.adapter.listener.restaurant.RestaurantLikeListListener
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class RestaurantLikeListFragment :
    BaseFragment<RestaurantLikeListViewModel, FragmentRestaurantLikeListBinding>() {

    override val viewModel by viewModel<RestaurantLikeListViewModel>()

    override fun getViewBinding(): FragmentRestaurantLikeListBinding =
        FragmentRestaurantLikeListBinding.inflate(layoutInflater)

    private val resourcesProvider by inject<ResourcesProvider>()

    private val adapter by lazy {
        ModelRecyclerAdapter<RestaurantModel, RestaurantLikeListViewModel>(
            listOf(),
            viewModel,
            resourcesProvider = resourcesProvider,
            adapterListener = object : RestaurantLikeListListener {
                override fun onDislikeItem(model: RestaurantModel) {
                    viewModel.dislikeRestaurant(model.toEntity())
                }

                override fun onItemClick(model: RestaurantModel) {
                    startActivity(
                        RestaurantDetailActivity.newIntent(requireContext(), model.toEntity())
                    )
                }
            }
        )
    }

    override fun initViews() = with(binding) {
        recyclerView.adapter = adapter
    }

    override fun observeData() = viewModel.restaurantLikeListLiveData.observe(viewLifecycleOwner) {
        checkListEmpty(it)
    }

    private fun checkListEmpty(restaurantList: List<RestaurantModel>) {
        val isEmpty = restaurantList.isEmpty()
        binding.recyclerView.isGone = isEmpty
        binding.emptyResultTextView.isVisible = isEmpty
        if (isEmpty.not()) {
            adapter.submitList(restaurantList)
        }
    }

    companion object {
        fun newInstance() = RestaurantLikeListFragment()
        const val TAG = "restaurantLikeListFragment"
    }
}