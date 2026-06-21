package com.frostdev.sukamus.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE_ENGLISH)
        db.execSQL(CREATE_TABLE_INDONESIA)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.TABLE_ENGLISH)
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.TABLE_INDONESIA)
        onCreate(db)
    }

    companion object {
        var DATABASE_NAME: String = "db_kamus"

        private const val DATABASE_VERSION = 1

        private val CREATE_TABLE_ENGLISH = String.format(
            ("CREATE TABLE %s"
                    + " (%s INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " %s TEXT NOT NULL," +
                    " %s TEXT NOT NULL)"),
            DatabaseContract.TABLE_ENGLISH,
            DatabaseContract.KamusColumns._ID,
            DatabaseContract.KamusColumns.KATA,
            DatabaseContract.KamusColumns.DESKRIPSI
        )

        private val CREATE_TABLE_INDONESIA = String.format(
            ("CREATE TABLE %s"
                    + " (%s INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " %s TEXT NOT NULL," +
                    " %s TEXT NOT NULL)"),
            DatabaseContract.TABLE_INDONESIA,
            DatabaseContract.KamusColumns._ID,
            DatabaseContract.KamusColumns.KATA,
            DatabaseContract.KamusColumns.DESKRIPSI
        )
    }
}
