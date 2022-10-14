package com.yb.part6_chapter01.screen.main.my

import android.app.Activity
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.yb.part6_chapter01.R
import com.yb.part6_chapter01.databinding.FragmentMyBinding
import com.yb.part6_chapter01.extensions.load
import com.yb.part6_chapter01.extensions.toGone
import com.yb.part6_chapter01.extensions.toVisible
import com.yb.part6_chapter01.screen.base.BaseFragment
import org.koin.android.viewmodel.ext.android.viewModel

class MyFragment : BaseFragment<MyViewModel, FragmentMyBinding>() {
    override val viewModel by viewModel<MyViewModel>()

    override fun getViewBinding(): FragmentMyBinding = FragmentMyBinding.inflate(layoutInflater)

    private val gso: GoogleSignInOptions by lazy {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
    }

    private val gsc by lazy { GoogleSignIn.getClient(requireActivity(), gso) }

    private val firebaseAuth by lazy { FirebaseAuth.getInstance() }

    private val loginLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    task.getResult(ApiException::class.java)?.let { account ->
                        viewModel.saveToken(account.idToken ?: throw Exception())
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

    override fun initViews() = with(binding) {
        loginButton.setOnClickListener {
            signInGoogle()
        }
        logoutButton.setOnClickListener {
            firebaseAuth.signOut()
            viewModel.signOut()
        }
    }

    private fun signInGoogle() {
        val signInIntent = gsc.signInIntent
        loginLauncher.launch(signInIntent)
    }

    override fun observeData() = viewModel.myStateLiveData.observe(viewLifecycleOwner) { state ->
        when (state) {
            is MyState.Loading -> handleLoading()
            is MyState.Login -> handleLogin(state)
            is MyState.Success -> handleSuccess(state)
            is MyState.Error -> handleError(state)
            else -> Unit
        }
    }

    private fun handleLoading() = with(binding) {
        loginRequiredGroup.toGone()
        progressBar.toVisible()
    }

    private fun handleLogin(state: MyState.Login) = with(binding) {
        progressBar.toVisible()
        val credential = GoogleAuthProvider.getCredential(state.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    viewModel.setUserInfo(user)
                } else {
                    firebaseAuth.signOut()
                    viewModel.setUserInfo(null)
                }
            }
    }

    private fun handleSuccess(state: MyState.Success) = with(binding) {
        progressBar.toGone()
        when (state) {
            is MyState.Success.Registered -> {
                handleRegistered(state)
            }
            is MyState.Success.NotRegistered -> {
                profileGroup.toGone()
                loginRequiredGroup.toVisible()
            }
        }
    }

    private fun handleRegistered(state: MyState.Success.Registered) = with(binding) {
        profileGroup.toVisible()
        loginRequiredGroup.toGone()
        profileImageView.load(state.profileUri.toString(), 60f)
        userNameTextView.text = state.userName
    }

    private fun handleError(state: MyState.Error) {

    }

    companion object {
        fun newInstance() = MyFragment()
        const val TAG = "MyFragment"
    }
}