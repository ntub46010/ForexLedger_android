package com.vincent.forexledger.service

import com.vincent.forexledger.model.bank.BankType
import com.vincent.forexledger.model.exchangerate.ExchangeRateVO
import com.vincent.forexledger.network.NetworkClient
import com.vincent.forexledger.network.ResponseEntity
import com.vincent.forexledger.utils.SimpleCallback
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.Response

object ExchangeRateService {

    fun getExchangeRate(
            bank: BankType,
            callback: SimpleCallback<ResponseEntity<List<ExchangeRateVO>>, String?>) {

        val exchangeRateApi = NetworkClient.exchangeRateAPI()
        var disposable: Disposable? = null
        disposable = exchangeRateApi.getExchangeRate(bank)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<Response<List<ExchangeRateVO>>>() {
                    override fun onSuccess(response: Response<List<ExchangeRateVO>>?) {
                        val responseEntity = ResponseEntity(response!!, listOf(disposable))
                        callback.onSuccessListener.invoke(responseEntity)
                    }

                    override fun onError(e: Throwable?) {
                        callback.onFailureListener.invoke(e?.message)
                    }
                })
    }
}