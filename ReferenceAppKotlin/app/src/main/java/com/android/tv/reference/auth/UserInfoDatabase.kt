package com.android.tv.reference.auth

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * A RoomDatabase class that represents the Sqlite db that stores user info
 */
@Database(entities = [UserInfo::class], version = 1, exportSchema = false)
abstract class UserInfoDatabase : RoomDatabase() {

    abstract fun userInfoDao(): UserInfoDao

    companion object {
        @Volatile
        private var INSTANCE: UserInfoDatabase? = null

        fun getDatabase(context: Context): UserInfoDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }

            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserInfoDatabase::class.java,
                    "user_info_database"
                ).fallbackToDestructiveMigration().build()

                INSTANCE = instance
                return instance
            }
        }
    }
}