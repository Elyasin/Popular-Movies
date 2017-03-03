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

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.android.popularmovies.adapters.MovieAdapter;
import com.example.android.popularmovies.asyncTasks.AsyncTaskListener;
import com.example.android.popularmovies.asyncTasks.MoviesLocalQueryTask;
import com.example.android.popularmovies.asyncTasks.MoviesQueryTask;
import com.example.android.popularmovies.data.MovieContract;
import com.example.android.popularmovies.models.Movie;
import com.example.android.popularmovies.utilities.NetworkUtils;

import java.net.URL;

/**
 * {@link MainActivity} displaying selectable movie posters in a grid (based on LinearLayout).
 * By default it displays Popular Movies selection.
 */

public class MainActivity extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener,
        MovieAdapter.MovieAdapterOnClickHandler {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();


    // Projection and indices for movies
    public static final String[] MOVIES_PROJECTION = {
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_MOVIE_FAVORITE
    };
    public static final int INDEX_MOVIE_ID = 0;
    public static final int INDEX_MOVIE_POSTER_PATH = 1;
    public static final int INDEX_MOVIE_FAVORITE = 2;


    private MovieAdapter mMovieAdapter;
    private RecyclerView mRecyclerViewMovies;

    private ProgressBar mLoadingIndicator;

    private TextView mErrorMessageDisplay;

    /**
     * Sets up {@link MainActivity} and initially queries database.
     *
     * @param savedInstanceState Bundle of Activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message);
        mErrorMessageDisplay.setText(getString(R.string.no_internet_access));

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        mRecyclerViewMovies = (RecyclerView) findViewById(R.id.rv_movies);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        mRecyclerViewMovies.setHasFixedSize(true);
        mRecyclerViewMovies.setLayoutManager(layoutManager);
        mMovieAdapter = new MovieAdapter(this, this);
        mRecyclerViewMovies.setAdapter(mMovieAdapter);

        //By default show popular movies
        queryMovieDatabase(false);
    }

    /**
     * Constructs the URL (using {@link NetworkUtils}) and fires off
     * an AsyncTask to perform the GET request using our {@link MoviesQueryTask}.
     * If there is no internet connection a message is displayed.
     *
     * @param rated - If true queries with top-rated URL, otherwise popular URL.
     */
    private void queryMovieDatabase(boolean rated) {
        if (NetworkUtils.isOnline()) {
            URL url = NetworkUtils.buildMoviesURL(rated);
            new MoviesQueryTask(this, new MoviesQueryTaskListener()).execute(url);
        } else {
            mRecyclerViewMovies.setVisibility(View.INVISIBLE);
            mErrorMessageDisplay.setVisibility(View.VISIBLE);
        }
    }


    /**
     * Fires off AsyncTask (see {@link MoviesLocalQueryTask}) to query
     * {@link com.example.android.popularmovies.data.MovieContentProvider}
     * for favorite movies.
     */
    private void queryLocalMovieDatabase() {
        Uri uri = MovieContract.MovieEntry.CONTENT_URI;
        new MoviesLocalQueryTask(this, new MoviesLocalQueryTaskListener()).execute(uri);
    }

    /**
     * Queries the movie database for selected item: popular or top-rated or favorites
     * The Favorites are from the local storage
     * ({@link com.example.android.popularmovies.data.MovieContentProvider})
     *
     * @param adapterView The AdapterView were selection happened.
     * @param view        The view that was clicked (within the AdapterView).
     * @param pos         The position of the selected item.
     * @param id          Row id of selected item.
     */
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {

        if (adapterView.getItemAtPosition(pos).equals(getString(R.string.popular))) {
            this.setTitle(getString(R.string.popular));
            queryMovieDatabase(false);

            Log.d(LOG_TAG, "Display popular movies");

        } else if (adapterView.getItemAtPosition(pos).equals(getString(R.string.top_rated))) {
            this.setTitle(getString(R.string.top_rated));
            queryMovieDatabase(true);

            Log.d(LOG_TAG, "Display top-rated movies");

        } else if (adapterView.getItemAtPosition(pos).equals(getString(R.string.favorite))) {
            this.setTitle(getString(R.string.favorite));
            queryLocalMovieDatabase();

            Log.d(LOG_TAG, "Display Favorite movies");

        }

    }

    /**
     * Not implemented.
     *
     * @param adapterView - The AdapterView (does not contain selected item).
     */
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        Log.v(LOG_TAG, "Nothing selected");
    }

    /**
     * Shows details of a selected movie in a new Activity.
     *
     * @param movie The movie that was clicked on.
     */
    @Override
    public void onClick(Movie movie) {
        Context context = MainActivity.this;
        Class destinationActivity = DetailActivity.class;
        Intent startDetailActivity = new Intent(context, destinationActivity);
        startDetailActivity.putExtra(getString(R.string.movie_key), movie.getMovieID());
        startActivity(startDetailActivity);
    }


    /**
     * Add a spinner with three options (popular, top-rated and favorites) to the menu.
     *
     * @param menu - The menu in which our items are placed.
     * @return true to display the menu.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);

        //Find/Create the action bar spinner
        MenuItem item = menu.findItem(R.id.spinner);
        Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);

        //Register MainActivity as listener
        spinner.setOnItemSelectedListener(this);

        //Set the spinner adapter
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_items, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        return true;
    }

    /**
     * Listener executed by onPreExecute and onPostExecute functionality of corresponding
     * AsyncTasks (see {@link MoviesQueryTask} and {@link MoviesLocalQueryTask}).
     * <p>
     * Suitable in order to access activity's members (views, adapter, etc.)
     */
    public class MoviesQueryTaskListener implements AsyncTaskListener<Movie[]> {

        /**
         * Executed in the corresponding onPostExecute method of the AsyncTask.
         * Display result if there is any, otherwise display error message.
         *
         * @param movieArray Array of movies returned from the AsyncTask.
         */
        @Override
        public void onTaskComplete(Movie[] movieArray) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);

            if (movieArray != null) {
                mRecyclerViewMovies.setVisibility(View.VISIBLE);
                mErrorMessageDisplay.setVisibility(View.INVISIBLE);
                mMovieAdapter.setMovieData(movieArray);
            } else {
                mRecyclerViewMovies.setVisibility(View.INVISIBLE);
                mErrorMessageDisplay.setText(getString(R.string.no_internet_access));
                mErrorMessageDisplay.setVisibility(View.VISIBLE);
            }
        }

        /**
         * Executed in the corresponding onPreExecute method of the AsyncTask.
         */
        @Override
        public void beforeTaskExecution() {
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

    }

    /**
     * Listener executed by onPreExecute and onPostExecute functionality of corresponding
     * AsyncTasks (see {@link MoviesQueryTask} and {@link MoviesLocalQueryTask}).
     * <p>
     * Suitable in order to access activity's members (views, adapter, etc.)
     */
    public class MoviesLocalQueryTaskListener implements AsyncTaskListener<Movie[]> {

        /**
         * Executed in the corresponding onPostExecute method of the AsyncTask.
         * Display result if there is any, otherwise display error message.
         *
         * @param movieArray Array of movies returned from the AsyncTask.
         */
        @Override
        public void onTaskComplete(Movie[] movieArray) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);

            if (movieArray != null) {
                mRecyclerViewMovies.setVisibility(View.VISIBLE);
                mErrorMessageDisplay.setVisibility(View.INVISIBLE);
                mMovieAdapter.setMovieData(movieArray);
            } else {
                mRecyclerViewMovies.setVisibility(View.INVISIBLE);
                mErrorMessageDisplay.setText(getString(R.string.no_favorites_in_list));
                mErrorMessageDisplay.setVisibility(View.VISIBLE);
            }
        }

        /**
         * Executed in the corresponding onPreExecute method of the AsyncTask.
         */
        @Override
        public void beforeTaskExecution() {
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

    }

}
