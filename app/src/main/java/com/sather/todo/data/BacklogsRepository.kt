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

package com.sather.todo.data

import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides insert, update, delete, and retrieve of [Backlog] from a given data source.
 */
class BacklogsRepository(private val backlogDao: BacklogDao) {
     
     fun getAllBacklogsStream(): Flow<List<Backlog>> = backlogDao.getAllBacklogs()
     fun getBacklogByString(timeTitle: String): Flow<List<Backlog>> = backlogDao.getBacklogByString(timeTitle)
     fun getBacklogByVisible(isVisible: Boolean): Flow<List<Backlog>> = backlogDao.getBacklogByVisible(isVisible)
    
     fun getBacklogStream(id: Long): Flow<Backlog?> = backlogDao.getBacklog(id)
     
     
    
     suspend fun insertBacklog(backlog: Backlog) = backlogDao.insert(backlog)
    
     suspend fun deleteBacklogById(id:Long) = backlogDao.deleteBacklogById(id)
    
     suspend fun updateBacklog(backlog: Backlog) = backlogDao.update(backlog)
     suspend fun onExpandChange(id:Long,isExpand:Boolean) =backlogDao.onExpandChange(id,isExpand)
     suspend fun onVisibleChange(id:Long,isVisible:Boolean) =backlogDao.onVisibleChange(id,isVisible)
}
