package com.example.fady.mymovies;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by fady on 8/22/2015.
 */
public class MoviePic implements Parcelable {

    public String getPosterUrl() {
        return PosterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        PosterUrl = posterUrl;
    }

    public MoviePic(Bitmap movie_pic, String original_title, String release_date, Double vote_average, String overview, int id, String posterUrl) {
        this.movie_pic = movie_pic;
        this.original_title = original_title;
        this.release_date = release_date;
        this.vote_average = vote_average;
        this.overview = overview;
        this.id = id;

        PosterUrl = posterUrl;
    }

    protected MoviePic(Parcel in) {
        movie_pic = in.readParcelable(Bitmap.class.getClassLoader());
        original_title = in.readString();
        release_date = in.readString();
        overview = in.readString();
        id = in.readInt();
        PosterUrl = in.readString();
    }

    public static final Creator<MoviePic> CREATOR = new Creator<MoviePic>() {
        @Override
        public MoviePic createFromParcel(Parcel in) {
            return new MoviePic(in);
        }

        @Override
        public MoviePic[] newArray(int size) {
            return new MoviePic[size];
        }
    };

    public String getOriginal_title() {
        return original_title;
    }

    public void setOriginal_title(String original_title) {
        this.original_title = original_title;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public Double getVote_average() {
        return vote_average;
    }

    public void setVote_average(Double vote_average) {
        this.vote_average = vote_average;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }




    public Bitmap getMovie_pic() {
        return movie_pic;
    }

    public void setMovie_pic(Bitmap movie_pic) {
        this.movie_pic = movie_pic;
    }



    private Bitmap movie_pic;
    private String original_title;
    private String release_date;
    private Double vote_average;
    private String overview;
    private int id;
    private String PosterUrl;

    public MoviePic() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(movie_pic, flags);
        dest.writeString(original_title);
        dest.writeString(release_date);
        dest.writeString(overview);
        dest.writeInt(id);
        dest.writeString(PosterUrl);
    }
}
