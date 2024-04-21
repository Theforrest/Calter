package com.example.calter.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Thumbnail(
    @SerializedName("thumb") val thumb: String,
): Serializable
data class IngredientSuggestion(
    @SerializedName("food_name") val name: String,
    @SerializedName("photo") val thumbnail: Thumbnail,
    ): Serializable

data class ListIngredientSuggestion(
    @SerializedName("common") val suggestions: List<IngredientSuggestion>,
    )
