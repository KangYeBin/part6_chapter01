package com.yb.part6_chapter01.screen.review.camera

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.hardware.display.DisplayManager
import android.hardware.display.DisplayManager.DisplayListener
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.registerForActivityResult
import androidx.camera.core.*
import androidx.camera.core.impl.ImageOutputConfig
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.yb.part6_chapter01.databinding.ActivityCameraBinding
import com.yb.part6_chapter01.extensions.load
import com.yb.part6_chapter01.extensions.toVisible
import com.yb.part6_chapter01.screen.base.BaseActivity
import com.yb.part6_chapter01.screen.review.camera.preview.PreviewImageListActivity
import com.yb.part6_chapter01.util.path.PathUtil
import org.koin.android.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileNotFoundException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraActivity : BaseActivity<CameraViewModel, ActivityCameraBinding>() {
    override val viewModel by viewModel<CameraViewModel>()

    override fun getViewBinding(): ActivityCameraBinding =
        ActivityCameraBinding.inflate(layoutInflater)

    private lateinit var cameraExecutor: ExecutorService
    private val cameraMainExecutor by lazy { ContextCompat.getMainExecutor(this) }
    private val cameraProviderFuture by lazy { ProcessCameraProvider.getInstance(this) }
    private val displayManager by lazy { getSystemService(DISPLAY_SERVICE) as DisplayManager }
    private var displayId: Int = -1
    private var camera: Camera? = null
    private lateinit var imageCapture: ImageCapture
    private var root: View? = null
    private var isCapturing: Boolean = false
    private var isFlashEnabled: Boolean = false
    private var uriList = mutableListOf<Uri>()

    private val displayListener = object : DisplayListener {
        override fun onDisplayAdded(displayId: Int) = Unit

        override fun onDisplayRemoved(displayId: Int) = Unit

        @SuppressLint("RestrictedApi")
        override fun onDisplayChanged(displayId: Int) {
            if (this@CameraActivity.displayId == displayId) {
                if (::imageCapture.isInitialized && root != null) {
                    imageCapture.targetRotation =
                        root?.display?.rotation ?: ImageOutputConfig.INVALID_ROTATION
                }
            }
        }
    }

    private val previewLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                setResult(RESULT_OK, result.data)
                finish()
            }
        }

    companion object {
        private val LENS_FACING = CameraSelector.LENS_FACING_BACK
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"

        fun newIntent(context: Context) = Intent(context, CameraActivity::class.java)
    }

    override fun initViews() = with(binding) {
        startCamera(viewFinder)
        captureButton.setOnClickListener {
            if (!isCapturing) {
                isCapturing = true
                captureCamera()
            }
        }
        previewImageView.setOnClickListener {
            previewLauncher.launch(PreviewImageListActivity.newIntent(this@CameraActivity, uriList))
        }
        val hasFlash = camera?.cameraInfo?.hasFlashUnit() ?: false

        if (hasFlash) {
            flashSwitch.toVisible()
            flashSwitch.setOnCheckedChangeListener { _, isChecked ->
                isFlashEnabled = isChecked
            }
        } else {
            isFlashEnabled = false
            flashSwitch.setOnCheckedChangeListener(null)
        }
    }

    private fun startCamera(viewFinder: PreviewView) {
        displayManager.registerDisplayListener(displayListener, null)
        cameraExecutor = Executors.newSingleThreadExecutor()
        viewFinder.postDelayed({
            displayId = viewFinder.display.displayId
            bindCameraUseCase()
        }, 10)
    }

    private fun bindCameraUseCase() = with(binding) {
        val rotation = viewFinder.display.rotation
        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(LENS_FACING)     // 카메라 설정 (후면)
            .build()

        cameraProviderFuture.addListener(
            {
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().apply {
                    setTargetAspectRatio(AspectRatio.RATIO_4_3)
                    setTargetRotation(rotation)
                }.build()

                val imageCaptureBuilder = ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                    .setTargetRotation(rotation)
                    .setFlashMode(ImageCapture.FLASH_MODE_AUTO)

                imageCapture = imageCaptureBuilder.build()

                try {
                    cameraProvider.unbindAll() // 기존에 바인딩 되어있는 카메라는 해제
                    camera = cameraProvider.bindToLifecycle(
                        this@CameraActivity,
                        cameraSelector,
                        preview,
                        imageCapture
                    )
                    preview.setSurfaceProvider(viewFinder.surfaceProvider)
                    bindZoomListener()

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, cameraMainExecutor
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun bindZoomListener() = with(binding) {
        val listener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                val currentZoomRatio = camera?.cameraInfo?.zoomState?.value?.zoomRatio ?: 1f
                val delta = detector.scaleFactor
                camera?.cameraControl?.setZoomRatio(currentZoomRatio * delta)

                return true
            }
        }

        val scaleGestureDetector = ScaleGestureDetector(this@CameraActivity, listener)
        viewFinder.setOnTouchListener { _, event ->
            scaleGestureDetector.onTouchEvent(event)
            return@setOnTouchListener true
        }
    }

    private var contentUri: Uri? = null

    private fun captureCamera() {
        if (::imageCapture.isInitialized.not()) return

        val photoFile = File(
            PathUtil.getOutputDirectory(this),
            SimpleDateFormat(FILENAME_FORMAT,
                Locale.KOREA).format(System.currentTimeMillis()) + ".jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        if (isFlashEnabled) {
            flashLight(true)
        }

        imageCapture.takePicture(
            outputOptions,
            cameraExecutor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val savedUri = outputFileResults.savedUri ?: Uri.fromFile(photoFile)
                    contentUri = savedUri
                    updateSavedImageContent()
                }

                override fun onError(exception: ImageCaptureException) {
                    exception.printStackTrace()
                    isCapturing = false
                    flashLight(false)
                }

            }
        )
    }

    private fun flashLight(light: Boolean) {
        val hasFlash = camera?.cameraInfo?.hasFlashUnit() ?: false
        if (hasFlash) {
            camera?.cameraControl?.enableTorch(light)
        }
    }

    private fun updateSavedImageContent() {
        contentUri?.let {
            isCapturing = try {
                val file = File(PathUtil.getPath(this, it) ?: throw FileNotFoundException())
                MediaScannerConnection.scanFile(this,
                    arrayOf(file.path),
                    arrayOf("image/jpeg"),
                    null)

                Handler(Looper.getMainLooper()).post {
                    binding.previewImageView.load(it.toString(), 4f)
                }
                uriList.add(it)
                flashLight(false)
                false
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "파일이 존재하지 않습니다", Toast.LENGTH_SHORT).show()
                flashLight(false)
                false
            }
        }
    }

    override fun observeData() {
    }
}