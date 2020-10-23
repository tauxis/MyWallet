package com.ccm2.projet.thematique.mywallet.googleactivity

import java.io.File

interface ServiceListener {
    fun loggedIn() //1
    fun fileDownloaded(file: File) //2
    fun cancelled() //3
    fun handleError(exception: Exception) //4
}