package com.yb.part6_chapter01.screen.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.Job

abstract class BaseActivity<VM : BaseViewModel, VB : ViewBinding> : AppCompatActivity() {

    abstract val viewModel: VM

    protected lateinit var binding: VB

    abstract fun getViewBinding(): VB

    private lateinit var fetchJob: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewBinding()
        setContentView(binding.root)
        initState()
    }

    open fun initState() {
        initViews()
        fetchJob = viewModel.fetchData()
        observeData()
    }

    open fun initViews() {
    }

    open fun observeData() {
    }

    override fun onDestroy() {
        if (fetchJob.isActive) {
            fetchJob.cancel()
        }
        super.onDestroy()
    }
}