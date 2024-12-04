package com.mobdeve.s20.arao.patricia.mobdeve_mplify

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class ArtistRecyclerAdapter(
    private var artist: ArrayList<Artist>,
    private val mContext: Context,
) : RecyclerView.Adapter<ArtistRecyclerAdapter.ArtistViewHolder>() {

    private val followStates = BooleanArray(artist.size) { true }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistViewHolder {
        // Inflate the layout for each item
        val artistView = LayoutInflater.from(mContext).inflate(R.layout.layout_following, parent, false)
        return ArtistViewHolder(artistView)
    }

    override fun getItemCount(): Int {
        return artist.size
    }

    override fun onBindViewHolder(holder: ArtistViewHolder, position: Int) {
        // Bind data to the views
        val artistList = artist[position]
        holder.artistFullName.text = artistList.name
        holder.artistTag.text = artistList.tag

        updateButtonAppearance(holder.toggleFollowButton, followStates[position])

        holder.toggleFollowButton.setOnClickListener {
            followStates[position] = !followStates[position]
            updateButtonAppearance(holder.toggleFollowButton, followStates[position])
        }

//        holder.artistName.setOnClickListener {
//            val intent = Intent(mContext, ArtistFragment::class.java)
//            intent.putExtra("ARTIST_NAME", artistSongs.artist.name)
//            mContext.startActivity(intent)
//        }
    }

    private fun updateButtonAppearance(button: Button, isFollowing: Boolean) {
        if (isFollowing) {
            button.text = "Following"
            button.setBackgroundColor(ContextCompat.getColor(mContext, R.color.primary))
        } else {
            button.text = "Follow"
            button.setBackgroundColor(ContextCompat.getColor(mContext, android.R.color.darker_gray))
        }
    }

    // Method to update the dataset and refresh the RecyclerView
    fun setFilteredSongs(filteredArtist: ArrayList<Artist>) {
        artist = filteredArtist
        notifyDataSetChanged() // Notify the adapter about the dataset change
    }

    // ViewHolder to hold references to the item views
    class ArtistViewHolder(artistView: View) : RecyclerView.ViewHolder(artistView) {
        val artistFullName: TextView = artistView.findViewById(R.id.tvArtistFullName)
        val artistTag: TextView = artistView.findViewById(R.id.tvArtistTag)
        val toggleFollowButton: Button = artistView.findViewById(R.id.toggleFollowButton)

    }
}