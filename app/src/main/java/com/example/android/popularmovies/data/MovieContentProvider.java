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


public class MovieContentProvider extends ContentProvider {

    private static final String LOG_TAG = MovieContentProvider.class.getSimpleName();


    private MovieDBHelper dbHelper;

    public static final int MOVIES = 100;
    public static final int MOVIE_WITH_ID = 101;

    public static final int TRAILERS = 200;
    public static final int TRAILER_WITH_MOVIE_ID = 201;

    public static final int REVIEWS = 300;
    public static final int REVIEW_WITH_MOVIE_ID = 301;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

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

    @Override
    public boolean onCreate() {
        Context context = getContext();
        dbHelper = new MovieDBHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

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

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {

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

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

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

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

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

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {

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
