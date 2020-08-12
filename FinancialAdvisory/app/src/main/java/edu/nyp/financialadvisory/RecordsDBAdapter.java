package edu.nyp.financialadvisory;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class RecordsDBAdapter {
    private static final String TAG = "RecordDBAdapter";
    private static final String KEY_NAME = "name";
    private static final String KEY_TYPE = "type"  ;        // Expenses, Deposits
    private static final String KEY_CATEGORY = "category";          // Food, Transport, Luxury, Entertainment
    private static final String KEY_AMOUNT = "amount";

    private static final String DB_NAME = "FinancialAdvisoryDB";
    private static final String DB_TABLE = "records";
    private static final int DB_VERSION = 2;

    private static final String DB_CREATE =
            "create table records (_id integer primary key " +
            "autoincrement, name text not null," +
            "type text," +
            "category text," +
            "amount float);";

    private Context context;

    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public RecordsDBAdapter(Context ctx) {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    public RecordsDBAdapter open() throws SQLException {
        System.out.println("Opening rDB, should create rDB here.");
        db = DBHelper.getWritableDatabase();
        return this;
    }

    public void close() {DBHelper.close();}

    public void insertRecord(String name, String type, String category, float amt) {
        String sqlStatement = "INSERT into " + DB_TABLE + " ( " + KEY_NAME + "," + KEY_TYPE + "," + KEY_CATEGORY + "," + KEY_AMOUNT +
                ") VALUES ('" + name + "','" + type + "','" + category + "','" + amt +"')";
        db.execSQL(sqlStatement);
    }

    public Cursor getAllRecordsByName(String name) {
        Cursor mCursor = db.rawQuery("SELECT * FROM " + DB_TABLE + " WHERE name = '" + name + "'", null);
        return mCursor;
    }

    public Cursor getAllRecordsByNameCategory(String name, String category) {
        Cursor mCursor = db.rawQuery("SELECT * FROM " + DB_TABLE + " WHERE name = '" + name + "' AND category = '" + category + "'", null);
        return mCursor;
    }

    public Cursor getAllRecordsByNameType(String name, String type) {
        Cursor mCursor = db.rawQuery("SELECT * FROM " + DB_TABLE + " WHERE name = '" + name + "' AND type = '" + type + "'", null);
        return mCursor;
    }

    public Cursor updateName(String old, String newName) {
        System.out.println(old + " -> " + newName);
        Cursor mCursor = db.rawQuery("UPDATE " + DB_TABLE + " SET name = '" + newName + "' WHERE name = '" + old + "'", null);
        return mCursor;
    }


    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) { super(context, DB_NAME, null, DB_VERSION); }

        public void onCreate(SQLiteDatabase db) {
            try {
                System.out.println("I am in onCreate");
                System.out.println(DB_CREATE);
                db.execSQL(DB_CREATE);
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public void onUpgrade (SQLiteDatabase db, int i, int i1) {
//            db.execSQL("DROP TABLE IF EXISTS records");
            System.out.println("Creating....");
            System.out.println(DB_CREATE);
            db.execSQL(DB_CREATE);
        }
    }
}
