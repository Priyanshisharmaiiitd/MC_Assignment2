package com.example.mc_assign2

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.mc_2.WeatherDatabaseHelper


class WeatherDataRepository(context: Context) {
    private val DataHelper = WeatherDatabaseHelper(context)

    @SuppressLint("Range")

    fun getAllWeatherData(): List<TempData> {
        val database = DataHelper.readableDatabase
        val weatherDataList = mutableListOf<TempData>()

        val cursor = database.query(
            WeatherDatabaseHelper.TABLE_NAME,
            arrayOf(
                WeatherDatabaseHelper.COLUMN_DATE,
                WeatherDatabaseHelper.COLUMN_MAX_TEMPERATURE,
                WeatherDatabaseHelper.COLUMN_MIN_TEMPERATURE
            ),
            null,
            null,
            null,
            null,
            null
        )

        while (cursor.moveToNext()) {
            val date = cursor.getString(cursor.getColumnIndex(WeatherDatabaseHelper.COLUMN_DATE))
            val maxTemp = cursor.getDouble(cursor.getColumnIndex(WeatherDatabaseHelper.COLUMN_MAX_TEMPERATURE))
            val minTemp = cursor.getDouble(cursor.getColumnIndex(WeatherDatabaseHelper.COLUMN_MIN_TEMPERATURE))
            weatherDataList.add(TempData(date, maxTemp, minTemp))
        }

        cursor.close()
        database.close()

        return weatherDataList
    }

    fun deleteAllWeatherData() {
        val database = DataHelper.writableDatabase
        database.delete(WeatherDatabaseHelper.TABLE_NAME, null, null)
        database.close()
    }

    fun insertWeather(weather: TempData) {
        val database = DataHelper.writableDatabase

        val values = ContentValues().apply {
            put(WeatherDatabaseHelper.COLUMN_DATE, weather.userdate)
            put(WeatherDatabaseHelper.COLUMN_MAX_TEMPERATURE, weather.maxTemp)
            put(WeatherDatabaseHelper.COLUMN_MIN_TEMPERATURE, weather.minTemp)
        }

        database.insertWithOnConflict(
            WeatherDatabaseHelper.TABLE_NAME,
            null,
            values,
            SQLiteDatabase.CONFLICT_REPLACE
        )
        database.close()
    }

    fun getWeather(date: String): TempData? {
        val database = DataHelper.readableDatabase

        val cursor = database.query(
            WeatherDatabaseHelper.TABLE_NAME,
            arrayOf(
                WeatherDatabaseHelper.COLUMN_DATE,
                WeatherDatabaseHelper.COLUMN_MAX_TEMPERATURE,
                WeatherDatabaseHelper.COLUMN_MIN_TEMPERATURE
            ),
            "${WeatherDatabaseHelper.COLUMN_DATE} = ?",
            arrayOf(date),
            null,
            null,
            null
        )
        @SuppressLint("Range")

        val weather: TempData? = if (cursor.moveToFirst()) {
            TempData(
                cursor.getString(cursor.getColumnIndex(WeatherDatabaseHelper.COLUMN_DATE)),
                cursor.getDouble(cursor.getColumnIndex(WeatherDatabaseHelper.COLUMN_MAX_TEMPERATURE)),
                cursor.getDouble(cursor.getColumnIndex(WeatherDatabaseHelper.COLUMN_MIN_TEMPERATURE))
            )
        } else {
            null
        }

        cursor.close()
        database.close()

        return weather
    }
}

