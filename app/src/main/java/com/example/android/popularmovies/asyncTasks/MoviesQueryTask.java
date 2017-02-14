/**
 * MIT License
 * <p>
 * Copyright (c) 2017 Elyasin Shaladi
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.example.android.popularmovies.asyncTasks;

import android.os.AsyncTask;
import android.util.Log;

import com.example.android.popularmovies.utilities.NetworkUtils;
import com.example.android.popularmovies.utilities.TMDbJsonUtils;
import com.example.android.popularmovies.models.Movie;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

/**
 * AsyncTask to download list of movies.
 */
public class MoviesQueryTask extends AsyncTask<URL, Void, Movie[]> {

    private static final String LOG_TAG = MoviesQueryTask.class.getSimpleName();

    private AsyncTaskListener<Movie[]> mListener;

    public MoviesQueryTask(AsyncTaskListener<Movie[]> listener) {
        this.mListener = listener;
    }

    /**
     * Show the progress bar.
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mListener.beforeTaskExecution();
    }

    /**
     * Retreival of movie data in form of an Movie[] array.
     *
     * @param urls - Contains one URL at position 0.
     * @return Movie[] array containing all retrieved movies information (only page 1).
     */
    @Override
    protected Movie[] doInBackground(URL... urls) {
        URL url = urls[0];
        Movie movieArray[] = null;

        try {
            String responseStr = NetworkUtils.getResponseFromHttpUrl(url);
            movieArray = TMDbJsonUtils.getMoviesFromJson(responseStr);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        Log.d(LOG_TAG, "Size of Movie array: " + (movieArray != null ? movieArray.length : 0));

        return movieArray;
    }

    /**
     * Hide progress bar.
     * Show movie posters if ok, otherwise display error message.
     *
     * @param movieArray - The result of retrieved movie data (if any).
     */
    @Override
    protected void onPostExecute(Movie[] movieArray) {
        super.onPostExecute(movieArray);
        mListener.onTaskComplete(movieArray);
    }
}