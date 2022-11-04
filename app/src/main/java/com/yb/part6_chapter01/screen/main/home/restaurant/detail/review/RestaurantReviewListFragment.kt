package com.yb.part6_chapter01.screen.main.home.restaurant.detail.review

import android.widget.Toast
import androidx.core.os.bundleOf
import com.yb.part6_chapter01.databinding.FragmentListBinding
import com.yb.part6_chapter01.extensions.toGone
import com.yb.part6_chapter01.extensions.toVisible
import com.yb.part6_chapter01.model.restaurant.review.RestaurantReviewModel
import com.yb.part6_chapter01.screen.base.BaseFragment
import com.yb.part6_chapter01.util.provider.ResourcesProvider
import com.yb.part6_chapter01.widget.adapter.ModelRecyclerAdapter
import com.yb.part6_chapter01.widget.adapter.listener.AdapterListener
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class RestaurantReviewListFragment :
    BaseFragment<RestaurantReviewListViewModel, FragmentListBinding>() {

    override val viewModel by viewModel<RestaurantReviewListViewModel>() {
        parametersOf(
            arguments?.getString(RESTAURANT_TITLE_KEY)
        )
    }

    override fun getViewBinding(): FragmentListBinding = FragmentListBinding.inflate(layoutInflater)

    private val resourcesProvider by inject<ResourcesProvider>()

    private val adapter by lazy {
        ModelRecyclerAdapter<RestaurantReviewModel, RestaurantReviewListViewModel>(
            listOf(),
            viewModel,
            resourcesProvider,
            adapterListener = object : AdapterListener {}
        )
    }

    override fun initViews() {
        binding.recyclerView.adapter = adapter
    }

    override fun observeData() =
        viewModel.reviewStateLiveData.observe(viewLifecycleOwner) { state ->
            when (state) {
                is RestaurantReviewState.Loading -> handleLoading()
                is RestaurantReviewState.Success -> handleSuccess(state)
                else -> Unit
            }
        }

    private fun handleLoading() {
        binding.progressBar.toVisible()
    }

    private fun handleSuccess(state: RestaurantReviewState.Success) {
        binding.progressBar.toGone()
        adapter.submitList(state.reviewList)
    }

    companion object {
        const val RESTAURANT_TITLE_KEY = "restaurantTitle"

        fun newInstance(
            restaurantTitle: String,
        ): RestaurantReviewListFragment {
            val bundle = bundleOf(
                RESTAURANT_TITLE_KEY to restaurantTitle,
            )
            return RestaurantReviewListFragment().apply {
                arguments = bundle
            }
        }
    }
}