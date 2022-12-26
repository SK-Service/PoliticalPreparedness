package com.example.android.politicalpreparedness.representative.adapter

import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.election.ElectionsViewModel
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.election.adapter.ElectionListener
import com.example.android.politicalpreparedness.representative.RepresentativeViewModel

@BindingAdapter("profileImage")
fun fetchImage(view: ImageView, src: String?) {
    src?.let {
        val uri = src.toUri().buildUpon().scheme("https").build()
        //TODO: Add Glide call to load image and circle crop - user ic_profile as a placeholder and for errors.
    }
}

@BindingAdapter("stateValue")
fun Spinner.setNewValue(value: String?) {
    val adapter = toTypedAdapter<String>(this.adapter as ArrayAdapter<*>)
    val position = when (adapter.getItem(0)) {
        is String -> adapter.getPosition(value)
        else -> this.selectedItemPosition
    }
    if (position >= 0) {
        setSelection(position)
    }
}

inline fun <reified T> toTypedAdapter(adapter: ArrayAdapter<*>): ArrayAdapter<T>{
    return adapter as ArrayAdapter<T>
}

@BindingAdapter("listRepresentatives")
fun bindRepresentativeList (recyclerview: RecyclerView, representativeViewModel: RepresentativeViewModel?) {
//    Log.i("BindngAdapter-bindElectionList", "inside bindElectionList")
//    Log.i("BindngAdapter-listElection",
//        "list:<${representativeViewModel?.listOfElection?.value?.size}>\n")
//    //Bind the ElectionRecyclerAdapter to the layout view - which references the RecyclerView
//    recyclerview.adapter = ElectionListAdapter(
//        ElectionListener{
//            representativeViewModel?.displayVoterInfo(it)
//        } )
//
//    val adapter = recyclerview.adapter as ElectionListAdapter
//    Log.i("BindngAdapter-listElection", "After getting hold of Adapter\n")
////@TODO - Handle when there are no election - may be we can add No Election to the list
//    if (representativeViewModel?.listOfElection != null) {
//        Log.i("BindngAdapter-listElection", "election list is not null\n")
//        adapter.submitList(representativeViewModel?.listOfElection.value )
//    }
}
