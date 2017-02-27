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

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.popularmovies.data.MovieContract;
import com.example.android.popularmovies.models.Movie;
import com.example.android.popularmovies.models.Review;
import com.example.android.popularmovies.models.Trailer;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.example.android.popularmovies.utilities.TMDbJsonUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

import static com.example.android.popularmovies.DetailActivity.INDEX_MOVIE_FAVORITE;
import static com.example.android.popularmovies.DetailActivity.INDEX_MOVIE_OVERVIEW;
import static com.example.android.popularmovies.DetailActivity.INDEX_MOVIE_POSTER;
import static com.example.android.popularmovies.DetailActivity.INDEX_MOVIE_POSTER_PATH;
import static com.example.android.popularmovies.DetailActivity.INDEX_MOVIE_RELEASE_DATE;
import static com.example.android.popularmovies.DetailActivity.INDEX_MOVIE_RUNTIME;
import static com.example.android.popularmovies.DetailActivity.INDEX_MOVIE_TITLE;
import static com.example.android.popularmovies.DetailActivity.INDEX_MOVIE_VOTE_AVERAGE;
import static com.example.android.popularmovies.DetailActivity.INDEX_REVIEW_AUTHOR;
import static com.example.android.popularmovies.DetailActivity.INDEX_REVIEW_CONTENT;
import static com.example.android.popularmovies.DetailActivity.INDEX_REVIEW_ID;
import static com.example.android.popularmovies.DetailActivity.INDEX_REVIEW_URL;
import static com.example.android.popularmovies.DetailActivity.INDEX_TRAILER_ID;
import static com.example.android.popularmovies.DetailActivity.INDEX_TRAILER_KEY;
import static com.example.android.popularmovies.DetailActivity.INDEX_TRAILER_NAME;
import static com.example.android.popularmovies.DetailActivity.INDEX_TRAILER_SITE;
import static com.example.android.popularmovies.DetailActivity.INDEX_TRAILER_TYPE;
import static com.example.android.popularmovies.DetailActivity.MOVIE_DETAIL_PROJECTION;
import static com.example.android.popularmovies.DetailActivity.REVIEWS_PROJECTION;
import static com.example.android.popularmovies.DetailActivity.TRAILERS_PROJECTION;

/**
 * AsyncTask to download list of movies.
 */
public class MovieDetailsQueryTask extends AsyncTask<Integer, Void, Movie> {

    private static final String LOG_TAG = MovieDetailsQueryTask.class.getSimpleName();

    private AsyncTaskListener<Movie> mListener;

    private Context mContext;

    public MovieDetailsQueryTask(Context context, AsyncTaskListener<Movie> listener) {
        this.mContext = context;
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
     * Retreival of movie data in form of a Movie array.
     *
     * @param id - the movie id.
     * @return Detailed information about a movie, including videos and reviews.
     */
    @Override
    protected Movie doInBackground(Integer... id) {

        int movieID = id[0];
        String movieIDString = String.valueOf(movieID);
        Log.d(LOG_TAG, movieIDString);
        Movie movie = null;

        Uri uri = MovieContract.MovieEntry.CONTENT_URI.buildUpon().
                appendPath(movieIDString).build();
        Cursor movieCursor = this.mContext.getContentResolver().query(uri, MOVIE_DETAIL_PROJECTION,
                "movie_id=?", new String[]{movieIDString}, null);

        if (movieCursor != null && movieCursor.moveToFirst()) {
            movie = new Movie(movieID);
            movie.setTitle(movieCursor.getString(INDEX_MOVIE_TITLE));
            movie.setOverview(movieCursor.getString(INDEX_MOVIE_OVERVIEW));
            movie.setReleaseDate(movieCursor.getString(INDEX_MOVIE_RELEASE_DATE));
            movie.setRuntime(movieCursor.getInt(INDEX_MOVIE_RUNTIME));
            movie.setVoteAverage(movieCursor.getFloat(INDEX_MOVIE_VOTE_AVERAGE));
            movie.setSmallPoster(movieCursor.getBlob(INDEX_MOVIE_POSTER));
            movie.setPosterPath(movieCursor.getString(INDEX_MOVIE_POSTER_PATH));
            movie.setFavorite(movieCursor.getInt(INDEX_MOVIE_FAVORITE));

            //Retrieve trailers
            Uri trailerUri = MovieContract.TrailerEntry.CONTENT_URI.buildUpon().
                    appendPath(movieIDString).build();
            Cursor trailersCursor = this.mContext.getContentResolver().
                    query(trailerUri, TRAILERS_PROJECTION,
                            "movie_id=?", new String[]{movieIDString}, null);

            if (trailersCursor != null) {
                Trailer[] trailerArray = new Trailer[trailersCursor.getCount()];
                while (trailersCursor.moveToNext()) {
                    Trailer trailer = new Trailer(
                            trailersCursor.getString(INDEX_TRAILER_ID),
                            trailersCursor.getString(INDEX_TRAILER_KEY),
                            trailersCursor.getString(INDEX_TRAILER_NAME),
                            trailersCursor.getString(INDEX_TRAILER_SITE),
                            trailersCursor.getString(INDEX_TRAILER_TYPE)
                    );
                    trailerArray[trailersCursor.getPosition()] = trailer;
                }
                movie.setTrailerArray(trailerArray);
                trailersCursor.close();
            }

            //Retrieve reviews
            Uri reviewUri = MovieContract.ReviewEntry.CONTENT_URI.buildUpon().
                    appendPath(movieIDString).build();
            Cursor reviewsCursor = mContext.getContentResolver().query(reviewUri, REVIEWS_PROJECTION,
                    "movie_id=?", new String[]{movieIDString}, null);

            if (reviewsCursor != null) {
                Review[] reviewArray = new Review[reviewsCursor.getCount()];
                while (reviewsCursor.moveToNext()) {
                    Review review = new Review(
                            reviewsCursor.getString(INDEX_REVIEW_ID),
                            reviewsCursor.getString(INDEX_REVIEW_AUTHOR),
                            reviewsCursor.getString(INDEX_REVIEW_CONTENT),
                            reviewsCursor.getString(INDEX_REVIEW_URL)
                    );
                    reviewArray[reviewsCursor.getPosition()] = review;
                }
                movie.setReviewArray(reviewArray);
                reviewsCursor.close();
            }

            movieCursor.close();

            Log.d(LOG_TAG, "Local movie data retrieved");

        } else {
            try {
                URL url = NetworkUtils.buildMovieURL(String.valueOf(movieID));
                String responseStr = NetworkUtils.getResponseFromHttpUrl(url);
                movie = TMDbJsonUtils.getMovieFromJson(responseStr);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            Log.d(LOG_TAG, "Movie data downloaded");
        }

        return movie;
    }

    /**
     * Hide progress bar.
     * Show movie posters if ok, otherwise display error message.
     *
     * @param movie - The result of retrieved movie data (if any).
     */
    @Override
    protected void onPostExecute(Movie movie) {
        super.onPostExecute(movie);
        mListener.onTaskComplete(movie);
    }
}
