package com.example.xyzreader.ui;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;

import com.example.xyzreader.R;

/**
 * Created by rafaelanastacioalves on 9/17/16.
 * Adapted from http://frogermcs.github.io/instamaterial-recyclerview-animations-done-right/#disqus_thread
 */
public class AnimationIntroAnimator extends DefaultItemAnimator {
    private final String TAG = getClass().getSimpleName();
    private int lastAddAnimatedItem = -2;

    @Override
    public boolean animateAdd(RecyclerView.ViewHolder holder) {

//        if(holder.getLayoutPosition() > lastAddAnimatedItem ) {
//            lastAddAnimatedItem++;
        runEnterAnimation((ArticleListActivity.ArticleItemViewHolder) holder);
        return false;
//        }
//        dispatchAddFinished(holder);
//        return false;
    }

    @Override
    public boolean animateRemove(RecyclerView.ViewHolder holder) {
        View view = holder.itemView;
//        view.setVisibility(View.INVISIBLE);
        runExitAnimation((ArticleListActivity.ArticleItemViewHolder) holder);
        return super.animateRemove(holder);
    }

    private void runEnterAnimation(ArticleListActivity.ArticleItemViewHolder holder) {
//        int count = mRecyclerView.getChildCount();
//        Log.i(TAG,"Animation: counting " + count + " elements");
        Context context = holder.itemView.getContext();
        float offset = context.getResources().getDimensionPixelSize(R.dimen.offset_y);
        Interpolator interpolator =
                AnimationUtils.loadInterpolator(holder.itemView.getContext(), android.R.interpolator.linear_out_slow_in);
//
//        // loop over the children setting an increasing translation y but the same animation
//        // duration + interpolation
        int count = holder.getLayoutPosition();
        Log.i(TAG, "Animation: animating item of position" + count);

        for (int i = 0; i <= count; i++) {
            offset *= 1.5f;
        }
        View view = holder.itemView;
        view.setVisibility(View.VISIBLE);
        view.setTranslationY(offset);
        view.setAlpha(0.85f);
        // then animate back to natural position
        view.animate()
                .translationY(0f)
                .alpha(1f)
                .setInterpolator(interpolator)
                .setDuration(1000L)
                .start();

        // increase the offset distance for the next view
        offset *= 1.5f;
    }

    private void runExitAnimation(ArticleListActivity.ArticleItemViewHolder holder) {
        //        int count = mRecyclerView.getChildCount();
//        Log.i(TAG,"Animation: counting " + count + " elements");
        Context context = holder.itemView.getContext();
        float offset = context.getResources().getDimensionPixelSize(R.dimen.offset_y);
        Interpolator interpolator =
                AnimationUtils.loadInterpolator(holder.itemView.getContext(), android.R.interpolator.linear_out_slow_in);
//
//        // loop over the children setting an increasing translation y but the same animation
//        // duration + interpolation
        int count = holder.getLayoutPosition();
        Log.i(TAG, "Animation: animating removing item of position" + count);

        for (int i = 0; i <= count; i++) {
            offset *= 1.5f;
        }
        View view = holder.itemView;
        view.setVisibility(View.VISIBLE);
        view.setTranslationY(0f);
        view.setAlpha(0.85f);
        // then animate back to natural position
        view.animate()
                .translationY(-1*offset)
                .alpha(1f)
                .setInterpolator(interpolator)
                .setDuration(1000L)
                .start();

        // increase the offset distance for the next view
        offset *= 1.5f;
    }

}
