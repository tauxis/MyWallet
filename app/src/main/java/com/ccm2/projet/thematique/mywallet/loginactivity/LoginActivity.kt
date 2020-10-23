package com.ccm2.projet.thematique.mywallet.loginactivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ccm2.projet.thematique.mywallet.R
import com.ccm2.projet.thematique.mywallet.menu.MenuActivity
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        login.setOnClickListener {
            goToMenuActivity();
        }
    }

    private fun goToMenuActivity() {
        startActivity(Intent(this, MenuActivity::class.java))
    }
}