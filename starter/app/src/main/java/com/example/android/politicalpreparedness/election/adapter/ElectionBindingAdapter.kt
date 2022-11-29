package com.example.android.politicalpreparedness.election.adapter

import android.util.Log
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.network.models.Election

@BindingAdapter("listData")
fun bindListData (recyclerview: RecyclerView, list:List<Election>?) {
    Log.i("BindngAdapter-listData", "inside bindListData")

    if (list != null) {
        Log.i("BindngAdapter-listData", "Inside list null check, list:<${list?.size}>\n")
        val adapter = recyclerview.adapter as ElectionListAdapter
        Log.i("BindngAdapter-listData", "After getting hold of Adapter\n")
        adapter.submitList(list)
        Log.i("BindngAdapter-listData", "after adapter.submitList\n")
    }
}