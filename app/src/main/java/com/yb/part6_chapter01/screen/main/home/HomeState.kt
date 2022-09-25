package com.yb.part6_chapter01.screen.main.home

sealed class HomeState {

    object Uninitialized : HomeState()

    object Loading : HomeState()

    object Success : HomeState()
}
