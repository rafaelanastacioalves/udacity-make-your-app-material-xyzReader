package com.example.xyzreader.ui;

import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;

/**
 * An activity representing a single Article detail screen, letting you swipe between articles.
 */
public class ArticleDetailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = getClass().getSimpleName();
    private Cursor mCursor;
    private long mStartId;

    private long mSelectedItemId;
    private int mSelectedItemUpButtonFloor = Integer.MAX_VALUE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().getDecorView().setSystemUiVisibility(
//                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
//                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//        }
        setContentView(R.layout.activity_article_detail);


        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getLoaderManager().initLoader(0, null, this);




//        mPagerAdapter = new MyPagerAdapter(getFragmentManager());
//        mPager = (ViewPager) findViewById(R.id.pager);
//        mPager.setAdapter(mPagerAdapter);
//        mPager.setPageMargin((int) TypedValue
//                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics()));
//        mPager.setPageMarginDrawable(new ColorDrawable(0x22000000));
//
//        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
//
//
//            @Override
//            public void onPageSelected(int position) {
//                if (mCursor != null) {
//                    mCursor.moveToPosition(position);
//                }
//                mSelectedItemId = mCursor.getLong(ArticleLoader.Query._ID);
////                updateUpButtonPosition();
//            }
//        });

//        mUpButtonContainer = findViewById(R.id.up_container);

//        mUpButton = findViewById(R.id.action_up);
//        mUpButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onSupportNavigateUp();
//            }
//        });
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            mUpButtonContainer.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
//                @Override
//                public WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
//                    view.onApplyWindowInsets(windowInsets);
//                    mTopInset = windowInsets.getSystemWindowInsetTop();
//                    mUpButtonContainer.setTranslationY(mTopInset);
//                    updateUpButtonPosition();
//                    return windowInsets;
//                }
//            });
//        }

        if (savedInstanceState == null) {
            Log.d(LOG_TAG,"onCreate: trying to call fragment.");

            if (getIntent() != null && getIntent().hasExtra(ArticleDetailFragment.EXTRA_ARTICLE_ID)) {
                mStartId = getIntent().getLongExtra(ArticleDetailFragment.EXTRA_ARTICLE_ID, 0);
                mSelectedItemId = mStartId;
                getFragmentManager().beginTransaction().add(R.id.article_fragment_container, ArticleDetailFragment.newInstance(mStartId)).commit();

//            }
            }

        }


    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return ArticleLoader.newAllArticlesInstance(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {

        Log.d(LOG_TAG,"Cursor loaded.");
        mCursor = cursor;

//        mPagerAdapter.notifyDataSetChanged();

//        // Select the start ID
//        if (mStartId > 0) {
//            mCursor.moveToFirst();
//            // TODO: optimize
//            while (!mCursor.isAfterLast()) {
//                if (mCursor.getLong(ArticleLoader.Query._ID) == mStartId) {
//                    final int position = mCursor.getPosition();
//                    mPager.setCurrentItem(position, false);
//                    break;
//                }
//                mCursor.moveToNext();
//            }
//            mStartId = 0;
//        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mCursor = null;
    }

//    public void onUpButtonFloorChanged(long itemId, ArticleDetailFragment fragment) {
//        if (itemId == mSelectedItemId) {
//            mSelectedItemUpButtonFloor = fragment.getUpButtonFloor();
//            updateUpButtonPosition();
//        }
//    }

//    private void updateUpButtonPosition() {
//        int upButtonNormalBottom = mTopInset + mUpButton.getHeight();
//        mUpButton.setTranslationY(Math.min(mSelectedItemUpButtonFloor - upButtonNormalBottom, 0));
//    }

//    private class MyPagerAdapter extends FragmentStatePagerAdapter {
//        public MyPagerAdapter(FragmentManager fm) {
//            super(fm);
//        }
//
//        @Override
//        public void setPrimaryItem(ViewGroup container, int position, Object object) {
//            super.setPrimaryItem(container, position, object);
//            ArticleDetailFragment fragment = (ArticleDetailFragment) object;
////            if (fragment != null) {
////                mSelectedItemUpButtonFloor = fragment.getUpButtonFloor();
////                updateUpButtonPosition();
////            }
//        }
//
//        @Override
//        public Fragment getItem(int position) {
//            mCursor.moveToPosition(position);
//            return ArticleDetailFragment.newInstance(mCursor.getLong(ArticleLoader.Query._ID));
//        }

//        @Override
//        public int getCount() {
//            return (mCursor != null) ? mCursor.getCount() : 0;
//        }

}
