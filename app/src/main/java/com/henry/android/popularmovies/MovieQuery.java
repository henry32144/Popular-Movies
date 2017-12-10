package com.henry.android.popularmovies;

import android.text.TextUtils;
import android.util.Log;

import com.henry.android.popularmovies.data.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Created by Henry on 2017/11/22.
 */

public class MovieQuery  {
    private static final String LOG_TAG = MovieQuery.class.getSimpleName();

    public MovieQuery() {};


    private static URL buildUrl(String movieID, int QueryType) {
        String urlString = null;
        switch (QueryType) {
            case Constant.QUERY_BY_POPULAR:
                urlString = Constant.BASIC_URL +
                        Constant.POPULAR_MOVIE_REQUEST +
                        Constant.API_KEY;
                break;
            case Constant.QUERY_BY_TOP_RATE:
                urlString = Constant.BASIC_URL +
                        Constant.TOP_RATED_REQUEST +
                        Constant.API_KEY;
                break;
            case Constant.QUERY_BY_MOVIEID:
                urlString = Constant.BASIC_URL +
                        movieID +
                        Constant.MOVIEID_REQUEST +
                        Constant.API_KEY;
                break;
            default:
                urlString = Constant.BASIC_URL +
                        Constant.POPULAR_MOVIE_REQUEST +
                        Constant.API_KEY;
        }
        
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }


    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // return if url is null
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // if request success, continue parse data
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the news JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    //Deal with input stream
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static ArrayList<MovieInfo> extractFeatureFromJson(String movieJSON, int RequestType) {
        // if json is empty, return null
        if (TextUtils.isEmpty(movieJSON)) {
            return null;
        }
        // create empty news array list
        ArrayList<MovieInfo> movieInfos = new ArrayList<>();

        if (RequestType == Constant.QUERY_BY_MOVIEID) {
            try {
                // create json object
                JSONObject baseJsonResponse = new JSONObject(movieJSON);

                String movieTitle = baseJsonResponse.optString(Constant.JSONKeys.MOVIETITLE);

                String moviePosterPath = baseJsonResponse.optString(Constant.JSONKeys.MOVIEPOSTER);

                String movieReleaseDate = baseJsonResponse.optString(Constant.JSONKeys.MOVIERELEASEDATE);

                String movieOverview = baseJsonResponse.optString(Constant.JSONKeys.MOVIEOVERVIEW);

                int movieID = baseJsonResponse.optInt(Constant.JSONKeys.MOVIEID);

                double movieVote = baseJsonResponse.optDouble(Constant.JSONKeys.MOVIEVOTE);

                int runTime = baseJsonResponse.optInt(Constant.JSONKeys.MOVIETIME);
                // create new news object
                MovieInfo movieObj = new MovieInfo(movieTitle, moviePosterPath, movieOverview,
                        movieReleaseDate, movieID, movieVote);

                movieObj.setMovieRunTime(runTime);
                Log.e(LOG_TAG, "MovieRuntime: " + movieID);
                Log.e(LOG_TAG, "MovieRuntime: " + runTime);
                movieInfos.add(movieObj);

            } catch (JSONException e) {
                Log.e(LOG_TAG, "Problem parsing the movie JSON results by ID", e);
            }
        }
        else {
            try {
                // create json object
                JSONObject baseJsonResponse = new JSONObject(movieJSON);

                JSONArray results = baseJsonResponse.getJSONArray(Constant.JSONKeys.RESULT);

                for (int i = 0; i < results.length(); i++) {

                    //get json data from array
                    JSONObject currentMovieInfo = results.getJSONObject(i);

                    String movieTitle = currentMovieInfo.optString(Constant.JSONKeys.MOVIETITLE);

                    String moviePosterPath = currentMovieInfo.optString(Constant.JSONKeys.MOVIEPOSTER);

                    String movieReleaseDate = currentMovieInfo.optString(Constant.JSONKeys.MOVIERELEASEDATE);

                    String movieOverview = currentMovieInfo.optString(Constant.JSONKeys.MOVIEOVERVIEW);

                    int movieID = currentMovieInfo.optInt(Constant.JSONKeys.MOVIEID);

                    double movieVote = currentMovieInfo.optDouble(Constant.JSONKeys.MOVIEVOTE);

                    // create new news object
                    MovieInfo movieObj = new MovieInfo(movieTitle, moviePosterPath, movieOverview,
                            movieReleaseDate, movieID, movieVote);

                    movieInfos.add(movieObj);
                }

            } catch (JSONException e) {
                Log.e(LOG_TAG, "Problem parsing the movies JSON results", e);
            }
        }
        return movieInfos;
    }

    public static ArrayList<MovieInfo> fetchMovieData(String movieID, int RequestType) {

        Log.e(LOG_TAG, movieID + "  " + RequestType);
        //create URL object
        URL url = buildUrl(movieID, RequestType);
        Log.e(LOG_TAG, "URL" + url.toString());
        // send HTTP request to url and parse the data
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        ArrayList<MovieInfo> moveInfoList = extractFeatureFromJson(jsonResponse, RequestType);

        return moveInfoList;
    }
}
