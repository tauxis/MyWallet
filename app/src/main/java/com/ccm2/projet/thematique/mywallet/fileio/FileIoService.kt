package com.ccm2.projet.thematique.mywallet.fileio

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface FileIoService {
    @Multipart
    @POST("/")
    fun upload(
        @Part file: MultipartBody.Part?,
        @Part("expires") expires: RequestBody?,
        @Part("maxDownloads") maxDownloads: RequestBody?,
        @Part("autoDelete") autodelete : RequestBody?,
    ): Call<ResponseBody>
}