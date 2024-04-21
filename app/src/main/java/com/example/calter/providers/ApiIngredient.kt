package com.example.calter.providers

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiIngredient {
    private val retrofit2: Retrofit = Retrofit.Builder()
        .baseUrl("https://trackapi.nutritionix.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val apiIngredient: IngredientInterface = retrofit2.create(IngredientInterface::class.java)
}