package com.ccm2.projet.thematique.mywallet.photoactivity

import android.R.attr
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.ccm2.projet.thematique.mywallet.R
import com.ccm2.projet.thematique.mywallet.fileio.FileIO
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_photo.*
import java.io.File



class PhotoActivity : AppCompatActivity() {
    val CAMERA_REQUEST_CODE = 0
    var resultUri: Uri? = null

    private lateinit var resultBitmap: Bitmap
    private var tmpFile: FileIO = FileIO()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo)


        CropImage.activity()
            .setGuidelines(CropImageView.Guidelines.ON)
            .start(this);
        restart_photo.setOnClickListener {
            CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
        }
        valid_photo.setOnClickListener {
            // TODO : Envoyer le bitmap vers Drive / Envoyer par mail /
            val link = tmpFile.getLocalLink(resultBitmap);
            if (link != null) {
                Log.d("Link", link)
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)

            if (resultCode == RESULT_OK) {
                val resultUri: Uri = result.uri
                cropImageView.setImageURI(resultUri);
                Log.d("uri result", resultUri.toString())
                val drawable: Drawable = cropImageView.drawable
                val bitmapDrawable = drawable as BitmapDrawable
                val bitmap = bitmapDrawable.bitmap
                resultBitmap = bitmap
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                System.out.println(error);
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("resultUri", resultUri.toString())
    }
}