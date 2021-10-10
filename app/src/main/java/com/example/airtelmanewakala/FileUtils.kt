package com.example.airtelmanewakala

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.text.NumberFormat
import android.os.Build
import android.telephony.SmsManager
import android.text.format.DateUtils
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import com.example.airtelmanewakala.db.MobileRepository
import com.example.airtelmanewakala.db.MoblieDatabase
import com.romellfudi.ussdlibrary.USSDApi
import com.romellfudi.ussdlibrary.USSDController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

var fromnetwork = "Airtel"
const val mtandao = "AirtelMoney"
const val errornumber = "+255683071757"
//val contactnumber = "+255714363727"
var floatinchange = StringBuilder()
var floatoutchange = StringBuilder()
fun generateFile(context: Context?, fileName: String): File? {
    val csvFile = File(context?.filesDir, fileName)
    csvFile.createNewFile()

    return if (csvFile.exists()) {
        csvFile
    } else {
        null
    }
}

fun goToFileIntent(context: Context?, file: File): Intent {
    val intent = Intent(Intent.ACTION_VIEW)
    val contentUri =
        context?.let { FileProvider.getUriForFile(it, "${context.packageName}.fileprovider", file) }
    val mimeType = contentUri?.let { context?.contentResolver.getType(it) }
    intent.setDataAndType(contentUri, mimeType)
    intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION

    return intent
}

fun sendSms(number: String, smstext: String) {
    val sms = SmsManager.getDefault()
    val parts: ArrayList<String> = sms.divideMessage(smstext)
    sms.sendMultipartTextMessage(
        number,
        null,
        parts,
        null,
        null
    )
}


fun filterBody(str: String, n: Int): String {
    var strr= str.split(" ")
    return if(strr.isNotEmpty()){
        strr[n-1]
    }else{
        ""
    }
}

fun filterNumber(str: String): String {
    return str.replace("+255", "0")
}

fun filterMoney(str: String): String {
    val i = str.substringBefore(".00")
    val word = Regex("[^0-9]")
    return word.replace(i, "")
}


@RequiresApi(Build.VERSION_CODES.N)
fun getComma(i: String): String {
    val ans = NumberFormat.getNumberInstance(Locale.US).format(i.toInt())
    return ans.toString()
}

@RequiresApi(Build.VERSION_CODES.O)
fun getDate(created: Long): String? {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    val instant = Instant.ofEpochMilli(created.toLong())
    val date = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
    return formatter.format(date).toString()
}

fun isTodayDate(x: Long): Boolean {
    return DateUtils.isToday(x)
}

@RequiresApi(Build.VERSION_CODES.O)
fun getTime(created: Long): String? {
    val formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    val instant = Instant.ofEpochMilli(created.toLong())
    val date = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
    return formatter.format(date).toString()
}

fun filterName(str: String): String {
    val i = str.substringBefore(".00")
    val word = Regex("[^0-9 ]")
    return word.replace(i, "")
}

fun filter(str: String): String {
    val word = Regex("[^0-9 ]")
    val words = word.replace(str, "")
    return words.substring(0, words.length - 2)
}

var floatinwords = arrayOf("Umepokea", "kutoka")

var floatoutwords = arrayOf("Umetuma", "kwenda")

fun containsWords(inputString: String, items: Array<String>): Boolean {
    var found = true
    for (item in items) {
        if (!inputString.contains(item)) {
            found = false
            break
        }
    }
    return found
}


fun checkFloatInWords(str: String): Boolean {
    return containsWords(str, floatinwords)
}

