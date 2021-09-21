package com.example.airtelmanewakala.db

import android.database.Cursor
import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow


@Dao
interface MobileDAO {

    //FLOATIN
    @Query("SELECT * FROM floatin_table WHERE madeatfloat >= 0+ strftime('s','now','localtime','start of day') AND deletestatus=0 ORDER BY madeatfloat  DESC")
    fun floatIn(): Flow<List<FloatIn>>

    @Query("SELECT * FROM floatin_table WHERE status = :status AND madeatfloat  >= 0+ strftime('s','now','localtime','start of day') AND deletestatus=0 ORDER BY madeatfloat  DESC")
    fun floatInFilter(status: Int): Flow<List<FloatIn>>

    @Query("SELECT NOT EXISTS(SELECT * FROM floatin_table WHERE transid = :transid)")
    suspend fun searchFloatInNotDuplicate(transid: String): Boolean

    @Query("SELECT * FROM floatin_table WHERE (wakalaorder is NULL or wakalaorder = '' ) AND wakalaidkey=:wakalaid AND( status=0 OR status=2) AND madeatfloat  >= 0+ strftime('s','now','localtime','start of day') ORDER BY floatinid DESC LIMIT 1")
    suspend fun getFloatInOrder(wakalaid: String): FloatIn

    @Insert
    suspend fun insertFloatIn(FloatIn: FloatIn)

    @Query("UPDATE floatin_table SET transid=:transid,amount=:amount,balance=:balance,maxamount=:maxamount ,wakalaidkey=:wakalaidkey ,status=:status,fromnetwork=:fromnetwork,wakalaorder=:wakalaorder,comment=:comment,fromwakalacode=:fromwakalacode,towakalacode=:towakalacode,wakalamkuunumber=:wakalamkuunumber,fromwakalaname=:fromwakalaname,towakalaname=:towakalaname,wakalacontact=:wakalacontact ,modifiedat=:modifiedat WHERE floatinid=:floatinid AND status=5")
    suspend fun updateFloatInChange(
        floatinid: Int,
        transid: String,
        amount: String,
        maxamount:String,
        balance: String,
        wakalaidkey: String,
        status: Int,
        fromnetwork: String,
        wakalaorder: String,
        comment: String,
        fromwakalacode: String,
        towakalacode: String,
        wakalamkuunumber: String,
        fromwakalaname: String,
        towakalaname: String,
        wakalacontact: String,
        modifiedat: Long
    ): Int

    @Query("UPDATE floatin_table SET deletestatus = 1,modifiedat=:modifiedat WHERE floatinid=:floatinid AND status=5")
    suspend fun deleteFloatInChange(
        modifiedat:Long,
        floatinid:Int,
    )


    @Query("UPDATE floatin_table SET status = :status,wakalaorder=:wakalaorder,comment=:comment,towakalacode=:towakalacode, wakalamkuunumber=:wakalamkuunumber,towakalaname=:towakalaname ,modifiedat=:modifiedat, madeatorder=:madeatorder WHERE floatinid=:floatinid AND (status=0 OR status=2)")
    suspend fun updateFloatIn(
        status: Int,
        floatinid: Int,
        wakalaorder: String,
        comment: String,
        towakalacode: String,
        wakalamkuunumber: String,
        towakalaname: String,
        modifiedat: Long,
        madeatorder:Long
    ): Int

    @Query("UPDATE floatin_table SET comment=:comment ,modifiedat=:modifiedat WHERE floatinid=:floatinid AND status=2")
    suspend fun updateFloatInLarge(
        floatinid: Int,
        comment: String,
        modifiedat: Long
    ): Int


    //FLOATOUT
    @Query("SELECT * FROM floatout_table WHERE madeAtOrder >= 0+ strftime('s','now','localtime','start of day') AND deletestatus=0 ORDER BY modifiedat DESC")
    fun floatOut(): Flow<List<FloatOut>>

