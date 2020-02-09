package gr.blackswamp.core.db

import androidx.annotation.VisibleForTesting
import androidx.room.RoomDatabase

@VisibleForTesting(otherwise = VisibleForTesting.NONE)
fun RoomDatabase.count(table: String): Int {
    val cursor = this.query("select count(*) from $table ", null)
    cursor.moveToFirst()
    return cursor.getInt(0)
}

@VisibleForTesting(otherwise = VisibleForTesting.NONE)
fun RoomDatabase.countWhere(table: String, condition: String): Int {
    val cursor = this.query("select count(*) from $table where $condition", null)
    cursor.moveToFirst()
    return cursor.getInt(0)
}