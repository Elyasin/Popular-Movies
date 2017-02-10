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

import java.net.URL;

/**
 * Main activity displaying selectable movie posters in a grid (based on LinearLayout).
 */

public class MainActivity extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener,
        MovieAdapter.MovieAdapterOnClickHandler {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();


    private MovieAdapter mMovieAdapter;

    private RecyclerView mRecyclerViewMovies;

    private ProgressBar mLoadingIndicator;

    private TextView mErrorMessageDisplay;

    /**
     * Sets up {@link MainActivity} and initially queries database.
     *
     * @param savedInstanceState - Bundle of Activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message);

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
     * This method constructs the URL (using {@link NetworkUtils}) and fires off
     * an AsyncTask to perform the GET request using our {@link MovieQueryTask}
     *
     * @param rated - If true queries with top-rated URL, otherwise popular URL.
     */
    private void queryMovieDatabase(boolean rated) {
        if (NetworkUtils.isOnline()) {
            URL url = NetworkUtils.buildUrl(rated);
            new MovieQueryTask(new MovieQueryTaskListener()).execute(url);
        } else {
            mErrorMessageDisplay.setText(getString(R.string.no_internet_access));
            mRecyclerViewMovies.setVisibility(View.INVISIBLE);
            mErrorMessageDisplay.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Queries the movie database for selected item: popular or top-rated
     *
     * @param adapterView - The AdapterView were selection happened.
     * @param view        - The view that was clicked (within the AdapterView).
     * @param pos         - The position of the selected item.
     * @param id          - Row id of selected item.
     */
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
        Log.v(LOG_TAG, "Item selected: " + adapterView.getItemAtPosition(pos));
        if (adapterView.getItemAtPosition(pos).equals(getString(R.string.popular))) {
            this.setTitle(getString(R.string.popular));
            queryMovieDatabase(false);
        } else if (adapterView.getItemAtPosition(pos).equals(getString(R.string.top_rated))) {
            this.setTitle(getString(R.string.top_rated));
            queryMovieDatabase(true);
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
     * @param movie - The movie that was clicked on.
     */
    @Override
    public void onClick(Movie movie) {
        Context context = MainActivity.this;
        Class destinationActivity = DetailActivity.class;
        Intent startDetailActivity = new Intent(context, destinationActivity);
        startDetailActivity.putExtra(getString(R.string.movie_key), movie);
        startActivity(startDetailActivity);
    }


    /**
     * Add a spinner with two options (popular and top-rated) to the menu.
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
     *
     */
    public class MovieQueryTaskListener implements AsyncTaskListener<Movie[]> {

        @Override
        public void onTaskComplete(Movie[] movieArray) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);

            if (movieArray != null) {
                mRecyclerViewMovies.setVisibility(View.VISIBLE);
                mErrorMessageDisplay.setVisibility(View.INVISIBLE);
                mMovieAdapter.setMovieData(movieArray);
            } else {
                mRecyclerViewMovies.setVisibility(View.INVISIBLE);
                mErrorMessageDisplay.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void beforeTaskExecution() {
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }


    }
}
