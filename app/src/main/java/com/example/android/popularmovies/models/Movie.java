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

package com.example.android.popularmovies.models;

import java.io.Serializable;

/**
 * Represents a movie.
 */
public class Movie implements Serializable {

    private int mMovieID;
    private String mPosterPath;
    private byte[] mW92Poster;
    private byte[] mW185Poster;
    private String mOverview;
    private String mReleaseDate;
    private String mTitle;
    private int mRuntime;
    private double mVoteAverage;
    private Trailer[] mTrailersArray;
    private Review[] mReviewArray;
    private boolean mFavorite;


    /**
     * Movie with posters.
     *
     * @param movieID    The movie id.
     * @param posterPath The relative poster path.
     * @param favorite   True if the movie is in favorites, otherwise false.
     */
    public Movie(int movieID, String posterPath, int favorite) {
        setMovieID(movieID);
        setPosterPath(posterPath);
        setFavorite(favorite);
    }

    /**
     * Movie with details.
     *
     * @param movieID     The movie id.
     * @param posterPath  The relative poster path.
     * @param overview    Summary of the movie.
     * @param releaseDate Release date of the movie.
     * @param title       The title of the movie.
     * @param runtime     The movie's runtime.
     * @param voteAverage The movie's vote average.
     * @param smallPoster Small (W92) poster image of the movie.
     * @param favorite    True if the movie is in favorites, otherwise false.
     */
    public Movie(int movieID, String posterPath, String overview,
                 String releaseDate, String title, int runtime, double voteAverage,
                 byte[] smallPoster, int favorite) {
        setMovieID(movieID);
        setPosterPath(posterPath);
        setOverview(overview);
        setReleaseDate(releaseDate);
        setTitle(title);
        setRuntime(runtime);
        setVoteAverage(voteAverage);
        setW92Poster(smallPoster);
        setFavorite(favorite);
    }

    public byte[] getW92Poster() {
        return mW92Poster;
    }

    public void setW92Poster(byte[] mW92Poster) {
        this.mW92Poster = mW92Poster;
    }

    public byte[] getW185Poster() {
        return mW185Poster;
    }

    public void setW185Poster(byte[] mW185Poster) {
        this.mW185Poster = mW185Poster;
    }

    public Trailer[] getTrailerArray() {
        return mTrailersArray;
    }

    public void setTrailerArray(Trailer[] trailerArray) {
        this.mTrailersArray = trailerArray;
    }

    public Review[] getReviewArray() {
        return mReviewArray;
    }

    public void setReviewArray(Review[] mReviewArray) {
        this.mReviewArray = mReviewArray;
    }

    public int getMovieID() {
        return mMovieID;
    }

    public void setMovieID(int mMovieID) {
        this.mMovieID = mMovieID;
    }

    public String getPosterPath() {
        return mPosterPath;
    }

    public void setPosterPath(String mPosterPath) {
        this.mPosterPath = mPosterPath;
    }

    public byte[] getSmallPoster() {
        return mW92Poster;
    }

    public void setSmallPoster(byte[] mSmallPoster) {
        this.mW92Poster = mSmallPoster;
    }

    public String getOverview() {
        return mOverview;
    }

    public void setOverview(String mOverview) {
        this.mOverview = mOverview;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public void setReleaseDate(String mReleaseDate) {
        this.mReleaseDate = mReleaseDate;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public double getVoteAverage() {
        return mVoteAverage;
    }

    public void setVoteAverage(double mVoteAverage) {
        this.mVoteAverage = mVoteAverage;
    }

    public int getRuntime() {
        return mRuntime;
    }

    public void setRuntime(int runtime) {
        this.mRuntime = runtime;
    }

    public boolean isFavorite() {
        return mFavorite;
    }

    public void setFavorite(int mFavorite) {
        this.mFavorite = (mFavorite == 0 ? false : true);
    }

    public void setFavorite(boolean mFavorite) {
        this.mFavorite = mFavorite;
    }
}
