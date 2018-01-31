package com.example.khadijah.newsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

//version 2
public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>> {

    private static final int NEWS_LOADER_ID = 1;

    /**
     * Constant value for the earthquake loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    public static final String LOG_TAG = MainActivity.class.getName();

    /**
     * URL for earthquake data from the USGS dataset
     */
    public static String USGS_REQUEST_URL =
            "http://content.guardianapis.com/search?order-by=newest&show-tags=contributor&show-elements=image&api-key=test";
    /**
     * Adapter for the list of earthquakes
     */
    private NewsArrayAdapter mAdapter;

    /**
     * TextView that is displayed when the list is empty
     */
    private TextView mEmptyStateTextView;

    private String keywordSearch = "";

    private Button SearchButton;

    private TextView textKeyword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find a reference to the {@link Button} in the layout
        SearchButton = (Button) findViewById(R.id.searchButton);

        // Find a reference to the {@link ListView} in the layout
        ListView newsListView = (ListView) findViewById(R.id.list);

        // Find a reference to the {@link TextView} in the layout
        textKeyword = (TextView) findViewById(R.id.editText);

        // Create a new {@link ArrayAdapter} of news
        mAdapter = new NewsArrayAdapter(this, new ArrayList<News>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        newsListView.setAdapter(mAdapter);

        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        newsListView.setEmptyView(mEmptyStateTextView);

        // If there is a network connection, fetch data
        if (isOnline()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
        } else {// Otherwise, display error
            // First, hide progress indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.progressView);
            loadingIndicator.setVisibility(View.GONE);

            // Set empty state text to display "No earthquakes found."
            mEmptyStateTextView.setText(R.string.no_connectivity);
        }

        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Get the {@link News} object located at this position in the Arraylist
                News currentNews = mAdapter.getItem(position); //or earthquakes.get(position);
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(currentNews.getUrl()));
                startActivity(browserIntent);
            }
        });

        SearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View v = getCurrentFocus();   //for hiding the keyboard when search button is clicked
                if (v != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }

                mEmptyStateTextView.setVisibility(View.GONE);
                mAdapter.clear();

                if (textKeyword.getText().toString().trim().matches("")) {
                    //set the empty view message
                    mEmptyStateTextView.setVisibility(View.VISIBLE);
                    mEmptyStateTextView.setText(R.string.search_message);
                } else {
                    //get data "keyword" from Edit TEXT
                    keywordSearch = textKeyword.getText().toString().trim();
                    textKeyword.setText("");
                    Log.v("tag", keywordSearch);

                    //chick the internet connectivity
                    // If there is a network connection, fetch data
                    if (isOnline()) {
                        // Restart the loader. Pass in the int ID constant defined above and pass in null for
                        // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
                        // because this activity implements the LoaderCallbacks interface)
                        getLoaderManager().restartLoader(NEWS_LOADER_ID, null, MainActivity.this);

                    } else {// Otherwise, display error
                        // First, hide progress indicator so error message will be visible
                        View loadingIndicator = findViewById(R.id.progressView);
                        loadingIndicator.setVisibility(View.GONE);

                        // Clear the adapter of previous newses data
                        mAdapter.clear();

                        // Set empty state text to display "No NEWS found."
                        // Set mEmptyStateTextView visible
                        mEmptyStateTextView.setVisibility(View.VISIBLE);
                        mEmptyStateTextView.setText(R.string.no_connectivity);
                    }
                }
            }
        });
    }

    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {
        View loadingIndicator = findViewById(R.id.progressView);
        loadingIndicator.setVisibility(View.VISIBLE);
        mEmptyStateTextView.setVisibility(View.GONE);
        if (keywordSearch.equals("")) {
            USGS_REQUEST_URL = "http://content.guardianapis.com/search?";
        } else {
            USGS_REQUEST_URL = "http://content.guardianapis.com/search?q=" + keywordSearch;
        }
        // Get the user's settings from SharedPreferences
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // getString retrieves a String value from the preferences. The second parameter is the default value for this preference.
        String newsTopic = sharedPrefs.getString(
                getString(R.string.settings_news_topic_key),
                getString(R.string.settings_news_topic_default));

        // parse breaks apart the URI string that's passed into its parameter
        Uri baseUri = Uri.parse(USGS_REQUEST_URL);

        // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        Uri.Builder uriBuilder = baseUri.buildUpon();

        // Append query parameter and its value. For example, the `q=sport`
        uriBuilder.appendQueryParameter("order-by", "newest");
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        uriBuilder.appendQueryParameter("show-elements", "image");
        uriBuilder.appendQueryParameter("section", newsTopic);
        uriBuilder.appendQueryParameter("api-key", "test");
        Log.v("full Uri", uriBuilder.toString());
        // Return the completed uri `
        USGS_REQUEST_URL = uriBuilder.toString();
        return new NewsLoader(this, USGS_REQUEST_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> news) {

        // Log.v("NOTE", "onLoadFinished CALLED");
        // Hide loading progress because the data has been loaded
        View loadingIndicator = findViewById(R.id.progressView);
        loadingIndicator.setVisibility(View.GONE);

        // Set empty state text to display "No news found."
        mEmptyStateTextView.setText(R.string.no_news);

        // Clear the adapter of previous news data
        mAdapter.clear();
        // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (news != null && !news.isEmpty()) {
            mAdapter.addAll(news);
        }
    }

    public boolean isOnline() {
        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connectMNGR = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo NWinfo = connectMNGR.getActiveNetworkInfo();
        return NWinfo != null && NWinfo.isConnected();
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }

    @Override
    // This method initialize the contents of the Activity's options menu.
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the Options Menu we specified in XML
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}