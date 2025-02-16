package com.example.agendaapp

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    
    private lateinit var calendarView: CalendarView
    private lateinit var tvSelectedDate: TextView
    private lateinit var rvNotes: RecyclerView
    private lateinit var fabAddNote: FloatingActionButton
    private lateinit var etSearch: EditText
    private lateinit var btnClearSearch: ImageButton
    private lateinit var btnCalendarView: Button

    private val notes = mutableListOf<Note>()
    private lateinit var notesAdapter: NotesAdapter
    private var selectedDate: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // Initialisation des vues
        calendarView = findViewById(R.id.calendarView)
        tvSelectedDate = findViewById(R.id.tvSelectedDate)
        rvNotes = findViewById(R.id.rvNotes)
        fabAddNote = findViewById(R.id.fabAddNote)
        etSearch = findViewById(R.id.etSearch)
        btnClearSearch = findViewById(R.id.btnClearSearch)
        btnCalendarView = findViewById(R.id.btnCalendarView)

        // Configuration du RecyclerView
        rvNotes.layoutManager = LinearLayoutManager(this)
        notesAdapter = NotesAdapter(this, notes)
        rvNotes.adapter = notesAdapter

        loadNotes()

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        selectedDate = dateFormat.format(Date())
        tvSelectedDate.text = "Notes du $selectedDate"

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            selectedDate = dateFormat.format(calendar.time)
            tvSelectedDate.text = "Notes du $selectedDate"
            updateNotesList()
        }

        fabAddNote.setOnClickListener { showAddNoteDialog() }
        btnCalendarView.setOnClickListener {
            val intent = Intent(this, CalendarViewActivity::class.java)
            startActivity(intent)
        }

        setupSearchFunctionality()
    }

    private fun setupSearchFunctionality() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterNotes(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {
                btnClearSearch.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
            }
        })

        btnClearSearch.setOnClickListener { etSearch.text.clear() }
    }

    private fun filterNotes(query: String) {
        val filteredNotes = notes.filter {
            it.title.contains(query, ignoreCase = true) || it.description.contains(query, ignoreCase = true)
        }
        notesAdapter.updateNotes(filteredNotes)
    }

    private fun showAddNoteDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_note, null)
        val etTitle = dialogView.findViewById<EditText>(R.id.etNoteTitle)
        val etDescription = dialogView.findViewById<EditText>(R.id.etNoteDescription)
        val tvDate = dialogView.findViewById<TextView>(R.id.tvSelectedDate)

        tvDate.text = "Date : $selectedDate"

        AlertDialog.Builder(this)
            .setTitle("Ajouter une Note")
            .setView(dialogView)
            .setPositiveButton("Enregistrer") { _, _ ->
                val noteTitle = etTitle.text.toString()
                val noteDescription = etDescription.text.toString()
                if (noteTitle.isNotBlank() && noteDescription.isNotBlank()) {
                    val note = Note(
                        date = selectedDate,
                        title = noteTitle,
                        description = noteDescription
                    )
                    notes.add(note)
                    saveNotes()
                    updateNotesList()
                } else {
                    Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Annuler", null)
            .show()
    }

    fun showEditNoteDialog(note: Note) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_note, null)
        val etTitle = dialogView.findViewById<EditText>(R.id.etNoteTitle)
        val etDescription = dialogView.findViewById<EditText>(R.id.etNoteDescription)
        val tvDate = dialogView.findViewById<TextView>(R.id.tvSelectedDate)

        etTitle.setText(note.title)
        etDescription.setText(note.description)
        tvDate.text = "Date : ${note.date}"

        AlertDialog.Builder(this)
            .setTitle("Modifier la Note")
            .setView(dialogView)
            .setPositiveButton("Enregistrer") { _, _ ->
                note.title = etTitle.text.toString()
                note.description = etDescription.text.toString()
                saveNotes()
                updateNotesList()
            }
            .setNegativeButton("Annuler", null)
            .show()
    }

    fun showDeleteNoteDialog(note: Note) {
        AlertDialog.Builder(this)
            .setTitle("Supprimer la Note")
            .setMessage("Voulez-vous vraiment supprimer cette note ?")
            .setPositiveButton("Supprimer") { _, _ ->
                notes.remove(note)
                saveNotes()
                updateNotesList()
            }
            .setNegativeButton("Annuler", null)
            .show()
    }

    private fun updateNotesList() {
        val filteredNotes = notes.filter { it.date == selectedDate }
        notesAdapter.updateNotes(filteredNotes)
    }

    private fun saveNotes() {
        val prefs = getSharedPreferences("NotesPrefs", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val json = Gson().toJson(notes)
        editor.putString("notes", json)
        editor.apply()
    }

    private fun loadNotes() {
        val prefs = getSharedPreferences("NotesPrefs", Context.MODE_PRIVATE)
        val json = prefs.getString("notes", null)
        val type = object : TypeToken<List<Note>>() {}.type
        val loadedNotes: List<Note>? = json?.let { Gson().fromJson(it, type) }
        notes.clear()
        loadedNotes?.let { notes.addAll(it) }
        updateNotesList()
    }
}
