package com.frostdev.sukamus.database

import android.content.ContentValues
import android.content.Context
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import com.frostdev.sukamus.database.DatabaseContract.KamusColumns
import com.frostdev.sukamus.model.ModelKamus

class KamusHelper(private val context: Context?) {
    private var dataBaseHelper: DatabaseHelper? = null

    private var database: SQLiteDatabase? = null

    private var table: String? = null

    private fun checkLanguage(language: Boolean) {
        if (language) { //language is true (1)
            table = DatabaseContract.TABLE_ENGLISH
        } else { //language is false (0)
            table = DatabaseContract.TABLE_INDONESIA
        }
    }

    @Throws(SQLException::class)
    fun open(): KamusHelper {
        dataBaseHelper = DatabaseHelper(context)
        database = dataBaseHelper!!.getWritableDatabase()
        return this
    }

    fun close() {
        dataBaseHelper!!.close()
    }

    fun selectAll(language: Boolean): ArrayList<ModelKamus?> {
        checkLanguage(language)

        val cursor = database!!.query(
            table!!,
            null,
            null,
            null,
            null,
            null,
            DatabaseContract.KamusColumns._ID + " ASC",
            null
        )
        cursor.moveToFirst()
        val arrayList = ArrayList<ModelKamus?>()
        var kamus: ModelKamus?
        if (cursor.getCount() > 0) {
            do {
                kamus = ModelKamus(
                    cursor.getInt(cursor.getColumnIndexOrThrow(BaseColumns._ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(KamusColumns.KATA)),
                    cursor.getString(cursor.getColumnIndexOrThrow(KamusColumns.DESKRIPSI))
                )
                arrayList.add(kamus)
                cursor.moveToNext()
            } while (!cursor.isAfterLast())
        }
        cursor.close()
        return arrayList
    }

    fun selectByKata(kata: String?, language: Boolean): ArrayList<ModelKamus?> {
        checkLanguage(language)

        val cursor =
            database!!.rawQuery("SELECT * FROM " + table + " WHERE kata LIKE '" + kata + "%'", null)
        cursor.moveToFirst()
        val arrayList = ArrayList<ModelKamus?>()
        var kamus: ModelKamus?
        if (cursor.getCount() > 0) {
            do {
                kamus = ModelKamus(
                    cursor.getInt(cursor.getColumnIndexOrThrow(BaseColumns._ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(KamusColumns.KATA)),
                    cursor.getString(cursor.getColumnIndexOrThrow(KamusColumns.DESKRIPSI))
                )
                arrayList.add(kamus)
                cursor.moveToNext()
            } while (!cursor.isAfterLast())
        }
        cursor.close()
        return arrayList
    }

    fun insert(kamus: ModelKamus, language: Boolean): Long {
        checkLanguage(language)

        val initialValues = ContentValues()
        initialValues.put(KamusColumns.KATA, kamus.kata)
        initialValues.put(KamusColumns.DESKRIPSI, kamus.deskripsi)
        return database!!.insert(table!!, null, initialValues)
    }

    fun update(kamus: ModelKamus, language: Boolean): Int {
        checkLanguage(language)

        val args = ContentValues()
        args.put(KamusColumns.KATA, kamus.kata)
        args.put(KamusColumns.DESKRIPSI, kamus.deskripsi)
        return database!!.update(table!!, args, BaseColumns._ID + "= '" + kamus.id + "'", null)
    }

    fun delete(id: Int, language: Boolean): Int {
        checkLanguage(language)
        return database!!.delete(table!!, BaseColumns._ID + " = '" + id + "'", null)
    }

    fun insertTransaction(listKamus: ArrayList<ModelKamus?>, language: Boolean) {
        checkLanguage(language)

        val sql = ("INSERT INTO " + table + " (" + KamusColumns.KATA + ", " + KamusColumns.DESKRIPSI
                + ") VALUES (?, ?)")

        database!!.beginTransaction()
        val stmt = database!!.compileStatement(sql)
        for (i in listKamus.indices) {
            stmt.bindString(1, listKamus[i]!!.kata)
            stmt.bindString(2, listKamus[i]!!.deskripsi)
            stmt.execute()
            stmt.clearBindings()
        }
        database!!.setTransactionSuccessful()
        database!!.endTransaction()
    }
}