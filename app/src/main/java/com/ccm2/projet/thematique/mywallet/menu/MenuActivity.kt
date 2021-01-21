package com.ccm2.projet.thematique.mywallet.menu

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ccm2.projet.thematique.mywallet.R
import com.ccm2.projet.thematique.mywallet.loginactivity.LoginActivity
import com.ccm2.projet.thematique.mywallet.photoactivity.PhotoActivity
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

        Upload.setOnClickListener{
            chooseImg()

        }
    }
    ////
    //// UPLOAD IMAGE
    private fun chooseImg() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        val mimeTypes = arrayOf("image/*", "application/pdf")
        intent.type = "*/*"
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        startActivityForResult(Intent.createChooser(intent, "Sélectionner une image"), UPLOAD_FILE)
    }
    //// LIST ALL FILES
//    fun listAllFiles() {
//        // [START storage_list_all]
//        val storage = Firebase.storage
//        val listRef = storage.reference.child("files/uid")
//
//        // You'll need to import com.google.firebase.storage.ktx.component1 and
//        // com.google.firebase.storage.ktx.component2
//        listRef.listAll()
//            .addOnSuccessListener { (items, prefixes) ->
//                prefixes.forEach { prefix ->
//                    // All the prefixes under listRef.
//                    // You may call listAll() recursively on them.
//                }
//
//                items.forEach { item ->
//                    // All the items under listRef.
//                }
//            }
//            .addOnFailureListener {
//                // Uh-oh, an error occurred!
//            }
//        // [END storage_list_all]
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UPLOAD_FILE && resultCode == Activity.RESULT_OK) {
            if (data != null && data.data != null) {
                val imgUri = data.data

            }
        }
    }
    //// TAKE PICTURE
    private fun goToAppareilPhoto() {
        startActivity(Intent(this, PhotoActivity::class.java));
    }

    //// CONSTANTES
    companion object {
        private const val UPLOAD_FILE = 100
        private const val UPLOAs = 200
    }
}