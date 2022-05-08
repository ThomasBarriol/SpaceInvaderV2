package com.info.spaceinvaderv2

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class ActivityRules: AppCompatActivity(), View.OnClickListener {

    lateinit var button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rules)
        button = findViewById(R.id.button1)
        button.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        val intent = Intent(this, MainActivityMenu::class.java).apply{}
        startActivity(intent)
    }

}