package com.example.airtelmanewakala.RecyclerView

import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.airtelmanewakala.R
import com.example.airtelmanewakala.databinding.FloatoutitemlistBinding
import com.example.airtelmanewakala.db.FloatIn
import com.example.airtelmanewakala.db.FloatOut
import com.example.airtelmanewakala.getDate
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

//class RecyclerViewWakalaMkuu(private val wakalaMkuuList:List<WakalaMkuu>):RecyclerView.Adapter<MyWakalaMkuuViewHolder>()

class RecyclerViewFloatOut(private val clickListener: (FloatOut)->Unit): RecyclerView.Adapter<MyFloatOutViewHolder>()
{
    val floatOutList= ArrayList<FloatOut>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyFloatOutViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        val binding: FloatoutitemlistBinding= DataBindingUtil.inflate(layoutInflater,R.layout.floatoutitemlist,parent,false)
        return  MyFloatOutViewHolder(binding)
//        val binding: WakalaitemlistBinding =
//            DataBindingUtil.inflate(layoutInflater, R.layout.wakalaitemlist,parent,false)
//        return MyWakalaViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MyFloatOutViewHolder, position: Int) {
        holder.bind(floatOutList[position],clickListener)
    }

    override fun getItemCount(): Int {
        return floatOutList.size
    }
    fun setList(floatOut: List<FloatOut>){
        floatOutList.clear()
        floatOutList.addAll(floatOut)
//        notifyDataSetChanged()
    }
}

class MyFloatOutViewHolder(val binding: FloatoutitemlistBinding):RecyclerView.ViewHolder(binding.root){

    @RequiresApi(Build.VERSION_CODES.O)
    fun bind(floatOut: FloatOut, clickListener: (FloatOut) -> Unit){
        if (floatOut.status == 0) {
            //pending
            binding.floatoutcardView.setCardBackgroundColor(Color.parseColor("#add8e6"))
            binding.sectionone.text="Amount -"+floatOut.amount+"\n"+floatOut.comment+"\n"+floatOut.transid+"\n"+getDate(floatOut.createdat)
            binding.sectiontwo.text=floatOut.wakalaname+"\n"+floatOut.wakalanumber
            binding.sectionthree.text=floatOut.network+"\n"+ getDate(floatOut.modifiedat)
        // Set text color what should be for Bus left
        } else if (floatOut.status == 1) {
            // "Ussd"
            binding.floatoutcardView.setCardBackgroundColor(Color.parseColor("#ffa500"))
            binding.sectionone.text="Amount -"+floatOut.amount+"\n"+floatOut.comment+"\n"+floatOut.transid+"\n"+getDate(floatOut.createdat)
            binding.sectiontwo.text=floatOut.wakalaname+"\n"+floatOut.wakalanumber
            binding.sectionthree.text=floatOut.network+"\n"+ getDate(floatOut.modifiedat)
            // Set text color what should be for upcoming buses
        } else if (floatOut.status == 2) {
            //Done
            binding.floatoutcardView.setCardBackgroundColor(Color.parseColor("#00ab66"))
            binding.sectionone.text="Amount -"+floatOut.amount+"\n"+floatOut.comment+"\n"+floatOut.transid+"\n"+getDate(floatOut.createdat)
            binding.sectiontwo.text=floatOut.wakalaname+"\n"+floatOut.wakalanumber
            binding.sectionthree.text=floatOut.network+"\n"+ getDate(floatOut.modifiedat)
            // Set text color what should be for upcoming buses
        } else if (floatOut.status == 3) {
            //invalid
            binding.sectionone.text=floatOut.networksms
            binding.sectiontwo.text=floatOut.comment
            binding.sectionthree.text=getDate(floatOut.modifiedat)
            binding.floatoutcardView.setCardBackgroundColor(Color.parseColor("#808080"))
            // Set text color what should be for upcoming buses
        } else if (floatOut.status == 4) {
            //Change
            binding.sectionone.text=floatOut.networksms
            binding.sectiontwo.text=floatOut.comment
            binding.sectionthree.text=getDate(floatOut.modifiedat)
            binding.floatoutcardView.setCardBackgroundColor(Color.parseColor("#ff0f0f"))
            // Set text color what should be for upcoming buses
        }
//        binding.sectionone.text="Amount -"+floatOut.amount+"\n"+floatOut.comment+"\n"+floatOut.transid+"\n"+getDate(floatOut.createdat)
//        binding.sectiontwo.text=floatOut.wakalaname+"\n"+floatOut.wakalanumber
//        binding.sectionthree.text=floatOut.network+"\n"+ getDate(floatOut.modifiedat)
        binding.listItemLayout.setOnClickListener {
            clickListener(floatOut)
        }
    }

}
