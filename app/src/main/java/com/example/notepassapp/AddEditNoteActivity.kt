package com.example.notepassapp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentReference
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.EditText
import android.text.TextWatcher
import android.text.Editable
import android.util.Log
import com.example.notepassapp.models.Note
import com.google.firebase.auth.FirebaseAuth

class AddEditNoteActivity : AppCompatActivity() {

    private lateinit var noteId: String
    private lateinit var descriptionEditText: EditText
    private lateinit var titleEditText: EditText
    private lateinit var db: FirebaseFirestore
    private lateinit var noteRef: DocumentReference  // This should be the correct Firebase type

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_note)

        descriptionEditText = findViewById(R.id.descriptionEditText)
        titleEditText = findViewById(R.id.titleEditText)

        db = FirebaseFirestore.getInstance()

        // Get the noteId from the intent if it exists (if editing an existing note)
        noteId = intent.getStringExtra("noteId") ?: ""

        if (noteId.isNotEmpty()) {
            // If noteId exists, it's an existing note, so load it
            loadNote()
        } else {
            // If no noteId is provided, it's a new note, so create a new note
            createNewNote()
        }

        // Enable real-time updates as the user types
        enableRealTimeUpdates()
    }

    // Load an existing note from Firestore to display in the EditText fields
    private fun loadNote() {
        noteRef = db.collection("notes").document(noteId)
        noteRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val note = document.toObject(Note::class.java)
                    titleEditText.setText(note?.title)
                    descriptionEditText.setText(note?.description)
                }
            }
    }

    // Create a new note if noteId is not provided
    private fun createNewNote() {
        // Get the userId of the currently logged-in user
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        // Create an empty note document in Firestore
        val newNote = Note(title = "", description = "", userId = userId)
        noteRef = db.collection("notes").document() // Firestore generates a new document ID
        noteId = noteRef.id // Save the generated ID to noteId for future updates

        // Save the new note with an empty title and description
        noteRef.set(newNote)
            .addOnSuccessListener {
                Log.d("AddEditNoteActivity", "New note created with ID: $noteId")
            }
            .addOnFailureListener { e ->
                Log.w("AddEditNoteActivity", "Error adding note", e)
            }
    }

    // Enable real-time updates for the note description while typing
    private fun enableRealTimeUpdates() {
        descriptionEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val updatedDescription = s.toString()

                // Update the description in Firestore instantly
                if (noteId.isNotEmpty()) {
                    noteRef.update("description", updatedDescription)
                    noteRef.update("timestamp", System.currentTimeMillis())
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        titleEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val updatedTitle = s.toString()

                // Update the title in Firestore instantly
                if (noteId.isNotEmpty()) {
                    noteRef.update("title", updatedTitle)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    // Handle the back press: delete the note if it's empty
    override fun onBackPressed() {
        val title = titleEditText.text.toString()
        val description = descriptionEditText.text.toString()

        // If both title and description are empty, delete the note and return to the notes page
        if (title.isEmpty() && description.isEmpty() && noteId.isNotEmpty()) {
            noteRef.delete()
                .addOnSuccessListener {
                    super.onBackPressed() // Go back to the previous page
                }
        } else {
            super.onBackPressed() // If content is not empty, just go back without deleting
        }
    }
}
