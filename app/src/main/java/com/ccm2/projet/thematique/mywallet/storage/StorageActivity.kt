package com.ccm2.projet.thematique.mywallet.storage

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ccm2.projet.thematique.mywallet.R
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_storage.*

class StorageActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_storage)

        var firebaseStorage = FirebaseStorage.getInstance()
        var currentFirebaseUser = FirebaseAuth.getInstance().currentUser
        var storageRef = firebaseStorage.getReference(
            "Users/" + (currentFirebaseUser?.uid ?: "UIDNOTFOUND)")
        )
        progressBar.visibility = View.VISIBLE

        val listAllTask: Task<ListResult> = storageRef.listAll()
        listAll(listAllTask)

    }

    fun listAll(listAllTask: Task<ListResult>){
        val itemList:ArrayList<StorageItem> = ArrayList()
        listAllTask.addOnCompleteListener { result ->
            val items: List<StorageReference> = result.result!!.items
            //add cycle for add image url to list
            items.forEachIndexed { index, item ->
                Log.d("", item.name)
                Log.d("METADATA", item.metadata.toString())

                item.downloadUrl.addOnSuccessListener {
                    Log.d("item", "$it")
                    itemList.add(StorageItem(it.toString(), item.name, item.path))
                }.addOnCompleteListener {
                    recyclerView.adapter = ItemAdapter(itemList, this)
                    recyclerView.layoutManager = LinearLayoutManager(this)
                    progressBar.visibility = View.GONE
                }
            }
        }
    }
}

