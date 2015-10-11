package com.example.fady.mymovies;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Set;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements OnDataFetch{
    private final String LOG_TAG = MainActivity.class.getSimpleName();
    final String ID ="PUT_YOUR_KEY";
    /*get your Id from movies api
 To fetch popular movies, you will use the API from themoviedb.org.
If you don’t already have an account, you will need to create one in order to request an API Key.
In your request for a key, state that your usage will be for educational/non-commercial use.
You will also need to provide some personal information to complete the request. Once you submit your request,
 you should receive your key via email shortly after.
In order to request popular movies you will want to request data from the /discover/movie endpoint. An API Key is required.
Once you obtain your key, you append it to your HTTP request as a URL parameter like so:
http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=[YOUR API KEY]
You will extract the movie id from this request. You will need this in subsequent requests.
     */
    final String Movies_Base_URL = "http://api.themoviedb.org/3/discover/movie?";
    final String YouTube_Base_URL="https://www.youtube.com/watch?v=";
    final String Trailers_Base_URL1="http://api.themoviedb.org/3/movie/";
    int Trailers_id=0;
    final String Trailers_Base_URL2="/videos?";
    final String Review_Base_URL2="/reviews?";
    final String QUERY = "api_key";
    final String sort_by = "sort_by";
    final String popular="popularity.desc";
   final String rated="vote_average.desc";
    final String vote_countQUERY ="vote_count.gte";
    final String vote_countValue ="300";
    final String RequestTag_mostPopular="MostPopular";
    final String RequestTag_HighestRating="HighestRating";
    final TinyDB tinydb = new TinyDB();
    String sort_user_pref=rated;
    private ListAdapter custom_adapter=null;
    private GridView mgrid;
    private int mclickedId;
    ArrayList<MoviePic> MoviesArrayList = new ArrayList<>();
    ArrayList<MoviePic> Fav_MoviesArrayList = new ArrayList<>();
    ArrayList<String> AllTraillers=new ArrayList<>();
    ArrayList<Review> AllReviews=new ArrayList<>();
    private boolean inTwopane;
    public MainActivityFragment() {
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_mainfragment, menu);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ConfigImgLib();
      View v=inflater.inflate(R.layout.fragment_main, container, false);

        mgrid = (GridView) v.findViewById(R.id.gridview);
        tinydb.TinyDBContext(getActivity());
        if (BuildMoviesUrl1() != null) {
             if (savedInstanceState != null) {
                 MoviesArrayList = savedInstanceState.<MoviePic>getParcelableArrayList("my_list");
             }
             else {
                 RatedMoviesExecute();
                 }

                custom_adapter = new ListAdapter(getActivity(), R.layout.movieitem, MoviesArrayList);

                mgrid.setAdapter(custom_adapter);

                mgrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                       Trailers_id=MoviesArrayList.get(position).getId();
                        TrailerExecute();
                        ReviewExecute();
                        mclickedId=position;
                    }
                });
            } else {
                Toast.makeText(getActivity(), "couldn't fetch the data", Toast.LENGTH_SHORT).show();
            }

        return v;
    }


    public void ReviewExecute()
    {
        FetchReviewsTask test=new FetchReviewsTask(getActivity(),Trailers_id);
        test.setOnReviewDataFetchListener(this);
        test.execute(BuildReviewUrl1());

    }
    public void TrailerExecute()
    {

        FetchTrailerTask test=new FetchTrailerTask(getActivity(),Trailers_id);
        test.setOnTrailerDataFetchListener(this);
        test.execute(YouTubeBuildUrl());

    }
    public void PopularMoviesExecute()
    {
        sort_user_pref=popular;
        MoviesArrayList.clear();
        FetchMovieTask test = new FetchMovieTask(getActivity(), RequestTag_mostPopular,getActivity().getResources().getString(R.string.mypicBase));
        test.setOnDataFetchListener(this);
        test.execute(BuildMoviesUrl1());
        mgrid.setAdapter(custom_adapter);
    }
    public void RatedMoviesExecute()
    {
        sort_user_pref=rated;
        MoviesArrayList.clear();
        FetchMovieTask test = new FetchMovieTask(getActivity(),RequestTag_HighestRating, getActivity().getResources().getString(R.string.mypicBase));
        test.setOnDataFetchListener(this);
        test.execute(BuildMoviesUrl1());
        mgrid.setAdapter(custom_adapter);
    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void FavoriteMoviesExecute()
    {
        Fav_MoviesArrayList.clear();
        ImageLoader imageLoader = ImageLoader.getInstance();
        ArrayList<String> tempId=tinydb.getListString("FavListIDs");

        for(int i=0;i<tempId.size();i++) {
            ArrayList<String> movie=tinydb.getListString("FavList" +tempId.get(i));

            for(int k=0;k<movie.size();k++) {
                Log.v(LOG_TAG, "movie list "+movie.get(k));
            }
            MoviePic tempMov=new MoviePic();
            tempMov.setId(Integer.parseInt(movie.get(0)));
            tempMov.setOriginal_title(movie.get(1));
            Bitmap bmp = imageLoader.loadImageSync(movie.get(2));
            tempMov.setMovie_pic(bmp);
            tempMov.setOverview(movie.get(3));
            tempMov.setRelease_date(movie.get(4));
            tempMov.setVote_average(Double.parseDouble(movie.get(5)));
            tempMov.setPosterUrl(movie.get(6));

            Fav_MoviesArrayList.add(tempMov);
        }
        MoviesArrayList.clear();
        if(Fav_MoviesArrayList!=null) {
            for (MoviePic StatuesStr : Fav_MoviesArrayList) {
                custom_adapter.add(StatuesStr);
            }

        }
    }

private void ConfigImgLib()
{
    DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
            .cacheInMemory(true).cacheOnDisk(true)
            .build();
    ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getActivity())
            .defaultDisplayImageOptions(defaultOptions)
            .build();
    ImageLoader.getInstance().init(config); // Do it on Application start
}
    public URL BuildMoviesUrl1()
    {
        Uri buildUri = Uri.parse(Movies_Base_URL).buildUpon()
                .appendQueryParameter(vote_countQUERY, vote_countValue)
                .appendQueryParameter(sort_by, sort_user_pref)
                .appendQueryParameter(QUERY, ID)
                .build();
        try {
            URL url = new URL(buildUri.toString());
            Log.v(LOG_TAG,"rated url is "+url.toString());
            return url;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }

    }
    public URL BuildReviewUrl1()
    {
        String myurl=Trailers_Base_URL1+ Integer.toString(Trailers_id)+Review_Base_URL2;
        Uri buildUri = Uri.parse(myurl).buildUpon()
                .appendQueryParameter(QUERY, ID)
                .build();
        try {
            URL url = new URL(buildUri.toString());
            Log.v(LOG_TAG,"review url is "+url.toString());
            return url;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }

    }
    public URL YouTubeBuildUrl()
    {
        String myurl=Trailers_Base_URL1+ Integer.toString(Trailers_id)+Trailers_Base_URL2;
        Uri buildUri = Uri.parse(myurl).buildUpon()
                .appendQueryParameter(QUERY, ID)
                .build();
        try {
            URL url = new URL(buildUri.toString());
            Log.v(LOG_TAG,url.toString());
            return url;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }

    }
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(int ID,int Trailer_id,String Title,String overview,String ReleaseDate,Double AverageVote,String posterURL,ArrayList<String> allTraillers,ArrayList<Review> allReviews);
    }

    @Override
    public void onDataFetched(ArrayList<?> data,String Tag) {
        ArrayList<MoviePic> movie=(ArrayList<MoviePic>) data;
//        if(Tag==RequestTag_HighestRating)
//        Rated_MoviesArrayList = (ArrayList<MoviePic>) data;
//        else if (Tag==RequestTag_mostPopular)
//            Popular_MoviesArrayList=(ArrayList<MoviePic>) data;

        if(movie!=null) {
            for (MoviePic StatuesStr : movie) {
                custom_adapter.add(StatuesStr);
            }

        }
    }
    @Override
    public void onTrailerDataFetched(ArrayList<?> data) {
        AllTraillers.clear();

        for (int i = 0; i < data.size(); i++) {
            String YouTube_QUERY_Value;
            YouTube_QUERY_Value=YouTube_Base_URL+data.get(i);
            Log.v(LOG_TAG, "youtube " + YouTube_QUERY_Value);
            AllTraillers.add(YouTube_QUERY_Value);
        }



    }
    @Override
    public void onReviewDataFetched(ArrayList<?> data) {
        AllReviews.clear();
        AllReviews= (ArrayList<Review>) data;

        ((Callback) getActivity())
                .onItemSelected(mclickedId,Trailers_id,MoviesArrayList.get(mclickedId).getOriginal_title(),MoviesArrayList.get(mclickedId).getOverview(),
                        MoviesArrayList.get(mclickedId).getRelease_date(),MoviesArrayList.get(mclickedId).getVote_average(),
                        MoviesArrayList.get(mclickedId).getPosterUrl(),AllTraillers,AllReviews);

    }



    @Override
    public void onFetchError(String msg) {

    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putParcelableArrayList("my_list",MoviesArrayList);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement

        if (id == R.id.menuSortPopular) {

            PopularMoviesExecute();
            return true;
        }
        if (id == R.id.menuSortRating) {

            RatedMoviesExecute();
            return true;
        }
        if(id== R.id.menufavorite) {

            FavoriteMoviesExecute();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }
}
