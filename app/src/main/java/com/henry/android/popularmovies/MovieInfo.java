package com.henry.android.popularmovies;

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
}
