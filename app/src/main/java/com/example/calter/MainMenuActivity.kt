package com.example.calter

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.calter.databinding.ActivityMainMenuBinding
import com.example.calter.fragments.CustomListFragment
import com.example.calter.fragments.ListFragment
import com.example.calter.fragments.OnFragmentActionListener
import com.example.calter.fragments.StatsFragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class MainMenuActivity : AppCompatActivity(), OnFragmentActionListener, NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainMenuBinding
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        setSupportActionBar(binding.mtMainMenu)
        val toggle = ActionBarDrawerToggle(
            this,
            binding.dlMainMenu,
            binding.mtMainMenu,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding.dlMainMenu.addDrawerListener(toggle)
        toggle.syncState()

        onBackPressedDispatcher.addCallback {
            if (binding.dlMainMenu.isDrawerOpen(GravityCompat.START)) {
                binding.dlMainMenu.closeDrawer(GravityCompat.START)
            }
        }
        binding.nvMainMenu.setNavigationItemSelectedListener(this)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
            }
            onNavigationItemSelected(binding.nvMainMenu.menu.getItem(0))
        }


    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        for (i in 0..<binding.nvMainMenu.menu.size()) {
            binding.nvMainMenu.menu.getItem(i).setChecked(false)
        }

        item.setChecked(true)
        when (item.itemId) {
            R.id.item_list -> {
                loadFragment(ListFragment(), null)
            }

            R.id.item_custom -> {
                loadFragment(CustomListFragment(), null)
            }

            R.id.item_stats -> {
                loadFragment(StatsFragment(), null)
            }

            R.id.item_logoff -> {
                auth.signOut()
                finish()
            }
        }
        binding.dlMainMenu.closeDrawer(GravityCompat.START)
        return true
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