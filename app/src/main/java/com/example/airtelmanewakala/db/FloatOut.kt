package com.example.airtelmanewakala.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "floatout_table")
data class FloatOut (
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name="floatoutid")
        val floatoutid: Int,
        @ColumnInfo(name="transid")
        val transid: String,
        @ColumnInfo(name="amount")
        val amount: String,
        @ColumnInfo(name="wakalaname")
        val wakalaname: String,
        @ColumnInfo(name="wakalacode")
        val wakalacode: String,
        @ColumnInfo(name="network")
        val network: String,
        @ColumnInfo(name="wakalaidkey")
        val wakalaidkey: String,
        @ColumnInfo(name="wakalamkuu")
        val wakalamkuu: String,
        @ColumnInfo(name="fromfloatinid")
        val fromfloatinid: String,
        @ColumnInfo(name="fromtransid")
        val fromtransid: String,
        @ColumnInfo(name="status")
        val status: Int,
        @ColumnInfo(name="comment")
        val comment: String,
        @ColumnInfo(name="networksms")
        val networksms: String,
        @ColumnInfo(name="wakalanumber")
        val wakalanumber: String,
        @ColumnInfo(name="createdat")
        val createdat: Long,
        @ColumnInfo(name="modifiedat")
        val modifiedat: Long,
        @ColumnInfo(name="madeatorder")
        val madeatorder: Long,
        @ColumnInfo(name="madeatfloat")
        val madeatfloat: Long,
        @ColumnInfo(name="deletestatus")
        val deletestatus: Int,

//
//        floatoutid: Int,
//        transid: String,
//        amount: String,
//        wakalaname: String,
//        wakalacode: String,
//        network: String,
//        wakalaidkey: String,
//        wakalamkuu: Int,
//        fromfloatinid: String,
//        fromtransid: String,
//        status: Int,
//        comment: String,
//        networksms: String,
//        wakalanumber: String,
//        createdAt: Long,
//        modifiedAt: Long,
//        madeAt: Long,
//
//
//        floatoutid,
//        transid,
//        amount,
//        wakalaname,
//        wakalacode,
//        network,
//        wakalaidkey,
//        wakalamkuu,
//        fromfloatinid,
//        fromtransid,
//        status,
//        comment,
//        networksms,
//        wakalanumber,
//        createdAt,
//        modifiedAt,
//        madeAt

//        floatoutid,transid,amount,wakalaname,wakalacode,network,wakalaidkey,wakalamkuu,fromfloatinid,fromtransid,status,comment,networksms,wakalanumber,createdAt,modifiedAt,madeAt

//        floatoutid=:floatoutid,transid=:transid,amount=:amount,wakalaname=:wakalaname,wakalacode=:wakalacode,network=:network,wakalaidkey=:wakalaidkey,wakalamkuu=:wakalamkuu,fromfloatinid=:fromfloatinid,fromtransid=:fromtransid,status=:status,comment=:comment,networksms=:networksms,wakalanumber=:wakalanumber,createdAt=:createdAt,modifiedAt=:modifiedAt,madeAt=:madeAt
//
//        floatoutid: Int,transid: String,amount: String,wakalaname: String, wakalacode: String,network: String,wakalaidkey: String,wakalamkuu: Int,fromfloatinid: String,fromtransid: String,status: Int,comment: String,networksms: String,wakalanumber: String,createdAt: Long,modifiedAt: Long,madeAt: Long,
)
