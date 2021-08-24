package com.vincent.forexledger.service

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.vincent.forexledger.R
import com.vincent.forexledger.model.user.CreateUserRequest
import com.vincent.forexledger.model.user.SocialLoginProvider
import com.vincent.forexledger.network.ResponseEntity
import io.reactivex.rxjava3.disposables.Disposable

object AuthService {

    private lateinit var loginFlowLauncher: ActivityResultLauncher<Intent>

    fun initialize(
            context: Context,
            loginFlowLauncher: ActivityResultLauncher<Intent>,
            onLoginSuccessListener: (ResponseEntity<Unit>) -> Unit) {

        this.loginFlowLauncher = loginFlowLauncher

        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            val user = auth.currentUser
            if (user == null) {
                launchLoginFlow()
            } else {
                onFirebaseAuthenticated(context, user, onLoginSuccessListener)
            }
        }

        FirebaseAuth.getInstance().addAuthStateListener(authStateListener)
    }

    private fun launchLoginFlow() {
        val authProviders = listOf(
            AuthUI.IdpConfig.FacebookBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )
        val intent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(authProviders)
            .setIsSmartLockEnabled(false)
            .setTheme(R.style.LoginPageTheme)
            .setLogo(R.drawable.app_logo)
            .build()

        loginFlowLauncher.launch(intent)
    }

    private fun onFirebaseAuthenticated(
            context: Context,
            user: FirebaseUser,
            onLoginSuccessListener: (ResponseEntity<Unit>) -> Unit) {

        val registerTime = user.metadata!!.creationTimestamp
        val lastLoginTime = user.metadata!!.lastSignInTimestamp
        val isNewRegisteredUser = lastLoginTime - registerTime < 500
        if (isNewRegisteredUser) {
            val createUserReq = CreateUserRequest(
                    user.email!!,
                    user.displayName!!,
                    SocialLoginProvider.fromProviderId(user.providerData)!!,
                    user.uid)
            var disposable: Disposable? = null
            disposable = UserService.createUser(context, createUserReq) {
                val response = ResponseEntity(it, listOf(disposable!!))
                // TODO: if 422 then remove firebase user
                onLoginSuccessListener.invoke(response)
            }
        } else {
            // TODO: obtain access token
            Toast.makeText(context, "obtain access token", Toast.LENGTH_SHORT).show()
        }
    }

    fun logout(context: Context, logoutSuccessListener: () -> Unit) {
        AuthUI.getInstance()
            .signOut(context)
            .addOnSuccessListener { logoutSuccessListener.invoke() }
    }
}