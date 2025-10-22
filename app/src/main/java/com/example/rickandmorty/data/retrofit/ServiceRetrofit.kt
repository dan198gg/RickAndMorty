package com.example.rickandmorty.data.retrofit

import com.example.rickandmorty.data.models.PersonData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ServiceRetrofit {
    @GET("character")
    fun getCharacters(): Call<PersonData>

    @GET("character/")
    fun getCharactersWithFilters(
        @Query("name") name: String? = null,
        @Query("status") status: String? = null,
        @Query("species") species: String? = null,
        @Query("type") type: String? = null,
        @Query("gender") gender: String? = null
    ): Call<PersonData>
}