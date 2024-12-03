package com.mobdeve.s20.arao.patricia.mplify

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s20.arao.patricia.mobdeve_mplify.ArtistFragment
import com.mobdeve.s20.arao.patricia.mobdeve_mplify.FollowingFragment
import com.mobdeve.s20.arao.patricia.mobdeve_mplify.Music
import com.mobdeve.s20.arao.patricia.mobdeve_mplify.MusicRecyclerAdapter
import com.mobdeve.s20.arao.patricia.mobdeve_mplify.R
import com.mobdeve.s20.arao.patricia.mobdeve_mplify.Generator
import com.mobdeve.s20.arao.patricia.mobdeve_mplify.HomeFragment
import com.mobdeve.s20.arao.patricia.mobdeve_mplify.UploadSongFragment

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

        val followingCount = fetchFollowingCount()
        val tvFollowingNum: TextView = view.findViewById(R.id.followingNum)
        tvFollowingNum.text = followingCount.toString()

        val followedArtistsList = view.findViewById<TextView>(R.id.followingList)
        followedArtistsList.setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.flFragment, FollowingFragment())
                addToBackStack(null)
            }
        }

        val uploadSongLink = view.findViewById<Button>(R.id.uploadSongButton)
        uploadSongLink.setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.flFragment, UploadSongFragment())
                addToBackStack(null)
            }
        }
    }

    private fun fetchFollowingCount(): Int {
        val followingArtists = Generator.loadFollowingArtists()
        return followingArtists.size
    }
}
