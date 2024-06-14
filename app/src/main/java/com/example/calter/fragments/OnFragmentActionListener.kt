package com.example.calter.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment

/**

 *
 */
interface OnFragmentActionListener {
    /**
     *
     * @param fragment
     * @param bundle
     */
    fun loadFragment(fragment: Fragment, bundle: Bundle?)

    /**
     *
     */
    fun openDrawer()
}