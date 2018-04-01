package com.example.android.maiauto

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import java.util.*

class splash_activity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_activity)
        Timer().schedule(object : TimerTask(){
            override fun run() {
                val intent=Intent(this@splash_activity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }


        }, 4200L)
    }
}
