package com.yb.part6_chapter01.screen.review.camera

import androidx.lifecycle.viewModelScope
import com.yb.part6_chapter01.screen.base.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class CameraViewModel: BaseViewModel() {

    override fun fetchData(): Job = viewModelScope.launch {

    }
}