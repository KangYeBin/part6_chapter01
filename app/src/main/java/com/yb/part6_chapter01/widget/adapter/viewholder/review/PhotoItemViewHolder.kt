package com.yb.part6_chapter01.widget.adapter.viewholder.review

import com.yb.part6_chapter01.databinding.ViewholderPhotoItemBinding
import com.yb.part6_chapter01.extensions.clear
import com.yb.part6_chapter01.extensions.load
import com.yb.part6_chapter01.model.restaurant.review.ReviewPhotoModel
import com.yb.part6_chapter01.screen.base.BaseViewModel
import com.yb.part6_chapter01.util.provider.ResourcesProvider
import com.yb.part6_chapter01.widget.adapter.listener.AdapterListener
import com.yb.part6_chapter01.widget.adapter.listener.gallery.GalleryPhotoListListener
import com.yb.part6_chapter01.widget.adapter.listener.review.PhotoListListener
import com.yb.part6_chapter01.widget.adapter.viewholder.ModelViewHolder

class PhotoItemViewHolder(
    private val binding: ViewholderPhotoItemBinding,
    viewModel: BaseViewModel,
    resourcesProvider: ResourcesProvider,
) : ModelViewHolder<ReviewPhotoModel>(binding, viewModel, resourcesProvider) {

    override fun reset() {
        binding.photoImageView.clear()
    }

    override fun bindViews(model: ReviewPhotoModel, adapterListener: AdapterListener) {
        if (adapterListener is PhotoListListener) {
            binding.deleteButton.setOnClickListener {
                adapterListener.onItemClickDelete(model)
            }
        }
    }

    override fun bindData(model: ReviewPhotoModel) = with(binding) {
        super.bindData(model)

        photoImageView.load(model.uri.toString(), 8f)
    }
}