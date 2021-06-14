package com.ccm2.projet.thematique.mywallet.mailactivity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ccm2.projet.thematique.mywallet.R


class MailActivity : AppCompatActivity() {
    lateinit var etEmail: EditText
    lateinit var etSubject: EditText
    lateinit var etMessage: EditText
    lateinit var send: Button
    lateinit var attachment: Button
    lateinit var tvAttachment: TextView
    lateinit var email: String
    lateinit var subject: String
    lateinit var message: String
    lateinit var uri: Uri
    var bundleFileIo: String? = null
    private val pickFromGallery:Int = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mail)

        val linkFileIO: String? = intent.getStringExtra("INTENT")
        val bundle = Bundle()
        bundle.putString("link", linkFileIO)
        bundleFileIo = bundle.getString("link")
        title = "KotlinApp"
        etEmail = findViewById(R.id.etTo)
        etSubject = findViewById(R.id.etSubject)
        etMessage = findViewById(R.id.etMessage)
        etMessage.append(bundleFileIo)
        attachment = findViewById(R.id.btAttachment)
        tvAttachment = findViewById(R.id.tvAttachment)
        send = findViewById(R.id.btSend)
        send.setOnClickListener { sendEmail() }
        attachment.setOnClickListener {
            openFolder()
        }

    }
    private fun openFolder() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        intent.putExtra("return-data", true)
        startActivityForResult(
            Intent.createChooser(intent, "Complete action using"),
            pickFromGallery
        )
    }
    // PB AVEC LE URI
    private fun sendEmail() {
        try {
            email = etEmail.text.toString()
            subject = etSubject.text.toString()
            message = etMessage.text.toString()

            val uri = Uri.parse("mailto:"+ email)
                .buildUpon()
                .appendQueryParameter("subject", subject)
//                .appendQueryParameter("email", email)
                .appendQueryParameter("body", message)
                .build()
            val emailIntent = Intent(Intent.ACTION_SENDTO, uri)
            startActivity(Intent.createChooser(emailIntent, "Envoyer email.."))
//            val emailIntent = Intent(Intent.ACTION_SEND)
//            emailIntent.setData(Uri.Builder().scheme("mailto").build())
//
//            emailIntent.type = "message/*"
//
////            emailIntent.type = "message/*"
//            //emailIntent.setData(Uri.parse("mailto:" + email));
//            emailIntent.putExtra(Intent.EXTRA_EMAIL, "mailto:"+email)
//            emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
//            //emailIntent.putExtra(Intent.EXTRA_STREAM, uri)
//            emailIntent.putExtra(Intent.EXTRA_TEXT, message)
//            this.startActivity(Intent.createChooser(emailIntent, "Sending email..."))
//
        }
        catch (t: Throwable) {
            Toast.makeText(this, "Request failed try again: $t", Toast.LENGTH_LONG).show()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == pickFromGallery && resultCode == RESULT_OK) {
            if (data != null) {
                uri = data.data!!
            }
            tvAttachment.text = uri.lastPathSegment
            tvAttachment.visibility = View.VISIBLE
        }
    }
}