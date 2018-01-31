package com.example.khadijah.newsapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

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
import java.util.List;

/**
 * Created by khadijah on 1/14/2018.
 */
public class QuiryUtil {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = QuiryUtil.class.getSimpleName();

    /**
     * Query the USGS dataset and return an {@link News} object to represent a single News.
     */
    public static List<News> fetchEarthquakeData(String requestUrl) {

        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }
        // Extract relevant fields from the JSON response and create a list of {@link Earthquake}s
        List<News> newsList = extractFeatureFromJson(jsonResponse);

        // Return the list of {@link newsList}s
        return newsList;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    public static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
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

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                // Log.e("connection200", "200 code: " + urlConnection.getResponseCode());
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e("ERROR", "Error response code: " + urlConnection.getErrorStream());
            }
        } catch (IOException e) {
            Log.e("ERROR", "Problem retrieving the earthquake JSON results." + e.getMessage(), e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
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

    /**
     * Return an {@link News} object by parsing out information
     * about the  news from the input newsJSON string.
     */
    private static List<News> extractFeatureFromJson(String SAMPLE_JSON_RESPONSE) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(SAMPLE_JSON_RESPONSE)) {
            return null;
        }
        // Create an empty ArrayList that we can start adding earthquakes to
        List<News> articlesList = new ArrayList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            // Create a JSONObject from the JSON response string
            JSONObject ROOT = new JSONObject(SAMPLE_JSON_RESPONSE);

            JSONObject responseObject = ROOT.getJSONObject("response");
            // Extract the JSONArray associated with the key called "results",
            // which represents a list of results (or articles).
            JSONArray newsArray = null;
            if (responseObject.has("results")) {
                newsArray = responseObject.getJSONArray("results");
            }

            // For each news article in the newsArray, create an {@link News} object
            for (int i = 0; i < newsArray.length(); i++) {
                // Get a single article at position i within the list of news articles
                JSONObject currentArticle = newsArray.getJSONObject(i);
                // For a given article, extract the JSONObject associated with the
                // key called "results", which represents a list of all properties
                // for that news.
                // Extract the value for the key called "sectionName"
                String sectionNameValue = currentArticle.optString("sectionName");

                // Extract the value for the key called "webTitle"
                String articleTitleValue = currentArticle.optString("webTitle");

                // Extract the value for the key called "webUrl"
                String articleUrlValue = currentArticle.optString("webUrl");

                // Extract the value for the key called "webPublicationDate"
                String articleDateValue = currentArticle.optString("webPublicationDate");
                //Log.e(LOG_TAG, "try to extraxt tags");
                JSONArray tagsArray = currentArticle.getJSONArray("tags");

                String articlAuthor = "";
                Bitmap articleImageBitMap = null;

                if (currentArticle.getJSONArray("tags") != null) {
                    //Log.e(LOG_TAG, "TAGS EXTRACTED");
                    String articleImageUrl = "";

                    // For each news article in the newsArray, create an {@link News} object
                    for (int j = 0; j < tagsArray.length(); j++) {
                        // Get a single tag at position i within the list of article tags
                        JSONObject currentTag = tagsArray.getJSONObject(j);
                        articlAuthor = currentTag.getString("firstName");
                        articleImageUrl = currentTag.optString("bylineImageUrl");
                        articleImageBitMap = getBitmapFromURL(articleImageUrl);
                    }
                }
                // Create a new {@link News} object with the article name, date, author, url, and section from the JSON response.
                News CurrentNews = new News(articleTitleValue, sectionNameValue, articleDateValue, articleUrlValue, articlAuthor, articleImageBitMap);
                articlesList.add(CurrentNews);
            }

            // build up a list of Earthquake objects with the corresponding data.
        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }
        // Return the list of earthquakes
        return articlesList;
    }

    public static Bitmap getBitmapFromURL(String src) {
        if (src == null || src.equals("")) {
            return null;
        } else {
            try {
                //Log.e("src",src);
                URL url = new URL(src);
                //Log.e("src",src);
                //Log.e("URL",url.toString());
                Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                return image;
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("Exception", e.getMessage());
                return null;
            }
        }
    }
}