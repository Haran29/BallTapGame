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

class ScoreActivity : AppCompatActivity() {

    private lateinit var scoreTextView: TextView
    private lateinit var highScoreTextView: TextView
    private lateinit var restartButton: ImageView
    private lateinit var viewModel: GameViewModel
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_score)

        scoreTextView = findViewById(R.id.scoreTextViewpage)
        highScoreTextView = findViewById(R.id.highScoreTextView)
        restartButton = findViewById(R.id.restartButton)

        // Initialize ViewModel
        viewModel = ViewModelProvider(this).get(GameViewModel::class.java)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("HighScore", Context.MODE_PRIVATE)

        // Get score and high score from intent and SharedPreferences
        val score = intent.getIntExtra("score", 0)
        viewModel.highScore = sharedPreferences.getInt("highScore", 0)

        // Update ViewModel score
        viewModel.score = score

        // Update UI with ViewModel data
        updateUI()

        restartButton.setOnClickListener {
            // Reset ViewModel score
            viewModel.score = 0

            // Navigate back to TapGameActivity
            val intent = Intent(this, TapGameActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun updateUI() {
        scoreTextView.text = "${viewModel.score}"
        highScoreTextView.text = "${viewModel.highScore}"
    }
}
