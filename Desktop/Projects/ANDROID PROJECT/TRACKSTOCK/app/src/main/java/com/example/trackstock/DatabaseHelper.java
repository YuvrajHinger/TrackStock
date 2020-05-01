package com.example.trackstock;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "TRACKSTOCK.db";
    public static final String TABLE_NAME = "items";

    public DatabaseHelper(Context context) { super(context, DATABASE_NAME, null, 1); }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+TABLE_NAME+"(id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT,stock INTEGER,price INTEGER,pic BLOB,status DEFAULT 0)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
    }

    public Cursor getMyItem() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME,new String[]{"id", "name","price","stock","pic"}, "status=0", null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public void registerItem(MyItem myItem) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name",myItem.getItemName());
        values.put("stock",myItem.getCurrentStock());
        values.put("pic",myItem.getItemImage());
        values.put("price",myItem.getPrice());
        sqLiteDatabase.insert(TABLE_NAME,null,values);
        sqLiteDatabase.close();
    }

    public void updateItem(int id, int updateStock) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("Update "+TABLE_NAME+" SET stock = "+ updateStock +" WHERE id="+id);
        db.close();
    }

    public void updateItemView(MyItem myItem) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name",myItem.getItemName());
        values.put("stock",myItem.getCurrentStock());
        values.put("pic",myItem.getItemImage());
        values.put("price",myItem.getPrice());
        sqLiteDatabase.update(TABLE_NAME,values,"id="+myItem.getId(),null);
        sqLiteDatabase.close();
    }

    public void deleteItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE "+TABLE_NAME+" SET status='1' WHERE id="+id);
        db.close();
    }
}