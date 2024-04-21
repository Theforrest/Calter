package com.example.calter.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Photo(
    @SerializedName("thumb") val thumb: String? = "",
    @SerializedName("highres") val highres: String? = "",
): Serializable
data class Ingredient(
    @SerializedName("food_name") val name: String? = "",
    @SerializedName("serving_weight_grams") val grams: Double? = 0.0,
    @SerializedName("nf_calories") val calories: Double? = 0.0,
    @SerializedName("nf_total_fat") val fat: Double? = 0.0,
    @SerializedName("nf_saturated_fat") val saturatedFat: Double? = 0.0,
    @SerializedName("nf_cholesterol") val cholesterol: Double? = 0.0,
    @SerializedName("nf_total_carbohydrate") val carbohydrates: Double? = 0.0,
    @SerializedName("nf_dietary_fiber") val fiber: Double? = 0.0,
    @SerializedName("nf_sugars") val sugars: Double? = 0.0,
    @SerializedName("nf_protein") val protein: Double? = 0.0,
    @SerializedName("nf_potassium") val potassium: Double? = 0.0,
    @SerializedName("nf_sodium") val sodium: Double? = 0.0,


    @SerializedName("photo") val photo: Photo? = null,
    ): Serializable

data class ListIngredient(
    @SerializedName("foods") val ingredients: List<Ingredient>,
    )
