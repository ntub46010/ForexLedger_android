package com.vincent.forexledger

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseUser
import com.vincent.forexledger.model.user.CreateUserRequest
import com.vincent.forexledger.model.user.SocialLoginProvider
import com.vincent.forexledger.service.AuthService
import com.vincent.forexledger.service.UserService
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

        // TODO: maybe can extract callback to service
        AuthService.initialize(loginFlowLauncher) { processLoginComplete(it!!) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ViewUtils.setInvisible(layout_universe)

        btLogout.setOnClickListener {
            AuthService.logout(this) {
                // FIXME: extract to another method
                tvEmail.text = null

                ViewUtils.setVisible(progressBar)
                ViewUtils.setInvisible(tvEmail, btLogout)
                Toast.makeText(this, "Logout successfully", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun processLoginComplete(user: FirebaseUser) {
        val registerTime = user.metadata!!.creationTimestamp
        val lastLoginTime = user.metadata!!.lastSignInTimestamp

        if (lastLoginTime - registerTime < 500) {
            val createUserReq = CreateUserRequest(
                    user.email!!,
                    user.displayName!!,
                    SocialLoginProvider.fromProviderId(user.providerData)!!,
                    user.uid)
            val disposable = UserService.createUser(createUserReq) // TODO: implement callback
            disposables.add(disposable)
        } else {
            // TODO: obtain token
        }

        tvEmail.text = user.email
        ViewUtils.setVisible(tvEmail, btLogout, layout_universe)
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