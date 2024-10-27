/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.compose.rally.data

import com.example.compose.rally.data.Backlog
import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides insert, update, delete, and retrieve of [Backlog] from a given data source.
 */
interface BacklogsRepository {
    /**
     * Retrieve all the backlogs from the the given data source.
     */
    fun getAllBacklogsStream(): Flow<List<Backlog>>

    fun getBacklogStream(id: Int): Flow<Backlog?>
    fun getBacklogStreamByString(timeTitle: String): Flow<Backlog?>

    suspend fun insertBacklog(backlog: Backlog)

    suspend fun deleteBacklog(backlog: Backlog)

    /**
     * Update backlog in the data source
     */
    suspend fun updateBacklog(backlog: Backlog)
}
