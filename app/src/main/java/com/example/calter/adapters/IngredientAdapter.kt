package com.example.calter.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.calter.R
import com.example.calter.models.Ingredient

/**
 *
 * @property list
 * @property delete
 */
class IngredientAdapter(var list: List<Ingredient>, private var delete: (Ingredient) -> Unit): RecyclerView.Adapter<IngredientHolder>() {
    /**
     *
     * @param parent
     * @param viewType
     * @return
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.ingredient_layout, parent, false)
        return IngredientHolder(v)
    }

    /**
     *
     */
    override fun getItemCount() = list.size

    /**
     *
     * @param holder
     * @param position
     */
    override fun onBindViewHolder(holder: IngredientHolder, position: Int) {
        holder.render(list[position], delete)
    }
}