package io.github.romanfabio.voidapps.template.feature.home.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import io.github.romanfabio.voidapps.template.data.local.TemplateDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class ItemDaoTest {

    private lateinit var database: TemplateDatabase
    private lateinit var dao: ItemDao

    @Before
    fun setUp() {
        database =
            Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), TemplateDatabase::class.java)
                .allowMainThreadQueries()
                .build()
        dao = database.itemDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `observeAll returns items newest first`() = runTest {
        dao.insert(ItemEntity(name = "First"))
        dao.insert(ItemEntity(name = "Second"))

        assertEquals(listOf("Second", "First"), dao.observeAll().first().map { it.name })
    }

    @Test
    fun `deleteById returns the number of deleted rows`() = runTest {
        val id = dao.insert(ItemEntity(name = "First"))

        assertEquals(1, dao.deleteById(id))
        assertEquals(0, dao.deleteById(id))
        assertEquals(emptyList(), dao.observeAll().first())
    }
}
