package com.example.android.popularmovies.asyncTasks;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import com.example.android.popularmovies.data.MovieContract;
import com.example.android.popularmovies.models.Movie;
import com.example.android.popularmovies.models.Review;
import com.example.android.popularmovies.models.Trailer;


public class MovieInsertTask extends AsyncTask<Movie, Void, Uri> {

    private static final String LOG_TAG = MovieInsertTask.class.getSimpleName();


    private AsyncTaskListener<Uri> mListener;

    private Context mContext;

    public MovieInsertTask(Context context, AsyncTaskListener<Uri> listener) {
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

        return uri;
    }

    /**
     * Hide progress bar.
     * Show movie posters if ok, otherwise display error message.
     *
     * @param uri - The result of retrieved movie data (if any).
     */
    @Override
    protected void onPostExecute(Uri uri) {
        super.onPostExecute(uri);
        mListener.onTaskComplete(uri);
    }
}
