package com.example.calter.fragments

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.calter.R
import com.example.calter.adapters.SuggestionAdapter
import com.example.calter.databinding.FragmentAddBinding
import com.example.calter.models.Ingredient
import com.example.calter.providers.ApiIngredient
import com.example.calter.providers.QueryRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale


class AddFragment : Fragment() {

    private lateinit var binding: FragmentAddBinding

    private var adapter = SuggestionAdapter(emptyList()) {ingredientName -> loadCardView(ingredientName)}

    private lateinit var auth: FirebaseAuth

    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference

    private var searchJob: Job? = null

    private var ingredient: Ingredient? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        setListeners()
        setRecycler()
        initDb()
    }

    private fun initDb() {
        database = FirebaseDatabase.getInstance("https://calter-default-rtdb.europe-west1.firebasedatabase.app/")
        reference = database.getReference("users")
    }

    private fun setRecycler() {
        val layoutManager = GridLayoutManager(this.context, 1)
        binding.rvSuggestions.layoutManager = layoutManager
        binding.rvSuggestions.adapter = adapter
    }

    private fun setListeners() {
        binding.btnConfirm.setOnClickListener {
            addIngredient()
        }
        binding.svIngredients.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val search = newText.toString().trim().lowercase()
                searchDebounced(search)
                return false
            }

        })
    }

    private fun addIngredient() {
        if (ingredient != null) {
            auth.uid?.let {uid ->
                val time = Calendar.getInstance().time
                val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val current = formatter.format(time)
                reference.child(uid).child(current).push().setValue(ingredient)
                    .addOnSuccessListener {
                        Toast.makeText(this.context, "Saved Succesfully", Toast.LENGTH_LONG).show()
                    }
                    .addOnFailureListener() {
                        Toast.makeText(this.context, "Error Saving", Toast.LENGTH_LONG).show()
                    }
            }
        }

    }

    fun searchDebounced(search: String) {
        searchJob?.cancel()

        searchJob = lifecycleScope.launch {
            delay(500)
            loadSuggestions(search)
        }
    }
    private fun loadSuggestions(search: String) {
        if (search.isNotBlank()) {
            lifecycleScope.launch (Dispatchers.IO) {
                val results = ApiIngredient.apiIngredient.getIngredientSuggestions(appId = getString(R.string.application_id), appKey = getString(R.string.application_key), body = QueryRequest(search)).suggestions
                adapter.list = results

                withContext(Dispatchers.Main){
                    adapter.notifyDataSetChanged()
                }
            }
        }

    }

    private fun loadCardView(ingredientName: String) {
        if (binding.svIngredients.query.trim().isNotEmpty()) {
            lifecycleScope.launch (Dispatchers.IO) {
                val results = ApiIngredient.apiIngredient.getIngredient(appId = getString(R.string.application_id), appKey = getString(R.string.application_key), body = QueryRequest(ingredientName))

                withContext(Dispatchers.Main) {
                    binding.tvDefault.visibility = View.INVISIBLE
                    binding.tvCalories.visibility = View.VISIBLE
                    binding.tvNameIngredient.visibility = View.VISIBLE
                    binding.ivPhoto.visibility = View.VISIBLE

                    ingredient = results.ingredients[0]
                    binding.tvNameIngredient.text= results.ingredients[0].name?.uppercase()
                    binding.tvCalories.text= results.ingredients[0].calories.toString()
                    Glide.with(binding.ivPhoto.context).load(results.ingredients[0].photo?.highres).into(binding.ivPhoto)
                }

            }
        }
    }

}