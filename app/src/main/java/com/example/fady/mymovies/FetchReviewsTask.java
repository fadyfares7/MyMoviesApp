package com.example.fady.mymovies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

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
 * Created by fady on 10/4/2015.
 */
public class FetchReviewsTask extends AsyncTask<URL, Void,ArrayList<Review>> {

    public FetchReviewsTask(Context mcontext, int mRequestID) {
        this.mcontext = mcontext;
        this.mRequestID = mRequestID;
    }

    private Context mcontext;
    private int mRequestID;
    final String OWM_statues = "results";
    final String OWM_author = "author";
    final String OWM_content = "content";
    private final String LOG_TAG = FetchTrailerTask.class.getSimpleName();
    private OnDataFetch onReviewDataFetchListener;

    public void setOnReviewDataFetchListener(OnDataFetch listener){
        onReviewDataFetchListener = listener;
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mcontext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    private ArrayList<Review> GetReviews(String respose)
            throws JSONException {

        JSONObject AllPosters = new JSONObject(respose);
        JSONArray PostersJsonarray = AllPosters.getJSONArray(OWM_statues);
        ArrayList<Review> VideoKey=new ArrayList<>();
        for (int i = 0; i < PostersJsonarray.length(); i++) {
            JSONObject status = PostersJsonarray.getJSONObject(i);
            String author = status.getString(OWM_author);
            String content=status.getString(OWM_content);

            Review temp=new Review(author,content);
            VideoKey.add(temp);
        }
        return VideoKey;
    }

    @Override
    protected void onPostExecute(ArrayList<Review> strings) {
        super.onPostExecute(strings);
        if (onReviewDataFetchListener != null) {
            onReviewDataFetchListener.onReviewDataFetched(strings);
        }
    }
    @Override
    protected ArrayList<Review> doInBackground(URL... url) {

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String movieJsonStr = null;
        ArrayList<Review> getback = null;
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
                Log.v(LOG_TAG, "Review response is " + movieJsonStr);

                PreferenceManager.getDefaultSharedPreferences(mcontext).edit().putString("ReviewDataResponse"+mRequestID, movieJsonStr).commit();
                getback = GetReviews(movieJsonStr);
            }
            catch (IOException e) {
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
                String OldResponse = PreferenceManager.getDefaultSharedPreferences(mcontext).getString("ReviewDataResponse"+mRequestID, "defaultStringIfNothingFound");
                getback = GetReviews(OldResponse);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return getback;
    }
}

