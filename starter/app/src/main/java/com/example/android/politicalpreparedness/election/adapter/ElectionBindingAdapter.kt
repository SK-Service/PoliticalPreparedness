package com.example.android.politicalpreparedness.election.adapter

import android.util.Log
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.election.ElectionsViewModel
import com.example.android.politicalpreparedness.network.models.Election

@BindingAdapter("listData")
fun bindListData (recyclerview: RecyclerView, electionViewModel: ElectionsViewModel) {
    Log.i("BindngAdapter-listData", "inside bindListData")
    //Bind the ElectionRecyclerAdapter to the layout view - which references the RecyclerView
    recyclerview.adapter = ElectionListAdapter(
        ElectionListener{
            electionViewModel.displayElectiondDetails(it)
        } )
//    Log.i("ElectionsFragment",
//        "Adapter: ${binding.upcomingElectionRecycler.adapter ?: "value null"}")

    val adapter = recyclerview.adapter as ElectionListAdapter
    Log.i("BindngAdapter-listData", "After getting hold of Adapter\n")

    if (electionViewModel.listOfElection != null) {
        Log.i("BindngAdapter-listData", "Inside list null check, " +
                "list:<${electionViewModel.listOfElection.value?.size}>\n")

        adapter.submitList(electionViewModel.listOfElection.value )
        Log.i("BindngAdapter-listData", "after adapter.submitList\n")
    }
}