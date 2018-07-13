package com.henry.android.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.henry.android.popularmovies.MovieDbHelper;

import static com.henry.android.popularmovies.data.MovieContract.PATH_MOVIES;


/**
 * Created by Henry on 2017/9/14.
 */

public class MovieProvider extends ContentProvider {
    /** Tag for the log messages */
    public static final String LOG_TAG = MovieProvider.class.getSimpleName();

    public MovieDbHelper mMovieDbHelper;

//    private static final int NO_PRODUCT_SOLD = 0;
//
    private static final int MOVIES = 100;

    private static final int FAVORITE_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.
        sUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, PATH_MOVIES , MOVIES);
        sUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, PATH_MOVIES + "/#", FAVORITE_ID);
    }
    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        // Make sure the variable is a global variable, so it can be referenced from other
        // ContentProvider methods.
        mMovieDbHelper = new MovieDbHelper(this.getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mMovieDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIES:

                cursor = database.query(MovieContract.MovieEntry.TABLE_NAME,projection,selection,selectionArgs,
                        null,null,sortOrder);
                break;
            case FAVORITE_ID:

                selection = MovieContract.MovieEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                cursor = database.query(MovieContract.MovieEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        // Set notification URI on the Cursor,
        // so we know what content URI the Cursor was created for.
        // If the data at this URI changes, then we know we need to update the Cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIES:

                return MovieContract.MovieEntry.CONTENT_LIST_TYPE;
            case FAVORITE_ID:

                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIES:
                Log.e("Provider insert", "aLL" );
                return insertMovie(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertMovie(Uri uri, ContentValues values) {

        Integer movieid = values.getAsInteger(MovieContract.MovieEntry.COLUMN_MOVIE_ID);

        if (movieid == null) {
            throw new IllegalArgumentException("Requires a ID");
        }

        // Check that the name is not null
        String title = values.getAsString(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE);
        if (title == null) {
            throw new IllegalArgumentException("Requires a title");
        }


        String overview = values.getAsString(MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW);
        if (overview == null) {
            values.put(MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW , "No overview");
        }

        SQLiteDatabase database = mMovieDbHelper.getWritableDatabase();

        long id = database.insert(MovieContract.MovieEntry.TABLE_NAME,null, values);
        if (id == -1) {

            return null;
        }
        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Track the number of rows that were deleted
        int rowsDeleted;

        // Get writable database
        SQLiteDatabase database = mMovieDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIES:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case FAVORITE_ID:
                // Delete a single row given by the ID in the URI
                selection = MovieContract.MovieEntry.COLUMN_MOVIE_ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Return the number of rows deleted
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIES:
                return updateMovie(uri, values, selection, selectionArgs);
            case FAVORITE_ID:
                selection = MovieContract.MovieEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateMovie(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateMovie(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        // check that the name value is not null.
        if (values.containsKey(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE)) {
            String title = values.getAsString(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE);
            if (title == null) {
                throw new IllegalArgumentException("Requires a title");
            }
        }

        if (values.containsKey(MovieContract.MovieEntry.COLUMN_MOVIE_ID)) {
            Integer movieid = values.getAsInteger(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
            if (movieid == null || movieid < 0) {
                throw new IllegalArgumentException("Requires valid movieid");
            }
        }

        if (values.containsKey(MovieContract.MovieEntry.COLUMN_MOVIE_VOTE)) {
            Integer vote = values.getAsInteger(MovieContract.MovieEntry.COLUMN_MOVIE_VOTE);
            if (vote == null) {
                throw new IllegalArgumentException("Requires a vote");
            }
        }

        if (values.containsKey(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER)) {
            String poster = values.getAsString(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER);
            if (poster == null) {
                throw new IllegalArgumentException("Requires a poster path");
            }
        }

        if (values.containsKey(MovieContract.MovieEntry.COLUMN_MOVIE_TIME)) {
            Integer time = values.getAsInteger(MovieContract.MovieEntry.COLUMN_MOVIE_TIME);
            if (time == null) {
                throw new IllegalArgumentException("Requires a time");
            }
        }

        if (values.containsKey(MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW)) {
            String overview = values.getAsString(MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW);
            if (overview == null) {
                throw new IllegalArgumentException("Requires a overview");
            }
        }

        if (values.containsKey(MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE)) {
            String releasedate = values.getAsString(MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE);
            if (releasedate == null) {
                throw new IllegalArgumentException("Requires a releasedate");
            }
        }
        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = mMovieDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(MovieContract.MovieEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Returns the number of database rows affected by the update statement
        return rowsUpdated;
    }
}
