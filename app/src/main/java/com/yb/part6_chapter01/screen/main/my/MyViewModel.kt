package com.yb.part6_chapter01.screen.main.my

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.yb.part6_chapter01.R
import com.yb.part6_chapter01.data.entity.OrderEntity
import com.yb.part6_chapter01.data.preference.PreferenceManager
import com.yb.part6_chapter01.data.repository.order.DefaultOrderRepository
import com.yb.part6_chapter01.data.repository.order.OrderRepository
import com.yb.part6_chapter01.data.repository.user.UserRepository
import com.yb.part6_chapter01.model.restaurant.order.OrderModel
import com.yb.part6_chapter01.screen.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyViewModel(
    private val preferenceManager: PreferenceManager,
    private val userRepository: UserRepository,
    private val orderRepository: OrderRepository,
) : BaseViewModel() {

    val myStateLiveData = MutableLiveData<MyState>(MyState.UnInitialized)

    fun saveToken(idToken: String) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            preferenceManager.putIdToken(idToken)
            fetchData()
        }
    }

    override fun fetchData(): Job = viewModelScope.launch {
        myStateLiveData.value = MyState.Loading
        preferenceManager.getIdToken()?.let { idToken ->
            myStateLiveData.value = MyState.Login(idToken)
        } ?: kotlin.run {
            myStateLiveData.value = MyState.Success.NotRegistered
        }
    }

    fun setUserInfo(firebaseUser: FirebaseUser?) = viewModelScope.launch {
        firebaseUser?.let { user ->
            when (val orderMenusResult = orderRepository.getAllOrderMenus(user.uid)) {
                is DefaultOrderRepository.Result.Success<*> -> {
                    val orderList = orderMenusResult.data as List<OrderEntity>
                    myStateLiveData.value = MyState.Success.Registered(
                        userName = user.displayName ?: "익명",
                        profileUri = user.photoUrl,
                        orderList = orderList.map {
                            OrderModel(
                                id = it.hashCode().toLong(),
                                orderId = it.id,
                                userId = it.userId,
                                restaurantId = it.restaurantId,
                                foodMenuList = it.foodMenuList,
                                restaurantTitle = it.restaurantTitle
                            )
                        }
                    )
                }
                is DefaultOrderRepository.Result.Error -> {
                    myStateLiveData.value = MyState.Error(
                        R.string.request_error,
                        orderMenusResult.e
                    )
                }
            }
        } ?: kotlin.run {
            myStateLiveData.value = MyState.Success.NotRegistered
        }
    }

    fun signOut() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            preferenceManager.removeIdToken()
        }
        userRepository.deleteAllUserLikedRestaurant()
        fetchData()
    }
}