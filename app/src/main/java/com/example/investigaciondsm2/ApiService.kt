package com.example.investigaciondsm2

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("photos")
    suspend fun getPhotos(): Response<List<Photo>>

    @POST("photos")
    suspend fun postPhoto(@Body photo: Photo): Response<Photo>
}

