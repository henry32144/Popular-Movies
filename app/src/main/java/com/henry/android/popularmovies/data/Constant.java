package com.henry.android.popularmovies.data;

/**
 * Created by Henry on 2017/11/20.
 */

public class Constant {
    public static String BASIC_URL = "https://api.themoviedb.org/3/movie/";
    public static String POPULAR_MOVIE_REQUEST = "popular?api_key=";
    public static String TOP_RATED_REQUEST = "top_rated?api_key=";
    public static String MOVIEID_REQUEST = "?api_key=";
    public static String TRAILER_REQUEST = "/videos";
    public static String REVIEW_REQUEST = "/reviews";
    public static String YOUTUBE_BASE_URI = "https://www.youtube.com/watch?v=";

    public static String API_KEY="71359934909ffbcaf9de68f10d21984b";

    public static String MOVIE_POST="http://image.tmdb.org/t/p/w185/";

    public static final int QUERY_BY_POPULAR = 0;
    public static final int QUERY_BY_TOP_RATE = 1;
    public static final int QUERY_BY_MOVIEID = 2;
    public static final int QUERY_FAVORITE = 3;
    public static final int QUERY_TRAILER = 4;

    public class JSONKeys {
        public static final String RESULT = "results";
        public static final String MOVIEID = "id";
        public static final String MOVIETITLE = "title";
        public static final String MOVIEPOSTER = "poster_path";
        public static final String MOVIEVOTE = "vote_average";
        public static final String MOVIETIME = "runtime";
        public static final String MOVIERELEASEDATE = "release_date";
        public static final String MOVIEOVERVIEW = "overview";
        public static final String MOVIETRAILER = "key";
        public static final String MOVIEREVIEW = "reviews";

        public static final String TRAILER_SITE = "site";
        public static final String VIDEO_TYPE = "type";
    }

    public class ExtraKeys {
        public static final String QueryType = "QueryType";
        public static final String MovieID = "ID";
        public static final String MovieTitle = "Title";
        public static final String MovieVote = "Vote";
        public static final String MoviePoster = "PosterPath";
        public static final String MovieReleaseDate = "Release";
        public static final String MovieOverview = "Overview";
    }
}
