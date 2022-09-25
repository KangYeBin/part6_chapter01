package com.yb.part6_chapter01.screen.main.home

import androidx.lifecycle.MutableLiveData
import com.yb.part6_chapter01.screen.base.BaseViewModel

class HomeViewModel : BaseViewModel() {

    val homeStateLiveData = MutableLiveData<HomeState>(HomeState.Uninitialized)

}