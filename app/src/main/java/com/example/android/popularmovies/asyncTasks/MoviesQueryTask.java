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

import android.os.AsyncTask;
import android.util.Log;

import com.example.android.popularmovies.models.Movie;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.example.android.popularmovies.utilities.TMDbJsonUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

/**
 * AsyncTask to download list of movies.
 * Params is the movies URL.
 * Result is an array of Movie objects.
 */
public class MoviesQueryTask extends AsyncTask<URL, Void, Movie[]> {

    private static final String LOG_TAG = MoviesQueryTask.class.getSimpleName();


    private AsyncTaskListener<Movie[]> mListener;

    /**
     * References to activity and listener(, which are usually both the same class).
     *
     * @param listener Listener to this task. Triggered before and after task completion.
     */
    public MoviesQueryTask(AsyncTaskListener<Movie[]> listener) {
        this.mListener = listener;
    }

    /**
     * Delegate to listener.
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mListener.beforeTaskExecution();
    }

    /**
     * Retrieval of movie data in form of an Movie[] array.
     *
     * @param params Contains the URL at position 0.
     * @return Movie array containing all retrieved movies information (only page 1).
     */
    @Override
    protected Movie[] doInBackground(URL... params) {
        URL url = params[0];
        Movie movieArray[] = null;

        try {
            String responseStr = NetworkUtils.getResponseFromHttpUrl(url);
            movieArray = TMDbJsonUtils.getMoviesFromJson(responseStr);

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        Log.d(LOG_TAG, "Movie data downloaded");

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
        mListener.onTaskComplete(movieArray);
    }
}