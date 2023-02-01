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
import com.example.android.politicalpreparedness.databinding.RepresentativesViewItemListBinding
import com.example.android.politicalpreparedness.network.models.Channel
import com.example.android.politicalpreparedness.representative.model.RepresentativeProfile
import kotlinx.android.synthetic.main.representatives_view_item_list.view.*

const val TAG = "RepresentativeRecycler"

class RepresentativeListAdapter :
    ListAdapter<RepresentativeProfile, RepresentativeViewHolder>
        (RepresentativeDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepresentativeViewHolder {
        Log.i(TAG, "inside onCreateViewHolder")
        val binding =  RepresentativesViewItemListBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)

        return RepresentativeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RepresentativeViewHolder, position: Int) {
        val representative = getItem(position)
        Log.i(TAG, "inside onBindViewHolder")

        holder.bind(representative)
    }

}

/**The ElectionViewHolder constructor takes the binding variable from the associated Election
List Item, which gives access to full Election **/
class RepresentativeViewHolder(private val binding: RepresentativesViewItemListBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(representative: RepresentativeProfile) {
        Log.i(TAG, "Inside RepresentativeViewHolder bind")
        binding.representative = representative
        //to ensure that data binding executes immediately and the Recycler calculates the size
        if (representative.channels != null) {
            showSocialLinks(representative.channels)
        }
        if (representative.urls != null) {
            showWWWLinks(representative.urls)
        }
        binding.executePendingBindings()
    }

    private fun showSocialLinks(channels: List<Channel>) {
        val facebookUrl = getFacebookUrl(channels)
        if (!facebookUrl.isNullOrBlank()) {
            enableLink(binding.imageViewFb, facebookUrl)
        } else {
            disableLink(binding.imageViewFb)
        }

        val twitterUrl = getTwitterUrl(channels)
        if (!twitterUrl.isNullOrBlank()) {
            enableLink(binding.imageViewTwt, twitterUrl)
        }else {
            disableLink(binding.imageViewTwt)
        }
    }

    private fun showWWWLinks(urls: List<String>) {
        val wwwUrl = getWWWUrl(urls)
        if(!wwwUrl.isNullOrBlank()) {
            enableLink(binding.imageViewWww, urls.first())
        } else {
            disableLink(binding.imageViewWww)
        }

    }

    private fun getWWWUrl(urls: List<String>): String? {
        return urls.firstOrNull()
    }

    private fun getFacebookUrl(channels: List<Channel>): String? {
        return channels.filter { channel -> channel.type == "Facebook" }
            .map { channel -> "https://www.facebook.com/${channel.id}" }
            .firstOrNull()
    }

    private fun getTwitterUrl(channels: List<Channel>): String? {
        return channels.filter { channel -> channel.type == "Twitter" }
            .map { channel -> "https://www.twitter.com/${channel.id}" }
            .firstOrNull()
    }

    private fun enableLink(view: ImageView, url: String) {
        view.visibility = View.VISIBLE
        view.setOnClickListener { setIntent(url) }
    }

    private fun disableLink(view: ImageView) {
        view.visibility = View.INVISIBLE
    }

    private fun setIntent(url: String) {
        val uri = Uri.parse(url)
        val intent = Intent(ACTION_VIEW, uri)
        itemView.context.startActivity(intent)
    }
}

/**
 * Allows the RecyclerView to determine which items have changed when the [List] of [Asteroid]
 * has been updated.
 */
object RepresentativeDiffCallback : DiffUtil.ItemCallback<RepresentativeProfile>() {
    override fun areItemsTheSame(
        oldItem: RepresentativeProfile,
        newItem: RepresentativeProfile
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: RepresentativeProfile,
        newItem: RepresentativeProfile
    ): Boolean {
        return oldItem.id == newItem.id
    }
}

/**
 * Click Listener to handle click on the recycler view item
 */

class RepresentativeListener(val clickListener: (representative: RepresentativeProfile) -> Unit) {

    fun onClick(representative: RepresentativeProfile) = clickListener(representative)
}