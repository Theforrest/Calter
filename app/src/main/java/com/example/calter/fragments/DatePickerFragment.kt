package com.example.calter.fragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import android.icu.util.Calendar
import java.util.Locale

/**

 *
 * @property onDateSelected
 */
class DatePickerFragment(private var onDateSelected: (String) -> Unit) : DialogFragment(), DatePickerDialog.OnDateSetListener {
    /**
     *
     * @param savedInstanceState
     * @return
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current date as the default date in the picker.
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        // Create a new instance of DatePickerDialog and return it.
        return DatePickerDialog(requireContext(), this, year, month, day)
    }

    /**
     *
     * @param p0
     * @param p1
     * @param p2
     * @param p3
     */
    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, p1)
        cal.set(Calendar.MONTH, p2)
        cal.set(Calendar.DAY_OF_MONTH, p3)
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = formatter.format(cal)
        onDateSelected(date)
    }


}