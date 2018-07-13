package com.henry.android.popularmovies;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.henry.android.popularmovies.data.Constant;
import com.henry.android.popularmovies.data.MovieContract;

import java.util.ArrayList;

public class FavoriteActivity extends AppCompatActivity implements MovieCursorAdapter.MovieAdapterOnClickListener, MovieCursorAdapter.MovieAdapterOnLongClickListener,
        LoaderManager.LoaderCallbacks<Cursor>{

    private static final int DELETE_ONE = 0;
    private static final int DELETE_ALL = 1;
    private static final int FAVORITE_LOADER_ID = 4;
    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private MovieCursorAdapter mMovieCursorAdapter;

    private TextView mErrorMessageDisplay;
    private Button mToPopularButton;
    private ProgressBar mLoadingIndicator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        mRecyclerView = findViewById(R.id.rv_favorite_movie);
        mErrorMessageDisplay = findViewById(R.id.tv_favorite_error_message);
        mLoadingIndicator = findViewById(R.id.pb_favorite_loading_indicator);
        mToPopularButton = findViewById(R.id.favorite_to_popular);

        mToPopularButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(view.getContext(), MainActivity.class);
                startActivity(it);
            }
        });

        GridLayoutManager layoutManager = new GridLayoutManager(this,2,
                GridLayoutManager.VERTICAL,false);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(false);

        mMovieCursorAdapter = new MovieCursorAdapter(this, new ArrayList<MovieInfo>(), this, this);

        mRecyclerView.setAdapter(mMovieCursorAdapter);

        startLoader(Constant.QUERY_BY_POPULAR);
    }

    private void startLoader(int queryType) {
        mLoadingIndicator.setVisibility(View.VISIBLE);
        LoaderManager loaderManager = getLoaderManager();

        // Initialize the loader. Pass in the int ID constant defined above and pass in null for
        // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
        // because this activity implements the LoaderCallbacks interface).

        if (loaderManager.getLoader(FAVORITE_LOADER_ID) == null) {
                loaderManager.initLoader(FAVORITE_LOADER_ID, null, this);
        } else {
                loaderManager.restartLoader(FAVORITE_LOADER_ID, null, this);
        }

        // Otherwise, display error
        // First, hide loading indicator so error message will be visible
        mLoadingIndicator.setVisibility(View.GONE);

        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] PROJECTION = {
                MovieContract.MovieEntry._ID,
                MovieContract.MovieEntry.COLUMN_MOVIE_ID,
                MovieContract.MovieEntry.COLUMN_MOVIE_TITLE,
                MovieContract.MovieEntry.COLUMN_MOVIE_VOTE,
                MovieContract.MovieEntry.COLUMN_MOVIE_POSTER,
                MovieContract.MovieEntry.COLUMN_MOVIE_TIME,
                MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW,
                MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE
        };
        return new CursorLoader(this, MovieContract.MovieEntry.CONTENT_URI,
                PROJECTION, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mErrorMessageDisplay.setVisibility(View.VISIBLE);

        mLoadingIndicator.setVisibility(View.GONE);
        if(cursor != null) {

            mMovieCursorAdapter.swapCursor(cursor);
        }
        if(cursor.getCount() > 0) {
            mErrorMessageDisplay.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMovieCursorAdapter.swapCursor(null);
    }

    @Override
    public void onClick(View view, MovieInfo movie) {
        Context context = this;
        Class destinationClass = DetailActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        Bundle infos = new Bundle();

        if(movie != null) {
            infos.putString(Constant.ExtraKeys.MovieTitle, movie.getMovieTitle());
            infos.putString(Constant.ExtraKeys.MoviePoster, movie.getMoviePosterPath());
            infos.putString(Constant.ExtraKeys.MovieOverview, movie.getMovieOverview());
            infos.putString(Constant.ExtraKeys.MovieReleaseDate, movie.getMovieReleaseDate());
            infos.putInt(Constant.ExtraKeys.MovieID, movie.getMovieId());
            infos.putDouble(Constant.ExtraKeys.MovieVote, movie.getMovieVote());
        }
        intentToStartDetailActivity.putExtras(infos);
        startActivity(intentToStartDetailActivity);
    }

    @Override
    public void onLongClick(View view, MovieInfo movie, int cursorPosition) {
        int id = movie.getMovieId();

        showDeleteConfirmationDialog(DELETE_ONE, id, cursorPosition);
    }
    private void showDeleteConfirmationDialog(int deleteType, final int movieid, final int cursorPosition) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (deleteType == DELETE_ALL) {
            builder.setMessage(R.string.deleteAll_dialog_msg);
            builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked the "Delete" button, so delete the product.
                    deleteAllMovies();
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked the "Cancel" button, so dismiss the dialog
                    // and continue editing the prodcut.
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            });
        } else {
            builder.setMessage(R.string.delete_dialog_msg);
            builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // Delete one
                    deleteMovie(movieid, cursorPosition);
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            });
        }
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.favorite_menu, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_delete_all) {
            showDeleteConfirmationDialog(DELETE_ALL, 0, 0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteAllMovies() {
        int rowsDeleted = getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI, null, null);
        mMovieCursorAdapter.swapCursor(null);
    }

    private void deleteMovie(int id, int cursorPosition) {
        Uri currentMovieUri = ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI, id);
        int rowsDeleted = getContentResolver().delete(currentMovieUri, null, null);
        mMovieCursorAdapter.notifyItemRemoved(cursorPosition);
        startLoader(FAVORITE_LOADER_ID);
    }


}
