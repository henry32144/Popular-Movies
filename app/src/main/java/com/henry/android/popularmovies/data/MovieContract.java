package com.henry.android.popularmovies.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Henry on 2017/9/14.
 */

public final class MovieContract {

    private MovieContract() {}

    public static final String CONTENT_AUTHORITY = "com.henry.android.popularmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIES = "favorite_movies";

    public static final class MovieEntry implements BaseColumns {


        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;


        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;


        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_MOVIES);

        public final static String TABLE_NAME = "movies";

        public final static String _ID = BaseColumns._ID;

        public final static String COLUMN_MOVIE_ID ="movieid";

        public final static String COLUMN_MOVIE_TITLE ="title";

        public final static String COLUMN_MOVIE_VOTE = "vote";

        public final static String COLUMN_MOVIE_POSTER = "posterpath";

        public static final String COLUMN_MOVIE_TIME = "runtime";

        public final static String COLUMN_MOVIE_RELEASE_DATE = "releasedate";

        public final static String COLUMN_MOVIE_OVERVIEW = "overview";
    }
}
