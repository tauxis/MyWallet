package com.ccm2.projet.thematique.mywallet.fileio

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object FileIoRetrofit {

    val retrofit = Retrofit.Builder()
        .baseUrl("https://file.io/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: FileIoService = retrofit.create<FileIoService>(FileIoService::class.java)
}