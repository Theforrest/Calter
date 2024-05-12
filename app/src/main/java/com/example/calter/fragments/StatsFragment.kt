package com.example.calter.fragments

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.calter.adapters.IngredientAdapter
import com.example.calter.databinding.FragmentListBinding
import com.example.calter.databinding.FragmentStatsBinding
import com.example.calter.models.Ingredient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.Year
import java.time.format.DateTimeFormatter
import java.util.Locale


class StatsFragment : Fragment() {

    private var listener: OnFragmentActionListener? = null
    private lateinit var binding: FragmentStatsBinding

    private lateinit var auth: FirebaseAuth

    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private var referenceListener: ValueEventListener? = null

    private var datePicker = DatePickerFragment {date -> setDate(date)}

    private var startDate = getCurrentDate()
    private var endDate = getCurrentDate()

    private var textView: TextView? = null
    var ingredientList: MutableList<Ingredient> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        // Inflate the layout for this fragment
        binding = FragmentStatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        initDb()
        setListeners()
    }

    private fun initDb() {
        database = FirebaseDatabase.getInstance("https://calter-default-rtdb.europe-west1.firebasedatabase.app/")
        reference = database.getReference("users")
    }

    private fun setDate(date: String) {
        textView?.let { it.text = date }
        startDate = binding.tvDateList2.text.toString()
        endDate = binding.tvDateList3.text.toString()
        auth.uid?.let {uid ->
            setReferenceListener(uid)

        }

    }
    private fun setListeners() {

        binding.tvDateList2.setOnClickListener{
            textView = binding.tvDateList2
            datePicker.show(parentFragmentManager, "datePicker")


        }
        binding.tvDateList3.setOnClickListener{
            textView = binding.tvDateList3

            datePicker.show(parentFragmentManager, "datePicker")

        }
        binding.button.setOnClickListener {
            startDate = getCurrentDate()
            binding.tvDateList2.text = startDate
            endDate = getCurrentDate()
            binding.tvDateList3.text = endDate
            auth.uid?.let {uid ->
                setReferenceListener(uid)

            }
        }
        binding.button2.setOnClickListener {
            val start = Calendar.getInstance()
            start.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().getActualMinimum(Calendar.DAY_OF_MONTH))
            val end = Calendar.getInstance()
            end.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH))

            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

            startDate = formatter.format(start.time)
            binding.tvDateList2.text = startDate
            endDate = formatter.format(end.time)
            binding.tvDateList3.text = endDate
            auth.uid?.let {uid ->
                setReferenceListener(uid)

            }
        }
        binding.button3.setOnClickListener {
            val start = Calendar.getInstance()
            start.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().getActualMinimum(Calendar.DAY_OF_MONTH))
            start.set(Calendar.MONTH, Calendar.getInstance().getActualMinimum(Calendar.MONTH))

            val end = Calendar.getInstance()
            end.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH))
            end.set(Calendar.MONTH, Calendar.getInstance().getActualMaximum(Calendar.MONTH))

            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

            startDate = formatter.format(start.time)
            binding.tvDateList2.text = startDate
            endDate = formatter.format(end.time)
            binding.tvDateList3.text = endDate
            auth.uid?.let {uid ->
                setReferenceListener(uid)

            }
        }


        auth.uid?.let {uid ->
            val current = getCurrentDate()
            binding.tvDateList2.text = current
            binding.tvDateList3.text = current
            setReferenceListener(uid)

        }


    }

    private fun getCurrentDate():String {
        val time = Calendar.getInstance().time
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formatter.format(time)
    }

    private fun setReferenceListener(uid:String) {
        referenceListener?.let { reference.child(uid).child("dates").removeEventListener(it) }
        referenceListener = reference.child(uid).child("dates").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                ingredientList = mutableListOf()

                for (postSnapshot in snapshot.children) {
                    val ingredientDate = postSnapshot.key.toString()
                    if (ingredientDate in startDate..endDate) {
                        for(postSnapshot2 in postSnapshot.children) {
                            postSnapshot2.getValue(Ingredient::class.java)
                                ?.let {
                                    ingredientList.add(it) }
                        }
                    }



                }

                loadStats()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentActionListener) listener=context
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
    private fun correctDouble(double: Double?): Double {
        if (double != null && double != -1.0) {
            return double
        }
        return 0.0
    }


    private fun loadStats() {
        var calories = 0.0
        var carbohydrates = 0.0
        var cholesterol = 0.0
        var fat = 0.0
        var fiber = 0.0
        var potassium = 0.0
        var protein = 0.0
        var saturatedFat = 0.0
        var sodium = 0.0
        var sugars = 0.0

        ingredientList.forEach {

            calories += correctDouble(it.calories)

            carbohydrates += correctDouble(it.carbohydrates)
             cholesterol += correctDouble(it.cholesterol)
             fat += correctDouble(it.fat)
             fiber += correctDouble(it.fiber)
             potassium += correctDouble(it.potassium)
             protein += correctDouble(it.protein)
             saturatedFat += correctDouble(it.saturatedFat)
             sodium += correctDouble(it.sodium)
             sugars += correctDouble(it.sugars)
        }

        binding.tvCalories.text = calories.toString()
        binding.tvCarbohydrates.text = carbohydrates.toString()
        binding.tvCholesterol.text = cholesterol.toString()
        binding.tvFat.text = fat.toString()
        binding.tvFiber.text = fiber.toString()
        binding.tvPotassium.text = potassium.toString()
        binding.tvProtein.text = protein.toString()
        binding.tvSaturatedFat.text = saturatedFat.toString()
        binding.tvSodium.text = sodium.toString()
        binding.tvSugar.text = sugars.toString()

    }
}