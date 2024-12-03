package com.mobdeve.s20.arao.patricia.mobdeve_mplify

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

class FollowingFragment: Fragment(){
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_following, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Fetch the data for songs

        val followingArtists: ArrayList<Artist> = Generator.loadFollowingArtists()

        // Initialize the RecyclerView


        val followingArtistsRecyclerView: RecyclerView = view.findViewById(R.id.followingArtistsList)
        followingArtistsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Set up the adapter


        val followingArtistsAdapter = ArtistRecyclerAdapter(followingArtists, requireContext())
        followingArtistsRecyclerView.adapter = followingArtistsAdapter

    }

//    private fun toggleFollowArtist(){
//        val toggleFollowBtn = view?.findViewById<Button>(R.id.toggleFollowButton)
//        toggleFollowBtn?.setOnClickListener{
//            toggleFollowBtn.setBackgroundColor(resources.getColor(android.R.color.darker_gray, null))
//            toggleFollowBtn.text = "Follow"
//        }
//    }
}