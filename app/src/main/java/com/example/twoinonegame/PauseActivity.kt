package com.example.twoinonegame

import GameViewModel
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider

class PauseActivity : AppCompatActivity() {

    private lateinit var score: TextView
    private lateinit var highScore: TextView
    private lateinit var backButton: ImageView
    private lateinit var restartButton: ImageView
    private lateinit var viewModel: GameViewModel
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pause)

        score = findViewById(R.id.scoreTextViewpage)
        highScore = findViewById(R.id.highScoreTextView)
        backButton = findViewById(R.id.backButton)
        restartButton = findViewById(R.id.restartButton)


        viewModel = ViewModelProvider(this).get(GameViewModel::class.java)


        sharedPreferences = getSharedPreferences("HighScore", Context.MODE_PRIVATE)


        val score = intent.getIntExtra("score", 0)
        viewModel.highScore = sharedPreferences.getInt("highScore", 0)


        viewModel.score = score


        updateUI()

        backButton.setOnClickListener {
            setResult(RESULT_OK)
            finish()
        }

        restartButton.setOnClickListener {

            viewModel.score = 0

            val intent = Intent(this, TapGameActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun updateUI() {
        score.text = "${viewModel.score}"
        highScore.text = "${viewModel.highScore}"
    }
}
