package com.calyx.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.calyx.app.data.local.dao.CallerStatsDao
import com.calyx.app.data.local.entities.CallerStatsEntity
import com.calyx.app.data.local.entities.DailyStatsEntity
import com.calyx.app.data.local.entities.SyncMetadata

@Database(entities = [CallerStatsEntity::class, SyncMetadata::class, DailyStatsEntity::class], version = 1, exportSchema = false)
abstract class CalyzDatabase : RoomDatabase() {
    abstract fun callerStatsDao(): CallerStatsDao

    companion object {
        @Volatile
        private var INSTANCE: CalyzDatabase? = null

        fun getDatabase(context: Context): CalyzDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CalyzDatabase::class.java,
                    "calyz_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
