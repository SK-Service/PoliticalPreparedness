package com.example.android.politicalpreparedness.representative.adapter

import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.election.ElectionsViewModel
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.election.adapter.ElectionListener
import com.example.android.politicalpreparedness.representative.RepresentativeViewModel
import com.squareup.picasso.Picasso

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
    Log.i("BindngAdapter-listRepresentatives", "inside bindRepresentativeList")
    Log.i("BindngAdapter-listRepresentatives",
        "list:<${representativeViewModel?.listOfRepresentatives?.value?.size}>\n")
    //Bind the ElectionRecyclerAdapter to the layout view - which references the RecyclerView
    recyclerview.adapter = RepresentativeListAdapter()

    val adapter = recyclerview.adapter as RepresentativeListAdapter
    Log.i("BindngAdapter-listRepresentatives", "After getting hold of Adapter\n")

    //@TODO - Handle when there are no saved election - may be we can add No Election to the list
    if (representativeViewModel?.listOfRepresentatives?.value != null) {
        Log.i("BindngAdapter-listRepresentatives", "List of representatives is not null\n")
        if(!representativeViewModel?.listOfRepresentatives?.value!!.isEmpty()) {
            Log.i("BindngAdapter-listRepresentatives", "List of representatives is not null and not empty\n")
            Log.i("BindngAdapter-listRepresentatives",
                "Value:<${representativeViewModel?.listOfRepresentatives?.value}>\n")

            adapter.submitList(representativeViewModel?.listOfRepresentatives.value )
        }

    }
}

@BindingAdapter("repProfileImage")
fun bindDetailsStatusImage(imageView: ImageView, repProfileImageURL: String) {
    Log.i("BindingAdapter-repProfileImage", "Inside bindDetailsStatusImage")
    Log.i("BindingAdapter-repProfileImage", "repProfileImageURL:<${repProfileImageURL}>")
    if (!repProfileImageURL.isNullOrEmpty()) {
        Log.i("BindingAdapter-repProfileImage", "Profile URL is not null")
        imageView.loadImage(repProfileImageURL)
        imageView.contentDescription = "Image of the representative"
    } else {
        Log.i("BindingAdapter-repProfileImage", "Profile URL is null")
        imageView.setImageResource(R.drawable.ic_profile)
        imageView.contentDescription = "There is no image available for this representative"
    }
}

fun ImageView.loadImage( url: String?) {
    Log.i("BindingAdapter", "Inside loadImage<${url}>")
//    val urlHTTPS = url?.replace("http:", "https:")
//    Log.i("BindingAdapter", "HTTPS URL<${urlHTTPS}>")
    Picasso.get().load(url)
        .placeholder(R.drawable.ic_profile)
        .error(R.drawable.ic_profile)
        .into(this)
}