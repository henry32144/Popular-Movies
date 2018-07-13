package com.henry.android.popularmovies;

import java.util.ArrayList;

/**
 * Created by Henry on 2017/11/21.
 */

class MovieInfo {
    private String MovieTitle;
    private String MoviePosterPath;
    private String MovieOverview;
    private String MovieReleaseDate;
    private int MovieId;
    private double MovieVote;
    private int MovieRunTime;
    private String[] MovieReview;
    private ArrayList<String> MovieTrailer;


    MovieInfo(String movieTitle, String moviePosterPath,
              String movieOverview, String movieReleaseDate,
              int movieId, double movieVote) {
        this.MovieTitle = movieTitle;
        this.MoviePosterPath = moviePosterPath;
        this.MovieOverview = movieOverview;
        this.MovieReleaseDate = movieReleaseDate;
        this.MovieId = movieId;
        this.MovieVote = movieVote;
    }

    MovieInfo(ArrayList<String> Trailer) {
        this.MovieTrailer = Trailer;
    }
    String getMovieTitle() { return MovieTitle; }

    String getMoviePosterPath() {
        return MoviePosterPath;
    }

    String getMovieOverview() {
        return MovieOverview;
    }

    String getMovieReleaseDate() {
        return MovieReleaseDate;
    }

    int getMovieId() {
        return MovieId;
    }

    double getMovieVote() {
        return MovieVote;
    }

    void setMovieRunTime(int MovieRunTime) {
        this.MovieRunTime = MovieRunTime;
    }

    int getMovieRunTime() { return MovieRunTime; }

    public String[] getMovieReview() {
        return MovieReview;
    }

    public ArrayList<String> getMovieTrailer() {
        return MovieTrailer;
    }

    public void setMovieReview(String[] movieReview) {
        MovieReview = movieReview;
    }

    public void setMovieTrailer(ArrayList<String> movieTrailer) {
        MovieTrailer = movieTrailer;
    }
}
