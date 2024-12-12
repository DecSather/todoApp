package com.sather.todo.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "backlogs")
data class Backlog(
    @PrimaryKey(autoGenerate = true)
    val id: Int=0,
    val timeTitle: String ="",
    val isExpand :Boolean =true,
    )