package com.vincent.forexledger.utils

import com.vincent.forexledger.network.ResponseEntity
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.Response

object RetrofitUtils {

    fun <T> sendRequest(single: Single<Response<T>>, callback: SimpleCallback<ResponseEntity<T>, String>) {
        var disposable: Disposable? = null
        disposable = single
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<Response<T>>() {
                    override fun onSuccess(response: Response<T>?) {
                        val responseEntity = ResponseEntity(response!!, listOf(disposable))
                        callback.onSuccessListener.invoke(responseEntity)
                    }

                    override fun onError(e: Throwable?) {
                        callback.onFailureListener.invoke(e?.message ?: "")
                    }
                })
    }
}