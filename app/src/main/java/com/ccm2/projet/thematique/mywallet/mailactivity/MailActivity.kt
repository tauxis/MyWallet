package com.ccm2.projet.thematique.mywallet.mailactivity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ccm2.projet.thematique.mywallet.R
import com.ccm2.projet.thematique.mywallet.fileio.FileIoRetrofit.service
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


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
    private val pickFromGallery:Int = 101


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mail)
        title = "KotlinApp"
        etEmail = findViewById(R.id.etTo)
        etSubject = findViewById(R.id.etSubject)
        etMessage = findViewById(R.id.etMessage)
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
    private fun sendEmail() {
        try {
            System.out.println(uri)
            val file: File = File(uri.path)
            Log.d("2",file.absolutePath)

            Log.d("1",file.toString())

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestUri = uri.toString().toRequestBody(mediaType)
            val requestExpires = "1w".toRequestBody(mediaType)
            val requestMaxDownload = "1".toRequestBody(mediaType)
            val requestFile: RequestBody = RequestBody.create(
                mediaType,
                file
            )
            val body: MultipartBody.Part =
                MultipartBody.Part.createFormData("picture", file.name, requestFile)

            val call: Call<ResponseBody?>? = service.upload(
                body,
                requestExpires,
                requestMaxDownload
            )
            call?.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>?,
                    response: Response<ResponseBody?>?
                ) {
                    Log.v("Upload", "success")
                    System.out.println(response)
                    System.out.println(call.toString())

                }

                override fun onFailure(call: Call<ResponseBody?>?, t: Throwable) {
                    t.message?.let { Log.e("Upload error:", it) }
                    System.out.println("iCIIIIIIIIIIIIIIIIIIIIIIII lkjlkjlkjlkj")

                }
            })

            //val fileio = uploadFile.upload(requestBody)
            email = etEmail.text.toString()
            subject = etSubject.text.toString()
            message = etMessage.text.toString()
            val emailIntent = Intent(Intent.ACTION_SEND)
            emailIntent.type = "plain/text"
            emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
            emailIntent.putExtra(Intent.EXTRA_STREAM, uri)
            emailIntent.putExtra(Intent.EXTRA_TEXT, message)
            this.startActivity(Intent.createChooser(emailIntent, "Sending email..."))
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