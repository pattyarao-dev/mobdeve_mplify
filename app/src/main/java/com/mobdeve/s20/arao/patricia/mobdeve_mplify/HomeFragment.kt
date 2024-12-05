package com.mobdeve.s20.arao.patricia.mobdeve_mplify

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mobdeve.s20.arao.patricia.mobdeve_mplify.R

class HomeFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var allSongs: ArrayList<ActualMusic>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    private fun fetchAllSongs(recycler: RecyclerView){
        db.collection("music").get().addOnSuccessListener { result ->
            for(data in result) {
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

                allSongs.add(music)
            }


            val allSongsRecyclerAdapter = ActualMusicRecyclerAdapter(allSongs, requireContext(), ::onSongClick)
            recycler.adapter = allSongsRecyclerAdapter
            allSongsRecyclerAdapter.notifyDataSetChanged()
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
        allSongs = ArrayList()

        // Fetch the data for songs
        // Initialize the RecyclerView

        val allSongsRecyclerView: RecyclerView = view.findViewById(R.id.recentReleases)
        allSongsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        fetchAllSongs(allSongsRecyclerView)
        // Set up the adapter
        val allSongsRecyclerAdapter = ActualMusicRecyclerAdapter(allSongs, requireContext(), ::onSongClick)
        allSongsRecyclerView.adapter = allSongsRecyclerAdapter
    }
}