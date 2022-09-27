package com.yb.part6_chapter01.screen.main.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import com.google.android.material.tabs.TabLayoutMediator
import com.yb.part6_chapter01.R
import com.yb.part6_chapter01.data.entity.LocationLatLngEntity
import com.yb.part6_chapter01.data.entity.MapSearchInfoEntity
import com.yb.part6_chapter01.databinding.FragmentHomeBinding
import com.yb.part6_chapter01.extensions.toGone
import com.yb.part6_chapter01.extensions.toVisible
import com.yb.part6_chapter01.screen.base.BaseFragment
import com.yb.part6_chapter01.screen.main.home.restaurant.RestaurantCategory
import com.yb.part6_chapter01.screen.main.home.restaurant.RestaurantListFragment
import com.yb.part6_chapter01.screen.mylocation.MyLocationActivity
import com.yb.part6_chapter01.widget.adapter.RestaurantListFragmentPagerAdapter
import org.koin.android.viewmodel.ext.android.viewModel

class HomeFragment : BaseFragment<HomeViewModel, FragmentHomeBinding>() {

    override val viewModel by viewModel<HomeViewModel>()

    override fun getViewBinding(): FragmentHomeBinding = FragmentHomeBinding.inflate(layoutInflater)

    private lateinit var viewPagerAdapter: RestaurantListFragmentPagerAdapter

    private lateinit var locationManager: LocationManager

    private lateinit var myLocationListener: LocationListener

    private val changeLocationLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.getParcelableExtra<MapSearchInfoEntity>(HomeViewModel.MY_LOCATION_KEY)
                    ?.let { mapSearchInfo ->
                        viewModel.loadReverseGeoInformation(mapSearchInfo.locationLatLng)
                    }
            }
        }

    private val locationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val responsePermissions = permissions.entries.filter {
                (it.key == Manifest.permission.ACCESS_FINE_LOCATION)
                        || (it.key == Manifest.permission.ACCESS_COARSE_LOCATION)
            }
            if (responsePermissions.filter { it.value }.size == locationPermissions.size) {
                setMyLocationListener()
            } else {
                with(binding.locationTextView) {
                    setText(R.string.please_setup_your_location_permission)
                    setOnClickListener {
                        getMyLocation()
                    }
                }
                Toast.makeText(requireContext(),
                    getString(R.string.can_not_assigned_permission),
                    Toast.LENGTH_SHORT).show()
            }
        }

    override fun initViews() = with(binding) {
        locationTextView.setOnClickListener {
            viewModel.getMapSearchInfo()?.let { mapSearchInfo ->
                changeLocationLauncher.launch(
                    MyLocationActivity.newIntent(requireContext(), mapSearchInfo)
                )
            }
        }
    }

    private fun initViewPager(locationLatLng: LocationLatLngEntity) = with(binding) {
        val restaurantCategories = RestaurantCategory.values()

        if (::viewPagerAdapter.isInitialized.not()) {
            val restaurantListFragmentList = restaurantCategories.map {
                RestaurantListFragment.newInstance(it)
            }
            viewPagerAdapter =
                RestaurantListFragmentPagerAdapter(this@HomeFragment, restaurantListFragmentList)
            viewPager.adapter = viewPagerAdapter
        }
        viewPager.offscreenPageLimit = restaurantCategories.size
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.setText(restaurantCategories[position].categoryNameId)
        }.attach()

    }

    override fun observeData() = viewModel.homeStateLiveData.observe(viewLifecycleOwner) { state ->
        when (state) {
            is HomeState.Uninitialized -> {
                getMyLocation()
            }
            is HomeState.Loading -> {
                binding.locationLoading.toVisible()
                binding.locationTextView.text = getString(R.string.loading)
            }
            is HomeState.Success -> {
                binding.locationLoading.toGone()
                binding.locationTextView.text = state.mapSearchInfo.fullAddress
                binding.tabLayout.toVisible()
                binding.filterScrollView.toVisible()
                binding.viewPager.toVisible()
                initViewPager(state.mapSearchInfo.locationLatLng)
            }
            is HomeState.Error -> {
                binding.locationLoading.toGone()
                binding.locationTextView.text = getString(R.string.location_no_found)
                binding.locationTextView.setOnClickListener {
                    getMyLocation()
                }
                Toast.makeText(requireContext(), state.messageId, Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

    private fun getMyLocation() {
        if (::locationManager.isInitialized.not()) {
            locationManager =
                requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }
        val isGpsUnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (isGpsUnabled) {
            locationPermissionLauncher.launch(locationPermissions)
        }
    }

    @SuppressLint("MissingPermission")
    private fun setMyLocationListener() {
        val minTime = 1500L
        val minDistance = 100f

        if (::myLocationListener.isInitialized.not()) {
            myLocationListener = MyLocationListener()
        }

        with(locationManager) {
            requestLocationUpdates(LocationManager.GPS_PROVIDER,
                minTime,
                minDistance,
                myLocationListener)
            requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                minTime,
                minDistance,
                myLocationListener)
        }
    }

    companion object {
        val locationPermissions = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        fun newInstance() = HomeFragment()
        const val TAG = "HomeFragment"
    }

    private fun removeLocationListener() {
        if (::locationManager.isInitialized && ::myLocationListener.isInitialized) {
            locationManager.removeUpdates(myLocationListener)
        }
    }

    inner class MyLocationListener : LocationListener {
        override fun onLocationChanged(location: Location) {
            binding.locationTextView.text = "${location.latitude},${location.longitude}"
            viewModel.loadReverseGeoInformation(
                LocationLatLngEntity(
                    location.latitude,
                    location.longitude)
            )
            removeLocationListener()
        }
    }
}