package com.sather.todo.glance.data

import android.content.Context
import androidx.compose.ui.util.fastFlatMap
import androidx.glance.GlanceId
import com.sather.todo.data.BacklogDatabase
import com.sather.todo.data.Routine
import com.sather.todo.data.RoutinesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update

/**
 * Glance Widget 的 Repository，负责管理 Routine 数据
 */
class RoutineWidgetRepository(
  private val routinesRepository: RoutinesRepository
) {
  
  private val routines = MutableStateFlow<List<Routine>>(emptyList())
  private val finishedRoutineIds = MutableStateFlow<Set<Long>>(emptySet())
  
  fun routines():Flow<List<Routine>> = routines
  fun finishedIds():Flow<Set<Long>> = finishedRoutineIds
  
  /**
   * 加载数据（从 RoutinesRepository 查询）
   */
  suspend fun load(backlogIds:List<Long>):List<Routine> {
    routines.value = backlogIds.map {
      routinesRepository.getRoutinesStreamByBacklogId(it).first()
    }.fastFlatMap { it }.filter { !it.finished }
    finishedRoutineIds.value = emptySet()
    return routines.value
  }
  
  /**
   * 标记某个 Routine 为完成/未完成
   */
  suspend fun toggleFinished(routineId: Long) {
    val currentFinished = finishedRoutineIds.value.contains(routineId)
    if (!currentFinished) {
      finishedRoutineIds.update { it + routineId } // 先标记为完成（优化 UI 响应）
      routinesRepository.updateFinished(routineId, true) // 更新数据库
    }
  }
  
  companion object {
    private val repositories = mutableMapOf<GlanceId, RoutineWidgetRepository>()
    
    /**
     * 获取或创建 Repository 实例
     */
    fun getRoutineRepo(
      context: Context,
      glanceId: GlanceId,
    ): RoutineWidgetRepository {
      // 获取 Repository（需注入 RoutinesRepository）
      val routinesRepository by lazy {
        RoutinesRepository(BacklogDatabase.getDatabase(context).routineDao())
      }
      return synchronized(repositories){
        repositories.getOrPut(glanceId) {
          RoutineWidgetRepository(routinesRepository)
        }
      }
      }
    }
    
    /**
     * 清理 Repository
     */
  fun cleanUp(glanceId: GlanceId) {
    synchronized(repositories) {
      repositories.remove(glanceId)
    }
  }
}
