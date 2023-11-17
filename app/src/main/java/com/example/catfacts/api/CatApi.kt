package com.example.catfacts.api

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import retrofit2.http.GET

interface CatApi {
    @GET("breeds")
    suspend fun getBreeds(): BreedsResponse
}

data class BreedsResponse(
    val data: List<CatBreed>
)
@Parcelize
data class CatBreed(
    val breed: String,
    val country: String,
    val origin: String,
    val coat: String,
    val pattern: String
) : Parcelable