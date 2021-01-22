package com.ccm2.projet.thematique.mywallet.menu

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.ccm2.projet.thematique.mywallet.R
import com.ccm2.projet.thematique.mywallet.loginactivity.LoginActivity
import com.ccm2.projet.thematique.mywallet.mailactivity.MailActivity
import com.ccm2.projet.thematique.mywallet.photoactivity.PhotoActivity
import com.ccm2.projet.thematique.mywallet.storage.StorageActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_menu.*


// declare the GoogleSignInClient
lateinit var mGoogleSignInClient: GoogleSignInClient

lateinit var storage: FirebaseStorage

// val auth is initialized by lazy
private val auth by lazy {
    FirebaseAuth.getInstance()
}

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        addPhoto.setOnClickListener() {
            goToAppareilPhoto();
        }

        sendFile.setOnClickListener() {
            goToSendFile();
        }

        // Auth
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient= GoogleSignIn.getClient(this, gso)

        logout.setOnClickListener {
            mGoogleSignInClient.signOut().addOnCompleteListener {
                val intent= Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        // end auth

        addPhoto.setOnClickListener{ goToAppareilPhoto() }
        storage.setOnClickListener { goToStorage() }
        Upload.setOnClickListener{ chooseImg() }
    }
    ////
    //// UPLOAD IMAGE
    private fun chooseImg() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        val mimeTypes = arrayOf("image/*")
        intent.type = "*/*"
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        startActivityForResult(Intent.createChooser(intent, "Sélectionner une image"), UPLOAD_FILE)
    }

    fun alertUpload(holyUri: Uri){
        val alert: AlertDialog.Builder = AlertDialog.Builder(this)
        val edittext = EditText(this)
        alert.setMessage("Quel est le nom du fichier ?");
        alert.setTitle("Envoyer un fichier dans le cloud");
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UPLOAD_FILE && resultCode == RESULT_OK) {
            if (data != null && data.data != null) {
                val imgUri = data.data
                Log.d("onActivityResult",imgUri.toString())
                if (imgUri != null) {
                    alertUpload(imgUri)
                }

            }
        }
    }
    //// TAKE PICTURE
    private fun goToAppareilPhoto() {
        startActivity(Intent(this, PhotoActivity::class.java));
    }

    private fun goToSendFile() {
        startActivity(Intent(this, MailActivity::class.java));
    }

    private fun goToStorage() {
        startActivity(Intent(this, StorageActivity::class.java));
    }
    //// CONSTANTES
    companion object {
        private const val UPLOAD_FILE = 100
        private const val UPLOAs = 200
    }
}