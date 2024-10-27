package com.example.compose.rally.data

import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "backlogs")
data class Backlog(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 1,
    val timeTitle: String,
    val routineListJson: String,
    val importCredit: Float =4f,
    val normalCredit: Float =0f,
    val faverCredit: Float =2f,
    )