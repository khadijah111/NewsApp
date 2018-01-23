package com.example.khadijah.newsapp;

import android.content.Context;
        import android.content.AsyncTaskLoader;
        import java.util.ArrayList;
        import java.util.List;

/**
 * Created by khadijah on 1/14/2018.
 */
public class NewsLoader extends AsyncTaskLoader<List<News>>{
    /**
     * This method is invoked (or called) on a background thread, so we can perform
     * long-running operations like making a network request.
     *
     * It is NOT okay to update the UI from a background thread, so we just return an
     * {@link ArrayList <NewsLoader>} object as the result.
     */
    /** Tag for log messages */
    private static final String LOG_TAG = NewsLoader.class.getName();

    /** Query URL */
    private String mUrl;

    /**
     * Constructs a new {@link NewsLoader}.
     *
     * @param context of the activity
     * @param url to load data from
     */
    public NewsLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * This is on a background thread.
     */
    @Override
    public List<News> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        // Perform the HTTP request for earthquake data and process the response. ALL in the background
        // Kick off an {@link AsyncTask} to perform the network request
        // Perform the network request, parse the response, and extract a list of earthquakes.
        List<News> news = QuiryUtil.fetchEarthquakeData(mUrl);
        return news;
    }
}
