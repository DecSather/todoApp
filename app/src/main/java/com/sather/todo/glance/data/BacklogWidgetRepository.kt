package com.sather.todo.glance.data

import android.content.Context
import androidx.glance.GlanceId
import com.sather.todo.data.BacklogDatabase
import com.sather.todo.data.BacklogsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first

class BacklogWidgetRepository(
    private val backlogsRepository: BacklogsRepository
) {
    private val backlogIds = MutableStateFlow<List<Long>>(emptyList())
    
    
    /**
     * 加载数据（从 BacklogsRepository 查询）
     */
    suspend fun load(timeTitle:String):List<Long> {
        backlogIds.value = backlogsRepository
            .getBacklogByString(timeTitle)
            .first() // 取 Flow 的第一个值
            .map { it.id } // 提取所有匹配的 backlogId
        return backlogIds.value
    }
    
    companion object {
        private val repositories = mutableMapOf<GlanceId, BacklogWidgetRepository>()
        
        /**
         * 获取或创建 Repository 实例
         */
        fun getBacklogRepo(
            context: Context,
            glanceId: GlanceId,
        ): BacklogWidgetRepository {
            // 获取 Repository（需注入 BacklogsRepository）
            val backlogsRepository by lazy {
                BacklogsRepository(BacklogDatabase.getDatabase(context).backlogDao())
            }
            
            return synchronized(repositories) {
                repositories.getOrPut(glanceId) {
                    BacklogWidgetRepository(backlogsRepository)
                }
            }
        }
        fun cleanUp(glanceId: GlanceId) {
            repositories.remove(glanceId)
        }
    }
}