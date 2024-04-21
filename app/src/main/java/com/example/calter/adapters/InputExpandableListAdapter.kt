package com.example.calter.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import com.example.calter.R
import com.example.calter.databinding.ListGroupBinding
import com.example.calter.databinding.ListItemBinding
import kotlinx.coroutines.withContext
import java.io.Console

class InputExpandableListAdapter(
    private var expandableList: HashMap<String, List<EditText>>,
    private var titleList: List<String>
): BaseExpandableListAdapter() {

    override fun getGroupCount() = expandableList.size

    override fun getChildrenCount(p0: Int) = expandableList[titleList[p0]]?.size ?: -1

    override fun getGroup(p0: Int)= titleList[p0]


    override fun getChild(p0: Int, p1: Int)= (expandableList[titleList[p0]])?.get(p1)

    override fun getGroupId(p0: Int)= p0.toLong()

    override fun getChildId(p0: Int, p1: Int)= p1.toLong()

    override fun hasStableIds() = false

    override fun getGroupView(p0: Int, p1: Boolean, p2: View?, p3: ViewGroup?): View {
        val v = LayoutInflater.from(p3?.context).inflate(R.layout.list_group, p3, false)
        val binding = ListGroupBinding.bind(v)
        binding.listTitle.text = getGroup(p0)
        return v
    }

    override fun getChildView(p0: Int, p1: Int, p2: Boolean, p3: View?, p4: ViewGroup?): View {
        val v = LayoutInflater.from(p4?.context).inflate(R.layout.list_item, p4, false)
        val binding = ListItemBinding.bind(v)
        val original = getChild(p0, p1) ?: binding.edListItem
        binding.edListItem.hint = original.hint
        binding.edListItem.text = original.text
        binding.edListItem.doOnTextChanged{ text, _, _, _ ->
            original.setText(text)
        }
        return v
    }

    override fun isChildSelectable(p0: Int, p1: Int)= false
}