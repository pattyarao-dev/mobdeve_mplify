package com.mobdeve.s20.arao.patricia.mobdeve_mplify

import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mobdeve.s20.arao.patricia.mobdeve_mplify.CreateAccountFragment
import com.mobdeve.s20.arao.patricia.mobdeve_mplify.LoginAccountFragment
import com.mobdeve.s20.arao.patricia.mobdeve_mplify.R
import com.mobdeve.s20.arao.patricia.mplify.ProfileFragment
import kotlin.math.log

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



//        val createAccount = CreateAccountFragment()
//        val loginAccount = LoginAccountFragment()

        if (savedInstanceState == null){
            replaceFragment(LoginAccountFragment())
        }

        bottomNavigationView = findViewById(R.id.bottomNavigation)

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    replaceFragment(HomeFragment())
                    true
                }
                R.id.profile -> {
                    replaceFragment(ProfileFragment())
                    true
                }
                else -> false
            }}



    }

    private fun replaceFragment(fragment: Fragment) {

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, fragment)
            commit()
        }
    }
}