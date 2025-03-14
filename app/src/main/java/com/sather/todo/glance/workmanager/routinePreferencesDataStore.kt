package com.sather.todo.glance.workmanager
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.sather.todo.data.Routine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
// 创建 DataStore
val Context.dataStore by preferencesDataStore(name = "widget_data")

// 定义 DataStore 的 Key
object DataStoreKeys {
    val ITEMS_KEY = stringPreferencesKey("grouped_items")
}

// 存储数据到 DataStore
suspend fun saveDataToDataStore(context: Context, data: List<Routine>) {
    val jsonString = Json.encodeToString(data)
    context.dataStore.edit { preferences ->
        preferences[DataStoreKeys.ITEMS_KEY] = jsonString
    }
}

// 从 DataStore 读取数据
fun getDataFromDataStore(context: Context):Flow<List<Routine>> {
    return context.dataStore.data
        .map { preferences ->
            val jsonString = preferences[DataStoreKeys.ITEMS_KEY] ?: return@map listOf(
                Routine(
                    backlogId = -1,
                    sortId = 0,
                    content = "getDataFromDataStore wrong"
                )
            )
            Json.decodeFromString(jsonString)
        }
}