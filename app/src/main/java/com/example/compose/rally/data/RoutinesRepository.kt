package com.example.compose.rally.data

import kotlinx.coroutines.flow.Flow

class RoutinesRepository(private val routineDao: RoutineDao) {
     fun getAllRoutinesStream(): Flow<List<Routine>> = routineDao.getAllRoutines()

     fun getRoutinesStreamByBacklogId(backlogId:Int): Flow<List<Routine>> = routineDao.getRoutinesByBacklogId(backlogId)

     fun getRoutineStream(id: Int): Flow<Routine?> = routineDao.getRoutine(id)

     suspend fun insertRoutine(routine: Routine) = routineDao.insert(routine)

     suspend fun deleteRoutineById(id:Int) = routineDao.deleteRoutineById(id)

     suspend fun updateRoutine(routine: Routine) = routineDao.update(routine)
}
