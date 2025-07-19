package com.example.rickandmorty.data.retrofit

import com.example.rickandmorty.data.models.PersonData
import retrofit2.Call
import retrofit2.http.GET

interface ServiceRetrofit {
    @GET("character")
    fun getCharacters(): Call<PersonData>
}