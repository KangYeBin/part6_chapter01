package com.yb.part6_chapter01.screen.main.home.restaurant.detail.review

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.yb.part6_chapter01.R
import com.yb.part6_chapter01.data.entity.RestaurantReviewEntity
import com.yb.part6_chapter01.data.repository.restaurant.review.DefaultRestaurantReviewRepository
import com.yb.part6_chapter01.data.repository.restaurant.review.RestaurantReviewRepository
import com.yb.part6_chapter01.model.restaurant.review.RestaurantReviewModel
import com.yb.part6_chapter01.screen.base.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class RestaurantReviewListViewModel(
    private val restaurantTitle: String,
    private val restaurantReviewRepository: RestaurantReviewRepository,
) : BaseViewModel() {

    val reviewStateLiveData =
        MutableLiveData<RestaurantReviewState>(RestaurantReviewState.Uninitialized)

    override fun fetchData(): Job = viewModelScope.launch {
        reviewStateLiveData.value = RestaurantReviewState.Loading
        when (val reviews = restaurantReviewRepository.getReviews(restaurantTitle)) {
            is DefaultRestaurantReviewRepository.Result.Success<*> -> {
                val reviewList = reviews.data as List<RestaurantReviewEntity>
                reviewStateLiveData.value = RestaurantReviewState.Success(
                    reviewList.map {
                        RestaurantReviewModel(
                            it.id,
                            restaurantTitle = restaurantTitle,
                            title = it.title,
                            content = it.content,
                            grade = it.rating.toInt(),
                            thumbnailImageUri = it.imageUrlList?.first()
                        )
                    }
                )
            }
            is DefaultRestaurantReviewRepository.Result.Error -> {
                reviewStateLiveData.value = RestaurantReviewState.Error(
                    R.string.request_error,
                    reviews.e
                )
            }
        }
    }
}