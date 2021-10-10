package com.example.airtelmanewakala.viewmodel

import android.content.Intent
import android.icu.text.NumberFormat
import android.os.Build
import android.telephony.SmsManager
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.lifecycle.*
import com.example.airtelmanewakala.*
import com.example.airtelmanewakala.db.Balance
import com.example.airtelmanewakala.db.FloatIn
import com.example.airtelmanewakala.db.MobileRepository
import com.example.airtelmanewakala.db.MoblieDatabase
import com.example.airtelmanewakala.network.RetroInstance
import com.romellfudi.ussdlibrary.SplashLoadingService
import com.romellfudi.ussdlibrary.USSDController
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class FloatInViewModel(private val repository: MobileRepository) : ViewModel(), Observable {


    @Bindable
    val allButton = MutableLiveData<String>()
    val zeroButton = MutableLiveData<String>()
    val oneButton = MutableLiveData<String>()
    val twoButton = MutableLiveData<String>()
    val threeButton = MutableLiveData<String>()
    val fourButton = MutableLiveData<String>()
    val fiveButton = MutableLiveData<String>()
    val uploadButton = MutableLiveData<String>()

    init {
        allButton.value = "All"
    }

    init {
        zeroButton.value = "Pending"
    }

    init {
        oneButton.value = "Done"
    }

    init {
        twoButton.value = "Large"
    }

    init {
        threeButton.value = "Invalid"
    }

    init {
        fourButton.value = "Late"
    }

    init {
        fiveButton.value = "Change"
    }

    init {
        uploadButton.value = "UPL"
    }

    @Bindable
    val getButtonText = MutableLiveData<String>()

    init {
        getButtonText.value = "FETCH FLOATIN"
    }

    val modifiedAt = System.currentTimeMillis()


    fun floatInFilter(status: Int) = liveData {
        repository.floatInFilter(status).collect {
            emit(it)
        }
    }

    fun floatIn() = liveData {
        repository.floatIn.collect {
            emit(it)
        }
    }
//    8IE95V3NYE9 Imethibitishwa, tarehe 14/9/21  saa 9:42 AM chukua Tsh5,000.00 kutoka 988830 - EXTRATIME ENTERPRISES STORE 6.Salio lako la M-Pesa ni Tsh10,000.00.

    @RequiresApi(Build.VERSION_CODES.O)
    fun floatInUpdate(floatIn: FloatIn): Job =
        viewModelScope.launch {
            if (checkFloatIn(floatIn.networksms)) {

                //GET VARIBLES
                val (amount, name, balance, transid,code) = getFloatIn(floatIn.networksms)

                //CHECK IF TRANSACTION EXISTS
                val searchFloatInNotDuplicate = repository.searchFloatInNotDuplicate(transid)
                if (searchFloatInNotDuplicate) {

                    //BALANCE FUNTION
                    checkbalancefunction(
                        balance,
                        amount,
                        name,
                        1,
                        floatIn.createdat,
                        floatIn.madeatfloat
                    )

                    //CHECK IF WAKALA EXISTS
                    val searchWakala = repository.searchWakala(name,code)

                    if (searchWakala != null) {

                        val wakalaKeyId = searchWakala.wakalaid
                        val wakalacontact = searchWakala.contact
                        val fromwakalaname = searchWakala.airtelname
                        val fromwakalacode = searchWakala.airtelmoney

                        val currentamount = amount.toInt()

                        val maxamount = searchWakala.maxamount
                        val maxAmount = maxamount.toInt()
                        if (isTodayDate(floatIn.madeatfloat)) {
                            //CHECK IF FLOAT IN AMOUNT LESS OR EQUAL TO MAX AMOUNT
                            if (currentamount <= maxAmount) {

                                //INSERT FLOATIN STATUS 0( WAITING ORDER)

                                uFloatInChange(
                                    floatIn.floatinid,
                                    transid,
                                    amount,
                                    maxamount,
                                    balance,
                                    wakalaKeyId,
                                    0,
                                    "",
                                    "WAITING ORDER",
                                    fromwakalacode,
                                    "",
                                    "",
                                    fromwakalaname,
                                    "",
                                    wakalacontact,
                                    modifiedAt,
                                )

                                val amounting = getComma(amount)

                                var smsText =
                                    "Kiasi: Tsh $amounting, Mtandao: $fromnetwork itumwe wapi? Jibu Tigo, Vodacom au Halotel."
                                sendSms(wakalacontact, smsText)

                            } else {

                                uFloatInChange(
                                    floatIn.floatinid,
                                    transid,
                                    amount,
                                    maxamount,
                                    balance,
                                    wakalaKeyId,
                                    2,
                                    "",
                                    "LARGE/WAIT",
                                    fromwakalacode,
                                    "",
                                    "",
                                    fromwakalaname,
                                    "",
                                    wakalacontact,
                                    modifiedAt,
                                )

                            }
                        } else {

                            //INSERT FLOATIN STATUS 4(LATE ORDER)....SMS IMECHELIWA BY 300000 seconds/5 min

                            uFloatInChange(
                                floatIn.floatinid,
                                transid,
                                amount,
                                maxamount,
                                balance,
                                wakalaKeyId,
                                4,
                                "",
                                "LATE ORDER",
                                fromwakalacode,
                                "",
                                "",
                                fromwakalaname,
                                "",
                                wakalacontact,
                                modifiedAt,
                            )

                        }
                    } else {
                        //INSERT FLOATIN STATUS 3(UNKWOWN WAKALA)..SIO WAKALA WETU

                        uFloatInChange(
                            floatIn.floatinid,
                            transid,
                            amount,
                            "",
                            balance,
                            "",
                            3,
                            "",
                            "UNKNOWN WAKALA",
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            modifiedAt,
                        )

                        //SEND ERROR TEXT TO ERROR NUMBER IF FLOATIN UNKNOWN WAKALA INSERTED
                        var smsText =
                            "$fromnetwork ERROR = UNKWOWN WAKALA: ${floatIn.networksms}"

                        sendSms(errornumber, smsText)

                    }
                } else {
                    //UPDATE FLOAT IN TRANSACTION ALREADY EXISTS
                    uFloatInChange(
                        floatIn.floatinid,
                        transid,
                        amount,
                        "",
                        balance,
                        "",
                        3,
                        "",
                        "DUPLICATE SMS",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        modifiedAt,
                    )
                }
            } else {
                val float = floatinchange.toString()
                uFloatInChange(
                    floatIn.floatinid,
                    "",
                    "",
                    "",
                    "",
                    "",
                    5,
                    "",
                    "CHANGES IN $float",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    modifiedAt,
                )

                val smsText =
                    "$fromnetwork ERROR = CHANGES: ${floatIn.networksms} -Changes in $float"
                sendSms(errornumber, smsText)
                floatinchange.clear()
            }
        }


    private suspend fun uFloatInChange(
        floatinid: Int,
        transid: String,
        amount: String,
        maxamount: String,
        balance: String,
        wakalaidkey: String,
        status: Int,
        wakalaorder: String,
        comment: String,
        fromwakalacode: String,
        towakalacode: String,
        wakalamkuunumber: String,
        fromwakalaname: String,
        towakalaname: String,
        wakalacontact: String,
        modifiedAt: Long,
    ) {
        repository.updateFloatInChange(
            floatinid,
            transid,
            amount,
            maxamount,
            balance,
            wakalaidkey,
            status,
            fromnetwork,
            wakalaorder,
            comment,
            fromwakalacode,
            towakalacode,
            wakalamkuunumber,
            fromwakalaname,
            towakalaname,
            wakalacontact,
            modifiedAt,
        )
    }

    fun uFloatInLarge(
        floatinid: Int,
        comment: String,
        modifiedat: Long
    ): Job =
        viewModelScope.launch  {
            repository.updateFloatInLarge(
                floatinid,
                comment,
                modifiedat,
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

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }
}