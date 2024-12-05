package com.mobdeve.s20.arao.patricia.mobdeve_mplify

import android.os.Bundle
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

class LoginAccountFragment: Fragment(R.layout.fragment_login) {

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()


        val emailText = view.findViewById<TextInputEditText>(R.id.email)
        val passwordText = view.findViewById<TextInputEditText>(R.id.password)

        val signupLink = view.findViewById<TextView>(R.id.createAccountLink)
        signupLink.setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.flFragment, CreateAccountFragment())
                addToBackStack(null)
            }
        }

        val loginUser = {

            val email = emailText.text.toString()
            val password = passwordText.text.toString()

            if(email.isNotEmpty() && password.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(requireContext(), "Successfully logged in!", Toast.LENGTH_SHORT).show()
                        parentFragmentManager.commit {
                            replace(R.id.flFragment, HomeFragment())
                            addToBackStack(null) // Optional: Allows the user to navigate back
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Failed to login!",
                            Toast.LENGTH_LONG
                        )
                            .show()
                    }
                }
            }
        }

        val loginButton = view.findViewById<Button>(R.id.loginButton)

        loginButton.setOnClickListener { loginUser() }

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