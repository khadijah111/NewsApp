package com.example.khadijah.newsapp;

import android.graphics.Bitmap;

/**
 * Created by khadijah on 1/14/2018.
 */
public class News {
    // @param  title of the article
    private String mArticleTitle;

    // @param  name of the section
    private String mSectionName;

    // @param articleDate
    private String mArticleDate;

    // @param URL of the Article
    private String mArticleUrl;

    // @param name of the Article
    private String mArticleAuthor;

    /*Image for the book*/
    private Bitmap mArticleImage;

    public News(String articleTitle, String sectionName, String date, String url, String author, Bitmap articleImage) {
        mArticleTitle = articleTitle;
        mSectionName = sectionName;
        mArticleDate = date;
        mArticleUrl = url;
        mArticleAuthor = author;
        mArticleImage = articleImage;
    }

    public Bitmap getArticleImage() {
        return mArticleImage;
    }

    public String getAuthorName() {
        return mArticleAuthor;
    }

    public String getSectionName() {
        return mSectionName;
    }

    public String getArticleDate() {
        return mArticleDate;
    }

    public String getUrl() {
        return mArticleUrl;
    }

    public String getArticleTitle() {
        return mArticleTitle;
    }
}