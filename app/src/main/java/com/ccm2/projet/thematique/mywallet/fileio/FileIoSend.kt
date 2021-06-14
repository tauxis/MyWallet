package com.ccm2.projet.thematique.mywallet.fileio

import android.content.Context
import android.content.Intent
import android.os.StrictMode
import android.util.Log
import androidx.core.content.ContextCompat
import com.ccm2.projet.thematique.mywallet.mailactivity.MailActivity
import com.ccm2.projet.thematique.mywallet.qrcodeactivity.QRCodeActivity
import com.ccm2.projet.thematique.mywallet.zipservice.ZipService
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class FileIoSend(contextFromIA: Context) {

    private val mediaType = "application/json; charset=utf-8".toMediaType()
    private val requestExpires = "1w".toRequestBody(mediaType)
    private val requestMaxDownload = "1".toRequestBody(mediaType)
    private val requestAutoDelete = "true".toRequestBody(mediaType)
    private val contextFromIA = contextFromIA
    private lateinit var zipService: ZipService

    private fun startQRCodeActivity(linkFileIo:String){
        //Supprimer le zip lorsqu'il a été utilisé
        zipService = ZipService(contextFromIA)
        zipService.cleanZip()
        //Lancer l'activité
        val qrIntent = Intent(contextFromIA, QRCodeActivity::class.java).putExtra("INTENT", linkFileIo)
        ContextCompat.startActivity(contextFromIA, qrIntent, null);
    }

    private fun startMailActivity(linkFileIo:String){
        //Supprimer le zip lorsqu'il a été utilisé
        zipService = ZipService(contextFromIA)
        zipService.cleanZip()
        //Lancer l'activité
        val qrIntent = Intent(contextFromIA, MailActivity::class.java).putExtra("INTENT", linkFileIo)
        ContextCompat.startActivity(contextFromIA, qrIntent, null);
    }

    fun parseResponse(response: String):String{
        var linkParse =""
        try {
            val jsonObject = JSONTokener(response).nextValue() as JSONObject
            linkParse = jsonObject.getString("link")
            return linkParse
        } catch (e2: JSONException) {
            e2.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return linkParse
    }


    fun curlFileIo(zipPath: String,func : Int)  {
        val policy =  StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy)

        val fileZip = File(zipPath)
        val requestFile = RequestBody.create("application/x-zip-compressed".toMediaType(), fileZip)
        val body: MultipartBody.Part =
            MultipartBody.Part.createFormData("file", fileZip.name, requestFile)
        val call: Call<ResponseBody> = FileIoRetrofit.service.upload(
            body,
            requestExpires,
            requestMaxDownload,
            requestAutoDelete
        )
        if(func==1){
            enqueue(call) { data -> startQRCodeActivity(data) }
        }
        if(func==2){
            enqueue(call) { data -> startMailActivity(data) }
        }
    }

    private fun enqueue(call: Call<ResponseBody>, callback: (String) -> Unit) {
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                Log.v("Upload", "success")
                val stringResponse = response.body()?.string().toString()
                val link = parseResponse(stringResponse)
                Log.d("REPONSE BODY", stringResponse)
                Log.d("LINK", link)
                callback(link);
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.message?.let { Log.e("Upload error:", it) }

            }
        })
    }
}