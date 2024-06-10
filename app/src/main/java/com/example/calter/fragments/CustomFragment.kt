package com.example.calter.fragments

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.calter.R
import com.example.calter.adapters.InputExpandableListAdapter
import com.example.calter.adapters.SuggestionAdapter
import com.example.calter.databinding.FragmentAddBinding
import com.example.calter.databinding.FragmentCustomBinding
import com.example.calter.models.Ingredient
import com.example.calter.models.Photo
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
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.util.Date
import java.util.Locale


class CustomFragment : Fragment() {

    private lateinit var binding: FragmentCustomBinding

    private lateinit var et_fat:EditText
    private lateinit var et_saturated_fat:EditText
    private lateinit var et_cholesterol:EditText
    private lateinit var et_carbohydrates:EditText
    private lateinit var et_fiber:EditText
    private lateinit var et_sugars:EditText
    private lateinit var et_protein:EditText
    private lateinit var et_potassium:EditText
    private lateinit var et_sodium:EditText
    private var imageBitmap: Bitmap? = null

    private lateinit var editTexts: List<EditText>

    private lateinit var adapter: InputExpandableListAdapter

    private lateinit var auth: FirebaseAuth

    private var storageRef = FirebaseStorage.getInstance().reference.child("images")

    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCustomBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        setEditTexts()
        setExpandableList()
        setListeners()
        initDb()


    }

    private fun initDb() {
        database = FirebaseDatabase.getInstance("https://calter-default-rtdb.europe-west1.firebasedatabase.app/")
        reference = database.getReference("users")
    }

    private fun setEditTexts() {
        et_fat= EditText(context)
        et_fat.hint = "Fat"
        et_saturated_fat= EditText(context)
        et_saturated_fat.hint = "Saturated Fat"
        et_cholesterol = EditText(context)
        et_cholesterol.hint = "Cholesterol"
        et_carbohydrates = EditText(context)
        et_carbohydrates.hint = "Carbohydrates"
        et_fiber = EditText(context)
        et_fiber.hint = "Fiber"
        et_sugars = EditText(context)
        et_sugars.hint = "Sugars"
        et_protein = EditText(context)
        et_protein.hint = "Protein"
        et_potassium = EditText(context)
        et_potassium.hint = "Potassium"
        et_sodium = EditText(context)
        et_sodium.hint = "Sodium"

        editTexts = listOf(et_fat, et_saturated_fat, et_cholesterol, et_carbohydrates,
        et_fiber, et_sugars, et_protein, et_potassium, et_sodium)
    }

    private fun setListeners() {
        binding.btnConfirm.setOnClickListener {
            createCustomIngredient()
        }
        binding.ibPhoto.setOnClickListener {
            selectImage()
        }
    }
    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            val projection = MediaStore.Images.Media.DATA
            val cursor = context?.contentResolver?.query(uri, arrayOf(projection), null, null, null)
            val columnIndex = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor?.moveToFirst()
            val filePath = columnIndex?.let { cursor.getString(it) }
            val bitmap = BitmapFactory.decodeFile(filePath)
            imageBitmap = bitmap
            binding.ibPhoto.setImageBitmap(bitmap)
        }
    }
    private fun selectImage() {

        getContent.launch("image/*")

    }
    private fun createCustomIngredient() {
        val name = binding.etNameIngredient.text.toString().trim()
        val calories = checkEditText(binding.etCalories.text.toString())
        val fat = checkEditText(et_fat.text.toString())
        val saturatedFat = checkEditText(et_saturated_fat.text.toString())
        val cholesterol = checkEditText(et_cholesterol.text.toString())
        val carbohydrates = checkEditText(et_carbohydrates.text.toString())
        val fiber = checkEditText(et_fiber.text.toString())
        val sugars =checkEditText(et_sugars.text.toString())
        val protein = checkEditText(et_protein.text.toString())
        val potassium = checkEditText(et_potassium.text.toString())
        val sodium = checkEditText(et_sodium.text.toString())
        var image = ""

        auth.uid?.let {uid ->
            imageBitmap?.let {
                val bitmap = it
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val data = baos.toByteArray()
                storageRef.child("${auth.uid}${Date()}").putBytes(data)
                    .addOnSuccessListener { uploadTask ->
                        uploadTask.metadata?.reference?.downloadUrl?.addOnSuccessListener {uri->

                            val ingredient = Ingredient(name= name,calories= calories,fat= fat,saturatedFat= saturatedFat,cholesterol= cholesterol,carbohydrates= carbohydrates, fiber= fiber, sugars = sugars, protein = protein, potassium = potassium, sodium = sodium, photo = Photo(uri.toString(), uri.toString()))


                            reference.child(uid).child("custom").push().setValue(ingredient)
                                .addOnSuccessListener {


                                    Toast.makeText(this.context, "Saved Succesfully", Toast.LENGTH_LONG).show()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this.context, "Error Saving", Toast.LENGTH_LONG).show()
                                }
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(this.context, "Error Saving", Toast.LENGTH_LONG).show()
                    }

            }


        }
    }

    private fun checkEditText(text: String): Double {
        val result = text.trim().toDoubleOrNull()
        return result ?: -1.0
    }


    private fun setExpandableList() {
        val titleList:List<String> = listOf("Extra options")
        val inputMap:HashMap<String, List<EditText>> = hashMapOf( titleList[0] to editTexts )
        adapter = InputExpandableListAdapter(inputMap, titleList)
        binding.expandableListViewSample.setAdapter(adapter)
    }
}