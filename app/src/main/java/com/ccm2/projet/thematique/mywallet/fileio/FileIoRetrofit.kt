package com.ccm2.projet.thematique.mywallet.fileio

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object FileIoRetrofit {

//    val interceptor : HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
//        this.level = HttpLoggingInterceptor.Level.BODY
//    }
//
//    val client : OkHttpClient = OkHttpClient.Builder().apply {
//        this.addInterceptor(interceptor)
//    }.build()

    val retrofit = Retrofit.Builder()
        .baseUrl("https://file.io/")
        .addConverterFactory(GsonConverterFactory.create())
//        .client(client)
        .build()

    val service: FileIoService = retrofit.create<FileIoService>(FileIoService::class.java)
}