    @Query("SELECT * FROM floatout_table WHERE status = :status AND madeAtOrder >= 0+ strftime('s','now','localtime','start of day') AND deletestatus=0  ORDER BY modifiedat DESC")
    fun floatOutFilter(status: Int): Flow<List<FloatOut>>

    @Query("SELECT NOT EXISTS(SELECT * FROM floatout_table WHERE transid = :transid)")
    suspend fun searchFloatOutNotDuplicate(transid: String): Boolean

    @Query("SELECT NOT EXISTS (SELECT * FROM floatout_table WHERE fromtransid = :fromtransid)")
    suspend fun searchFloatOutOrderNotDuplicate(
        fromtransid: String
    ): Boolean

    @Insert
    suspend fun insertFloatOut(FloatOut: FloatOut)

    @Query("UPDATE floatout_table SET transid=:transid,amount=:amount,wakalaname=:wakalaname,wakalacode=:wakalacode,network=:network,wakalaidkey=:wakalaidkey,wakalamkuu=:wakalamkuu,fromfloatinid=:fromfloatinid,fromtransid=:fromtransid,status=:status,comment=:comment,wakalanumber=:wakalanumber,modifiedat=:modifiedat WHERE floatoutid=:floatoutid AND status=4")
    suspend fun updateFloatOutChange(
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
        modifiedat: Long,
    ): Int

    @Query("UPDATE floatout_table SET deletestatus =1,modifiedat=:modifiedat WHERE floatoutid=:floatoutid AND status=4")
    suspend fun deleteFloatOutChange(
        modifiedat:Long,
        floatoutid:Int,
    )

    @Query("UPDATE floatout_table SET status = :status, transid=:transid, comment=:comment,modifiedat=:modifiedat ,networksms=:networksms,madeatfloat=:madeatfloat WHERE  floatoutid IN(SELECT floatoutid from floatout_table WHERE amount =:amount AND wakalaname=:wakalaname AND (fromfloatinid is NOT NULL or fromfloatinid != '' ) AND (transid is NULL or transid = '' )  AND (status=0 OR status=1) ORDER BY floatoutid ASC LIMIT 1)")
    suspend fun updateFloatOut(
        status: Int,
        amount: String,
        wakalaname: String,
        transid: String,
        networksms: String,
        comment: String,
        modifiedat: Long,
        madeatfloat:Long
    ): Int


    @Query("SELECT EXISTS(SELECT * FROM floatout_table WHERE wakalaname = :wakalaname AND (status=0 OR status=1) LIMIT 1)")
    suspend fun searchFloatOutWakalaOrder(wakalaname: String): Boolean

    @Query("UPDATE floatout_table SET status = :status ,comment=:comment,modifiedat=:modifiedat  WHERE amount =:amount AND fromfloatinid=:fromfloatinid AND fromtransid=:fromtransid AND status=0")
    suspend fun updateFloatOutUSSD(
        status: Int,
        amount: String,
        fromfloatinid: String,
        fromtransid: String,
        comment: String,
        modifiedat: Long
    )



    //BALANCE
    @Query("SELECT * FROM balance_table ORDER BY createdAt DESC")
    fun balance(): Flow<List<Balance>>

    @Insert
    suspend fun insertBalance(balance: Balance)

    @Query("SELECT * FROM balance_table WHERE balanceid= (SELECT MAX(balanceid) FROM balance_table) ")
    suspend fun getBalance(): List<Balance>


    //WAKALAMKUU
    @Query("SELECT * FROM wakalamkuu_table ORDER BY wakalamkuuid DESC LIMIT 1")
    fun WakalaMkuu(): LiveData<List<WakalaMkuu>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWakalaMkuu(wakalaMkuu: List<WakalaMkuu>)

    @Query("SELECT * FROM wakalamkuu_table WHERE  status = 1 LIMIT 1")
    suspend fun getWakalaMkuu(): WakalaMkuu

//    @Query("SELECT * FROM wakalamkuu_table WHERE :column = :columnvalue AND status = 1 LIMIT 1")
//    suspend fun searchWakalaMkuu(column: String, columnvalue: String): WakalaMkuu

