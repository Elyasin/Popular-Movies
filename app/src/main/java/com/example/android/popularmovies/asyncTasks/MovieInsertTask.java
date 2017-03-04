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

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.popularmovies.data.MovieContract;
import com.example.android.popularmovies.models.Movie;
import com.example.android.popularmovies.models.Review;
import com.example.android.popularmovies.models.Trailer;

/**
 * AsyncTask to insert a movie into {@link com.example.android.popularmovies.data.MovieContentProvider}.
 * Params is the movie object to insert.
 * Result is the Uri of the inserted movie.
 */
public class MovieInsertTask extends AsyncTask<Movie, Void, Uri> {

    private static final String LOG_TAG = MovieInsertTask.class.getSimpleName();


    private AsyncTaskListener<Uri> mListener;

    private Context mContext;

    /**
     * References to activity and listener(, which are usually both the same class).
     *
     * @param context  The activity using this task.
     * @param listener Listener to this task. Triggered before and after task completion.
     */
    public MovieInsertTask(Context context, AsyncTaskListener<Uri> listener) {
        this.mContext = context;
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
     * Inserts the movie and its trailers and reviews into the local storage.
     *
     * @param params The movie object to insert (at position 0).
     * @return The movie's Uri after insertion.
     */
    @Override
    protected Uri doInBackground(Movie... params) {

        Movie movie = params[0];

        //Insert movie data
        ContentValues movieContentValues = new ContentValues();

        movieContentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie.getMovieID());
        movieContentValues.put(
                MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW, movie.getOverview());
        movieContentValues.put(
                MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE, movie.getReleaseDate());
        movieContentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, movie.getTitle());
        movieContentValues.put(
                MovieContract.MovieEntry.COLUMN_MOVIE_RUNTIME, movie.getRuntime());
        movieContentValues.put(
                MovieContract.MovieEntry.COLUMN_MOVIE_VOTE_AVERAGE, movie.getVoteAverage());
        movieContentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_W92_POSTER,
                movie.getSmallPoster());
        movieContentValues.put(
                MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH, movie.getPosterPath());
        movieContentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_FAVORITE, true);

        Uri uri = this.mContext.getContentResolver().
                insert(MovieContract.MovieEntry.CONTENT_URI, movieContentValues);

        Log.d(LOG_TAG, "Movie " + movie.getMovieID() + " inserted.");

        //Insert movie trailers
        Trailer[] trailers = movie.getTrailerArray();
        ContentValues[] trailerContentValuesArray = new ContentValues[trailers.length];
        for (int i = 0; i < trailers.length; i++) {
            trailerContentValuesArray[i] = new ContentValues();
            trailerContentValuesArray[i].put(
                    MovieContract.TrailerEntry.COLUMN_TRAILER_ID, trailers[i].getTrailerID());
            trailerContentValuesArray[i].put(
                    MovieContract.TrailerEntry.COLUMN_TRAILER_KEY, trailers[i].getKey());
            trailerContentValuesArray[i].put(
                    MovieContract.TrailerEntry.COLUMN_TRAILER_NAME, trailers[i].getName());
            trailerContentValuesArray[i].put(
                    MovieContract.TrailerEntry.COLUMN_TRAILER_SITE, trailers[i].getSite());
            trailerContentValuesArray[i].put(
                    MovieContract.TrailerEntry.COLUMN_TRAILER_TYPE, trailers[i].getType());
            trailerContentValuesArray[i].put(
                    MovieContract.TrailerEntry.COLUMN_MOVIE_ID, movie.getMovieID());
        }
        int rowsTrailersInserted = this.mContext.getContentResolver().
                bulkInsert(MovieContract.TrailerEntry.CONTENT_URI, trailerContentValuesArray);

        Log.d(LOG_TAG, rowsTrailersInserted + " trailers inserted.");

        //Insert movie reviews
        Review[] reviews = movie.getReviewArray();
        ContentValues[] reviewsContentValuesArray = new ContentValues[reviews.length];

        for (int i = 0; i < reviews.length; i++) {
            reviewsContentValuesArray[i] = new ContentValues();
            reviewsContentValuesArray[i].put(
                    MovieContract.ReviewEntry.COLUMN_REVIEW_ID, reviews[i].getReviewID());
            reviewsContentValuesArray[i].put(
                    MovieContract.ReviewEntry.COLUMN_REVIEW_AUTHOR, reviews[i].getAuthor());
            reviewsContentValuesArray[i].put(
                    MovieContract.ReviewEntry.COLUMN_REVIEW_CONTENT, reviews[i].getContent());
            reviewsContentValuesArray[i].put(
                    MovieContract.ReviewEntry.COLUMN_REVIEW_URL, reviews[i].getUrlString());
            reviewsContentValuesArray[i].put(
                    MovieContract.ReviewEntry.COLUMN_MOVIE_ID, movie.getMovieID());
        }
        int rowsReviewsInserted = this.mContext.getContentResolver().
                bulkInsert(MovieContract.ReviewEntry.CONTENT_URI, reviewsContentValuesArray);

        Log.d(LOG_TAG, rowsReviewsInserted + " reviews inserted.");

        return uri;
    }

    /**
     * Delegate to listener.
     *
     * @param movieUri The Uri of the inserted movie.
     */
    @Override
    protected void onPostExecute(Uri movieUri) {
        super.onPostExecute(movieUri);
        mListener.onTaskComplete(movieUri);
    }
}
