package com.ccm2.projet.thematique.mywallet.storage
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class StorageItem(
    var itemUrl: String,
    var itemName: String,
    var itemPath:String
)