package michal.cardmaker.presenter.cropViewLibrary;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class CropView extends View {
    private static final String LOGTAG = "CropView";

    private RectF mCanvasRect;
    private RectF mScreenInCanvas;
    private RectF mCropInScreen;

    private Bitmap mImage;
    private Paint mPaint;
    private Paint mBorderPaint;
    private Paint mOverlayPaint;
    private CropObject mCropObj;

    private int mRotation = 0;
    private Matrix mInitialDisplayImageMatrix;
    private Matrix mDisplayImageMatrix;
    private Matrix mDisplayCropMatrix;
    private Matrix mImageCropInverse;
    // private Matrix mDisplayTemlateMatrix;
    // private Matrix mDisplayMatrixInverse;
    private boolean mDirty = false;
    private int mMarginHorizontal = 0;
    private int mMarginVertical = 0;
    private int mOverlayShadowColor = 0x80000000;
    private int mBorderColor = 0xFFFFFFFF;
    private int mMinSideSize = 90;
    // --
    float mLastX = 0;
    float mLastY = 0;
    PointF mEventCenter = new PointF();
    float mOriginalDistance = 1f;
    float mOriginalDegrees = 0;
    Matrix mSavedImageMatrix = new Matrix();
    Matrix mSavedImageCropInverse = new Matrix();
    private static final int TOUCH_MODE_NONE = 0;
    private static final int TOUCH_MODE_DRAG = 1;
    private static final int TOUCH_MODE_ZOOM = 2;
    int mTouchMode = TOUCH_MODE_NONE;

    float mNewX = 0;
    float mNewY = 0;

    public CropView(Context context) {
        super(context);
        setup(context);
    }

    public CropView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(context);
    }

    public CropView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setup(context);
    }

    @TargetApi(21)
    public CropView(Context context, AttributeSet attrs, int defStyleAttr,
                    int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setup(context);
    }

    private void setup(Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        mMarginHorizontal = 0;
        mMarginVertical = 0;
        mMinSideSize = (int) (45 * density + 0.5);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
        mOverlayPaint = new Paint();
        mOverlayPaint.setColor(mOverlayShadowColor);
        mOverlayPaint.setStyle(Paint.Style.FILL);

        mBorderPaint = new Paint();
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setStrokeWidth(CropUtils.dip2px(getContext(), 2));
    }

    public void initialize(Bitmap image, RectF imageOriginalRect, Rect cropRect, int rotation) {
        mImage = image;
        mRotation = rotation;
        RectF imageRect = new RectF(0, 0, image.getWidth(), image.getHeight());
        mCropObj = new CropObject(imageRect, imageOriginalRect, cropRect);
        mDirty = true;
        invalidate();
    }

    public Result getCropResult() {
        Result result = new Result();
        RectF imageRect = mCropObj.getImageRect();
        RectF cropRect = mCropObj.getCropRect();
        RectF invertedImageRect = new RectF();
        RectF invertedCropRect = new RectF();
        Matrix tempMatrix = new Matrix();
        /**
         * image is fixed, crop is translate/scale/rotate
         */
        // get initial image rect
        mInitialDisplayImageMatrix.mapRect(invertedImageRect, imageRect);
        // get crop rect
        tempMatrix.reset();
        if (!mImageCropInverse.invert(tempMatrix)) {
            return null;
        }
        tempMatrix.preConcat(mDisplayCropMatrix);
        tempMatrix.mapRect(invertedCropRect, cropRect);

        // get intersection rect
        float[] cropCorners = CropMath.getCornersFromRect(invertedCropRect);
        RectF unrotatedCropRect = CropMath.trapToRect(cropCorners);

        float[] unrotatedCropCorners = CropMath
                .getCornersFromRect(unrotatedCropRect);
        CropMath.getEdgePoints(invertedImageRect, unrotatedCropCorners);
        RectF intersectionRect = CropMath.trapToRect(unrotatedCropCorners);

        if (intersectionRect.width() == 0 || intersectionRect.height() == 0) {
            return null;
        }

        tempMatrix = new Matrix();
        tempMatrix.reset();
        if (!mInitialDisplayImageMatrix.invert(tempMatrix)) {
            return null;
        }
        RectF rawIntersectionRect = new RectF();
        tempMatrix.mapRect(rawIntersectionRect, intersectionRect);

        RectF displayCropRect = new RectF();
        mDisplayCropMatrix.mapRect(displayCropRect, cropRect);

        result.rawImageRect = imageRect;
        result.rawIntersectionRect = rawIntersectionRect;
        result.displayImageMatrix = mDisplayImageMatrix;
        result.displayCropRect = displayCropRect;

        Log.d(LOGTAG,
                "rawImageRect: " + result.rawImageRect.toString()
                        + ",rawIntersectionRect: "
                        + result.rawIntersectionRect.toString());
        return result;
    }

    public void resetImagePosition() {
        clearDisplay();
        mNewX = 0;
        mNewY = 0;
    }

    public static class Result {
        public RectF displayCropRect;
        public RectF rawImageRect;
        public RectF rawIntersectionRect;
        public Matrix displayImageMatrix;
    }

    public RectF getCropRect() {
        return mCropObj.getCropRect();
    }

    public RectF getImageRect() {
        return mCropObj.getImageRect();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mDisplayImageMatrix == null || mDisplayCropMatrix == null) {
            return true;
        }

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mTouchMode = TOUCH_MODE_DRAG;

                mLastX = event.getX();
                mLastY = event.getY();
                Log.d("point_cord", String.valueOf(mLastX) + " " + String.valueOf(mLastY));
                mSavedImageMatrix.set(mDisplayImageMatrix);
                mSavedImageCropInverse.set(mImageCropInverse);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mTouchMode = TOUCH_MODE_ZOOM;
                mSavedImageMatrix.set(mDisplayImageMatrix);
                mSavedImageCropInverse.set(mImageCropInverse);
                mOriginalDistance = getDistance(event);
                mOriginalDegrees = getDegrees(event);
                getCenter(mEventCenter, event);
                break;
            case MotionEvent.ACTION_MOVE:
                if (mTouchMode == TOUCH_MODE_ZOOM) {
                    mDisplayImageMatrix.set(mSavedImageMatrix);
                    mImageCropInverse.set(mSavedImageCropInverse);
                    float degrees = getDegrees(event) - mOriginalDegrees;
                    float distance = getDistance(event);
                    float scale = distance / mOriginalDistance;
                    mDisplayImageMatrix.postScale(scale, scale, mEventCenter.x, mEventCenter.y);
                    mImageCropInverse.postScale(scale, scale, mEventCenter.x, mEventCenter.y);
                    mDisplayImageMatrix.postRotate(degrees % 360, mEventCenter.x, mEventCenter.y);
                    mImageCropInverse.postRotate((degrees) % 360, mEventCenter.x, mEventCenter.y);
                    invalidate();
                } else if (mTouchMode == TOUCH_MODE_DRAG) {
                    mDisplayImageMatrix.set(mSavedImageMatrix);
                    mImageCropInverse.set(mSavedImageCropInverse);
                    mDisplayImageMatrix.postTranslate(event.getX() - mLastX,
                            event.getY() - mLastY);
                    mImageCropInverse.postTranslate((event.getX() - mLastX),
                            (event.getY() - mLastY));

                    mNewX = event.getX();
                    mNewY = event.getY();

                    Log.d("LastX", String.valueOf(mNewX));
                    Log.d("LastY", String.valueOf(mNewY));
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_POINTER_UP:
                mTouchMode = TOUCH_MODE_NONE;
                break;
        }
        return true;
    }

    private float getDistance(MotionEvent event) {
        return GeometryMathUtils.getDistance(event.getX(0), event.getY(0),
                event.getX(1), event.getY(1));
    }

    private void getCenter(PointF point, MotionEvent event) {
        float x = mLastX + event.getX(1);
        float y = mLastY + event.getY(1);
        point.set(x / 2, y / 2);
    }

    private float getDegrees(MotionEvent event) {
        return GeometryMathUtils.getDegrees(event.getX(0), event.getY(0),
                event.getX(1), event.getY(1));
    }

    // private void reset() {
    // Log.w(LOGTAG, "crop reset called");
    // mCropObj = null;
    // mDisplayImageMatrix = null;
    // mDisplayCropMatrix = null;
    // mRotation = 0;
    // clearDisplay();
    // }

    public void clearDisplay() {
        mDisplayImageMatrix = null;
        mDisplayCropMatrix = null;
        invalidate();
    }

    public void configChanged() {
        mDirty = true;
    }

    @Override
    public void onDraw(Canvas canvas) {

        if (mImage == null || mCropObj == null) {
            return;
        }
        if (mDirty) {
            mDirty = false;
            clearDisplay();
        }

        if (mCanvasRect == null
                || (mCanvasRect.width() != canvas.getWidth() && mCanvasRect
                .height() != canvas.getHeight())) {
            mCanvasRect = new RectF(0, 0, canvas.getWidth(), canvas.getHeight());
            mScreenInCanvas = new RectF(0, 0, canvas.getWidth(),
                    canvas.getHeight());
            mScreenInCanvas.inset(mMarginHorizontal, mMarginVertical);
        }

        // If display matrix doesn't exist, create it and its dependencies
        if (mDisplayImageMatrix == null || mDisplayCropMatrix == null) {
            mDisplayImageMatrix = new Matrix();
            mDisplayImageMatrix.reset();
            if (!CropMath.setImageToScreenMatrix(mDisplayImageMatrix,
                    mCropObj.getImageRect(), mCropObj.getImageOriginalRect(), mScreenInCanvas, mRotation)) {
                Log.e(LOGTAG, "failed to get image matrix");
                mDisplayImageMatrix = null;
                return;
            }
            // backup initial display matrix
            mInitialDisplayImageMatrix = new Matrix(mDisplayImageMatrix);

            mCropInScreen = (mCropInScreen == null ? new RectF()
                    : mCropInScreen);
            mCropObj.getCropRect(mCropInScreen);
            mDisplayCropMatrix = new Matrix();
            mDisplayCropMatrix.reset();
            if (!CropMath.setCropToScreenMatrix(mDisplayCropMatrix,
                    mCropInScreen, mScreenInCanvas)) {
                Log.e(LOGTAG, "failed to get crop matrix");
                mDisplayCropMatrix = null;
                return;
            }
            mDisplayCropMatrix.mapRect(mCropInScreen);

            mImageCropInverse = new Matrix();
        }

        // Draw actual image
        canvas.drawBitmap(mImage, mDisplayImageMatrix, mPaint);
        // Draw overlay shadows
        CropDrawingUtils.drawShadows(canvas, mOverlayPaint, mCropInScreen,
                mCanvasRect);
        // Draw crop rect
        CropDrawingUtils.drawCropRect(canvas, mBorderPaint, mCropInScreen);
    }

    public void maximizeImage(Rect inner, CropView outer, float bitmapWidth, float bitmapHeight) {
        float scale = 2f;

        mSavedImageMatrix.set(mDisplayImageMatrix);
        mSavedImageCropInverse.set(mImageCropInverse);

        mDisplayImageMatrix.set(mSavedImageMatrix);
        mImageCropInverse.set(mSavedImageCropInverse);

        float [] values = new float[9];
        mDisplayImageMatrix.getValues(values);

        scale = 1.1f;//(float) Math.ceil(((float) inner.width() / (float) mImage.getWidth()) * 100) / 100;


        mDisplayImageMatrix.postScale(scale, scale, outer.getWidth()/2, outer.getHeight()/2);
        mImageCropInverse.postScale(scale, scale,  outer.getWidth()/2, outer.getHeight()/2);

        Log.d("scale_size_matrix", String.valueOf(values[Matrix.MSCALE_X]));

//        mDisplayImageMatrix.postScale(scale, scale, outer.getWidth()/2, outer.getHeight()/2);
//        mImageCropInverse.postScale(scale, scale, outer.getWidth()/2, outer.getHeight()/2);

        mNewX = outer.getWidth()/2;
        mNewY = outer.getHeight()/2;

        invalidate();


//        if(inner.width() > inner.height()) {
//            if (bitmapWidth < bitmapHeight) {
//                scale = (float) Math.ceil(((float) inner.width() / (float) mImage.getWidth()) * 100) / 100;
//                mDisplayImageMatrix.postScale(scale, scale, 0,0);
//                mImageCropInverse.postScale(scale, scale, 0, 0);
//
//                Log.d("image_outer_cord", String.valueOf(outer.getX()) + " " + String.valueOf(outer.getY()));
//
//
//                //mImageCropInverse.postScale(scale, scale, bitmapWidth, bitmapHeight);
//
//                float leftOffset = (outer.getWidth() - inner.width()) / 2;
//                float topOffset = (outer.getHeight() - inner.height()) / 2;
//
////                float leftOffset = 0;
////                float topOffset = 0;
////
//                mDisplayImageMatrix.postTranslate(leftOffset, topOffset);
//                mImageCropInverse.postTranslate(leftOffset, topOffset);
//            }
////            else {
////                scale = (float) Math.ceil(((float) inner.width() / (float) mImage.getWidth()) * 100) / 100;
////                mDisplayImageMatrix.postScale(scale, scale, 0, 0);
////                mDisplayCropMatrix.postScale(scale, scale, 0, 0);
////
////                float leftOffset = ((outer.getWidth() - inner.width()) / 2) - 5;
////                float topOffset = (outer.getHeight() - inner.height()) / 2 - (mImage.getHeight() * scale - inner.height()) / 2 + 5;
////
////                mDisplayImageMatrix.postTranslate(leftOffset, topOffset);
////                mDisplayCropMatrix.postTranslate(leftOffset, topOffset);
////            }
//
//        }


//        Log.d("Scale", String.valueOf(scale));
        Log.d("Scale_size", String.valueOf(scale));
        Log.d("Scale_size_bitmap", String.valueOf(bitmapWidth) + " " + String.valueOf(bitmapHeight));
        Log.d("Scale_size_inner", String.valueOf(inner.width()) + " " + String.valueOf(inner.height()));
        Log.d("Scale_size_outer", String.valueOf(outer.getWidth()) + " " + String.valueOf(outer.getHeight()));
        Log.d("Scale_size_mImage", String.valueOf(mImage.getWidth()) + " " + String.valueOf(mImage.getHeight()));

        //invalidate();
    }

}
