package cnu.lineup.com.cnulineup;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

import static android.provider.BaseColumns._ID;

/**
 * Created by macgongmon on 2/9/17.
 */

public class DBOpenHelper {
    public static SQLiteDatabase DB;

    private static final String DB_NAME = "user_info.db";
    private static final int DB_VERSION = 1;
    public static final String CREATE_FAVORITE =
            "create table favorite (_id integer primary key autoincrement, name text not null); ";
    public static final String CREATE_VOTE_LIST = "create table vote (_id integer primary key " +
            "autoincrement, name text not null, proportion text not null);";

    private DatabaseHelper DBHelper;
    private Context context;



    private class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context, String name,
                              SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_FAVORITE);
            db.execSQL(CREATE_VOTE_LIST);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }

        @Override
        public void onOpen(SQLiteDatabase db) {
            super.onOpen(db);
        }
    }


    public DBOpenHelper(Context context) {
        this.context = context;
    }


    public DBOpenHelper open() throws SQLException {
        DBHelper = new DatabaseHelper(context, DB_NAME, null, DB_VERSION);
        //DB = DBHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        DB.close();
    }

    public List getFavoriteRestaurant(){
        DB = DBHelper.getReadableDatabase();
        String tableName = "favorite";
        //String[] projection = {"name"};

        Cursor cursor = DB.query(tableName,null,null,null,null,null,null);;
        List resultList = new ArrayList<>();
        while(cursor.moveToNext()){
            String name = cursor.getString(cursor.getColumnIndex("name"));
            resultList.add(name);
        }
        //cursor.close();
        return resultList;
    }

    public List getVote(){
        DB = DBHelper.getReadableDatabase();
        String tableName = "vote";
        //String[] projection = {"name","proportion"};

        Cursor cursor = DB.query(tableName,null,null,null,null,null,null);
        List resultList = new ArrayList<ArrayList<String>>();

        while(cursor.moveToNext()){
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String proportion = cursor.getString(cursor.getColumnIndex("proportion"));
            List<String> item = new ArrayList<String>();
            item.add(name);
            item.add(proportion);
            resultList.add(item);
        }
        cursor.close();
        return resultList;
    }

    public void insertFavoriteRestaurant(String name){
        DB = DBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name",name);

        DB.insert("favorite",null,values);
    }

    public void insertVote(String name, String proportion){
        DB = DBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name",name);
        values.put("proportion",proportion);

        DB.insert("vote",null,values);
    }

}

