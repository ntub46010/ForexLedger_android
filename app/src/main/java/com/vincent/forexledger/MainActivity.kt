package com.vincent.forexledger

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseUser
import com.vincent.forexledger.service.AuthService
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    init {
        val contract = FirebaseAuthUIActivityResultContract()
        val loginFlowLauncher = registerForActivityResult(contract) { result ->
            if (result.resultCode != RESULT_OK) {
                processLoginIncomplete(result)
            }
        }

        AuthService.initialize(loginFlowLauncher) {
            displayUser(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btLogout.setOnClickListener {
            AuthService.logout(this) {
                tvEmail.text = "N/A"
                Toast.makeText(this, "Logout successfully", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun processLoginIncomplete(result: FirebaseAuthUIAuthenticationResult) {
        result.idpResponse?.error?.message?.let {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
    }

    private fun displayUser(user: FirebaseUser?) {
        tvEmail.text = user?.email
    }
}