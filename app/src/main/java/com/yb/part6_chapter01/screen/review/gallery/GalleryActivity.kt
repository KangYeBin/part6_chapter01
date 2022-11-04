package com.yb.part6_chapter01.screen.review.gallery

import android.content.Context
import android.content.Intent
import com.yb.part6_chapter01.databinding.ActivityGalleryBinding
import com.yb.part6_chapter01.extensions.toGone
import com.yb.part6_chapter01.extensions.toVisible
import com.yb.part6_chapter01.model.gallery.GalleryPhotoModel
import com.yb.part6_chapter01.screen.base.BaseActivity
import com.yb.part6_chapter01.util.provider.ResourcesProvider
import com.yb.part6_chapter01.widget.adapter.ModelRecyclerAdapter
import com.yb.part6_chapter01.widget.adapter.listener.gallery.GalleryPhotoListListener
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class GalleryActivity : BaseActivity<GalleryViewModel, ActivityGalleryBinding>() {

    override val viewModel by viewModel<GalleryViewModel>()

    override fun getViewBinding(): ActivityGalleryBinding =
        ActivityGalleryBinding.inflate(layoutInflater)

    private val resourcesProvider by inject<ResourcesProvider>()

    private val adapter by lazy {
        ModelRecyclerAdapter<GalleryPhotoModel, GalleryViewModel>(
            listOf(),
            viewModel,
            resourcesProvider,
            object : GalleryPhotoListListener {
                override fun onItemClick(model: GalleryPhotoModel) {
                    viewModel.selectPhoto(model)
                }
            }
        )
    }

    companion object {
        fun newIntent(context: Context) = Intent(context, GalleryActivity::class.java)

        private const val URI_LIST_KEY = "uriList"
    }

    override fun initViews() = with(binding) {
        viewModel.fetchData()
        toolbar.setNavigationOnClickListener { finish() }
        recyclerView.adapter = adapter
        confirmButton.setOnClickListener {
            viewModel.confirmCheckedPhotos()
        }
    }

    override fun observeData() = viewModel.galleryStateLiveData.observe(this) { state ->
        when (state) {
            is GalleryState.Loading -> handleLoading()
            is GalleryState.Success -> handleSuccess(state)
            is GalleryState.Confirm -> handleConfirm(state)
            else -> Unit
        }
    }

    private fun handleLoading() = with(binding) {
        progressBar.toVisible()
        recyclerView.toGone()
    }

    private fun handleSuccess(state: GalleryState.Success) = with(binding) {
        progressBar.toGone()
        recyclerView.toVisible()
        adapter.submitList(state.photoList)
        adapter.notifyDataSetChanged()
    }

    private fun handleConfirm(state: GalleryState.Confirm) {
        setResult(RESULT_OK, Intent().apply {
            putExtra(URI_LIST_KEY, ArrayList(state.photoList.map { it.uri }))
        })
        finish()
    }

}