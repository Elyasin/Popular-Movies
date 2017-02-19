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
package com.example.android.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.android.popularmovies.adapters.ReviewAdapter;
import com.example.android.popularmovies.adapters.TrailerAdapter;
import com.example.android.popularmovies.asyncTasks.AsyncTaskListener;
import com.example.android.popularmovies.asyncTasks.MovieDetailsQueryTask;
import com.example.android.popularmovies.models.Movie;
import com.example.android.popularmovies.models.Review;
import com.example.android.popularmovies.models.Trailer;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.Locale;

/**
 * {@link DetailActivity} displaying information about a selected movie.
 */
public class DetailActivity
        extends AppCompatActivity
        implements TrailerAdapter.TrailerAdapterOnClickHandler,
        ReviewAdapter.ReviewAdapterOnClickHandler {

    private static final String LOG_TAG = DetailActivity.class.getSimpleName();

    private ProgressBar mLoadingIndicator;

    private TextView mErrorMessageDisplay;

    private ScrollView mScrollViewMovieData;

    private ImageView mSmallPoster;
    private TextView mOverview;
    private TextView mReleaseDate;
    private TextView mRuntime;
    private TextView mVoteAverage;

    private RecyclerView mRecyclerViewTrailers;
    private TrailerAdapter mTrailerAdapter;

    private RecyclerView mRecyclerViewReviews;
    private ReviewAdapter mReviewAdapter;

    /**
     * Sets up the activity with data passed through the Intent.
     *
     * @param savedInstanceState - Bundle of Activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message);
        mErrorMessageDisplay.setText(getString(R.string.no_internet_access));

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        mScrollViewMovieData = (ScrollView) findViewById(R.id.sv_movie_data);

        mSmallPoster = (ImageView) findViewById(R.id.iv_small_poster);
        mOverview = (TextView) findViewById(R.id.tv_overview);
        mReleaseDate = (TextView) findViewById(R.id.tv_release_date);
        mRuntime = (TextView) findViewById(R.id.tv_runtime);
        mVoteAverage = (TextView) findViewById(R.id.tv_vote_average);

        //Use RecyclerView for trailers
        mRecyclerViewTrailers = (RecyclerView) findViewById(R.id.rv_trailers);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerViewTrailers.setHasFixedSize(true);
        mRecyclerViewTrailers.setLayoutManager(layoutManager);

        mTrailerAdapter = new TrailerAdapter(this, this);
        mRecyclerViewTrailers.setAdapter(mTrailerAdapter);


        //Use RecyclerView for reviews
        mRecyclerViewReviews = (RecyclerView) findViewById(R.id.rv_reviews);
        LinearLayoutManager layoutManager1 =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerViewReviews.setHasFixedSize(true);
        mRecyclerViewReviews.setLayoutManager(layoutManager1);

        mReviewAdapter = new ReviewAdapter(this, this);
        mRecyclerViewReviews.setAdapter(mReviewAdapter);


        //Retreive movie data from database
        queryMovieDatabase();
    }

    private void queryMovieDatabase() {
        if (NetworkUtils.isOnline()) {
            Intent intent = getIntent();
            if (intent != null) {
                if (intent.hasExtra(getString(R.string.movie_key))) {
                    int movieID = (int) intent.getIntExtra(getString(R.string.movie_key), -1);
                    if (movieID == -1) {
                        //TODO Is that the appropriate exception type?
                        throw new IllegalArgumentException(LOG_TAG + ": movie ID must not be -1.");
                    }
                    URL url = NetworkUtils.buildMovieURL(String.valueOf(movieID));
                    new MovieDetailsQueryTask(new MovieQueryTaskListener()).execute(url);
                } else {
                    //TODO Is that the appropriate exception type?
                    throw new IllegalArgumentException(LOG_TAG + ": No movie ID in Intent.");
                }
            }
        } else {
            mScrollViewMovieData.setVisibility(View.INVISIBLE);
            mErrorMessageDisplay.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onClick(Trailer trailer) {

        Log.d(LOG_TAG, "Start new intent to show trailer " + trailer.getName());

        startActivity(
                new Intent(Intent.ACTION_VIEW,
                        Uri.parse(NetworkUtils.YOUTUBE_BASE_URL + trailer.getKey()))
        );
    }

    @Override
    public void onClick(Review review) {
        Log.d(LOG_TAG, "Clicked on a review item");
    }


    /**
     * Listener executes onPreExecute and onPostExecute functionality of corresponding
     * MovieDetailsQueryTask.
     * <p>
     * Suitable in order to access activity's members (views, adapter, etc.)
     */
    public class MovieQueryTaskListener implements AsyncTaskListener<Movie> {

        @Override
        public void onTaskComplete(Movie movie) {

            mLoadingIndicator.setVisibility(View.INVISIBLE);

            if (movie != null) {
                mScrollViewMovieData.setVisibility(View.VISIBLE);
                mErrorMessageDisplay.setVisibility(View.INVISIBLE);

                //Set movie title for activity
                DetailActivity.this.setTitle(movie.getTitle());

                //Set the poster (small size)
                String posterURLString =
                        NetworkUtils.IMDB_IMAGE_BASE_URL +
                                NetworkUtils.IMDB_IMAGE_SMALL_SIZE +
                                movie.getPosterPath();
                Log.d(LOG_TAG, "Small poster: " + posterURLString);
                Picasso.with(DetailActivity.this).load(posterURLString).into(mSmallPoster);

                //Set the summary
                mOverview.setText(movie.getOverview());

                //Show only the year of the release date
                mReleaseDate.setText(movie.getReleaseDate().substring(0, 4));

                //Set the runtime
                mRuntime.setText(String.format(Locale.US,
                        getString(R.string.runtime), movie.getRuntime())
                );

                //Set the average vote
                mVoteAverage.setText(
                        String.format(
                                Locale.US,
                                getString(R.string.vote_average_by_ten),
                                movie.getVoteAverage())
                );

                //TODO What if movie does not have trailers ?
                mTrailerAdapter.setTrailerArray(movie.getTrailerArray());

                //TODO What if movie does not have reviews ?
                mReviewAdapter.setReviewArray(movie.getmReviewArray());

            } else {
                mScrollViewMovieData.setVisibility(View.INVISIBLE);
                mErrorMessageDisplay.setVisibility(View.VISIBLE);
            }

        }

        @Override
        public void beforeTaskExecution() {
            mScrollViewMovieData.setVisibility(View.INVISIBLE);
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

    }

}
