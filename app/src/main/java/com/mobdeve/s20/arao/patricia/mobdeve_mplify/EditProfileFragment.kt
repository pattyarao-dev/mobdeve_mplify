package com.mobdeve.s20.arao.patricia.mobdeve_mplify

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.mobdeve.s20.arao.patricia.mplify.ProfileFragment

class EditProfileFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val saveChangesBtn = view.findViewById<Button>(R.id.saveChangesButton)
        saveChangesBtn.setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.flFragment, ProfileFragment())
                addToBackStack(null)
            }
        }

    }
}