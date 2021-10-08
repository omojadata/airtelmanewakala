package com.example.airtelmanewakala.RecyclerView

import android.graphics.Color
import android.icu.text.NumberFormat
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.airtelmanewakala.R
import com.example.airtelmanewakala.databinding.BalanceitemlistBinding

import com.example.airtelmanewakala.db.Balance
import com.example.airtelmanewakala.db.FloatIn
import com.example.airtelmanewakala.getComma
import com.example.airtelmanewakala.getDate
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*


class RecyclerViewBalance (): RecyclerView.Adapter<MyBalanceViewHolder>() {
    private val balanceList= ArrayList<Balance>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyBalanceViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        val binding: BalanceitemlistBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.balanceitemlist,parent,false)
        return MyBalanceViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MyBalanceViewHolder, position: Int) {
        holder.bind(balanceList[position])
    }
    fun setList(balance: List<Balance>){
        balanceList.clear()
        balanceList.addAll(balance)
//        notifyDataSetChanged()
    }
    override fun getItemCount(): Int {
        return balanceList.size
    }
}
class MyBalanceViewHolder(val binding: BalanceitemlistBinding): RecyclerView.ViewHolder(binding.root){

    @RequiresApi(Build.VERSION_CODES.O)
    fun bind(balance: Balance){
        if (balance.status == 1) {
            binding.balancecardView.setCardBackgroundColor(Color.parseColor("#00ab66"))
            binding.sectionone.text="Tsh "+getComma(balance.balance)
            binding.sectiontwo.text=balance.floatname+"\n"+"Tsh "+getComma(balance.floatamount)
            binding.sectionthree.text=getDate(balance.createdAt)
        } else if (balance.status == 2) {
            binding.balancecardView.setCardBackgroundColor(Color.parseColor("#ff0f0f"))
            binding.sectionone.text="Tsh "+getComma(balance.balance)
            binding.sectiontwo.text=balance.floatname+"\n"+"Tsh "+getComma(balance.floatamount)
            binding.sectionthree.text=getDate(balance.createdAt)
        }else{
            binding.balancecardView.setCardBackgroundColor(Color.parseColor("#808080"))
            binding.sectionone.text="Tsh "+getComma(balance.balance)
            binding.sectiontwo.visibility= View.GONE
            binding.sectionthree.text=getDate(balance.createdAt)
        }

    }


}