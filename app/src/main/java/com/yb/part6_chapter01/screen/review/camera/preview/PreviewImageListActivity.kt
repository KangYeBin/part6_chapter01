package com.yb.part6_chapter01.screen.review.camera.preview

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.yb.part6_chapter01.databinding.ActivityPreviewImageListBinding
import com.yb.part6_chapter01.model.preview.PreviewImageModel
import com.yb.part6_chapter01.screen.base.BaseActivity
import com.yb.part6_chapter01.util.provider.ResourcesProvider
import com.yb.part6_chapter01.widget.adapter.ModelRecyclerAdapter
import com.yb.part6_chapter01.widget.adapter.listener.AdapterListener
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class PreviewImageListActivity :
    BaseActivity<PreviewImageListViewModel, ActivityPreviewImageListBinding>() {

    override val viewModel by inject<PreviewImageListViewModel>() {
        parametersOf(uriList)
    }

    override fun getViewBinding(): ActivityPreviewImageListBinding =
        ActivityPreviewImageListBinding.inflate(layoutInflater)

    companion object {
        const val URI_LIST_KEY = "uriList"

        fun newIntent(context: Context, uriList: List<Uri>) =
            Intent(context, PreviewImageListActivity::class.java).apply {
                putExtra(URI_LIST_KEY, ArrayList<Uri>().apply {
                    uriList.forEach { uri ->
                        add(uri)
                    }
                })
            }
    }

    private val resourcesProvider by inject<ResourcesProvider>()

    private val adapter by lazy {
        ModelRecyclerAdapter<PreviewImageModel, PreviewImageListViewModel>(
            listOf(),
            viewModel,
            resourcesProvider,
            adapterListener = object : AdapterListener {}
        )
    }

    private val uriList by lazy<List<Uri>> { intent.getParcelableArrayListExtra(URI_LIST_KEY)!! }
    private var currentUri: Uri? = null

    override fun initViews() = with(binding) {
        toolbar.setNavigationOnClickListener { finish() }
        imageViewPager.adapter = adapter
        indicator.setViewPager(imageViewPager)
        imageViewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (val model = adapter.modelList[position]) {
                    is PreviewImageModel -> currentUri = model.uri
                }
            }
        })
        deleteButton.setOnClickListener {
            if (currentUri == null) {
                Toast.makeText(this@PreviewImageListActivity,
                    "삭제할 수 있는 이미지가 없습니다.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
            currentUri?.let { uri ->
                viewModel.removeImage(uri, this@PreviewImageListActivity)
            }
        }
        confirmButton.setOnClickListener {
            viewModel.confirmImage()
        }
    }

    override fun observeData() = viewModel.previewImageListStateLiveData.observe(this) { state ->
        when (state) {
            is PreviewImageListState.Uninitialized -> Unit
            is PreviewImageListState.Loading -> Unit
            is PreviewImageListState.Success -> handleSuccess(state)
            is PreviewImageListState.Confirm -> handleConfirm(state)
        }
    }

    private fun handleSuccess(state: PreviewImageListState.Success) {
        adapter.submitList(state.previewImageList)
    }

    private fun handleConfirm(state: PreviewImageListState.Confirm) {
        setResult(RESULT_OK, Intent().apply {
            putExtra(URI_LIST_KEY, ArrayList<Uri>().apply {
                state.previewImageList.forEach { model ->
                    add(model.uri)
                }
            })
        })
        finish()
    }
}