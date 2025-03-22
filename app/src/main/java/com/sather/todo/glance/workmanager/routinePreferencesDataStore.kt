package com.sather.todo.glance.workmanager
import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.sather.todo.data.Routine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
private const val WIDGET_PREFERENCES_NAME = "widget_data"
// 创建 DataStore
val Context.dataStore by preferencesDataStore(name = WIDGET_PREFERENCES_NAME)
const val DEFAULT_TIMETITLE = "1970-01-01"
// 定义 DataStore 的 Key
object DataStoreKeys {
    val ROUTINES_KEY = stringPreferencesKey("routines")
    val TIMETITLE = stringPreferencesKey("timeTitle")
}

// 存储数据到 DataStore
suspend fun saveRoutinesToDataStore(context: Context, data: List<Routine>) {
    val jsonString = Json.encodeToString(data)
    context.dataStore.edit { preferences ->
        preferences[DataStoreKeys.ROUTINES_KEY] = jsonString
    }
}
suspend fun saveTimeTitleToDataStore(context: Context, data: String) {
    context.dataStore.edit { preferences ->
        preferences[DataStoreKeys.TIMETITLE] = data
    }
}
// 从 DataStore 读取routine数据
fun getRoutinesFromDataStore(context: Context):Flow<List<Routine>> = context.dataStore.data
        .map { preferences ->
            val jsonString = preferences[DataStoreKeys.ROUTINES_KEY] ?: return@map listOf(
                Routine(
                    backlogId = -1,
                    sortId = 0,
                    content = "getDataFromDataStore wrong"
                )
            )
            Json.decodeFromString(jsonString)
        }
fun getTimeTitleFromDataStore(context: Context):Flow<String> = context.dataStore.data
    .map { preferences ->
         preferences[DataStoreKeys.TIMETITLE] ?: return@map DEFAULT_TIMETITLE
    }
