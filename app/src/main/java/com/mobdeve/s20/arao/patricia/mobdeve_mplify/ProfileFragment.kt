package com.mobdeve.s20.arao.patricia.mplify

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s20.arao.patricia.mobdeve_mplify.Music
import com.mobdeve.s20.arao.patricia.mobdeve_mplify.MusicRecyclerAdapter
import com.mobdeve.s20.arao.patricia.mobdeve_mplify.R
import com.mobdeve.s20.arao.patricia.mobdeve_mplify.Generator

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Fetch the data for songs
        val artistSongList: ArrayList<Music> = Generator.loadArtistSongs()
        val artistLikedSongs: ArrayList<Music> = Generator.loadLikedSongs()

        // Initialize the RecyclerView
        val recyclerView: RecyclerView = view.findViewById(R.id.artistSongs)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val likedSongsRecyclerView: RecyclerView = view.findViewById(R.id.likedSongs)
        likedSongsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Set up the adapter
        val musicAdapter = MusicRecyclerAdapter(artistSongList, requireContext())
        recyclerView.adapter = musicAdapter

        val likedSongsAdapter = MusicRecyclerAdapter(artistLikedSongs, requireContext())
        likedSongsRecyclerView.adapter = likedSongsAdapter
    }
}
