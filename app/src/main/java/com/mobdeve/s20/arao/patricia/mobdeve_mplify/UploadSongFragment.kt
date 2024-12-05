package com.mobdeve.s20.arao.patricia.mobdeve_mplify

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.mobdeve.s20.arao.patricia.mplify.ProfileFragment
import com.squareup.picasso.Picasso
import java.util.UUID

class UploadSongFragment : Fragment() {

    private lateinit var imageView: ImageView
    private lateinit var button: FloatingActionButton
    private lateinit var btnSelectFile: Button
    private lateinit var songURI: Uri
    private lateinit var imageUri: Uri
    private lateinit var songGenreInput: TextInputEditText
    private lateinit var songTitleText: TextInputEditText

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    companion object {
        const val FILE_PICKER_REQUEST_CODE = 1001
        const val IMAGE_PICKER_REQUEST_CODE = 1002
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_upload_song, container, false)

        imageView = view.findViewById(R.id.uploadImage)
        button = view.findViewById(R.id.cameraButton)
        btnSelectFile = view.findViewById(R.id.btnSelectFile)
        songGenreInput = view.findViewById(R.id.songGenre)
        songTitleText = view.findViewById(R.id.songTitle)
        db = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()

        val currentUser = firebaseAuth.currentUser


        // MP3 file picker
        btnSelectFile.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "audio/mpeg"  // Only allow MP3 files
            }
            startActivityForResult(intent, FILE_PICKER_REQUEST_CODE)
        }

        // Image picker for profile picture or album cover
        button.setOnClickListener {
            ImagePicker.with(this)
                .crop()                 // Crop image (optional)
                .compress(1024)         // Final image size will be less than 1 MB (optional)
                .maxResultSize(
                    1080,
                    1080
                )  // Final image resolution will be less than 1080 x 1080 (optional)
                .start(IMAGE_PICKER_REQUEST_CODE)
        }

        val uploadSongBtn = view.findViewById<Button>(R.id.uploadSongButton)
        uploadSongBtn.setOnClickListener {
            if (::imageUri.isInitialized && ::songURI.isInitialized && currentUser != null) {
                val songTitle = songTitleText.text.toString()
                val songGenre = songGenreInput.text.toString()
                val progressDialog = ProgressDialog(context).apply {
                    setMessage("Uploading Song...")
                    setCancelable(false)
                    show()
                }

                val storageRef = FirebaseStorage.getInstance().reference
                val songImageRef = storageRef.child("song_image/${UUID.randomUUID()}.jpg")
                val songMpegRef = storageRef.child("music/${songTitle}.mp3")

                songMpegRef.putFile(songURI).addOnSuccessListener {
                    songMpegRef.downloadUrl.addOnSuccessListener { uri ->
                        val songUrl = uri.toString()

                        // Now upload the image
                        songImageRef.putFile(imageUri).addOnSuccessListener {
                            songImageRef.downloadUrl.addOnSuccessListener { uri ->
                                val imageUrl = uri.toString()

                                // Prepare the new song data
                                val newSong = hashMapOf(
                                    "songTitle" to songTitle,
                                    "songGenre" to songGenre,
                                    "music" to songUrl,
                                    "songImage" to imageUrl,
                                    "artist" to currentUser.email.toString()
                                )

                                db.collection("music").add(newSong).addOnSuccessListener {
                                    progressDialog.dismiss()
                                    Toast.makeText(
                                        requireContext(),
                                        "Song uploaded successfully!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }.addOnFailureListener { e ->
                                    progressDialog.dismiss()
                                    Toast.makeText(
                                        requireContext(),
                                        "Failed to upload song: ${e.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }.addOnFailureListener { e ->
                                progressDialog.dismiss()
                                Toast.makeText(
                                    requireContext(),
                                    "Failed to upload song image: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }.addOnFailureListener { e ->
                            progressDialog.dismiss()
                            Toast.makeText(
                                requireContext(),
                                "Failed to upload song MP3: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }.addOnFailureListener { e ->
                    progressDialog.dismiss()
                    Toast.makeText(
                        requireContext(),
                        "Failed to upload MP3 file: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }


            //            parentFragmentManager.commit {
//                replace(R.id.flFragment, ProfileFragment())
//                addToBackStack(null)
//            }
        }

        return view
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Handle the MP3 file selection
        if (requestCode == FILE_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                songURI = uri
                Toast.makeText(requireContext(), "MP3 File Selected: $uri", Toast.LENGTH_SHORT)
                    .show()
                // Use this URI to upload the MP3 file or perform other actions
            }
        }

        // Handle the image picker result
        else if (requestCode == IMAGE_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                imageUri = uri
                // Set the selected image to the ImageView
                imageView.setImageURI(uri)
                Toast.makeText(requireContext(), "Image Selected: $uri", Toast.LENGTH_SHORT).show()
                // Use this URI to upload the image or perform other actions
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Error selecting file/image", Toast.LENGTH_SHORT)
                .show()
        }
    }
}
