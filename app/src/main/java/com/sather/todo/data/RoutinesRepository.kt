package com.sather.todo.data


import kotlinx.coroutines.flow.Flow

class RoutinesRepository(
     private val routineDao: RoutineDao,
) {
     fun getAllRoutinesStream(): Flow<List<Routine>> = routineDao.getAllRoutines()

     fun getRoutinesStreamByBacklogId(backlogId:Long): Flow<List<Routine>> = routineDao.getRoutinesByBacklogId(backlogId)

     fun getRoutineStream(id: Long): Flow<Routine?> = routineDao.getRoutine(id)

     suspend fun insertRoutine(routine: Routine):Long = routineDao.insert(routine)

     suspend fun deleteRoutineById(id:Long) = routineDao.deleteRoutineById(id)
     
     suspend fun deleteRoutineByBacklogId(id:Long) = routineDao.deleteRoutineByBacklogId(id)

     suspend fun updateRoutine(routine: Routine) = routineDao.update(routine)
     
     suspend fun updateFinished(id:Long,finished: Boolean)=routineDao.undateFinished(id,finished)
}
