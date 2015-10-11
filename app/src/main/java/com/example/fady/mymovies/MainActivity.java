package com.example.fady.mymovies;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

public class MainActivity extends ActionBarActivity implements MainActivityFragment.Callback {
    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    public static boolean mTwoPane;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (findViewById(R.id.weather_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.weather_detail_container, new DetailsActivityFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
                  } else {
                      mTwoPane = false;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement




        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onItemSelected(int ID, int Trailer_id, String Title, String overview, String ReleaseDate, Double AverageVote, String posterURL, ArrayList<String> allTraillers, ArrayList<Review> allReviews) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            args.putInt("mId", ID);
            args.putInt("mTrailer_id", Trailer_id);
            args.putString("mTitle", Title);
            args.putString("mOverview", overview);
            args.putString("mReleaseDate", ReleaseDate);
            args.putDouble("mAverageVote", AverageVote);
            args.putString("mPosterUrl", posterURL);
            args.putStringArrayList("Trailers",allTraillers);
            args.putParcelableArrayList("ReviewArr", allReviews);

            DetailsActivityFragment fragment = new DetailsActivityFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.weather_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            Intent i=new Intent(this,DetailsActivity.class);
            i.putExtra("Intent_mId", ID);
        i.putExtra("Intent_mTrailer_id", Trailer_id);

        i.putExtra("Intent_mTitle",Title);
        i.putExtra("Intent_mOverview",overview);
        i.putExtra("Intent_mReleaseDate",ReleaseDate);
        i.putExtra("Intent_mAverageVote", AverageVote);
        i.putExtra("Intent_mPosterUrl", posterURL);
        i.putStringArrayListExtra("Intent_Trailers", allTraillers);
        i.putParcelableArrayListExtra("Intent_ReviewArr", allReviews);
            startActivity(i);
        }
    }
}
