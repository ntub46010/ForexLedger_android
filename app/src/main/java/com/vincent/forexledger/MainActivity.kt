package com.vincent.forexledger

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.vincent.forexledger.service.AuthService
import com.vincent.forexledger.utils.SimpleCallback
import com.vincent.forexledger.utils.ViewUtils
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_progress_bar.*

class MainActivity : AppCompatActivity() {

    private val disposables = CompositeDisposable()

    init {
        val contract = FirebaseAuthUIActivityResultContract()
        val loginFlowLauncher = registerForActivityResult(contract) { result ->
            if (result.resultCode != RESULT_OK) {
                processLoginIncomplete(result)
            }
        }

        val callback = SimpleCallback<String, String>(
                { onLoginSuccess(it) },
                { onLoginFailed(it) }
        )

        AuthService.initialize(loginFlowLauncher, callback)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ViewUtils.setInvisible(layout_universe)

        btLogout.setOnClickListener {
            AuthService.logout(this) {
                // FIXME: extract to another method
                tvMsg.text = null

                ViewUtils.setVisible(progressBar)
                ViewUtils.setInvisible(tvMsg, btLogout)
                Toast.makeText(this, "Logout successfully", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onLoginSuccess(message: String) {
        tvMsg.text = message
        ViewUtils.setVisible(tvMsg, btLogout, layout_universe)
        ViewUtils.setInvisible(progressBar)
    }

    private fun onLoginFailed(message: String) {
        tvMsg.text = message
        ViewUtils.setVisible(tvMsg, btLogout, layout_universe)
        ViewUtils.setInvisible(progressBar)
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

    override fun onDestroy() {
        disposables.dispose()
        super.onDestroy()
    }
}