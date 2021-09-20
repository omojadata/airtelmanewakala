package com.example.airtelmanewakala

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Intent
import android.icu.text.NumberFormat
import android.os.Build
import android.os.IBinder
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.airtelmanewakala.db.*
import com.example.airtelmanewakala.viewmodel.FloatInViewModel
import com.romellfudi.ussdlibrary.SplashLoadingService
import com.romellfudi.ussdlibrary.USSDController
import com.romellfudi.ussdlibrary.USSDController.context
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import java.util.*
import kotlin.collections.HashMap


class ForegroundSmsService : Service() {
    lateinit var smsreceiver: SmsBroadcastReceiver
    lateinit var receiver: BroadcastReceiver
    private val scope = CoroutineScope(Dispatchers.Main)
    private val TAG = "MyService"

    private val CHANNELID = "ForegroundService Kotlin"
//    private lateinit var floatInViewModel: FloatInViewModel
//    val dao = MoblieDatabase.getInstance(application).MobileDAO
//    val repository = MobileRepository(dao)

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNELID, "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager!!.createNotificationChannel(serviceChannel)
        }
    }


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, 0
        )
        val notification = NotificationCompat.Builder(this, CHANNELID)
            .setContentTitle("MANE WAKALA IS ACTIVE")
            .setContentText("AIRTEL")
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .build()
        startForeground(1, notification)

        val smsAddress = intent?.getStringExtra("smsadress").toString()
        val smsbody = intent?.getStringExtra("smsbody").toString().replace("\\s+".toRegex(), " ")
        val smsTime = intent?.getStringExtra("smstime").toString()

        val firstword = filterBody(smsbody, 1)
        val lastword = smsbody.substring(smsbody.lastIndexOf(" ") + 1)

        val createdAt = System.currentTimeMillis()
        val modifiedAt = System.currentTimeMillis()
        val madeAt = smsTime.toLong()

        //do heavy work on a background thread
        scope.launch {

            val dao = MoblieDatabase.getInstance(application).MobileDAO
            val repository = MobileRepository(dao)
            val dataStorePreference = DataStorePreference(application)

            //CHECK IF SMS IF FROM mpesa
            if (smsAddress == mtandao) {

                //CHECK IF FIRST WORD IS "UMEPOKEA"
                if (checkFloatInWords(smsbody)) {

                    if (checkFloatIn(smsbody)) {

                        //GET VARIBLES
                        val (amount, name, balance, transid) = getFloatIn(smsbody)

                        //CHECK IF TRANSACTION EXISTS
                        val searchFloatInNotDuplicate =
                            repository.searchFloatInNotDuplicate(transid)
                        if (searchFloatInNotDuplicate) {

                            //BALANCE FUNTION
                            checkbalancefunction(
                                balance,
                                amount,
                                name,
                                1,
                                createdAt,
                                madeAt,
                                repository
                            )

                            //CHECK IF WAKALA EXISTS
                            val searchWakala = repository.searchWakala(name)

                            if (searchWakala != null) {

                                val timeDiff = createdAt - madeAt

                                val wakalaKeyId = searchWakala.wakalaid
                                val wakalacontact = searchWakala.contact
                                val fromwakalaname = searchWakala.vodaname
                                val fromwakalacode = searchWakala.mpesa
                                val maxamount = searchWakala.maxamount

                                val currentamount = amount.toInt()
                                val maxAmount = searchWakala.maxamount.toInt()

                                if (isTodayDate(madeAt)) {

                                    //CHECK IF FLOAT IN AMOUNT LESS OR EQUAL TO MAX AMOUNT
                                    if (currentamount <= maxAmount) {

                                        //INSERT FLOATIN STATUS 0( WAITING ORDER)

                                        iFloatIn(
                                            transid,
                                            amount,
                                            maxamount,
                                            balance,
                                            wakalaKeyId,
                                            0,
                                            "WAITING ORDER",
                                            fromwakalacode,
                                            fromwakalaname,
                                            wakalacontact,
                                            smsbody,
                                            createdAt,
                                            modifiedAt,
                                            madeAt,
                                            0,
                                            repository
                                        )

                                        sendBroadcast(Intent().setAction("floatInReceiver"))

                                        val amounting = getComma(amount)
                                        val timeM = getTime(madeAt)
                                        val timeC = getTime(createdAt)
                                        var smsText =
                                            "Muamala No: ${transid}, Kiasi: Tsh $amounting, Muda uliotuma: $timeM, Muda ulioingia: $timeC, Mtandao: $fromnetwork itumwe wapi? Jibu Tigopesa, Airtelmoney au Halopesa"
                                        sendSms(wakalacontact, smsText)

                                    } else {

                                        //INSERT FLOATIN STATUS 2(LARGE ORDER)
                                        iFloatIn(
                                            transid,
                                            amount,
                                            maxamount,
                                            balance,
                                            wakalaKeyId,
                                            2,
                                            "LARGE/WAIT",
                                            fromwakalacode,
                                            fromwakalaname,
                                            wakalacontact,
                                            smsbody,
                                            createdAt,
                                            modifiedAt,
                                            madeAt,
                                            0,
                                            repository
                                        )

                                        sendBroadcast(Intent().setAction("floatInReceiver"))

                                    }
                                } else {

                                    //INSERT FLOATIN STATUS 4(LATE ORDER)....SMS IMECHELIWA BY 300000 seconds

                                    iFloatIn(
                                        transid,
                                        amount,
                                        maxamount,
                                        balance,
                                        wakalaKeyId,
                                        4,
                                        "LATE ORDER",
                                        fromwakalacode,
                                        fromwakalaname,
                                        wakalacontact,
                                        smsbody,
                                        createdAt,
                                        modifiedAt,
                                        madeAt,
                                        0,
                                        repository
                                    )

                                    sendBroadcast(Intent().setAction("floatInReceiver"))

                                }
                            } else {
                                //INSERT FLOATIN STATUS 3(UNKWOWN WAKALA)..SIO WAKALA WETU


                                iFloatIn(
                                    transid,
                                    amount,
                                    "",
                                    balance,
                                    "",
                                    3,
                                    "UNKNOWN WAKALA",
                                    "",
                                    "",
                                    "",
                                    smsbody,
                                    createdAt,
                                    modifiedAt,
                                    madeAt,
                                    0,
                                    repository
                                )

                                sendBroadcast(Intent().setAction("floatInReceiver"))
//
//                                    //SEND ERROR TEXT TO ERROR NUMBER IF FLOATIN UNKNOWN WAKALA INSERTED
//                                    var smsText =
//                                        "$fromnetwork ERROR = UNKWOWN WAKALA: FLOATIN: $smsbody"
//                                    sendSms(errornumber, smsText)

                            }
                        } else {
                            //UPDATE FLOAT IN TRANSACTION ALREADY EXISTS

                            iFloatIn(
                                "",
                                "",
                                "",
                                "",
                                "",
                                3,
                                "DUPLICATE SMS",
                                "",
                                "",
                                "",
                                smsbody,
                                createdAt,
                                modifiedAt,
                                madeAt,
                                0,
                                repository
                            )
                            sendBroadcast(Intent().setAction("floatInReceiver"))

                        }
                    } else {

                        val float = floatinchange.toString()

                        iFloatIn(
                            "",
                            "",
                            "",
                            "",
                            "",
                            5,
                            "CHANGES IN $float",
                            "",
                            "",
                            "",
                            smsbody,
                            createdAt,
                            modifiedAt,
                            madeAt,
                            0,
                            repository
                        )

                        sendBroadcast(Intent().setAction("floatInReceiver"))

                        val sendText =
                            "$fromnetwork ERROR = CHANGES: FLOATIN: $smsbody -Changes in $float"
                        floatinchange.clear()

                        sendSms(errornumber, sendText)

                    }
                } else if (checkFloatOutWords(smsbody)) {

                    if (checkFloatOut(smsbody)) {

                        //GET VARIBLES
                        val (amount, name, balance, transid) = getFloatOut(smsbody)

                        //CHECK IF TRANSACTION(transactionid) EXISTS
                        val searchFloatOutNotDuplicate =
                            repository.searchFloatOutNotDuplicate(transid)

                        if (searchFloatOutNotDuplicate) {

                            //BALANCE FUNTION
                            checkbalancefunction(
                                balance,
                                amount,
                                name,
                                2,
                                createdAt,
                                madeAt,
                                repository
                            )

                            //CHECK IF WAKALA EXISTS
                            val searchWakala = repository.searchWakala(name)
                            Log.e("SANTA", name)
                            if (searchWakala != null) {

                                // CHECK IF FLOATOUT WAKALA ORDER EXISTS (floatinid(sent bu wakala mkuu order)) EXISTS
                                val searchFloatOutWakalaOrder =
                                    repository.searchFloatOutWakalaOrder(name)
                                Log.e("SANTA2", searchFloatOutWakalaOrder.toString())
                                if (searchFloatOutWakalaOrder) {
                                    Log.e("SANTA", "name")
                                    //UPDATE FLOATOUT STATUS 2(DONE)
                                    val wakalaKeyId = searchWakala.wakalaid


                                    uFloatOut(
                                        2,
                                        amount,
                                        name,
                                        transid,
                                        smsbody,
                                        "DONE",
                                        modifiedAt,
                                        madeAt,
                                        repository
                                    )
                                    sendBroadcast(Intent().setAction("floatOutReceiver"))

                                } else {

                                    //INSERT FLOATOUT STATUS 3(UNKWOWN ORDER)
                                    iFloatOut(
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
                                        smsbody,
                                        "",
                                        createdAt,
                                        modifiedAt,
                                        0,
                                        madeAt,
                                        repository
                                    )

                                    sendBroadcast(Intent().setAction("floatOutReceiver"))

                                }
                            } else {


                                //INSERT FLOATOUT STATUS 4(UNKWOWN WAKALA)
                                iFloatOut(
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
                                    smsbody,
                                    "",
                                    createdAt,
                                    modifiedAt,
                                    0,
                                    madeAt,
                                    repository
                                )

                                sendBroadcast(Intent().setAction("floatOutReceiver"))

                            }
                        } else {

                            iFloatOut(
                                "",
                                "",
                                "",
                                "",
                                "",
                                "",
                                "",
                                "",
                                "",
                                3,
                                "DUPLICATE SMS",
                                smsbody,
                                "",
                                createdAt,
                                modifiedAt,
                                0,
                                madeAt,
                                repository
                            )

                            sendBroadcast(Intent().setAction("floatOutReceiver"))


                        }
                    } else {
                        val float = floatoutchange.toString()
                        iFloatOut(
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
                            smsbody,
                            "",
                            createdAt,
                            modifiedAt,
                            0,
                            madeAt,
                            repository
                        )

                        sendBroadcast(Intent().setAction("floatOutReceiver"))
//                            val float = floatinchange.toString()
                        val sendText =
                            "$fromnetwork ERROR = CHANGES: FLOATOUT: $smsbody -Changes in $float"

                        sendSms(errornumber, sendText)
                        floatoutchange.clear()

                    }
                }
            } else {
                if (firstword == "WAKALAMKUU" && lastword == "WAKALAMKUU") {
                    // "WAKALAMKUU $firstword $amount $towakalacode $[towakalaname] $fromfloatinid $fromtransid $wakalano $fromnetwork $wakalakeyid WAKALAMKUU"
                    val firstW = filterBody(smsbody, 2)
                    val amount = filterBody(smsbody, 3)
                    val wakalacode = filterBody(smsbody, 4)

                    val namedata = smsbody.substringAfter("[")
                    val wakalaname = namedata.substringBefore("]")

                    val smsbody2 = smsbody.substringAfter("] ")
                    val fromfloatinid = filterBody(smsbody2, 1)
                    val fromtransid = filterBody(smsbody2, 2)
                    val wakalano = filterBody(smsbody2, 3)
                    val fromnetwork = filterBody(smsbody2, 4)
                    val wakalakeyid = filterBody(smsbody2, 5)

//                    val floatinstatus = filterBody(smsbody, 9)
                    val phone = filterNumber(smsAddress)
                    //CHECK IF TRANSACTION EXISTS
                    Log.e("hasan", "1")
                    val searchFloatOutOrderNotDuplicate =
                        repository.searchFloatOutOrderNotDuplicate(
                            fromtransid
                        )

                    if (searchFloatOutOrderNotDuplicate) {
                        Log.e("hasan", "2")
                        //CHECK IF WAKALA MKUU EXISTS AND GET ID
                        val searchWakalaMkuu = when (fromnetwork) {
                            "Tigopesa" -> repository.searchWakalaMkuuTigo(phone)?.wakalamkuuid
                            "Airtelmoney" -> repository.searchWakalaMkuuAirtel(phone)?.wakalamkuuid
                            "Halopesa" -> repository.searchWakalaMkuuHalotel(phone)?.wakalamkuuid
                            else -> ""
                        }

//                       val searchWakalaMkuu = repository.searchWakalaMkuuVoda(phone).wakalamkuuid

                        if (!searchWakalaMkuu.isNullOrBlank()) {
                            //CHECK IF WAKALA EXISTS AND GET CODE
                            val searchWakalaCode = repository.searchWakalaVoda(
                                wakalaname,
                                wakalacode,
                                wakalakeyid
                            )?.mpesa

                            //CHECK IF WAKALA EXISTS AND GET NAME
                            val searchWakalaName = repository.searchWakalaVoda(
                                wakalaname,
                                wakalacode,
                                wakalakeyid
                            )?.vodaname

                            if (searchWakalaCode != null && searchWakalaName != null) {
                                //INSERT FLOATOUT STATUS 0(PENDING)

                                iFloatOut(
                                    "",
                                    amount,
                                    wakalaname,
                                    wakalacode,
                                    fromnetwork,
                                    "",
                                    searchWakalaMkuu,
                                    fromfloatinid,
                                    fromtransid,
                                    0,
                                    "PENDING",
                                    "",
                                    wakalano,
                                    createdAt,
                                    modifiedAt,
                                    madeAt,
                                    0,
                                    repository
                                )
                                sendBroadcast(Intent().setAction("floatOutReceiver"))
                                //CHECK BALANCE
//                                    val balanci = repository.getBalance().balance.toInt()
                                val balancecheck = repository?.getBalance()
                                if (balancecheck >= amount.toInt()) {
                                    // CHECK IF AUTO ON
                                    val checkAuto = dataStorePreference.autoMode.first()
                                    if (checkAuto) {
                                        //DAILL USSD
//
                                        dialUssd(
                                            "*150*00#",
                                            wakalacode,
                                            wakalaname,
                                            amount,
                                            modifiedAt,
                                            fromfloatinid,
                                            fromtransid,
                                            repository,
                                            applicationContext,
                                            scope
                                        )
                                    }
                                } else {
                                    Toast.makeText(
                                        applicationContext,
                                        "HAMNA SALIO",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    val SmsText = "HAMNA SALIO BALANCE NI:{$balancecheck}"
                                    sendSms(errornumber, SmsText)
                                }

                            }
                        }
                    }

                } else if (firstword == "Tigopesa" || firstword == "Airtelmoney" || firstword == "Halopesa") {

                    val phone = filterNumber(smsAddress)

                    val wakalaorder = firstword
                    //CHECK IF WAKALACONTACT EXISTS AND GET WAKALA
                    val searchWakala = repository.searchWakalaContact(phone)
                    if (searchWakala != null) {

                        //CHECK IF THERE IS UNSERVED FLOATIN ORDER(check if wakala id exists and madeAt is greater than 12 hrs
                        val searchFloatInOrder =
                            repository.getFloatInOrder(searchWakala.wakalaid)
                        if (searchFloatInOrder != null) {

                            val wakalamkuunumber = when (firstword) {
                                "Tigopesa" -> repository.getWakalaMkuu().tigophone
                                "Airtelmoney" -> repository.getWakalaMkuu().airtelphone
                                "Halopesa" -> repository.getWakalaMkuu().halophone
                                else -> ""
                            }

                            val towakalacode = when (firstword) {
                                "Tigopesa" -> searchWakala.tigopesa
                                "Airtelmoney" -> searchWakala.airtelmoney
                                "Halopesa" -> searchWakala.halopesa
                                else -> ""
                            }

                            val towakalaname = when (firstword) {
                                "Tigopesa" -> searchWakala.tigoname
                                "Airtelmoney" -> searchWakala.airtelname
                                "Halopesa" -> searchWakala.haloname
                                else -> ""
                            }

                            val wakalano = searchFloatInOrder.wakalacontact
                            val fromfloatinid = searchFloatInOrder.floatinid
                            val amount = searchFloatInOrder.amount
                            val fromtransid = searchFloatInOrder.transid
                            val wakalaidkey = searchFloatInOrder.wakalaidkey

                            if (searchFloatInOrder.status == 0) {

                                //UPDATE FLOATIN WITH ORDER(STATUS 1= ODERSENT)

                                repository.updateFloatIn(
                                    1,
                                    fromfloatinid,
                                    wakalaorder,
                                    "PENDING->DONE",
                                    towakalacode,
                                    wakalamkuunumber,
                                    towakalaname,
                                    modifiedAt,
                                    madeAt
                                )

                                sendBroadcast(Intent().setAction("floatInReceiver"))
                                //"WAKALAMKUU $wakalaorder $amount $towakalacode $towakalaname $fromfloatinid $fromtransid $wakalano $fromnetwork wakalaidkey WAKALAMKUU"

                                val smsText =
                                    "WAKALAMKUU $wakalaorder $amount $towakalacode [$towakalaname] $fromfloatinid $fromtransid $wakalano $fromnetwork $wakalaidkey WAKALAMKUU"
                                sendSms(wakalamkuunumber, smsText)

                            } else if (searchFloatInOrder.status == 2 && searchFloatInOrder.comment == "LARGE") {
                                //UPDATE FLOATIN WITH ORDER(STATUS 1= ODERSENT)

                                repository.updateFloatIn(
                                    1,
                                    fromfloatinid,
                                    wakalaorder,
                                    "LARGE->DONE",
                                    towakalacode,
                                    wakalamkuunumber,
                                    towakalaname,
                                    modifiedAt,
                                    madeAt
                                )

                                sendBroadcast(Intent().setAction("floatInReceiver"))

                                val smsText =
                                    "WAKALAMKUU $wakalaorder $amount $towakalacode [$towakalaname] $fromfloatinid $fromtransid $wakalano $fromnetwork $wakalaidkey WAKALAMKUU"
                                sendSms(wakalamkuunumber, smsText)

                            }
                        }
                    }
                }
            }
        }
        stopForeground(true)
        stopSelf()
        return START_NOT_STICKY;
    }


    @RequiresApi(Build.VERSION_CODES.N)
    private suspend fun checkbalancefunction(
        balance: String,
        amount: String,
        name: String,
        status: Int,
        createdAt: Long,
        madeAt: Long,
        repository: MobileRepository
    ) {
        scope.launch {
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

            sendBroadcast(Intent().setAction("balanceReceiver"))

            //CHECK BALANCE
            val balancecheck = repository?.getBalance();
            if (balancecheck < 100000) {
                val smsText =
                    "$fromnetwork SALIO = : CHINI CHA ${getComma(balancecheck.toString())}"
                Log.e("hasms", balancecheck.toString())
                Log.e("hasms", repository.getBalance().toString())
                Toast.makeText(applicationContext, "This foreground", Toast.LENGTH_SHORT).show()
                sendSms(errornumber, smsText)
            }
        }
    }

    private suspend fun iFloatIn(
        transid: String,
        amount: String,
        maxamount: String,
        balance: String,
        wakalaidkey: String,
        status: Int,
        comment: String,
        fromwakalacode: String,
        fromwakalaname: String,
        wakalacontact: String,
        networksms: String,
        createdat: Long,
        modifiedat: Long,
        madeatfloat: Long,
        madeatorder: Long,
        repository: MobileRepository
    ) {
        repository.insertFloatIn(
            FloatIn(
                0,
                transid,
                amount,
                maxamount,
                balance,
                wakalaidkey,
                status,
                fromnetwork,
                "",
                comment,
                fromwakalacode,
                "",
                "",
                fromwakalaname,
                "",
                wakalacontact,
                networksms,
                createdat,
                modifiedat,
                madeatfloat,
                madeatorder,
                0
            )
        )
    }

    private suspend fun iFloatOut(
        transid: String,
        amount: String,
        name: String,
        code: String,
        network: String,
        wakalaKeyId: String,
        wakalamkuu: String,
        fromfloatinid: String,
        fromtransid: String,
        status: Int,
        comment: String,
        networksms: String,
        wakalanumber: String,
        createdat: Long,
        modifiedat: Long,
        madeatorder: Long,
        madeatfloat: Long,
        repository: MobileRepository
    ) {
        repository.insertFloatOut(
            FloatOut(
                0,
                transid,
                amount,
                name,
                code,
                network,
                wakalaKeyId,
                wakalamkuu,
                fromfloatinid,
                fromtransid,
                status,
                comment,
                networksms,
                wakalanumber,
                createdat,
                modifiedat,
                madeatorder,
                madeatfloat,
                0
            )
        )
    }

    private suspend fun uFloatOut(
        status: Int,
        amount: String,
        wakalaname: String,
        transid: String,
        networksms: String,
        comment: String,
        modifiedat: Long,
        madeatfloat: Long,
        repository: MobileRepository
    ) {
        repository.updateFloatOut(
            status,
            amount,
            wakalaname,
            transid,
            networksms,
            comment,
            modifiedat,
            madeatfloat
        )
    }


}

