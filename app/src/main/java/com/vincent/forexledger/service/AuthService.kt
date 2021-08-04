package com.vincent.forexledger.service

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.vincent.forexledger.R

object AuthService {

    private lateinit var loginFlowLauncher: ActivityResultLauncher<Intent>

    fun initialize(
            loginFlowLauncher: ActivityResultLauncher<Intent>,
            firebaseAuthSuccessListener: (user: FirebaseUser?) -> Unit) {

        this.loginFlowLauncher = loginFlowLauncher

        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            val user = auth.currentUser
            if (user == null) {
                launchLoginFlow()
            } else {
                firebaseAuthSuccessListener.invoke(user)
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

    fun logout(context: Context, logoutSuccessListener: () -> Unit) {
        AuthUI.getInstance()
            .signOut(context)
            .addOnSuccessListener { logoutSuccessListener.invoke() }
    }
}