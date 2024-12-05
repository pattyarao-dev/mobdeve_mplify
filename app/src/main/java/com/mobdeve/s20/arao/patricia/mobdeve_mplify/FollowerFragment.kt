package com.mobdeve.s20.arao.patricia.mobdeve_mplify

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FollowerFragment : Fragment() {

    private var artistEmail: String? = null
    private lateinit var artistFollower: ArrayList<ActualArtist>
    private lateinit var artistFollowerEmails: ArrayList<String>

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
        return inflater.inflate(R.layout.fragment_followers, container, false)
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

    private suspend fun getFollowersList(email: String) {
        try {
            val result = db.collection("users").whereEqualTo("email", email).get().await()
            for (data in result) {
                val followers = data.get("follower_artists") as? ArrayList<String>
                if (!followers.isNullOrEmpty()) {
                    artistFollowerEmails = followers
                }
            }
        } catch (e: Exception) {
            // Handle error, for example log the error
        }
    }

    private suspend fun getFollowers(followers: ArrayList<String>, recyclerView: RecyclerView) {
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
                    artistFollower.add(actualArtist)
                }

                val adapter = ArtistRecyclerAdapter(artistFollower, requireContext(), ::openArtistPage)
                recyclerView.adapter = adapter
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        artistFollower = ArrayList()
        artistFollowerEmails = ArrayList()

        db = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()

        // Initialize the RecyclerView
        val followersRecyclerView: RecyclerView = view.findViewById(R.id.followersList)
        followersRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Use coroutine to wait for getFollowersList to complete
        viewLifecycleOwner.lifecycleScope.launch {
            // Wait for the followers list to be fetched
            getFollowersList(artistEmail.toString())

            // Now, fetch the followers
            getFollowers(artistFollowerEmails, followersRecyclerView)
        }
    }
}
