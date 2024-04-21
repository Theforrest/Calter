package com.example.calter.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.calter.R
import com.example.calter.models.IngredientSuggestion

class SuggestionAdapter(var list: List<IngredientSuggestion>, private var onItemClick: (String) -> Unit): RecyclerView.Adapter<SuggestionHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestionHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.suggestion_layout, parent, false)
        return SuggestionHolder(v)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: SuggestionHolder, position: Int) {
        holder.render(list[position], onItemClick)
    }
}