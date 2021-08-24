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

        if (!isNewRegisteredUser(user)) {
            // TODO: obtain token
            Toast.makeText(context, "obtain access token", Toast.LENGTH_SHORT).show()
            return
        }

        val disposables: MutableList<Disposable> = mutableListOf()
        val createUserReq = genCreateUserRequest(user)
        UserService.createUser(context, createUserReq) {
            // when email address is registered on server
            if (it.code() == 422) {
                user.delete()
                // FIXME: actually not success
                onLoginSuccessListener.invoke(ResponseEntity(it, disposables))
                return@createUser
            }

            // TODO: obtain token then wrap to response entity
            Toast.makeText(context, "obtain access token", Toast.LENGTH_SHORT).show()
        }.also {
            disposables.add(it)
        }
    }

    fun logout(context: Context, logoutSuccessListener: () -> Unit) {
        AuthUI.getInstance()
            .signOut(context)
            .addOnSuccessListener { logoutSuccessListener.invoke() }
    }

    private fun isNewRegisteredUser(user: FirebaseUser): Boolean {
        val registerTime = user.metadata!!.creationTimestamp
        val lastLoginTime = user.metadata!!.lastSignInTimestamp

        return lastLoginTime - registerTime < 500
    }

    private fun genCreateUserRequest(user: FirebaseUser): CreateUserRequest {
        return CreateUserRequest(
                user.email!!,
                user.displayName!!,
                SocialLoginProvider.fromProviderId(user.providerData)!!,
                user.uid)
    }
}