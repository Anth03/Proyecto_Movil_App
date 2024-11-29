package com.example.proyectofinal.data

import androidx.room.TypeConverter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Converters {
    @TypeConverter
    fun fromString(value: String?): List<String> {
        return value?.split(",") ?: emptyList()
    }

    @TypeConverter
    fun fromList(list: List<String>): String {
        return list.joinToString(",")
    }

    fun fromStringToDate(dateString: String?): Date? {
        return dateString?.let { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(it) }
    }

    @TypeConverter
    fun fromDateToString(date: Date?): String? {
        return date?.let { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(it) }
    }
}