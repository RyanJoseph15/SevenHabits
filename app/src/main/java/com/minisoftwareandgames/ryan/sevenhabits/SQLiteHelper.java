package com.minisoftwareandgames.ryan.sevenhabits;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by ryan on 12/18/15.
 */
public class SQLiteHelper extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME = "QuadrantDetails";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_QDetails = "main";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_QUAD = "quadrant";
    private static final String COLUMN_DETAILS = "details";
    private static final String DB_QUADRANTS_CREATE =
            "CREATE TABLE IF NOT EXISTS " +
                    TABLE_QDetails +
                    "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TITLE + ", " +
                    COLUMN_QUAD + " INTEGER, " +
                    COLUMN_DETAILS + ")";

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public void addEntry(String title, int quadrant, String details) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_QUAD, quadrant);
        values.put(COLUMN_DETAILS, details);
        database.insert(TABLE_QDetails, null, values);
        database.close();
    }

    public boolean uniqueTitle(String title) {
        SQLiteDatabase database = getReadableDatabase();
        String query = "SELECT " + COLUMN_TITLE + " FROM " + TABLE_QDetails +
                " WHERE " + COLUMN_TITLE + " = '" + title + "'";
        Cursor cursor = database.rawQuery(query, null);
        if (cursor.getCount() > 0) {                                            // if not unique
            cursor.close();
            database.close();
            return false;
        } else {
            cursor.close();
            database.close();
            return true;
        }
    }

    public void removeEntry(String title, int quadrant, String details) {
        SQLiteDatabase database = getWritableDatabase();
        String whereClause = COLUMN_TITLE + " = ? AND " + COLUMN_QUAD + " = ? AND " + COLUMN_DETAILS + " = ?";
        String[] whereArgs = {title, String.valueOf(quadrant), details};
        database.delete(TABLE_QDetails, whereClause, whereArgs);
        database.close();
    }

    public void removeTitle(String title) {
        SQLiteDatabase database = getWritableDatabase();
        String whereClause = COLUMN_TITLE + " = ?";
        String[] whereArgs = {title};
        database.delete(TABLE_QDetails, whereClause, whereArgs);
        database.close();
    }

    public void updateEntry(
            String oldTitle, int oldQuadrant, String oldDetails,
            String newTitle, int newQuadrant, String newDetails) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_TITLE, newTitle);
        contentValues.put(COLUMN_QUAD, newQuadrant);
        contentValues.put(COLUMN_DETAILS, newDetails);
        String whereClause = COLUMN_TITLE + " = ? AND " + COLUMN_QUAD + " = ? AND " + COLUMN_DETAILS + " = ?";
        String[] whereArgs = {oldTitle, String.valueOf(oldQuadrant), oldDetails};
        Log.d("update affected: ", ""+database.update(TABLE_QDetails, contentValues, whereClause, whereArgs) + " rows.");
        database.close();
    }

    public boolean updateTitle(String oldTitle, String newTitle) {
        // check if title already exists
        if (!uniqueTitle(newTitle)) {                                           // Does exist
            return false;
        } else {                                                                // Does not exist
            ArrayList<QuadrantDetail> detailList = getDetails(oldTitle, Utilities.QUADRANT.ALL);
            if (detailList != null) {
                for (QuadrantDetail detail : detailList) {
                    updateEntry(oldTitle, detail.getQuadrant(), detail.getDetails(),
                            newTitle, detail.getQuadrant(), detail.getDetails());
                }
            }
            return true;
        }
    }

    public ArrayList<QuadrantDetail> getDetails(String detailTitle, Utilities.QUADRANT quad) {
        SQLiteDatabase database = getReadableDatabase();
        String [] allColumns = {COLUMN_TITLE, COLUMN_QUAD, COLUMN_DETAILS};
        ArrayList<QuadrantDetail> details = new ArrayList<QuadrantDetail>();
        // set up query for different quadrants
        String query = "SELECT * FROM " + TABLE_QDetails +
                " WHERE " + COLUMN_TITLE + " = '" + detailTitle;
        if (quad != Utilities.QUADRANT.ALL) {
            query += "' AND " + COLUMN_QUAD + " = '";
            switch (quad) {
                case ONE:
                    query += 1 + "'";
                    break;
                case TWO:
                    query += 2 + "'";
                    break;
                case THREE:
                    query += 3 + "'";
                    break;
                case FOUR:
                    query += 4 + "'";
                    break;
            }
        } else query += "' ORDER BY " + COLUMN_QUAD + " ASC";
        Log.d("query", query);
        Cursor cursor = database.rawQuery(query, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE));
                int quadrant = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUAD));
                String detail = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DETAILS));
                QuadrantDetail quadrantDetail = QuadrantDetail.newInstance(title, quadrant, detail);
                details.add(quadrantDetail);
            } while (cursor.moveToNext());
            cursor.close();
            database.close();
            return details;
        } else {
            database.close();
            return null;
        }
    }

    /* ------------------------------------------------------------------------------------ /
     *                                   Override methods
     * ----------------------------------------------------------------------------------- */

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DB_QUADRANTS_CREATE);
        Log.d("onCreate", "Database Created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO: do database upgrade
        Log.w(SQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QDetails);
        onCreate(db);
    }

}
