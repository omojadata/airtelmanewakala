package com.example.airtelmanewakala.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "balance_table")
data class Balance(


    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="balanceid")
    val balanceid: Int,
    @ColumnInfo(name="balance")
    val balance: String,
    @ColumnInfo(name="floatamount")
    val floatamount: String,
    @ColumnInfo(name="floatname")
    val floatname: String,
    @ColumnInfo(name="status")
    val status: Int,
    @ColumnInfo(name="createdAt")
    val createdAt: Long,
    @ColumnInfo(name="madeAt")
    val madeAt: Long,
)
