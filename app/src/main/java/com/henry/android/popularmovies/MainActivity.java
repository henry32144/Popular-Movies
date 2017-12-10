package com.henry.android.popularmovies;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.henry.android.popularmovies.data.Constant;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickListener,
        LoaderManager.LoaderCallbacks<ArrayList<MovieInfo>> {

    private static final int MOVIES_LOADER_ID = 1;
    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;

    private TextView mErrorMessageDisplay;

    private ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mRecyclerView = findViewById(R.id.recyclerview_movie);
        mErrorMessageDisplay = findViewById(R.id.tv_error_message_display);
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

        GridLayoutManager layoutManager = new GridLayoutManager(this,2,
                GridLayoutManager.VERTICAL,false);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(false);

        mMovieAdapter = new MovieAdapter(this, new ArrayList<MovieInfo>(),this);

        mRecyclerView.setAdapter(mMovieAdapter);

        startLoader(Constant.QUERY_BY_POPULAR);
    }


    private void startLoader(int queryType) {
        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            Bundle bundle = new Bundle();
            bundle.putInt(Constant.ExtraKeys.QueryType,queryType);
            Log.e("Main", "" + queryType);
            if (loaderManager.getLoader(MOVIES_LOADER_ID) == null) {
                loaderManager.initLoader(MOVIES_LOADER_ID, bundle, this);
            } else {
                loaderManager.restartLoader(MOVIES_LOADER_ID, bundle, this);
            }
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            mLoadingIndicator.setVisibility(View.GONE);

            mErrorMessageDisplay.setVisibility(View.VISIBLE);
        }
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
    public Loader<ArrayList<MovieInfo>> onCreateLoader(int i, Bundle bundle) {
        int queryType = bundle.getInt(Constant.ExtraKeys.QueryType);
        return new MovieLoader(this, queryType);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<MovieInfo>> loader, ArrayList<MovieInfo> movieInfos) {
        if(movieInfos != null && !movieInfos.isEmpty()) {
            mMovieAdapter.setMovieData(movieInfos);
        }

        mLoadingIndicator.setVisibility(View.GONE);

    }

    @Override
    public void onLoaderReset(Loader<ArrayList<MovieInfo>> loader) {
        mMovieAdapter.setMovieData(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.main_menu, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_sort_by_popular) {
            startLoader(Constant.QUERY_BY_POPULAR);
            return true;
        }

        if (id == R.id.action_sort_by_rate) {
            startLoader(Constant.QUERY_BY_TOP_RATE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