    @Query("SELECT * FROM wakalamkuu_table WHERE tigophone = :columnvalue AND status = 1 LIMIT 1")
    suspend fun searchWakalaMkuuTigo(columnvalue: String): WakalaMkuu

    @Query("SELECT * FROM wakalamkuu_table WHERE vodaphone= :columnvalue AND status = 1 LIMIT 1")
    suspend fun searchWakalaMkuuVoda(columnvalue: String): WakalaMkuu

    @Query("SELECT * FROM wakalamkuu_table WHERE airtelphone = :columnvalue AND status = 1 LIMIT 1")
    suspend fun searchWakalaMkuuAirtel(columnvalue: String): WakalaMkuu

    @Query("SELECT * FROM wakalamkuu_table WHERE halophone = :columnvalue AND status = 1 LIMIT 1")
    suspend fun searchWakalaMkuuHalotel(columnvalue: String): WakalaMkuu

    @Query("SELECT * FROM wakalamkuu_table WHERE ttclphone = :columnvalue AND status = 1 LIMIT 1")
    suspend fun searchWakalaMkuuTtcl(columnvalue: String): WakalaMkuu

    @Update
    suspend fun updateWakalaMkuu(wakalaMkuu: WakalaMkuu)


    //Wakala
//    @Query("SELECT * FROM wakala_table")
//    fun Wakala():LiveData<List<Wakala>>

    @Query("SELECT * FROM wakala_table")
    fun wakala(): Flow<List<Wakala>>

//    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
//    @Query("SELECT * FROM wakala_table")
//     fun wakalaCSV(): Cursor

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWakala(wakala: List<Wakala>)

    @Query("SELECT COUNT (airtelmoney) FROM wakala_table WHERE status = 1")
    fun getWakalaCount(): LiveData<Int>

    @Query("SELECT * FROM wakala_table  WHERE tigoname LIKE '%'||:text||'%' OR airtelname LIKE '%'||:text||'%' OR vodaname LIKE '%'||:text||'%' OR haloname LIKE '%'||:text||'%' OR tigopesa LIKE '%'||:text||'%' OR airtelmoney LIKE '%'||:text||'%' OR halopesa LIKE '%'||:text||'%' OR mpesa LIKE '%'||:text||'%' OR contact LIKE '%'||:text||'%'")
    fun searchViewWakala(text: String): Flow<List<Wakala>>

    @Query("SELECT * FROM wakala_table WHERE vodaname = :columnvalue AND status = 1 LIMIT 1")
    suspend fun searchWakala(columnvalue: String): Wakala

    @Query("SELECT * FROM wakala_table WHERE contact = :columnvalue AND status = 1 LIMIT 1")
    suspend fun searchWakalaContact(columnvalue: String): Wakala

    @Query("SELECT * FROM wakala_table WHERE  tigopesa = :columnvalue AND wakalaid=:wakalaidkey AND status = 1 LIMIT 1")
    suspend fun searchWakalaTigo(columnvalue: String, wakalaidkey: String): Wakala

    @Query("SELECT * FROM wakala_table WHERE airtelname=:columnname AND airtelmoney = :columnvalue AND wakalaid=:wakalaidkey AND status = 1 LIMIT 1")
    suspend fun searchWakalaAirtel(columnname: String,columnvalue: String, wakalaidkey: String): Wakala

    @Query("SELECT * FROM wakala_table WHERE vodaname = :columnname AND mpesa = :columnvalue AND wakalaid=:wakalaidkey AND status = 1 LIMIT 1")
    suspend fun searchWakalaVoda(columnname: String,columnvalue: String, wakalaidkey: String): Wakala

    @Query("SELECT * FROM wakala_table WHERE haloname=:columnname AND halopesa = :columnvalue AND wakalaid=:wakalaidkey AND status = 1 LIMIT 1")
    suspend fun searchWakalaHalotel(columnname: String,columnvalue: String, wakalaidkey: String): Wakala

