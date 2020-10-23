package com.ccm2.projet.thematique.mywallet.loginactivity


import android.content.Intent
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import com.ccm2.projet.thematique.mywallet.R
import com.ccm2.projet.thematique.mywallet.googleactivity.GoogleDriveConfig
import com.ccm2.projet.thematique.mywallet.googleactivity.GoogleDriveService
import com.ccm2.projet.thematique.mywallet.googleactivity.GoogleDriveService.Companion.REQUEST_CODE_OPEN_ITEM
import com.ccm2.projet.thematique.mywallet.googleactivity.GoogleDriveService.Companion.REQUEST_CODE_SIGN_IN
import com.ccm2.projet.thematique.mywallet.googleactivity.ServiceListener
import com.ccm2.projet.thematique.mywallet.menu.MenuActivity
import com.google.android.material.snackbar.Snackbar

import kotlinx.android.synthetic.main.activity_login.*
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
        start.setOnClickListener {
            googleDriveService.pickFiles(null)
        }
        logout.setOnClickListener {
            googleDriveService.logout()
            state = GoogleDriveService.ButtonState.LOGGED_OUT
            setButtons()
        }

        //5
        setButtons()
        gotomenu.setOnClickListener {
            goToMenuActivity();
        }
    }
    private lateinit var googleDriveService: GoogleDriveService
    private var state = GoogleDriveService.ButtonState.LOGGED_OUT

    private fun setButtons() {
        when (state) {
            GoogleDriveService.ButtonState.LOGGED_OUT -> {
                status.text = getString(R.string.status_logged_out)
                start.isEnabled = false
                logout.isEnabled = false
                login.isEnabled = true
            }

            else -> {
                status.text = getString(R.string.status_logged_in)
                start.isEnabled = true
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

    override fun fileDownloaded(file: File) {
        val intent = Intent(Intent.ACTION_VIEW)
        val apkURI = FileProvider.getUriForFile(
            this,
            applicationContext.packageName + ".provider",
            file)
        val uri = Uri.fromFile(file)
        val extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        intent.setDataAndType(apkURI, mimeType)
        intent.flags = FLAG_GRANT_READ_URI_PERMISSION
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Snackbar.make(login_layout, R.string.not_open_file, Snackbar.LENGTH_LONG).show()
        }
    }

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

