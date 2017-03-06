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

package com.example.android.popularmovies;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.android.popularmovies.adapters.ReviewAdapter;
import com.example.android.popularmovies.adapters.TrailerAdapter;
import com.example.android.popularmovies.asyncTasks.AsyncTaskListener;
import com.example.android.popularmovies.asyncTasks.MovieDetailsQueryTask;
import com.example.android.popularmovies.asyncTasks.MovieInsertTask;
import com.example.android.popularmovies.asyncTasks.MovieRemoveTask;
import com.example.android.popularmovies.data.MovieContract;
import com.example.android.popularmovies.models.Movie;
import com.example.android.popularmovies.models.Review;
import com.example.android.popularmovies.models.Trailer;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.Locale;

/**
 * {@link DetailActivity} displaying detailed information about a selected movie.
 * A movie can be added to favorites.
 */
public class DetailActivity
        extends AppCompatActivity
        implements
        TrailerAdapter.TrailerAdapterOnClickHandler,
        ReviewAdapter.ReviewAdapterOnClickHandler {

    // Projection and indices for movie details
    public static final String[] MOVIE_DETAIL_PROJECTION = {
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_W92_POSTER,
            MovieContract.MovieEntry.COLUMN_MOVIE_W185_POSTER,
            MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_MOVIE_RUNTIME,
            MovieContract.MovieEntry.COLUMN_MOVIE_TITLE,
            MovieContract.MovieEntry.COLUMN_MOVIE_VOTE_AVERAGE,
            MovieContract.MovieEntry.COLUMN_MOVIE_FAVORITE
    };

    public static final int INDEX_MOVIE_ID = 0;
    public static final int INDEX_MOVIE_W92_POSTER = 1;
    public static final int INDEX_MOVIE_W185_POSTER = 2;
    public static final int INDEX_MOVIE_POSTER_PATH = 3;
    public static final int INDEX_MOVIE_OVERVIEW = 4;
    public static final int INDEX_MOVIE_RELEASE_DATE = 5;
    public static final int INDEX_MOVIE_RUNTIME = 6;
    public static final int INDEX_MOVIE_TITLE = 7;
    public static final int INDEX_MOVIE_VOTE_AVERAGE = 8;
    public static final int INDEX_MOVIE_FAVORITE = 9;

    //Projection and indices for trailers
    public static final String[] TRAILERS_PROJECTION = {
            MovieContract.TrailerEntry.COLUMN_TRAILER_ID,
            MovieContract.TrailerEntry.COLUMN_TRAILER_KEY,
            MovieContract.TrailerEntry.COLUMN_TRAILER_NAME,
            MovieContract.TrailerEntry.COLUMN_TRAILER_SITE,
            MovieContract.TrailerEntry.COLUMN_TRAILER_TYPE
    };

    public static final int INDEX_TRAILER_ID = 0;
    public static final int INDEX_TRAILER_KEY = 1;
    public static final int INDEX_TRAILER_NAME = 2;
    public static final int INDEX_TRAILER_SITE = 3;
    public static final int INDEX_TRAILER_TYPE = 4;

    //Projection and indices for reviews
    public static final String[] REVIEWS_PROJECTION = {
            MovieContract.ReviewEntry.COLUMN_REVIEW_ID,
            MovieContract.ReviewEntry.COLUMN_REVIEW_AUTHOR,
            MovieContract.ReviewEntry.COLUMN_REVIEW_CONTENT,
            MovieContract.ReviewEntry.COLUMN_REVIEW_URL
    };
    public static final int INDEX_REVIEW_ID = 0;
    public static final int INDEX_REVIEW_AUTHOR = 1;
    public static final int INDEX_REVIEW_CONTENT = 2;
    public static final int INDEX_REVIEW_URL = 3;

    private static final String LOG_TAG = DetailActivity.class.getSimpleName();


    //The main top level views
    private ProgressBar mLoadingIndicator;
    private TextView mErrorMessageDisplay;
    private ScrollView mScrollViewMovieData;

    //View details about a movie
    private ImageView mIVw92Poster;
    private TextView mTVOverview;
    private TextView mTVReleaseDate;
    private TextView mTVRuntime;
    private TextView mTVVoteAverage;
    private TextView mTVIsFavorite;

    //Trailers of a movie
    private LinearLayout mLLTrailers;
    private RecyclerView mRVTrailers;
    private TrailerAdapter mTrailerAdapter;
    private TextView mTVTrailersEmptyView;

    //Reviews of a movie (only first page is shown if there is any)
    private LinearLayout mLLReviews;
    private RecyclerView mRVReviews;
    private ReviewAdapter mReviewAdapter;
    private TextView mTVReviewsEmptyView;
    private Movie mMovie;

    /**
     * Sets up the activity with data passed through the Intent.
     *
     * @param savedInstanceState Bundle of Activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);

        //Top-level main views
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message);
        mErrorMessageDisplay.setText(getString(R.string.no_internet_access));
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        mScrollViewMovieData = (ScrollView) findViewById(R.id.sv_movie_data);

        //Views for details
        mIVw92Poster = (ImageView) findViewById(R.id.iv_w92_poster);
        mTVOverview = (TextView) findViewById(R.id.tv_overview);
        mTVReleaseDate = (TextView) findViewById(R.id.tv_release_date);
        mTVRuntime = (TextView) findViewById(R.id.tv_runtime);
        mTVVoteAverage = (TextView) findViewById(R.id.tv_vote_average);
        mTVIsFavorite = (TextView) findViewById(R.id.tv_is_favorite);

        //Use RecyclerView for trailers
        mLLTrailers = (LinearLayout) findViewById(R.id.ll_trailers);
        mRVTrailers = (RecyclerView) findViewById(R.id.rv_trailers);
        mTVTrailersEmptyView = (TextView) findViewById(R.id.tv_empty_trailers_view);
        LinearLayoutManager trailersLayoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRVTrailers.setHasFixedSize(true);
        mRVTrailers.setLayoutManager(trailersLayoutManager);
        mTrailerAdapter = new TrailerAdapter(this);
        mRVTrailers.setAdapter(mTrailerAdapter);


        //Use RecyclerView for reviews
        mLLReviews = (LinearLayout) findViewById(R.id.ll_reviews);
        mRVReviews = (RecyclerView) findViewById(R.id.rv_reviews);
        mTVReviewsEmptyView = (TextView) findViewById(R.id.tv_empty_reviews_view);
        LinearLayoutManager reviewsLayoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRVReviews.setHasFixedSize(true);
        mRVReviews.setLayoutManager(reviewsLayoutManager);
        mReviewAdapter = new ReviewAdapter(this, this);
        mRVReviews.setAdapter(mReviewAdapter);


        //Retreive movie data from database
        queryMovieDatabase();
    }

    /**
     * Loads the movie details using {@link MovieDetailsQueryTask}
     */
    private void queryMovieDatabase() {

        Intent intent = getIntent();
        if (intent != null) {

            //Movie ID must be passed to Intent, otherwise throw exception
            mMovie = (Movie) intent.getSerializableExtra(getString(R.string.movie_key));
            if (mMovie == null) {
                throw new IllegalArgumentException(LOG_TAG + ": Movie must not be null.");
            }
            //Load the data
            new MovieDetailsQueryTask(this, new MovieDetailsQueryTaskListener()).execute(mMovie);
        }

    }

    /**
     * Starts intent with the URL for the trailer.
     *
     * @param trailer The trailer that was clicked on contains the Youtube key.
     */
    @Override
    public void onClick(Trailer trailer) {

        Log.d(LOG_TAG, "Start new intent to show trailer " + trailer.getName());

        startActivity(
                new Intent(Intent.ACTION_VIEW,
                        Uri.parse(NetworkUtils.YOUTUBE_BASE_URL + trailer.getKey()))
        );
    }

    /**
     * When clicked on a review....
     *
     * @param review The review that was clicked on.
     */
    @Override
    public void onClick(Review review) {
        Log.d(LOG_TAG, "Clicked on a review item");
    }


    /**
     * If the movie is in the favorite list, remove it using {@link MovieRemoveTask}.
     * Otherwise add it to favorites using {@link MovieInsertTask}
     *
     * @param view The view that was clicked on
     */
    public void onClickAddOrRemoveFavorite(View view) {

        if (mMovie.isFavorite()) {

            Log.d(LOG_TAG, "Removing movie: " + mMovie.getMovieID());
            new MovieRemoveTask(this, new MovieRemoveTaskListener()).execute(mMovie);

        } else {

            if (mMovie.getW92Poster() == null) {
                //Save the image bytes in movie in case it is added to favorites
                Bitmap bitmap = ((BitmapDrawable) mIVw92Poster.getDrawable()).getBitmap();
                ByteArrayOutputStream binOutStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, binOutStream);
                byte[] imageInByte = binOutStream.toByteArray();
                mMovie.setW92Poster(imageInByte);

                Log.d(LOG_TAG, "W92 poster added to movie. Size is " + binOutStream.size() / 1024 + " KB.");
            }

            //Add movie to database
            Log.d(LOG_TAG, "Adding movie: " + mMovie.getMovieID());
            new MovieInsertTask(this, new MovieInsertTaskListener()).execute(mMovie);
        }

    }


    /**
     * Listener executed by onPreExecute and onPostExecute functionality of corresponding
     * {@link MovieDetailsQueryTask}.
     * <p>
     * Suitable in order to access activity's members (views, adapter, etc.)
     */
    private class MovieDetailsQueryTaskListener implements AsyncTaskListener<Movie> {

        private final String LOG_TAG = MovieDetailsQueryTaskListener.class.getSimpleName();


        /**
         * If movie was retrieved display the details. Otherwise display error message.
         *
         * @param movie The movie that was retrieved.
         */
        @Override
        public void onTaskComplete(Movie movie) {

            mLoadingIndicator.setVisibility(View.INVISIBLE);

            if (movie != null) {
                mScrollViewMovieData.setVisibility(View.VISIBLE);
                mErrorMessageDisplay.setVisibility(View.INVISIBLE);

                //Set movie title for activity
                DetailActivity.this.setTitle(movie.getTitle());

                //If movie is in the favorites, display poster from cache.
                //Otherwise display poster from "internet".
                //Set the favorites button accordingly
                String posterURLString =
                        NetworkUtils.IMDB_IMAGE_BASE_URL +
                                NetworkUtils.IMDB_IMAGE_W92_SIZE +
                                movie.getPosterPath();

                if (movie.isFavorite()) {


                    //Image is in database
                    byte[] imageBytes = movie.getW185Poster();
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    mIVw92Poster.setImageBitmap(bitmap);

                    mTVIsFavorite.setText(getString(R.string.remove_from_favorite));
                    mTVIsFavorite.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.btn_star_big_on, 0, 0, 0);

                    Log.d(LOG_TAG, "Image loaded from database.");

                } else {

                    //get image from image server
                    Picasso.with(DetailActivity.this)
                            .load(posterURLString)
                            .into(mIVw92Poster);

                    mTVIsFavorite.setText(getString(R.string.mark_as_favorite));
                    mTVIsFavorite.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.btn_star_big_off, 0, 0, 0);

                    Log.d(LOG_TAG, "Image loaded from server: " + posterURLString);

                }

                //Set the summary
                mTVOverview.setText(movie.getOverview());

                //Show only the year of the release date
                mTVReleaseDate.setText(movie.getReleaseDate().substring(0, 4));

                //Set the runtime
                mTVRuntime.setText(String.format(Locale.US,
                        getString(R.string.runtime), movie.getRuntime())
                );

                //Set the average vote
                mTVVoteAverage.setText(
                        String.format(
                                Locale.US,
                                getString(R.string.vote_average_by_ten),
                                movie.getVoteAverage())
                );


                mTrailerAdapter.setTrailerArray(movie.getTrailerArray());
                if (mTrailerAdapter.getItemCount() == 0) {
                    mLLTrailers.setVisibility(View.GONE);
                    mTVTrailersEmptyView.setVisibility(View.VISIBLE);
                } else {
                    mLLTrailers.setVisibility(View.VISIBLE);
                    mTVTrailersEmptyView.setVisibility(View.GONE);
                }


                mReviewAdapter.setReviewArray(movie.getReviewArray());
                if (mReviewAdapter.getItemCount() == 0) {
                    mLLReviews.setVisibility(View.GONE);
                    mTVReviewsEmptyView.setVisibility(View.VISIBLE);
                } else {
                    mLLReviews.setVisibility(View.VISIBLE);
                    mTVReviewsEmptyView.setVisibility(View.GONE);
                }

                mMovie = movie;
            } else {
                mScrollViewMovieData.setVisibility(View.INVISIBLE);
                mErrorMessageDisplay.setVisibility(View.VISIBLE);
            }

        }

        /**
         * Executed in the corresponding onPreExecute method of the AsyncTask.
         */
        @Override
        public void beforeTaskExecution() {
            mScrollViewMovieData.setVisibility(View.INVISIBLE);
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

    }

    /**
     * Listener executed by onPreExecute and onPostExecute functionality of corresponding
     * {@link MovieInsertTask}.
     * <p>
     * Suitable in order to access activity's members (views, adapter, etc.)
     */
    private class MovieInsertTaskListener implements AsyncTaskListener<Uri> {

        private final String LOG_TAG = MovieInsertTaskListener.class.getSimpleName();


        /**
         * Executed in the corresponding onPostExecute method of the AsyncTask.
         * Set favorite button accordingly and set movie as favorite
         *
         * @param uri The Uri of the inserted movie
         */
        @Override
        public void onTaskComplete(Uri uri) {
            mTVIsFavorite.setText(getString(R.string.remove_from_favorite));
            mTVIsFavorite.setCompoundDrawablesWithIntrinsicBounds(
                    android.R.drawable.btn_star_big_on, 0, 0, 0
            );
            mMovie.setFavorite(true);
            Log.d(LOG_TAG, "Movie inserted");
        }

        /**
         * Executed in the corresponding onPreExecute method of the AsyncTask.
         */
        @Override
        public void beforeTaskExecution() {
        }
    }

    /**
     * Listener executes onPreExecute and onPostExecute functionality of corresponding
     * {@link MovieRemoveTask}.
     * <p>
     * Suitable in order to access activity's members (views, adapter, etc.)
     */
    private class MovieRemoveTaskListener implements AsyncTaskListener<Integer> {

        private final String LOG_TAG = MovieRemoveTaskListener.class.getSimpleName();


        /**
         * If movie was deleted, set favorite button accordingly, and set movie as non favorite.
         *
         * @param rowsDeleted Number of rows deleted. Must be 1 if deletion took place.
         */
        @Override
        public void onTaskComplete(Integer rowsDeleted) {
            if (rowsDeleted > 0) {
                mTVIsFavorite.setText(getString(R.string.mark_as_favorite));
                mTVIsFavorite.setCompoundDrawablesWithIntrinsicBounds(
                        android.R.drawable.btn_star_big_off, 0, 0, 0
                );
                mMovie.setFavorite(false);
                Log.d(LOG_TAG, "Movie removed");
            } else {

                Log.d(LOG_TAG, "Movie has not been removed");
                throw new UnsupportedOperationException("Movie had not been removed. This is wrong");

            }
        }

        /**
         * Executed in the corresponding onPreExecute method of the AsyncTask.
         */
        @Override
        public void beforeTaskExecution() {
        }
    }

}
