package com.yb.part6_chapter01.screen.main.home.restaurant.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.yb.part6_chapter01.data.entity.RestaurantEntity
import com.yb.part6_chapter01.data.entity.RestaurantFoodEntity
import com.yb.part6_chapter01.data.repository.restaurant.food.RestaurantFoodRepository
import com.yb.part6_chapter01.data.repository.user.UserRepository
import com.yb.part6_chapter01.screen.base.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class RestaurantDetailViewModel(
    private val restaurantEntity: RestaurantEntity,
    private val restaurantFoodRepository: RestaurantFoodRepository,
    private val userRepository: UserRepository,
) : BaseViewModel() {

    val restaurantDetailStateLiveData =
        MutableLiveData<RestaurantDetailState>(RestaurantDetailState.Uninitialized)


    override fun fetchData(): Job = viewModelScope.launch {
        restaurantDetailStateLiveData.value = RestaurantDetailState.Success(restaurantEntity)
        restaurantDetailStateLiveData.value = RestaurantDetailState.Loading
        val foods = restaurantFoodRepository.getFoods(restaurantEntity.restaurantInfoId, restaurantEntity.restaurantTitle)
        val foodMenuListInBasket = restaurantFoodRepository.getAllFoodMenuListInBasket()
        val isLiked =
            userRepository.getUserLikedRestaurant(restaurantEntity.restaurantTitle) != null
        restaurantDetailStateLiveData.value =
            RestaurantDetailState.Success(
                restaurantEntity = restaurantEntity,
                restaurantFoodList = foods,
                foodMenuListInBasket = foodMenuListInBasket,
                isLiked = isLiked)
    }

    fun getRestaurantTelNumber(): String? {
        return when (val data = restaurantDetailStateLiveData.value) {
            is RestaurantDetailState.Success -> {
                data.restaurantEntity.restaurantTelNumber
            }
            else -> null
        }
    }

    fun getRestaurantInfo(): RestaurantEntity? {
        return when (val data = restaurantDetailStateLiveData.value) {
            is RestaurantDetailState.Success -> {
                data.restaurantEntity
            }
            else -> null
        }
    }

    fun toggleLikedRestaurant() = viewModelScope.launch {
        when (val data = restaurantDetailStateLiveData.value) {
            is RestaurantDetailState.Success -> {
                userRepository.getUserLikedRestaurant(data.restaurantEntity.restaurantTitle)
                    ?.let { entity ->
                        userRepository.deleteUserLikedRestaurant(entity.restaurantTitle)
                        restaurantDetailStateLiveData.value = data.copy(
                            isLiked = false
                        )
                    } ?: kotlin.run {
                    userRepository.insertUserLikedRestaurant(restaurantEntity)
                    restaurantDetailStateLiveData.value = data.copy(
                        isLiked = true
                    )
                }
            }
        }
    }

    fun notifyFoodMenuListInBasket(restaurantFoodEntity: RestaurantFoodEntity) =
        viewModelScope.launch {
            when (val data = restaurantDetailStateLiveData.value) {
                is RestaurantDetailState.Success -> {
                    restaurantDetailStateLiveData.value = data.copy(
                        foodMenuListInBasket = data.foodMenuListInBasket?.toMutableList()?.apply {
                            add(restaurantFoodEntity)
                        }
                    )
                }
                else -> Unit
            }
        }

    fun notifyClearNeedAlertInBasket(isClearNeed: Boolean, afterAction: () -> Unit) =
        viewModelScope.launch {
            when (val data = restaurantDetailStateLiveData.value) {
                is RestaurantDetailState.Success -> {
                    restaurantDetailStateLiveData.value = data.copy(
                        isClearNeedInBasketAndAction = isClearNeed to afterAction
                    )
                }
                else -> Unit
            }
        }

    fun notifyClearBasket() = viewModelScope.launch {
        when (val data = restaurantDetailStateLiveData.value) {
            is RestaurantDetailState.Success -> {
                restaurantDetailStateLiveData.value = data.copy(
                    foodMenuListInBasket = listOf(),
                    isClearNeedInBasketAndAction = false to {}
                )
            }
            else -> Unit
        }

    }
}