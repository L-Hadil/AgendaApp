package com.example.agendaapp

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Serializable
import java.util.UUID

data class Note(
    val id: String = UUID.randomUUID().toString(),
    var date: String = "",
    var time: String = "",
    var title: String = "",
    var description: String = "",
    var isEnabled: Boolean = true
) : Serializable

// Classe utilitaire pour la sauvegarde et le chargement des notes
object NoteManager {
    private const val PREFS_NAME = "NotesPrefs"
    private const val NOTES_KEY = "notes"

    // Sauvegarde toutes les notes
    fun saveNotes(context: Context, notes: List<Note>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val json = Gson().toJson(notes)
        editor.putString(NOTES_KEY, json)
        editor.apply()
    }

    // Charge toutes les notes
    fun loadNotes(context: Context): MutableList<Note> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(NOTES_KEY, null)
        val type = object : TypeToken<List<Note>>() {}.type
        return json?.let {
            Gson().fromJson(json, type)
        } ?: mutableListOf()
    }
}