package com.example.airtelmanewakala.Fragments

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.icu.text.NumberFormat
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.airtelmanewakala.*
import com.example.airtelmanewakala.RecyclerView.RecyclerViewFloatIn
import com.example.airtelmanewakala.databinding.FragmentFloatInBinding
import com.example.airtelmanewakala.db.FloatIn
import com.example.airtelmanewakala.db.MobileRepository
import com.example.airtelmanewakala.db.MoblieDatabase
import com.example.airtelmanewakala.viewmodel.FloatInViewModel
import com.example.airtelmanewakala.viewmodel.FloatInViewModelFactory
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import com.google.android.material.chip.Chip
import com.romellfudi.ussdlibrary.USSDController
import java.io.File
import java.util.*


class FloatIn : Fragment() {
    private lateinit var binding: FragmentFloatInBinding
    private lateinit var floatInViewModel: FloatInViewModel
    private lateinit var adapter: RecyclerViewFloatIn

    val modifiedAt = System.currentTimeMillis()
//  private lateinit var receiver: BroadcastReceiver

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFloatInBinding.inflate(inflater, container, false)
        val dao = context?.let { MoblieDatabase.getInstance(it).MobileDAO }
        val repository = dao?.let { MobileRepository(it) }
        val factory = repository?.let { FloatInViewModelFactory(it) }
        floatInViewModel =
            factory?.let { ViewModelProvider(this, it).get(FloatInViewModel::class.java) }!!
        context?.registerReceiver(floatInReceiver, IntentFilter("floatInReceiver"))
        binding.floatinRecyclerView.layoutManager = LinearLayoutManager(activity)
        binding.myFloatInViewModel = floatInViewModel
        binding.lifecycleOwner = this

