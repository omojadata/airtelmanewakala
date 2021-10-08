package com.example.airtelmanewakala.Fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.airtelmanewakala.R
import com.example.airtelmanewakala.RecyclerView.RecyclerViewBalance
import com.example.airtelmanewakala.RecyclerView.RecyclerViewFloatIn
import com.example.airtelmanewakala.databinding.FragmentBalanceBinding
import com.example.airtelmanewakala.databinding.FragmentFloatInBinding
import com.example.airtelmanewakala.db.Balance
import com.example.airtelmanewakala.db.FloatIn
import com.example.airtelmanewakala.db.MobileRepository
import com.example.airtelmanewakala.db.MoblieDatabase
import com.example.airtelmanewakala.viewmodel.BalanceViewModel
import com.example.airtelmanewakala.viewmodel.BalanceViewModelFactory
import com.example.airtelmanewakala.viewmodel.FloatInViewModel
import com.example.airtelmanewakala.viewmodel.FloatInViewModelFactory
import com.google.android.material.chip.Chip

class Balance: Fragment() {
    private lateinit var binding: FragmentBalanceBinding
    private  lateinit var balanceViewModel: BalanceViewModel
    private lateinit var adapter:RecyclerViewBalance
    var isAllFabsVisible: Boolean? = null
    //    private lateinit var wakalaMkuuViewModel: WakalaMkuuViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentBalanceBinding.inflate(inflater,container,false)
        val dao = context?.let { MoblieDatabase.getInstance(it).MobileDAO }
        val repository = dao?.let { MobileRepository(it) }
        val factory = repository?.let { BalanceViewModelFactory(it) }

        balanceViewModel= factory?.let { ViewModelProvider(this, it).get(BalanceViewModel::class.java) }!!
        context?.registerReceiver(balanceReceiver, IntentFilter("balanceReceiver"));
        binding.balanceRecyclerView.layoutManager=LinearLayoutManager(activity)
        binding.myBalanceViewModel=balanceViewModel
        binding.lifecycleOwner=this

        initRecyclerView()
        binding.insertBalanceFab.visibility= View.GONE;
        binding.downloadBalanceFab.visibility= View.GONE;

        isAllFabsVisible = false;

        binding.balanceFab.setOnClickListener(
            View.OnClickListener {
                isAllFabsVisible = if (!isAllFabsVisible!!) {
                    binding.insertBalanceFab.show()
                    binding.downloadBalanceFab.show()
                    true
                } else {
                    binding.insertBalanceFab.hide()
                    binding.downloadBalanceFab.hide()
                    false
                }
            })

        binding.downloadBalanceFab.setOnClickListener(
            View.OnClickListener {
//                wakalaMkuuViewModel.onGetButton()
//                wakalaViewModel.onGetButton()
                Toast.makeText(
                    context,
                    "Download balance",
                    Toast.LENGTH_SHORT
                ).show()
            })
        binding.insertBalanceFab .setOnClickListener(
            View.OnClickListener {
//                wakalaMkuuViewModel.onGetButton()
//                wakalaViewModel.onGetButton()
                val builder= context?.let { it1 -> AlertDialog.Builder(it1) }
                val inflater=layoutInflater
                val dialogLayout=inflater.inflate(R.layout.insert_balance_layout,null)

                val editText=dialogLayout.findViewById<EditText>(R.id.balance_amount)
                with(builder){
                    this?.setTitle("BALANCE")
                    this?.setPositiveButton("OK"){ dialog,which->
                        //insert in romm database
                        if (editText.text.isNotEmpty()&& editText.text.toString().toIntOrNull() != null){
                            balanceViewModel.insertBalance( Balance(
                                0,
                                editText.text.toString(),
                                "",
                                "",
                                0,
                                System.currentTimeMillis(),
                                System.currentTimeMillis()
                            )
                            )
                        }
                    }

                    this?.setNegativeButton("Cancel"){ dialog,which->

                    }
                    this?.setView(dialogLayout)
                    this?.show()
                }
//                Toast.makeText(
//                    context,
//                    "Download balance",
//                    Toast.LENGTH_SHORT
//                ).show()
            })

        return  binding.root
    }

    private fun initRecyclerView(){
        adapter= RecyclerViewBalance()
        binding.balanceRecyclerView.adapter=adapter
        displayTransactionList()
    }

    private fun displayTransactionList(){
        balanceViewModel.balance().observe(this, Observer {
            adapter.setList(it)
            adapter.notifyDataSetChanged()
        })
    }

    private val balanceReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.N)
        override fun onReceive(context: Context, intent: Intent) {
            return displayTransactionList()
        }
    }

    override fun onDestroyView() {
        Log.d("xyxv", "LifecycleFragment: onDestroyView() called")
        super.onDestroyView()
        context?.unregisterReceiver(balanceReceiver);
    }
}