package com.mobdeve.s20.arao.patricia.mobdeve_mplify

import android.content.Context
import android.graphics.Color
import android.media.Image
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
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

    private lateinit var db: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
        // Inflate the layout for each item
        val songView = LayoutInflater.from(mContext).inflate(R.layout.layout_song, parent, false)
        return MusicViewHolder(songView)
    }

    override fun getItemCount(): Int {
        return songs.size
    }

    private fun likeSong(
        currentUser: FirebaseUser?,
        songActualMusic: ActualMusic,
        liked: Boolean,
        onResult: (Boolean) -> Unit
    ) {
        if (currentUser != null) {
            val userEmail = currentUser.email

            // Ensure the email is not null
            if (userEmail != null) {
                db.collection("users")
                    .whereEqualTo("email", userEmail)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        if (!querySnapshot.isEmpty) {
                            // Get the document ID of the user
                            val documentId = querySnapshot.documents[0].id

                            // Choose operation based on liked status
                            val fieldUpdate = if (liked) {
                                FieldValue.arrayRemove(songActualMusic.songTitle) // Remove the song
                            } else {
                                FieldValue.arrayUnion(songActualMusic.songTitle) // Add the song
                            }

                            // Update the liked_songs array
                            db.collection("users")
                                .document(documentId)
                                .update("liked_songs", fieldUpdate)
                                .addOnSuccessListener {
                                    val message = if (liked) {
                                        "Song removed from liked_songs successfully."
                                    } else {
                                        "Song added to liked_songs successfully."
                                    }
                                    Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show()
                                    onResult(true)
                                }
                                .addOnFailureListener { e ->
                                    Log.e("Firestore", "Error updating liked_songs", e)
                                    Toast.makeText(
                                        mContext,
                                        "Error updating liked_songs!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    onResult(false)
                                }
                        } else {
                            Log.e("Firestore", "User not found")
                            Toast.makeText(mContext, "User not found!", Toast.LENGTH_SHORT).show()
                            onResult(false)
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("Firestore", "Error querying user", e)
                        Toast.makeText(mContext, "Error querying user!", Toast.LENGTH_SHORT).show()
                        onResult(false)
                    }
            } else {
                Log.e("Firestore", "User email is null")
                onResult(false)
            }
        } else {
            Log.e("Firestore", "User is not logged in")
            onResult(false)
        }
    }


    private fun checkIfUserLikesSong(
        currentUser: FirebaseUser?,
        song: ActualMusic,
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
                                val likedSongs = data.get("liked_songs") as? List<String>
                                val isLiked = likedSongs != null && song.songTitle in likedSongs
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
        } else {
            Log.e("Firestore", "User is not logged in")
            onResult(false)
        }
    }


    override fun onBindViewHolder(holder: MusicViewHolder, position: Int) {
        // Bind data to the views

        var liked = false

        db = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()

        val currentUser = firebaseAuth.currentUser

        val song = songs[position]
        holder.songTitle.text = song.songTitle.toString()
        holder.artistName.text = song.artist.toString()
        if (currentUser != null) {
            checkIfUserLikesSong(currentUser, song) { isLiked ->
                if (isLiked) {
                    liked = true
                    holder.likeButton.setImageResource(R.drawable.heart_colored) // Set background to red
                }
            }
        }

        Log.d("Image", song.songImage.toString())
        Picasso.get().load(song.songImage.toString()).into(holder.songImage)

        holder.likeButton.setOnClickListener {
            // Toggle the liked state
            val isLiked = liked // Use the current state of "liked"
            likeSong(currentUser, song, isLiked) { success ->
                if (success) {
                    if (isLiked) {
                        // The user removed the song, set to uncolored heart
                        holder.likeButton.setImageResource(R.drawable.heart_svgrepo_com)
                    } else {
                        // The user liked the song, set to colored heart
                        holder.likeButton.setImageResource(R.drawable.heart_colored)
                    }

                    // Toggle the liked state
                    liked = !isLiked
                }
            }
        }

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
