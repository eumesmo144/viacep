package com.example.viacepmobile.model.dados

import retrofit2.http.GET

interface UsuarioServiceIF {
    @GET("usuarios")
    suspend fun listar(): List<Usuario>

    @GET("58013240/json/")
    suspend fun getEndereço(): Endereco
}