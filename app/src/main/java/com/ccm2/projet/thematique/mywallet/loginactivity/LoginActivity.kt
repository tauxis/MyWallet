package com.ccm2.projet.thematique.mywallet.loginactivity


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ccm2.projet.thematique.mywallet.R
import com.ccm2.projet.thematique.mywallet.googleactivity.GoogleDriveConfig
import com.ccm2.projet.thematique.mywallet.googleactivity.GoogleDriveService
import com.ccm2.projet.thematique.mywallet.googleactivity.ServiceListener
import com.ccm2.projet.thematique.mywallet.menu.MenuActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.logout
import java.io.File

class LoginActivity : AppCompatActivity(), ServiceListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        //1
        val config = GoogleDriveConfig(
            getString(R.string.source_google_drive),
            GoogleDriveService.documentMimeTypes
        )
        googleDriveService = GoogleDriveService(this, config)
        //2
        googleDriveService.serviceListener = this
        //3
        googleDriveService.checkLoginStatus()
        //4
        login.setOnClickListener {
            googleDriveService.auth()
        }
        logout.setOnClickListener {
            googleDriveService.logout()
            state = GoogleDriveService.ButtonState.LOGGED_OUT
            setButtons()
        }
//        start.setOnClickListener {
//            googleDriveService.pickFiles(null)
//        }
        //5
        setButtons()

        gotomenu.setOnClickListener {
            goToMenuActivity();
        }
    }


    private lateinit var googleDriveService: GoogleDriveService
    var state = GoogleDriveService.ButtonState.LOGGED_OUT

    fun setButtons() {
        when (state) {
            GoogleDriveService.ButtonState.LOGGED_OUT -> {
                statusL.text = getString(R.string.status_logged_out)
                //start.isEnabled = false
                gotomenu.isEnabled = false
                logout.isEnabled = false
                login.isEnabled = true
            }

            else -> {
                statusL.text = getString(R.string.status_logged_in)
                //start.isEnabled = true
                gotomenu.isEnabled = true
                logout.isEnabled = true
                login.isEnabled = false
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        googleDriveService.onActivityResult(requestCode, resultCode, data)
    }

    override fun loggedIn() {
        state = GoogleDriveService.ButtonState.LOGGED_IN
        setButtons()
    }
    override fun fileDownloaded(file: File) {}


    override fun cancelled() {
        Snackbar.make(login_layout, R.string.status_user_cancelled, Snackbar.LENGTH_LONG).show()
    }

    override fun handleError(exception: Exception) {
        val errorMessage = getString(R.string.status_error, exception.message)
        Snackbar.make(login_layout, errorMessage, Snackbar.LENGTH_LONG).show()
    }


    private fun goToMenuActivity() {
        startActivity(Intent(this, MenuActivity::class.java))
    }


}

