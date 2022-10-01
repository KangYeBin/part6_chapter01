package com.yb.part6_chapter01.screen.mylocation

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.yb.part6_chapter01.R
import com.yb.part6_chapter01.data.entity.LocationLatLngEntity
import com.yb.part6_chapter01.data.entity.MapSearchInfoEntity
import com.yb.part6_chapter01.databinding.ActivityMyLocationBinding
import com.yb.part6_chapter01.extensions.toGone
import com.yb.part6_chapter01.extensions.toVisible
import com.yb.part6_chapter01.screen.base.BaseActivity
import com.yb.part6_chapter01.screen.main.home.HomeViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class MyLocationActivity : BaseActivity<MyLocationViewModel, ActivityMyLocationBinding>(),
    OnMapReadyCallback {

    companion object {
        const val CAMERA_ZOOM_LEVEL = 17f

        fun newIntent(context: Context, mapSearchInfoEntity: MapSearchInfoEntity) =
            Intent(context, MyLocationActivity::class.java).apply {
                putExtra(HomeViewModel.MY_LOCATION_KEY, mapSearchInfoEntity)
            }
    }

    override val viewModel by viewModel<MyLocationViewModel>() {
        parametersOf(
            intent.getParcelableExtra<MapSearchInfoEntity>(HomeViewModel.MY_LOCATION_KEY)
        )
    }

    override fun getViewBinding(): ActivityMyLocationBinding =
        ActivityMyLocationBinding.inflate(layoutInflater)

    private lateinit var map: GoogleMap

    private var isMapInitialized: Boolean = false
    private var isChangeLocation: Boolean = false

    override fun onMapReady(map: GoogleMap) {
        this.map = map ?: return
        viewModel.fetchData()
    }

    override fun initViews() = with(binding) {
        toolbar.setNavigationOnClickListener {
            finish()
        }
        confirmButton.setOnClickListener {
            viewModel.confirmSelectLocation()
        }
        setupGoogleMap()
    }

    private fun setupGoogleMap() {
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun observeData() = viewModel.myLocationStateLiveData.observe(this) { state ->
        when (state) {
            is MyLocationState.Loading -> {
                handleLoadingState()
            }
            is MyLocationState.Success -> {
                if (::map.isInitialized) {
                    handleSuccessState(state)
                }
            }
            is MyLocationState.Confirm -> {
                setResult(Activity.RESULT_OK, Intent().apply {
                    putExtra(HomeViewModel.MY_LOCATION_KEY, state.mapSearchInfoEntity)
                })
                finish()
            }
            is MyLocationState.Error -> {
                Toast.makeText(this, state.messageId, Toast.LENGTH_SHORT).show()
            }
            else -> Unit
        }
    }

    private fun handleLoadingState() = with(binding) {
        locationLoading.toVisible()
        locationTextView.text = getString(R.string.loading)
    }

    private fun handleSuccessState(state: MyLocationState.Success) = with(binding) {
        val mapSearchInfoEntity = state.mapSearchInfoEntity
        locationLoading.toGone()
        locationTextView.text = mapSearchInfoEntity.fullAddress

        if (isMapInitialized.not()) {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    mapSearchInfoEntity.locationLatLng.latitude,
                    mapSearchInfoEntity.locationLatLng.longitude
                ), CAMERA_ZOOM_LEVEL
            ))
            map.setOnCameraIdleListener {
                if (isChangeLocation.not()) {
                    isChangeLocation = true
                    Handler(Looper.getMainLooper()).postDelayed(kotlinx.coroutines.Runnable {
                        val cameraLatLng = map.cameraPosition.target
                        viewModel.changeLocationInfo(
                            LocationLatLngEntity(
                                cameraLatLng.latitude,
                                cameraLatLng.longitude
                            )
                        )
                        isChangeLocation = false
                    }, 1000)
                }

            }
            isMapInitialized = true
        }
    }
}