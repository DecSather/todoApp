
package com.example.compose.rally.data

import com.example.compose.rally.data.Backlog
import com.example.compose.rally.data.BacklogDao
import kotlinx.coroutines.flow.Flow
//替换 BacklogsRepository 接口中定义的函数，从 BacklogDao 调用构造函数。
class OfflineBacklogsRepository(private val backlogDao: BacklogDao) : BacklogsRepository {
    override fun getAllBacklogsStream(): Flow<List<Backlog>> = backlogDao.getAllBacklogs()
    
    override fun getBacklogStream(id: Int): Flow<Backlog?> = backlogDao.getBacklog(id)
    override fun getBacklogStreamByString(timeTitle: String): Flow<Backlog?>  = backlogDao.getBacklogByString(timeTitle)

    override suspend fun insertBacklog(backlog: Backlog) = backlogDao.insert(backlog)

    override suspend fun deleteBacklog(backlog: Backlog) = backlogDao.delete(backlog)

    override suspend fun updateBacklog(backlog: Backlog) = backlogDao.update(backlog)
}
