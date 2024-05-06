package com.example.twoinonegame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView

class OnBoardTapGame : AppCompatActivity() {
    private lateinit var  start: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_board_tap_game)

        start= findViewById(R.id.start_btn)

        start.setOnClickListener {
            val intent = Intent(this,TapGameActivity::class.java)
            startActivity(intent)
            finish()
    }}
}