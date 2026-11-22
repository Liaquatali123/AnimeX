package com.liaquatali.animex.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [FavoriteEntity::class, WatchHistoryEntity::class, CatalogCacheEntity::class], version = 3, exportSchema = false)
abstract class AnimeDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
    abstract fun watchHistoryDao(): WatchHistoryDao
    abstract fun catalogCacheDao(): CatalogCacheDao

    companion object {
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS watch_history (mediaUri TEXT NOT NULL PRIMARY KEY, displayName TEXT NOT NULL, positionMs INTEGER NOT NULL, durationMs INTEGER NOT NULL, updatedAt INTEGER NOT NULL)")
            }
        }
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS catalog_cache (cacheKey TEXT NOT NULL, page INTEGER NOT NULL, id TEXT NOT NULL, title TEXT NOT NULL, overview TEXT NOT NULL, posterUrl TEXT, releaseDate TEXT, rating REAL, mediaType TEXT NOT NULL, PRIMARY KEY(cacheKey, page, id))")
            }
        }

        @Volatile private var INSTANCE: AnimeDatabase? = null
        fun get(context: Context): AnimeDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(context.applicationContext, AnimeDatabase::class.java, "animex.db")
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                .build().also { INSTANCE = it }
        }
    }
}
