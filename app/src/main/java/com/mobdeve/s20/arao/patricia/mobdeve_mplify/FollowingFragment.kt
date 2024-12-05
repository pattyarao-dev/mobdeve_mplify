package com.mobdeve.s20.arao.patricia.mobdeve_mplify

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FollowingFragment: Fragment(){
    private var artistEmail: String? = null
    private lateinit var artistFollowing: ArrayList<ActualArtist>
    private lateinit var artistFollowingEmails: ArrayList<String>

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        arguments?.let { bundle ->
            artistEmail = bundle.getString("email")
        }


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_following, container, false)
    }



    private fun openArtistPage(artist: ActualArtist) {
        val bundle = Bundle().apply {
            putString("artistEmail", artist.email.toString())
        }

        val artistFragment = ArtistFragment().apply {
            arguments = bundle
        }
        parentFragmentManager.commit {
            replace(R.id.flFragment, artistFragment)
        }
    }


    private suspend fun getFollowingList(email: String) {
        try {
            val result = db.collection("users").whereEqualTo("email", email).get().await()
            for (data in result) {
                val followers = data.get("followed_artists") as? ArrayList<String>
                if (!followers.isNullOrEmpty()) {
                    artistFollowingEmails = followers
                }
            }
        } catch (e: Exception) {
            // Handle error, for example log the error
        }
    }

    private suspend fun getFollowings(followers: ArrayList<String>, recyclerView: RecyclerView) {
        if (followers.isNotEmpty()) {
            try {
                val result = db.collection("users").whereIn("email", followers).get().await()
                for (data in result) {
                    val actualArtist = ActualArtist(
                        email = data.getString("email") ?: "",
                        follower = data.getLong("follower")?.toInt(),
                        following = data.getLong("following")?.toInt(),
                        name = data.getString("name") ?: "",
                        profilePicture = data.getString("profilePicture") ?: "",
                        username = data.getString("username") ?: ""
                    )
                    artistFollowing.add(actualArtist)
                }

                val adapter = ArtistRecyclerAdapter(artistFollowing, requireContext(), ::openArtistPage)
                recyclerView.adapter = adapter
            } catch (e: Exception) {
                // Handle error
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        artistFollowing = ArrayList()
        artistFollowingEmails = ArrayList()

        db = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
        // Fetch the data for songs


        // Initialize the RecyclerView


        val followingArtistsRecyclerView: RecyclerView = view.findViewById(R.id.followingArtistsList)
        followingArtistsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Set up the adapter

        viewLifecycleOwner.lifecycleScope.launch {
            // Wait for the followers list to be fetched
            getFollowingList(artistEmail.toString())

            // Now, fetch the followers
            getFollowings(artistFollowingEmails, followingArtistsRecyclerView)
        }

    }

//    private fun toggleFollowArtist(){
//        val toggleFollowBtn = view?.findViewById<Button>(R.id.toggleFollowButton)
//        toggleFollowBtn?.setOnClickListener{
//            toggleFollowBtn.setBackgroundColor(resources.getColor(android.R.color.darker_gray, null))
//            toggleFollowBtn.text = "Follow"
//        }
//    }
}