package com.ccm2.projet.thematique.mywallet.storage

import android.annotation.SuppressLint
import android.content.Context
import android.view.*
import android.view.View.*
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ActionMode
import androidx.recyclerview.widget.RecyclerView
import com.ccm2.projet.thematique.mywallet.R
import com.ccm2.projet.thematique.mywallet.zipservice.ZipService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class ItemAdapter(
    private val context: Context,
    var items: ArrayList<StorageItem>,
    private val activity: StorageActivity, val titleChange: () -> Unit
):
    RecyclerView.Adapter<ItemAdapter.ViewHolder>(), ActionMode.Callback {

    private val inflater : LayoutInflater = LayoutInflater.from(context)
    private var multiSelect = false
    private val selectedItems = arrayListOf<StorageItem>()
    private var selectedItemsCount = "0"
    private lateinit var zipService: ZipService

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
        if(multiSelect) {holder.checkbox.visibility= VISIBLE}
        else {holder.checkbox.visibility= GONE }

        Picasso.get().load(item.itemUrl).into(holder.imageView)
        holder.textViewName.text=item.itemName
        holder.deleteButton.setOnClickListener{ clickAction(position, item) }
        holder.itemView.setOnLongClickListener {
            if (!multiSelect) {
                // Add it to the list containing all the selected images
                activity.startSupportActionMode(this)
                selectItem(holder, items[position])
                true
            }else {
                selectItem(holder, items[position])
            true}
        }
    }

    private fun clickAction(position: Int, item: StorageItem){
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Supprimer l'item ?")
        builder.setPositiveButton("Oui") { dialog, which ->
            items.drop(position)
            deleteItem(item.itemPath)
            rebuild(items)
        }
        builder.setNegativeButton("Non") { dialog, which ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun deleteItem(pathFirebase: String){
        val tmpStorageItem = firebaseStorage.reference.child(pathFirebase)
        tmpStorageItem.delete()
    }

    class ViewHolder(view: View):RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.itemRecyclerViewImage)
        val textViewName: TextView = view.findViewById(R.id.itemRecyclerViewName)
        val deleteButton: ImageView = view.findViewById(R.id.delete_button)
        val checkbox: CheckBox = view.findViewById(R.id.checkb)
    }

    fun rebuild(itemList: ArrayList<StorageItem>) {
        itemList.clear()
        itemList.addAll(itemList)
        this.notifyDataSetChanged()
    }

    fun selectedItemsCount():String{
        return selectedItems.size.toString()
    }

    @SuppressLint("Range")
    private fun selectItem(holder: ViewHolder, item: StorageItem) {
        // If the "selectedItems" list contains the item, remove it and set it's state to normal
        if (selectedItems.contains(item)) {
            selectedItems.remove(item)
            holder.itemView.alpha = 1.0f
            holder.checkbox.visibility= INVISIBLE
            holder.checkbox.isChecked=false
            selectedItemsCount = selectedItemsCount()
            activity.invalidateOptionsMenu()
            Toast.makeText(context, "Objet retiré de la liste.", Toast.LENGTH_SHORT).show()
        } else {
            selectedItems.add(item)
            holder.itemView.alpha = 1.3f
            holder.checkbox.visibility=VISIBLE
            holder.checkbox.isChecked=true
            selectedItemsCount = selectedItemsCount()
            activity.invalidateOptionsMenu()
            Toast.makeText(context, "Objet ajouté à la liste.", Toast.LENGTH_SHORT).show()
        }
        titleChange()
    }

    // Called when a menu item was clicked
    override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
        mode.title=selectedItemsCount
        if (item.itemId == R.id.action_delete) {
            if(selectedItems.isNotEmpty()) {
                val builder = AlertDialog.Builder(context)
                builder.setTitle("Supprimer les items ?")
                builder.setPositiveButton("Oui") { dialog, which ->
                    // Delete button is clicked, handle the deletion and finish the multi select process
                    selectedItems.forEachIndexed { index, item ->
                        deleteItem(item.itemPath)
                        items.drop(index)
                    }
                    rebuild(items)
                    Toast.makeText(context, "Selected items deleted", Toast.LENGTH_SHORT).show()
                    this.notifyDataSetChanged()
                    mode.finish()
                }
                builder.setNegativeButton("Non") { dialog, which ->
                    dialog.dismiss()
                }
                val dialog = builder.create()
                dialog.show()
            }else Toast.makeText(context, "No items selected", Toast.LENGTH_SHORT).show()

        }
        if (item.itemId == R.id.action_qr) {
            if(selectedItems.isNotEmpty()) {
                Toast.makeText(context, "Création du zip en cours", Toast.LENGTH_SHORT).show()
                //Instanciation de la classe
                zipService = ZipService()
                //Fichiers séléctionnés dans le ZIP
                val zipFile = zipService.zipFilesSelected(selectedItems)
                Toast.makeText(context, "Zip créé : $zipFile", Toast.LENGTH_SHORT).show()
                //Supprimer le zip lorsqu'il a été utilisé
                //zipService.cleanZip()
            }else Toast.makeText(context, "No items selected", Toast.LENGTH_SHORT).show()
        }

        return true
    }

    // Called when the menu is created i.e. when the user starts multi-select mode (inflate your menu xml here)
    override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
        // Inflate the menu resource providing context menu items
        val inflater: MenuInflater = mode.menuInflater
        multiSelect = true
        inflater.inflate(R.menu.menu, menu)
        activity.actionMode=mode
        mode.title=selectedItemsCount
        return true
    }

    // Called when the Context ActionBar disappears i.e. when the user leaves multi-select mode
    override fun onDestroyActionMode(mode: ActionMode?) {
        // finished multi selection
        multiSelect = false
        selectedItems.clear()
        mode?.finish()
        notifyDataSetChanged()
    }

    // Called to refresh an action mode's action menu
    override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
        mode.title=selectedItemsCount
        return true
    }

}


