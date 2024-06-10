package com.example.calter.fragments

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.calter.R
import com.example.calter.adapters.SuggestionAdapter
import com.example.calter.databinding.FragmentAddBinding
import com.example.calter.models.Ingredient
import com.example.calter.models.IngredientSuggestion
import com.example.calter.models.Photo
import com.example.calter.models.Thumbnail
import com.example.calter.providers.ApiIngredient
import com.example.calter.providers.QueryRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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
    private var adapterCustom = SuggestionAdapter(emptyList()) {ingredientName -> loadCardViewCustom(ingredientName)}

    private lateinit var auth: FirebaseAuth

    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference

    private var date = ""
    private var time = ""

    private var searchJob: Job? = null

    private var ingredient: Ingredient? = null
    private var grams: Double = 0.0
    private var units: Double = 0.0
    private var ingredientsCustom: List<Ingredient> = emptyList()

    private var timePicker = TimePickerFragment {time -> setTime(time) }

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
        initDb()
        recoverData()
        setListeners()
        setRecycler()
        val time = Calendar.getInstance()
        val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
        binding.tvTime.text = formatter.format(time)
        binding.btnDefault.text = String.format(Locale.US, getString(R.string.default_search), "↓")
        binding.btnCustom.text = String.format(Locale.US, getString(R.string.custom_search), "↑")

    }
    private fun recoverData() {
        val bundle: Bundle? = arguments
        if (bundle!=null) {
            date = bundle.getString("DATE").toString()
        }
    }
    private fun initDb() {
        database = FirebaseDatabase.getInstance("https://calter-default-rtdb.europe-west1.firebasedatabase.app/")
        reference = database.getReference("users")
    }

    private fun setRecycler() {
        binding.rvSuggestions.layoutManager = GridLayoutManager(context, 1)
        binding.rvSuggestions.adapter = adapter
        binding.rvSuggestionsCustom.layoutManager = GridLayoutManager(context, 1)
        binding.rvSuggestionsCustom.adapter = adapterCustom
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
        binding.btnTimepicker.setOnClickListener {
            val delayMillis: Long = 900
            it?.isClickable = false
            it.postDelayed({ it.isClickable = true }, delayMillis)
            timePicker.show(parentFragmentManager, "timePicker")

        }
        auth.uid?.let {uid ->
            reference.child(uid).child("custom").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    val ingredientList: MutableList<Ingredient> = mutableListOf()

                    for (postSnapshot in snapshot.children) {

                        postSnapshot.getValue(Ingredient::class.java)
                            ?.let { ingredientList.add(it) }

                    }
                    ingredientsCustom = ingredientList
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("AAAAAAAAAAA", error.message)
                }
            })

        }
        binding.etGrams.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                binding.rvSuggestions.visibility = View.GONE
                binding.btnDefault.text = String.format(Locale.US, getString(R.string.default_search), "↑")
                binding.rvSuggestionsCustom.visibility = View.GONE
                binding.btnCustom.text = String.format(Locale.US, getString(R.string.custom_search), "↑")
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().trim().isNotEmpty() && s.toString().trim() != ".") {
                    val calculatedUnits = s.toString().trim().toDouble() / grams
                    if (calculatedUnits != units) {
                        units = calculatedUnits
                        binding.etUnits.setText(String.format(Locale.US,"%.2f", calculatedUnits))
                    }
                } else {
                    binding.etUnits.setText("0")

                }
            }
        })
        binding.btnDefault.setOnClickListener {
            if (binding.rvSuggestions.visibility == View.VISIBLE) {
                binding.rvSuggestions.visibility = View.GONE
                binding.btnDefault.text = String.format(Locale.US, getString(R.string.default_search), "↑")
            } else {
                binding.rvSuggestions.visibility = View.VISIBLE
                binding.btnDefault.text = String.format(Locale.US, getString(R.string.default_search), "↓")
            }
        }
        binding.btnCustom.setOnClickListener {
            if (binding.rvSuggestionsCustom.visibility == View.VISIBLE) {
                binding.rvSuggestionsCustom.visibility = View.GONE
                binding.btnCustom.text = String.format(Locale.US, getString(R.string.custom_search), "↑")
            } else {
                binding.rvSuggestionsCustom.visibility = View.VISIBLE
                binding.btnCustom.text = String.format(Locale.US, getString(R.string.custom_search), "↓")
            }
        }
    }

    private fun setTime(time: String) {
        binding.tvTime.text = time
        this.time = time
    }

    private fun addIngredient() {
        if (ingredient != null) {
            auth.uid?.let {uid ->

                val time = Calendar.getInstance()

                val result = date.ifEmpty {
                    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    formatter.format(time)
                }
                ingredient?.let { it.time = this.time.ifEmpty {
                    val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
                    formatter.format(time)
                }
                    it.units = units
                }
                reference.child(uid).child("dates").child(result).push().setValue(ingredient)
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
            loadCustomSuggestions(search)
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
    private fun loadCustomSuggestions(search: String) {
        if (search.isNotBlank()) {
            val tempList: MutableList<IngredientSuggestion> = mutableListOf()
            ingredientsCustom.forEach {
                val name = it.name ?: ""
                val thumbnail = it.photo?.thumb ?: ""
                if (name.length >= search.length && name.substring(0, search.length) == search) {
                    tempList.add(IngredientSuggestion(name = name, thumbnail = Thumbnail(thumbnail)))
                }
            }
            lifecycleScope.launch (Dispatchers.IO) {
                adapterCustom.list = tempList

                withContext(Dispatchers.Main){
                    adapterCustom.notifyDataSetChanged()
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
                    binding.cvIngredient.visibility = View.VISIBLE

                    ingredient = results.ingredients[0]
                    binding.tvIngredientName.text= results.ingredients[0].name?.uppercase()
                    binding.etGrams.setText(results.ingredients[0].calories.toString())
                    grams = results.ingredients[0].calories ?: 0.0
                    binding.etUnits.setText("1")
                    units = 1.0
                    if (!results.ingredients[0].photo?.highres.isNullOrEmpty()) {
                        Glide.with(binding.ivPhoto.context).load(results.ingredients[0].photo?.highres).into(binding.ivPhoto)
                    } else {
                        binding.ivPhoto.setImageResource(R.drawable.notfound)
                    }

                    val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                    imm?.hideSoftInputFromWindow(binding.svIngredients.windowToken, 0)
                }

            }
        }
    }
    private fun loadCardViewCustom(ingredientName: String) {
        if (binding.svIngredients.query.trim().isNotEmpty()) {
            val ingredient = ingredientsCustom.find { it.name == ingredientName }
            if (ingredient!= null) {
                binding.tvDefault.visibility = View.INVISIBLE
                binding.cvIngredient.visibility = View.VISIBLE

                this.ingredient = ingredient

                binding.tvIngredientName.text= ingredient.name?.uppercase()
                binding.etGrams.setText(ingredient.calories.toString())
                grams = ingredient.calories ?: 0.0
                binding.etUnits.setText("1")
                units = 1.0
                if (!ingredient.photo?.highres.isNullOrEmpty()) {
                    Glide.with(binding.ivPhoto.context).load(ingredient.photo?.highres).into(binding.ivPhoto)
                } else {
                    binding.ivPhoto.setImageResource(R.drawable.notfound)
                }

                val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(binding.svIngredients.windowToken, 0)

            }

        }
    }
}