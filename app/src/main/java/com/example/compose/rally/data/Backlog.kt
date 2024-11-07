package com.example.compose.rally.data

import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "backlogs")
data class Backlog(
    @PrimaryKey(autoGenerate = true)
    val id: Int=0,
    val timeTitle: String ="yyyy-MM-dd",
    )