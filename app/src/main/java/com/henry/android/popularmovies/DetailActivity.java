package com.henry.android.popularmovies;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.henry.android.popularmovies.data.Constant;
import com.henry.android.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity{
    private TextView MovieTitleTv;
    private TextView MovieDateTv;
    private TextView MovieTimeTv;
    private TextView MovieVoteTv;
    private TextView MovieOverViewTv;

    private ImageView MoviePosterIv;

    private Button FavoriteButton;

    private FrameLayout trailer_1;
    private FrameLayout trailer_2;
    private FrameLayout trailer_3;
    private LinearLayout trailer_area;

    private ArrayList<FrameLayout> trailer_blocks = new ArrayList<FrameLayout>();

    private int Runtime;
    private double vote;
    private String posterPath;
    private int movidID;
    private int queryByMovieid = Constant.QUERY_BY_MOVIEID;
    private int queryFavoriteDb = Constant.QUERY_FAVORITE;
    private int queryTrailer = Constant.QUERY_TRAILER;
    private String QUERY_TYPE = "QueryType";
    private Boolean FavoriteButtonMark = false;

    private static final int MOVIES_LOADER_ID = 2;
    private static final int CURSOR_LOADER_ID = 3;
    private static final int TRAILER_LOADER_ID = 4;

    // Fetch data from The Movie Database
    private LoaderManager.LoaderCallbacks<ArrayList<MovieInfo>> httpMovieRuntimeLoader = new LoaderManager.LoaderCallbacks<ArrayList<MovieInfo>>() {
        @Override
        public Loader<ArrayList<MovieInfo>> onCreateLoader(int i, Bundle bundle) {
            String movieId = bundle.getString(Constant.ExtraKeys.MovieID);
            return new MovieLoader(DetailActivity.this, movieId, queryByMovieid);
        }
        @Override
        public void onLoadFinished(Loader<ArrayList<MovieInfo>> loader, ArrayList<MovieInfo> movieInfo) {

            if(movieInfo != null && !movieInfo.isEmpty()) {
                String MovieRunTime = String.valueOf(movieInfo.get(0).getMovieRunTime()) + "min";
                Runtime = movieInfo.get(0).getMovieRunTime();
                MovieTimeTv.setText(MovieRunTime);


                FavoriteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        markFavorite();
                    }
                });
            }
        }
        @Override
        public void onLoaderReset(Loader<ArrayList<MovieInfo>> loader) {
            MovieTimeTv.setText(R.string.detail_loading);
        }
    };

    private LoaderManager.LoaderCallbacks<ArrayList<MovieInfo>> httpMovieTrailerLoader = new LoaderManager.LoaderCallbacks<ArrayList<MovieInfo>>() {
        @Override
        public Loader<ArrayList<MovieInfo>> onCreateLoader(int i, Bundle bundle) {
            Log.e("Detail", "Create Trailer Loader");
            String movieId = bundle.getString(Constant.ExtraKeys.MovieID);
            return new MovieLoader(DetailActivity.this, movieId, queryTrailer);
        }
        @Override
        public void onLoadFinished(Loader<ArrayList<MovieInfo>> loader, ArrayList<MovieInfo> movieInfo) {

            if(movieInfo != null && !movieInfo.isEmpty()) {

                ArrayList<String> movieTrailer = movieInfo.get(0).getMovieTrailer();
                int total_length = movieTrailer.size();
                if (total_length > 0 && total_length <= 3) {

                    for (int i = 0; i < total_length; i++) {
                        String key = movieTrailer.get(i);

                        final String trailerUri = Constant.YOUTUBE_BASE_URI + key;
                        FrameLayout trailerBlock = trailer_blocks.get(i);
                        trailerBlock.setVisibility(View.VISIBLE);
                        trailerBlock.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                Uri uri = Uri.parse(trailerUri);
                                intent.setData(uri);
                                startActivity(intent);
                            }
                        });
                    }
                    trailer_area.setVisibility(View.VISIBLE);
                }
            }
        }
        @Override
        public void onLoaderReset(Loader<ArrayList<MovieInfo>> loader) {
            MovieTimeTv.setText(R.string.detail_loading);
        }
    };

    // Loader Fetch data from SQLite Database
    private LoaderManager.LoaderCallbacks<Cursor> MovieCursorLoader  = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
            int queryType = bundle.getInt(QUERY_TYPE);

            String[] PROJECTION = {
                    MovieContract.MovieEntry._ID,
                    MovieContract.MovieEntry.COLUMN_MOVIE_ID
            };

            String select = "(" +MovieContract.MovieEntry.COLUMN_MOVIE_ID + "=" + movidID + ")";

            return new CursorLoader(DetailActivity.this, MovieContract.MovieEntry.CONTENT_URI,
                    PROJECTION, select, null, null);
        }


        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            if (cursor == null || cursor.getCount() < 1) {
                return;
            }
            // Already set favorite in database
            if (cursor.moveToFirst()) {
                changeFavoriteButton(true);
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
           //Do nothing
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();

        Bundle infos = intent.getExtras();

        vote = infos.getDouble(Constant.ExtraKeys.MovieVote);


        String MovieTitle = infos.getString(Constant.ExtraKeys.MovieTitle);
        String MovieOverView = infos.getString(Constant.ExtraKeys.MovieOverview);
        String MoviePoster = infos.getString(Constant.ExtraKeys.MoviePoster);
        String MovieReleaseDate = infos.getString(Constant.ExtraKeys.MovieReleaseDate);
        String MovieID = String.valueOf(infos.getInt(Constant.ExtraKeys.MovieID));
        String MovieVote = String.valueOf(infos.getDouble(Constant.ExtraKeys.MovieVote)) +
                                    getString(R.string.movie_vote_score);
        posterPath = MoviePoster;
        movidID = infos.getInt(Constant.ExtraKeys.MovieID);

        MovieTitleTv = (TextView) findViewById(R.id.detail_movie_title_tv);
        MovieDateTv = (TextView) findViewById(R.id.detail_movie_date_tv);
        MovieTimeTv = (TextView) findViewById(R.id.detail_movie_runtime_tv);
        MovieVoteTv = (TextView) findViewById(R.id.detail_movie_vote_tv);
        MovieOverViewTv = (TextView) findViewById(R.id.detail_movie_overview_tv);
        MoviePosterIv = (ImageView) findViewById(R.id.detail_movie_poster_iv);

        FavoriteButton = (Button) findViewById(R.id.detail_favorite_button);

        MovieTitleTv.setText(MovieTitle);
        MovieDateTv.setText(MovieReleaseDate);
        MovieVoteTv.setText(MovieVote);
        MovieOverViewTv.setText(MovieOverView);

        trailer_area = findViewById(R.id.trailer_block);
        trailer_1 = findViewById(R.id.trailer_1);
        trailer_2 = findViewById(R.id.trailer_2);
        trailer_3 = findViewById(R.id.trailer_3);
        trailer_blocks.add(trailer_1);
        trailer_blocks.add(trailer_2);
        trailer_blocks.add(trailer_3);

        String MoviePosterPath = Constant.MOVIE_POST + MoviePoster;

        Picasso.with(this).load(MoviePosterPath).
                placeholder(R.drawable.loading).
                fit().
                into(MoviePosterIv);
        startLoader(queryFavoriteDb, "");

        Log.e("Detail", "Before By Movie");
        startLoader(queryByMovieid, MovieID);
        startLoader(queryTrailer, MovieID);
    }


    private void startLoader(int queryType, String MovieID) {
        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        LoaderManager loaderManager = getLoaderManager();
        // Check queryType
        if (queryType == queryByMovieid) {
            // If there is a network connection, fetch data
            if (networkInfo != null && networkInfo.isConnected()) {
                // Get a reference to the LoaderManager, in order to interact with loaders.

                // Initialize the loader. Pass in the int ID constant defined above and pass in movie ID for
                // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
                // because this activity implements the LoaderCallbacks interface).
                Bundle bundle = new Bundle();
                bundle.putString(Constant.ExtraKeys.MovieID, MovieID);
                bundle.putInt(QUERY_TYPE, queryByMovieid);
                loaderManager.initLoader(MOVIES_LOADER_ID, bundle, httpMovieRuntimeLoader);
            } else {
                // Otherwise, unable to get movie time
                MovieTimeTv.setText(R.string.detail_time_error_message);
            }
            // query favorite database
        } else if (queryType == queryTrailer) {
            Bundle bundle = new Bundle();
            bundle.putString(Constant.ExtraKeys.MovieID, MovieID);
            bundle.putInt(QUERY_TYPE, queryTrailer);
            loaderManager.initLoader(TRAILER_LOADER_ID, bundle, httpMovieTrailerLoader);
        } else {
            Bundle bundle = new Bundle();
            bundle.putInt(Constant.ExtraKeys.MovieID, movidID);
            bundle.putInt(QUERY_TYPE, queryFavoriteDb);
            loaderManager.initLoader(CURSOR_LOADER_ID, bundle, MovieCursorLoader);
        }

    }



    private void markFavorite() {
        if (!FavoriteButtonMark) {
            ContentValues values = new ContentValues();

            values.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movidID);
            values.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, MovieTitleTv.getText().toString()); // Text not null
            values.put(MovieContract.MovieEntry.COLUMN_MOVIE_VOTE, vote); // Float
            values.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER, posterPath); // Text
            values.put(MovieContract.MovieEntry.COLUMN_MOVIE_TIME, Runtime); // Int
            values.put(MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW, MovieOverViewTv.getText().toString()); // Text
            values.put(MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE, MovieDateTv.getText().toString()); // Text

            Uri uri = getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, values);
            changeFavoriteButton(true);
        } else {
            Toast.makeText(this, "Already in favorite list", Toast.LENGTH_SHORT).show();
        }
    }

    private void changeFavoriteButton(Boolean setTrue) {
        // Set to True
        if (setTrue) {
            FavoriteButton.setTextColor(getResources().getColor(R.color.textLightGray));
            FavoriteButton.setBackgroundColor(getResources().getColor(R.color.lightGreen));
            FavoriteButton.setText(R.string.detail_Marked);
            FavoriteButtonMark = true;
        } else {
            // Set to False
            FavoriteButton.setTextColor(getResources().getColor(R.color.textWhite));
            FavoriteButton.setBackgroundColor(getResources().getColor(R.color.lightGray));
            FavoriteButton.setText(R.string.detail_favorite_button);
            FavoriteButtonMark = false;
        }
    }

}
