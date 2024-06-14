package com.example.calter.adapters

import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.getString
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.calter.R
import com.example.calter.databinding.IngredientLayoutBinding
import com.example.calter.models.Ingredient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.coroutineContext

/**
 *
 *
 * @param v
 */
class IngredientHolder (v: View): RecyclerView.ViewHolder(v) {
    private val binding = IngredientLayoutBinding.bind(v)
    private var units: Double = 1.0
    private var grams: Double = -1.0

    /**
     *
     * @param ingredient
     * @param delete
     */
    fun render(ingredient: Ingredient, delete: (Ingredient) -> Unit) {
        if (!ingredient.photo?.highres.isNullOrEmpty()) {
            Glide.with(binding.ivPhoto.context).load(ingredient.photo?.highres).into(binding.ivPhoto)
        } else {
            binding.ivPhoto.setImageResource(R.drawable.notfound)
        }
        binding.tvIngredientName.text = ingredient.name?.uppercase()
        binding.tvTime.text = ingredient.time
        ingredient.grams?.let {
            grams = units.times(it)
        }
        ingredient.units?.let {

            if (it > 0.0 && grams > 0.0) {
                units = it

                binding.tvGrams.text =  String.format(Locale.US, getString(binding.tvGrams.context, R.string.gram), grams)
            } else {
                binding.tvGrams.visibility = View.GONE

            }
        }


        setText(binding.tvCalories, ingredient.calories, getString(binding.tvCalories.context, R.string.calories))
        setText(binding.tvCarbohydrates, ingredient.carbohydrates, getString(binding.tvCarbohydrates.context, R.string.carbohydrates))
        setText(binding.tvFat, ingredient.fat, getString(binding.tvFat.context, R.string.fat))
        setText(binding.tvFiber, ingredient.fiber, getString(binding.tvFiber.context, R.string.fiber))
        setText(binding.tvCholesterol, ingredient.cholesterol, getString(binding.tvCholesterol.context, R.string.cholesterol))
        setText(binding.tvPotassium, ingredient.potassium, getString(binding.tvPotassium.context, R.string.potassium))
        setText(binding.tvProtein, ingredient.protein, getString(binding.tvProtein.context, R.string.protein))
        setText(binding.tvSaturatedFat, ingredient.saturatedFat, getString(binding.tvSaturatedFat.context, R.string.saturated_fat))
        setText(binding.tvSodium, ingredient.sodium, getString(binding.tvSodium.context, R.string.sodium))
        setText(binding.tvSugar, ingredient.sugars, getString(binding.tvSugar.context, R.string.sugars))

        binding.btnDelete.setOnClickListener {
            delete(ingredient)
        }
    }

    /**
     *
     * @param view
     * @param number
     * @param format
     */
    private fun setText(view:TextView, number: Double?, format: String) {
        if (number != null && number > 0) {
            view.text = String.format(Locale.US, format, number.times(units))
        } else {
            view.visibility = View.GONE
        }
    }
}