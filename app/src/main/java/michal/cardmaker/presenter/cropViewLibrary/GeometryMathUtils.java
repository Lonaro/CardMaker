package michal.cardmaker.presenter.cropViewLibrary;

import android.annotation.SuppressLint;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;

public class GeometryMathUtils {
    public static final float SHOW_SCALE = .9f;

    private GeometryMathUtils() {
    }

    ;

    // Math operations for 2d vectors
    public static float clamp(float i, float low, float high) {
        return Math.max(Math.min(i, high), low);
    }

    public static float[] lineIntersect(float[] line1, float[] line2) {
        float a0 = line1[0];
        float a1 = line1[1];
        float b0 = line1[2];
        float b1 = line1[3];
        float c0 = line2[0];
        float c1 = line2[1];
        float d0 = line2[2];
        float d1 = line2[3];
        float t0 = a0 - b0;
        float t1 = a1 - b1;
        float t2 = b0 - d0;
        float t3 = d1 - b1;
        float t4 = c0 - d0;
        float t5 = c1 - d1;

        float denom = t1 * t4 - t0 * t5;
        if (denom == 0)
            return null;
        float u = (t3 * t4 + t5 * t2) / denom;
        float[] intersect = {b0 + u * t0, b1 + u * t1};
        return intersect;
    }

    public static float[] shortestVectorFromPointToLine(float[] point,
                                                        float[] line) {
        float x1 = line[0];
        float x2 = line[2];
        float y1 = line[1];
        float y2 = line[3];
        float xdelt = x2 - x1;
        float ydelt = y2 - y1;
        if (xdelt == 0 && ydelt == 0)
            return null;
        float u = ((point[0] - x1) * xdelt + (point[1] - y1) * ydelt)
                / (xdelt * xdelt + ydelt * ydelt);
        float[] ret = {(x1 + u * (x2 - x1)), (y1 + u * (y2 - y1))};
        float[] vec = {ret[0] - point[0], ret[1] - point[1]};
        return vec;
    }

    // A . B
    public static float dotProduct(float[] a, float[] b) {
        return a[0] * b[0] + a[1] * b[1];
    }

    public static float[] normalize(float[] a) {
        float length = (float) Math.sqrt(a[0] * a[0] + a[1] * a[1]);
        float[] b = {a[0] / length, a[1] / length};
        return b;
    }

    // A onto B
    public static float scalarProjection(float[] a, float[] b) {
        float length = (float) Math.sqrt(b[0] * b[0] + b[1] * b[1]);
        return dotProduct(a, b) / length;
    }

    public static float[] getVectorFromPoints(float[] point1, float[] point2) {
        float[] p = {point2[0] - point1[0], point2[1] - point1[1]};
        return p;
    }

    public static float[] getUnitVectorFromPoints(float[] point1, float[] point2) {
        float[] p = {point2[0] - point1[0], point2[1] - point1[1]};
        float length = (float) Math.sqrt(p[0] * p[0] + p[1] * p[1]);
        p[0] = p[0] / length;
        p[1] = p[1] / length;
        return p;
    }

    public static void scaleRect(RectF r, float scale) {
        r.set(r.left * scale, r.top * scale, r.right * scale, r.bottom * scale);
    }

    // A - B
    public static float[] vectorSubtract(float[] a, float[] b) {
        int len = a.length;
        if (len != b.length)
            return null;
        float[] ret = new float[len];
        for (int i = 0; i < len; i++) {
            ret[i] = a[i] - b[i];
        }
        return ret;
    }

    public static float vectorLength(float[] a) {
        return (float) Math.sqrt(a[0] * a[0] + a[1] * a[1]);
    }

    public static float scale(float oldWidth, float oldHeight, float newWidth,
                              float newHeight) {
        if (oldHeight == 0 || oldWidth == 0
                || (oldWidth == newWidth && oldHeight == newHeight)) {
            return 1;
        }
        return Math.min(newWidth / oldWidth, newHeight / oldHeight);
    }

    public static Rect roundNearest(RectF r) {
        Rect q = new Rect(Math.round(r.left), Math.round(r.top),
                Math.round(r.right), Math.round(r.bottom));
        return q;
    }

    @SuppressLint("FloatMath")
    public static float getDistance(float x1, float y1, float x2, float y2) {
        float x = x1 - x2;
        float y = y1 - y2;
        return (float) Math.sqrt(x * x + y * y);
    }

    public static float getDegrees(float x1, float y1, float x2, float y2) {
        double deltaX = x1 - x2;
        double deltaY = y1 - y2;
        double radians = Math.atan2(deltaY, deltaX);
        return (float) Math.toDegrees(radians);
    }

    public static float getCalculateRealScale(Matrix matrix) {
        float[] v = new float[9];
        matrix.getValues(v);
        float scalex = v[Matrix.MSCALE_X];
        float skewy = v[Matrix.MSKEW_Y];
        float rScale = (float) Math.sqrt(scalex * scalex + skewy * skewy);

        return rScale;
    }

    public static float getCalculateRealAngle(Matrix matrix) {
        float[] v = new float[9];
        matrix.getValues(v);
        float scalex = v[Matrix.MSCALE_X];
        float skewx = v[Matrix.MSKEW_X];

        float rAngle = Math.round(Math.atan2(skewx, scalex) * (180 / Math.PI));
        return rAngle;
    }

}
