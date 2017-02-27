package com.example.android.popularmovies.asyncTasks;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import com.example.android.popularmovies.data.MovieContract;
import com.example.android.popularmovies.models.Movie;


public class MovieRemoveTask extends AsyncTask<Movie, Void, Integer> {

    private static final String LOG_TAG = MovieRemoveTask.class.getSimpleName();


    private final Context mContext;
    private final AsyncTaskListener<Integer> mListener;

    public MovieRemoveTask(Context context, AsyncTaskListener<Integer> listener) {
        this.mContext = context;
        this.mListener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.mListener.beforeTaskExecution();
    }

    @Override
    protected Integer doInBackground(Movie... params) {

        Movie movie = params[0];
        String movieIDString = String.valueOf(movie.getMovieID());

        Uri uri = MovieContract.MovieEntry.CONTENT_URI.buildUpon().appendPath(movieIDString).build();
        int rowsDeleted = this.mContext.getContentResolver().delete(uri, null, null);

        return rowsDeleted;
    }

    @Override
    protected void onPostExecute(Integer rowsDeleted) {
        super.onPostExecute(rowsDeleted);
        this.mListener.onTaskComplete(rowsDeleted);
    }
}
