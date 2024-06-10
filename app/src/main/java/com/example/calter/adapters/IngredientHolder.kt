package com.example.calter.adapters

import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.calter.R
import com.example.calter.databinding.IngredientLayoutBinding
import com.example.calter.models.Ingredient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext

class IngredientHolder (v: View): RecyclerView.ViewHolder(v) {
    private val binding = IngredientLayoutBinding.bind(v)
    fun render(ingredient: Ingredient, delete: (Ingredient) -> Unit) {
        if (!ingredient.photo?.highres.isNullOrEmpty()) {
            Glide.with(binding.ivPhoto.context).load(ingredient.photo?.highres).into(binding.ivPhoto)
        } else {
            binding.ivPhoto.setImageResource(R.drawable.notfound)
        }
        binding.tvIngredientName.text = ingredient.name
        binding.tvTime.text = ingredient.time
        binding.tvCalories.text = ingredient.calories.toString()
        setText(binding.tvCarbohydrates, ingredient.carbohydrates)
        setText(binding.tvFat, ingredient.fat)
        setText(binding.tvFiber, ingredient.fiber)
        setText(binding.tvCholesterol, ingredient.cholesterol)
        setText(binding.tvPotassium, ingredient.potassium)
        setText(binding.tvProtein, ingredient.protein)
        setText(binding.tvSaturatedFat, ingredient.saturatedFat)
        setText(binding.tvSodium, ingredient.sodium)
        setText(binding.tvSugar, ingredient.sugars)

        binding.btnBorrar.setOnClickListener {
            val date = ingredient.date ?: ""
            delete(ingredient)
        }
    }
    private fun setText(view:TextView, number: Double?) {
        if (number != null && number > 0) {
            view.text = number.toString()
        } else {
            view.visibility = View.GONE
        }
    }
}