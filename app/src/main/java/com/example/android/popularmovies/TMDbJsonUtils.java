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

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Utility class to convert Json string into a Movie[] array
 */
public final class TMDbJsonUtils {

    private static final String LOG_TAG = TMDbJsonUtils.class.getSimpleName();


    /**
     * Helper function to transform Json result into Movie[] array.
     *
     * @param jsonString - Json string as returned by the TMDb queries.
     * @return Movie[] array containing all movies from Json string
     * @throws JSONException - Generic Json exception.
     */
    public static Movie[] getMoviesFromJson(String jsonString) throws JSONException {

        final String TMDB_MOVIE_ID = "id";
        final String TMDB_POSTER_PATH = "poster_path";
        final String TMDB_OVERVIEW = "overview";
        final String TMDB_RELEASE_DATE = "release_date";
        final String TMDB_TITLE = "title";
        final String TMDB_VOTE_AVERAGE = "vote_average";

        Movie[] parsedMovies = null;

        JSONObject movieData = new JSONObject(jsonString);
        JSONArray movieArray = movieData.getJSONArray("results");

        Log.v(LOG_TAG, "Number of movies: " + movieArray.length());

        parsedMovies = new Movie[movieArray.length()];

        int movieID;
        String posterPath;
        String overview;
        String releaseDate;
        String title;
        double voteAverage;

        for (int i = 0; i < movieArray.length(); i++) {

            //Get JSON object of movie
            JSONObject movie = movieArray.getJSONObject(i);

            movieID = movie.getInt(TMDB_MOVIE_ID);
            posterPath = movie.getString(TMDB_POSTER_PATH);
            overview = movie.getString(TMDB_OVERVIEW);
            releaseDate = movie.getString(TMDB_RELEASE_DATE);
            title = movie.getString(TMDB_TITLE);
            voteAverage = movie.getDouble(TMDB_VOTE_AVERAGE);

            parsedMovies[i] = new Movie(movieID, posterPath, overview, releaseDate, title, voteAverage);

            Log.v(LOG_TAG, parsedMovies[i].getMovieID() + " " + parsedMovies[i].getTitle() + " - " + parsedMovies[i].getReleaseDate());

        }

        return parsedMovies;
    }
}
