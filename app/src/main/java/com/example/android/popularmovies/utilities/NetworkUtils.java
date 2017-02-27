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

import android.net.Uri;

import com.example.android.popularmovies.BuildConfig;
import com.example.android.popularmovies.data.MovieContract;
import com.example.android.popularmovies.models.Movie;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * These utilities will be used to communicate with the internet.
 * <p>
 * The image URL string is made of the base URL, the image size and the poster string URL of a
 * Movie object.
 * <p>
 * Example for image in Main Activity:  IMDB_IMAGE_BASE_URL + IMDB_IMAGE_SIZE + movie.getPosterPath().
 * <p>
 * The movie data from TMDb is made of the base URL, the sort option (popular or top-rated) and
 * the API key. Use the method @builtUri to build either of the possible query URLs.
 * <p>
 * Use @getResponseFromHttpUrl method to retrieve the movie data base on a URL.
 *
 * @see Movie
 */
public class NetworkUtils {

    private static final String LOG_TAG = NetworkUtils.class.getSimpleName();

    // Image URL components
    public final static String IMDB_IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";
    public final static String IMDB_IMAGE_SIZE = "w185";       // for main activity
    public final static String IMDB_IMAGE_SMALL_SIZE = "w92";  // for detail activity

    //TMDb URL components
    public final static String TMDb_BASE_URL = "http://api.themoviedb.org/3/movie/";
    public final static String TMDb_popular = "popular";
    public final static String TMDb_top_rated = "top_rated";
    //trailers and reviews end point string must be preceded by a movie id
    //to form e.g. http://api.themoviedb.org/3/movie/{id}/videos?api_key=...
    public final static String TMDb_trailers = "videos";
    public final static String TMDb_reviews = "reviews";
    public final static String TMDb_QUERY_API_KEY_PARAM = "api_key";
    public final static String TMDb_QUERY_API_KEY_VALUE = BuildConfig.TMDb_API_KEY;

    //Request videos and reviews together with a movie data
    public final static String TMDb_QUERY_APPEND_TO_RESPONSE_KEY = "append_to_response";
    public final static String TMDb_QUERY_APPEND_TO_RESPONSE_VALUES = "videos,reviews";

    //Youtube base URL
    public final static String YOUTUBE_BASE_URL = "https://www.youtube.com/watch?v=";


    /**
     * Builds the URL used to query TMDb for list of movies.
     *
     * @param rated If True then query top-rated movies otherwise most popular movies.
     * @return The URL used to query the TMDb server.
     */
    public static URL buildMoviesURL(boolean rated) {

        Uri builtUri = Uri.parse(TMDb_BASE_URL);

        if (rated) {
            builtUri = builtUri.buildUpon().
                    appendEncodedPath(TMDb_top_rated).
                    appendQueryParameter(TMDb_QUERY_API_KEY_PARAM, BuildConfig.TMDb_API_KEY).
                    build();
        } else {
            builtUri = builtUri.buildUpon().
                    appendEncodedPath(TMDb_popular).
                    appendQueryParameter(TMDb_QUERY_API_KEY_PARAM, BuildConfig.TMDb_API_KEY).
                    build();
        }


        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static URL buildMovieURL(String movieId) {

        Uri builtUri = Uri.parse(TMDb_BASE_URL);

        builtUri = builtUri.buildUpon().
                appendEncodedPath(movieId).
                appendQueryParameter(TMDb_QUERY_API_KEY_PARAM, TMDb_QUERY_API_KEY_VALUE).
                appendQueryParameter(TMDb_QUERY_APPEND_TO_RESPONSE_KEY, TMDb_QUERY_APPEND_TO_RESPONSE_VALUES).
                build();


        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }


    public static URL buildFavoriteMoviesURL() {

        Uri builtUri = MovieContract.MovieEntry.CONTENT_URI;

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    /**
     * Simple check (ping -c 8.8.8.8) to see if internet is accessible.
     *
     * @return true if internet is accessible, false otherwise
     */
    public static boolean isOnline() {

        Runtime runtime = Runtime.getRuntime();
        try {

            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return false;
    }

}