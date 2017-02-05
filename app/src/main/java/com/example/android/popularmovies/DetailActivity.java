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
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * {@link DetailActivity} displaying information about a selected movie.
 */
public class DetailActivity extends AppCompatActivity {

    private ImageView mSmallPoster;

    private TextView mTitle;

    private TextView mOverview;

    private TextView mReleaseDate;

    private TextView mVoteAverage;

    /**
     * Sets up the activity with data passed through the Intent.
     *
     * @param savedInstanceState - Bundle of Activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mSmallPoster = (ImageView) findViewById(R.id.iv_small_poster);
        mTitle = (TextView) findViewById(R.id.tv_title);
        mOverview = (TextView) findViewById(R.id.tv_overview);
        mReleaseDate = (TextView) findViewById(R.id.tv_release_date);
        mVoteAverage = (TextView) findViewById(R.id.tv_vote_average);

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(getString(R.string.movie_key))) {
                Movie movie = (Movie) intent.getSerializableExtra(getString(R.string.movie_key));

                String posterURLString = NetworkUtils.IMDB_IMAGE_BASE_URL + NetworkUtils.IMDB_IMAGE_SMALL_SIZE + movie.getPosterPath();
                Picasso.with(this).load(posterURLString).into(mSmallPoster);

                mTitle.setText(movie.getTitle());
                mOverview.setText(movie.getOverview());
                //Show only the year of the release date
                mReleaseDate.setText(movie.getReleaseDate().substring(0, 4));
                mVoteAverage.setText(Double.valueOf(movie.getVoteAverage()).toString() + "/10");

                this.setTitle(movie.getTitle());

            }
        }
    }
}
