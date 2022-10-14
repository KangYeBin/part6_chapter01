package com.yb.part6_chapter01.screen.main.my

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.yb.part6_chapter01.data.preference.PreferenceManager
import com.yb.part6_chapter01.screen.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyViewModel(
    private val preferenceManager: PreferenceManager,
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
            myStateLiveData.value = MyState.Success.Registered(
                userName = user.displayName ?: "익명",
                profileUri = user.photoUrl)
        } ?: kotlin.run {
            myStateLiveData.value = MyState.Success.NotRegistered
        }
    }

    fun signOut() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            preferenceManager.removeIdToken()
        }
        fetchData()
    }
}