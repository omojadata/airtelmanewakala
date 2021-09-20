package com.example.airtelmanewakala

import android.annotation.TargetApi
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.SmsMessage
import android.util.Log
import androidx.annotation.RequiresApi

class SmsBroadcastReceiver : BroadcastReceiver() {
    //    private val TAG = 'MY'
    private val TAG = "boadcast"
    val pduType = "pdus"
    @RequiresApi(Build.VERSION_CODES.O)
    @TargetApi(Build.VERSION_CODES.M)
    override fun onReceive(context: Context?, intent: Intent) {
        // Get the SMS message.
        val bundle = intent.extras
        val msgs: Array<SmsMessage?>
        val format = bundle!!.getString("format")
        // Retrieve the SMS message received.
        val pdus = bundle[pduType] as Array<*>?

        var strAddress = ""
        var strBody = StringBuilder()
        var  strTime= ""

        for (pdu in pdus!!) {
            val smsMessage = SmsMessage.createFromPdu(pdu as ByteArray?)
            strBody.append(smsMessage.messageBody)
            strAddress = smsMessage.displayOriginatingAddress
            strTime = smsMessage.timestampMillis.toString()
        }


        val intent1 = Intent(context!!.applicationContext, ForegroundSmsService::class.java)
        intent1.putExtra("smsadress",strAddress)
        intent1.putExtra("smsbody",strBody.toString())
        intent1.putExtra("smstime",strTime)
        context.applicationContext.startForegroundService(intent1)

    }


}