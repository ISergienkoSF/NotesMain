package com.viol4tsf.noteappm.db

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.viol4tsf.noteappm.model.Group
import com.viol4tsf.noteappm.model.Note

@Database(
    entities = [Note::class, Group::class],
    version = 4,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 3, to = 4)
    ]
)
abstract class NoteDatabase:RoomDatabase() {

    abstract fun getNoteDao(): NoteDao
    abstract fun getGroupDao(): GroupDao

    companion object{
        //видимость для других потоков
        @Volatile
        private var instance: NoteDatabase? = null
        private val LOCK = Any()

        //перегрузка invoke
        operator fun invoke(context: Context) = instance?: synchronized(LOCK){
            instance?:
            createDatabase(context).also {
                instance = it
            }
        }

        private val migration2To3 = object : Migration(2, 3){
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS groups (groupName CHAR NOT NULL PRIMARY KEY)")
            }
        }

        private fun createDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            NoteDatabase::class.java,
            "note-db"
        ).addMigrations(migration2To3).build()
    }
}