fun checkFloatOutWords(str: String): Boolean {
    return containsWords(str, floatoutwords)
}
//Umepokea Tsh10,000.00 kutoka 689358490,EDWIN DAMAS MROSSO. Salio jipya Tsh160,000.00.Muamala No: PP211006.1816.E27570
fun checkFloatIn(str: String): Boolean {

    //amount
    val amount = filterBody(str, 2)
    val amountRegex = Regex("^Tsh\\d+(,\\d{3})*(\\.00)?\$")
    val checkAmount = amount.matches(amountRegex)

    //code
    val codedata =  str.substringAfter("kutoka ")
    val codedata2 = codedata.substringBefore(",")
    val codeRegex = Regex("^[0-9]\\d{8}")
    val checkCode = codedata2.matches(codeRegex)

    //name
    val namedata = str.substringAfter("kutoka ")
    val namedata2 = namedata.substring(9)
    val namedata3 = namedata2.substringBefore(" Salio")
    val nameRegex = Regex("^,[A-Za-z0-9 ]+.")
    val checkName = namedata3.matches(nameRegex)

    //balance
    val balancedata = str.substringAfter("jipya ")
    val balancedata2 = balancedata.substringBefore(".Muamala")
    val balanceRegex = Regex("^Tsh\\d+(,\\d{3})*(\\.00)?\$")
    val checkBalance = balancedata2.matches(balanceRegex)

    //Transid
    val transiddata = str.substringAfter("Muamala")
    val transiddata2 = transiddata.replace("\\s+".toRegex(), "")
    val transidRegex = Regex("^No:PP\\d{6}.\\d{4}.[A-Z0-9]{6}")
    val checkTransid = transiddata2.matches(transidRegex)


    if (!checkName) {
        floatinchange.append("Name ")
    }

    if (!checkAmount) {
        floatinchange.append("Amount ")
    }

    if (!checkTransid) {
        floatinchange.append("Transid ")
    }

    if (!checkBalance) {
        floatinchange.append("Balance ")
    }

    if (!checkCode) {
        floatinchange.append("Code ")
    }

    return checkName && checkAmount && checkTransid && checkBalance && checkCode
}

fun getFloatIn(str: String): Array<String> {

    //amount
    val amountdata = filterBody(str, 2)
    val amount = filterMoney(amountdata)

    //code
    val codedata =  str.substringAfter("kutoka ")
    val codedata2 = codedata.substringBefore(",")
    val code ="0"+codedata2

    //name
    val namedata = str.substringAfter("kutoka ")
    val namedata2 = namedata.substring(9)
    val namedata3 = namedata2.substringBefore(". Salio")
    val nameRegex = Regex("[^A-Za-z0-9 ]")
    val name = nameRegex.replace(namedata3, "").trim()

    //balance
    val balancedata = str.substringAfter("jipya ")
    val balancedata2 = balancedata.substringBefore(".Muamala")
    val balance = filterMoney(balancedata2)

    //transid
    val transiddata = str.substringAfter("Muamala")
    val transiddata2 = transiddata.replace("\\s+".toRegex(), "")
    val transid = transiddata2.removeRange(0, 3)

    return arrayOf(amount, name, balance, transid, code)
}

//fun main(){
////    val stri="Umetuma 10,000.00 Tsh kwenda 689358490,EDWIN DAMAS MROSSO.Kodi Tsh 0.00). Salio Jipya Tsh 150,000.00Tsh .Muamala No. PP211006.1842.E28687.Tunakujali,Muamala Huu Ni BURE!"
//val stri= "Umepokea Tsh100,000.00 kutoka 787193129,AUDAX OSCAR MUJUNI. Salio jipya Tsh955,000.00.Muamala No: PP211005.1314.B61281"
////    checkFloatOut(stri)
//    println(checkFloatIn(stri).toString())
//    for (ele in getFloatIn(stri) ){
//        println(ele)
//    }
//}
//Umetuma 10,000.00 Tsh kwenda 689358490,EDWIN DAMAS MROSSO.Kodi Tsh 0.00). Salio Jipya Tsh 150,000.00Tsh .Muamala No. PP211006.1842.E28687.Tunakujali,Muamala Huu Ni BURE!
fun checkFloatOut(str: String): Boolean {
    //amount
    val amountdata = filterBody(str, 2)
    val amountRegex = Regex("^\\d+(,\\d{3})*(\\.00)?\$")
    val checkAmount = amountdata.matches(amountRegex)

    //code
    val codedata =  str.substringAfter("kwenda ")
    val codedata2 = codedata.substringBefore(",")
    val codeRegex = Regex("^[0-9]\\d{8}")
    val checkCode = codedata2.matches(codeRegex)

    //name
    val namedata = str.substringAfter("kwenda ")
    val namedata2 = namedata.substring(9)
    val namedata3 = namedata2.substringBefore("Kodi")
    val nameRegex = Regex("^,[A-Za-z0-9 ]+.")
    val checkName = namedata3.matches(nameRegex)

    //balance
    val balancedata = str.substringAfter("Salio Jipya Tsh ")
    val balancedata2 = balancedata.substringBefore(" .Muamala")
    val balanceRegex = Regex("^\\d+(,\\d{3})*(\\.00Tsh)?\$")
    val checkBalance = balancedata2.matches(balanceRegex)

    //Transid
    val transiddata = str.substringAfter("Muamala")
    val transiddata2 = transiddata.substringBefore(".Tunakujali,Muamala")
    val transiddata3 = transiddata2.replace("\\s+".toRegex(), "")
    val transidRegex = Regex("^No.PP\\d{6}.\\d{4}.[A-Z0-9]{6}")
    val checkTransid = transiddata3.matches(transidRegex)


    if (!checkName) {
        floatoutchange.append("Name ")
    }

    if (!checkAmount) {
        floatoutchange.append("Amount ")
    }

    if (!checkTransid) {
        floatoutchange.append("Transid ")
    }

    if (!checkBalance) {
        floatoutchange.append("Balance ")
    }

    if (!checkCode) {
        floatoutchange.append("Code ")
    }
    return checkName && checkAmount && checkTransid && checkBalance && checkCode
}


