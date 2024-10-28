package com.example.compose.rally.data

import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "backlogs")
data class Backlog(
    @PrimaryKey(autoGenerate = true)
    val id: Int=0,
    val timeTitle: String ="yyyy-MM-dd",
    val routineListJson: String,
    val importCredit: Float =4f,
    val normalCredit: Float =0f,
    val faverCredit: Float =2f,
    )