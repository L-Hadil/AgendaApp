package com.example.agendaapp

import android.os.Bundle
import android.widget.CalendarView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CalendarViewActivity : AppCompatActivity() {
    private lateinit var calendarView: CalendarView
    private lateinit var tvSelectedDate: TextView
    private lateinit var rvNotesGrid: RecyclerView
    private val notes = mutableListOf<Note>()
    private lateinit var calendarAdapter: CalendarAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar_view)

        calendarView = findViewById(R.id.calendarView)
        tvSelectedDate = findViewById(R.id.tvSelectedDate)
        rvNotesGrid = findViewById(R.id.rvNotesGrid)

        // Charger les notes enregistrées
        notes.addAll(NoteManager.loadNotes(this))

        // 📌 Configuration de la RecyclerView en grille
        calendarAdapter = CalendarAdapter(notes)
        rvNotesGrid.layoutManager = GridLayoutManager(this, 2) // 🛠️ 2 colonnes pour bien afficher
        rvNotesGrid.adapter = calendarAdapter

        // 📌 Sélection de la date et affichage des notes
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
            tvSelectedDate.text = "Notes du $selectedDate"
            calendarAdapter.updateNotes(notes.filter { it.date == selectedDate })
        }
    }
}
