package com.ccm2.projet.thematique.mywallet.parametersactivity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.ccm2.projet.thematique.mywallet.R
import com.ccm2.projet.thematique.mywallet.loginactivity.LoginActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_parameters.*


// declare the GoogleSignInClient
lateinit var mGoogleSignInClient: GoogleSignInClient

lateinit var storage: FirebaseStorage

// val auth is initialized by lazy
private val auth by lazy {
    FirebaseAuth.getInstance()
}


class ParametersActivity : AppCompatActivity() {

    private lateinit var objectSharedPreferences : SharedPreferences

    companion object {
        private const val DARK_STATUS = "io.ccm2.projet.thematique.mywallet.parameters.DARK_STATUS"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parameters)

        objectSharedPreferences=this.getSharedPreferences(
            "MyWalletPreferences",
            Context.MODE_PRIVATE
        )

        // Auth
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient= GoogleSignIn.getClient(this, gso)

        val firebaseStorage = FirebaseStorage.getInstance()
        val currentFirebaseUser = FirebaseAuth.getInstance().currentUser

        daynight.setOnClickListener{ chooseThemeDialog() }

        delete_account.setOnClickListener{
            if (currentFirebaseUser != null) {
                deleteAccount(currentFirebaseUser, firebaseStorage)
            }
        }

    }

    private fun editStatus(value: Int){
        val sharedPreferenceEditor= objectSharedPreferences.edit()
        sharedPreferenceEditor.putInt(DARK_STATUS, value)
        sharedPreferenceEditor.apply()
    }

    private fun chooseThemeDialog() {

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choisir le thème de l'application :")
        val styles = arrayOf("Jour", "Nuit", "Défaut")
        val checkedItem = objectSharedPreferences.getInt(DARK_STATUS, 2)
        // ici pb quand on change de mode, ça quitte l'activity au lieu de quitter le dialog
        builder.setSingleChoiceItems(styles, checkedItem) { dialog, which ->
            when (which) {
                0 -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    editStatus(0)
                    delegate.applyDayNight()
                    dialog.dismiss()
                }
                1 -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    editStatus(1)
                    delegate.applyDayNight()
                    dialog.dismiss()
                }
                2 -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    editStatus(2)
                    delegate.applyDayNight()
                    dialog.dismiss()
                }
            }
        }
        val dialog = builder.create()
        dialog.show()
    }


    private fun deleteAccount(currentFirebaseUser: FirebaseUser, firebaseStorage: FirebaseStorage){
        val storageRef = firebaseStorage.getReference(
            "Users/" + (currentFirebaseUser.uid)
        )
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Attention !")
        builder.setMessage("Êtes-vous sûr de vouloir supprimer votre compte ainsi que toutes les informations qui y sont liées ?")
        builder.setPositiveButton("Oui") { dialog, which ->
            deleteFolderContents(storageRef)
            currentFirebaseUser.delete()
            mGoogleSignInClient.revokeAccess()
            mGoogleSignInClient.signOut()
            val intent = Intent(applicationContext, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
        builder.setNegativeButton("Non") { dialog, which ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun deleteFolderContents(path: StorageReference) {
        path.listAll().
        addOnCompleteListener { result ->
            val items: List<StorageReference> = result.result!!.items
            items.forEachIndexed { index, item ->
                item.delete()
            }
        }
    }
}