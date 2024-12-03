package com.mobdeve.s20.arao.patricia.mobdeve_mplify

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit

class CreateAccountFragment: Fragment(R.layout.fragment_create_account) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val signinLink = view.findViewById<TextView>(R.id.signinLink)
        val registerButton = view.findViewById<Button>(R.id.registerButton)

        // Set click listeners
        val navigateToLogin = {
            parentFragmentManager.commit {
                replace(R.id.flFragment, LoginAccountFragment())
                addToBackStack(null) // Optional: Allows the user to navigate back
            }
        }

        signinLink.setOnClickListener { navigateToLogin() }
        registerButton.setOnClickListener { navigateToLogin() }

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