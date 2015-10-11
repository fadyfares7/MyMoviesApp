package com.example.fady.mymovies;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.app.ActionBar.LayoutParams;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailsActivityFragment extends Fragment {
    private final String LOG_TAG = DetailsActivity.class.getSimpleName();
    private int mID;
    private String mTitle;
    private String mReleaseDate;
    private Double mVoteAverage;
    private String mOverView;
    private String mPosterUrl;
    private int mTrailerID;
    private int mFavID;
    private ShareActionProvider mShareActionProvider;
    ArrayList<String> mTrailers=new ArrayList<>();
    ArrayList<Review> AllReviews=new ArrayList<>();
    final TinyDB tinydb = new TinyDB();
    ArrayList<String> favMovieIds=new ArrayList<>();
    public DetailsActivityFragment() {
        setHasOptionsMenu(true);
    }
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_detailsfragment, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // If onLoadFinished happens before this, we can go ahead and set the share intent now.
        if (mTrailers.get(0) != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ImageLoader imageLoader = ImageLoader.getInstance();
        View view= inflater.inflate(R.layout.fragment_details, container, false);

        Bundle arguments = getArguments();
        if (arguments != null) {
            if (savedInstanceState != null) {
                mID = savedInstanceState.getInt("mId", 0);
                mTitle = savedInstanceState.getString("mTitle");
                mReleaseDate = savedInstanceState.getString("mReleaseDate");
                mVoteAverage = savedInstanceState.getDouble("mAverageVote", 0);
                mOverView = savedInstanceState.getString("mOverview");
                mPosterUrl = savedInstanceState.getString("mPosterUrl");
                mTrailerID = savedInstanceState.getInt("mTrailer_id", 0);
                mTrailers = savedInstanceState.getStringArrayList("Trailers");
                AllReviews = savedInstanceState.getParcelableArrayList("ReviewArr");

            }
            else {
                mID = arguments.getInt("mId", 0);
                mTitle = arguments.getString("mTitle");
                mReleaseDate = arguments.getString("mReleaseDate");
                mVoteAverage = arguments.getDouble("mAverageVote", 0);
                mOverView = arguments.getString("mOverview");
                mPosterUrl = arguments.getString("mPosterUrl");
                mTrailerID = arguments.getInt("mTrailer_id", 0);
                mTrailers = arguments.getStringArrayList("Trailers");
                AllReviews = arguments.getParcelableArrayList("ReviewArr");
            }
            tinydb.TinyDBContext(getActivity());
            TextView title = (TextView) view.findViewById(R.id.title);
            title.setText(mTitle);
            TextView date = (TextView) view.findViewById(R.id.data);
            TextView rating = (TextView) view.findViewById(R.id.rate);
            TextView overview = (TextView) view.findViewById(R.id.overview);
            overview.setText(mOverView);
            rating.setText(mVoteAverage.toString() + "/10");
            date.setText(mReleaseDate.substring(0, 4));
            mFavID = PreferenceManager.getDefaultSharedPreferences(getActivity()).getInt("FavID", 0);
            ImageView poster = (ImageView) view.findViewById(R.id.poster);
            Bitmap bmp = imageLoader.loadImageSync(mPosterUrl);
            poster.setScaleType(ImageView.ScaleType.FIT_XY);
            poster.setImageBitmap(bmp);

            favMovieIds = tinydb.getListString("FavListIDs");
            for (int i = 0; i < mTrailers.size(); i++) {
                Log.v(LOG_TAG, mTrailers.get(i));
            }
            final LinearLayout lm = (LinearLayout) view.findViewById(R.id.trailersList);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

            for (int j = 0; j < mTrailers.size(); j++) {
                // Create LinearLayout
                LinearLayout ll = new LinearLayout(getActivity(), null, android.R.attr.listSeparatorTextViewStyle);
                ll.setOrientation(LinearLayout.HORIZONTAL);
                ll.setPadding(0, 16, 0, 16);


                // Create Button
                final Button btn = new Button(getActivity());
                // Give button an ID
                btn.setId(j + 1);
//            btn.setText("Add To Cart");
                btn.setBackground(getActivity().getResources().getDrawable(R.drawable.icon_play));

                // set the layoutParams on the button
                btn.setLayoutParams(new LinearLayout.LayoutParams(
                        LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT));

                final int index = j;
                // Set click listener for button
                btn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        Log.i("TAG", "index :" + index);
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mTrailers.get(index))));
                    }
                });

                //Add button to LinearLayout
                ll.addView(btn);
                //Add button to LinearLayout defined in XML
                lm.addView(ll);

                // Create TextView
                TextView trailerNum = new TextView(getActivity());
                trailerNum.setText("Trailer" + Integer.toString(j + 1));
                trailerNum.setLayoutParams(new LinearLayout.LayoutParams(
                        LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT, 3.0f));
                trailerNum.setTextColor(getActivity().getResources().getColor(R.color.Movies_light_grey));
                trailerNum.setTypeface(Typeface.create("sans-serif-condensed", Typeface.NORMAL));
                trailerNum.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
                ll.addView(trailerNum);

            }
            final LinearLayout Review_lm = (LinearLayout) view.findViewById(R.id.ReviewList);

            for (int j = 0; j < AllReviews.size(); j++) {
                // Create LinearLayout
                LinearLayout ll = new LinearLayout(getActivity(), null, android.R.attr.listSeparatorTextViewStyle);
                ll.setOrientation(LinearLayout.VERTICAL);
                ll.setPadding(0, 16, 0, 16);


                // Create TextView
                TextView author = new TextView(getActivity());
                author.setText("author: " + AllReviews.get(j).getAuthor());
                author.setLayoutParams(new LinearLayout.LayoutParams(
                        LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT));
                author.setTextColor(getActivity().getResources().getColor(R.color.blue));
                author.setTypeface(Typeface.create("sans-serif-condensed", Typeface.NORMAL));
                author.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
                ll.addView(author);

                TextView content = new TextView(getActivity());
                content.setText(AllReviews.get(j).getContent());
                content.setLayoutParams(new LinearLayout.LayoutParams(
                        LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT));
                content.setTextColor(getActivity().getResources().getColor(R.color.Movies_light_grey));
                content.setTypeface(Typeface.create("sans-serif-condensed", Typeface.NORMAL));
                content.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
                ll.addView(content);
                Review_lm.addView(ll);
            }

            final ArrayList<String> set = new ArrayList<>();
            set.add(Integer.toString(mTrailerID));
            set.add(mTitle);
            set.add(mPosterUrl);
            set.add(mOverView);
            set.add(mReleaseDate);
            set.add(Double.toString(mVoteAverage));
            set.add(mPosterUrl);

            final ToggleButton toggle = (ToggleButton) view.findViewById(R.id.favorite);
            int star = PreferenceManager.getDefaultSharedPreferences(getActivity()).getInt("FavStar" + mTrailerID, 0);
            if (star == 1)
                toggle.setChecked(true);
            else
                toggle.setChecked(false);


            toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int x;
                    if (isChecked) {
                        // The toggle is enabled
                        Toast.makeText(getActivity(), "This movie is added to favorite", Toast.LENGTH_SHORT).show();

                        PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putInt("FavStar" + mTrailerID, 1).commit();//saving last state for Star button
                        tinydb.putListString("FavList" + mTrailerID, set);
                        favMovieIds.add(Integer.toString(mTrailerID));
                        for (int k = 0; k < favMovieIds.size(); k++) {
                            Log.v(LOG_TAG, "favList contains: " + favMovieIds.get(k));
                        }
                        tinydb.putListString("FavListIDs", favMovieIds);
                        toggle.setChecked(true);
                        logprint();

                    } else {
                        // The toggle is disabled
                        PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putInt("FavStar" + mTrailerID, 0).commit();//saving last state for Star button

                        tinydb.remove("FavList" + mTrailerID);
                        favMovieIds.remove(Integer.toString(mTrailerID));
                        for (int k = 0; k < favMovieIds.size(); k++) {
                            Log.v(LOG_TAG, "favList contains: " + favMovieIds.get(k));
                        }
                        tinydb.putListString("FavListIDs", favMovieIds);
                        toggle.setChecked(false);
                        Toast.makeText(getActivity(), "This movie is removed from favorite", Toast.LENGTH_SHORT).show();
                        logprint();
                    }
                }
            });
        }

        return view;
    }
    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,mTrailers.get(0));
        return shareIntent;
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putInt("mId", mID);
        savedInstanceState.putInt("mTrailer_id",mTrailerID);
        savedInstanceState.putString("mTitle",mTitle);
        savedInstanceState.putString("mOverview", mOverView);
        savedInstanceState.putString("mReleaseDate", mReleaseDate);
        savedInstanceState.putDouble("mAverageVote", mVoteAverage);
        savedInstanceState.putString("mPosterUrl", mPosterUrl);
        savedInstanceState.putStringArrayList("Trailers",mTrailers);
        savedInstanceState.putParcelableArrayList("ReviewArr", AllReviews);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void logprint()
    {
        ArrayList<String> tempId=tinydb.getListString("FavListIDs");
        for(int i=0;i<tempId.size();i++) {

            ArrayList<String> temp=tinydb.getListString("FavList" + tempId.get(i));
            for(int j=0;j<temp.size();j++)
            {
                Log.v(LOG_TAG,"log list: "+temp.get(j));
            }
        }

    }

}
