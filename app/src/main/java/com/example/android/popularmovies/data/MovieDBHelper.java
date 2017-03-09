/*
  MIT License

  Copyright (c) 2017 Elyasin Shaladi

  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
  associated documentation files (the "Software"), to deal in the Software without restriction,
  including without limitation the rights to use, copy, modify, merge, publish, distribute,
  sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
  furnished to do so, subject to the following conditions:

  The above copyright notice and this permission notice shall be included in all copies or
  substantial portions of the Software.

  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
  NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
  DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.example.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.popularmovies.data.MovieContract.MovieEntry;
import com.example.android.popularmovies.data.MovieContract.ReviewEntry;
import com.example.android.popularmovies.data.MovieContract.TrailerEntry;

/**
 * DBHelper for movie database. A movie has 0 or more trailers or reviews.
 */
public class MovieDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "moviesMainDb.db";


    private static final int VERSION = 1;

    public MovieDBHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    /**
     * The movie id is unique and referenced as foreign key by a trailer or review.
     *
     * @param db The SQLite database to manage.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {


        final String CREATE_MOVIE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                MovieEntry.COLUMN_MOVIE_OVERVIEW + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_MOVIE_RELEASE_DATE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_MOVIE_RUNTIME + " INTEGER NOT NULL, " +
                MovieEntry.COLUMN_MOVIE_VOTE_AVERAGE + " REAL NOT NULL, " +
                MovieEntry.COLUMN_MOVIE_W92_POSTER + " BLOB NOT NULL, " +
                MovieEntry.COLUMN_MOVIE_W185_POSTER + " BLOB NOT NULL, " +
                MovieEntry.COLUMN_MOVIE_POSTER_PATH + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_MOVIE_FAVORITE + " INTEGER NOT NULL, " +
                " UNIQUE (" + MovieEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";


        final String CREATE_TRAILER_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TrailerEntry.TABLE_NAME + " (" +
                TrailerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TrailerEntry.COLUMN_TRAILER_ID + " TEXT NOT NULL, " +
                TrailerEntry.COLUMN_TRAILER_KEY + " TEXT NOT NULL, " +
                TrailerEntry.COLUMN_TRAILER_NAME + " TEXT NOT NULL, " +
                TrailerEntry.COLUMN_TRAILER_SITE + " TEXT NOT NULL, " +
                TrailerEntry.COLUMN_TRAILER_TYPE + " TEXT NOT NULL, " +
                TrailerEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                "FOREIGN KEY(" + TrailerEntry.COLUMN_MOVIE_ID + ") REFERENCES " +
                MovieEntry.TABLE_NAME + "(" + MovieEntry.COLUMN_MOVIE_ID + ")" +
                " UNIQUE (" + TrailerEntry.COLUMN_TRAILER_ID + ") ON CONFLICT REPLACE);";


        final String CREATE_REVIEW_TABLE = "CREATE TABLE IF NOT EXISTS " +
                ReviewEntry.TABLE_NAME + " (" +
                ReviewEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ReviewEntry.COLUMN_REVIEW_ID + " TEXT NOT NULL, " +
                ReviewEntry.COLUMN_REVIEW_AUTHOR + " TEXT NOT NULL, " +
                ReviewEntry.COLUMN_REVIEW_CONTENT + " TEXT NOT NULL, " +
                ReviewEntry.COLUMN_REVIEW_URL + " TEXT NOT NULL, " +
                ReviewEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                "FOREIGN KEY(" + ReviewEntry.COLUMN_MOVIE_ID + ") REFERENCES " +
                MovieEntry.TABLE_NAME + "(" + MovieEntry.COLUMN_MOVIE_ID + ")" +
                " UNIQUE (" + ReviewEntry.COLUMN_REVIEW_ID + ") ON CONFLICT REPLACE);";

        db.execSQL(CREATE_MOVIE_TABLE);
        db.execSQL(CREATE_TRAILER_TABLE);
        db.execSQL(CREATE_REVIEW_TABLE);
    }

    /**
     * Upgrade the table from old version to new version.
     * TODO Simply dropping tables is not the right thing to do (if you go in production).
     *
     * @param db         The SQLite database.
     * @param oldVersion Old version number.
     * @param newVersion New version number.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TrailerEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ReviewEntry.TABLE_NAME);
        onCreate(db);
    }
}
