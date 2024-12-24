package com.example.notepassapp

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notepassapp.adapters.NotesAdapter
import com.example.notepassapp.databinding.ActivityNotesBinding
import com.example.notepassapp.models.Note
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class NotesActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityNotesBinding
    private val db = FirebaseFirestore.getInstance()
    private lateinit var notesAdapter: NotesAdapter
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNotesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup Toolbar and Drawer
        setSupportActionBar(binding.toolbar)
        toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout, binding.toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        binding.navigationView.setNavigationItemSelectedListener(this)

        // Setup RecyclerView
        binding.notesRecyclerView.layoutManager = LinearLayoutManager(this)
        notesAdapter = NotesAdapter { note ->
            val intent = Intent(this, AddEditNoteActivity::class.java)
            intent.putExtra("noteId", note.id)
            startActivity(intent)
        }
        binding.notesRecyclerView.adapter = notesAdapter

        loadNotes()

        binding.addNoteFAB.setOnClickListener {
            startActivity(Intent(this, AddEditNoteActivity::class.java))
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_account -> {
                // Handle account action
            }
            R.id.nav_settings -> {
                // Handle settings action
            }
            R.id.nav_about -> {
                // Handle about action
            }
            R.id.nav_logout -> {
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
        binding.drawerLayout.closeDrawers()
        return true
    }

    private fun loadNotes() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        val notesRef = db.collection("notes")
            .whereEqualTo("userId", userId)
            .orderBy("timestamp", Query.Direction.DESCENDING)

        notesRef.get()
            .addOnSuccessListener { documents ->
                val notes = documents.map { document ->
                    val note = document.toObject(Note::class.java)
                    note.copy(id = document.id)
                }
                notesAdapter.submitList(notes)
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }
}
