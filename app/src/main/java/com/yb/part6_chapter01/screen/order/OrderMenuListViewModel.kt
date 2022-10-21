package com.yb.part6_chapter01.screen.order

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.yb.part6_chapter01.R
import com.yb.part6_chapter01.data.repository.order.DefaultOrderRepository
import com.yb.part6_chapter01.data.repository.order.OrderRepository
import com.yb.part6_chapter01.data.repository.restaurant.food.RestaurantFoodRepository
import com.yb.part6_chapter01.model.CellType
import com.yb.part6_chapter01.model.restaurant.food.FoodModel
import com.yb.part6_chapter01.screen.base.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class OrderMenuListViewModel(
    private val restaurantFoodRepository: RestaurantFoodRepository,
    private val orderRepository: OrderRepository,
) : BaseViewModel() {

    private val firebaseAuth by lazy { FirebaseAuth.getInstance() }

    val orderMenuListStateLiveData =
        MutableLiveData<OrderMenuListState>(OrderMenuListState.UnInitialized)

    override fun fetchData(): Job = viewModelScope.launch {
        orderMenuListStateLiveData.value = OrderMenuListState.Loading
        val foodMenuList = restaurantFoodRepository.getAllFoodMenuListInBasket()
        orderMenuListStateLiveData.value = OrderMenuListState.Success(
            foodMenuList.map {
                FoodModel(
                    id = it.hashCode().toLong(),
                    type = CellType.ORDER_FOOD_CELL,
                    title = it.title,
                    description = it.description,
                    price = it.price,
                    imageUrl = it.imageUrl,
                    restaurantId = it.restaurantId,
                    foodId = it.id,
                    restaurantTitle = it.restaurantTitle
                )
            }
        )
    }

    fun clearOrderMenu() = viewModelScope.launch {
        restaurantFoodRepository.clearFoodMenuListInBasket()
        fetchData()
    }

    fun removeOrderModel(foodModel: FoodModel) = viewModelScope.launch {
        restaurantFoodRepository.removeFoodMenuInBasket(foodModel.foodId)
        fetchData()
    }

    fun orderMenu() = viewModelScope.launch {
        val foodMenuList = restaurantFoodRepository.getAllFoodMenuListInBasket()
        if (foodMenuList.isNotEmpty()) {
            val restaurantId = foodMenuList.first().restaurantId
            val restaurantTitle = foodMenuList.first().restaurantTitle
            firebaseAuth.currentUser?.let { user ->
                when (val data = orderRepository.orderMenu(restaurantId, user.uid, foodMenuList, restaurantTitle)) {
                    is DefaultOrderRepository.Result.Success<*> -> {
                        restaurantFoodRepository.clearFoodMenuListInBasket()
                        orderMenuListStateLiveData.value = OrderMenuListState.Order
                    }
                    is DefaultOrderRepository.Result.Error -> {
                        orderMenuListStateLiveData.value = OrderMenuListState.Error(
                            R.string.request_error, data.e
                        )
                    }
                }
            } ?: kotlin.run {
                orderMenuListStateLiveData.value = OrderMenuListState.Error(
                    R.string.user_id_not_found, IllegalAccessException()
                )

            }
        }
    }
}