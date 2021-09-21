package com.example.airtelmanewakala

//import android.databinding.DataBindingUtil
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import androidx.viewpager2.widget.ViewPager2
import com.example.airtelmanewakala.Adapter.ViewPageAdapter
import com.example.airtelmanewakala.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.romellfudi.ussdlibrary.USSDController
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
//    private hassan manengelo lateinit var wakalaMkuuViewModel: WakalaMkuuViewModel
  lateinit var dataStorePreference: DataStorePreference
   var auto=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        binding= DataBindingUtil.setContentView(this,R.layout.activity_main)
        binding= ActivityMainBinding.inflate(layoutInflater)
        val view= binding.root
        setContentView(view)

        val tabLayout=findViewById<TabLayout>(R.id.tab_layout)
        val viewPager2=findViewById<ViewPager2>(R.id.view_pager_2).apply {
            isUserInputEnabled=false
        }

        dataStorePreference = DataStorePreference(this)
        observeData()
        val adapter= ViewPageAdapter(supportFragmentManager,lifecycle)

        if(ActivityCompat.checkSelfPermission(this, arrayOf(
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.SEND_SMS,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.SYSTEM_ALERT_WINDOW,
                Manifest.permission.READ_SMS,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION
            ).toString()
            )!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, arrayOf(
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.SEND_SMS,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.SYSTEM_ALERT_WINDOW,
                Manifest.permission.READ_SMS,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
               )
                ,111)
        }
        USSDController.verifyAccesibilityAccess(this)
        USSDController.verifyOverLay(this)

        binding.autoLay.setOnCheckedChangeListener { buttonView, isChecked ->
            GlobalScope.launch {
                if (isChecked){
                    dataStorePreference.saveAutoMode(
                        true
                    )
                }else{
                    dataStorePreference.saveAutoMode(
                        false
                    )
                }
            }
        }

        viewPager2.adapter=adapter
        TabLayoutMediator(tabLayout,viewPager2){tab,position->
            when(position){
                0->{
                    tab.text="Wak"
                }
                1->{
                    tab.text="W/M"
                }
                2->{
                    tab.text="F/0"
                }
                3->{
                    tab.text="F/I"
                }
                4->{
                    tab.text="Bal"
                }
            }

        }.attach()
    }
    private fun observeData() {
        dataStorePreference.autoMode.asLiveData().observe(this, {
            binding.autoLay.text=if(it){"AutoUSSD is ON"}else{"AutoUSSD is OFF"}
            binding.autoLay.isChecked= it
        })
    }
}