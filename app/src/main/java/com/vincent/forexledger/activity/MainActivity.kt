package com.vincent.forexledger.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.vincent.forexledger.R
import com.vincent.forexledger.fragment.FragmentProvider
import com.vincent.forexledger.service.AuthService
import com.vincent.forexledger.utils.SimpleCallback
import com.vincent.forexledger.utils.ViewUtils
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_progress_bar.*

class MainActivity : AppCompatActivity() {

    private val disposables = CompositeDisposable()
    private var currentFragment: Fragment? = null

    init {
        val contract = FirebaseAuthUIActivityResultContract()
        val loginFlowLauncher = registerForActivityResult(contract) { result ->
            if (result.resultCode != RESULT_OK) {
                processLoginIncomplete(result)
            }
        }

        val callback = SimpleCallback<Unit, String>(
                { onLoginSuccess() },
                { onLoginFailed(it) }
        )

        AuthService.initialize(loginFlowLauncher, callback)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ViewUtils.setInvisible(layout_universe)

        setupNavigationBar()
    }

    private fun onLoginSuccess() {
        navigationLanding.selectedItemId = R.id.navExchangeRate
        ViewUtils.setVisible(layout_universe)
    }

    private fun onLoginFailed(message: String) {
        AuthService.logout(this) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
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

    private fun logout() {
        AuthService.logout(this) {
            Toast.makeText(this, "Logout successfully", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigate(fragment: Fragment) {
        if (fragment == currentFragment) {
            return
        }

        val transaction = supportFragmentManager.beginTransaction()
        if (currentFragment == null) {
            transaction.add(R.id.layout_navigation_container, fragment)
        } else {
            transaction.hide(currentFragment!!)
            if (fragment.isAdded) {
                transaction.show(fragment)
            } else {
                transaction.add(R.id.layout_navigation_container, fragment)
            }
        }

        transaction.commit()
        currentFragment = fragment;
    }

    private fun setupNavigationBar() {
        navigationLanding.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navExchangeRate -> navigate(FragmentProvider.exchangeRate())
                R.id.navBook -> navigate(FragmentProvider.bookList())
                R.id.navLogout -> logout()
            }
            true
        }

        navigationLanding.menu.forEach {
            navigationLanding
                    .findViewById<View>(it.itemId)
                    .setOnLongClickListener { true }
        }
    }

    override fun onDestroy() {
        disposables.dispose()
        super.onDestroy()
    }
}