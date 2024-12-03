package com.mobdeve.s20.arao.patricia.mobdeve_mplify

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit

class LoginAccountFragment: Fragment(R.layout.fragment_login) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val signupLink = view.findViewById<TextView>(R.id.createAccountLink)
        signupLink.setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.flFragment, CreateAccountFragment())
                addToBackStack(null)
            }
        }

        val homepageLink = view.findViewById<Button>(R.id.loginButton)
        homepageLink.setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.flFragment, HomeFragment())
                addToBackStack(null)
            }
        }

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