    @Query("SELECT * FROM wakala_table WHERE tpesa = :columnvalue AND wakalaid=:wakalaidkey AND status = 1 LIMIT 1")
    suspend fun searchWakalaTtcl(columnvalue: String, wakalaidkey: String): Wakala

    @Query("UPDATE wakala_table SET tigopesa = :tigopesa WHERE wakalaid =:wakalaid")
    suspend fun updateWakala(tigopesa: String, wakalaid: String): Int


////AIRTELMONEY
////Out
// Umetuma Tshs5,000,000.00 kwa 678909076 AMINA NASORRO ANTONY. Salio Tshs2,540,890.00.Muamala No.PP2104011625.E91936.Tuma Pesa BURE na Airtel APP
// Umetuma TZS1,200,000.00 kwa 783327429,AMOSI KANAMUGILE. Salio TZS1,285,000.00.Muamala No.PP210624.1205.A33447.Tuma Pesa BURE na Airtel APP

////IN
    //then
// Umepokea Tshs20,000.00 kutoka 788014470,ASANITIELI RAPHAEL MKENDA. Salio jipya Tshs7,053,935.00.Muamala No:PP210401.1601.B46963
//now
//Umepokea TZS300,000.00 kutoka 693903391,JUMA JUMA. Salio jipya TZS1,785,000.00.Muamala No: PP210624.1338.B44267

    // IN FLROM WAKALA MKUU
// Umepokea TZS50,000.00 kutoka 699751145,HASSANI A MALENGELO. Salio jipya TZS1,485,000.00.Muamala No: PP210624.1316.D41897


////HALOPESA
//IN
// Utambulisho wa muamala:658767567.WAKALA:Susan M Mbwagai,namba ya simu 255623641076 imetoa TSH 150,000 wakati 01/04/2021 14:54:38. Kamisheni: TSH 0.Salio jipya la floti ni TSH3,164,634. Asante!
//OUT
// Utambulisho wa muamala:658684058.TSH 100,000 imewekwa kwa WAKALA:Suzan M Mbwagai,utambulisho 334833 wakati 01/04/2021 13:20:18. Kamisheni: TSH 0.Salio jipya la floti ni TSH 2,714,634. Ahsante!
//

    //Utambulisho wa muamala:839726029. WAKALA: IDDI HASSANI HURUKU, namba ya simu 255620604076 imetoa TSH 100,000 wakati 26/08/2021 16:23:04. Kamisheni: TSH 0. Salio jipya la floti ni TSH 1,710,000. Ahsante

//    Utambulisho wa muamala: 839753514. TSH 150,000 imewekwa kwa WAKALA: JACKSON PHILIMON FESTORY, utambulisho 380760 wakati 26/08/2021 16:46:51. Kamisheni: TSH 0. Salio jipya la floti ni TSH 1,560,000. Ahsante!


////VODACOM
//out
// 8D1750EA8L9 imethibitishwa 1/4/21 saa 4:48 PM chukua Tsh3,000,000.00 toka 950506 - ESTER SHOGHOLO MSOFE. Salio jipya ya akaunti yako ni Tsh7,128,782.00
//in
// 8D1450EF2L2 Imethibitishwa,tarehe 1/4/21 saa 5:05 PM chukua Tsh700,000.00 kutoka 555062 - j link Christopher Nkuu.Salio lako la M-Pesa ni Tsh7,828,792.00
//

//8HQ15RXUIAD Imethibitishwa, tarehe 26/8/21  saa 12:14 PM chukua Tsh100,000.00 kutoka 498911 - KULWA JOHN BAZILIO.Salio lako la M-Pesa ni Tsh720,000.00.

////TIGOPESA
// Zoezi la kuhamisha Fedha kutokaa kwa TRACE MOBILE LIMITED kwenda kwa TRACE MGT TILL GO

}