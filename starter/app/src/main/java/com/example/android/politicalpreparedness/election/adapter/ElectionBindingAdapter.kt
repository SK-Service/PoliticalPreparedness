package com.example.android.politicalpreparedness.election.adapter

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.election.ElectionsViewModel
import com.example.android.politicalpreparedness.election.WebViewActivity
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.android.synthetic.main.fragment_voter_info.view.*

@BindingAdapter("listElections")
fun bindElectionList (recyclerview: RecyclerView, electionViewModel: ElectionsViewModel?) {
    Log.i("BindngAdapter-bindElectionList", "inside bindElectionList")
    Log.i("BindngAdapter-listElection",
        "list:<${electionViewModel?.listOfElection?.value?.size}>\n")
    //Bind the ElectionRecyclerAdapter to the layout view - which references the RecyclerView
    recyclerview.adapter = ElectionListAdapter(
        ElectionListener{
            electionViewModel?.displayVoterInfo(it)
        } )

    val adapter = recyclerview.adapter as ElectionListAdapter
    Log.i("BindngAdapter-listElection", "After getting hold of Adapter\n")

    if (electionViewModel?.listOfElection != null) {
        Log.i("BindngAdapter-listElection", "election list is not null\n")
        adapter.submitList(electionViewModel?.listOfElection.value )
    }
}

@BindingAdapter("listSavedElections")
fun bindSavedElectionList (recyclerview: RecyclerView, electionViewModel: ElectionsViewModel?) {
    Log.i("BindngAdapter-bindSavedElectionList", "inside bindSavedElectionList")
    Log.i("BindngAdapter-listSavedElection",
        "list:<${electionViewModel?.listOfSavedElections?.value?.size}>\n")
    //Bind the ElectionRecyclerAdapter to the layout view - which references the RecyclerView
    recyclerview.adapter = ElectionListAdapter(
        ElectionListener{
            electionViewModel?.displayVoterInfo(it)
        } )

    val adapter = recyclerview.adapter as ElectionListAdapter
    Log.i("BindngAdapter-bindSavedElectionList", "After getting hold of Adapter\n")


    if (electionViewModel?.listOfSavedElections != null) {
        Log.i("BindngAdapter-listSavedElection", "Saved Elections is not null\n")
        adapter.submitList(electionViewModel?.listOfSavedElections.value )
    }
}

@BindingAdapter("clickableTextLocation")
fun bindClickableTextLocation(textView: TextView, textUrl: String?) {
    Log.i("BindingAdapter-clickableTextLocation",
        "TextView:${textView.id}")
    if (textUrl.isNullOrEmpty())
        return
    val text = "State Locations"
    textView.setText(text)

    val spannableString = SpannableString(text)
    val clickableSpan: ClickableSpan = object : ClickableSpan() {
        override fun onClick(widget: View) {
            val intent = Intent(textView.context, WebViewActivity::class.java)
            intent.putExtra("URL",textUrl)
            textView.context.startActivity(intent)
        }
        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.color = ContextCompat.getColor(textView.context, R.color.hyperlink)
        }
    }
    val foregroundColorSpanBlue = ForegroundColorSpan(Color.RED)
    spannableString.setSpan(clickableSpan, 0, text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

    textView.setText(spannableString, TextView.BufferType.SPANNABLE)
    textView.movementMethod = LinkMovementMethod.getInstance()
}

@BindingAdapter("clickableTextBallot")
fun bindClickableTextBallot(textView: TextView, textUrl: String?) {
    Log.i("BindingAdapter-clickableTextBallot",
        "TextView:${textView.id}")
    if (textUrl.isNullOrEmpty())
        return
    val text = "State Ballots"
    textView.setText(text)

    val spannableString = SpannableString(text)
    val clickableSpan: ClickableSpan = object : ClickableSpan() {
        override fun onClick(widget: View) {
            val intent = Intent(textView.context, WebViewActivity::class.java)
            intent.putExtra("URL",textUrl)
            textView.context.startActivity(intent)
        }
        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.color = ContextCompat.getColor(textView.context, R.color.hyperlink)
        }
    }
    spannableString.setSpan(clickableSpan, 0, text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    textView.setText(spannableString, TextView.BufferType.SPANNABLE)
    textView.movementMethod = LinkMovementMethod.getInstance()
}

