package com.mobdeve.s20.arao.patricia.mobdeve_mplify

import android.content.Context
import android.media.Image
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

//TODO:
// MAKE THE LIKE FUNCTION
// MAKE THE FOLLOW FUNCTION
// FIX THE PASSWORD UI
// FUCK MY LIFE

class ActualMusicRecyclerAdapter(
    private var songs: ArrayList<ActualMusic>,
    private var mContext: Context,
    private val onSongClick: (ActualMusic) -> Unit
) : RecyclerView.Adapter<ActualMusicRecyclerAdapter.MusicViewHolder>() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
        // Inflate the layout for each item
        val songView = LayoutInflater.from(mContext).inflate(R.layout.layout_song, parent, false)
        return MusicViewHolder(songView)
    }

    override fun getItemCount(): Int {
        return songs.size
    }

    override fun onBindViewHolder(holder: MusicViewHolder, position: Int) {
        // Bind data to the views
        val song = songs[position]
        holder.songTitle.text = song.songTitle.toString()
        holder.artistName.text = song.artist.toString()
        Log.d("Image", song.songImage.toString())
        Picasso.get().load(song.songImage.toString()).into(holder.songImage)

        holder.playButton.setOnClickListener {
            // Notify the listener (which would be the parent fragment or activity)
            onSongClick(song)
        }
    }

    // Method to update the dataset and refresh the RecyclerView
    fun setFilteredSongs(filteredSongs: ArrayList<ActualMusic>) {
        songs = filteredSongs
        notifyDataSetChanged() // Notify the adapter about the dataset change
    }

    // ViewHolder to hold references to the item views
    class MusicViewHolder(songView: View) : RecyclerView.ViewHolder(songView) {
        val songTitle: TextView = songView.findViewById(R.id.tvSongTitle)
        val artistName: TextView = songView.findViewById(R.id.tvArtistName)
        val songImage: ImageView = songView.findViewById(R.id.songImage)
        val playButton: ImageButton = songView.findViewById(R.id.playBtn)
        val likeButton: ImageButton = songView.findViewById(R.id.likeBtn)
    }
}
