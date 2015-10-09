package com.example.fady.mymovies;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by fady on 8/22/2015.
 */
public class FetchMovieTask extends AsyncTask<URL, Void, ArrayList<MoviePic>> {

    public FetchMovieTask(Context mcontext, String requestTag, String mypicBase) {
        this.mcontext = mcontext;
        this.requestTag = requestTag;
        this.mypicBase = mypicBase;
    }

    private String requestTag;
    private Context mcontext;
    final String OWM_statues = "results";
    final String OWM_poster = "poster_path";
    final String OWM_Original_title="original_title";
    private String OWM_release_date="release_date";
    private String OWM_vote_average="vote_average";
    private String OWM_overview="overview";
    final String OWM_id="id";
    final String RequestTag_mostPopular="MostPopular";
    final String RequestTag_HighestRating="HighestRating";
    private String mypicBase;
    private final String LOG_TAG = FetchMovieTask.class.getSimpleName();
    private OnDataFetch onDataFetchListener;

    public void setOnDataFetchListener(OnDataFetch listener){
        onDataFetchListener = listener;
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mcontext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }



    @Override
    protected ArrayList<MoviePic> doInBackground(URL... url) {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String movieJsonStr = null;
            ArrayList<MoviePic> getback = null;
        if(isNetworkAvailable()) {
            try {

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url[0].openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                movieJsonStr = buffer.toString();
                    getback = GetMovies(movieJsonStr);
                        PreferenceManager.getDefaultSharedPreferences(mcontext).edit().putString("MoviesDataResponse"+requestTag, movieJsonStr).commit();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
        }
        else {
            try {
                    String OldResponse = PreferenceManager.getDefaultSharedPreferences(mcontext).getString("MoviesDataResponse"+requestTag, "defaultStringIfNothingFound");
                    getback = GetMovies(OldResponse);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return getback;
    }



    private ArrayList<MoviePic> GetMovies(String respose)
            throws JSONException {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
        .cacheInMemory(true)
                .cacheOnDisk(true)
        .build();

        JSONObject AllPosters = new JSONObject(respose);
        JSONArray PostersJsonarray = AllPosters.getJSONArray(OWM_statues);

        ArrayList<MoviePic> TempMovieList = new ArrayList<MoviePic>();

        for (int i = 0; i < PostersJsonarray.length(); i++) {
            ImageLoader imageLoader = ImageLoader.getInstance();

            JSONObject status = PostersJsonarray.getJSONObject(i);
            String posterURL=status.getString(OWM_poster);
            String title=status.getString(OWM_Original_title);
            String date=status.getString(OWM_release_date);
            String overview=status.getString(OWM_overview);
            Double vote=status.getDouble(OWM_vote_average);
            int id=status.getInt(OWM_id);
            if(posterURL!="null") {
                String mypicURL = mypicBase + posterURL;
                Bitmap bmp = imageLoader.loadImageSync(mypicURL, options);
                MoviePic TempMovie = new MoviePic(bmp,title,date,vote,overview,id,mypicURL);
                TempMovieList.add(TempMovie);
            }
        }

        return TempMovieList;
    }

    @Override
    protected void onPostExecute(ArrayList<MoviePic> posters) {
        super.onPostExecute(posters);
        if(onDataFetchListener!=null) {
            onDataFetchListener.onDataFetched(posters,requestTag);
        }

    }

}

