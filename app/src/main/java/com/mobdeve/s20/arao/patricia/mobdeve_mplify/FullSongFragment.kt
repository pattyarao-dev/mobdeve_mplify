package com.mobdeve.s20.arao.patricia.mobdeve_mplify

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import java.io.IOException

class FullSongFragment : Fragment() {

    private lateinit var songImageView: ImageView
    private lateinit var songTitleText: TextView
    private lateinit var artistNameText: TextView
    private lateinit var playButton: ImageButton
    private lateinit var remainingTime: TextView
    private lateinit var timeConsumed: TextView
    private lateinit var seekBar: SeekBar

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private var songTitle: String? = null
    private var artistEmail: String? = null
    private var songUrl: String? = null  // URL of the song to be played
    private lateinit var mediaPlayer: MediaPlayer
    private var songImage: String? = null
    private var updateJob: Job? = null

    private val scope = MainScope()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Get data passed from the activity
        arguments?.let { bundle ->
            songTitle = bundle.getString("songTitle")
            artistEmail = bundle.getString("email")
            songUrl = bundle.getString("songUrl")  // Assuming song URL is passed in the bundle
        }

        return inflater.inflate(R.layout.fragment_full_song, container, false)
    }

    private suspend fun fetchSong(songTitle: String) {
        try {
            val result = db.collection("music")
                .whereEqualTo("songTitle", songTitle)
                .get()
                .await() // This will suspend the coroutine until the query completes.

            for (data in result) {
                songImage = data.getString("songImage").toString()
                songUrl = data.getString("music").toString()
                Log.d("Fetched Song URL", songUrl.toString())
            }
        } catch (e: Exception) {
            Log.e("FetchSongError", "Error fetching song: ${e.message}")
        }
    }

    private fun playMusicFromUrl(url: String) {

    }

    private fun stopMusic() {
        mediaPlayer.stop()
        mediaPlayer.release()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()

        songImageView = view.findViewById(R.id.songImage)
        songTitleText = view.findViewById(R.id.songTitle)
        artistNameText = view.findViewById(R.id.artistName)
        playButton = view.findViewById(R.id.playBtn)
        remainingTime = view.findViewById(R.id.timeRemaining)
        timeConsumed = view.findViewById(R.id.timeConsumed)
        seekBar = view.findViewById(R.id.seekBar)
        mediaPlayer = MediaPlayer()


        Log.d("Actual Title", songTitle.toString())
        Log.d("Song url", songUrl.toString())

        scope.launch {
            // Wait until fetchSong completes.
            fetchSong(songTitle.toString())

            // Set text after fetching data.
            artistNameText.text = artistEmail
            songTitleText.text = songTitle

            // Start playing music only after the song URL is fetched.


            seekBar.progress = 0
            try {
                mediaPlayer.setDataSource(songUrl)
                mediaPlayer.prepareAsync() // Prepare asynchronously
                mediaPlayer.setOnPreparedListener {
                    mediaPlayer.start()  // Start playing once ready
                    playButton.setImageResource(R.drawable.pause) // Change button to pause icon
                    seekBar.max = mediaPlayer.duration
                    remainingTime.text = formatDuration(mediaPlayer.duration)
                    Picasso.get().load(songImage).into(songImageView)
                    startSeekBarUpdate()
                }

                mediaPlayer.setOnCompletionListener {
                    seekBar.progress = 0
                    playButton.setImageResource(R.drawable.play_arrow)
                    updateJob?.cancel() // Reset button to play icon after completion
                }
            } catch (e: IOException) {
                Log.e("MediaPlayer", "Error preparing media player: ${e.message}")
            }
        }

        // Assuming the song URL is provided, start playing when fragment is created
//        songUrl?.let {
//            playMusicFromUrl(it)
//        }

        //playMusicFromUrl(songUrl.toString())


        playButton.setOnClickListener {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
                playButton.setImageResource(R.drawable.play_arrow)
            } else {
                mediaPlayer.start()
                playButton.setImageResource(R.drawable.pause)
            }
        }


        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, changed: Boolean) {
                if (changed) {
                    mediaPlayer.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                updateJob?.cancel() // Cancel update when user starts seeking
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Optionally restart the update after seeking
                if (mediaPlayer.isPlaying) {
                    startSeekBarUpdate() // Restart updating if still playing
                }
            }
        })

    }


    private fun startSeekBarUpdate() {
        updateJob = scope.launch {
            while (mediaPlayer.isPlaying) {
                seekBar.progress = mediaPlayer.currentPosition
                timeConsumed.text = formatDuration(mediaPlayer.currentPosition)
                delay(1000) // Update every second
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopMusic() // Stop and release MediaPlayer when fragment is destroyed
    }

    @SuppressLint("DefaultLocale")
    private fun formatDuration(duration: Int): String {
        val minutes = (duration / 1000) / 60
        val seconds = (duration / 1000) % 60
        return String.format("%d:%02d", minutes, seconds)
    }
}
