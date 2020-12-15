package com.ccm2.projet.thematique.mywallet.googleactivity

import android.app.Activity
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Environment
import android.provider.Settings.Global.getString
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.android.gms.drive.*
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.drive.DriveScopes
import okio.Okio
import java.io.File
import java.io.IOException


class GoogleDriveService(private val activity: Activity, private val config: GoogleDriveConfig) {
    companion object {
        private val SCOPES = setOf<Scope>(Drive.SCOPE_FILE, Drive.SCOPE_APPFOLDER)
        val documentMimeTypes = arrayListOf(
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        )

        const val REQUEST_CODE_OPEN_ITEM = 100
        const val REQUEST_CODE_SIGN_IN = 0
        const val TAG = "GoogleDriveService"
        const val REQUEST_CODE_CREATOR = 2;
    }
    var serviceListener: ServiceListener? = null //1
    private var mDriveClient: DriveClient? = null //2
    private var mDriveResourceClient: DriveResourceClient? = null //3
    private var mGoogleSignInClient: GoogleSignInClient? = null //4
    private var mGoogleSignInAccount: GoogleSignInAccount? = null //4

    fun signIn() {
        mGoogleSignInClient = buildGoogleSignInClient();
        activity.startActivityForResult(mGoogleSignInClient?.signInIntent, REQUEST_CODE_SIGN_IN)
    }

    fun checkLoginStatus() {
        val requiredScopes = HashSet<Scope>(2)
        requiredScopes.add(Drive.SCOPE_FILE)
        requiredScopes.add(Drive.SCOPE_APPFOLDER)
        mGoogleSignInAccount = GoogleSignIn.getLastSignedInAccount(activity)
        val containsScope = mGoogleSignInAccount?.grantedScopes?.containsAll(requiredScopes)
        val account = mGoogleSignInAccount
        if (account != null && containsScope == true) {
            initializeDriveClient(account)
        }
    }

    fun logout() {
        buildGoogleSignInClient()?.signOut()
        mGoogleSignInClient = null
    }

    private fun buildGoogleSignInClient() : GoogleSignInClient? {
        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestScopes(Scope(DriveScopes.DRIVE))
            .build()
        return GoogleSignIn.getClient(activity, signInOptions)
    }

    private fun handleSignIn(data: Intent) {
        val getAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data)
        if (getAccountTask.isSuccessful) {
            initializeDriveClient(getAccountTask.result)

            val credential = GoogleAccountCredential.usingOAuth2(
                activity, listOf(DriveScopes.DRIVE_FILE)
            )
            credential.selectedAccount = getAccountTask.result?.account

        } else {
            serviceListener?.handleError(Exception("Sign-in failed.", getAccountTask.exception))
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CODE_SIGN_IN -> {
                if(resultCode == RESULT_OK) {
                    if (data != null) {
                        handleSignIn(data)
                    } else {
                        serviceListener?.cancelled()
                    }
                }
                else if (resultCode == RESULT_CANCELED) {
                    serviceListener?.handleError(Exception("Sign-in failed."))
                }
            }

            REQUEST_CODE_OPEN_ITEM -> {
                if (data != null) {
                    openItem(data)
                } else {
                    serviceListener?.cancelled()
                }
            }
        }
    }


    private fun initializeDriveClient(signInAccount: GoogleSignInAccount?) {
        mDriveClient = signInAccount?.let { Drive.getDriveClient(activity.applicationContext, it) }
        mDriveResourceClient =
            signInAccount?.let { Drive.getDriveResourceClient(activity.applicationContext, it) }
        serviceListener?.loggedIn()
    }

    private fun openItem(data: Intent) {
        val driveId = data.getParcelableExtra<DriveId>(OpenFileActivityOptions.EXTRA_RESPONSE_DRIVE_ID)
        downloadFile(driveId)
    }
    private fun downloadFile(data: DriveId?) {
        if (data == null) {
            Log.e(TAG, "downloadFile data is null")
            return
        }
        val drive = data.asDriveFile()
        var fileName = "test"
        mDriveResourceClient?.getMetadata(drive)?.addOnSuccessListener {
            fileName = it.originalFilename
        }
        val openFileTask = mDriveResourceClient?.openFile(drive, DriveFile.MODE_READ_ONLY)
        openFileTask?.continueWithTask { task ->
            val contents = task.result
            contents?.inputStream.use {
                try {
                    //This is the app's download directory, not the phones
                    val storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                    val tempFile = File(storageDir, fileName)
                    tempFile.createNewFile()
                    val sink = Okio.buffer(Okio.sink(tempFile))
                    sink.writeAll(Okio.source(it))
                    sink.close()

                    serviceListener?.fileDownloaded(tempFile)
                } catch (e: IOException) {
                    Log.e(TAG, "Problems saving file", e)
                    serviceListener?.handleError(e)
                }
            }
            mDriveResourceClient?.discardContents(contents!!)
        }?.addOnFailureListener { e ->
            // Handle failure
            Log.e(TAG, "Unable to read contents", e)
            serviceListener?.handleError(e)
        }
    }

    /**
     * Prompts the user to select a text file using OpenFileActivity.
     *
     * @return Task that resolves with the selected item's ID.
     */
    fun pickFiles(driveId: DriveId?) {
        val builder = OpenFileActivityOptions.Builder()
        if (config.mimeTypes != null) {
            builder.setMimeType(config.mimeTypes)
        } else {
            builder.setMimeType(documentMimeTypes)
        }
        if (config.activityTitle != null && config.activityTitle.isNotEmpty()) {
            builder.setActivityTitle(config.activityTitle)
        }
        if (driveId != null) {
            builder.setActivityStartFolder(driveId)
        }
        val openOptions = builder.build()
        pickItem(openOptions)
    }

    private fun pickItem(openOptions: OpenFileActivityOptions) {
        val openTask = mDriveClient?.newOpenFileActivityIntentSender(openOptions)
        openTask?.let {
            openTask.continueWith { task ->
                ActivityCompat.startIntentSenderForResult(
                    activity, task.result!!, REQUEST_CODE_OPEN_ITEM,
                    null, 0, 0, 0, null
                )
            }
        }
    }

    enum class ButtonState {
        LOGGED_OUT,
        LOGGED_IN
    }


}