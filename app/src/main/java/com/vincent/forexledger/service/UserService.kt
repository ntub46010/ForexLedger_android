package com.vincent.forexledger.service

import android.content.Context
import com.vincent.forexledger.model.user.CreateUserRequest
import com.vincent.forexledger.network.NetworkClient
import com.vincent.forexledger.utils.CallbackUtils
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import retrofit2.Response

object UserService {

    fun createUser(
            context: Context,
            request: CreateUserRequest,
            onSuccessListener: (Response<Unit>) -> Unit): Disposable {
        return NetworkClient.userAPI()
                .createUser(request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<Response<Unit>>() {
                    override fun onSuccess(response: Response<Unit>) = onSuccessListener.invoke(response)
                    override fun onError(e: Throwable) = CallbackUtils.createShowToastCallback(context).invoke(e)
                })
    }
}