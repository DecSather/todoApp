package com.sather.todo.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RoutineDao {

    //建议在持久性层中使用 Flow
    // 将返回值类型设为 Flow 后，只要数据库中的数据发生更改，您就会收到通知。
    // 也就是说，您只需要显式获取一次数据。
    // 由于返回值类型为 Flow，Room 还会在后台线程上运行该查询，无需将其明确设为 suspend 函数并在协程作用域内调用它。
    @Query("SELECT * from routines ORDER BY sortId ASC, id ASC")
    fun getAllRoutines(): Flow<List<Routine>>
    @Query("SELECT * from routines WHERE backlogId = :backlogId ORDER BY sortId ASC, id ASC")
    fun getRoutinesByBacklogId(backlogId:Long): Flow<List<Routine>>

    @Query("SELECT * from routines WHERE id = :id")
    fun getRoutine(id: Long?): Flow<Routine>
//    suspend关键词-单独线程运行-Room 不允许在主线程上访问数据库
    @Insert
    suspend fun insert(item: Routine):Long

    //    @Update 注解与 insert() 方法类似，使用 suspend 关键字标记此函数。
    @Update
    suspend fun update(item: Routine)
    
    @Query("UPDATE routines SET finished = :finished, sortId = 0 WHERE id = :id")
    suspend fun undateFinished(id:Long,finished:Boolean)
    
    @Query("DELETE FROM routines WHERE id = :id")
    suspend fun deleteRoutineById(id: Long)
    @Query("DELETE FROM routines WHERE backlogId = :id")
    suspend fun deleteRoutineByBacklogId(id: Long)


}