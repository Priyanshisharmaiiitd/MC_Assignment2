package com.example.mc_2
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class WeatherDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val createTableSQL = """
            CREATE TABLE $TABLE_NAME (
            $COLUMN_DATE TEXT PRIMARY KEY,
            $COLUMN_MAX_TEMPERATURE REAL,
            $COLUMN_MIN_TEMPERATURE REAL)
        """.trimIndent()
        db.execSQL(createTableSQL)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "Temp.db"
        const val TABLE_NAME = "entries"
        const val COLUMN_DATE = "date"
        const val COLUMN_MAX_TEMPERATURE = "max_temp"
        const val COLUMN_MIN_TEMPERATURE = "min_temp"
    }
}
