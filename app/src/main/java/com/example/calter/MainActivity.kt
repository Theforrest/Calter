package com.example.calter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import com.example.calter.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

/**

 *
 */
class MainActivity : AppCompatActivity() {

    private var responseLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode== RESULT_OK){
            val task= GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try {
                val account = task.getResult(ApiException::class.java)
                if(account!=null){
                    val credential = GoogleAuthProvider.getCredential(account.idToken,null)
                    FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener{ completeTask ->
                        if(completeTask.isSuccessful){
                            goMainMenuActivity()
                        }
                    }
                }
            }catch (_: ApiException){
            }
        }
    }

    private lateinit var binding : ActivityMainBinding

    private var email=""
    private var password=""



    private lateinit var auth: FirebaseAuth

    /**
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        setListener()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }

    /**
     *
     */
    override fun onStart() {
        super.onStart()
        val user = auth.currentUser
        if(user!=null){
            goMainMenuActivity()
        }
        resetFields()
    }

    /**
     *
     */
    private fun resetFields() {
        email=""
        password=""
        binding.etEmail.setText(email)
        binding.etPassword.setText(password)
    }

    /**
     *
     */
    private fun setListener() {
        binding.btnRegistrar.setOnClickListener {
            if(checkFields()){
                basicRegistration()
            }
        }
        binding.btnLogin.setOnClickListener {
            if(checkFields()){
                basicLogin()
            }
        }
        binding.btnGoogle.setOnClickListener{
            loginGoogle()
        }
    }

    /**
     *
     */
    private fun loginGoogle() {
        val googleConf= GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.google_id)).requestEmail().build()
        val googleClient= GoogleSignIn.getClient(this,googleConf)
        googleClient.signOut()


        responseLauncher.launch(googleClient.signInIntent)
    }

    /**
     *
     */
    private fun basicLogin() {
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener{
            if(it.isSuccessful){
                goMainMenuActivity()
            }else{
                Toast.makeText(this, "Email or Password are incorrect", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     *
     */
    private fun goMainMenuActivity() {
        startActivity(Intent(this,MainMenuActivity::class.java))
    }

    /**
     *
     * @return
     */
    private fun checkFields(): Boolean {
        email=binding.etEmail.text.toString().trim()
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.etEmail.error="Introduce a valid email"
            return false
        }
        password=binding.etPassword.text.toString().trim()
        if(password.length < 6){
            binding.etPassword.error="Password must be at least six characters"
            return false
        }
        return true
    }

    /**
     *
     */
    private fun basicRegistration() {
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener{
            if(it.isSuccessful){
                basicLogin()
            }else{
                Toast.makeText(this, "User already exists", Toast.LENGTH_SHORT).show()
            }
        }
    }
}