package com.vincent.forexledger.service

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.vincent.forexledger.R
import com.vincent.forexledger.RuntimeCache
import com.vincent.forexledger.utils.SimpleCallback

object AuthService {

    private lateinit var loginFlowLauncher: ActivityResultLauncher<Intent>

    fun initialize(
            loginFlowLauncher: ActivityResultLauncher<Intent>,
            callback: SimpleCallback<Unit, String>) {

        this.loginFlowLauncher = loginFlowLauncher

        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            val user = auth.currentUser
            if (user == null) {
                launchLoginFlow()
            } else {
                onFirebaseAuthenticated(user, callback)
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
            user: FirebaseUser,
            callback: SimpleCallback<Unit, String>) {

        user.getIdToken(true)
                .addOnSuccessListener { tokenResult ->
                    RuntimeCache.accessToken = tokenResult.token!!
                    callback.onSuccessListener.invoke(Unit)
                }
                .addOnFailureListener {
                    callback.onFailureListener.invoke(it.message ?: "Failed to get Firebase id token, please try to login again.")
                }
    }

    fun logout(context: Context, logoutSuccessListener: () -> Unit) {
        AuthUI.getInstance()
            .signOut(context)
            .addOnSuccessListener {
                RuntimeCache.accessToken = ""
                logoutSuccessListener.invoke()
            }
    }

}