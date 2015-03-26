package com.munon.turboimageview;

import android.app.admin.DeviceAdminInfo;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

/**
 * Created by nuborisar on 20/03/2015.
 */
public class TextObject extends MultiTouchObject {

    private static final double INITIAL_SCALE_FACTOR = 0.15;
    private String mText;

    public void setText(String str) {
        mText = str;
    }

    public TextObject(String str, Resources res) {
        super(res);

        mText = str;
    }


    public TextObject(TextObject e, Resources res) {
        super(res);

        //mDrawable = e.mDrawable;
        mText = e.mText;
        mScaleX = e.mScaleX;
        mScaleY = e.mScaleY;
        mCenterX = e.mCenterX;
        mCenterY = e.mCenterY;
        mAngle = e.mAngle;
    }

    public void draw(Canvas canvas) {
        canvas.save();

        float dx = (mMaxX + mMinX) / 2;
        float dy = (mMaxY + mMinY) / 2;

        //  mDrawable.setBounds((int) mMinX, (int) mMinY, (int) mMaxX, (int) mMaxY);

        canvas.translate(dx, dy);
        canvas.rotate(mAngle * 180.0f / (float) Math.PI);
        canvas.translate(-dx, -dy);

        Paint p1 = new Paint(Paint.ANTI_ALIAS_FLAG);
        if (misRemarked)
            p1.setColor(Color.parseColor("#000000"));
        else
            p1.setColor(Color.parseColor("#ffffff"));
        p1.setStyle(Paint.Style.FILL);
        p1.setTextSize(40 + mScaleX * mScaleY);

        canvas.drawText(mText, dx, dy, p1);
        // canvas.drawRect(0,dy,dx,0,p1);

        // canvas.drawCircle(dx,dy,10*mScaleX+mScaleY,p1);
        canvas.restore();

    }

    public void unRemark(Canvas canvas) {
        misRemarked = false;
//        float dx = (mMaxX + mMinX) / 2;
//        float dy = (mMaxY + mMinY) / 2;
//        Paint p1 = new Paint(Paint.ANTI_ALIAS_FLAG);
//        p1.setColor(Color.parseColor("#990000ff"));
//        p1.setStyle(Paint.Style.FILL);

        // canvas.drawCircle(dx,dy,10*mScaleX+mScaleY,p1);
//

        this.draw(canvas);
    }

    boolean misRemarked = false;

    public void remark(Canvas canvas) {
        misRemarked = true;
//        float dx = (mMaxX + mMinX) / 2;
//        float dy = (mMaxY + mMinY) / 2;
//        Paint p1 = new Paint(Paint.ANTI_ALIAS_FLAG);
//        p1.setColor(Color.parseColor("#990000ff"));
//        p1.setStyle(Paint.Style.FILL);
//
//        canvas.drawCircle(dx,dy,10*mScaleX+mScaleY,p1);
        //borrar?
//
    }

    /**
     * Called by activity's onPause() method to free memory used for loading the images
     */
    @Override
    public void unload() {
        //  this.mDrawable = null;
    }

    /**
     * Called by activity's onResume() method to load the images
     */
    @Override
    public void load(Context context, float startMidX, float startMidY) {
        Resources res = context.getResources();
        getMetrics(res);

        mStartMidX = startMidX;
        mStartMidY = startMidY-72;
//failstringmeasure?
        mWidth = 144;
        mHeight = 72;

        float centerX, centerY;
        float scaleX, scaleY;
        float angle;
        if (mFirstLoad) {
            centerX = startMidX;
            centerY = startMidY;

            float scaleFactor = (float) (Math.max(mDisplayWidth, mDisplayHeight) /
                    (float) Math.max(mWidth, mHeight) * INITIAL_SCALE_FACTOR);
            scaleX = scaleY = scaleFactor;
            angle = 0.0f;

            mFirstLoad = false;
        } else {
            centerX = mCenterX;
            centerY = mCenterY;
            scaleX = mScaleX;
            scaleY = mScaleY;
            angle = mAngle;
        }
        setPos(centerX, centerY, scaleX, scaleY, mAngle);
    }
}

