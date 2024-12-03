package com.mobdeve.s20.arao.patricia.mobdeve_mplify

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class MusicRecyclerAdapter(
    private var songs: ArrayList<Music>,
    private val mContext: Context,
) : RecyclerView.Adapter<MusicRecyclerAdapter.MusicViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
        // Inflate the layout for each item
        val songsView = LayoutInflater.from(mContext).inflate(R.layout.layout_song, parent, false)
        return MusicViewHolder(songsView)
    }

    override fun getItemCount(): Int {
        return songs.size
    }

    override fun onBindViewHolder(holder: MusicViewHolder, position: Int) {
        // Bind data to the views
        val artistSongs = songs[position]
        holder.songTitle.text = artistSongs.title
        holder.artistName.text = artistSongs.artist.name

        holder.artistName.setOnClickListener {
            val intent = Intent(mContext, ArtistFragment::class.java)
            intent.putExtra("ARTIST_NAME", artistSongs.artist.name)
            mContext.startActivity(intent)
        }
    }

    // Method to update the dataset and refresh the RecyclerView
    fun setFilteredSongs(filteredSongs: ArrayList<Music>) {
        songs = filteredSongs
        notifyDataSetChanged() // Notify the adapter about the dataset change
    }

    // ViewHolder to hold references to the item views
    class MusicViewHolder(songsView: View) : RecyclerView.ViewHolder(songsView) {
        val songTitle: TextView = songsView.findViewById(R.id.tvSongTitle)
        val artistName: TextView = songsView.findViewById(R.id.tvArtistName)
    }
}