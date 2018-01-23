package com.example.khadijah.newsapp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by khadijah on 1/14/2018.
 */
public class NewsArrayAdapter extends ArrayAdapter<News> {

    private static final String LOG_TAG = News.class.getSimpleName();

    public NewsArrayAdapter(Activity context, ArrayList<News> news) {
        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for three TextViews , the adapter is not
        // going to use this second argument, so it can be any value. Here, we used 0.
        super(context, 0, news);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.news_list_item, parent, false);
        }

        //Get the {@link News} object located at this position in the list
        News currentNews = getItem(position);

        //1--  Get the article title string from the News object
        String articleTitle = currentNews.getArticleTitle();

        //NOW Find the TextView in the news_list_item.xml layout with the ID article_title_textView
        TextView articleTitleTextView = (TextView) listItemView.findViewById(R.id.article_title_textView);

        // Display the article title of the current news in that TextView
        articleTitleTextView.setText(articleTitle);

        // 2-- Get the article Author string from the News object
        String articleAuthor = currentNews.getAuthorName();

        //NOW Find the TextView in the news_list_item.xml layout with the ID auther_name_textView
        TextView authorTextView = (TextView) listItemView.findViewById(R.id.auther_name_textView);

        // Display the author name of the current news in that TextView
        authorTextView.setText(articleAuthor);

        // 3-- Get the sectionName string from the News object
        String sectionName = currentNews.getSectionName();

        //NOW Find the TextView in the news_list_item.xml layout with the ID auther_name_textView
        TextView sectionNameTextView = (TextView) listItemView.findViewById(R.id.section_title_textView);

        // Display the author name of the current news in that TextView
        sectionNameTextView.setText("##" + sectionName);

        // 4- Get the article date as string from the News object
        String articleDate = currentNews.getArticleDate();
        String[] articleDateFormatted = articleDate.split("T");
        articleDate = articleDateFormatted[0];

        //NOW Find the TextView in the news_list_item.xml layout with the ID article_date_textView
        TextView articleDateTextView = (TextView) listItemView.findViewById(R.id.article_date_textView);

        // Display the article date of the current news in that TextView
        articleDateTextView.setText(articleDate);

        // 5- Get the article date (Bitmap) from the News object

        //NOW Find the TextView in the news_list_item.xml layout with the ID articleImage_imageView
        ImageView articleImageTextView = (ImageView) listItemView.findViewById(R.id.articleImage_imageView);

        if( currentNews.getArticleImage() != null)
        {
            Bitmap articleImage = currentNews.getArticleImage();

            // Display the article image of the current news in that TextView
            articleImageTextView.setImageBitmap(articleImage);
        }
        else
        {
            articleImageTextView.setImageResource(R.drawable.nobookcover);
        }


        // Return the whole list item layout (containing 3 TextViews)
        // so that it can be shown in the ListView
        return listItemView;
    }

    public String FormatDate(Long milliSecondDate) {
        Date dateObject = new Date(milliSecondDate);
        SimpleDateFormat dateFormatter = new SimpleDateFormat("LLL dd, yyyy");
        return dateFormatter.format(dateObject);
    }

}
