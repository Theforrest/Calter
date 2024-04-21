package com.example.calter

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.calter.databinding.ActivityMainMenuBinding
import com.example.calter.fragments.ListFragment
import com.example.calter.fragments.OnFragmentActionListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainMenuActivity : AppCompatActivity(), OnFragmentActionListener {
    private lateinit var binding: ActivityMainMenuBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add(binding.fcvMain.id, ListFragment())
            }
        }
    }





    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_logoff -> {
                auth.signOut()
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun loadFragment(fragment: Fragment, bundle: Bundle?) {
        binding.fcvMain.removeAllViews()

        if (bundle != null) {
            fragment.arguments = bundle
        }

        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(binding.fcvMain.id, fragment)
        }
    }
}