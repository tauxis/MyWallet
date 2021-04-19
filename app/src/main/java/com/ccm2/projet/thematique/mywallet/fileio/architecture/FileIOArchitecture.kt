package com.ccm2.projet.thematique.mywallet.fileio.architecture

import com.ccm2.projet.thematique.mywallet.fileio.endpoint.FileIOEndpoint
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object FileIOArchitecture {

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://wwww.file.io")
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()))
        .build()


    fun postFile(): FileIOEndpoint = retrofit.create(FileIOEndpoint::class.java)
}