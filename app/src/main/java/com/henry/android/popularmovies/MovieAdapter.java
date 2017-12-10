package com.henry.android.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.henry.android.popularmovies.data.Constant;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Henry on 2017/11/21.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private Context context;
    private ArrayList<MovieInfo> MovieInfos;

    private MovieAdapterOnClickListener onItemClickListener;

    public interface MovieAdapterOnClickListener {
        void onClick(View view, MovieInfo movie);
    }

    public MovieAdapter(Context context, ArrayList<MovieInfo> MovieInfos, MovieAdapterOnClickListener clickHandler) {
        this.context = context;
        this.MovieInfos=MovieInfos;

        onItemClickListener = clickHandler;
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
        MovieInfo movieInfo = MovieInfos.get(position);
        String MovieTitle = movieInfo.getMovieTitle();
        String MovieVote = String.valueOf(movieInfo.getMovieVote());
        String MoviePostPath = Constant.MOVIE_POST + movieInfo.getMoviePosterPath();

        holder.MovieTitleTv.setText(MovieTitle);
        holder.MovieVoteTv.setText(MovieVote);
        Picasso.with(context).load(MoviePostPath).
                placeholder(R.drawable.loading).
                fit().
                into(holder.MoviePostIv);
    }

    @Override
    public int getItemCount() {
        if (MovieInfos == null) return 0;
        return MovieInfos.size();
    }


    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView MovieTitleTv;
        TextView MovieVoteTv;
        ImageView MoviePostIv;


        public MovieViewHolder(View itemView) {
            super(itemView);
            MovieTitleTv = (TextView) itemView.findViewById(R.id.grid_item_movie_title);
            MovieVoteTv = (TextView) itemView.findViewById(R.id.grid_item_movie_rate);
            MoviePostIv = (ImageView) itemView.findViewById(R.id.grid_item_iv);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            MovieInfo movie = MovieInfos.get(adapterPosition);
            onItemClickListener.onClick(view, movie);
        }
    }

    public void setMovieData(ArrayList<MovieInfo> movieInfos) {
        MovieInfos = movieInfos;
        notifyDataSetChanged();
    }

    public int getScreenSize(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        return width;
    }
}

