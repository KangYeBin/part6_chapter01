package com.yb.part6_chapter01.screen.order

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.yb.part6_chapter01.databinding.ActivityOrderMenuListBinding
import com.yb.part6_chapter01.screen.base.BaseActivity
import org.koin.android.viewmodel.ext.android.viewModel

class OrderMenuListActivity : BaseActivity<OrderMenuListViewModel, ActivityOrderMenuListBinding>() {
    override val viewModel by viewModel<OrderMenuListViewModel>()

    override fun getViewBinding(): ActivityOrderMenuListBinding =
        ActivityOrderMenuListBinding.inflate(layoutInflater)

    override fun observeData() {

    }
}