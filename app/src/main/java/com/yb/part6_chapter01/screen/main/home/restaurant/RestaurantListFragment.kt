package com.yb.part6_chapter01.screen.main.home.restaurant

import android.widget.Toast
import androidx.core.os.bundleOf
import com.yb.part6_chapter01.data.entity.LocationLatLngEntity
import com.yb.part6_chapter01.databinding.FragmentRestaurantListBinding
import com.yb.part6_chapter01.model.restaurant.RestaurantModel
import com.yb.part6_chapter01.screen.base.BaseFragment
import com.yb.part6_chapter01.util.provider.ResourcesProvider
import com.yb.part6_chapter01.widget.adapter.ModelRecyclerAdapter
import com.yb.part6_chapter01.widget.adapter.listener.restaurant.RestaurantListListener
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class RestaurantListFragment :
    BaseFragment<RestaurantListViewModel, FragmentRestaurantListBinding>() {

    private val restaurantCategory by lazy {
        arguments?.getSerializable(RESTAURANT_CATEGORY_KEY) as RestaurantCategory
    }
    private val locationLatLng by lazy {
        arguments?.getParcelable<LocationLatLngEntity>(LOCATION_KEY) as LocationLatLngEntity
    }

    override val viewModel by viewModel<RestaurantListViewModel>() { parametersOf(restaurantCategory, locationLatLng) }

    override fun getViewBinding(): FragmentRestaurantListBinding =
        FragmentRestaurantListBinding.inflate(layoutInflater)

    private val resourcesProvider by inject<ResourcesProvider>()

    private val adapter by lazy {
        ModelRecyclerAdapter<RestaurantModel, RestaurantListViewModel>(
            listOf(),
            viewModel,
            resourcesProvider,
            adapterListener = object : RestaurantListListener {
                override fun onItemClick(model: RestaurantModel) {
                    Toast.makeText(requireContext(), "$model", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    override fun initViews() = with(binding) {
        recyclerView.adapter = adapter
    }

    override fun observeData() = viewModel.restaurantListLiveData.observe(viewLifecycleOwner) {
        adapter.submitList(it)
    }

    companion object {
        const val RESTAURANT_CATEGORY_KEY = "restaurantCategory"
        const val LOCATION_KEY = "location"

        fun newInstance(
            restaurantCategory: RestaurantCategory,
            locationLatLng: LocationLatLngEntity,
        ): RestaurantListFragment {
            return RestaurantListFragment().apply {
                arguments = bundleOf(
                    RESTAURANT_CATEGORY_KEY to restaurantCategory,
                    LOCATION_KEY to locationLatLng
                )
            }
        }
    }
}