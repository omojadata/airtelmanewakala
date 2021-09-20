package com.example.airtelmanewakala.viewmodel

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.telephony.SmsManager
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.view.children
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.airtelmanewakala.*
import com.example.airtelmanewakala.db.*
import com.google.android.material.chip.Chip
import com.romellfudi.ussdlibrary.USSDController
import com.romellfudi.ussdlibrary.USSDController.context
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*


class FloatOutViewModel(private val repository: MobileRepository) : ViewModel(), Observable {

    @Bindable
    val allButton = MutableLiveData<String>()
    val zeroButton = MutableLiveData<String>()
    val oneButton = MutableLiveData<String>()
    val twoButton = MutableLiveData<String>()
    val threeButton = MutableLiveData<String>()
    val fourButton = MutableLiveData<String>()
    val uploadButton = MutableLiveData<String>()

    init {
        allButton.value = "A"
    }

    init {
        zeroButton.value = "Pending"
    }

    init {
        oneButton.value = "Ussd"
    }

    init {
        twoButton.value = "Done"
    }

    init {
        threeButton.value = "Invalid"
    }

    init {
        fourButton.value = "Change"
    }

    init {
        uploadButton.value = "UP"
    }

    val modifiedAt = System.currentTimeMillis()

    @Bindable
    val getButtonText = MutableLiveData<String>()

    init {
        getButtonText.value = "FETCH FLOATOUT"
    }


    fun floatOutFilter(status: Int) = liveData {
        repository.floatOutFilter(status).collect {
            emit(it)
        }
    }

    fun floatOut() = liveData {
        repository.floatOut.collect {
            emit(it)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun floatOutUpdate(floatOut: FloatOut): Job =
        viewModelScope.launch {

            if (checkFloatOut(floatOut.networksms)) {

                //GET VARIBLES
                val (amount, name, balance, transid) = getFloatOut(floatOut.networksms)

                //CHECK IF TRANSACTION(transactionid) EXISTS
                val searchFloatOutNotDuplicate = repository.searchFloatOutNotDuplicate(transid)
                if (searchFloatOutNotDuplicate) {

                    //BALANCE FUNTION
                    checkbalancefunction(
                        balance,
                        amount,
                        name,
                        2,
                        floatOut.createdat,
                        floatOut.madeatfloat
                    )

                    //CHECK IF WAKALA EXISTS
                    val searchWakala = repository.searchWakala(name)
                    if (searchWakala != null) {

                        // CHECK IF FLOATOUT WAKALA ORDER EXISTS (floatinid(sent bu wakala mkuu order)) EXISTS
                        val searchFloatOutWakalaOrder = repository.searchFloatOutWakalaOrder(name)
                        if (searchFloatOutWakalaOrder) {

                            //UPDATE FLOATOUT STATUS 2(DONE)

                            var uflaut = uFloatOut(
                                2,
                                amount,
                                name,
                                transid,
                                "DONE",
                                floatOut.networksms,
                                modifiedAt,
                                floatOut.madeatfloat
                            )

                            Log.e("santa45", "$uflaut.toString()")
                            if (uflaut == 1) {
                                dFloatOut(
                                    modifiedAt,
                                    floatOut.floatoutid
                                )
                            }

                        } else {
                            launch {
                                //INSERT FLOATOUT STATUS 3(UNKWOWN ORDER)
                                uFloatOutChange(
                                    floatOut.floatoutid,
                                    transid,
                                    amount,
                                    name,
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    3,
                                    "UNKNOWN ORDER",
                                    "",
                                    modifiedAt
                                )

                            }
                        }
                    } else {
                        launch {

                            //INSERT FLOATOUT STATUS 4(UNKWOWN WAKALA)
                            uFloatOutChange(
                                floatOut.floatoutid,
                                transid,
                                amount,
                                name,
                                "",
                                "",
                                "",
                                "",
                                "",
                                "",
                                3,
                                "UNKWOWN WAKALA",
                                "",
                                modifiedAt
                            )

                        }
                    }
                } else {
                    launch {
                        uFloatOutChange(
                            floatOut.floatoutid,
                            transid,
                            amount,
                            name,
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            3,
                            "DUPLICATE SMS",
                            "",
                            modifiedAt
                        )
                    }
                }
            } else {
                val float = floatoutchange.toString()
                launch {

                    uFloatOutChange(
                        floatOut.floatoutid,
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        4,
                        "CHANGES IN $float",
                        "",
                        modifiedAt
                    )

                    val smsText =
                        "$fromnetwork ERROR = CHANGES: ${floatOut.networksms} -Changes in $float"
                    sendSms(errornumber, smsText)

                    floatoutchange.clear()

                }
            }
        }

    private suspend fun dFloatOut(
        modifiedat: Long,
        floatoutid: Int
    ) {
        repository.deleteFloatOutChange(
            modifiedat,
            floatoutid
        )
    }

    private suspend fun uFloatOut(
        status: Int,
        amount: String,
        name: String,
        transid: String,
        comment: String,
        networksms: String,
        modifiedat: Long,
        madeatfloat: Long
    ): Int {
        return repository.updateFloatOut(
            status,
            amount,
            name,
            transid,
            comment,
            networksms,
            modifiedat,
            madeatfloat
        )
    }

    private suspend fun uFloatOutChange(
        floatoutid: Int,
        transid: String,
        amount: String,
        wakalaname: String,
        wakalacode: String,
        network: String,
        wakalaidkey: String,
        wakalamkuu: String,
        fromfloatinid: String,
        fromtransid: String,
        status: Int,
        comment: String,
        wakalanumber: String,
        modifiedAt: Long,
    ) {
        repository.updateFloatOutChange(
            floatoutid,
            transid,
            amount,
            wakalaname,
            wakalacode,
            network,
            wakalaidkey,
            wakalamkuu,
            fromfloatinid,
            fromtransid,
            status,
            comment,
            wakalanumber,
            modifiedAt,
        )
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private suspend fun checkbalancefunction(
        balance: String,
        amount: String,
        name: String,
        status: Int,
        createdAt: Long,
        madeAt: Long
    ) {
        //INSERT BALANCE
        repository.insertBalance(
            Balance(
                0,
                balance,
                amount,
                name,
                status,
                createdAt,
                madeAt
            )
        )
        //CHECK BALANCE
        val balancecheck = repository?.getBalance();
        if (balancecheck > 100000) {
            val smsText = "$fromnetwork SALIO = : CHINI CHA ${getComma("100000")}"
            sendSms(errornumber, smsText)
        }

    }


    @RequiresApi(Build.VERSION_CODES.N)
    fun doUssd(floatOut: FloatOut) : Job =
        viewModelScope.launch {
            var balanci = repository?.getBalance();
            if (balanci != null) {
                if (balanci >= floatOut.amount.toInt()) {
                    dialUssd(
                        "*150*00#",
                        floatOut.wakalacode,
                        floatOut.wakalaname,
                        floatOut.amount,
                        modifiedAt,
                        floatOut.fromfloatinid,
                        floatOut.fromtransid,
                        repository,
                        context,
                        viewModelScope
                    )
                } else {
                    val smsText = "$fromnetwork SALIO = : CHINI CHA ${getComma("100000")}"
                    sendSms(errornumber, smsText)
                }
            }
        }


    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }
}

