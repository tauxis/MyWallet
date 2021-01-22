package com.ccm2.projet.thematique.mywallet.storage

import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.ccm2.projet.thematique.mywallet.R
import com.ccm2.projet.thematique.mywallet.mailactivity.MailActivity
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.io.File


class ItemAdapter(private val context: Context, var items: ArrayList<StorageItem>):
    RecyclerView.Adapter<ItemAdapter.ViewHolder>() {


    private val inflater : LayoutInflater = LayoutInflater.from(context)

    val firebaseStorage = FirebaseStorage.getInstance()
    var currentFirebaseUser = FirebaseAuth.getInstance().currentUser

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.storage_item, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        Picasso.get().load(item.itemUrl).into(holder.imageView)
        holder.textViewName.text=item.itemName
        holder.itemView.setOnClickListener{ clickAction(position, item) }
    }

    private fun clickAction(position: Int, item: StorageItem){
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Envoyer ou Supprimer ?")
        builder.setPositiveButton("Supprimer") { dialog, which ->
            deleteItem(item.itemPath)
            items.drop(position)
            this.notifyDataSetChanged()
            rebuild(items)
        }
        builder.setNegativeButton("Envoyer") { dialog, which ->
            //@TODO créer le lien avec l'activité Mail
            val intent = Intent(context, MailActivity::class.java)
            downloadItem(items.get(position))
//            intent.putExtra("items", items)
//            intent.putExtra("position", position)
//            context.startActivity(intent);
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun deleteItem(pathFirebase: String){
        Log.d("DeleteItem", pathFirebase)
        val tmpStorageItem = firebaseStorage.reference.child(pathFirebase)
        tmpStorageItem.delete()
    }

    private fun downloadItem(item: StorageItem){
        val tmpStorageItem = firebaseStorage.reference.child(item.itemPath)
        val pd = ProgressDialog(context)
        pd.setTitle(item.itemName)
        pd.setMessage("Downloading Please Wait!")
        pd.isIndeterminate = true
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        pd.show()

        val rootPath = File(Environment.getExternalStorageDirectory(), "MADBO DOWNLOADS")

        if (!rootPath.exists()) {
            rootPath.mkdirs()
        }

        val localFile = File(rootPath, item.itemName)

        tmpStorageItem.getFile(localFile)
            .addOnSuccessListener(object : OnSuccessListener<FileDownloadTask.TaskSnapshot?> {
                override fun onSuccess(taskSnapshot: FileDownloadTask.TaskSnapshot?) {
                    Log.e("firebase ", ";local tem file created  created $localFile")
                    if (localFile.canRead()) {
                        pd.dismiss()
                    }
                    Toast.makeText(context, "Download Completed", Toast.LENGTH_SHORT).show()
                }
            }).addOnFailureListener(object : OnFailureListener {
                override fun onFailure(exception: Exception) {
                    System.out.println(item.itemUrl)
                    System.out.println(item.itemPath)
                    Log.e("firebase ", ";local tem file not created  created $exception")
                    Toast.makeText(context, "Download Incompleted", Toast.LENGTH_LONG).show()
                }
            })
    }

    class ViewHolder(view: View):RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.itemRecyclerViewImage)
        val textViewName: TextView = view.findViewById(R.id.itemRecyclerViewName)
    }

    fun rebuild(itemList: ArrayList<StorageItem>) {
        // This is the simplest way to update the list
        itemList.clear()
        itemList.addAll(itemList)
        // Needed to said to recycler view we have new data
        this.notifyDataSetChanged()
    }

}


