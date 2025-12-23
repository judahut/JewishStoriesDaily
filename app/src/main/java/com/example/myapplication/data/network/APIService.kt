package com.example.myapplication.data.network

import com.example.myapplication.data.model.SefariaResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("v3/texts/{ref}")
    suspend fun getText(
        @Path("ref") ref: String,
        @Query("version") version: String? = null
    ): SefariaResponse


}

val service: ApiService = Retrofit.Builder()
    .baseUrl("https://www.sefaria.org/api/")
    .addConverterFactory(GsonConverterFactory.create()) // Standard Gson is fine now!
    .build()
    .create(ApiService::class.java)