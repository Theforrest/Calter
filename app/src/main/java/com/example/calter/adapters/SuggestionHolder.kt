package com.example.calter.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.calter.databinding.SuggestionLayoutBinding
import com.example.calter.models.IngredientSuggestion

/**

 *
 * @property v
 */
class SuggestionHolder (private var v: View): RecyclerView.ViewHolder(v) {
    private val binding = SuggestionLayoutBinding.bind(v)

    /**
     *
     * @param suggestion
     * @param onItemClick
     */
    fun render(suggestion: IngredientSuggestion, onItemClick: (String) -> Unit) {
        Glide.with(binding.ivThumb.context).load(suggestion.thumbnail.thumb).into(binding.ivThumb)
        binding.tvName.text = suggestion.name

        v.setOnClickListener {
            onItemClick(suggestion.name)
        }
    }
}