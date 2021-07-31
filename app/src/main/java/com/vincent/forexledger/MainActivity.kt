package com.vincent.forexledger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val loginFlowLauncher: ActivityResultLauncher<Intent>

    init {
        val contract = FirebaseAuthUIActivityResultContract()
        loginFlowLauncher = registerForActivityResult(contract) { result ->
            processLoginResult(result)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        registerAuthStateListener()

        btLogout.setOnClickListener { logout() }
    }

    private fun registerAuthStateListener() {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            val firebaseUser = auth.currentUser
            if (firebaseUser == null) {
                launchLoginFlow()
            } else {
                displayUser(firebaseUser)
            }
        }

        FirebaseAuth.getInstance().addAuthStateListener(listener)
    }

    private fun launchLoginFlow() {
        val authProviders = listOf(
            //AuthUI.IdpConfig.FacebookBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )
        val intent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(authProviders)
            .build()

        loginFlowLauncher.launch(intent)
    }

    private fun processLoginResult(result: FirebaseAuthUIAuthenticationResult) {
        if (result.resultCode == RESULT_OK) {
            val firebaseUser = FirebaseAuth.getInstance().currentUser
            displayUser(firebaseUser)
        } else {
            val response = result.idpResponse
            Toast.makeText(this, response?.error?.errorCode!!, Toast.LENGTH_SHORT).show()
        }
    }

    private fun displayUser(user: FirebaseUser?) {
        tvEmail.text = user?.email
    }

    private fun logout() {
        AuthUI.getInstance()
            .signOut(this)
            .addOnSuccessListener {
                Toast.makeText(this, "Logout successfully", Toast.LENGTH_SHORT).show()
            }
    }
}