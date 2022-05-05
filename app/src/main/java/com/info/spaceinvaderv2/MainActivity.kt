package com.info.spaceinvaderv2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    private var SpaceView : SpaceView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        SpaceView = findViewById<SpaceView>(R.id.vMain)
    }

    override fun onPause() {
        super.onPause()
        SpaceView?.pause()
    }

    override fun onResume() {
        super.onResume()
        SpaceView?.resume()
    }
}