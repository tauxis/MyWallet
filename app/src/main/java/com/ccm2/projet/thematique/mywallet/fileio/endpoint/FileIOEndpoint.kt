package com.ccm2.projet.thematique.mywallet.fileio.endpoint

import retrofit2.http.POST
import retrofit2.http.Path

interface FileIOEndpoint {

    @POST("/")
    suspend fun getRandomQuote(): String
}