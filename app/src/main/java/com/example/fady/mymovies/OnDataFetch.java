package com.example.fady.mymovies;

import java.util.ArrayList;

/**
 * Created by fady on 8/22/2015.
 */
public interface OnDataFetch {
    public void onDataFetched(ArrayList<?> data,String Tag);
    public void onTrailerDataFetched(ArrayList<?> data);
    public void onReviewDataFetched(ArrayList<?> data);

    public void onFetchError(String msg);
}
