package com.example.xyzreader.ui;

import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.UpdaterService;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Collections;

import okhttp3.OkHttpClient;
import okhttp3.Protocol;

import static android.R.attr.columnCount;
import static android.view.View.VISIBLE;

/**
 * An activity representing a list of Articles. This activity has different presentations for
 * handset and tablet-size devices. On handsets, the activity presents a list of items, which when
 * touched, lead to a {@link ArticleDetailActivity} representing item details. On tablets, the
 * activity presents a grid of items as cards.
 */
public class ArticleListActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LAST_SYNC_BUNDLE_KEY = "LAST_SYNC_BUNDLE_KEY";
    public static final int BASIC_LOADER_ID = 0;
    private Toolbar mToolbar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private static long last_sync;
    private long SYNC_THRESHOLD = 60 * 60 * 1000;
    private Long mDetailActivityCachedUrlID;
    private Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_list);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);


        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        setupRecyclerView();
        animateIntro();
        getLoaderManager().initLoader(BASIC_LOADER_ID, null, this);
        getLoaderManager().initLoader(ArticleDetailFragment.LOADER_ID_ARTICLE_WITH_ID, null, this);

        if (savedInstanceState != null) {
            Log.d(TAG, "Retrieving last_sync equal to " + last_sync);

            last_sync = savedInstanceState.getLong(LAST_SYNC_BUNDLE_KEY);
        }

        if ((Calendar.getInstance().getTimeInMillis() - last_sync) > SYNC_THRESHOLD){
            Log.d(TAG, "Syncing because difference is " + (Calendar.getInstance().getTimeInMillis() - last_sync) + " is bigger than the SYNC_THRESHOLD " + SYNC_THRESHOLD);
            refresh();


        }

        final OkHttpClient client = new OkHttpClient.Builder()
                .protocols(Collections.singletonList(Protocol.HTTP_1_1))
                .build();

        Picasso picasso = new Picasso.Builder(getApplicationContext())
                .downloader(new OkHttp3Downloader(client))
                .build();

        // set the global instance to use this Picasso object
        // all following Picasso (with Picasso.with(Context context) requests will use this Picasso object
        // you can only use the setSingletonInstance() method once!
        try {
            Picasso.setSingletonInstance(picasso);
        } catch (IllegalStateException ignored) {
            Log.e(TAG,ignored.getStackTrace().toString());
            // Picasso instance was already set
            // cannot set it after Picasso.with(Context) was already in use
        }

    }

    private void refresh() {

        startService(new Intent(this, UpdaterService.class));
        Log.d(TAG, "Saving last_sync equal to " + Calendar.getInstance().getTimeInMillis());
        last_sync = Calendar.getInstance().getTimeInMillis();
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(mRefreshingReceiver,
                new IntentFilter(UpdaterService.BROADCAST_ACTION_STATE_CHANGE));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mRefreshingReceiver);
    }


    private boolean mIsRefreshing = false;

    private final String TAG = this.getClass().getSimpleName();
    private BroadcastReceiver mRefreshingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (UpdaterService.BROADCAST_ACTION_STATE_CHANGE.equals(intent.getAction())) {
                adapter.swapCursor(null);
                adapter.notifyDataSetChanged();
                mIsRefreshing = intent.getBooleanExtra(UpdaterService.EXTRA_REFRESHING, false);
                Log.d(TAG, "Calling updateRefreshUI");

                updateRefreshingUI();
            }
        }
    };

    private void updateRefreshingUI() {
        Log.d(TAG, "Setting refreshing to " + mIsRefreshing);
        mSwipeRefreshLayout.setRefreshing(mIsRefreshing);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        if((id == ArticleDetailFragment.LOADER_ID_ARTICLE_WITH_ID) && (mDetailActivityCachedUrlID != null)){
            Log.d(TAG,"Creating loader for detail activity");
            return ArticleLoader.newInstanceForItemId(this, mDetailActivityCachedUrlID);
        }else if (id == BASIC_LOADER_ID){
            Log.d(TAG,"Creating loader for main activity");
            return ArticleLoader.newAllArticlesInstance(this);

        }
        return null;

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putLong(LAST_SYNC_BUNDLE_KEY, last_sync);
        super.onSaveInstanceState(outState);
    }

    public void setupRecyclerView(){
        adapter = new Adapter();
        adapter.setHasStableIds(true);
        int columnCount = getResources().getInteger(R.integer.list_column_count);
        StaggeredGridLayoutManager sglm =
                new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(sglm);



    }


    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {

        if(cursorLoader.getId() == BASIC_LOADER_ID){


            if (cursor != null){
                if(cursor.moveToFirst()){
                    Log.i(TAG, "Notifying item range inserted");
                    adapter.swapCursor(cursor);
                    adapter.notifyDataSetChanged();
                }

            }else{
                adapter.swapCursor(null);
                adapter.notifyDataSetChanged();
            }




        }

        if(cursorLoader.getId() == ArticleDetailFragment.LOADER_ID_ARTICLE_WITH_ID){
            if(cursor !=null && cursor.moveToFirst()){

                Picasso.with(this)
                        .load(cursor.getString(ArticleLoader.Query.PHOTO_URL))
                        .fetch();
            }
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.i("Loader", "onLoaderReset");
        adapter.swapCursor(null);
        adapter.notifyDataSetChanged();
    }

    private class Adapter extends RecyclerView.Adapter<ArticleItemViewHolder> {
        private Cursor mCursor;

        public Adapter(Cursor cursor) {
            mCursor = cursor;
        }

        public Adapter() {
            mCursor = null;
        }

        @Override
        public long getItemId(int position) {
            mCursor.moveToPosition(position);
            return mCursor.getLong(ArticleLoader.Query._ID);
        }

        @Override
        public ArticleItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Log.i("adapter", "onCreateViewHolder");
            View view = getLayoutInflater().inflate(R.layout.list_item_article, parent, false);
            final ArticleItemViewHolder vh = new ArticleItemViewHolder(view);
            view.setOnClickListener(new View.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onClick(View view) {
                    if (!mIsRefreshing) {
                        mDetailActivityCachedUrlID = getItemId(vh.getAdapterPosition());
                        getLoaderManager().restartLoader(ArticleDetailFragment.LOADER_ID_ARTICLE_WITH_ID, null, ArticleListActivity.this);


                        ImageView transitionImageView = (ImageView) view.findViewById(R.id.thumbnail);
                        Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(ArticleListActivity.this,
                                transitionImageView, transitionImageView.getTransitionName()).toBundle();

                        Intent i = new Intent(getApplicationContext(), ArticleDetailActivity.class);
                        i.putExtra(ArticleDetailFragment.EXTRA_ARTICLE_ID, getItemId(vh.getAdapterPosition()));
                        startActivity(i,bundle);
                    } else {
                        Toast.makeText(getApplicationContext(), "Wait a moment: refreshing.", Toast.LENGTH_SHORT).show();
                    }

                }
            });
            return vh;
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }

        @Override
        public void onBindViewHolder(ArticleItemViewHolder holder, int position) {
            Log.i("adapter", "onBindViewHolder for position " + position);

            mCursor.moveToPosition(position);
            holder.titleView.setText(mCursor.getString(ArticleLoader.Query.TITLE));
            holder.subtitleView.setText(
                    DateUtils.getRelativeTimeSpanString(
                            mCursor.getLong(ArticleLoader.Query.PUBLISHED_DATE),
                            System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                            DateUtils.FORMAT_ABBREV_ALL).toString()
                            + " by "
                            + mCursor.getString(ArticleLoader.Query.AUTHOR));



            Picasso.with(getApplicationContext())
                    .load(mCursor.getString(ArticleLoader.Query.THUMB_URL))
                    .placeholder(getDrawable(R.drawable.empty_detail))
                    .resize(500,500)
                    .centerInside()
                    .into(holder.thumbnailView);
            Log.d(TAG,"Trying to get data from " + mCursor.getString(ArticleLoader.Query.THUMB_URL));
        }

        @Override
        public int getItemCount() {
            if (mCursor != null){
                return mCursor.getCount();
            }else {
                return 0;
            }
        }

        public void swapCursor(Cursor cursor) {
            this.mCursor = cursor;
        }
    }

    private void animateIntro(){
        Log.i(TAG, "Setting animator...");
        mRecyclerView.setItemAnimator(new AnimationIntroAnimator());
    }

    public static class ArticleItemViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnailView;
        public TextView titleView;
        public TextView subtitleView;

        public ArticleItemViewHolder(View view) {
            super(view);
            thumbnailView = (ImageView) view.findViewById(R.id.thumbnail);
            titleView = (TextView) view.findViewById(R.id.article_title);
            subtitleView = (TextView) view.findViewById(R.id.article_subtitle);
        }
    }
}
