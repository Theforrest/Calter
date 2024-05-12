package com.example.calter.fragments

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.calter.adapters.IngredientAdapter
import com.example.calter.databinding.FragmentListBinding
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
import java.time.format.DateTimeFormatter
import java.util.Locale


class ListFragment : Fragment() {

    private var listener: OnFragmentActionListener? = null
    private lateinit var binding: FragmentListBinding

    private var adapter = IngredientAdapter(emptyList())

    private lateinit var auth: FirebaseAuth

    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private var referenceListener: ValueEventListener? = null

    private var datePicker = DatePickerFragment {date -> setDate(date)}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        // Inflate the layout for this fragment
        binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        initDb()
        setRecycler()
        setListeners()
    }

    private fun initDb() {
        database = FirebaseDatabase.getInstance("https://calter-default-rtdb.europe-west1.firebasedatabase.app/")
        reference = database.getReference("users")
    }
    private fun setRecycler() {
        val layoutManager = GridLayoutManager(this.context, 1)
        binding.rvIngredientList.layoutManager = layoutManager
        binding.rvIngredientList.adapter = adapter
    }

    private fun setDate(date: String) {
        auth.uid?.let {uid ->
            referenceListener?.let { reference.child(uid).child("dates").child(binding.tvDateList.text.toString()).removeEventListener(it) }
            binding.tvDateList.text = date

            setReferenceListener(uid, date)
            adapter.list= emptyList()
            adapter.notifyDataSetChanged()
        }
    }
    private fun setListeners() {
        binding.btnAdd.setOnClickListener {
            loadAddFragment()
        }
        binding.tvDateList.setOnClickListener{
            datePicker.show(parentFragmentManager, "datePicker")

        }


        auth.uid?.let {uid ->
            val time = Calendar.getInstance().time
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val current = formatter.format(time)
            binding.tvDateList.text = current
            setReferenceListener(uid, current)

        }


    }


    private fun setReferenceListener(uid:String, date: String) {

        referenceListener = reference.child(uid).child("dates").child(date).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val ingredientList: MutableList<Ingredient> = mutableListOf()

                for (postSnapshot in snapshot.children) {
                    postSnapshot.getValue(Ingredient::class.java)
                        ?.let { ingredientList.add(it) }

                }
                val filteredIngredients = ingredientList.sortedBy { it.time}
                loadIngredients(filteredIngredients)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("AAAAAAAAAAA", error.message)
            }
        })
    }

    private fun loadAddFragment() {
        val bundle = Bundle().apply {
            putString("DATE", binding.tvDateList.text.toString())
        }
        listener?.loadFragment(AddFragment(),bundle)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentActionListener) listener=context
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
    private fun loadIngredients(ingredients: List<Ingredient>) {
        if (ingredients.isNotEmpty()) {
            lifecycleScope.launch (Dispatchers.IO) {
                adapter.list = ingredients

                withContext(Dispatchers.Main){
                    adapter.notifyDataSetChanged()
                }
            }

        }

    }
}