package com.yb.part6_chapter01.screen.main.home.restaurant.detail

import android.app.AlertDialog
import android.content.ClipDescription.MIMETYPE_TEXT_PLAIN
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.yb.part6_chapter01.R
import com.yb.part6_chapter01.data.entity.RestaurantEntity
import com.yb.part6_chapter01.data.entity.RestaurantFoodEntity
import com.yb.part6_chapter01.databinding.ActivityRestaurantDetailBinding
import com.yb.part6_chapter01.extensions.fromDpToPx
import com.yb.part6_chapter01.extensions.load
import com.yb.part6_chapter01.extensions.toGone
import com.yb.part6_chapter01.extensions.toVisible
import com.yb.part6_chapter01.screen.base.BaseActivity
import com.yb.part6_chapter01.screen.main.MainActivity
import com.yb.part6_chapter01.screen.main.MainTabMenu
import com.yb.part6_chapter01.screen.main.home.restaurant.RestaurantListFragment
import com.yb.part6_chapter01.screen.main.home.restaurant.detail.menu.RestaurantMenuListFragment
import com.yb.part6_chapter01.screen.main.home.restaurant.detail.review.RestaurantReviewListFragment
import com.yb.part6_chapter01.screen.order.OrderMenuListActivity
import com.yb.part6_chapter01.util.event.MenuChangeEventBus
import com.yb.part6_chapter01.widget.adapter.RestaurantDetailListFragmentPagerAdapter
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.experimental.property.inject
import kotlin.math.abs

