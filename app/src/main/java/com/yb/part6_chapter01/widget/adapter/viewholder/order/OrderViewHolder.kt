package com.yb.part6_chapter01.widget.adapter.viewholder.order

import com.yb.part6_chapter01.R
import com.yb.part6_chapter01.databinding.ViewholderOrderBinding
import com.yb.part6_chapter01.model.restaurant.order.OrderModel
import com.yb.part6_chapter01.screen.base.BaseViewModel
import com.yb.part6_chapter01.util.provider.ResourcesProvider
import com.yb.part6_chapter01.widget.adapter.listener.AdapterListener
import com.yb.part6_chapter01.widget.adapter.viewholder.ModelViewHolder

class OrderViewHolder(
    private val binding: ViewholderOrderBinding,
    viewModel: BaseViewModel,
    resourcesProvider: ResourcesProvider,
) : ModelViewHolder<OrderModel>(binding, viewModel, resourcesProvider) {

    override fun reset() = Unit

    override fun bindViews(model: OrderModel, adapterListener: AdapterListener) = Unit

    override fun bindData(model: OrderModel) = with(binding) {
        super.bindData(model)

        orderTitleTextView.text =
            resourcesProvider.getString(R.string.order_history_title, model.orderId)
        model.foodMenuList.groupBy { it.title }
            .entries.forEach { (title, menuList) ->
                val orderDataStr = orderContentTextView.text.toString() +
                        "메뉴 : $title | 가격 : ${menuList.first().price}원 X ${menuList.size}\n"
                orderContentTextView.text = orderDataStr
            }
        orderContentTextView.text = orderContentTextView.text.trim()
        orderTotalPriceTextView.text =
            resourcesProvider.getString(
                R.string.price,
                model.foodMenuList.map { it.price }.reduce { total, price -> total + price }
            )
    }

}