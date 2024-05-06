package com.example.twoinonegame

import GameViewModel
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import kotlin.random.Random

class TapGameActivity : AppCompatActivity() {
    private lateinit var container: ViewGroup
    private lateinit var score: TextView
    private lateinit var timer: TextView
    private lateinit var viewModel: GameViewModel
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sound: MediaPlayer
    private lateinit var countdownTimer: CountDownTimer

    private var gameActive = true
    private var comboCounter: Int = 0
    private var speed= 1.0f
    private var initialTimerDuration = 110000L
    private var scoreMultiplier = 1

    private val powerball_type = 3

    companion object {
        private const val REQUEST_PAUSE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tap_game)

        container = findViewById(R.id.container)
        score = findViewById(R.id.scoreTextView)
        timer = findViewById(R.id.Timmer)

        viewModel = ViewModelProvider(this).get(GameViewModel::class.java)

        sharedPreferences = getSharedPreferences("HighScore", Context.MODE_PRIVATE)
        viewModel.highScore = sharedPreferences.getInt("highScore", 0)

        sound = MediaPlayer.create(this, R.raw.tap_sound)

        updateUI()

        startTimer()

        val pauseButton: ImageView = findViewById(R.id.pause)
        pauseButton.setOnClickListener {
            gameActive = !gameActive
            if (gameActive) {
                startTimer()
            } else {
                val intent = Intent(this, PauseActivity::class.java)
                intent.putExtra("score", viewModel.score)
                startActivityForResult(intent, REQUEST_PAUSE)
            }
        }

        container.setOnClickListener {
            if (gameActive) {
                comboCounter = 0
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_PAUSE && resultCode == RESULT_OK) {
            gameActive = true
            startTimer()
        }
    }

    private fun startTimer() {
        countdownTimer = object : CountDownTimer(initialTimerDuration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                if (gameActive) {
                    val minutes = millisUntilFinished / 60000
                    val seconds = (millisUntilFinished % 60000) / 1000
                    timer.text = String.format("%02d:%02d", minutes, seconds)

                    generateBall()
                    moveBalls()
                    speed += 0.01f
                    initialTimerDuration -= 100
                }
            }

            override fun onFinish() {
                if (gameActive) {
                    endGame()
                }
            }
        }

        countdownTimer.start()
    }

    private fun generateBall() {
        val ballImageView = ImageView(this)
        val ballType = Random.nextInt(0, 4)

        when (ballType) {
            0 -> ballImageView.setImageResource(R.drawable.ball)
            1 -> ballImageView.setImageResource(R.drawable.ball2)
            2 -> ballImageView.setImageResource(R.drawable.ball3)
            3 -> ballImageView.setImageResource(R.drawable.power_up_ball)
        }

        val layoutParams = container.layoutParams as ViewGroup.MarginLayoutParams
        val totalMargins = layoutParams.leftMargin + layoutParams.rightMargin
        val availableWidth = container.width - totalMargins

        if (availableWidth <= 0) {
            Log.e("TapGameActivity", "Invalid available width: $availableWidth")
            return
        }

        val randomX = Random.nextInt(0, availableWidth)

        val params = ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.setMargins(randomX, 0, 0, 0)

        ballImageView.layoutParams = params
        container.addView(ballImageView)

        ballImageView.setOnClickListener {
            val ballValue = when (ballType) {
                0 -> 2
                1 -> 5
                2 -> 10
                3 -> 0 // Power-Up ball
                else -> 0
            }

            if (ballType == powerball_type) {
                activatePowerUp()
                playTapSound()
            } else {
                comboCounter++
                if (comboCounter % 5 == 0) {
                    scoreMultiplier++
                }
                viewModel.score += ballValue * scoreMultiplier
                playTapSound()
            }

            updateUI()
            container.removeView(it)
        }
    }

    private fun moveBalls() {
        val speedIncrease = 1.1f
        for (i in 0 until container.childCount) {
            val ball = container.getChildAt(i) as ImageView
            ball.translationY += 150f * speed * speedIncrease

            if (ball.translationY >= container.height) {
                container.removeView(ball)
                updateUI()
                endGame()
                return
            }
        }
    }

    private fun endGame() {

        Toast.makeText(this@TapGameActivity, "Game Over", Toast.LENGTH_SHORT).show()
        gameActive = false
        if (viewModel.score > viewModel.highScore) {
            viewModel.highScore = viewModel.score
            sharedPreferences.edit().putInt("highScore", viewModel.highScore).apply()
        }
        playGameOverSound()
        updateUI()

        val intent = Intent(this, ScoreActivity::class.java)
        intent.putExtra("score", viewModel.score)
        startActivity(intent)
        finish()
    }

    private fun updateUI() {
        score.text = "${viewModel.score}"

    }

    private fun playTapSound() {
        if (sound.isPlaying) {
            sound.stop()
            sound.release()
        }
        sound= MediaPlayer.create(this, R.raw.tap_sound)
        sound.start()
    }

    private fun playGameOverSound() {
        if ( sound.isPlaying) {
            sound.stop()
            sound.release()
        }
        sound = MediaPlayer.create(this, R.raw.game_over)
        sound.start()
    }

    private fun activatePowerUp() {
        when (Random.nextInt(0, 3)) {
            0 -> {
                speed -= 0.1f
                Toast.makeText(this, "Slow Motion Activated!", Toast.LENGTH_SHORT).show()
            }
            1 -> {
                viewModel.score += 50
                Toast.makeText(this, "Extra Points!", Toast.LENGTH_SHORT).show()
            }
            2 -> {
                initialTimerDuration += 5000
                Toast.makeText(this, "Extra Time!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