        allRecyclerView()
        Log.d("xyxv", "LifecycleFragment: onCreateView() called")
        binding.chipGroup.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == -1) {
                allRecyclerView()
            }
        }

        binding.zerobutton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Log.i("xyxv", "Zero checked")
                filterRecyclerView(0)
            }
        }

        binding.onebutton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Log.i("xyxv", "one checked")
                filterRecyclerView(1)
            }
        }

        binding.twobutton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Log.i("xyxv", "two checked")
                filterRecyclerView(2)
            }
        }

        binding.threebutton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Log.i("xyxv", "Three checked")
                filterRecyclerView(3)
            }
        }

        binding.fourbutton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Log.i("xyxv", "Three checked")
                filterRecyclerView(4)
            }
        }

        binding.fivebutton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Log.i("xyxv", "Three checked")
                filterRecyclerView(5)
            }
        }

        binding.floatinDownload.setOnClickListener(
            View.OnClickListener {
                exportFloatIn()
                Toast.makeText(
                    context,
                    "Download FloatIn",
                    Toast.LENGTH_SHORT
                ).show()
            })

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun allRecyclerView() {
        adapter = RecyclerViewFloatIn { selectedItem: FloatIn -> allClicked(selectedItem) }
        binding.floatinRecyclerView.adapter = adapter
        displayAllList()
    }

    private fun displayAllList() {
        floatInViewModel.floatIn().observe(this, {
            adapter.setList(it)
            adapter.notifyDataSetChanged()
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun filterRecyclerView(status: Int) {
        when (status) {
            0 -> {
                Log.i("xyxv", "zero")
                adapter = RecyclerViewFloatIn { selectedItem: FloatIn -> allClicked(selectedItem) }
                binding.floatinRecyclerView.adapter = adapter
                displayFilterList(status)
            }
            1 -> {
                Log.i("xyxv", "one")
                adapter = RecyclerViewFloatIn { selectedItem: FloatIn -> allClicked(selectedItem) }
                binding.floatinRecyclerView.adapter = adapter
                displayFilterList(status)
            }
            2 -> {
                Log.i("xyxv", "two")
                adapter = RecyclerViewFloatIn { selectedItem: FloatIn -> allClicked(selectedItem) }
                binding.floatinRecyclerView.adapter = adapter
                displayFilterList(status)
            }
            3 -> {
                Log.i("xyxv", "three")
                adapter = RecyclerViewFloatIn { selectedItem: FloatIn -> allClicked(selectedItem) }
                binding.floatinRecyclerView.adapter = adapter
                displayFilterList(status)
            }
            4 -> {
                Log.i("xyxv", "four")
                adapter = RecyclerViewFloatIn { selectedItem: FloatIn -> allClicked(selectedItem) }
                binding.floatinRecyclerView.adapter = adapter
                displayFilterList(status)
            }
            5 -> {
                Log.i("xyxv", "five")
                adapter = RecyclerViewFloatIn { selectedItem: FloatIn -> allClicked(selectedItem) }
                binding.floatinRecyclerView.adapter = adapter
                displayFilterList(status)
            }
        }
    }

    private fun displayFilterList(status: Int) {

        floatInViewModel.floatInFilter(status).observe(this, {
            adapter.setList(it)
            adapter.notifyDataSetChanged()
//            adapter.setHasStableIds(true)
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun recover() {
        val chipText = binding.chipGroup.children.toList().filter { (it as Chip).isChecked }
            .joinToString(", ") {
                (it as Chip).text
            }
        when (chipText) {
            "Pending" -> {
                Log.i("xyxv", chipText)
                return filterRecyclerView(0)
            }
            "Done" -> {
                Log.i("xyxv", chipText)
                return filterRecyclerView(1)
            }
            "Large" -> {
                Log.i("xyxv", chipText)
                return filterRecyclerView(2)
            }
            "Invalid" -> {
                Log.i("xyxv", chipText)
                return filterRecyclerView(3)
            }
            "Late" -> {
                Log.i("xyxv", chipText)
                return filterRecyclerView(4)
            }
            "Change" -> {
                Log.i("xyxv", chipText)
                return filterRecyclerView(5)
            }
            else -> {
                Log.i("xyxv", chipText)
                return allRecyclerView()
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun allClicked(floatIn: FloatIn) {
        when (floatIn.status) {
            0 -> {
                sendTextWakala(floatIn)
                Toast.makeText(context, "This zero ${floatIn.comment}", Toast.LENGTH_SHORT).show()
                //sned text to wakala for order
            }
            1 -> {
                sendTextWakalaMkuu(floatIn)
                Toast.makeText(context, "This one ${floatIn.comment}", Toast.LENGTH_SHORT).show()
                //send text to wakla mkuu
            }
            2 -> {
                sendTextWakala(floatIn)
                Toast.makeText(context, "This two ${floatIn.comment}", Toast.LENGTH_SHORT).show()
                //send text to wakala for large order/lateorder
            }
            3 -> {
                getFloatIn(floatIn.networksms)
                Toast.makeText(context, "This three ${floatIn.comment}", Toast.LENGTH_SHORT).show()
            }
            4 -> {
                sendTextWakala(floatIn)
                Toast.makeText(context, "This four ${floatIn.comment}", Toast.LENGTH_SHORT).show()
                //send text to wakalamkuu
            }
            5 -> {
                updateWakala(floatIn)
                Toast.makeText(context, "This five ${floatIn.comment}", Toast.LENGTH_SHORT).show()
                //send text to wakalamkuu
            }
        }
    }

    private fun sendTextWakalaMkuu(floatIn: FloatIn) {
        when (floatIn.status) {
            1 -> {
                //"WAKALAMKUU $firstword $amount $towakalacode [$towakalaname] $fromfloatinid $fromtransid $wakalano $fromnetwork $wakalaidkey WAKALAMKUU"
                val sendSms =
                    "WAKALAMKUU ${floatIn.wakalaorder} ${floatIn.amount} ${floatIn.towakalacode} ${"[" + floatIn.towakalaname + "]"} ${floatIn.floatinid} ${floatIn.transid} ${floatIn.wakalacontact} ${floatIn.fromnetwork} ${floatIn.wakalaidkey} WAKALAMKUU"
                sendSms(floatIn.wakalamkuunumber, sendSms)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendTextWakala(floatIn: FloatIn) {
        if (floatIn.status == 0) {
            //pending
            val amounting = getComma(floatIn.amount)
            val sendSms =
                "Kiasi: Tsh $amounting, Mtandao: $fromnetwork itumwe wapi? Jibu Tigopesa, Mpesa au Halopesa"
            sendSms(floatIn.wakalacontact, sendSms)
        } else if (floatIn.status == 2 ) {
            //large
            val amounting = getComma(floatIn.amount)
            val maxamount = getComma(floatIn.maxamount)

            if(floatIn.comment == "LARGE/WAIT"){
                floatInViewModel.uFloatInLarge(floatIn.floatinid,"LARGE",modifiedAt)

                val sendSms =
                    "Kiwango chako cha juu ni Tsh $maxamount. Kiasi: Tsh $amounting, Mtandao: $fromnetwork itumwe wapi? Jibu Tigopesa, Mpesa au Halopesa."
                sendSms(floatIn.wakalacontact, sendSms)
            }else if(floatIn.comment=="LARGE"){
                val sendSms =
                    "Kiwango chako cha juu ni Tsh $maxamount.Kiasi: Tsh $amounting, Mtandao: $fromnetwork itumwe wapi? Jibu Tigopesa, Mpesa au Halopesa."
                sendSms(floatIn.wakalacontact, sendSms)
            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateWakala(floatIn: FloatIn) {
        if (floatIn.status == 5) {
            floatInViewModel.floatInUpdate(floatIn)
        }
    }


    fun exportFloatIn() {
        val csvFile = generateFile(context, "FloatIn.csv")
        if (csvFile != null) {
            exportRoomToFile(csvFile)
            val intent = goToFileIntent(context, csvFile)
            startActivity(intent)
        } else {
            Toast.makeText(context, "Not Exported", Toast.LENGTH_SHORT)
        }
    }

    fun exportRoomToFile(csvFile: File) {

        csvWriter().open(csvFile, append = false) {
            // Header
            writeRow(
                listOf(
                    "transid",
                    "amount",
                    "balance",
                    "wakalaidkey",
                    "status",
                    "fromnetwork",
                    "wakalaorder",
                    "comment",
                    "fromwakalacode",
                    "towakalacode",
                    "wakalamkuunumber",
                    "fromwakalaname",
                    "towakalaname",
                    "wakalacontact",
                    "networksms",
                    "createdat",
                    "modifiedat",
                    "madeatfloat",
                    "madeatorder",
                    "deletestatus"
                )
            )

            adapter.floatInList.forEachIndexed { _, x ->
                writeRow(
                    listOf(
                        x.transid,
                        x.amount,
                        x.balance,
                        x.wakalaidkey,
                        x.status,
                        x.fromnetwork,
                        x.wakalaorder,
                        x.comment,
                        x.fromwakalacode,
                        x.towakalacode,
                        x.wakalamkuunumber,
                        x.fromwakalaname,
                        x.towakalaname,
                        x.wakalacontact,
                        x.networksms,
                        x.createdat,
                        x.modifiedat,
                        x.madeatfloat,
                        x.madeatorder,
                        x.deletestatus
                    )
                )
            }
        }
    }


    private val floatInReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onReceive(context: Context, intent: Intent) {
            val chipText = binding.chipGroup.children.toList().filter { (it as Chip).isChecked }
                .joinToString(", ") {
                    (it as Chip).text
                }
            when (chipText) {
                "Pending" -> {
                    Log.i("xyxv", chipText)
                    return filterRecyclerView(0)
                }
                "Done" -> {
                    Log.i("xyxv", chipText)
                    return filterRecyclerView(1)
                }
                "Large" -> {
                    Log.i("xyxv", chipText)
                    return filterRecyclerView(2)
                }
                "Invalid" -> {
                    Log.i("xyxv", chipText)
                    return filterRecyclerView(3)
                }
                "Late" -> {
                    Log.i("xyxv", chipText)
                    return filterRecyclerView(4)
                }
                "Change" -> {
                    Log.i("xyxv", chipText)
                    return filterRecyclerView(5)
                }
                else -> {
                    Log.i("xyxv", chipText)
                    return displayAllList()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStart() {
        Log.d("xyxv", "LifecycleFragment: onStart() called")
        super.onStart()
        recover()
    }

    override fun onResume() {
        Log.d("xyxv", "LifecycleFragment: onResume() called")
        super.onResume()
    }

    override fun onPause() {
        Log.d("xyxv", "LifecycleFragment: onPause() called")
        super.onPause()
    }

    override fun onStop() {
        Log.d("xyxv", "LifecycleFragment: onStop() called")
        super.onStop()
    }

    override fun onDestroy() {
        Log.d("xyxv", "LifecycleFragment: onDestroy() called")
        super.onDestroy()

    }

    override fun onDestroyView() {
        Log.d("xyxv", "LifecycleFragment: onDestroyView() called")
        super.onDestroyView()
        context?.unregisterReceiver(floatInReceiver)
    }
}