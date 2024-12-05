package com.mobdeve.s20.arao.patricia.mobdeve_mplify

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class ArtistFragment : Fragment() {

    private var artistEmail: String? = null
    private lateinit var artistSongs: ArrayList<ActualMusic>

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var artistPhoto: ImageView
    private lateinit var followersText: TextView
    private lateinit var tvFollowingNum: TextView
    private lateinit var usernameText: TextView
    private lateinit var artistNameText: TextView
    private lateinit var followButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        arguments?.let { bundle ->
            artistEmail = bundle.getString("artistEmail")

        }

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_artist, container, false)
    }

    private fun setPage(email: String) {
        Log.d("EMAIL SETUPPAGE", email)
        db.collection("users").whereEqualTo("email", email).get().addOnSuccessListener { result ->
            for (document in result) {
                val profpic = document.getString("profilePicture")

                val followers = document.get("follower_artists") as? ArrayList<String>
                val following = document.get("followed_artists") as? ArrayList<String>
                if (profpic != null) {
                    Picasso.get().load(profpic).into(artistPhoto)
                }
                var usernameString = "@" + document.getString("username").toString()
                followersText.text = followers?.size?.toString() ?: "0"
                tvFollowingNum.text = following?.size?.toString() ?: "0"
                usernameText.text = usernameString
                artistNameText.text = document.getString("name").toString()
            }
        }
    }

    private fun onSongClick(song: ActualMusic) {
        val bundle = Bundle().apply {
            putString("songTitle", song.songTitle.toString())
            putString("email", song.artist.toString())
        }

        val fullSongFragment = FullSongFragment().apply {
            arguments = bundle
        }

        parentFragmentManager.commit {
            replace(R.id.flFragment, fullSongFragment)
            addToBackStack(null)
        }
    }

    private fun getArtistSongs(email: String, recyclerView: RecyclerView) {
        db.collection("music").whereEqualTo("artist", email).get()
            .addOnSuccessListener { result ->

                for (data in result) {
                    val songTitle = data.getString("songTitle")
                    val songGenre = data.getString("songGenre")
                    val songUrl = data.getString("music")
                    val email = data.getString("artist")
                    val music = ActualMusic(
                        songTitle.toString(),
                        data.getString("songImage").toString(),
                        songGenre.toString(),
                        songUrl.toString(),
                        email.toString()
                    )

                    artistSongs.add(music)
                }
                val musicAdapter =
                    ActualMusicRecyclerAdapter(artistSongs, requireContext(), ::onSongClick)
                recyclerView.adapter = musicAdapter
                musicAdapter.notifyDataSetChanged()
            }
    }

    private fun followUser(
        currentUser: FirebaseUser?,
        artistEmail: String,
        followed: Boolean,
        onResult: (Boolean) -> Unit
    ) {
        if (currentUser != null) {

            db.collection("users").whereEqualTo("email", currentUser.email).get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val documentId = querySnapshot.documents[0].id

                        val fieldUpdate = if (followed) {
                            FieldValue.arrayRemove(artistEmail)
                        } else {
                            FieldValue.arrayUnion(artistEmail)
                        }

                        db.collection("users")
                            .document(documentId)
                            .update("followed_artists", fieldUpdate)
                            .addOnSuccessListener {
                                val message = if (followed) {
                                    "Artist unfollowed"
                                } else {
                                    "Artist followed"
                                }
                                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                                onResult(true)
                            }
                            .addOnFailureListener { e ->
                                Log.e("Firestore", "Error updating liked_songs", e)
                                Toast.makeText(
                                    requireContext(),
                                    "Error updating followed artists!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                onResult(false)
                            }
                    }
                }

            db.collection("users").whereEqualTo("email", artistEmail).get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val documentId = querySnapshot.documents[0].id

                        val artistField = if (followed) {
                            FieldValue.arrayRemove(currentUser.email)
                        } else {
                            FieldValue.arrayUnion(currentUser.email)
                        }

                        db.collection("users")
                            .document(documentId)
                            .update("follower_artists", artistField)
                            .addOnSuccessListener {

                            }
                            .addOnFailureListener { e ->
                                Log.e("Firestore", "Error updating liked_songs", e)
                            }
                    }
                }
        }
    }

    private fun checkIfUserFollows(
        currentUser: FirebaseUser?,
        artistEmail: String,
        onResult: (Boolean) -> Unit
    ) {
        if (currentUser != null) {
            val email = currentUser.email

            email?.let {
                db.collection("users")
                    .whereEqualTo("email", email)
                    .get()
                    .addOnSuccessListener { result ->
                        if (!result.isEmpty) {
                            for (data in result) {
                                val likedSongs = data.get("followed_artists") as? List<String>
                                val isLiked = likedSongs != null && artistEmail in likedSongs
                                onResult(isLiked) // Pass the result to the callback
                                return@addOnSuccessListener
                            }
                        }
                        onResult(false) // User or liked_songs not found
                    }
                    .addOnFailureListener { e ->
                        Log.e("Firestore", "Error fetching user document", e)
                        onResult(false) // Error fetching data
                    }
            } ?: run {
                Log.e("Firestore", "User email is null")
                onResult(false)
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        artistSongs = ArrayList()

        var followed = false

        artistPhoto = view.findViewById(R.id.artistPhoto)
        followersText = view.findViewById(R.id.followers)
        tvFollowingNum = view.findViewById(R.id.following)
        usernameText = view.findViewById(R.id.artistTag)
        artistNameText = view.findViewById(R.id.artistTitle)
        followButton = view.findViewById(R.id.followButton)

        db = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()


        val currentUser = firebaseAuth.currentUser

        if(currentUser != null) {
            checkIfUserFollows(currentUser, artistEmail.toString()) {
                isFollowed ->
                if(isFollowed) {
                    followed = true
                    followButton.backgroundTintList =
                        ColorStateList.valueOf(Color.parseColor("#0FA3B1"))
                    followButton.text = "Following"
                }
            }
        }
        setPage(artistEmail.toString())

        followButton.setOnClickListener {
            val isFollowed = followed

            followUser(currentUser, artistEmail.toString(), isFollowed) { success ->
                if (isFollowed) {
                    // The user removed the song, set to uncolored heart
                    followButton.backgroundTintList =
                        ColorStateList.valueOf(Color.parseColor("#EDDEA4"))
                    followButton.text = "Follow"

                } else {
                    // The user liked the song, set to colored heart
                    followButton.backgroundTintList =
                        ColorStateList.valueOf(Color.parseColor("#0FA3B1"))
                    followButton.text = "Following"
                }
                followed = !isFollowed
            }
        }

        val followedArtistsList = view.findViewById<TextView>(R.id.followingPageLinks)
        followedArtistsList.setOnClickListener {
            parentFragmentManager.commit {

                val bundle = Bundle().apply {
                    putString("email", artistEmail.toString())
                }
                val followingFragment = FollowingFragment().apply {
                    arguments = bundle
                }
                replace(R.id.flFragment, followingFragment)
                addToBackStack(null)
            }
        }

        val artistFollowersList = view.findViewById<TextView>(R.id.followersPageLink)
        artistFollowersList.setOnClickListener {
            parentFragmentManager.commit {
                val bundle = Bundle().apply {
                    putString("email", artistEmail.toString())
                }
                val followerFragment = FollowerFragment().apply {
                    arguments = bundle
                }
                replace(R.id.flFragment, followerFragment)
                addToBackStack(null)
            }
        }
        // Fetch the data for songs
        val artistSongList: ArrayList<Music> = Generator.loadArtistSongs()

        // Initialize the RecyclerView
        val recyclerView: RecyclerView = view.findViewById(R.id.artistSongs)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        getArtistSongs(artistEmail.toString(), recyclerView)

//        // Set up the adapter
//        val musicAdapter = MusicRecyclerAdapter(artistSongList, requireContext())
//        recyclerView.adapter = musicAdapter
    }
}