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

package com.example.android.popularmovies.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.models.Movie;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

/**
 * MovieAdapter holds an array of Movie objects.
 * A MovieAdapter object must have a Context and a MovieAdapterOnClickHandler.
 */
public class MovieAdapter
        extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();


    private Movie[] mMovies;

    private Context mContext;

    private MovieAdapterOnClickHandler mClickHandler;

    /**
     * Keeps references to context and listener. Listener are informed in onPreExecute and
     * onPostExecute.
     *
     * @param context      The Activity using the MovieAdapter
     * @param clickHandler The Activity implementing the MovieAdapterOnClickHandler
     */
    public MovieAdapter(Context context, MovieAdapterOnClickHandler clickHandler) {
        this.mContext = context;
        this.mClickHandler = clickHandler;
    }

    /**
     * Inflate the movie grid item into the layout.
     *
     * @param viewGroup The parent's view.
     * @param viewType  The type of the new View.
     * @return MovieAdapterViewHolder with inflated movie item
     */
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.movie_grid_item, viewGroup, false);
        return new MovieAdapterViewHolder(view);
    }

    /**
     * Display the poster of the specified position (of the movie array).
     *
     * @param holder   MovieAdapterViewHolder which has a reference to ImageView that is to be
     *                 updated.
     * @param position Position of the movie (in the Movie[] array).
     */
    @Override
    public void onBindViewHolder(MovieAdapterViewHolder holder, int position) {

        String posterURLString =
                NetworkUtils.IMDB_IMAGE_BASE_URL + NetworkUtils.IMDB_IMAGE_W185_SIZE +
                        mMovies[position].getPosterPath();
        boolean isFavorite = mMovies[holder.getAdapterPosition()].isFavorite();

        if (isFavorite) {

            //Image is in database
            byte[] imageBytes = mMovies[position].getW185Poster();
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            holder.mPoster.setImageBitmap(bitmap);

            Log.d(LOG_TAG, "Image loaded from database.");

        } else {

            Picasso.with(mContext)
                    .load(posterURLString)
                    .placeholder(R.drawable.placeholder_185_277)
                    .into(holder.mPoster);

            Log.d(LOG_TAG, "Image loaded from server: " + posterURLString);

        }

    }

    /**
     * Number of movies managed in this adapter.
     *
     * @return Total number of movies in Movie array.
     */
    @Override
    public int getItemCount() {
        if (null == mMovies) return 0;
        return mMovies.length;
    }

    /**
     * Sets new movie data and notifies of data changes.
     *
     * @param newMovieArray Movie array to be set for this MovieAdapter.
     */
    public void setMovieData(Movie[] newMovieArray) {
        mMovies = newMovieArray;
        Log.d(LOG_TAG, "Notifiy about changes in movie adapter.");
        notifyDataSetChanged();
    }

    /**
     * Interface for onClick(Movie, View) method.
     */
    public interface MovieAdapterOnClickHandler {

        /**
         * On click on a movie view item the corresponding movie object can be dealt with here.
         *
         * @param movie The movie object that was clicked on.
         * @param view  The view holding the movie.
         */
        void onClick(Movie movie, View view);
    }

    /**
     * ViewHolder for MovieAdapter implementing an OnClickListener.
     * When a movie is clicked on, the onClick is delegated to the MovieAdapterOnClickHandler.
     *
     * @see MovieAdapterOnClickHandler
     */
    public class MovieAdapterViewHolder
            extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        ImageView mPoster;

        /**
         * Poster reference and register as listener to the view holder.
         *
         * @param itemView - view holder at which MovieAdapterViewHolder is registered as listener.
         */
        public MovieAdapterViewHolder(View itemView) {
            super(itemView);
            mPoster = (ImageView) itemView.findViewById(R.id.iv_w185_poster);
            itemView.setOnClickListener(this);
        }

        /**
         * Delegate a click on a movie to the {@link MovieAdapterOnClickHandler}
         *
         * @param view - view holding the selected movie.
         */
        @Override
        public void onClick(View view) {
            Movie movie = mMovies[getAdapterPosition()];
            mClickHandler.onClick(movie, view);
        }
    }
}
