package com.example.calter.fragments

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.example.calter.R
import com.example.calter.databinding.FragmentCustomBinding
import com.example.calter.models.Ingredient
import com.example.calter.models.Photo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.util.Date
import java.util.Locale

/**

 *
 */
class CustomFragment : OnFragmentBack() {

    private lateinit var binding: FragmentCustomBinding
    private var listener: OnFragmentActionListener? = null

    private var imageBitmap: Bitmap? = null

    private lateinit var auth: FirebaseAuth

    private var storageRef = FirebaseStorage.getInstance().reference.child("images")

    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference

    /**
     *
     */
    override fun onBackPressed() {
        backToList()
    }

    /**
     *
     */
    private fun backToList() {
        listener?.loadFragment(CustomListFragment(), null)
    }

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
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCustomBinding.inflate(inflater, container, false)
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
        setListeners()
        initDb()

        binding.btnOptional.text = String.format(Locale.US, getString(R.string.optional), "↑")


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
    private fun setListeners() {
        binding.btnConfirm.setOnClickListener {
            if (checkFields()) {
                createCustomIngredient()
            }
        }
        binding.ibPhoto.setOnClickListener {
            selectImage()
        }
        binding.btnOptional.setOnClickListener {
            if (binding.gEdittext.visibility == View.VISIBLE) {
                binding.gEdittext.visibility = View.GONE
                binding.btnOptional.text = String.format(Locale.US, getString(R.string.optional), "↑")
            } else {
                binding.gEdittext.visibility = View.VISIBLE
                binding.btnOptional.text = String.format(Locale.US, getString(R.string.optional), "↓")
            }
        }
    }

    /**
     *
     * @return
     */
    private fun checkFields(): Boolean {
        val name = binding.etNameIngredient.text.toString().trim()
        if(name.isBlank()){
            binding.etNameIngredient.error="Name required"
            return false
        }
        val grams = checkEditText(binding.etGrams.text.toString())
        if(grams < 0.0){
            binding.etGrams.error="Grams required"
            return false
        }
        val calories = checkEditText(binding.etCalories.text.toString())
        if(calories < 0.0){
            binding.etGrams.error="Calories required"
            return false
        }
        return true
    }

    /**
     *
     */
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

    /**
     *
     */
    private fun selectImage() {

        getContent.launch("image/*")

    }

    /**
     *
     */
    private fun createCustomIngredient() {
        val grams = checkEditText(binding.etGrams.text.toString())
        val name = binding.etNameIngredient.text.toString().trim()
        val calories = checkEditText(binding.etCalories.text.toString())
        val fat = checkEditText(binding.etFat.text.toString())
        val saturatedFat = checkEditText(binding.etSaturatedFat.text.toString())
        val cholesterol = checkEditText(binding.etCholesterol.text.toString())
        val carbohydrates = checkEditText(binding.etCarbohydrates.text.toString())
        val fiber = checkEditText(binding.etFiber.text.toString())
        val sugars =checkEditText(binding.etSugars.text.toString())
        val protein = checkEditText(binding.etProtein.text.toString())
        val potassium = checkEditText(binding.etPotassium.text.toString())
        val sodium = checkEditText(binding.etSodium.text.toString())

        auth.uid?.let {uid ->
            if (imageBitmap != null) {
                val bitmap = imageBitmap
                val baos = ByteArrayOutputStream()
                bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val data = baos.toByteArray()
                storageRef.child("${auth.uid}${Date()}").putBytes(data)
                    .addOnSuccessListener { uploadTask ->
                        uploadTask.metadata?.reference?.downloadUrl?.addOnSuccessListener {uri->

                            val ingredient = Ingredient(name= name,calories= calories, grams = grams, fat= fat,saturatedFat= saturatedFat,cholesterol= cholesterol,carbohydrates= carbohydrates, fiber= fiber, sugars = sugars, protein = protein, potassium = potassium, sodium = sodium, photo = Photo(uri.toString(), uri.toString()))


                            reference.child(uid).child("custom").push().setValue(ingredient)
                                .addOnSuccessListener {

                                    Toast.makeText(this.context, "Saved Succesfully", Toast.LENGTH_LONG).show()
                                    backToList()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this.context, "Error Saving", Toast.LENGTH_LONG).show()
                                }
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(this.context, "Error Saving", Toast.LENGTH_LONG).show()
                    }
            } else {
                val ingredient = Ingredient(name= name,calories= calories, grams = grams, fat= fat,saturatedFat= saturatedFat,cholesterol= cholesterol,carbohydrates= carbohydrates, fiber= fiber, sugars = sugars, protein = protein, potassium = potassium, sodium = sodium)


                reference.child(uid).child("custom").push().setValue(ingredient)
                    .addOnSuccessListener {

                        Toast.makeText(this.context, "Saved Succesfully", Toast.LENGTH_LONG).show()
                        backToList()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this.context, "Error Saving", Toast.LENGTH_LONG).show()
                    }
            }

        }
    }

    /**
     *
     * @param text
     * @return
     */
    private fun checkEditText(text: String): Double {
        val result = text.trim().toDoubleOrNull()
        return result ?: -1.0
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
}