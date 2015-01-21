package ru.ifmo.md.exam1;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by daria on 21.01.15.
 */
public class TaskContentProvider extends ContentProvider {
    static final String DB_NAME = "tasks.db";
    static final int DB_VERSION = 1;

    static final String TABLE_TASKS = "tasks";

    private static final int TASKS = 0;
    private static final int TASKS_ID = 1;


    static final String KEY_ID = "id";
    static final String KEY_TITLE = "title";
    static final String KEY_DESCRIPTION = "description";
    static final String KEY_DATE = "date";
    static final String KEY_TAGS = "tags";
    static final String KEY_LABEL = "label";

    static final String AUTHORITY = "ru.ifmo.md";

    static final String TASKS_PATH = TABLE_TASKS;

    private static final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        matcher.addURI(AUTHORITY, TASKS_PATH, TASKS);
        matcher.addURI(AUTHORITY, TASKS_PATH + "/#", TASKS_ID);
    }

    private static final Uri TASKS_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TASKS_PATH);

    DBHelper dbHelper;
    SQLiteDatabase db;

    static final String TABLE_CREATE = "create table " + TABLE_TASKS + "("
            + KEY_ID + " integer primary key autoincrement, "
            + KEY_TITLE + " text, "
            + KEY_DESCRIPTION + " text, "
            + KEY_DATE + " text, "
            + KEY_TAGS + " text, "
            + KEY_LABEL + " text);";

    @Override
    public boolean onCreate() {
        dbHelper = new DBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        switch (matcher.match(uri)) {
            case TASKS:
                queryBuilder.setTables(TABLE_TASKS);
                break;
            case TASKS_ID:
                queryBuilder.setTables(TABLE_TASKS);
                queryBuilder.appendWhere("id=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }

        db = dbHelper.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        db = dbHelper.getWritableDatabase();
        long rowID;
        switch (matcher.match(uri)) {
            case TASKS:
                rowID = db.insert(TABLE_TASKS, null, values);
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        Uri resultUri = Uri.withAppendedPath(uri, ""+rowID);
        getContext().getContentResolver().notifyChange(uri, null);
        return resultUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        db = dbHelper.getWritableDatabase();
        int deleted = 0;
        switch (matcher.match(uri)) {
            case TASKS:
                deleted = db.delete(TABLE_TASKS, null, null);
                break;
            case TASKS_ID:
                String id = uri.getLastPathSegment();
                deleted = db.delete(TABLE_TASKS, KEY_ID + "=" + id, null);
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return deleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        db = dbHelper.getWritableDatabase();
        int updated = 0;
        if (matcher.match(uri) == TASKS_ID) {
            String id = uri.getLastPathSegment();
            updated = db.update(TABLE_TASKS, values, KEY_ID + "=" + id, null);
        }
        else {
            throw new IllegalArgumentException("Wrong URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return updated;
    }

    @Override
    public String getType(Uri uri) {
        return Integer.toString(matcher.match(uri));
    }

    private class DBHelper extends SQLiteOpenHelper {
        public DBHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("PRAGMA foreign_keys=ON;");
            db.execSQL(TABLE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("drop table if exist " + TABLE_TASKS);

            onCreate(db);
        }
    }



}
