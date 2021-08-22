package com.vincent.forexledger.service

import com.vincent.forexledger.model.user.CreateUserRequest
import com.vincent.forexledger.network.NetworkClient
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import retrofit2.Response

object UserService {

    fun createUser(request: CreateUserRequest): Disposable {
        return NetworkClient.userAPI()
                .createUser(request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<Response<Unit>>() {
                    override fun onSuccess(response: Response<Unit>) {
                        // TODO
                    }

                    override fun onError(e: Throwable) {
                        // TODO
                    }
                })
    }
}