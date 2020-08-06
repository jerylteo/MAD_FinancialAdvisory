package edu.nyp.financialadvisory;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class AccountsDBAdapter {
    private static final String TAG = "AccountsDBAdapter";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";

    private static final String DB_NAME = "FinancialAdvisoryDB";
    private static final String DB_TABLE = "accounts";
    private static final int DB_VERSION = 1;

    private static final String DB_CREATE =
            "create table accounts (_id integer primary key " +
            "autoincrement, name text not null," +
            "email text not null);";

    private Context context;

    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public AccountsDBAdapter(Context ctx) {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    // OPENS DB
    public AccountsDBAdapter open() throws SQLException {
        System.out.println("Opening DB, should create DB here.");
        db = DBHelper.getWritableDatabase();
        return this;
    }

    // CLOSES DB
    public void close() {
        DBHelper.close();
    }

    // INSERTS RECORD
    public void insertAccount(String name, String email) {
        String sqlStatement = "INSERT into " + DB_TABLE + " (" + KEY_NAME + "," + KEY_EMAIL + ") VALUES ('"+ name +"','"+ email +"')";
        db.execSQL(sqlStatement);
    }

    // RETRIEVES ALL ACCOUNTS - NOT USED, FOR REFERENCE ONLY
    public Cursor getAllAccounts() {
        Cursor mCursor = db.rawQuery("SELECT * FROM " + DB_TABLE, null);
        return mCursor;
    }

    // RETRIEVES SPECIFIC ACCOUNT
    public Cursor getAccount(String name) {
        Cursor mCursor = db.rawQuery("SELECT * FROM " + DB_TABLE + " WHERE name = '" + name + "'", null);
        return mCursor;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            try {
                System.out.println("Creating....");
                System.out.println(DB_CREATE);
                db.execSQL(DB_CREATE);
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public void onUpgrade(SQLiteDatabase db, int i, int i1) {
            Log.d(TAG, "Upgrading DB from v-" + i + " to v-" + i1 + ", which will destroy all old data.");
            db.execSQL("DROP TABLE IF EXISTS accounts");
        }
    }
}
