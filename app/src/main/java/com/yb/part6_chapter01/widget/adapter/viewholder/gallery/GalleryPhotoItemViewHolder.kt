package com.yb.part6_chapter01.widget.adapter.viewholder.gallery

import androidx.core.content.ContextCompat
import com.yb.part6_chapter01.R
import com.yb.part6_chapter01.databinding.ViewholderGalleryPhotoItemBinding
import com.yb.part6_chapter01.extensions.clear
import com.yb.part6_chapter01.extensions.load
import com.yb.part6_chapter01.model.gallery.GalleryPhotoModel
import com.yb.part6_chapter01.screen.base.BaseViewModel
import com.yb.part6_chapter01.util.provider.ResourcesProvider
import com.yb.part6_chapter01.widget.adapter.listener.AdapterListener
import com.yb.part6_chapter01.widget.adapter.listener.gallery.GalleryPhotoListListener
import com.yb.part6_chapter01.widget.adapter.viewholder.ModelViewHolder

class GalleryPhotoItemViewHolder(
    private val binding: ViewholderGalleryPhotoItemBinding,
    viewModel: BaseViewModel,
    resourcesProvider: ResourcesProvider,
) : ModelViewHolder<GalleryPhotoModel>(binding, viewModel, resourcesProvider) {

    override fun reset() {
        binding.photoImageView.clear()
    }

    override fun bindViews(model: GalleryPhotoModel, adapterListener: AdapterListener) {
        if (adapterListener is GalleryPhotoListListener) {
            binding.root.setOnClickListener {
                adapterListener.onItemClick(model)
            }
        }
    }

    override fun bindData(model: GalleryPhotoModel) = with(binding) {
        super.bindData(model)

        photoImageView.load(model.uri.toString(), 8f)
        checkButton.setImageDrawable(
            ContextCompat.getDrawable(
                root.context,
                if (model.isSelected)
                    R.drawable.ic_check_enabled
                else
                    R.drawable.ic_check_disabled
            )
        )
    }
}