package com.example.utsavlive

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.example.utsavlive.databinding.ActivityMainBinding
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private var userRole="0"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       binding=DataBindingUtil. setContentView(this,R.layout.activity_main)
        requestPermission()
        with(binding){
            submit.setOnClickListener {
                userRole = if (binding.host.isChecked){
                    "0"
                } else{
                    "1"
                }
                val intent=Intent(this@MainActivity,LiveActivity::class.java)
                intent.putExtra("channelName",binding.channel.text.toString())
                intent.putExtra("userRole",userRole)
                startActivity(intent)
            }

        }
    }
    private fun requestPermission(){
        ActivityCompat.requestPermissions(
            this, arrayOf(
                android.Manifest.permission.CAMERA,android.Manifest.permission.RECORD_AUDIO),22)
    }
}