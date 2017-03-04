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
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.popularmovies.data.MovieContract;
import com.example.android.popularmovies.models.Movie;

/**
 * AsyncTask to remove a movie from {@link com.example.android.popularmovies.data.MovieContentProvider}.
 * Params is the movie object to delete.
 * Result is the number of rows deleted, which must be 1.
 */
public class MovieRemoveTask extends AsyncTask<Movie, Void, Integer> {

    private static final String LOG_TAG = MovieRemoveTask.class.getSimpleName();


    private final Context mContext;

    private final AsyncTaskListener<Integer> mListener;

    /**
     * References to activity and listener(, which are usually both the same class).
     *
     * @param context  The activity using this task.
     * @param listener Listener to this task. Triggered before and after task completion.
     */
    public MovieRemoveTask(Context context, AsyncTaskListener<Integer> listener) {
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
     * Deletes a movie from the local storage.
     *
     * @param params Movie object to be deleted (at position 0).
     * @return The number of deleted rows, which must be 1.
     */
    @Override
    protected Integer doInBackground(Movie... params) {

        Movie movie = params[0];
        String movieIDString = String.valueOf(movie.getMovieID());

        Uri uri = MovieContract.MovieEntry.CONTENT_URI.buildUpon().appendPath(movieIDString).build();

        Log.d(LOG_TAG, "Deleting movie " + movieIDString);

        return this.mContext.getContentResolver().delete(uri, null, null);
    }

    /**
     * Delegate to listener.
     *
     * @param rowsDeleted The number of rows deleted (must be 1).
     */
    @Override
    protected void onPostExecute(Integer rowsDeleted) {
        super.onPostExecute(rowsDeleted);
        this.mListener.onTaskComplete(rowsDeleted);
    }
}
