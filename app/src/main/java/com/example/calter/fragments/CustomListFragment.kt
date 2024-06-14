package com.example.calter.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.calter.adapters.IngredientAdapter
import com.example.calter.databinding.FragmentCustomListBinding
import com.example.calter.models.Ingredient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 *
 */
class CustomListFragment : OnFragmentBack() {

    private var listener: OnFragmentActionListener? = null
    private lateinit var binding: FragmentCustomListBinding

    private var adapter = IngredientAdapter(emptyList()) { ingredient -> deleteIngredient(ingredient)}

    private lateinit var auth: FirebaseAuth

    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    private var referenceListener: ValueEventListener? = null

    /**
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        // Inflate the layout for this fragment
        binding = FragmentCustomListBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     *
     * @param view
     * @param savedInstanceState
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        initDb()
        setRecycler()
        setListeners()
    }

    /**
     *
     */
    private fun initDb() {
        database = FirebaseDatabase.getInstance("https://calter-default-rtdb.europe-west1.firebasedatabase.app/")
        reference = database.getReference("users")
    }

    /**
     *
     */
    private fun setRecycler() {
        val layoutManager = GridLayoutManager(this.context, 1)
        binding.rvIngredientListCustom.layoutManager = layoutManager
        binding.rvIngredientListCustom.adapter = adapter
    }

    /**
     *
     */
    private fun setListeners() {
        binding.btnAddCustom.setOnClickListener {
            loadAddCustomFragment()
        }

        auth.uid?.let {uid ->

            setReferenceListener(uid)

        }


    }

    /**
     *
     * @param uid
     */
    private fun setReferenceListener(uid:String) {

        referenceListener = reference.child(uid).child("custom").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val ingredientList: MutableList<Ingredient> = mutableListOf()

                for (postSnapshot in snapshot.children) {
                    postSnapshot.getValue(Ingredient::class.java)
                        ?.let {
                            it.id = postSnapshot.key
                            ingredientList.add(it) }

                }
                val filteredIngredients = ingredientList.sortedBy { it.time}
                loadIngredients(filteredIngredients)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("AAAAAAAAAAA", error.message)
            }
        })
    }

    /**
     *
     */
    private fun loadAddCustomFragment() {

        listener?.loadFragment(CustomFragment(),null)
    }

    /**
     *
     */
    override fun onBackPressed() {
        listener?.openDrawer()
    }

    /**
     *
     * @param context
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentActionListener) listener=context
    }

    /**
     *
     */
    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     *
     * @param ingredients
     */
    private fun loadIngredients(ingredients: List<Ingredient>) {
        if (ingredients.isNotEmpty()) {
            binding.ivEmpty.visibility = View.INVISIBLE
            binding.tvEmpty.visibility = View.INVISIBLE
        } else {
            binding.ivEmpty.visibility = View.VISIBLE
            binding.tvEmpty.visibility = View.VISIBLE
        }
        lifecycleScope.launch (Dispatchers.IO) {
            adapter.list = ingredients

            withContext(Dispatchers.Main){
                adapter.notifyDataSetChanged()
            }
        }
    }

    /**
     *
     * @param ingredient
     */
    private fun deleteIngredient(ingredient: Ingredient) {
        val uid = auth.uid
        uid?.let {
            Log.e("AAAAAAAAAA", ingredient.id ?: "")
            ingredient.id?.let {
                it1 -> reference.child(it).child("custom").child(it1).removeValue()
                ingredient.photo?.thumb?.let { it2 ->
                    FirebaseStorage.getInstance().getReferenceFromUrl(
                        it2
                    ).delete()
                }
            }
        }

    }
}