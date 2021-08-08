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
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_progress_bar.*
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private val disposables = CompositeDisposable()

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
                // FIXME: extract to another method
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

        val disposable = NetworkClient.userAPI()
                .createUser(UserRequest(""))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<Response<UserResponse>>() {
                    override fun onSuccess(response: Response<UserResponse>) =
                        Toast.makeText(this@MainActivity, "onSuccess", Toast.LENGTH_SHORT).show()

                    override fun onError(e: Throwable) =
                        Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
                })
        disposables.add(disposable)
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