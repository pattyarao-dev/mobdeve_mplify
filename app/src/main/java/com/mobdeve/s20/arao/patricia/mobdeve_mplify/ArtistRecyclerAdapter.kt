package com.mobdeve.s20.arao.patricia.mobdeve_mplify

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class ArtistRecyclerAdapter(
    private var artist: ArrayList<ActualArtist>,
    private val mContext: Context,
    private val onArtistClick: (ActualArtist) -> Unit
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
        holder.artistTag.text = artistList.username

        Picasso.get().load(artistList.profilePicture).into(holder.artistPhoto)

        holder.artistTag.setOnClickListener {
            onArtistClick(artistList)
        }

        holder.artistFullName.setOnClickListener {
            onArtistClick(artistList)
        }
    }

    // ViewHolder to hold references to the item views
    class ArtistViewHolder(artistView: View) : RecyclerView.ViewHolder(artistView) {
        val artistFullName: TextView = artistView.findViewById(R.id.tvArtistFullName)
        val artistTag: TextView = artistView.findViewById(R.id.tvArtistTag)
        val toggleFollowButton: Button = artistView.findViewById(R.id.toggleFollowButton)
        val artistPhoto: ImageView = artistView.findViewById(R.id.artistPhoto)
    }
}