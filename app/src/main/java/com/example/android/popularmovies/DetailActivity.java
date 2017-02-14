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
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.adapters.TrailerAdapter;
import com.example.android.popularmovies.asyncTasks.AsyncTaskListener;
import com.example.android.popularmovies.asyncTasks.MovieQueryTask;
import com.example.android.popularmovies.models.Movie;
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
        implements TrailerAdapter.TrailerAdapterOnClickHandler {

    private static final String LOG_TAG = DetailActivity.class.getSimpleName();

    private TrailerAdapter mTrailerAdapter;


    /**
     * Sets up the activity with data passed through the Intent.
     *
     * @param savedInstanceState - Bundle of Activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.v(LOG_TAG, "Creating " + this.toString());

        setContentView(R.layout.activity_detail);

        ImageView mSmallPoster = (ImageView) findViewById(R.id.iv_small_poster);
        TextView mTitle = (TextView) findViewById(R.id.tv_title);
        TextView mOverview = (TextView) findViewById(R.id.tv_overview);
        TextView mReleaseDate = (TextView) findViewById(R.id.tv_release_date);
        TextView mVoteAverage = (TextView) findViewById(R.id.tv_vote_average);

        //Use RecyclerView for trailers
        RecyclerView recyclerViewTrailers = (RecyclerView) findViewById(R.id.rv_trailers);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewTrailers.setHasFixedSize(true);
        recyclerViewTrailers.setLayoutManager(layoutManager);
        mTrailerAdapter = new TrailerAdapter(this, this);
        recyclerViewTrailers.setAdapter(mTrailerAdapter);

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(getString(R.string.movie_key))) {
                Movie movie = (Movie) intent.getSerializableExtra(getString(R.string.movie_key));

                String posterURLString =
                        NetworkUtils.IMDB_IMAGE_BASE_URL +
                                NetworkUtils.IMDB_IMAGE_SMALL_SIZE +
                                movie.getPosterPath();
                Log.d(LOG_TAG, "Small poster: " + posterURLString);
                Picasso.with(this).load(posterURLString).into(mSmallPoster);

                mTitle.setText(movie.getTitle());
                mOverview.setText(movie.getOverview());
                //Show only the year of the release date
                mReleaseDate.setText(movie.getReleaseDate().substring(0, 4));
                mVoteAverage.setText(
                        String.format(
                                Locale.US,
                                getString(R.string.vote_average_by_ten),
                                movie.getVoteAverage())
                );

                this.setTitle(movie.getTitle());

                URL url = NetworkUtils.buildMovieURL(String.valueOf(movie.getMovieID()));
                new MovieQueryTask(new MovieQueryTaskListener()).execute(url);
            }
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

    /**
     * Listener executes onPreExecute and onPostExecute functionality of corresponding
     * MoviesQueryTask.
     * <p>
     * Suitable in order to access activity's members (views, adapter, etc.)
     */
    public class MovieQueryTaskListener implements AsyncTaskListener<Movie> {

        @Override
        public void onTaskComplete(Movie movie) {

            Log.d(LOG_TAG, MovieQueryTaskListener.class.getSimpleName() +
                    ": Movie data downloaded for movie + " + movie.getMovieID());

            mTrailerAdapter.setTrailerArray(movie.getTrailerArray());
        }

        @Override
        public void beforeTaskExecution() {

            Log.d(LOG_TAG, MovieQueryTaskListener.class.getSimpleName() +
                    ": About to start movie data download");

        }

    }

}
