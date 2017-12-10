package com.henry.android.popularmovies;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.henry.android.popularmovies.data.Constant;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<MovieInfo>> {
    private TextView MovieTitleTv;
    private TextView MovieDateTv;
    private TextView MovieTimeTv;
    private TextView MovieVoteTv;
    private TextView MovieOverViewTv;

    private ImageView MoviePosterIv;

    private int queryType = Constant.QUERY_BY_MOVIEID;
    private static final int MOVIES_LOADER_ID = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();

        Bundle infos = intent.getExtras();

        String MovieTitle = infos.getString(Constant.ExtraKeys.MovieTitle);
        String MovieOverView = infos.getString(Constant.ExtraKeys.MovieOverview);
        String MoviePoster = infos.getString(Constant.ExtraKeys.MoviePoster);
        String MovieReleaseDate = infos.getString(Constant.ExtraKeys.MovieReleaseDate);
        String MovieID = String.valueOf(infos.getInt(Constant.ExtraKeys.MovieID));
        String MovieVote = String.valueOf(infos.getDouble(Constant.ExtraKeys.MovieVote)) +
                                    getString(R.string.movie_vote_score);

        MovieTitleTv = (TextView) findViewById(R.id.detail_movie_title_tv);
        MovieDateTv = (TextView) findViewById(R.id.detail_movie_date_tv);
        MovieTimeTv = (TextView) findViewById(R.id.detail_movie_runtime_tv);
        MovieVoteTv = (TextView) findViewById(R.id.detail_movie_vote_tv);
        MovieOverViewTv = (TextView) findViewById(R.id.detail_movie_overview_tv);
        MoviePosterIv = (ImageView) findViewById(R.id.detail_movie_poster_iv);

        MovieTitleTv.setText(MovieTitle);
        MovieDateTv.setText(MovieReleaseDate);
        MovieVoteTv.setText(MovieVote);
        MovieOverViewTv.setText(MovieOverView);

        String MoviePosterPath = Constant.MOVIE_POST + MoviePoster;

        Picasso.with(this).load(MoviePosterPath).
                placeholder(R.drawable.loading).
                fit().
                into(MoviePosterIv);

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
            bundle.putString(Constant.ExtraKeys.MovieID, MovieID);
            loaderManager.initLoader(MOVIES_LOADER_ID, bundle, this);
        } else {
            // Otherwise, unable to get movie time
            MovieTimeTv.setText(R.string.detail_time_error_message);
        }
    }


    @Override
    public Loader<ArrayList<MovieInfo>> onCreateLoader(int i, Bundle bundle) {
        String movieId = bundle.getString(Constant.ExtraKeys.MovieID);
        return new MovieLoader(this, movieId, queryType);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<MovieInfo>> loader, ArrayList<MovieInfo> movieInfo) {

        if(movieInfo != null && !movieInfo.isEmpty()) {
            String MovieRunTime = String.valueOf(movieInfo.get(0).getMovieRunTime()) + "min";
            MovieTimeTv.setText(MovieRunTime);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<MovieInfo>> loader) {
        MovieTimeTv.setText(R.string.detail_loading);
    }
}
