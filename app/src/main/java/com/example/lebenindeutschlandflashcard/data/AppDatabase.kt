package com.example.lebenindeutschlandflashcard.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.InputStreamReader

@Database(entities = [Question::class], version = 20, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun questionDao(): QuestionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "flashcard_database"
                )
                .fallbackToDestructiveMigration()
                .addCallback(AppDatabaseCallback(context, scope))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class AppDatabaseCallback(
        private val context: Context,
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDatabaseFromJson(context, database.questionDao())
                }
            }
        }

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    val count = database.query("SELECT COUNT(*) FROM questions", null).use { cursor ->
                        if (cursor.moveToFirst()) cursor.getInt(0) else 0
                    }
                    if (count == 0) {
                        populateDatabaseFromJson(context, database.questionDao())
                    }
                }
            }
        }

        private suspend fun populateDatabaseFromJson(context: Context, questionDao: QuestionDao) {
            try {
                val jsonString = context.assets.open("questions.json").bufferedReader().use { it.readText() }
                
                val moshi = Moshi.Builder()
                    .add(KotlinJsonAdapterFactory())
                    .build()
                
                val listType = Types.newParameterizedType(List::class.java, Question::class.java)
                val adapter = moshi.adapter<List<Question>>(listType)
                
                val questions = adapter.fromJson(jsonString)
                
                if (questions != null) {
                    questionDao.deleteAll()
                    questionDao.insertAll(questions)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
