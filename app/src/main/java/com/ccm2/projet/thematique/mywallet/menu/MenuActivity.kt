package com.ccm2.projet.thematique.mywallet.menu

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ccm2.projet.thematique.mywallet.R
import com.ccm2.projet.thematique.mywallet.photoactivity.PhotoActivity
import kotlinx.android.synthetic.main.activity_menu.*

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        addPhoto.setOnClickListener() {
            goToAppareilPhoto();
        }
    }

    private fun goToAppareilPhoto() {
        startActivity(Intent(this, PhotoActivity::class.java));
    }
}