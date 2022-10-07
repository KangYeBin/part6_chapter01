package com.yb.part6_chapter01.widget.adapter.viewholder.food

import android.util.Log
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.yb.part6_chapter01.R
import com.yb.part6_chapter01.databinding.ViewholderFoodMenuBinding
import com.yb.part6_chapter01.extensions.clear
import com.yb.part6_chapter01.extensions.load
import com.yb.part6_chapter01.model.restaurant.food.FoodModel
import com.yb.part6_chapter01.screen.base.BaseViewModel
import com.yb.part6_chapter01.util.provider.ResourcesProvider
import com.yb.part6_chapter01.widget.adapter.listener.AdapterListener
import com.yb.part6_chapter01.widget.adapter.viewholder.ModelViewHolder

class FoodMenuViewHolder(
    private val binding: ViewholderFoodMenuBinding,
    viewModel: BaseViewModel,
    resourcesProvider: ResourcesProvider,
) : ModelViewHolder<FoodModel>(binding, viewModel, resourcesProvider) {
    override fun reset() = with(binding) {
        foodImageView.clear()
    }

    override fun bindViews(model: FoodModel, adapterListener: AdapterListener) {

    }

    override fun bindData(model: FoodModel) = with(binding) {
        foodImageView.load(model.imageUrl, 24f, CenterCrop())
        foodTitleTextView.text = model.title
        foodDescriptionTextView.text = model.description
        priceTextView.text = resourcesProvider.getString(R.string.price, model.price)
    }
}