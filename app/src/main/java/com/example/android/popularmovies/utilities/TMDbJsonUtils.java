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

package com.example.android.popularmovies.utilities;

import com.example.android.popularmovies.models.Movie;
import com.example.android.popularmovies.models.Review;
import com.example.android.popularmovies.models.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Utility class to convert Json strings into a Movie object or array.
 */
public final class TMDbJsonUtils {

    private static final String LOG_TAG = TMDbJsonUtils.class.getSimpleName();


    /**
     * Helper function to transform Json result into Movie[] array.
     *
     * @param jsonString Json string as returned by the TMDb queries.
     * @return Movie[] array containing all movies from Json string
     * @throws JSONException Generic Json exception.
     */
    public static Movie[] getMoviesFromJson(String jsonString) throws JSONException {

        final String TMDB_MOVIE_ID = "id";
        final String TMDB_POSTER_PATH = "poster_path";
        final String TMDB_OVERVIEW = "overview";
        final String TMDB_RELEASE_DATE = "release_date";
        final String TMDB_TITLE = "title";
        final String TMDB_VOTE_AVERAGE = "vote_average";

        Movie[] parsedMovies;

        JSONObject moviesJson = new JSONObject(jsonString);
        JSONArray moviesJsonArray = moviesJson.getJSONArray("results");

        parsedMovies = new Movie[moviesJsonArray.length()];

        int movieID;
        String posterPath;
        String overview;
        String releaseDate;
        String title;
        double voteAverage;

        for (int i = 0; i < moviesJsonArray.length(); i++) {

            //Get JSON object of movie
            JSONObject movie = moviesJsonArray.getJSONObject(i);

            movieID = movie.getInt(TMDB_MOVIE_ID);
            posterPath = movie.getString(TMDB_POSTER_PATH);
            overview = movie.getString(TMDB_OVERVIEW);
            releaseDate = movie.getString(TMDB_RELEASE_DATE);
            title = movie.getString(TMDB_TITLE);
            voteAverage = movie.getDouble(TMDB_VOTE_AVERAGE);

            parsedMovies[i] = new Movie(movieID, posterPath, overview, releaseDate,
                    title, 0, voteAverage, null, 0);
        }

        return parsedMovies;
    }

    /**
     * Helper function to transform Json result into Movie object.
     *
     * @param jsonString Json string as returned by the TMDb queries.
     * @return Movie object containing details, trailers and reviews.
     * @throws JSONException Generic Json exception.
     */
    public static Movie getMovieFromJson(String jsonString) throws JSONException {

        final String TMDB_MOVIE_ID = "id";
        final String TMDB_MOVIE_POSTER_PATH = "poster_path";
        final String TMDB_MOVIE_OVERVIEW = "overview";
        final String TMDB_MOVIE_RELEASE_DATE = "release_date";
        final String TMDB_MOVIE_TITLE = "title";
        final String TMDB_MOVIE_RUNTIME = "runtime";
        final String TMDB_MOVIE_VOTE_AVERAGE = "vote_average";

        final String TMDB_VIDEO_ID = "id";
        final String TMDB_VIDEO_KEY = "key";
        final String TMDB_VIDEO_NAME = "name";
        final String TMDB_VIDEO_SITE = "site";
        final String TMDB_VIDEO_TYPE = "type";

        final String TMDB_REVIEW_ID = "id";
        final String TMDB_REVIEW_AUTHOR = "author";
        final String TMDB_REVIEW_CONTENT = "content";
        final String TMDB_REVIEW_URL = "url";

        Movie movie;

        JSONObject movieJson = new JSONObject(jsonString);

        JSONObject videosJson = movieJson.getJSONObject("videos");
        JSONArray videosJSONArray = videosJson.getJSONArray("results");

        JSONObject reviewsJson = movieJson.getJSONObject("reviews");
        JSONArray reviewJSONArray = reviewsJson.getJSONArray("results");

        int movieID;
        String moviePosterPath;
        String movieOverview;
        String movieReleaseDate;
        String movieTitle;
        double movieVoteAverage;
        int movieRuntime;

        movieID = movieJson.getInt(TMDB_MOVIE_ID);
        moviePosterPath = movieJson.getString(TMDB_MOVIE_POSTER_PATH);
        movieOverview = movieJson.getString(TMDB_MOVIE_OVERVIEW);
        movieReleaseDate = movieJson.getString(TMDB_MOVIE_RELEASE_DATE);
        movieTitle = movieJson.getString(TMDB_MOVIE_TITLE);
        movieVoteAverage = movieJson.getDouble(TMDB_MOVIE_VOTE_AVERAGE);
        movieRuntime = movieJson.getInt(TMDB_MOVIE_RUNTIME);

        movie = new Movie(
                movieID, moviePosterPath, movieOverview, movieReleaseDate,
                movieTitle, movieRuntime, movieVoteAverage, null, 0);

        String trailerID;
        String trailerName;
        String trailerKey;
        String trailerSite;
        String trailerType;

        //Add video (trailer) data to movie object
        Trailer[] trailerArray = new Trailer[videosJSONArray.length()];
        for (int i = 0; i < trailerArray.length; i++) {
            //Get JSON object of video
            JSONObject video = videosJSONArray.getJSONObject(i);

            trailerID = video.getString(TMDB_VIDEO_ID);
            trailerName = video.getString(TMDB_VIDEO_NAME);
            trailerKey = video.getString(TMDB_VIDEO_KEY);
            trailerSite = video.getString(TMDB_VIDEO_SITE);
            trailerType = video.getString(TMDB_VIDEO_TYPE);

            trailerArray[i] = new Trailer(
                    trailerID, trailerKey, trailerName, trailerType, trailerSite
            );
        }
        movie.setTrailerArray(trailerArray);

        //Add reviews to movie object
        Review[] reviewArray = new Review[reviewJSONArray.length()];
        String reviewID, author, content, reviewURL;
        for (int i = 0; i < reviewArray.length; i++) {
            //Get JSON object of review
            JSONObject review = reviewJSONArray.getJSONObject(i);

            reviewID = review.getString(TMDB_REVIEW_ID);
            author = review.getString(TMDB_REVIEW_AUTHOR);
            content = review.getString(TMDB_REVIEW_CONTENT);
            reviewURL = review.getString(TMDB_REVIEW_URL);

            reviewArray[i] = new Review(reviewID, author, content, reviewURL);
        }
        movie.setReviewArray(reviewArray);

        return movie;
    }
}
