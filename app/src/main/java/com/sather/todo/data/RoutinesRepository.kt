package com.sather.todo.data

import androidx.work.WorkManager
import kotlinx.coroutines.flow.Flow

class RoutinesRepository(private val routineDao: RoutineDao) {
     fun getAllRoutinesStream(): Flow<List<Routine>> = routineDao.getAllRoutines()

     fun getRoutinesStreamByBacklogId(backlogId:Int): Flow<List<Routine>> = routineDao.getRoutinesByBacklogId(backlogId)

     fun getRoutineStream(id: String): Flow<Routine?> = routineDao.getRoutine(id)

     suspend fun insertRoutine(routine: Routine):Long = routineDao.insert(routine)

     suspend fun deleteRoutineById(id:String) = routineDao.deleteRoutineById(id)
     
     suspend fun deleteRoutineByBacklogId(id:Int) = routineDao.deleteRoutineByBacklogId(id)

     suspend fun updateRoutine(routine: Routine) = routineDao.update(routine)
     
     suspend fun updateFinished(id:String,finished: Boolean)=routineDao.undateFinished(id,finished)
}
