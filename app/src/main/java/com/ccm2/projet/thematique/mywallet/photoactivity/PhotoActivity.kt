package com.ccm2.projet.thematique.mywallet.photoactivity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.ccm2.projet.thematique.mywallet.R
import com.ccm2.projet.thematique.mywallet.fileio.FileIO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_photo.*
import java.io.File




class PhotoActivity : AppCompatActivity() {
    val CAMERA_REQUEST_CODE = 0
    var resultUri: Uri? = null

    private lateinit var resultBitmap: Bitmap
    private lateinit var resultHolyUri: Uri
    private var tmpFile: FileIO = FileIO()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo)

        checkStatus()

        CropImage.activity()
            .setGuidelines(CropImageView.Guidelines.ON)
            .start(this)
        restart_photo.setOnClickListener {
            CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this)
            checkStatus()
        }
        upload.setOnClickListener{
            alertUpload(resultHolyUri)
        }
        valid_photo.setOnClickListener {
            // TODO : Envoyer par mail le lien obtenu doesn't work yet
            val link = tmpFile.getLocalLink(resultHolyUri)
            if (link != null) {
                Log.d("Link", link)
            }
        }
    }
    fun alertUpload(holyUri: Uri){
        val alert: AlertDialog.Builder = AlertDialog.Builder(this)
        val edittext = EditText(this)
        alert.setMessage("Quel est le nom du fichier ?");
        alert.setTitle("Envoyer une photo dans le cloud");
        alert.setView(edittext);
        alert.setPositiveButton(
            "Confirmer"
        ) { dialog, whichButton -> //What ever you want to do with the value
            val youEditTextValue = edittext.text.toString()
            uploadPNG(holyUri,youEditTextValue)
        }

        alert.setNegativeButton(
            "Annuler"
        ) { dialog, whichButton ->

        }
        alert.show()
    }

    fun uploadPNG(holyUri: Uri, filename: String){
        val firebaseStorage = FirebaseStorage.getInstance()
        val currentFirebaseUser = FirebaseAuth.getInstance().currentUser
        if (currentFirebaseUser != null) {
            Log.d("Firebase user",currentFirebaseUser.uid.toString())
        }
        Log.d("Tag pathname firebase","Users/" + (currentFirebaseUser?.uid ?: "UIDNOTFOUND")+"/"+filename)
        val ref = firebaseStorage.reference.child(
            "Users/" + (currentFirebaseUser?.uid.toString() ?: "UIDNOTFOUND")+"/"+filename
        )

        val task = ref.putFile(holyUri)
        task.addOnProgressListener {
            val progress = (100.0 * it.bytesTransferred) / it.totalByteCount
            Toast.makeText(this, "Upload image progress ${progress} %", Toast.LENGTH_SHORT).show()
        }
        task.addOnFailureListener {
            Toast.makeText(this, "OnFailure ${it.message}", Toast.LENGTH_SHORT).show()
        }
        task.addOnCompleteListener {
            Toast.makeText(this, "OnComplete", Toast.LENGTH_SHORT).show()
        }
    }

    fun checkStatus(){
        if (cropImageView.drawable==null) {
            upload.isEnabled=false
            valid_photo.isEnabled=false
        }
        else{
            upload.isEnabled=true
            valid_photo.isEnabled=true
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)

            if (resultCode == RESULT_OK) {
                val resultUri: Uri = result.uri
                cropImageView.setImageURI(resultUri);
                checkStatus()
                Log.d("uri result", resultUri.toString())
                val drawable: Drawable = cropImageView.drawable
                val bitmapDrawable = drawable as BitmapDrawable
                val bitmap = bitmapDrawable.bitmap
                resultBitmap = bitmap
                resultHolyUri = resultUri
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                checkStatus()
                System.out.println(error);
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("resultUri", resultUri.toString())
    }
}