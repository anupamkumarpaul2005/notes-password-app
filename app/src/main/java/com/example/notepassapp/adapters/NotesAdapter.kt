package com.example.notepassapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.notepassapp.databinding.ItemNoteBinding
import com.example.notepassapp.models.Note

class NotesAdapter(
    private val onNoteClick: (Note) -> Unit // Click listener as a parameter
) : RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {

    private val notesList = mutableListOf<Note>()

    fun submitList(notes: List<Note>) {
        notesList.clear()
        notesList.addAll(notes)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = ItemNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(notesList[position])
    }

    override fun getItemCount() = notesList.size

    inner class NoteViewHolder(private val binding: ItemNoteBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(note: Note) {
            binding.noteTitle.text = note.title
            binding.noteDescription.text = note.description
            binding.noteTimestamp.text = java.text.DateFormat.getDateTimeInstance().format(note.timestamp)

            // Set up the click listener
            binding.root.setOnClickListener {
                onNoteClick(note)
            }
        }
    }
}
