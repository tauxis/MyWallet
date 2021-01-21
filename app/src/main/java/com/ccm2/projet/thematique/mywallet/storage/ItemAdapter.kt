package com.ccm2.projet.thematique.mywallet.storage

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.ccm2.projet.thematique.mywallet.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso


class ItemAdapter(private var items: List<StorageItem>, private val context: Context):
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
        builder.setTitle("Supprimer l'item ?")
        builder.setPositiveButton("Oui") { dialog, which ->
            deleteItem(item.itemPath)
            items.drop(position)
            this.notifyDataSetChanged()
        }
        builder.setNegativeButton("Non") { dialog, which ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun deleteItem(pathFirebase: String){
        Log.d("DeleteItem", pathFirebase)
        val tmpStorageItem = firebaseStorage.reference.child(pathFirebase)
        tmpStorageItem.delete()
    }

    class ViewHolder(view: View):RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.itemRecyclerViewImage)
        val textViewName: TextView = view.findViewById(R.id.itemRecyclerViewName)
    }



}


