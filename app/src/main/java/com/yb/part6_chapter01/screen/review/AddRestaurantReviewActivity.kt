package com.yb.part6_chapter01.screen.review

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.auth.FirebaseAuth
import com.yb.part6_chapter01.R
import com.yb.part6_chapter01.data.entity.RestaurantReviewEntity
import com.yb.part6_chapter01.databinding.ActivityAddRestaurantReviewBinding
import com.yb.part6_chapter01.extensions.toGone
import com.yb.part6_chapter01.extensions.toVisible
import com.yb.part6_chapter01.model.restaurant.review.ReviewPhotoModel
import com.yb.part6_chapter01.screen.base.BaseActivity
import com.yb.part6_chapter01.screen.review.camera.CameraActivity
import com.yb.part6_chapter01.screen.review.gallery.GalleryActivity
import com.yb.part6_chapter01.util.provider.ResourcesProvider
import com.yb.part6_chapter01.widget.adapter.ModelRecyclerAdapter
import com.yb.part6_chapter01.widget.adapter.listener.review.PhotoListListener
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class AddRestaurantReviewActivity :
    BaseActivity<AddRestaurantReviewViewModel, ActivityAddRestaurantReviewBinding>() {

    override val viewModel by viewModel<AddRestaurantReviewViewModel>() {
        parametersOf(imageUriList)
    }

    override fun getViewBinding(): ActivityAddRestaurantReviewBinding =
        ActivityAddRestaurantReviewBinding.inflate(layoutInflater)

    private var imageUriList: ArrayList<Uri> = arrayListOf()
    private val restaurantTitle by lazy { intent.getStringExtra(RESTAURANT_TITLE_KEY) }
    private val orderId by lazy { intent.getStringExtra(ORDER_ID_KEY) }

    private val resourcesProvider by inject<ResourcesProvider>()

    private val adapter by lazy {
        ModelRecyclerAdapter<ReviewPhotoModel, AddRestaurantReviewViewModel>(
            listOf(),
            viewModel,
            resourcesProvider,
            adapterListener = object : PhotoListListener {
                override fun onItemClickDelete(model: ReviewPhotoModel) {
                    viewModel.deletePhoto(model)
                    imageUriList.remove(model.uri)
                }
            }
        )
    }

    private val firebaseAuth by inject<FirebaseAuth>()

    companion object {
        fun newIntent(
            context: Context,
            restaurantTitle: String,
            orderId: String,
        ) = Intent(context, AddRestaurantReviewActivity::class.java).apply {
            putExtra(RESTAURANT_TITLE_KEY, restaurantTitle)
            putExtra(ORDER_ID_KEY, orderId)
        }

        const val URI_LIST_KEY = "uriList"
        const val RESTAURANT_TITLE_KEY = "restaurantTitle"
        const val ORDER_ID_KEY = "orderId"
    }

    private val cameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                cameraLauncher.launch(CameraActivity.newIntent(this))
            } else {
                Toast.makeText(this,
                    getString(R.string.can_not_assigned_permission),
                    Toast.LENGTH_SHORT).show()
            }
        }

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val uriList = result.data?.getParcelableArrayListExtra<Uri>(URI_LIST_KEY)
                if (uriList != null) {
                    imageUriList.addAll(uriList)
                    viewModel.addPhoto(uriList)
                }
            }
        }

    private val galleryPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                galleryLauncher.launch(GalleryActivity.newIntent(this))
            } else {
                Toast.makeText(this,
                    getString(R.string.can_not_assigned_permission),
                    Toast.LENGTH_SHORT).show()
            }
        }

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val uriList = result.data?.getParcelableArrayListExtra<Uri>(URI_LIST_KEY)
                if (uriList != null) {
                    imageUriList.addAll(uriList)
                    viewModel.addPhoto(uriList)
                }
            }
        }

    override fun initViews() = with(binding) {
        titleTextView.text = getString(R.string.add_restaurant_review, restaurantTitle)
        photoRecyclerView.adapter = adapter
        imageAddButton.setOnClickListener {
            showPictureUploadDialog()
        }
        submitButton.setOnClickListener {
            val title = titleEditText.text.toString()
            val content = contentEditText.text.toString()
            val userId = firebaseAuth.currentUser?.uid.orEmpty()
            val rating = ratingBar.rating

            val restaurantReviewEntity = RestaurantReviewEntity(
                hashCode().toLong(),
                userId,
                title,
                System.currentTimeMillis(),
                content,
                rating,
                imageUriList.map { it.toString() },
                orderId!!,
                restaurantTitle!!
            )
            viewModel.uploadReview(restaurantReviewEntity)
        }
    }

    private fun showPictureUploadDialog() {
        AlertDialog.Builder(this)
            .setTitle("사진 첨부")
            .setMessage("사진 첨부할 방식을 선택하세요")
            .setPositiveButton("카메라") { _, _ ->
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
            .setNegativeButton("갤러리") { _, _ ->
                galleryPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            .create()
            .show()
    }

    override fun observeData() = viewModel.uploadReviewStateLiveData.observe(this) { state ->
        when (state) {
            is AddRestaurantReviewState.Uninitialized -> Unit
            is AddRestaurantReviewState.Loading -> handleLoading()
            is AddRestaurantReviewState.Success -> handleSuccess(state)
            is AddRestaurantReviewState.Confirm -> handleConfirm()
            is AddRestaurantReviewState.Error -> handleError(state)
        }
    }

    private fun handleLoading() {
        binding.progressBar.toVisible()
    }

    private fun handleSuccess(state: AddRestaurantReviewState.Success) {
        binding.progressBar.toGone()
        adapter.submitList(state.reviewPhotoList)
    }

    private fun handleConfirm() {
        binding.progressBar.toGone()
        Toast.makeText(this, "리뷰가 업로드되었습니다", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun handleError(state: AddRestaurantReviewState.Error) {
        Toast.makeText(this, getString(state.messageId, state.e), Toast.LENGTH_SHORT).show()
    }
}