package com.example.campusbite

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class OrderDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "campusbite.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE orders (id INTEGER PRIMARY KEY AUTOINCREMENT, items TEXT, total REAL, order_time TEXT)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS orders")
        onCreate(db)
    }

    fun saveOrder(items: String, total: Double, time: String) {
        val values = ContentValues()
        values.put("items", items)
        values.put("total", total)
        values.put("order_time", time)
        writableDatabase.insert("orders", null, values)
    }

    fun getOrderHistory(): String {
        val result = StringBuilder()
        val cursor = readableDatabase.rawQuery(
            "SELECT id, items, total, order_time FROM orders ORDER BY id DESC", null
        )
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(0)
                val items = cursor.getString(1)
                val total = cursor.getDouble(2)
                val time = cursor.getString(3)
                result.append("Order #$id\n$items\nTotal: ₹${"%.2f".format(total)}\n$time\n\n")
            } while (cursor.moveToNext())
        }
        cursor.close()
        if (result.isEmpty()) return "No orders placed yet."
        return result.toString()
    }
}