fun getFloatOut(str: String): Array<String> {

    //amount
    val amountdata = filterBody(str, 2)
    val amount = filterMoney(amountdata)

    //code
    val codedata =  str.substringAfter("kwenda ")
    val codedata2 = codedata.substringBefore(",")
    val code ="0"+codedata2

    //name
    val namedata = str.substringAfter("kwenda ")
    val namedata2 = namedata.substring(9)
    val namedata3 = namedata2.substringBefore("Kodi")
    val nameRegex = Regex("[^A-Za-z0-9 _]")
    val name = nameRegex.replace(namedata3, "").trim()

    //balance
    val balancedata = str.substringAfter("Salio Jipya Tsh ")
    val balancedata2 = balancedata.substringBefore(" .Muamala")
    val balance = filterMoney(balancedata2)



    //transid
    val transiddata = str.substringAfter("Muamala")
    val transiddata2 = transiddata.substringBefore(".Tunakujali,Muamala")
    val transiddata3 = transiddata2.replace("\\s+".toRegex(), "")
    val transid = transiddata3.removeRange(0, 3)

    return arrayOf(amount, name, balance, transid, code)

}

suspend fun dialUssd(
    ussdCode: String,
    wakalacode: String,
    wakalaname: String,
    amount: String,
    modifiedAt: Long,
    fromfloatinid: String,
    fromtransid: String,
    repository: MobileRepository,
    context: Context,
    scope: CoroutineScope
) {
    var ussdchange = StringBuilder()
    val map = HashMap<String, List<String>>()
    map["KEY_LOGIN"] = Arrays.asList("USSD code running...")
    map["KEY_ERROR"] =
        Arrays.asList("Connection problem or invalid MMI code", "problem", "error", "null")
    val ussdApi = USSDController
    USSDController.callUSSDOverlayInvoke(
        context,
        ussdCode,
        map,
        object : USSDController.CallbackInvoke {
            override fun responseInvoke(message: String) {
                // message has the response string data
                ussdchange.append("*150*60#")
                ussdApi.send("1") {
                    ussdchange.append(" 1")
                    ussdApi.send(wakalacode) {
                        ussdchange.append(" code")
                        ussdApi.send(amount) { message3 ->
                            ussdchange.append(" amount")
                            if (message3.contains(wakalaname)) {
                                ussdchange.append(" Accept")
                                ussdApi.send("1212") {

                                }
                            } else {
                                ussdApi.cancel()
                                ussdchange.clear()
                            }
                        }

                    }
                }
            }

            override fun over(message: String) {
                if (message.contains("Ndugu mteja ombi lako linashughulikiwa.")) {
                    Log.e("USSDTAG2", ussdchange.toString())
                    if (ussdchange.toString().contains("Accept")) {
                        Log.e("USSDTAG22", ussdchange.toString())
                        scope.launch {
                            repository.updateFloatOutUSSD(
                                1,
                                amount,
                                fromfloatinid,
                                fromtransid,
                                "USSD",
                                modifiedAt
                            )
                        }
                    }
                } else if (message.contains("Connection problem or invalid MMI code")) {
                    Log.e("USSDTAG2", "$message")
                    if (ussdchange.toString().contains("Accept")) {
                        Log.e("USSDTAG2", "$message")
                        scope.launch {
                            repository.updateFloatOutUSSD(
                                1,
                                amount,
                                fromfloatinid,
                                fromtransid,
                                "USSD",
                                modifiedAt
                            )
                        }
                    }
                }
                ussdchange.clear()

            }
        })
}

