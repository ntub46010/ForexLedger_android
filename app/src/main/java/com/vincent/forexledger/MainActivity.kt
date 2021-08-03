package com.vincent.forexledger

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseUser
import com.vincent.forexledger.service.AuthService
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_progress_bar.*

class MainActivity : AppCompatActivity() {

    init {
        val contract = FirebaseAuthUIActivityResultContract()
        val loginFlowLauncher = registerForActivityResult(contract) { result ->
            if (result.resultCode != RESULT_OK) {
                processLoginIncomplete(result)
            }
        }

        AuthService.initialize(loginFlowLauncher) { processLoginComplete(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btLogout.setOnClickListener {
            AuthService.logout(this) {
                tvEmail.text = null
                tvEmail.visibility = View.INVISIBLE
                btLogout.visibility = View.INVISIBLE
                progressBar.visibility = View.VISIBLE
                Toast.makeText(this, "Logout successfully", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun processLoginComplete(user: FirebaseUser?) {
        tvEmail.text = user?.email
        progressBar.visibility = View.INVISIBLE
        tvEmail.visibility = View.VISIBLE
        btLogout.visibility = View.VISIBLE
    }

    private fun processLoginIncomplete(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (response == null) {
            // User cancel login flow.
            progressBar.visibility = View.INVISIBLE
            finish()
        } else {
            response.error?.message?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

}