package com.mobdeve.s20.arao.patricia.mobdeve_mplify

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract.Profile
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.mobdeve.s20.arao.patricia.mplify.ProfileFragment
import com.squareup.picasso.Picasso

class EditProfileFragment : Fragment() {

    private lateinit var nameInput: TextInputEditText
    private lateinit var usernameInput: TextInputEditText
    private lateinit var cameraButton: FloatingActionButton
    private lateinit var userPhoto: ImageView
    private lateinit var userPhotoURI: Uri


    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_profile, container, false)
    }

    private fun setPage(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            db.collection("users").whereEqualTo("email", currentUser?.email).get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        val profpic = document.getString("profilePicture")
                        if (profpic != null) {
                            Picasso.get().load(profpic).into(userPhoto)
                        }
                        nameInput.setText(document.getString("name"))
                        usernameInput.setText(document.getString("username"))
                    }
                }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nameInput = view.findViewById(R.id.name)
        usernameInput = view.findViewById(R.id.username)
        cameraButton = view.findViewById(R.id.cameraButton)
        userPhoto = view.findViewById(R.id.userPhoto)
        db = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()

        val currentUser = firebaseAuth.currentUser

        cameraButton.setOnClickListener {
            // Handle button click
            ImagePicker.with(this)
                .crop()                    //Crop image(Optional), Check Customization for more option
                .compress(1024)            //Final image size will be less than 1 MB(Optional)
                .maxResultSize(
                    1080,
                    1080
                )    //Final image resolution will be less than 1080 x 1080(Optional)
                .start()
        }

        setPage(currentUser)

        val saveChangesBtn = view.findViewById<Button>(R.id.saveChangesButton)
        saveChangesBtn.setOnClickListener {
            if (::userPhotoURI.isInitialized && currentUser != null) {
                // Create and show a progress dialog
                val progressDialog = ProgressDialog(context).apply {
                    setMessage("Updating profile...")
                    setCancelable(false)
                    show()
                }

                // Upload the image to Firebase Storage
                val storageRef = FirebaseStorage.getInstance().reference
                val profilePicRef = storageRef.child("image/${currentUser.uid}.jpg")
                profilePicRef.putFile(userPhotoURI)
                    .addOnSuccessListener {
                        // Get the download URL
                        profilePicRef.downloadUrl.addOnSuccessListener { uri ->
                            val photoUrl = uri.toString()
                            val username = usernameInput.text.toString()
                            val name = nameInput.text.toString()

                            db.collection("users")
                                .whereEqualTo("email", currentUser.email)
                                .get()
                                .addOnSuccessListener { documents ->
                                    if (!documents.isEmpty) {
                                        for (document in documents) {
                                            val updates = mapOf(
                                                "profilePicture" to photoUrl,
                                                "username" to username,
                                                "name" to name
                                            )

                                            // Update all fields at once
                                            db.collection("users").document(document.id)
                                                .update(updates)
                                                .addOnSuccessListener {
                                                    progressDialog.dismiss()
                                                    Toast.makeText(
                                                        context,
                                                        "Profile updated successfully",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    parentFragmentManager.commit {
                                                        replace(R.id.flFragment, ProfileFragment())
                                                        addToBackStack(null)
                                                    }
                                                }
                                                .addOnFailureListener { e ->
                                                    progressDialog.dismiss()
                                                    Toast.makeText(
                                                        context,
                                                        "Failed to update profile: ${e.message}",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                        }
                                    } else {
                                        progressDialog.dismiss()
                                        Toast.makeText(
                                            context,
                                            "User document not found",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                                .addOnFailureListener { e ->
                                    progressDialog.dismiss()
                                    Toast.makeText(
                                        context,
                                        "Failed to fetch user document: ${e.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                        }
                    }
                    .addOnFailureListener { e ->
                        progressDialog.dismiss()
                        Toast.makeText(
                            context,
                            "Failed to upload image: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            } else {
                Toast.makeText(
                    context,
                    "No image selected or user not logged in",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            // Image Uri will not be null for RESULT_OK
            val uri: Uri = data.data!!
            userPhotoURI = uri

            // Use Uri object instead of File to avoid storage permissions
            view?.findViewById<ImageView>(R.id.userPhoto)?.setImageURI(uri)
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }
}