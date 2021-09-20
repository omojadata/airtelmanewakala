package com.example.airtelmanewakala.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "floatin_table")
data class FloatIn (
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name="floatinid")
        val floatinid: Int,
        @ColumnInfo(name="transid")
        val transid: String,
        @ColumnInfo(name="amount")
        val amount: String,
        @ColumnInfo(name="maxamount")
        val maxamount: String,
        @ColumnInfo(name="balance")
        val balance: String,
        @ColumnInfo(name="wakalaidkey")
        val wakalaidkey: String,
        @ColumnInfo(name="status")
        val status: Int,
        @ColumnInfo(name="fromnetwork")
        val fromnetwork: String,
        @ColumnInfo(name="wakalaorder")
        val wakalaorder: String,
        @ColumnInfo(name="comment")
        val comment: String,
        @ColumnInfo(name="fromwakalacode")
        val fromwakalacode: String,
        @ColumnInfo(name="towakalacode")
        val towakalacode: String,
        @ColumnInfo(name="wakalamkuunumber")
        val wakalamkuunumber: String,
        @ColumnInfo(name="fromwakalaname")
        val fromwakalaname: String,
        @ColumnInfo(name="towakalaname")
        val towakalaname: String,
        @ColumnInfo(name="wakalacontact")
        val wakalacontact: String,
        @ColumnInfo(name="networksms")
        val networksms: String,
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


//         transid: String,
//         amount: String,
//         balance: String,
//         wakalaidkey: String,
//         status: Int,
//         fromnetwork: String,
//         wakalaorder: String,
//         comment: String,
//         fromwakalacode: String,
//         towakalacode: String,
//         wakalamkuunumber: String,
//         fromwakalaname: String,
//         towakalaname: String,
//         wakalacontact: String,
//         networksms: String,
//         createdAt: Long,
//         modifiedAt: Long,
//         madeAt: Long,
//
//
//        transid,amount,balance,wakalaidkey,status,fromnetwork,wakalaorder,comment,fromwakalacode,towakalacode,wakalamkuunumber,fromwakalaname,towakalaname,wakalacontact,networksms,createdAt,modifiedAt,madeAt
//
//
//        transid=:transid,amount=:amount,balance=:balance,wakalaidkey=:wakalaidkey ,status=:status,fromnetwork=:fromnetwork,wakalaorder=:wakalaorder,comment=:comment,fromwakalacode=:fromwakalacode,towakalacode=:towakalacode,wakalamkuunumber=:wakalamkuunumber,fromwakalaname=:fromwakalaname,towakalaname=:towakalaname,wakalacontact=:wakalacontact,networksms=:networksms,createdAt=:createdAt ,modifiedAt=:modifiedAt,madeAt=:madeAt,
)