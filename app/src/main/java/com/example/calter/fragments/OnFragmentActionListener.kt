package com.example.calter.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment

interface OnFragmentActionListener {
    fun loadFragment(fragment: Fragment, bundle: Bundle?)
}