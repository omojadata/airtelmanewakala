package com.example.airtelmanewakala.Fragments

import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.airtelmanewakala.RecyclerView.RecyclerViewWakala
import com.example.airtelmanewakala.databinding.FragmentWakalaBinding
import com.example.airtelmanewakala.db.CSVWriter
import com.example.airtelmanewakala.db.MobileRepository
import com.example.airtelmanewakala.db.MoblieDatabase
import com.example.airtelmanewakala.db.Wakala
import com.example.airtelmanewakala.generateFile
import com.example.airtelmanewakala.goToFileIntent
import com.example.airtelmanewakala.viewmodel.WakalaViewModel
import com.example.airtelmanewakala.viewmodel.WakalaViewModelFactory
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import com.romellfudi.ussdlibrary.USSDController
import java.io.File
import java.io.FileWriter


class Wakala : Fragment() {
    private lateinit var binding: FragmentWakalaBinding
    private lateinit var wakalaViewModel: WakalaViewModel
    val modifiedAt = System.currentTimeMillis()
    private lateinit var adapter :RecyclerViewWakala
    var isAllFabsVisible: Boolean? = null

//    private var wakala: ArrayList<Wakala> = arrayListOf()
//    private var matchedWakala: ArrayList<Wakala> = arrayListOf()
//    private var wakalaAdapter :RecyclerViewWakala=RecyclerViewWakala{ selectedItem: Wakala ->
//        listItemClicked(
//            selectedItem
//        )
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding= FragmentWakalaBinding.inflate(inflater, container, false)
//       binding =DataBindingUtil.inflate(inflater,R.layout.fragment_wakala, container, false)
        val dao = context?.let { MoblieDatabase.getInstance(it).MobileDAO }
        val repository = dao?.let { MobileRepository(it) }
        val factory = repository?.let { WakalaViewModelFactory(it) }
        wakalaViewModel= factory?.let { ViewModelProvider(this, it).get(WakalaViewModel::class.java) }!!
        binding.wakalaRecyclerView.layoutManager= LinearLayoutManager(activity)
        binding.myWakalaViewModel=wakalaViewModel
        binding.lifecycleOwner=this

        allRecyclerView()

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            @RequiresApi(Build.VERSION_CODES.N)
            override fun onQueryTextSubmit(query: String): Boolean {
                searchRecyclerView(query)
                return true
            }

            @RequiresApi(Build.VERSION_CODES.N)
            override fun onQueryTextChange(newText: String): Boolean {
                searchRecyclerView(newText)
                return false
            }
        })

        binding.searchView.setOnCloseListener {
            allRecyclerView()
            false
        }


//        // This FAB button is the Parent
//        mAddFab = binding.addFab;
//        // FAB button
//        mAddAlarmFab = findViewById(R.id.add_alarm_fab);
//        mAddPersonFab = findViewById(R.id.add_person_fab);
//        // Also register the action name text, of all the FABs.
//        addAlarmActionText = findViewById(R.id.add_alarm_action_text);
//        addPersonActionText = findViewById(R.id.add_person_action_text);

        binding.listWakalaText.text=adapter.itemCount.toString() +" "+" WAKALA IN TOTAL"
        binding.listWakalaText.visibility=View.GONE;

        binding.refreshWakalaFab.visibility = View.GONE;
        binding.downloadWakalaFab.visibility=View.GONE;
        binding.refreshWakalaText.visibility=View.GONE;
        binding.downloadWakalaText.visibility=View.GONE;

        isAllFabsVisible = false;
        binding.wakalaFab.setOnClickListener(
            View.OnClickListener {
                isAllFabsVisible = if (!isAllFabsVisible!!) {
                    binding.refreshWakalaFab.show()
                    binding.downloadWakalaFab.show()
                    binding.listWakalaText.visibility=View.GONE;
                    binding.refreshWakalaText.visibility=View.GONE
                    binding.downloadWakalaText.visibility=View.GONE
                    true
                } else {
                    binding.refreshWakalaFab.hide()
                    binding.downloadWakalaFab.hide()
                    binding.listWakalaText.visibility=View.GONE;
                    binding.refreshWakalaText.visibility=View.GONE
                    binding.downloadWakalaText.visibility=View.GONE
                    false
                }
            })

        binding.refreshWakalaFab.setOnClickListener(
            View.OnClickListener {
                wakalaViewModel.onGetButton()
                Toast.makeText(
                    context,
                    "Add Wakala",
                    Toast.LENGTH_SHORT
                ).show()
            })

        binding.downloadWakalaFab.setOnClickListener(
            View.OnClickListener {
                exportFile()
                Toast.makeText(
                    context,
                    "Download Wakala",
                    Toast.LENGTH_SHORT
                ).show()
            })

        return  binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun allRecyclerView(){
        adapter= RecyclerViewWakala { selectedItem: Wakala -> listItemClicked(selectedItem) }
        binding.wakalaRecyclerView.adapter=adapter
        displayAllList()
    }

    private fun displayAllList(){
        wakalaViewModel.wakala().observe(this, Observer {
            adapter.setList(it)
            adapter.notifyDataSetChanged()
        })
    }

    private fun listItemClicked(wakala: Wakala){
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:${wakala.contact}")
        startActivity(intent)
//        Toast.makeText(activity, "Hola ratita ${wakala.tigoname}", Toast.LENGTH_SHORT).show()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun searchRecyclerView(text: String){
        Log.i("xyxv","five")
        adapter= RecyclerViewWakala {selectedItem: Wakala -> listItemClicked(selectedItem)  }
        binding.wakalaRecyclerView.adapter =adapter
        displaySearchList(text)
        binding.searchView.isSubmitButtonEnabled=true
    }

    private fun displaySearchList(text: String){
        wakalaViewModel.wakalaSearch(text).observe(this, Observer {
            adapter.setList(it)
            adapter.notifyDataSetChanged()
        })
    }

    fun exportFile() {
        val csvFile =  generateFile(context, "Wakala.csv")
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
            writeRow(listOf("id", "maxAmount", "contact","status","airtelmoney","airtel","mpesa","vodacom","tigopesa","tigo","halopesa","halotel","tpesa","ttcl"))

            adapter.wakalaList.forEachIndexed { index, wakala ->
                writeRow(listOf(wakala.wakalaid,wakala.maxamount,wakala.contact,wakala.status,wakala.airtelmoney,wakala.airtelname,wakala.mpesa,wakala.vodaname,wakala.tigopesa,wakala.tigoname,wakala.halopesa,wakala.haloname,wakala.tpesa,wakala.ttclname))
            }
        }
    }


}