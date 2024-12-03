package com.sather.todo.data

import android.content.Context

interface AppContainer {
    val backlogsRepository: BacklogsRepository
    val routinesRepository: RoutinesRepository
}

class AppDataContainer(private val context: Context): AppContainer {
    override val backlogsRepository: BacklogsRepository by lazy { BacklogsRepository(BacklogDatabase.getDatabase(context).backlogDao()) }
    override val routinesRepository: RoutinesRepository by lazy { RoutinesRepository(BacklogDatabase.getDatabase(context).routineDao()) }
}