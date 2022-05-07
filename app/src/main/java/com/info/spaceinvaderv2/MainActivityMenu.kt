package com.info.spaceinvaderv2

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivityMenu : AppCompatActivity(), View.OnClickListener {
    lateinit var buttonPlay: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)
        buttonPlay = findViewById(R.id.button_play)
        buttonPlay.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        openMainActivity()
    }

    private fun openMainActivity(){
        val intent = Intent(this, MainActivity::class.java).apply{}
        startActivity(intent)
    }
}