package com.example.agendaapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NotesAdapter(private val context: Context, private var notes: List<Note>) :
    RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {

    inner class NoteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvNoteTitle)
        val tvDescription: TextView = view.findViewById(R.id.tvNoteDescription)
        val btnEdit: ImageButton = view.findViewById(R.id.btnEditNote)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDeleteNote)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        holder.tvTitle.text = note.title
        holder.tvDescription.text = note.description
        holder.btnEdit.setOnClickListener { (context as MainActivity).showEditNoteDialog(note) }
        holder.btnDelete.setOnClickListener { (context as MainActivity).showDeleteNoteDialog(note) }
    }

    override fun getItemCount(): Int = notes.size

    fun updateNotes(newNotes: List<Note>) {
        notes = newNotes
        notifyDataSetChanged()
    }
}
