package com.yb.part6_chapter01.screen.main.my

import android.net.Uri
import androidx.annotation.StringRes
import com.yb.part6_chapter01.data.entity.OrderEntity
import com.yb.part6_chapter01.model.restaurant.order.OrderModel

sealed class MyState {

    object UnInitialized : MyState()

    object Loading : MyState()

    data class Login(
        val idToken: String,
    ) : MyState()

    sealed class Success : MyState() {
        data class Registered(
            val userName: String,
            val profileUri: Uri?,
            val orderList: List<OrderModel>
        ) : Success()

        object NotRegistered : Success()
    }

    data class Error(
        @StringRes val messageId: Int,
        val e: Throwable,
    ) : MyState()
}
