package com.example.fady.mymovies;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class DetailsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        if (savedInstanceState == null) {

            Bundle args = new Bundle();
            args.putInt("mId", getIntent().getIntExtra("Intent_mId", 0));
            args.putInt("mTrailer_id",getIntent().getIntExtra("Intent_mTrailer_id",0));
            args.putString("mTitle",getIntent().getStringExtra("Intent_mTitle"));
            args.putString("mOverview", getIntent().getStringExtra("Intent_mOverview"));
            args.putString("mReleaseDate", getIntent().getStringExtra("Intent_mReleaseDate"));
            args.putDouble("mAverageVote", getIntent().getDoubleExtra("Intent_mAverageVote", 0));
            args.putString("mPosterUrl", getIntent().getStringExtra("Intent_mPosterUrl"));
            args.putStringArrayList("Trailers",getIntent().getStringArrayListExtra("Intent_Trailers"));
            args.putParcelableArrayList("ReviewArr", getIntent().getParcelableArrayListExtra("Intent_ReviewArr"));

            DetailsActivityFragment fragment = new DetailsActivityFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.weather_detail_container, fragment)
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_details, menu);
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
}
