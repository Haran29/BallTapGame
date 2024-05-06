package com.example.twoinonegame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper

class MainActivity : AppCompatActivity() {
    private val DELAYTIME: Long = 3000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Handler(Looper.getMainLooper()).postDelayed({

            val intent = Intent(this,OnBoardTapGame::class.java)
            startActivity(intent)
            finish()
        }, DELAYTIME)
    }
}