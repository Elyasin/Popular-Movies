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
import static com.example.android.popularmovies.MainActivity.INDEX_MOVIE_W185_POSTER;
import static com.example.android.popularmovies.MainActivity.INDEX_MOVIE_W92_POSTER;
import static com.example.android.popularmovies.MainActivity.MOVIES_PROJECTION;

/**
 * AsyncTask to download list of movies from {@link com.example.android.popularmovies.data.MovieContentProvider}.
 * Params is the movies Uri.
 * Result is an array of Movie objects.
 */
public class MoviesLocalQueryTask extends AsyncTask<Uri, Void, Movie[]> {

    private static final String LOG_TAG = MoviesLocalQueryTask.class.getSimpleName();


    private AsyncTaskListener<Movie[]> mListener;

    private Context mContext;

    /**
     * References to activity and listener(, which are usually both the same class).
     *
     * @param context  The activity using this task.
     * @param listener Listener to this task. Triggered before and after task completion.
     */
    public MoviesLocalQueryTask(Context context, AsyncTaskListener<Movie[]> listener) {
        this.mContext = context;
        this.mListener = listener;
    }

    /**
     * Delegate to listener.
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.mListener.beforeTaskExecution();
    }

    /**
     * Retrieval of movie data in form of an Movie[] array.
     *
     * @param params Contains the Uri at position 0.
     * @return Movie array containing all retrieved movies information from local storage.
     */
    @Override
    protected Movie[] doInBackground(Uri... params) {
        Uri uri = params[0];
        Movie movieArray[] = null;

        Cursor cursor = this.mContext.getContentResolver().query(
                uri, MOVIES_PROJECTION, null, null, null);

        try {
            if (cursor != null && !cursor.isClosed()) {
                movieArray = new Movie[cursor.getCount()];
                while (cursor.moveToNext()) {

                    int pos = cursor.getPosition();
                    movieArray[pos] = new Movie(
                            Integer.valueOf(cursor.getString(INDEX_MOVIE_ID)),
                            cursor.getString(INDEX_MOVIE_POSTER_PATH),
                            cursor.getBlob(INDEX_MOVIE_W92_POSTER),
                            cursor.getBlob(INDEX_MOVIE_W185_POSTER),
                            cursor.getInt(INDEX_MOVIE_FAVORITE)
                    );

                    Log.d(LOG_TAG, "Movie created: " +
                            movieArray[pos].getMovieID() + ", " +
                            movieArray[pos].getPosterPath() + ", " +
                            movieArray[pos].getW92Poster().length + ", " +
                            movieArray[pos].getW185Poster().length + ", " +
                            movieArray[pos].isFavorite()
                    );
                }
            }
        } finally {
            cursor.close();
        }

        Log.d(LOG_TAG, "Local movie data retrieved.");

        return movieArray;
    }

    /**
     * Delegate to listener.
     *
     * @param movieArray The movie array.
     */
    @Override
    protected void onPostExecute(Movie[] movieArray) {
        super.onPostExecute(movieArray);
        this.mListener.onTaskComplete(movieArray);
    }
}
