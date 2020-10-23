package com.ccm2.projet.thematique.mywallet.menu

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ccm2.projet.thematique.mywallet.R
import com.ccm2.projet.thematique.mywallet.googleactivity.GoogleDriveConfig
import com.ccm2.projet.thematique.mywallet.googleactivity.GoogleDriveService
import com.ccm2.projet.thematique.mywallet.googleactivity.ServiceListener
import com.ccm2.projet.thematique.mywallet.loginactivity.LoginActivity
import com.ccm2.projet.thematique.mywallet.photoactivity.PhotoActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_menu.*
import kotlinx.android.synthetic.main.activity_menu.logout
import java.io.File


class MenuActivity : AppCompatActivity(), ServiceListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        val config = GoogleDriveConfig(
            getString(R.string.source_google_drive),
            GoogleDriveService.documentMimeTypes
        )
        googleDriveService = GoogleDriveService(this, config)
        //2
        googleDriveService.serviceListener = this
        //3
        googleDriveService.checkLoginStatus()
        addPhoto.setOnClickListener() {
            goToAppareilPhoto();
        }
        logout.setOnClickListener {
            googleDriveService.logout()
            //LoginActivity().state = GoogleDriveService.ButtonState.LOGGED_OUT
            setButtons()
            finish()
        }
    }
    //// Auth Service
        private lateinit var googleDriveService: GoogleDriveService

        override fun loggedIn() {
        }

        override fun fileDownloaded(file: File) {}
        //    override fun fileDownloaded(file: File) {
    //        val intent = Intent(Intent.ACTION_VIEW)
    //        val apkURI = FileProvider.getUriForFile(
    //            this,
    //            applicationContext.packageName + ".provider",
    //            file)
    //        val uri = Uri.fromFile(file)
    //        val extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
    //        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
    //        intent.setDataAndType(apkURI, mimeType)
    //        intent.flags = FLAG_GRANT_READ_URI_PERMISSION
    //        if (intent.resolveActivity(packageManager) != null) {
    //            startActivity(intent)
    //        } else {
    //            Snackbar.make(login_layout, R.string.not_open_file, Snackbar.LENGTH_LONG).show()
    //        }
    //    }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            googleDriveService.onActivityResult(requestCode, resultCode, data)
        }
        override fun cancelled() {
            Snackbar.make(login_layout, R.string.status_user_cancelled, Snackbar.LENGTH_LONG).show()
        }

        override fun handleError(exception: Exception) {
            val errorMessage = getString(R.string.status_error, exception.message)
            Snackbar.make(login_layout, errorMessage, Snackbar.LENGTH_LONG).show()
        }

        fun setButtons() {
            when (LoginActivity().state) {
                GoogleDriveService.ButtonState.LOGGED_OUT -> {
                    statusL.text = getString(R.string.status_logged_out)
                    statusM.text = getString(R.string.status_logged_out)
                    //start.isEnabled = false
                    logout.isEnabled = false
                    login.isEnabled = true
                }

                else -> {
                    statusL.text = getString(R.string.status_logged_in)
                    statusM.text = getString(R.string.status_logged_in)
                    //start.isEnabled = true
                    logout.isEnabled = true
                    login.isEnabled = false
                }
            }
        }

    ////

    private fun goToAppareilPhoto() {
        startActivity(Intent(this, PhotoActivity::class.java));
    }
}