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

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Content provider for movie data; provides access to movies, trailers and reviews.
 */
@SuppressWarnings("ConstantConditions")
public class MovieContentProvider extends ContentProvider {

    //Uri matcher codes
    public static final int MOVIES = 100;
    public static final int MOVIE_WITH_ID = 101;
    public static final int TRAILERS = 200;
    public static final int TRAILER_WITH_MOVIE_ID = 201;
    public static final int REVIEWS = 300;
    public static final int REVIEW_WITH_MOVIE_ID = 301;

    private static final String LOG_TAG = MovieContentProvider.class.getSimpleName();

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private MovieDBHelper dbHelper;

    /**
     * Add matchers for movies, trailers and reviews.
     *
     * @return a Uri matcher for the movie content provider.
     */
    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_MOVIES, MOVIES);
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_MOVIES + "/#",
                MOVIE_WITH_ID);

        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_TRAILERS, TRAILERS);
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_TRAILERS + "/#",
                TRAILER_WITH_MOVIE_ID);

        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_REVIEWS, REVIEWS);
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_REVIEWS + "/#",
                REVIEW_WITH_MOVIE_ID);

        return uriMatcher;
    }

    /**
     * Initialize the movie DB helper.
     *
     * @return true if the provider was successfully loaded, false otherwise
     */
    @Override
    public boolean onCreate() {
        Context context = getContext();
        dbHelper = new MovieDBHelper(context);
        return true;
    }

    /**
     * Query either of the tables movies, trailers or reviews and registers the Uri to be observerd
     * for changes.
     *
     * @param uri           The Uri for the query.
     * @param projection    Columns to select.
     * @param selection     Where clause.
     * @param selectionArgs Arguments for where clause, if used.
     * @param sortOrder     Column to be sorted by.
     * @return Cursor object as received from db.query(...) method.
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection,
                        String selection, String[] selectionArgs, String sortOrder) {

        final SQLiteDatabase db = dbHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);
        Cursor retCursor;
        String movieID, mSelection;
        String[] mSelectionArgs;
        switch (match) {
            case MOVIES:
                retCursor = db.query(MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case MOVIE_WITH_ID:
                movieID = uri.getPathSegments().get(1);
                mSelection = "movie_id = ?";
                mSelectionArgs = new String[]{movieID};
                retCursor = db.query(MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        mSelection,
                        mSelectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case TRAILERS:
                retCursor = db.query(MovieContract.TrailerEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case TRAILER_WITH_MOVIE_ID:
                movieID = uri.getPathSegments().get(1);
                mSelection = "movie_id = ?";
                mSelectionArgs = new String[]{movieID};
                retCursor = db.query(MovieContract.TrailerEntry.TABLE_NAME,
                        projection,
                        mSelection,
                        mSelectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case REVIEWS:
                retCursor = db.query(MovieContract.ReviewEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case REVIEW_WITH_MOVIE_ID:
                movieID = uri.getPathSegments().get(1);
                mSelection = "movie_id = ?";
                mSelectionArgs = new String[]{movieID};
                retCursor = db.query(MovieContract.ReviewEntry.TABLE_NAME,
                        projection,
                        mSelection,
                        mSelectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }


        retCursor.setNotificationUri(getContext().getContentResolver(), uri);


        return retCursor;
    }

    /**
     * Not used
     *
     * @param uri The Uri to specify the type of.
     * @return The type of the Uri content.
     */
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    /**
     * Inserts data into tables movies, trailers or reviews and notifies the Uri observers
     * of the change.
     *
     * @param uri    The Uri for the inserted data.
     * @param values Content values to insert into table.
     * @return The Uri
     */
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {

        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        Log.d(LOG_TAG, "Doing an insert");
        int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {

            case MOVIES:
                long movieRowID = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                if (movieRowID > 0) {
                    returnUri = ContentUris.
                            withAppendedId(MovieContract.MovieEntry.CONTENT_URI, movieRowID);
                } else {
                    throw new android.database.SQLException("Failed to insert movie data");
                }
                Log.d(LOG_TAG, "Movie inserted");
                break;

            case TRAILERS:
                long trailerRowID = db.insert(MovieContract.TrailerEntry.TABLE_NAME, null, values);
                if (trailerRowID > 0) {
                    returnUri = ContentUris.
                            withAppendedId(MovieContract.TrailerEntry.CONTENT_URI, trailerRowID);
                } else {
                    throw new android.database.SQLException("Failed to insert trailer data");
                }
                Log.d(LOG_TAG, "Trailer inserted");
                break;

            case REVIEWS:
                long reviewRowID = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, values);
                if (reviewRowID > 0) {
                    returnUri = ContentUris.
                            withAppendedId(MovieContract.ReviewEntry.CONTENT_URI, reviewRowID);
                } else {
                    throw new android.database.SQLException("Failed to insert review data");
                }
                Log.d(LOG_TAG, "Review inserted");
                break;

            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    /**
     * Deletes a movie/trailer/review from the corresponding table. In case of a delete notifies
     * the Uri observers of the change.
     *
     * @param uri           The Uri to be deleted.
     * @param selection     The where clause.
     * @param selectionArgs Arguments for where clause if used.
     * @return Number of rows deleted.
     */
    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        int rowsDeleted;
        String movieID;
        switch (match) {
            case MOVIE_WITH_ID:
                movieID = uri.getPathSegments().get(1);
                rowsDeleted = db.delete(
                        MovieContract.MovieEntry.TABLE_NAME, "movie_id = ?", new String[]{movieID}
                );
                break;

            case TRAILER_WITH_MOVIE_ID:
                movieID = uri.getPathSegments().get(1);
                rowsDeleted = db.delete(
                        MovieContract.TrailerEntry.TABLE_NAME, "movie_id = ?", new String[]{movieID}
                );
                break;

            case REVIEW_WITH_MOVIE_ID:
                movieID = uri.getPathSegments().get(1);
                rowsDeleted = db.delete(
                        MovieContract.ReviewEntry.TABLE_NAME, "movie_id = ?", new String[]{movieID}
                );
                break;

            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;

    }

    /**
     * Updates the table movie/trailer/review. If data was update notifies the Uri observers
     * of the change.
     *
     * @param uri           The movies's/trailer's/review's Uri to update.
     * @param values        The new values of the update.
     * @param selection     The where clause.
     * @param selectionArgs Arguments for the where clause, if used.
     * @return Number of rows updated.
     */
    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        int nbrRowsUpdated;
        String movieID;
        switch (match) {
            case MOVIE_WITH_ID:
                movieID = uri.getPathSegments().get(1);
                nbrRowsUpdated = db.update(
                        MovieContract.MovieEntry.TABLE_NAME,
                        values, "_id = ?", new String[]{movieID}
                );
                break;

            case TRAILER_WITH_MOVIE_ID:
                movieID = uri.getPathSegments().get(1);
                nbrRowsUpdated = db.update(
                        MovieContract.TrailerEntry.TABLE_NAME,
                        values, "movie_id = ?", new String[]{movieID}
                );
                break;

            case REVIEW_WITH_MOVIE_ID:
                movieID = uri.getPathSegments().get(1);
                nbrRowsUpdated = db.update(
                        MovieContract.ReviewEntry.TABLE_NAME,
                        values, "movie_id = ?", new String[]{movieID}
                );
                break;

            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }

        if (nbrRowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return nbrRowsUpdated;
    }

    /**
     * Bulk insert of trailer or review data. If data was inserted notifies the Uri observers
     * of the change.
     *
     * @param uri    The Uri of the insertion.
     * @param values The values to insert into the table.
     * @return Number of rows inserted.
     */
    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {

        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)) {
            case TRAILERS:
                db.beginTransaction();
                int trailerRowsInserted = 0;
                try {
                    for (ContentValues value : values) {
                        long id = db.insert(MovieContract.TrailerEntry.TABLE_NAME, null, value);
                        if (id != -1) {
                            trailerRowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if (trailerRowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return trailerRowsInserted;
            case REVIEWS:
                db.beginTransaction();
                int reviewRowsInserted = 0;
                try {
                    for (ContentValues value : values) {
                        long id = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, value);
                        if (id != -1) {
                            reviewRowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if (reviewRowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return reviewRowsInserted;

            default:
                return super.bulkInsert(uri, values);
        }
    }
}
