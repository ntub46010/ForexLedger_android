package com.vincent.forexledger.service

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.vincent.forexledger.R
import com.vincent.forexledger.model.user.CreateUserRequest
import com.vincent.forexledger.model.user.SocialLoginProvider
import com.vincent.forexledger.utils.SimpleCallback
import io.reactivex.rxjava3.disposables.Disposable

object AuthService {

    private lateinit var loginFlowLauncher: ActivityResultLauncher<Intent>
    private const val FIREBASE_CREATE_USER_BUFFER_TIME = 500

    fun initialize(
            loginFlowLauncher: ActivityResultLauncher<Intent>,
            callback: SimpleCallback<String, String>) {

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
            callback: SimpleCallback<String, String>) {

        if (isNotNewRegisteredUser(user)) {
            callback.onSuccessListener.invoke("Ready to obtain access token from server.")
            return
        }

        // TODO: Remember to dispose them
        val disposables: MutableList<Disposable> = mutableListOf()

        val createUserReq = genCreateUserRequest(user)
        UserService.createUser(createUserReq) {
            // when email address is registered on server
            if (it.code() == 422) {
                user.delete()
                callback.onFailureListener.invoke("Try to create user in server but email is registered.")
                return@createUser
            }

            callback.onSuccessListener.invoke("Create user in server successfully.")
        }.also {
            disposables.add(it)
        }
    }

    fun logout(context: Context, logoutSuccessListener: () -> Unit) {
        AuthUI.getInstance()
            .signOut(context)
            .addOnSuccessListener { logoutSuccessListener.invoke() }
    }

    private fun isNotNewRegisteredUser(user: FirebaseUser): Boolean {
        val creationTime = user.metadata!!.creationTimestamp
        val lastSignInTime = user.metadata!!.lastSignInTimestamp

        return lastSignInTime - creationTime > FIREBASE_CREATE_USER_BUFFER_TIME
                || System.currentTimeMillis() - creationTime > FIREBASE_CREATE_USER_BUFFER_TIME
    }

    private fun genCreateUserRequest(user: FirebaseUser): CreateUserRequest {
        return CreateUserRequest(
                user.email!!,
                user.displayName!!,
                SocialLoginProvider.fromProviderId(user.providerData)!!,
                user.uid)
    }
}