package com.vincent.forexledger

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseUser
import com.vincent.forexledger.network.NetworkClient
import com.vincent.forexledger.request.UserRequest
import com.vincent.forexledger.response.UserResponse
import com.vincent.forexledger.service.AuthService
import com.vincent.forexledger.utils.ViewUtils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_progress_bar.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

        ViewUtils.setInvisible(layout_universe)

        btLogout.setOnClickListener {
            AuthService.logout(this) {
                tvEmail.text = null

                ViewUtils.setVisible(progressBar)
                ViewUtils.setInvisible(tvEmail, btLogout)
                Toast.makeText(this, "Logout successfully", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun processLoginComplete(user: FirebaseUser?) {
        tvEmail.text = user?.email
        ViewUtils.setVisible(tvEmail, btLogout, layout_universe)
        ViewUtils.setInvisible(progressBar)

        NetworkClient.userAPI().createUser(UserRequest(""))
                .enqueue(object : Callback<UserResponse> {
                    override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                        Toast.makeText(this@MainActivity, response.code(), Toast.LENGTH_SHORT).show()
                    }

                    override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                        Toast.makeText(this@MainActivity, t.message, Toast.LENGTH_SHORT).show()
                    }
                })
    }

    private fun processLoginIncomplete(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (response == null) {
            // User cancel login flow.
            ViewUtils.setInvisible(progressBar)
            finish()
        } else {
            response.error?.message?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

}