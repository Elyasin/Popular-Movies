package com.example.android.popularmovies.asyncTasks;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.popularmovies.models.Movie;

import static com.example.android.popularmovies.MainActivity.INDEX_MOVIE_FAVORITE;
import static com.example.android.popularmovies.MainActivity.INDEX_MOVIE_ID;
import static com.example.android.popularmovies.MainActivity.INDEX_MOVIE_POSTER_PATH;
import static com.example.android.popularmovies.MainActivity.MOVIES_PROJECTION;

public class MoviesLocalQueryTask extends AsyncTask<Uri, Void, Movie[]> {

    private static final String LOG_TAG = MoviesLocalQueryTask.class.getSimpleName();

    private AsyncTaskListener<Movie[]> mListener;

    private Context mContext;

    public MoviesLocalQueryTask(Context context, AsyncTaskListener listener) {
        this.mContext = context;
        this.mListener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.mListener.beforeTaskExecution();
    }

    @Override
    protected Movie[] doInBackground(Uri... params) {
        Uri uri = params[0];
        Movie movieArray[] = null;

        Cursor cursor = this.mContext.getContentResolver().query(
                uri, MOVIES_PROJECTION, null, null, null);

        if (cursor != null) {
            movieArray = new Movie[cursor.getCount()];
            while (cursor.moveToNext()) {
                Movie movie = new Movie(Integer.valueOf(cursor.getString(INDEX_MOVIE_ID)));
                movie.setPosterPath(cursor.getString(INDEX_MOVIE_POSTER_PATH));
                movie.setFavorite(cursor.getInt(INDEX_MOVIE_FAVORITE));

                movieArray[cursor.getPosition()] = movie;
            }
            cursor.close();
        }

        Log.d(LOG_TAG, "Local movie data retrieved");

        return movieArray;
    }

    @Override
    protected void onPostExecute(Movie[] movies) {
        super.onPostExecute(movies);
        this.mListener.onTaskComplete(movies);
    }
}
