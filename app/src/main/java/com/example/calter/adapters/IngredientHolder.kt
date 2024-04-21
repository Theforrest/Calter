package com.example.calter.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.calter.databinding.IngredientLayoutBinding
import com.example.calter.models.Ingredient

class IngredientHolder (v: View): RecyclerView.ViewHolder(v) {
    private val binding = IngredientLayoutBinding.bind(v)
    fun render(ingredient: Ingredient) {
        Glide.with(binding.ivPhoto.context).load(ingredient.photo?.highres).into(binding.ivPhoto)
        binding.tvNameIngredient.text = ingredient.name
        binding.tvCalories.text = ingredient.calories.toString()
        binding.tvCarbohydrates.text = ingredient.carbohydrates.toString()
        binding.tvFat.text = ingredient.fat.toString()
        binding.tvFiber.text = ingredient.fiber.toString()
        binding.tvCholesterol.text = ingredient.cholesterol.toString()
        binding.tvPotassium.text = ingredient.potassium.toString()
        binding.tvProtein.text = ingredient.protein.toString()
        binding.tvSaturatedFat.text = ingredient.saturatedFat.toString()
        binding.tvSodium.text = ingredient.sodium.toString()
        binding.tvSugar.text = ingredient.sugars.toString()



    }
}