class RestaurantDetailActivity :
    BaseActivity<RestaurantDetailViewModel, ActivityRestaurantDetailBinding>() {

    companion object {
        fun newIntent(context: Context, restaurantEntity: RestaurantEntity) =
            Intent(context, RestaurantDetailActivity::class.java).apply {
                putExtra(RestaurantListFragment.RESTAURANT_KEY, restaurantEntity)
            }
    }

    override val viewModel by viewModel<RestaurantDetailViewModel>() {
        parametersOf(
            intent.getParcelableExtra<RestaurantEntity>(RestaurantListFragment.RESTAURANT_KEY)
        )
    }

    override fun getViewBinding(): ActivityRestaurantDetailBinding =
        ActivityRestaurantDetailBinding.inflate(layoutInflater)

    private val firebaseAuth by lazy { FirebaseAuth.getInstance() }

    private val menuChangeEventBus by inject<MenuChangeEventBus>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    override fun initViews() {
        initAppBar()
    }

    private lateinit var viewPagerAdapter: RestaurantDetailListFragmentPagerAdapter

    private fun initAppBar() = with(binding) {
        appBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val topPadding = 300f.fromDpToPx().toFloat()
            val realAlphaScrollHeight = appBarLayout.measuredHeight - appBarLayout.totalScrollRange
            val abstractOffset = abs(verticalOffset)
            val realAlphaVerticalOffset: Float =
                if (abstractOffset - topPadding < 0) 0f else abstractOffset - topPadding

            if (abstractOffset < topPadding) {
                restaurantTitleTextView.alpha = 0f
                return@OnOffsetChangedListener
            }

            val percentage = realAlphaVerticalOffset / realAlphaScrollHeight
            restaurantTitleTextView.alpha =
                1 - (if (1 - percentage * 2 < 0) 0f else 1 - percentage * 2)
        })

        toolbar.setNavigationOnClickListener { finish() }
        callButton.setOnClickListener {
            viewModel.getRestaurantTelNumber().let { telNumber ->
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$telNumber"))
                startActivity(intent)
            }

        }
        likeButton.setOnClickListener {
            viewModel.toggleLikedRestaurant()
        }
        shareButton.setOnClickListener {
            viewModel.getRestaurantInfo()?.let { restaurantInfo ->
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = MIMETYPE_TEXT_PLAIN
                    putExtra(
                        Intent.EXTRA_TEXT,
                        "????????? ????????? : ${restaurantInfo.restaurantTitle}" +
                                "\n?????? : ${restaurantInfo.grade}" +
                                "\n????????? : ${restaurantInfo.restaurantTelNumber}"
                    )
                    Intent.createChooser(this, "???????????? ????????????")
                }
                startActivity(intent)
            }
        }
    }


    override fun observeData() = viewModel.restaurantDetailStateLiveData.observe(this) { state ->
        when (state) {
            is RestaurantDetailState.Uninitialized -> {

            }
            is RestaurantDetailState.Loading -> {
                handleLoading()
            }
            is RestaurantDetailState.Success -> {
                handleSuccess(state)
            }
        }
    }

    private fun handleLoading() = with(binding) {
        menuAndReviewLoading.toVisible()
    }

    private fun handleSuccess(state: RestaurantDetailState.Success) = with(binding) {
        menuAndReviewLoading.toGone()

        val restaurantEntity = state.restaurantEntity

        if (!restaurantEntity.restaurantTelNumber.isNullOrBlank())
            callButton.toVisible()
        restaurantTitleTextView.text = restaurantEntity.restaurantTitle
        restaurantImage.load(restaurantEntity.restaurantImageUrl)
        restaurantMainTitleTextView.text = restaurantEntity.restaurantTitle
        ratingBar.rating = restaurantEntity.grade
        deliveryTimeTextView.text = getString(R.string.delivery_expected_time,
            restaurantEntity.deliveryTimeRange.first,
            restaurantEntity.deliveryTimeRange.second)
        deliveryTipTextView.text = getString(R.string.delivery_tip_range,
            restaurantEntity.deliveryTipRange.first,
            restaurantEntity.deliveryTipRange.second)
        likeText.setCompoundDrawablesWithIntrinsicBounds(
            ContextCompat.getDrawable(this@RestaurantDetailActivity,
                if (state.isLiked == true)
                    R.drawable.ic_heart_enable
                else
                    R.drawable.ic_heart_disable),
            null, null, null
        )

        if (::viewPagerAdapter.isInitialized.not()) {
            initViewPager(state.restaurantEntity.restaurantInfoId,
                state.restaurantEntity.restaurantTitle,
                state.restaurantFoodList)
        }

        notifyBasketCount(state.foodMenuListInBasket)

        val (isClearNeed, afterAction) = state.isClearNeedInBasketAndAction
        if (isClearNeed) {
            alertClearNeedInBasket(afterAction)
        }
    }

    private fun initViewPager(
        restaurantInfoId: Long,
        restaurantTitle: String,
        restaurantFoodList: List<RestaurantFoodEntity>?,
    ) = with(binding) {
        viewPagerAdapter = RestaurantDetailListFragmentPagerAdapter(
            this@RestaurantDetailActivity,
            listOf(
                RestaurantMenuListFragment.newInstance(restaurantInfoId,
                    ArrayList(restaurantFoodList ?: listOf())
                ),
                RestaurantReviewListFragment.newInstance(restaurantTitle)
            )
        )
        menuAndReviewViewPager.adapter = viewPagerAdapter
        TabLayoutMediator(menuAndReviewTabLayout, menuAndReviewViewPager) { tab, position ->
            tab.setText(RestaurantDetailCategory.values()[position].categoryNameId)
        }.attach()
    }

    private fun notifyBasketCount(foodMenuListInBasket: List<RestaurantFoodEntity>?) =
        with(binding) {
            basketCountTextView.text = if (foodMenuListInBasket.isNullOrEmpty()) {
                "0"
            } else {
                getString(R.string.basket_count, foodMenuListInBasket.size)
            }
            basketFloatingButton.setOnClickListener {
                if (firebaseAuth.currentUser == null) {
                    alertLoginNeed {
                        lifecycleScope.launch {
                            menuChangeEventBus.changeMenu(MainTabMenu.MY)
                            finish()
                        }
                    }
                } else {
                    startActivity(
                        OrderMenuListActivity.newIntent(this@RestaurantDetailActivity)
                    )
                }
            }
        }

    private fun alertLoginNeed(afterAction: () -> Unit) {
        AlertDialog.Builder(this)
            .setTitle("???????????? ???????????????")
            .setMessage("??????????????? ???????????? ???????????????\n My ????????? ?????????????????????????")
            .setPositiveButton("??????") { dialog, _ ->
                afterAction()
                dialog.dismiss()
            }
            .setNegativeButton("??????") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun alertClearNeedInBasket(afterAction: () -> Unit) {
        AlertDialog.Builder(this)
            .setTitle("?????????????????? ?????? ????????? ????????? ?????? ??? ????????????")
            .setMessage("???????????? ????????? ??????????????? ?????? ?????? ????????? ?????? ????????? ???????????????")
            .setPositiveButton("??????", DialogInterface.OnClickListener { dialogInterface, _ ->
                viewModel.notifyClearBasket()
                afterAction()
                dialogInterface.dismiss()
            })
            .setNegativeButton("?????????", DialogInterface.OnClickListener { dialogInterface, _ ->
                dialogInterface.dismiss()
            })
            .create()
            .show()
    }
}