package com.sather.todo.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "diaries")
data class Diary(
    @PrimaryKey val id: Long = generateSimpleId(),
    val timeTitle: String = "",
    @ColumnInfo(typeAffinity = ColumnInfo.TEXT)
    val content: String = "",
)