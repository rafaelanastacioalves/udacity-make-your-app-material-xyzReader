package com.example.xyzreader.ui;

import android.graphics.Bitmap;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

/**
 * Created by rafael.alves on 15/09/16.
 */
public class OnDemandAspectRatioTransformation implements Transformation {

    private float mAspectRatio;

    public OnDemandAspectRatioTransformation(float aspectRatio){
        mAspectRatio = aspectRatio;

    }

    @Override
    public Bitmap transform(Bitmap source) {
        int height = (int) (((float)source.getWidth())/mAspectRatio);
        Bitmap result = Bitmap.createScaledBitmap(source,
                source.getWidth(),
                height,
                false);
        if (result != source){
            source.recycle();
        }
        return result;
    }

    @Override
    public String key() {
        return "nDemandAspectRatioTransformation";
    }
}
