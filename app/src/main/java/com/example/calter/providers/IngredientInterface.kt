package com.example.calter.providers

import com.example.calter.models.ListIngredient
import com.example.calter.models.ListIngredientSuggestion
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface IngredientInterface {
    @POST("v2/natural/nutrients")
    suspend fun getIngredient(@Header("x-app-id") appId:String,
                              @Header("x-app-key") appKey:String,
                              @Header("Content-Type") contentType:String = "application/json",
                              @Body body: QueryRequest): ListIngredient
    @POST("v2/search/instant")
    suspend fun getIngredientSuggestions(@Header("x-app-id") appId:String,
                              @Header("x-app-key") appKey:String,
                              @Header("Content-Type") contentType:String = "application/json",
                              @Body body: QueryRequest): ListIngredientSuggestion
}


class QueryRequest (val query: String)