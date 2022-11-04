package com.yb.part6_chapter01.widget.adapter.viewholder.review

import com.yb.part6_chapter01.databinding.ViewholderRestaurantReviewBinding
import com.yb.part6_chapter01.extensions.clear
import com.yb.part6_chapter01.extensions.load
import com.yb.part6_chapter01.extensions.toGone
import com.yb.part6_chapter01.extensions.toVisible
import com.yb.part6_chapter01.model.restaurant.review.RestaurantReviewModel
import com.yb.part6_chapter01.screen.base.BaseViewModel
import com.yb.part6_chapter01.util.provider.ResourcesProvider
import com.yb.part6_chapter01.widget.adapter.listener.AdapterListener
import com.yb.part6_chapter01.widget.adapter.viewholder.ModelViewHolder

class RestaurantReviewViewHolder(
    private val binding: ViewholderRestaurantReviewBinding,
    viewModel: BaseViewModel,
    resourcesProvider: ResourcesProvider,
) : ModelViewHolder<RestaurantReviewModel>(binding, viewModel, resourcesProvider) {
    override fun reset() = with(binding) {
        reviewThumbnailImage.clear()
        reviewThumbnailImage.toGone()
    }

    override fun bindViews(model: RestaurantReviewModel, adapterListener: AdapterListener) = Unit

    override fun bindData(model: RestaurantReviewModel) = with(binding) {
        super.bindData(model)

        if (model.thumbnailImageUri != null) {
            reviewThumbnailImage.toVisible()
            reviewThumbnailImage.load(model.thumbnailImageUri.toString(), 24f)
        } else {
            reviewThumbnailImage.toGone()
        }
        reviewTitleTextView.text = model.title
        reviewTextView.text = model.content
        ratingBar.rating = model.grade.toFloat()
    }
}