package com.example.android.politicalpreparedness.representative.adapter

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.ElectionViewItemListBinding
import com.example.android.politicalpreparedness.databinding.RepresentativesViewItemListBinding
//import com.example.android.politicalpreparedness.databinding.ViewholderRepresentativeBinding
import com.example.android.politicalpreparedness.network.models.Channel
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.representative.model.Representative
import com.example.android.politicalpreparedness.representative.model.RepresentativeProfile

const val TAG ="RepresentativeRecycler"

class RepresentativeListAdapter():
                    ListAdapter<RepresentativeProfile, RepresentativeViewHolder>
                                                    (RepresentativeDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepresentativeViewHolder {
        Log.i(TAG, "inside onCreateViewHolder")
        return RepresentativeViewHolder (
            RepresentativesViewItemListBinding.inflate(
            LayoutInflater.from(parent.context)))
    }

    //TODO: Bind ViewHolder
    override fun onBindViewHolder(holder: RepresentativeViewHolder, position: Int) {
        val representative =getItem(position)
        Log.i(TAG, "inside onBindViewHolder")
//        holder.itemView.setOnClickListener {
//            run {
//                Log.i(TAG, "inside onBindViewHolder - Set on click listener")
//                clickListener.onClick(representative)
//                Log.i(TAG, "inside onBindViewHolder - After " +
//                        "onClickListener.onClick(election)")
//            }
//
//        }
        holder.bind(representative)
    }

    //TODO: Add companion object to inflate ViewHolder (from)
}

//TODO: Create ElectionViewHolder
/**The ElectionViewHolder constructor takes the binding variable from the associated Election
List Item, which gives access to full Election **/
class RepresentativeViewHolder(private val binding: RepresentativesViewItemListBinding) :
    RecyclerView.ViewHolder ( binding.root){

    fun bind(representative: RepresentativeProfile) {
        Log.i(TAG, "Inside ElectionViewHOlder bind")
        binding.representative = representative
        //to ensure that data binding executes immediately and the Recycler calculates the size
        binding.executePendingBindings()
    }
}

//TODO: Create ElectionDiffCallback
/**
 * Allows the RecyclerView to determine which items have changed when the [List] of [Asteroid]
 * has been updated.
 */
object RepresentativeDiffCallback: DiffUtil.ItemCallback<RepresentativeProfile>() {
    override fun areItemsTheSame(oldItem: RepresentativeProfile, newItem: RepresentativeProfile): Boolean {
        return oldItem == newItem
    }
    override fun areContentsTheSame(oldItem: RepresentativeProfile, newItem: RepresentativeProfile): Boolean {
        return oldItem.id == newItem.id
    }
}

//TODO: Create ElectionListener
/**
 * Click Listener to handle click on the recycler view item
 */

class RepresentativeListener(val clickListener: (representative: RepresentativeProfile) -> Unit) {

    fun onClick(representative: RepresentativeProfile) =  clickListener(representative)
}

//class RepresentativeListAdapter: ListAdapter<Representative, RepresentativeViewHolder>(RepresentativeDiffCallback()){
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepresentativeViewHolder {
//        return RepresentativeViewHolder.from(parent)
//    }
//
//    override fun onBindViewHolder(holder: RepresentativeViewHolder, position: Int) {
//        val item = getItem(position)
//        holder.bind(item)
//    }
//}
//
//class RepresentativeViewHolder(val binding: ViewholderRepresentativeBinding): RecyclerView.ViewHolder(binding.root) {
//
//    fun bind(item: Representative) {
//        binding.representative = item
//        binding.representativePhoto.setImageResource(R.drawable.ic_profile)
//
//        //TODO: Show social links ** Hint: Use provided helper methods
//        //TODO: Show www link ** Hint: Use provided helper methods
//
//        binding.executePendingBindings()
//    }
//
//    //TODO: Add companion object to inflate ViewHolder (from)
//
//    private fun showSocialLinks(channels: List<Channel>) {
//        val facebookUrl = getFacebookUrl(channels)
//        if (!facebookUrl.isNullOrBlank()) { enableLink(binding.facebookIcon, facebookUrl) }
//
//        val twitterUrl = getTwitterUrl(channels)
//        if (!twitterUrl.isNullOrBlank()) { enableLink(binding.twitterIcon, twitterUrl) }
//    }
//
//    private fun showWWWLinks(urls: List<String>) {
//        enableLink(binding.wwwIcon, urls.first())
//    }
//
//    private fun getFacebookUrl(channels: List<Channel>): String? {
//        return channels.filter { channel -> channel.type == "Facebook" }
//                .map { channel -> "https://www.facebook.com/${channel.id}" }
//                .firstOrNull()
//    }
//
//    private fun getTwitterUrl(channels: List<Channel>): String? {
//        return channels.filter { channel -> channel.type == "Twitter" }
//                .map { channel -> "https://www.twitter.com/${channel.id}" }
//                .firstOrNull()
//    }
//
//    private fun enableLink(view: ImageView, url: String) {
//        view.visibility = View.VISIBLE
//        view.setOnClickListener { setIntent(url) }
//    }
//
//    private fun setIntent(url: String) {
//        val uri = Uri.parse(url)
//        val intent = Intent(ACTION_VIEW, uri)
//        itemView.context.startActivity(intent)
//    }
//
//}
//
////TODO: Create RepresentativeDiffCallback
//
////TODO: Create RepresentativeListener