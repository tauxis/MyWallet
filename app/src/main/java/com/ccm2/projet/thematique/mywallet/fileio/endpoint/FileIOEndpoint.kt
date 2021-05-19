package com.ccm2.projet.thematique.mywallet.fileio.endpoint

import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.http.*

interface FileIOEndpoint {

    @Multipart
    @POST("/")
    suspend fun postFile(@PartMap map: HashMap<String?, RequestBody?>): Response<ResponseBody>
}