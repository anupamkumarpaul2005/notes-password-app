package com.example.notepassapp.models

data class Note(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val userId: String = ""
)
