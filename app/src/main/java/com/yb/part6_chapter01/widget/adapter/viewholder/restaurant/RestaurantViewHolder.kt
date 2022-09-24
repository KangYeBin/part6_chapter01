package com.yb.part6_chapter01.widget.adapter.viewholder.restaurant

import com.yb.part6_chapter01.R
import com.yb.part6_chapter01.databinding.ViewholderRestaurantBinding
import com.yb.part6_chapter01.extensions.clear
import com.yb.part6_chapter01.extensions.load
import com.yb.part6_chapter01.model.Model
import com.yb.part6_chapter01.model.restaurant.RestaurantModel
import com.yb.part6_chapter01.screen.base.BaseViewModel
import com.yb.part6_chapter01.util.provider.ResourcesProvider
import com.yb.part6_chapter01.widget.adapter.listener.AdapterListener
import com.yb.part6_chapter01.widget.adapter.listener.restaurant.RestaurantListListener
import com.yb.part6_chapter01.widget.adapter.viewholder.ModelViewHolder

class RestaurantViewHolder(
    private val binding: ViewholderRestaurantBinding,
    viewModel: BaseViewModel,
    resourcesProvider: ResourcesProvider,
) : ModelViewHolder<RestaurantModel>(binding, viewModel, resourcesProvider) {

    override fun reset() = with(binding) {
        restaurantImage.clear()
    }

    override fun bindViews(model: RestaurantModel, adapterListener: AdapterListener) =
        with(binding) {
            if (adapterListener is RestaurantListListener) {
                root.setOnClickListener {
                    adapterListener.onItemClick(model)
                }
            }
        }

    override fun bindData(model: RestaurantModel) = with(binding){
        super.bindData(model)

        restaurantImage.load(model.restaurantImageUrl, 24f)
        restaurantTitleTextView.text = model.restaurantTitle
        gradeTextView.text = resourcesProvider.getString(R.string.grade_format, model.grade)
        reviewCountTextView.text = resourcesProvider.getString(R.string.review_count, model.reviewCount)
        val (minTime, maxTime) = model.deliveryTimeRange
        deliveryTimeTextView.text = resourcesProvider.getString(R.string.delivery_time, minTime, maxTime)
        val (minTip,maxTip) = model.deliveryTipRange
        deliveryTipTextView.text = resourcesProvider.getString(R.string.delivery_tip, minTip, maxTip)

    }
}