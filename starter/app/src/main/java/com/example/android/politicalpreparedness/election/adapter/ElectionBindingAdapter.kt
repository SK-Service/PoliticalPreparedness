package com.example.android.politicalpreparedness.election.adapter

import android.content.Intent
import android.net.Uri
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.election.ElectionsViewModel
import com.example.android.politicalpreparedness.election.WebViewActivity
import com.example.android.politicalpreparedness.network.models.Election

@BindingAdapter("listData")
fun bindListData (recyclerview: RecyclerView, electionViewModel: ElectionsViewModel?) {
    Log.i("BindngAdapter-listData", "inside bindListData")
    //Bind the ElectionRecyclerAdapter to the layout view - which references the RecyclerView
    recyclerview.adapter = ElectionListAdapter(
        ElectionListener{
            electionViewModel?.displayVoterInfo(it)
        } )

    val adapter = recyclerview.adapter as ElectionListAdapter
    Log.i("BindngAdapter-listData", "After getting hold of Adapter\n")

    if (electionViewModel?.listOfElection != null) {
        Log.i("BindngAdapter-listData", "Inside list null check, " +
                "list:<${electionViewModel?.listOfElection.value?.size}>\n")

        adapter.submitList(electionViewModel?.listOfElection.value )
        Log.i("BindngAdapter-listData", "after adapter.submitList\n")
    }
}


@BindingAdapter("clickableText")
fun bindClickableText(textView: TextView, textUrl: String?) {
    Log.i("BindingAdapter-clickableText",
                "TextView:${textView.id} AND textURL:${textUrl}")
    if (textUrl.isNullOrEmpty())
        return
    val spannableString = SpannableString(textUrl)
    val clickableSpan: ClickableSpan = object : ClickableSpan() {
        override fun onClick(widget: View) {
             val intent = Intent(textView.context, WebViewActivity::class.java)
            intent.putExtra("URL",textUrl)
            textView.context.startActivity(intent)
        }
    }
    spannableString.setSpan(clickableSpan, 0, textUrl.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    textView.setText(spannableString, TextView.BufferType.SPANNABLE)
    textView.movementMethod = LinkMovementMethod.getInstance()
}