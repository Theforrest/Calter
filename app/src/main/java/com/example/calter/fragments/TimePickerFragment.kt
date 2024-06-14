package com.example.calter.fragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import com.example.calter.R
import java.util.Locale
import android.text.format.DateFormat;
import android.util.Log
import android.widget.TimePicker

/**

 *
 * @property onTimeSelected
 */
class TimePickerFragment(private var onTimeSelected: (String) -> Unit) : DialogFragment(), TimePickerDialog.OnTimeSetListener {
    /**
     *
     * @param savedInstanceState
     * @return
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current date as the default date in the picker.
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)
        // Create a new instance of DatePickerDialog and return it.
        return TimePickerDialog(requireContext(), this, hour, minute, DateFormat.is24HourFormat(activity))
    }

    /**
     *
     * @param p0
     * @param p1
     * @param p2
     */
    override fun onTimeSet(p0: TimePicker?, p1: Int, p2: Int) {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, p1)
        cal.set(Calendar.MINUTE, p2)
        val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
        val date = formatter.format(cal)
        onTimeSelected(date)
    }

}