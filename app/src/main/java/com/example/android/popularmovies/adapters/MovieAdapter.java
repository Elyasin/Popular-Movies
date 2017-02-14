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
package com.example.android.popularmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovies.utilities.NetworkUtils;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.models.Movie;
import com.squareup.picasso.Picasso;

/**
 * MovieAdapter holds an array of Movie[] objects.
 * A MovieAdapter object must have a Context and a MovieAdapterOnClickHandler.
 * <p>
 * MovieAdapterOnClickHandler is an inner interface.
 * <p>
 * MovieAdapterViewHolder is an inner class, holding an ImageView for the movie poster. It
 * implements the OnClickListener interface, registers itself to the view as a listener and
 * delegates the onClick call the parent class' MovieAdapterOnClickHandler.
 */

public class MovieAdapter
        extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();

    private Movie[] mMovies;

    private Context mContext;

    private MovieAdapterOnClickHandler mClickHandler;

    /**
     * @param context      - the Activity using the MovieAdapter
     * @param clickHandler - the Activity implementing the MovieAdapterOnClickHandler
     */
    public MovieAdapter(Context context, MovieAdapterOnClickHandler clickHandler) {
        this.mContext = context;
        this.mClickHandler = clickHandler;
    }

    /**
     * Interface for onClick(Movie) method.
     */
    public interface MovieAdapterOnClickHandler {
        void onClick(Movie movie);
    }

    /**
     * ViewHolder for MovieAdapter implementing an OnClickListener.
     * When a movie is clicked on the onClick is delegated to the MovieAdapterOnClickHandler.
     *
     * @see MovieAdapterOnClickHandler
     */
    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView mPoster;

        /**
         * @param view - view holder at which MovieAdapterViewHolder is registered as listener.
         */
        public MovieAdapterViewHolder(View view) {
            super(view);
            mPoster = (ImageView) view.findViewById(R.id.iv_poster);
            view.setOnClickListener(this);
        }

        /**
         * Delegate a click on a movie to the {@link MovieAdapterOnClickHandler}
         *
         * @param view - view holding the selected movie.
         */
        @Override
        public void onClick(View view) {
            Movie movie = mMovies[getAdapterPosition()];
            mClickHandler.onClick(movie);
        }
    }

    /**
     * @param viewGroup - parent's view
     * @param viewType  - type of the new View
     * @return MovieAdapterViewHolder with inflated movie item
     */
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.movie_grid_item, viewGroup, false);
        return new MovieAdapterViewHolder(view);
    }

    /**
     * @param holder   - MovieAdapterViewHolder which has a reference to ImageView that is to be
     *                 updated.
     * @param position - Position of the movie in the Movie[] array.
     */
    @Override
    public void onBindViewHolder(MovieAdapterViewHolder holder, int position) {
        String posterURLString = NetworkUtils.IMDB_IMAGE_BASE_URL + NetworkUtils.IMDB_IMAGE_SIZE + mMovies[position].getPosterPath();
        Log.d(LOG_TAG, posterURLString);
        Picasso.with(mContext).load(posterURLString).into(holder.mPoster);
    }

    /**
     * @return Total number of movies in Movie[] array.
     */
    @Override
    public int getItemCount() {
        if (null == mMovies) return 0;
        return mMovies.length;
    }

    /**
     * Sets new movie data and notifies of data changes.
     *
     * @param movieArray - Movie[] array to be set for this MovieAdapter.
     */
    public void setMovieData(Movie[] movieArray) {
        mMovies = movieArray;
        notifyDataSetChanged();
    }
}
