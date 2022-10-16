package com.yb.part6_chapter01.screen.order

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.yb.part6_chapter01.databinding.ActivityOrderMenuListBinding
import com.yb.part6_chapter01.extensions.toGone
import com.yb.part6_chapter01.extensions.toVisible
import com.yb.part6_chapter01.model.restaurant.food.FoodModel
import com.yb.part6_chapter01.screen.base.BaseActivity
import com.yb.part6_chapter01.util.provider.ResourcesProvider
import com.yb.part6_chapter01.widget.adapter.ModelRecyclerAdapter
import com.yb.part6_chapter01.widget.adapter.listener.order.OrderMenuListListener
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class OrderMenuListActivity : BaseActivity<OrderMenuListViewModel, ActivityOrderMenuListBinding>() {

    companion object {
        fun newIntent(context: Context) = Intent(context, OrderMenuListActivity::class.java)
    }

    override val viewModel by viewModel<OrderMenuListViewModel>()

    override fun getViewBinding(): ActivityOrderMenuListBinding =
        ActivityOrderMenuListBinding.inflate(layoutInflater)

    private val resourcesProvider by inject<ResourcesProvider>()
    val adapter by lazy {
        ModelRecyclerAdapter<FoodModel, OrderMenuListViewModel>(
            listOf(),
            viewModel,
            resourcesProvider,
            adapterListener = object : OrderMenuListListener {
                override fun onRemoveItem(foodModel: FoodModel) {
                    viewModel.removeOrderModel(foodModel)
                }
            }
        )
    }

    override fun initViews() = with(binding) {
        recyclerView.adapter = adapter
        toolbar.setNavigationOnClickListener {
            finish()
        }
        confirmButton.setOnClickListener {
            viewModel.orderMenu()
        }
        orderClearButton.setOnClickListener {
            viewModel.clearOrderMenu()
        }

    }

    override fun observeData() = viewModel.orderMenuListStateLiveData.observe(this) { state ->
        when (state) {
            is OrderMenuListState.Loading -> {
                handleLoading()
            }
            is OrderMenuListState.Success -> {
                handleSuccess(state)
            }
            is OrderMenuListState.Order -> {}
            is OrderMenuListState.Error -> {}
            else -> Unit
        }
    }

    private fun handleLoading() = with(binding) {
        progressBar.toVisible()
    }

    private fun handleSuccess(state: OrderMenuListState.Success) = with(binding) {
        progressBar.toGone()
        adapter.submitList(state.restaurantFoodModelList)
        val menuOrderIsEmpty = state.restaurantFoodModelList.isNullOrEmpty()
        confirmButton.isEnabled = menuOrderIsEmpty.not()
        if (menuOrderIsEmpty) {
            Toast.makeText(this@OrderMenuListActivity, "주문할 메뉴가 없어 화면을 종료합니다", Toast.LENGTH_SHORT)
                .show()
            finish()
        }
    }
}