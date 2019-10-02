package gr.blackswamp.core

import androidx.room.RoomDatabase

fun RoomDatabase.count(table: String): Int {
    val cursor = this.query("select count(*) from $table ", null)
    cursor.moveToFirst()
    return cursor.getInt(0)
}

fun RoomDatabase.countWhere(table: String, condition: String): Int {
    val cursor = this.query("select count(*) from $table where $condition", null)
    cursor.moveToFirst()
    return cursor.getInt(0)
}
