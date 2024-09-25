package com.example.investigaciondsm2

import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("photos")
    suspend fun getPhotos(): Response<List<Photo>>
}

