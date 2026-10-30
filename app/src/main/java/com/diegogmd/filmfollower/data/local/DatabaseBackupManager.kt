package com.diegogmd.filmfollower.data.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.util.Log
import java.io.File

class DatabaseBackupManager(private val context: Context) {
    fun importDatabase(source: Uri): Boolean {
        val tempFile = File(context.cacheDir, "import-temp.sqlite")

        return try {
            val input = context.contentResolver.openInputStream(source) ?: return false

            input.use { inp ->
                tempFile.outputStream().use { out ->
                    inp.copyTo(out)
                }
            }

            if (!isValidBackup(tempFile)) {
                Log.e("DatabaseBackup", "Not a valid FilmFollower backup")
                return false
            }

            FilmFillowerDatabase.getInstance(context).close()
            val dbFile = context.getDatabasePath(FilmFillowerDatabase.DATABASE_NAME)
            File(dbFile.path + "-wal").delete()
            File(dbFile.path + "-shm").delete()

            tempFile.copyTo(dbFile, overwrite = true)
            Log.d("DatabaseBackup", "Import completed")
            true
        } catch (e: Exception) {
            Log.e("DatabaseBackup", "Import failed", e)
            false
        } finally {
            tempFile.delete()
        }
    }

    fun exportDatabase(destination: Uri): Boolean {
        try {
            val dbHelper = FilmFillowerDatabase.getInstance(context)
            val db = dbHelper.writableDatabase

            // block any writing in the db file
            db.rawQuery("PRAGMA wal_checkpoint(FULL)", null).use { it.moveToFirst() }

            // Open the destination
            val output = context.contentResolver.openOutputStream(destination)
            if (output == null) {
                Log.d("DatabaseBackup", "Couldn't open an output stream for $destination")
                return false
            }

            // Copy the db file inside destination
            val dbFile = context.getDatabasePath(FilmFillowerDatabase.DATABASE_NAME)
            output.use { out ->
                dbFile.inputStream().use { input ->
                    input.copyTo(out)
                }
            }

            Log.d("DatabaseBackup", "Export completed. Location: $destination")
            return true
        } catch (e: Exception) {
            Log.e("DatabaseBackup", "Export failed", e)
            return false
        }
    }
}

private fun isValidBackup(file: File): Boolean {
    return try {
        SQLiteDatabase.openDatabase(file.path, null, SQLiteDatabase.OPEN_READONLY).use { db ->
            val intact = db.rawQuery("PRAGMA integrity_check", null).use {
                it.moveToFirst() && it.getString(0) == "ok"
            }
            val sameVersion = db.version == FilmFillowerDatabase.DATABASE_VERSION
            intact && sameVersion
        }
    } catch (e: Exception) {
        Log.e("DatabaseBackup", "Backup validation failed", e)
        false
    }
}