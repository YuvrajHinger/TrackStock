package com.example.trackstock;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class DBQueries {
    private Context context;
    private SQLiteDatabase sqLiteDatabase;
    private DatabaseHelper databaseHelper;

    public DBQueries(Context context){
        this.context=context;
    }

    public DBQueries open() throws SQLException{
        databaseHelper = new DatabaseHelper(context);
        sqLiteDatabase = databaseHelper.getWritableDatabase();
        return this;
    }

    public void close(){
        databaseHelper.close();
    }

    public boolean insertItem(MyItem myItem){
        ContentValues values = new ContentValues();
        values.put("name",myItem.getItemName());
        values.put("stock",myItem.getCurrentStock());
        values.put("pic",myItem.getItemImage());
        values.put("price",myItem.getPrice());
        return sqLiteDatabase.insert(DatabaseHelper.TABLE_NAME,null,values)>-1;
    }

    public ArrayList<MyItem> readMyitem(){
        ArrayList<MyItem> myItems = new ArrayList<>();
        try {
            Cursor cursor;
            sqLiteDatabase = databaseHelper.getReadableDatabase();
            cursor = sqLiteDatabase.query(DatabaseHelper.TABLE_NAME,new String[]{"id", "name","price","stock","pic"}, "status=0", null, null, null, null);
            myItems.clear();
            if (cursor.getCount()>0){
                if (cursor.moveToFirst()) {
                    do {
                        myItems.add(new MyItem(cursor.getInt(0),cursor.getString(1),cursor.getInt(2),cursor.getInt(3),cursor.getBlob(4)));
                    } while (cursor.moveToNext());
                }
            }
            cursor.close();
        } catch (Exception e){
            e.printStackTrace();
        }
        return myItems;
    }

}
