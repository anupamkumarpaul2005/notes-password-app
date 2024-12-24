package com.example.notepassapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Check if a user is already logged in
        val currentUser = auth.currentUser

        if (currentUser != null) {
            val intent = Intent(this, NotesActivity::class.java) // Replace LoginActivity with your login/signup page activity
            startActivity(intent)
            finish() // Finish MainActivity to prevent going back to it
        } else {
            // User is not logged in, redirect to login/signup page
            val intent = Intent(this, LoginActivity::class.java) // Replace LoginActivity with your login/signup page activity
            startActivity(intent)
            finish() // Finish MainActivity to prevent going back to it
        }
    }
}
