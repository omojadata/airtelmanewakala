package com.example.airtelmanewakala.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.airtelmanewakala.RecyclerView.RecyclerViewWakalaMkuu
import com.example.airtelmanewakala.databinding.FragmentWakalaMkuuBinding
import com.example.airtelmanewakala.db.MoblieDatabase
import com.example.airtelmanewakala.db.MobileRepository
import com.example.airtelmanewakala.db.WakalaMkuu
import com.example.airtelmanewakala.viewmodel.WakalaMkuuViewModel
import com.example.airtelmanewakala.viewmodel.WakalaMkuuViewModelFactory

class WakalaMkuu : Fragment() {
    private lateinit var binding: FragmentWakalaMkuuBinding
    private lateinit var wakalaMkuuViewModel: WakalaMkuuViewModel

    var isAllFabsVisible: Boolean? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding= FragmentWakalaMkuuBinding.inflate(inflater, container, false)
        val dao = context?.let { MoblieDatabase.getInstance(it).MobileDAO }
        val repository = dao?.let { MobileRepository(it) }
        val factory = repository?.let { WakalaMkuuViewModelFactory(it, activity!!.application) }
        wakalaMkuuViewModel= factory?.let { ViewModelProvider(this, it).get(WakalaMkuuViewModel::class.java) }!!
        binding.myWakalaMkuuViewModel=wakalaMkuuViewModel
        binding.lifecycleOwner=this

        initRecyclerView()

        binding.refreshWakalamkuuFab.visibility = View.GONE;
        binding.refreshWakalamkuuText.visibility=View.GONE;

        isAllFabsVisible = false;

        binding.wakalamkuuFab.setOnClickListener(
            View.OnClickListener {
                isAllFabsVisible = if (!isAllFabsVisible!!) {
                    binding.refreshWakalamkuuFab.show()
                    binding.refreshWakalamkuuText.visibility=View.GONE
                    true
                } else {
                    binding.refreshWakalamkuuFab.hide()
                    binding.refreshWakalamkuuText.visibility=View.GONE
                    false
                }
            })

        binding.refreshWakalamkuuFab.setOnClickListener(
            View.OnClickListener {
                wakalaMkuuViewModel.onGetButton()
//                wakalaViewModel.onGetButton()
                Toast.makeText(
                    context,
                    "Wakala Mkuu Added",
                    Toast.LENGTH_SHORT
                ).show()
            })

        return  binding.root
    }


    fun initRecyclerView(){
        binding.wakalamkuuRecyclerView.layoutManager=LinearLayoutManager(context)
        displayTransactionList()
    }

    private fun displayTransactionList(){
        wakalaMkuuViewModel.wakalaMkuu.observe(this, Observer {
            binding.wakalamkuuRecyclerView.adapter=RecyclerViewWakalaMkuu(it,{selectedItem:WakalaMkuu->listItemClicked(selectedItem)})
        })
    }

    private fun listItemClicked(wakalaMkuu: WakalaMkuu){

        Toast.makeText(activity, "Hola ratita ${wakalaMkuu.wakalamkuuid}", Toast.LENGTH_SHORT).show()

    }
}