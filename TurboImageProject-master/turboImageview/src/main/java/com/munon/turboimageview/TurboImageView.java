package com.munon.turboimageview;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

import com.munon.turboimageview.MultiTouchController.MultiTouchObjectCanvas;
import com.munon.turboimageview.MultiTouchController.PointInfo;
import com.munon.turboimageview.MultiTouchController.PositionAndScale;

import java.util.ArrayList;

public class TurboImageView extends View implements
        MultiTouchObjectCanvas<MultiTouchObject> {

    private ArrayList<MultiTouchObject> mImages = new ArrayList<MultiTouchObject>();
    private ArrayList<MultiTouchObject> mText = new ArrayList<MultiTouchObject>();

    private MultiTouchController<MultiTouchObject> multiTouchController = new MultiTouchController<MultiTouchObject>(
            this);
    private PointInfo currTouchPoint = new PointInfo();
    private static final int UI_MODE_ROTATE = 1, UI_MODE_ANISOTROPIC_SCALE = 2;
    private int mUIMode = UI_MODE_ROTATE;
    private static final float SCREEN_MARGIN = 100;
    private int displayWidth, displayHeight;

    private Canvas mcanvas;

    public TurboImageView(Context context) {
        this(context, null);
    }

    public TurboImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TurboImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        Resources res = context.getResources();
        setBackgroundColor(Color.TRANSPARENT);

        DisplayMetrics metrics = res.getDisplayMetrics();
        this.displayWidth = res.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? Math
                .max(metrics.widthPixels, metrics.heightPixels) : Math.min(
                metrics.widthPixels, metrics.heightPixels);
        this.displayHeight = res.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? Math
                .min(metrics.widthPixels, metrics.heightPixels) : Math.max(
                metrics.widthPixels, metrics.heightPixels);
    }

    public void loadImages(Context context, int resourceId) {
        Resources res = context.getResources();
        mImages.add(new ImageObject(resourceId, res));
        float cx = getX() + getWidth() / 2;
        float cy = getY() + getHeight() / 2;
        mImages.get(mImages.size() - 1).load(context, cx, cy);
        invalidate();
    }

    public void loadTextView(Context context, String text) {
        Resources res = context.getResources();
        mText.add(new TextObject(text, res));
        // float cx = getX() + getWidth() / 2;
        float cy = getY() + getHeight() / 2;
        mText.get(mText.size() - 1).load(context, 20, cy);
        invalidate();
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int n = mImages.size();
        for (int i = 0; i < n; i++)
            mImages.get(i).draw(canvas);

        n = mText.size(); //los text x encima
        for (int i = 0; i < n; i++)
            mText.get(i).draw(canvas);

        mcanvas = canvas;
    }

    public void trackballClicked() {
        mUIMode = (mUIMode + 1) % 3;
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return multiTouchController.onTouchEvent(event);
    }

    /**
     * Get the image that is under the single-touch point, or return null
     * (canceling the drag op) if none
     */
    public MultiTouchObject getDraggableObjectAtPoint(PointInfo pt) {
        float x = pt.getX(), y = pt.getY();
        try {
            int n = mImages.size();
            for (int i = n - 1; i >= 0; i--) {
                ImageObject im = (ImageObject) mImages.get(i);
                if (im.containsPoint(x, y))
                    return im;
            }
        } catch (Exception e) {
        }
        try {


            int n = mText.size();
            for (int i = n - 1; i >= 0; i--) {
                TextObject tx = (TextObject) mText.get(i);
                if (tx.containsPoint(x, y))
                    return tx;
            }
        } catch (Exception e) {
        }


        return null;
    }

    /**
     * Select an object for dragging. Called whenever an object is found to be
     * under the point (non-null is returned by getDraggableObjectAtPoint()) and
     * a drag operation is starting. Called with null when drag op ends.
     */
    MultiTouchObject currentSelectedObject;

    public void selectObject(MultiTouchObject multiTouchObject, PointInfo touchPoint) {
        currTouchPoint.set(touchPoint);
        //pillamos el view sea, vemos de donde borrar en el deleteSelecteObjet
        if (multiTouchObject != null) {

            //no chuta, metodos monguers lvl1
            if (multiTouchObject.equals(currentSelectedObject)) {
                multiTouchObject.unRemark(mcanvas);
                currentSelectedObject = null;
            } else
                multiTouchObject.remark(mcanvas);

            currentSelectedObject = multiTouchObject;

        }

        //region  Move image to the top of the stack when selected
         /*   if (mImages.contains(currentSelectedObject)) {
                mImages.remove(currentSelectedObject);
                mImages.add(currentSelectedObject);
            }

            if (mText.contains(currentSelectedObject)) {
                mText.remove(currentSelectedObject);
                mText.add(currentSelectedObject);
            }
*/
        //endregion}

        invalidate();
    }

    public void deleteSelectedObject() {
        if (currentSelectedObject != null) {
            if (mImages != null && mImages.size() > 0
                    && mImages.contains(currentSelectedObject)) {
                currentSelectedObject.unload();
                mImages.remove(currentSelectedObject);
            }
            if (mText != null && mText.size() > 0
                    && mText.contains(currentSelectedObject)) {
                currentSelectedObject.unload();
                mText.remove(currentSelectedObject);
            }
        }
//        if (mImages.size() > 0) {
//
//
//
//            mImages.remove(mImages.size() - 1);//asv esto comba con lo de mover la img dentro del array en el select
        invalidate();
//        }
    }

    public void changeSelectedTextObject(String str) {

        if (mText != null && mText.size() > 0
                && mText.contains(currentSelectedObject)) {

            ((TextObject) currentSelectedObject).setText(str);
        }

    }

    /**
     * Get the current position and scale of the selected image. Called whenever
     * a drag starts or is reset.
     */
    public void getPositionAndScale(MultiTouchObject img,
                                    PositionAndScale objPosAndScaleOut) {
        objPosAndScaleOut.set(img.getCenterX(), img.getCenterY(),
                (mUIMode & UI_MODE_ANISOTROPIC_SCALE) == 0,
                (img.getScaleX() + img.getScaleY()) / 2,
                (mUIMode & UI_MODE_ANISOTROPIC_SCALE) != 0, img.getScaleX(),
                img.getScaleY(), (mUIMode & UI_MODE_ROTATE) != 0,
                img.getAngle());
    }

    /**
     * Set the position and scale of the dragged/stretched image.
     */
    public boolean setPositionAndScale(MultiTouchObject img,
                                       PositionAndScale newImgPosAndScale, PointInfo touchPoint) {


        boolean ok;
        currTouchPoint.set(touchPoint);
        try {
            ok = ((ImageObject) img).setPos(newImgPosAndScale);
            if (ok)
                invalidate();
            return ok;
        } catch (Exception e) {
        }
        try {

            ok = ((TextObject) img).setPos(newImgPosAndScale);
            if (ok)
                invalidate();
            return ok;
        } catch (Exception e) {
        }
        return false;
    }

    public boolean pointInObjectGrabArea(PointInfo pt, MultiTouchObject img) {
        return false;
    }


}
