package com.android.listapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.android.listapp.model.ListInfo

@Database(entities = [ListInfo::class], version = 1)
abstract class ListDatabase : RoomDatabase() {
    abstract fun listDao(): ListDao

    companion object {
        private var instance: ListDatabase? = null
        @Synchronized // -> 동시에 호출되는 가능성을 방지하기 위해서 사용하고, 순서대로 규칙을 정해 교통정리되는 느낌.
        fun getInstance(context: Context) : ListDatabase? {
            if (instance == null) {
                synchronized(ListDatabase::class) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        ListDatabase::class.java,
                        "list-database"
                    ).build()
                }
            }
            return instance
        }
    }
}