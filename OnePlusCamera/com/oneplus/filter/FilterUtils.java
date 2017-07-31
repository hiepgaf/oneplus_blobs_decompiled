package com.oneplus.filter;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import com.oneplus.base.Log;
import com.oneplus.base.NativeLibrary;
import com.oneplus.media.LookupTable;

public final class FilterUtils
{
  private static final float[] IDENTITY_COLOR_MATRIX = { 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F };
  private static final String TAG = FilterUtils.class.getSimpleName();
  
  public static Bitmap applyColorMatrix(Bitmap paramBitmap, ColorMatrix paramColorMatrix)
  {
    Bitmap localBitmap = Bitmap.createBitmap(paramBitmap.getWidth(), paramBitmap.getHeight(), paramBitmap.getConfig());
    Paint localPaint = new Paint();
    localPaint.setAntiAlias(true);
    localPaint.setColorFilter(new ColorMatrixColorFilter(paramColorMatrix));
    new Canvas(localBitmap).drawBitmap(paramBitmap, 0.0F, 0.0F, localPaint);
    return localBitmap;
  }
  
  public static boolean applyRGBLUT(Bitmap paramBitmap1, Bitmap paramBitmap2, LookupTable paramLookupTable1, LookupTable paramLookupTable2, LookupTable paramLookupTable3)
  {
    if (!NativeLibrary.load())
    {
      Log.e(TAG, "applyRGBLUT() - Cannot load library");
      return false;
    }
    if (paramBitmap1 == null)
    {
      Log.e(TAG, "applyRGBLUT() - Source image cannot be null");
      return false;
    }
    if (paramBitmap1.getConfig() != Bitmap.Config.ARGB_8888)
    {
      Log.e(TAG, "applyRGBLUT() - Source bitmap config is not supported: " + paramBitmap1.getConfig());
      return false;
    }
    Bitmap localBitmap = paramBitmap2;
    if (paramBitmap2 == null) {
      localBitmap = paramBitmap1;
    }
    if (localBitmap.getConfig() != Bitmap.Config.ARGB_8888)
    {
      Log.e(TAG, "applyRGBLUT() - Destination bitmap config is not supported: " + localBitmap.getConfig());
      return false;
    }
    if ((paramLookupTable1 == null) || (paramLookupTable1.size() < 256))
    {
      Log.e(TAG, "applyRGBLUT() - Red LUT is null");
      return false;
    }
    if ((paramLookupTable2 == null) || (paramLookupTable2.size() < 256))
    {
      Log.e(TAG, "applyRGBLUT() - Green LUT is null");
      return false;
    }
    if ((paramLookupTable3 == null) || (paramLookupTable3.size() < 256))
    {
      Log.e(TAG, "applyRGBLUT() - Blue LUT is null");
      return false;
    }
    if (!applyRGBTableNative(paramBitmap1, localBitmap, paramLookupTable1.array(), paramLookupTable2.array(), paramLookupTable3.array()))
    {
      Log.e(TAG, "applyRGBTable() - Error to apply RGB table");
      return false;
    }
    return true;
  }
  
  private static native boolean applyRGBTableNative(Bitmap paramBitmap1, Bitmap paramBitmap2, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int[] paramArrayOfInt3);
  
  public static void interpolateColorMatrices(ColorMatrix paramColorMatrix1, ColorMatrix paramColorMatrix2, ColorMatrix paramColorMatrix3, float paramFloat)
  {
    if ((paramColorMatrix1 == null) || (paramColorMatrix3 == null)) {
      return;
    }
    if (paramColorMatrix1 != null) {}
    float[] arrayOfFloat;
    for (paramColorMatrix1 = paramColorMatrix1.getArray();; paramColorMatrix1 = IDENTITY_COLOR_MATRIX)
    {
      paramColorMatrix2 = paramColorMatrix2.getArray();
      arrayOfFloat = paramColorMatrix3.getArray();
      int i = 19;
      while (i >= 0)
      {
        float f1 = paramColorMatrix2[i];
        float f2 = paramColorMatrix1[i];
        paramColorMatrix1[i] += (f1 - f2) * paramFloat;
        i -= 1;
      }
    }
    paramColorMatrix3.set(arrayOfFloat);
  }
  
  public static boolean isIdentityColorMatrix(ColorMatrix paramColorMatrix)
  {
    paramColorMatrix = paramColorMatrix.getArray();
    int i = 0;
    while (i < paramColorMatrix.length)
    {
      if (paramColorMatrix[i] != IDENTITY_COLOR_MATRIX[i]) {
        return false;
      }
      i += 1;
    }
    return true;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/filter/FilterUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */