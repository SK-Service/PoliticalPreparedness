package com.example.android.politicalpreparedness.election.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.databinding.ElectionViewItemListBinding
import com.example.android.politicalpreparedness.network.models.Election

const val TAG ="ElectionRecycler"

class ElectionListAdapter(private val clickListener: ElectionListener): ListAdapter<Election, ElectionViewHolder>(ElectionDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElectionViewHolder {
        Log.i(TAG, "inside onCreateViewHolder")
        return ElectionViewHolder (ElectionViewItemListBinding.inflate(
                            LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ElectionViewHolder, position: Int) {
        val election =getItem(position)
        Log.i(TAG, "inside onBindViewHolder")
        holder.itemView.setOnClickListener {
            run {
                Log.i(TAG, "inside onBindViewHolder - Set on click listener")
                clickListener.onClick(election)
                Log.i(TAG, "inside onBindViewHolder - After " +
                        "onClickListener.onClick(election)")
            }

        }
        holder.bind(election)
    }

}

/**The ElectionViewHolder constructor takes the binding variable from the associated Election
List Item, which gives access to full Election **/
class ElectionViewHolder(private val binding:ElectionViewItemListBinding) :
    RecyclerView.ViewHolder ( binding.root){

    fun bind(election: Election) {
        Log.i(TAG, "Inside ElectionViewHOlder bind")
        binding.election = election
        //to ensure that data binding executes immediately and the Recycler calculates the size
        binding.executePendingBindings()
    }
}

/**
 * Allows the RecyclerView to determine which items have changed when the [List] of [Asteroid]
 * has been updated.
 */
object ElectionDiffCallback: DiffUtil.ItemCallback<Election>() {
    override fun areItemsTheSame(oldItem: Election, newItem: Election): Boolean {
        return oldItem == newItem
    }
    override fun areContentsTheSame(oldItem: Election, newItem: Election): Boolean {
        return oldItem.id == newItem.id
    }
}

/**
 * Click Listener to handle click on the recycler view item
 */

class ElectionListener(val clickListener: (election: Election) -> Unit) {

    fun onClick(election: Election) =  clickListener(election)
}
