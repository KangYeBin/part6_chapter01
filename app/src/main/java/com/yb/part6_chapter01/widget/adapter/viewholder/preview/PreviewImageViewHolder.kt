package com.yb.part6_chapter01.widget.adapter.viewholder.preview

import com.yb.part6_chapter01.databinding.ViewholderPreviewImageBinding
import com.yb.part6_chapter01.extensions.clear
import com.yb.part6_chapter01.extensions.load
import com.yb.part6_chapter01.model.preview.PreviewImageModel
import com.yb.part6_chapter01.screen.base.BaseViewModel
import com.yb.part6_chapter01.util.provider.ResourcesProvider
import com.yb.part6_chapter01.widget.adapter.listener.AdapterListener
import com.yb.part6_chapter01.widget.adapter.viewholder.ModelViewHolder

class PreviewImageViewHolder(
    private val binding: ViewholderPreviewImageBinding,
    viewModel: BaseViewModel,
    resourcesProvider: ResourcesProvider,
) : ModelViewHolder<PreviewImageModel>(binding, viewModel, resourcesProvider) {

    override fun reset() {
        binding.imageView.clear()
    }

    override fun bindData(model: PreviewImageModel) {
        super.bindData(model)

        binding.imageView.load(model.uri.toString())
    }

    override fun bindViews(model: PreviewImageModel, adapterListener: AdapterListener) = Unit
}