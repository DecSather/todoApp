package com.sather.todo.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BacklogDao {
    
    //建议在持久性层中使用 Flow
    // 将返回值类型设为 Flow 后，只要数据库中的数据发生更改，您就会收到通知。
    // 也就是说，您只需要显式获取一次数据。
    // 由于返回值类型为 Flow，Room 还会在后台线程上运行该查询，无需将其明确设为 suspend 函数并在协程作用域内调用它。
    @Query("SELECT * from backlogs ORDER BY isVisible DESC, timeTitle, id DESC")
    fun getAllBacklogs(): Flow<List<Backlog>>
    
    @Query("SELECT * from backlogs WHERE id = :id")
    fun getBacklog(id: Long?): Flow<Backlog>
    
    @Query("SELECT * from backlogs WHERE timeTitle = :timeTitle ORDER BY id DESC")
    fun getBacklogByString(timeTitle: String?): Flow<List<Backlog>>
    @Query("SELECT * from backlogs WHERE isVisible = :isVisible ORDER BY id DESC")
    fun getBacklogByVisible(isVisible: Boolean?): Flow<List<Backlog>>
    
    //    OnConflictStrategy.IGNORE-在 Inventory 应用中，仅从一处（即 Add Backlog 界面）插入实体
//    suspend关键词-单独线程运行-Room 不允许在主线程上访问数据库
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: Backlog):Long
    
    //    @Update 注解与 insert() 方法类似，使用 suspend 关键字标记此函数。
    @Update
    suspend fun update(item: Backlog)
    @Query("UPDATE backlogs SET isExpand = :isExpand WHERE id = :id")
    suspend fun onExpandChange(id:Long,isExpand:Boolean)
    @Query("UPDATE backlogs SET isVisible = :isVisible,isExpand = :isVisible WHERE id = :id")
    suspend fun onVisibleChange(id:Long,isVisible:Boolean)
    
    @Query("DELETE FROM backlogs WHERE id = :id")
    suspend fun deleteBacklogById(id: Long)
    
    
}