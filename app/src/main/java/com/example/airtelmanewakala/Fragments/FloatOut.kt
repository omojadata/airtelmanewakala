package com.example.airtelmanewakala.Fragments

import android.app.PendingIntent
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
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.airtelmanewakala.RecyclerView.RecyclerViewFloatOut
import com.example.airtelmanewakala.databinding.FragmentFloatOutBinding
import com.example.airtelmanewakala.db.FloatOut
import com.example.airtelmanewakala.db.MobileRepository
import com.example.airtelmanewakala.db.MoblieDatabase
import com.example.airtelmanewakala.generateFile
import com.example.airtelmanewakala.getDate
import com.example.airtelmanewakala.goToFileIntent
import com.example.airtelmanewakala.viewmodel.FloatOutViewModel
import com.example.airtelmanewakala.viewmodel.FloatOutViewModelFactory
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import com.google.android.material.chip.Chip
import java.io.File


class FloatOut : Fragment() {
    private lateinit var binding: FragmentFloatOutBinding
    private lateinit var floatOutViewModel: FloatOutViewModel
    private lateinit var adapter: RecyclerViewFloatOut
    val modifiedAt = System.currentTimeMillis()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentFloatOutBinding.inflate(inflater, container, false)
        val dao = context?.let { MoblieDatabase.getInstance(it).MobileDAO }
        val repository = dao?.let { MobileRepository(it) }
        val factory = repository?.let { FloatOutViewModelFactory(it) }
        floatOutViewModel =
            factory?.let { ViewModelProvider(this, it).get(FloatOutViewModel::class.java) }!!
        context?.registerReceiver(floatOutReceiver, IntentFilter("floatOutReceiver"))
        binding.floatoutRecyclerView.layoutManager = LinearLayoutManager(activity)
        binding.myFloatOutViewModel = floatOutViewModel
        binding.lifecycleOwner = this


//        initRecyclerView()
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

