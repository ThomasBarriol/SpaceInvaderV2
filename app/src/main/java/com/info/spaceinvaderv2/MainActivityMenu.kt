package com.info.spaceinvaderv2

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivityMenu : AppCompatActivity(), View.OnClickListener {
    lateinit var buttonPlay: Button
    lateinit var buttonRegle : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)
        buttonPlay = findViewById(R.id.button_play)
        buttonRegle = findViewById(R.id.regle_button)
        buttonPlay.setOnClickListener(this)
        buttonRegle.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v?.id){
            R.id.button_play -> openMainActivity()
            R.id.regle_button -> openRegleActivity()
        }
    }

    private fun openMainActivity(){
        val intent = Intent(this, MainActivity::class.java).apply{}
        startActivity(intent)
    }

    private fun openRegleActivity(){
        val intent = Intent(this, ActivityRules::class.java).apply{}
        startActivity(intent)

    }


}