package com.example.xyzreader.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.xyzreader.R;

/**
 * An activity representing a single Article detail screen, letting you swipe between articles.
 */
public class ArticleDetailActivity extends AppCompatActivity {

    private final String LOG_TAG = getClass().getSimpleName();
    private Cursor mCursor;
    private long mStartId;

    private long mSelectedItemId;
    private int mSelectedItemUpButtonFloor = Integer.MAX_VALUE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_article_detail);






        if (savedInstanceState == null) {
            Log.d(LOG_TAG,"onCreate: trying to call fragment.");

            if (getIntent() != null && getIntent().hasExtra(ArticleDetailFragment.EXTRA_ARTICLE_ID)) {
                mStartId = getIntent().getLongExtra(ArticleDetailFragment.EXTRA_ARTICLE_ID, 0);
                mSelectedItemId = mStartId;
                getFragmentManager().beginTransaction().add(R.id.article_fragment_container, ArticleDetailFragment.newInstance(mStartId)).commit();
                supportPostponeEnterTransition();
            }

        }


    }




}
