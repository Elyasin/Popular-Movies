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

package com.example.android.popularmovies.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Represents a movie.
 */
public class Movie implements Parcelable {

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
     * @param w92Poster  Small image bytes (w92).
     * @param w185Poster Big image bytes (w185).
     * @param favorite   True if the movie is in favorites, otherwise false.
     */
    public Movie(int movieID, String posterPath, byte[] w92Poster, byte[] w185Poster, int favorite) {
        setMovieID(movieID);
        setPosterPath(posterPath);
        setW92Poster(w92Poster);
        setW185Poster(w185Poster);
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
     * @param w92Poster   Small (W92) poster image of the movie.
     * @param w185Poster  Big (W185) poster image for the movie.
     * @param favorite    True if the movie is in favorites, otherwise false.
     */
    public Movie(int movieID, String posterPath, String overview,
                 String releaseDate, String title, int runtime, double voteAverage,
                 byte[] w92Poster, byte[] w185Poster, int favorite) {
        setMovieID(movieID);
        setPosterPath(posterPath);
        setOverview(overview);
        setReleaseDate(releaseDate);
        setTitle(title);
        setRuntime(runtime);
        setVoteAverage(voteAverage);
        setW92Poster(w92Poster);
        setW185Poster(w185Poster);
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
        this.mFavorite = (mFavorite != 0);
    }

    public void setFavorite(boolean mFavorite) {
        this.mFavorite = mFavorite;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(getMovieID());
        dest.writeString(getPosterPath());
        dest.writeByteArray(getW92Poster());
        dest.writeByteArray(getW185Poster());
        dest.writeString(getOverview());
        dest.writeString(getReleaseDate());
        dest.writeString(getTitle());
        dest.writeInt(getRuntime());
        dest.writeDouble(getVoteAverage());
        dest.writeInt(isFavorite() ? 1 : 0);
        dest.writeTypedArray(getTrailerArray(), 0);
        dest.writeTypedArray(getReviewArray(), 0);
    }

    public static final Parcelable.Creator<Movie> CREATOR
            = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    private Movie(Parcel in) {
        setMovieID(in.readInt());
        setPosterPath(in.readString());
        setW92Poster(in.createByteArray());
        setW185Poster(in.createByteArray());
        setOverview(in.readString());
        setReleaseDate(in.readString());
        setTitle(in.readString());
        setRuntime(in.readInt());
        setVoteAverage(in.readDouble());
        setFavorite(in.readInt());
        setTrailerArray(in.createTypedArray(Trailer.CREATOR));
        setReviewArray(in.createTypedArray(Review.CREATOR));
    }

}
