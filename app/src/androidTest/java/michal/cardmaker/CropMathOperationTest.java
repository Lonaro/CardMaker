package michal.cardmaker;

import android.graphics.Bitmap;
import android.graphics.RectF;

import org.junit.Test;

import michal.cardmaker.presenter.cropViewLibrary.CropMath;
import static org.junit.Assert.assertEquals;


public class CropMathOperationTest {
    CropMath cropMath = new CropMath();


    @Test
    public void getRotatedTest(){
        float[] rotatedRect = {30, 10, 25, 15};
        float[] center = {15, 5};
        RectF unrotated = new RectF();
        assertEquals(-45f, cropMath.getUnrotated(rotatedRect, center, unrotated), 1);

        float[] rotatedRect2 = {0, 0, 25, 15};
        float[] center2 = {15, 5};
        assertEquals(30f, cropMath.getUnrotated(rotatedRect2, center2, unrotated), 1);
    }

    @Test
    public void inclusiveContainsTest() {
        RectF rectF = new RectF(0,0,10,10);
        assertEquals(true, cropMath.inclusiveContains(rectF, 5, 5));

        rectF = new RectF(10,5,20,10);
        assertEquals(false, cropMath.inclusiveContains(rectF, 0, 30));

        rectF = new RectF(12,18,22,32);
        assertEquals(false, cropMath.inclusiveContains(rectF, 5, 2));

        rectF = new RectF(6,5,16,15);
        assertEquals(false, cropMath.inclusiveContains(rectF, 5, 10));
    }
}
