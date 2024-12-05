package com.mobdeve.s20.arao.patricia.mplify

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.accessibility.AccessibilityViewCommand.SetProgressArguments
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.mobdeve.s20.arao.patricia.mobdeve_mplify.ActualMusic
import com.mobdeve.s20.arao.patricia.mobdeve_mplify.ActualMusicRecyclerAdapter
import com.mobdeve.s20.arao.patricia.mobdeve_mplify.ArtistFragment
import com.mobdeve.s20.arao.patricia.mobdeve_mplify.EditProfileFragment
import com.mobdeve.s20.arao.patricia.mobdeve_mplify.FollowerFragment
import com.mobdeve.s20.arao.patricia.mobdeve_mplify.FollowingFragment
import com.mobdeve.s20.arao.patricia.mobdeve_mplify.FullSongFragment
import com.mobdeve.s20.arao.patricia.mobdeve_mplify.Music
import com.mobdeve.s20.arao.patricia.mobdeve_mplify.MusicRecyclerAdapter
import com.mobdeve.s20.arao.patricia.mobdeve_mplify.R
import com.mobdeve.s20.arao.patricia.mobdeve_mplify.Generator
import com.mobdeve.s20.arao.patricia.mobdeve_mplify.HomeFragment
import com.mobdeve.s20.arao.patricia.mobdeve_mplify.UploadSongFragment
import com.squareup.picasso.Picasso
import org.w3c.dom.Text

class ProfileFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore


    private lateinit var artistPhoto: ImageView
    private lateinit var followersText: TextView
    private lateinit var tvFollowingNum: TextView
    private lateinit var usernameText: TextView
    private lateinit var artistNameText: TextView
    private lateinit var mySongs: ArrayList<ActualMusic>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
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

    private fun setPage(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            db.collection("users").whereEqualTo("email", currentUser?.email).get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        val profpic = document.getString("profilePicture")
                        if (profpic != null) {
                            Picasso.get().load(profpic).into(artistPhoto)
                        }
                        var usernameString = "@" + document.getString("username").toString()
                        followersText.text = document.getLong("follower").toString()
                        tvFollowingNum.text = document.getLong("following").toString()
                        usernameText.text = usernameString
                        artistNameText.text = document.getString("name").toString()
                    }
                }
        }
    }

    private fun getMySongs(currentUser: FirebaseUser?, recyclerView: RecyclerView) {
        if (currentUser != null) {
            db.collection("music").whereEqualTo("artist", currentUser.email).get()
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

                        mySongs.add(music)
                    }
                    val musicAdapter = ActualMusicRecyclerAdapter(mySongs, requireContext(), ::onSongClick)
                    recyclerView.adapter = musicAdapter
                    musicAdapter.notifyDataSetChanged()
                }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        artistPhoto = view.findViewById(R.id.artistPhoto)
        followersText = view.findViewById(R.id.followers)
        usernameText = view.findViewById(R.id.artistTag)
        artistNameText = view.findViewById(R.id.artistTitle)

        mySongs = ArrayList()

        db = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()

        val currentUser = firebaseAuth.currentUser


        // Fetch the data for songs
        val artistSongList: ArrayList<Music> = Generator.loadArtistSongs()
        val artistLikedSongs: ArrayList<Music> = Generator.loadLikedSongs()

        // Initialize the RecyclerView
        val recyclerView: RecyclerView = view.findViewById(R.id.artistSongs)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val likedSongsRecyclerView: RecyclerView = view.findViewById(R.id.likedSongs)
        likedSongsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Set up the adapter
        val musicAdapter = ActualMusicRecyclerAdapter(mySongs, requireContext(), ::onSongClick)
        recyclerView.adapter = musicAdapter

        val likedSongsAdapter = MusicRecyclerAdapter(artistLikedSongs, requireContext())
        likedSongsRecyclerView.adapter = likedSongsAdapter

        tvFollowingNum = view.findViewById(R.id.followingNum)


        getMySongs(currentUser, recyclerView)
        setPage(currentUser)


        val followedArtistsList = view.findViewById<TextView>(R.id.followingPageLink)
        followedArtistsList.setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.flFragment, FollowingFragment())
                addToBackStack(null)
            }
        }

        val artistFollowersList = view.findViewById<TextView>(R.id.followersPageLink)
        artistFollowersList.setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.flFragment, FollowerFragment())
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

        val editProfileLink = view.findViewById<TextView>(R.id.editProfileButton)
        editProfileLink.setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.flFragment, EditProfileFragment())
                addToBackStack(null)
            }
        }
    }

    private fun fetchFollowingCount(): Int {
        val followingArtists = Generator.loadFollowingArtists()
        return followingArtists.size
    }
}
