package com.example.proyectofinal.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Note::class], version = 1)
@TypeConverters(Converters::class)
abstract class NoteDB : RoomDatabase() {
    abstract fun NoteDao(): NoteDao
}
