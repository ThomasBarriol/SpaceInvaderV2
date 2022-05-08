package com.info.spaceinvaderv2

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import kotlin.system.exitProcess

class MainActivityMenu : AppCompatActivity(), View.OnClickListener {
    lateinit var buttonPlay: Button
    lateinit var buttonRegle : Button
    lateinit var buttonCredits : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)
        buttonPlay = findViewById(R.id.button_play)
        buttonRegle = findViewById(R.id.regle_button)
        buttonCredits = findViewById(R.id.button_credit)
        buttonPlay.setOnClickListener(this)
        buttonRegle.setOnClickListener(this)
        buttonCredits.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v?.id){
            R.id.button_play -> openMainActivity()
            R.id.regle_button -> openRegleActivity()
            R.id.button_credit -> openCredit()
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

    private fun openCredit(){
        class Credits: DialogFragment() {
            override fun onCreateDialog(bundle: Bundle?): Dialog {
                val builder = AlertDialog.Builder(getActivity())
                builder.setTitle(R.string.credits)
                builder.setMessage(R.string.developpeurs)
                builder.setPositiveButton("Retour") { _, _->}
                return builder.create()
            }
        }
        runOnUiThread(
            Runnable {
                val ft = supportFragmentManager.beginTransaction()
                val prev = supportFragmentManager.findFragmentByTag("dialog")
                if (prev != null) {
                    ft.remove(prev)
                }
                ft.addToBackStack(null)
                val credits = Credits()
                credits.setCancelable(false)
                credits.show(ft,"dialog")
            }

        )
    }


}