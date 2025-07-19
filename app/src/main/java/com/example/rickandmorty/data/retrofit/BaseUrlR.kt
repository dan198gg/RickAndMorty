package com.example.rickandmorty.data.retrofit

object BaseUrlR {
    private val url = "https://rickandmortyapi.com/api/"
    val serviceRetrofit: ServiceRetrofit get() =
        ClientR.getClient(url).create(ServiceRetrofit::class.java)
}