        binding.floatoutDownload.setOnClickListener(
            View.OnClickListener {
                exportDatabaseToCSVFile()
                Toast.makeText(
                    context,
                    "Download FloatOut",
                    Toast.LENGTH_SHORT
                ).show()
            })


        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun recover() {
        val chipText = binding.chipGroup.children.toList().filter { (it as Chip).isChecked }
            .joinToString(", ") {
                (it as Chip).text
            }
        when (chipText) {
            "Pending" -> {
                return filterRecyclerView(0)
            }
            "Ussd" -> {
                Log.i("xyxv", chipText)
                return filterRecyclerView(1)
            }
            "Done" -> {
                return filterRecyclerView(2)
            }
            "Invalid" -> {
                return filterRecyclerView(3)
            }
            "Change" -> {
                return filterRecyclerView(4)
            }
            else -> {
                return allRecyclerView()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun allRecyclerView() {
        adapter = RecyclerViewFloatOut { selectedItem: FloatOut -> allClicked(selectedItem) }
        binding.floatoutRecyclerView.adapter = adapter
        displayAllList()
    }

    private fun displayAllList() {
        floatOutViewModel.floatOut().observe(this, {
            adapter.setList(it)
            adapter.notifyDataSetChanged()
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun filterRecyclerView(status: Int) {
        when (status) {
            0 -> {
                Log.i("xyxv", "zero")
                adapter =
                    RecyclerViewFloatOut { selectedItem: FloatOut -> allClicked(selectedItem) }
                binding.floatoutRecyclerView.adapter = adapter
                displayFilterList(status)
            }
            1 -> {
                Log.i("xyxv", "one")
                adapter =
                    RecyclerViewFloatOut { selectedItem: FloatOut -> allClicked(selectedItem) }
                binding.floatoutRecyclerView.adapter = adapter
                displayFilterList(status)
            }
            2 -> {
                Log.i("xyxv", "two")
                adapter =
                    RecyclerViewFloatOut { selectedItem: FloatOut -> allClicked(selectedItem) }
                binding.floatoutRecyclerView.adapter = adapter
                displayFilterList(status)
            }
            3 -> {
                Log.i("xyxv", "three")
                adapter =
                    RecyclerViewFloatOut { selectedItem: FloatOut -> allClicked(selectedItem) }
                binding.floatoutRecyclerView.adapter = adapter
                displayFilterList(status)
            }
            4 -> {
                Log.i("xyxv", "four")
                adapter =
                    RecyclerViewFloatOut { selectedItem: FloatOut -> allClicked(selectedItem) }
                binding.floatoutRecyclerView.adapter = adapter
                displayFilterList(status)
            }
        }
    }

    private fun displayFilterList(status: Int) {
        floatOutViewModel.floatOutFilter(status).observe(this, {
            adapter.setList(it)
            adapter.notifyDataSetChanged()
        })
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


    private val floatOutReceiver: BroadcastReceiver = object : BroadcastReceiver() {

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
                "Ussd" -> {
                    Log.i("xyxv", chipText)
                    return filterRecyclerView(1)
                }
                "Done" -> {
                    Log.i("xyxv", chipText)
                    return filterRecyclerView(2)
                }
                "Invalid" -> {
                    Log.i("xyxv", chipText)
                    return filterRecyclerView(3)
                }
                "Change" -> {
                    Log.i("xyxv", chipText)
                    return filterRecyclerView(4)
                }
                else -> {
                    Log.i("xyxv", chipText)
                    return displayAllList()
                }
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun allClicked(floatOut: FloatOut) {
        if (floatOut.status == 0) {
            floatOutViewModel.doUssd(floatOut)
//            Toast.makeText(context, "Inatuma pesa ${floatOut.comment}", Toast.LENGTH_SHORT).show()
            //send USSD
        } else if (floatOut.status == 1) {
            //sendUSSD
            if (floatOutViewModel.modifiedAt >= floatOut.modifiedat + 300000) {
                floatOutViewModel.doUssd(floatOut)
            } else {
                val diff = floatOut.modifiedat + 300000
                Toast.makeText(
                    context,
                    "Wait till ${getDate(diff)}",
                    Toast.LENGTH_SHORT
                ).show()
            }

        } else if (floatOut.status == 4) {
            Toast.makeText(context, "Changes from ${floatOut.floatoutid}", Toast.LENGTH_SHORT)
                .show()
            updateWakala(floatOut)
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateWakala(floatOut: FloatOut) {
        if (floatOut.status == 4) {
            floatOutViewModel.floatOutUpdate(floatOut)
        }
    }

    fun exportDatabaseToCSVFile() {
        val csvFile = generateFile(context, "FloatOut.csv")
        if (csvFile != null) {
            exportMoviesWithDirectorsToCSVFile(csvFile)
            val intent = goToFileIntent(context, csvFile)
            startActivity(intent)
        } else {
            Toast.makeText(context, "Not Exported", Toast.LENGTH_SHORT)
        }
    }

    fun exportMoviesWithDirectorsToCSVFile(csvFile: File) {
        csvWriter().open(csvFile, append = false) {
            // Header
            writeRow(
                listOf(
                    "floatoutid",
                    "transid",
                    "amount",
                    "wakalaname",
                    "wakalacode",
                    "network",
                    "wakalaidkey",
                    "wakalamkuu",
                    "fromfloatinid",
                    "fromtransid",
                    "status",
                    "comment",
                    "networksms",
                    "wakalanumber",
                    "createdAt",
                    "modifiedat",
                    "madeatorder",
                    "madeatfloat",
                    "deletestatus"
                )
            )

            adapter.floatOutList.forEachIndexed { index, x ->
                writeRow(
                    listOf(
                        x.floatoutid,
                        x.transid,
                        x.amount,
                        x.wakalaname,
                        x.wakalacode,
                        x.network,
                        x.wakalaidkey,
                        x.wakalamkuu,
                        x.fromfloatinid,
                        x.fromtransid,
                        x.status,
                        x.comment,
                        x.networksms,
                        x.wakalanumber,
                        x.createdat,
                        x.modifiedat,
                        x.madeatorder,
                        x.madeatfloat,
                        x.deletestatus
                    )
                )
            }
        }
    }

    override fun onDestroyView() {
        Log.d("xyxv", "LifecycleFragment: onDestroyView() called")
        super.onDestroyView()
        context?.unregisterReceiver(floatOutReceiver);
    }
}