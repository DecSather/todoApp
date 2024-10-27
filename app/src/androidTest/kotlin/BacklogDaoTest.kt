package  com.example.compose
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.compose.rally.data.BacklogDao
import com.example.compose.rally.data.BacklogDatabase
import org.junit.runner.RunWith
import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.compose.rally.data.Backlog
import com.example.compose.rally.data.fromListToJson
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.IOException

import org.junit.Assert.assertEquals


@RunWith(AndroidJUnit4::class)

class BacklogDaoTest {
//
//    private lateinit var itemDao: BacklogDao
//    private lateinit var inventoryDatabase: BacklogDatabase
//
//    @Before
//    fun createDb() {
//        val context: Context = ApplicationProvider.getApplicationContext()
//        // Using an in-memory database because the information stored here disappears when the
//        // process is killed.
//        inventoryDatabase = Room.inMemoryDatabaseBuilder(context, BacklogDatabase::class.java)
//            // Allowing main thread queries, just for testing.
//            .allowMainThreadQueries()
//            .build()
//        itemDao = inventoryDatabase.backlogDao()
//    }
//
//
//    @After
//    @Throws(IOException::class)
//    fun closeDb() {
//        inventoryDatabase.close()
//    }
//
//    private var item1=Backlog(
//    timeTitle = "2024-1-1",
//    routineListJson = fromListToJson(List<Int>(1,{0})),
//    )
//    private var item2 = Backlog(
//    timeTitle = "2024-1-2",
//    routineListJson = fromListToJson(List<Int>(1,{1})),
//    )
//    private suspend fun addOneItemToDb() {
//        itemDao.insert(item1)
//    }
//
//    private suspend fun addTwoItemsToDb() {
//        itemDao.insert(item1)
//        itemDao.insert(item2)
//    }
//    @Test
//    @Throws(Exception::class)
//    fun daoInsert_insertsItemIntoDB() = runBlocking {
//        addOneItemToDb()
//        val allItems = itemDao.getAllBacklogs().first()
//        assertEquals(allItems[0], item1)
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun daoGetAllItems_returnsAllItemsFromDB() = runBlocking {
//        addTwoItemsToDb()
//        val allItems = itemDao.getAllBacklogs().first()
//        assertEquals(allItems[0], item1)
//    }

}