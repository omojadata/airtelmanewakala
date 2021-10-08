package com.example.airtelmanewakala.viewmodel

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.*
import com.example.airtelmanewakala.db.Balance
import com.example.airtelmanewakala.db.MobileRepository
import com.example.airtelmanewakala.network.RetroInstance
import com.romellfudi.ussdlibrary.USSDController.context
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BalanceViewModel (private val repository: MobileRepository): ViewModel(), Observable {
//    val balance= repository.balance

    fun balance() = liveData {
        repository.balance.collect {
            emit(it)
        }
    }
    fun insertBalance(balance: Balance): Job =
        viewModelScope.launch {
            repository.insertBalance(balance)
        }
//
//    fun insertBalance(balance: List<Balance>): Job =
//        viewModelScope.launch {
//            repository.insertTestBalance(balance)
//        }
    @Bindable
    val getButtonText =MutableLiveData<String>()

    init {
        getButtonText.value= "FETCH BALANCE"
    }
    fun copyText(text:String){
        val myClipboard = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val myClip: ClipData = ClipData.newPlainText("Label", text)
        myClipboard.setPrimaryClip(myClip)
    }
    fun onGetButton() {

        val myClipboard = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val myClip: ClipData = ClipData.newPlainText("Label","text2323")
        myClipboard.setPrimaryClip(myClip)

//
//        val service= RetroInstance.getRetroInstance()
//        val retrofitData = service?.getDataBalance()

//        if (retrofitData != null) {
//            retrofitData.enqueue(object : Callback<List<Balance>> {
//                override fun onResponse(
//                    call: Call<List<Balance>>,
//                    response: Response<List<Balance>>
//                ) {
//                    val responseBody =response.body()
//                    if (responseBody != null) {
//                        insertBalance(responseBody)
//                    }
//                    Log.d("WAKALAmy", "Balance")
//                }
//
//                override fun onFailure(call: Call<List<Balance>>, t: Throwable) {
//
//                }
//            })
//        }
//        else{
//            Log.d("WAKALA", "omNull")
//        }
    }
    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }
}