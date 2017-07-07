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
    public static final String CREATE_VOTE_COUNT = "create table count (_id integer primary key " +
            "autoincrement, time text not null, count integer not null)";

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
            db.execSQL(CREATE_VOTE_COUNT);
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

        Cursor cursor = DB.query(tableName,null,null,null,null,null,null);;
        List resultList = new ArrayList<>();
        while(cursor.moveToNext()){
            String name = cursor.getString(cursor.getColumnIndex("name"));
            resultList.add(name);
        }
        cursor.close();
        return resultList;
    }

    public List getVote(){
        DB = DBHelper.getReadableDatabase();
        String tableName = "vote";

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

    public int getCount(){
        DB = DBHelper.getReadableDatabase();
        String tableName = "count";

        Cursor cursor = DB.query(tableName,null,null,null,null,null,null);
        List resultList = new ArrayList<ArrayList<String>>();

        while(cursor.moveToNext()){
            String time = cursor.getString(cursor.getColumnIndex("time"));
            int count =
        }
    }

    // 즐겨찾기 식당 DB에 저장
    public void insertFavoriteRestaurant(String name){
        DB = DBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name",name);

        DB.insert("favorite",null,values);
    }

    // 투표했을 경우, 자신이 투표한 테이블에 투표가게 이름과 비율 저장
    public void insertVote(String name, String proportion){
        DB = DBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name",name);
        values.put("proportion",proportion);

        DB.insert("vote",null,values);
    }

    // 가게이름 받아서 해당 가게를 DB에서 삭제
    public void deleteFavorite(String name){
        DB = DBHelper.getWritableDatabase();
        DB.delete("favorite","name='" + name + "'",null);
    }

    // 기존 count테이블에 존재하는
    public void decreaseVoteCount(){
        DB = DBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("time",UtilMethod.getTimeNow());
        values.put("");
        DB.update("count",ContentValues, where)
    }

}

