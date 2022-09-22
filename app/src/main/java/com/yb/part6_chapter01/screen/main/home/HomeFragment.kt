package com.yb.part6_chapter01.screen.main.home

import com.yb.part6_chapter01.databinding.FragmentHomeBinding
import com.yb.part6_chapter01.screen.base.BaseFragment
import org.koin.android.viewmodel.ext.android.viewModel

class HomeFragment : BaseFragment<HomeViewModel, FragmentHomeBinding>() {

    override val viewModel by viewModel<HomeViewModel>()

    override fun getViewBinding(): FragmentHomeBinding = FragmentHomeBinding.inflate(layoutInflater)

    override fun observeData() {
    }

    companion object {
        fun newInstance() = HomeFragment()
        const val TAG = "HomeFragment"
    }
}