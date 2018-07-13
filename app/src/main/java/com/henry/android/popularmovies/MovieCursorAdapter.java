package com.henry.android.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.henry.android.popularmovies.data.Constant;
import com.henry.android.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Henry on 2017/11/21.
 */

public class MovieCursorAdapter extends RecyclerView.Adapter<MovieCursorAdapter.MovieViewHolder> {

    private Context context;
    private Cursor mCursor;
    private ArrayList<MovieInfo> MovieInfos;

    private MovieAdapterOnClickListener onItemClickListener;
    private MovieAdapterOnLongClickListener onItemLongClickListener;

    public interface MovieAdapterOnClickListener {
        void onClick(View view, MovieInfo movie);
    }
    public interface MovieAdapterOnLongClickListener {
        void onLongClick(View view, MovieInfo movie, int position);
    }

    public MovieCursorAdapter(Context context,  ArrayList<MovieInfo> MovieInfos,  MovieAdapterOnClickListener clickHandler, MovieAdapterOnLongClickListener longClickHandler) {
        this.context = context;
        this.MovieInfos=MovieInfos;

        onItemClickListener = clickHandler;
        onItemLongClickListener =longClickHandler;
    }

    @Override
    public MovieViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.grid_item;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(layoutIdForListItem, parent, false);
        int ItemWidth = getScreenSize(context) / 2;

        view.setMinimumWidth(ItemWidth);
        view.setMinimumHeight((int) parent.getResources().getDimension(R.dimen.grid_item_height));
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        String MovieTitle = mCursor.getString(mCursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE));
        String MovieVote = mCursor.getString(mCursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_MOVIE_VOTE));
        String MoviePostPath = Constant.MOVIE_POST + mCursor.getString(mCursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER));

        holder.MovieTitleTv.setText(MovieTitle);
        holder.MovieVoteTv.setText(MovieVote);
        Picasso.with(context).load(MoviePostPath).
                placeholder(R.drawable.loading).
                fit().
                into(holder.MoviePostIv);
    }

    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        return mCursor.getCount();
    }


    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView MovieTitleTv;
        TextView MovieVoteTv;
        ImageView MoviePostIv;


        public MovieViewHolder(View itemView) {
            super(itemView);
            MovieTitleTv = (TextView) itemView.findViewById(R.id.grid_item_movie_title);
            MovieVoteTv = (TextView) itemView.findViewById(R.id.grid_item_movie_rate);
            MoviePostIv = (ImageView) itemView.findViewById(R.id.grid_item_iv);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);

            final int id = mCursor.getInt(mCursor.getColumnIndexOrThrow(MovieContract.MovieEntry._ID));

            int movieID = mCursor.getInt(mCursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_MOVIE_ID));

            String movieTitle = mCursor.getString(mCursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE));

            String moviePosterPath = mCursor.getString(mCursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER));

            String movieReleaseDate = mCursor.getString(mCursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE));

            String movieOverview = mCursor.getString(mCursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW));

            double movieVote = mCursor.getDouble(mCursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_MOVIE_VOTE));

            // create new news object
            MovieInfo movieObj = new MovieInfo(movieTitle, moviePosterPath, movieOverview,
                    movieReleaseDate, movieID, movieVote);

            onItemClickListener.onClick(view, movieObj);
        }

        @Override
        public boolean onLongClick(View view) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);

            final int id = mCursor.getInt(mCursor.getColumnIndexOrThrow(MovieContract.MovieEntry._ID));

            int movieID = mCursor.getInt(mCursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_MOVIE_ID));

            String movieTitle = mCursor.getString(mCursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE));

            String moviePosterPath = mCursor.getString(mCursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER));

            String movieReleaseDate = mCursor.getString(mCursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE));

            String movieOverview = mCursor.getString(mCursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW));


            double movieVote = mCursor.getDouble(mCursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_MOVIE_VOTE));

            // create new news object
            MovieInfo movieObj = new MovieInfo(movieTitle, moviePosterPath, movieOverview,
                    movieReleaseDate, movieID, movieVote);

            onItemLongClickListener.onLongClick(view, movieObj, adapterPosition);
            return true;
        }
    }

    void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        // After the new Cursor is set, call notifyDataSetChanged
        notifyDataSetChanged();
    }

    public int getScreenSize(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        return width;
    }
}

