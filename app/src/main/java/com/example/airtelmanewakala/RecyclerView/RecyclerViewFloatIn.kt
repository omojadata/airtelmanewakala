package com.example.airtelmanewakala.RecyclerView

import android.graphics.Color
import android.icu.text.NumberFormat
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.airtelmanewakala.R
import com.example.airtelmanewakala.databinding.FloatinitemlistBinding
import com.example.airtelmanewakala.db.FloatIn
import com.example.airtelmanewakala.getComma
import com.example.airtelmanewakala.getDate
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

//class RecyclerViewWakalaMkuu(private val wakalaMkuuList:List<WakalaMkuu>):RecyclerView.Adapter<MyWakalaMkuuViewHolder>()

class RecyclerViewFloatIn(private val clickListener: (FloatIn)->Unit): RecyclerView.Adapter<MyFloatInViewHolder>()
{
    val floatInList= ArrayList<FloatIn>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyFloatInViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        val binding: FloatinitemlistBinding= DataBindingUtil.inflate(
            layoutInflater,
            R.layout.floatinitemlist,
            parent,
            false
        )
        return MyFloatInViewHolder(binding)
//        val binding: FloatoutitemlistBinding = DataBindingUtil.inflate(layoutInflater,
//            R.layout.floatoutitemlist,parent,false)
//        return  MyFloatOutViewHolder(binding)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MyFloatInViewHolder, position: Int) {
        holder.bind(floatInList[position],clickListener)
    }

    fun setList(floatIn: List<FloatIn>){
        floatInList.clear()
        floatInList.addAll(floatIn)
//        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
      return floatInList.size
    }


}

class MyFloatInViewHolder(val binding: FloatinitemlistBinding):RecyclerView.ViewHolder(binding.root){
 @RequiresApi(Build.VERSION_CODES.O)
 fun bind(floatIn: FloatIn, clickListener: (FloatIn)->Unit){
//     val bus: String = mData.get(position)
//     holder.busStopName.setText(bus)
     if (floatIn.status == 0) {
         binding.floatincardView.setCardBackgroundColor(Color.parseColor("#add8e6"))
         // Set text color what should be for Bus left
     } else if (floatIn.status == 1) {
         binding.floatincardView.setCardBackgroundColor(Color.parseColor("#00ab66"))
         // Set text color what should be for upcoming buses
     } else if (floatIn.status == 2) {
       binding.floatincardView.setCardBackgroundColor(Color.parseColor("#ffa500"))
         // Set text color what should be for upcoming buses
     } else if (floatIn.status == 3) {
         binding.floatincardView.setCardBackgroundColor(Color.parseColor("#808080"))
         // Set text color what should be for upcoming buses
     }else if (floatIn.status == 4) {
         binding.floatincardView.setCardBackgroundColor(Color.parseColor("#ffff00"))
         // Set text color what should be for upcoming buses
     }else if (floatIn.status == 5) {
         binding.floatincardView.setCardBackgroundColor(Color.parseColor("#ff0f0f"))
         // Set text color what should be for upcoming buses
     }

     if(floatIn.status==3){
         binding.sectionone.text=floatIn.networksms
         binding.sectiontwo.text=floatIn.comment
         binding.sectionthree.text=getDate(floatIn.modifiedat)
     }else if(floatIn.status==5){
         binding.sectionone.text=floatIn.networksms
         binding.sectiontwo.text=floatIn.comment
         binding.sectionthree.text=getDate(floatIn.modifiedat)
     }else{
         binding.sectionone.text="Tsh "+getComma(floatIn.amount)+"\n"+floatIn.comment+"\n"+floatIn.transid+"\n"+getDate(floatIn.createdat)
         binding.sectiontwo.text=floatIn.fromwakalaname+"  "+floatIn.fromwakalacode+"\n"+floatIn.towakalaname+"  "+floatIn.towakalacode
         binding.sectionthree.text=floatIn.fromnetwork+"->"+floatIn.wakalaorder +"\n"+getDate(floatIn.modifiedat)
     }

     binding.listItemLayout.setOnClickListener {
         clickListener(floatIn)
     }
 }

}
