package com.mobdeve.s20.arao.patricia.mobdeve_mplify

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.database
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class CreateAccountFragment : Fragment(R.layout.fragment_create_account) {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    @IgnoreExtraProperties
    data class User(
        val username: String? = null,
        val name: String? = null,
        val email: String? = null
    )

    private fun writeUser(name: String, username: String, email: String): Boolean {
        var success = false
//        val user =  User(username, name, email)
        val user = hashMapOf(
            "name" to name,
            "username" to username,
            "email" to email,
            "follower" to 0,
            "following" to 0,
            "profilePicture" to "https://i.pinimg.com/originals/f1/0f/f7/f10ff70a7155e5ab666bcdd1b45b726d.jpg",
        )
        db.collection("users").add(user).addOnSuccessListener { success = true }

        return success
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val signinLink = view.findViewById<TextView>(R.id.signinLink)
        val registerButton = view.findViewById<Button>(R.id.registerButton)
        val nameText = view.findViewById<TextInputEditText>(R.id.name)
        val usernameText = view.findViewById<TextInputEditText>(R.id.username)
        val emailText = view.findViewById<TextInputEditText>(R.id.email)
        val passwordText = view.findViewById<TextInputEditText>(R.id.password)
        val confirmPasswordText = view.findViewById<TextInputEditText>(R.id.confirmPassword)

        // Set click listeners
        val signUpUser = {
            val name = nameText.text.toString()
            val username = usernameText.text.toString()
            val email = emailText.text.toString()
            val password = passwordText.text.toString()
            val confirmPassword = confirmPasswordText.text.toString()

            if (name.isNotEmpty() && password.isNotEmpty() && username.isNotEmpty() && email.isNotEmpty() && confirmPassword.isNotEmpty()) {
                if (password == confirmPassword) {
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                writeUser(name, username, email)
                                Toast.makeText(requireContext(), "Successfully created an account!", Toast.LENGTH_SHORT)
                                    .show()
                                parentFragmentManager.commit {
                                    replace(R.id.flFragment, LoginAccountFragment())
                                    addToBackStack(null) // Optional: Allows the user to navigate back
                                }
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    it.exception.toString(),
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                            }
                        }
                } else {
                    Toast.makeText(requireContext(), "Password do not match!", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                Toast.makeText(requireContext(), "Fields are incomplete!", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        val navigateToLogin = {
            parentFragmentManager.commit {
                replace(R.id.flFragment, LoginAccountFragment())
                addToBackStack(null) // Optional: Allows the user to navigate back
            }
        }

        signinLink.setOnClickListener { navigateToLogin() }
        registerButton.setOnClickListener { signUpUser() }

        // Find the TextView and set up click listener
//        val signinLink = view.findViewById<TextView>(R.id.signinLink)
//        signinLink.setOnClickListener {
//            parentFragmentManager.commit {
//                replace(R.id.flFragment, LoginAccountFragment())
//                addToBackStack(null) // Adds to back stack for navigation
//            }
//        }
//
//        val loginPageLink = view.findViewById<Button>(R.id.registerButton)
//        loginPageLink.setOnClickListener {
//            parentFragmentManager.commit {
//                replace(R.id.flFragment, LoginAccountFragment())
//                addToBackStack(null)
//            }
//        }

//        val mplifyTitle = findViewById<TextView>(R.id.mplifyTitle)
//        val gradient = mplifyTitle.paint
//        val width = gradient.measureText(mplifyTitle.text.toString())
//        mplifyTitle.paint.shader = LinearGradient(
//            0f,0f, width, mplifyTitle.textSize, intArrayOf(
//                Color.parseColor("#0FA3B1"),
//                Color.parseColor("#F7A072")
//            ), null, Shader.TileMode.REPEAT
//        )
    }
}