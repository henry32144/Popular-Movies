package com.henry.android.popularmovies;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Henry on 2017/11/22.
 */

public class MovieLoader extends AsyncTaskLoader< ArrayList<MovieInfo> > {

    private String movieID;
    private int queryType;

    public MovieLoader(Context context, int queryType) {
        super(context);
        this.queryType = queryType;
    }

    public MovieLoader(Context context, String movieID, int queryType) {
        super(context);
        this.movieID = movieID;
        this.queryType = queryType;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public ArrayList<MovieInfo> loadInBackground() {
        ArrayList<MovieInfo> movieInfos;

        //Check valid queryType
        if (queryType < 0 || queryType > 4) {
            return null;
        }

        if (movieID == null) {
            movieInfos = MovieQuery.fetchMovieData("",queryType);
        } else {
            movieInfos = MovieQuery.fetchMovieData(movieID,queryType);
        }

        return movieInfos;
    }
}
