package com.yb.part6_chapter01.screen.main.home.restaurant.detail

import android.app.AlertDialog
import android.content.ClipDescription.MIMETYPE_TEXT_PLAIN
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.yb.part6_chapter01.R
import com.yb.part6_chapter01.data.entity.RestaurantEntity
import com.yb.part6_chapter01.data.entity.RestaurantFoodEntity
import com.yb.part6_chapter01.databinding.ActivityRestaurantDetailBinding
import com.yb.part6_chapter01.extensions.fromDpToPx
import com.yb.part6_chapter01.extensions.load
import com.yb.part6_chapter01.extensions.toGone
import com.yb.part6_chapter01.extensions.toVisible
import com.yb.part6_chapter01.screen.base.BaseActivity
import com.yb.part6_chapter01.screen.main.home.restaurant.RestaurantListFragment
import com.yb.part6_chapter01.screen.main.home.restaurant.detail.menu.RestaurantMenuListFragment
import com.yb.part6_chapter01.screen.main.home.restaurant.detail.review.RestaurantReviewListFragment
import com.yb.part6_chapter01.widget.adapter.RestaurantDetailListFragmentPagerAdapter
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
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
                        "맛있는 음식점 : ${restaurantInfo.restaurantTitle}" +
                                "\n평점 : ${restaurantInfo.grade}" +
                                "\n연락처 : ${restaurantInfo.restaurantTelNumber}"
                    )
                    Intent.createChooser(this, "친구에게 공유하기")
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
                // TODO 주문하기 화면으로 이동 or 로그인
            }
        }

    private fun alertClearNeedInBasket(afterAction: () -> Unit) {
        AlertDialog.Builder(this)
            .setTitle("장바구니에는 같은 가게의 메뉴만 담을 수 있습니다")
            .setMessage("선택하신 메뉴를 장바구니에 담을 경우 이전에 담은 메뉴가 삭제됩니다")
            .setPositiveButton("담기", DialogInterface.OnClickListener { dialogInterface, _ ->
                viewModel.notifyClearBasket()
                afterAction()
                dialogInterface.dismiss()
            })
            .setNegativeButton("아니오", DialogInterface.OnClickListener { dialogInterface, _ ->
                dialogInterface.dismiss()
            })
            .create()
            .show()
    }
}