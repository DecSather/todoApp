package com.example.compose.rally.data

import android.content.Context

interface AppContainer {
    val backlogsRepository: BacklogsRepository
}

class AppDataContainer(private val context: Context):AppContainer{
    override val backlogsRepository: BacklogsRepository by lazy { BacklogsRepository(BacklogDatabase.getDatabase(context).backlogDao()) }
}