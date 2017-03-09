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
 * Represents a movie trailer.
 */
public class Trailer implements Parcelable {

    private String trailerID;
    private String key;
    private String name;
    private String site;
    private String type;

    public Trailer(String trailerID, String key, String name, String type, String site) {
        setTrailerID(trailerID);
        setKey(key);
        setName(name);
        setType(type);
        setSite(site);
    }

    public String getTrailerID() {
        return trailerID;
    }

    public void setTrailerID(String trailerID) {
        this.trailerID = trailerID;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getTrailerID());
        dest.writeString(getKey());
        dest.writeString(getName());
        dest.writeString(getSite());
        dest.writeString(getType());
    }

    public static final Parcelable.Creator<Trailer> CREATOR
            = new Parcelable.Creator<Trailer>() {
        public Trailer createFromParcel(Parcel in) {
            return new Trailer(in);
        }

        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };

    private Trailer(Parcel in) {
        setTrailerID(in.readString());
        setKey(in.readString());
        setName(in.readString());
        setSite(in.readString());
        setType(in.readString());
    }
}
