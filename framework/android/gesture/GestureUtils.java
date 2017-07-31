package android.gesture;

import android.graphics.RectF;
import android.util.Log;
import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public final class GestureUtils
{
  private static final float NONUNIFORM_SCALE = (float)Math.sqrt(2.0D);
  private static final float SCALING_THRESHOLD = 0.26F;
  
  static void closeStream(Closeable paramCloseable)
  {
    if (paramCloseable != null) {}
    try
    {
      paramCloseable.close();
      return;
    }
    catch (IOException paramCloseable)
    {
      Log.e("Gestures", "Could not close stream", paramCloseable);
    }
  }
  
  static float[] computeCentroid(float[] paramArrayOfFloat)
  {
    float f2 = 0.0F;
    float f1 = 0.0F;
    int j = paramArrayOfFloat.length;
    int i = 0;
    while (i < j)
    {
      f2 += paramArrayOfFloat[i];
      i += 1;
      f1 += paramArrayOfFloat[i];
      i += 1;
    }
    return new float[] { 2.0F * f2 / j, 2.0F * f1 / j };
  }
  
  private static float[][] computeCoVariance(float[] paramArrayOfFloat)
  {
    float[][] arrayOfFloat = (float[][])Array.newInstance(Float.TYPE, new int[] { 2, 2 });
    arrayOfFloat[0][0] = 0;
    arrayOfFloat[0][1] = 0;
    arrayOfFloat[1][0] = 0;
    arrayOfFloat[1][1] = 0;
    int j = paramArrayOfFloat.length;
    int i = 0;
    while (i < j)
    {
      float f1 = paramArrayOfFloat[i];
      i += 1;
      float f2 = paramArrayOfFloat[i];
      float[] arrayOfFloat1 = arrayOfFloat[0];
      arrayOfFloat1[0] += f1 * f1;
      arrayOfFloat1 = arrayOfFloat[0];
      arrayOfFloat1[1] += f1 * f2;
      arrayOfFloat[1][0] = arrayOfFloat[0][1];
      arrayOfFloat1 = arrayOfFloat[1];
      arrayOfFloat1[1] += f2 * f2;
      i += 1;
    }
    paramArrayOfFloat = arrayOfFloat[0];
    paramArrayOfFloat[0] /= j / 2;
    paramArrayOfFloat = arrayOfFloat[0];
    paramArrayOfFloat[1] /= j / 2;
    paramArrayOfFloat = arrayOfFloat[1];
    paramArrayOfFloat[0] /= j / 2;
    paramArrayOfFloat = arrayOfFloat[1];
    paramArrayOfFloat[1] /= j / 2;
    return arrayOfFloat;
  }
  
  private static float[] computeOrientation(float[][] paramArrayOfFloat)
  {
    float[] arrayOfFloat = new float[2];
    if ((paramArrayOfFloat[0][1] == 0.0F) || (paramArrayOfFloat[1][0] == 0.0F))
    {
      arrayOfFloat[0] = 1.0F;
      arrayOfFloat[1] = 0.0F;
    }
    float f5 = -paramArrayOfFloat[0][0];
    float f6 = paramArrayOfFloat[1][1];
    float f1 = paramArrayOfFloat[0][0];
    float f2 = paramArrayOfFloat[1][1];
    float f3 = paramArrayOfFloat[0][1];
    float f4 = paramArrayOfFloat[1][0];
    f5 = (f5 - f6) / 2.0F;
    f2 = (float)Math.sqrt(Math.pow(f5, 2.0D) - (f1 * f2 - f3 * f4));
    f1 = -f5 + f2;
    f2 = -f5 - f2;
    if (f1 == f2)
    {
      arrayOfFloat[0] = 0.0F;
      arrayOfFloat[1] = 0.0F;
      return arrayOfFloat;
    }
    if (f1 > f2) {}
    for (;;)
    {
      arrayOfFloat[0] = 1.0F;
      arrayOfFloat[1] = ((f1 - paramArrayOfFloat[0][0]) / paramArrayOfFloat[0][1]);
      return arrayOfFloat;
      f1 = f2;
    }
  }
  
  public static OrientedBoundingBox computeOrientedBoundingBox(ArrayList<GesturePoint> paramArrayList)
  {
    int j = paramArrayList.size();
    float[] arrayOfFloat = new float[j * 2];
    int i = 0;
    while (i < j)
    {
      GesturePoint localGesturePoint = (GesturePoint)paramArrayList.get(i);
      int k = i * 2;
      arrayOfFloat[k] = localGesturePoint.x;
      arrayOfFloat[(k + 1)] = localGesturePoint.y;
      i += 1;
    }
    return computeOrientedBoundingBox(arrayOfFloat, computeCentroid(arrayOfFloat));
  }
  
  public static OrientedBoundingBox computeOrientedBoundingBox(float[] paramArrayOfFloat)
  {
    int j = paramArrayOfFloat.length;
    float[] arrayOfFloat = new float[j];
    int i = 0;
    while (i < j)
    {
      arrayOfFloat[i] = paramArrayOfFloat[i];
      i += 1;
    }
    return computeOrientedBoundingBox(arrayOfFloat, computeCentroid(arrayOfFloat));
  }
  
  private static OrientedBoundingBox computeOrientedBoundingBox(float[] paramArrayOfFloat1, float[] paramArrayOfFloat2)
  {
    translate(paramArrayOfFloat1, -paramArrayOfFloat2[0], -paramArrayOfFloat2[1]);
    float[] arrayOfFloat = computeOrientation(computeCoVariance(paramArrayOfFloat1));
    float f1;
    if ((arrayOfFloat[0] == 0.0F) && (arrayOfFloat[1] == 0.0F)) {
      f1 = -1.5707964F;
    }
    float f6;
    float f3;
    float f5;
    float f2;
    for (;;)
    {
      f6 = Float.MAX_VALUE;
      f3 = Float.MAX_VALUE;
      f5 = Float.MIN_VALUE;
      f2 = Float.MIN_VALUE;
      int j = paramArrayOfFloat1.length;
      int i = 0;
      while (i < j)
      {
        float f4 = f6;
        if (paramArrayOfFloat1[i] < f6) {
          f4 = paramArrayOfFloat1[i];
        }
        f6 = f5;
        if (paramArrayOfFloat1[i] > f5) {
          f6 = paramArrayOfFloat1[i];
        }
        i += 1;
        float f7 = f3;
        if (paramArrayOfFloat1[i] < f3) {
          f7 = paramArrayOfFloat1[i];
        }
        f3 = f2;
        if (paramArrayOfFloat1[i] > f2) {
          f3 = paramArrayOfFloat1[i];
        }
        i += 1;
        f5 = f6;
        f2 = f3;
        f6 = f4;
        f3 = f7;
      }
      f1 = (float)Math.atan2(arrayOfFloat[1], arrayOfFloat[0]);
      rotate(paramArrayOfFloat1, -f1);
    }
    return new OrientedBoundingBox((float)(180.0F * f1 / 3.141592653589793D), paramArrayOfFloat2[0], paramArrayOfFloat2[1], f5 - f6, f2 - f3);
  }
  
  static float computeStraightness(float[] paramArrayOfFloat)
  {
    float f1 = computeTotalLength(paramArrayOfFloat);
    float f2 = paramArrayOfFloat[2];
    float f3 = paramArrayOfFloat[0];
    float f4 = paramArrayOfFloat[3];
    float f5 = paramArrayOfFloat[1];
    return (float)Math.hypot(f2 - f3, f4 - f5) / f1;
  }
  
  static float computeStraightness(float[] paramArrayOfFloat, float paramFloat)
  {
    float f1 = paramArrayOfFloat[2];
    float f2 = paramArrayOfFloat[0];
    float f3 = paramArrayOfFloat[3];
    float f4 = paramArrayOfFloat[1];
    return (float)Math.hypot(f1 - f2, f3 - f4) / paramFloat;
  }
  
  static float computeTotalLength(float[] paramArrayOfFloat)
  {
    float f1 = 0.0F;
    int j = paramArrayOfFloat.length;
    int i = 0;
    while (i < j - 4)
    {
      float f2 = paramArrayOfFloat[(i + 2)];
      float f3 = paramArrayOfFloat[i];
      float f4 = paramArrayOfFloat[(i + 3)];
      float f5 = paramArrayOfFloat[(i + 1)];
      f1 = (float)(f1 + Math.hypot(f2 - f3, f4 - f5));
      i += 2;
    }
    return f1;
  }
  
  static float cosineDistance(float[] paramArrayOfFloat1, float[] paramArrayOfFloat2)
  {
    float f = 0.0F;
    int j = paramArrayOfFloat1.length;
    int i = 0;
    while (i < j)
    {
      f += paramArrayOfFloat1[i] * paramArrayOfFloat2[i];
      i += 1;
    }
    return (float)Math.acos(f);
  }
  
  static float minimumCosineDistance(float[] paramArrayOfFloat1, float[] paramArrayOfFloat2, int paramInt)
  {
    int j = paramArrayOfFloat1.length;
    float f2 = 0.0F;
    float f1 = 0.0F;
    int i = 0;
    while (i < j)
    {
      f2 += paramArrayOfFloat1[i] * paramArrayOfFloat2[i] + paramArrayOfFloat1[(i + 1)] * paramArrayOfFloat2[(i + 1)];
      f1 += paramArrayOfFloat1[i] * paramArrayOfFloat2[(i + 1)] - paramArrayOfFloat1[(i + 1)] * paramArrayOfFloat2[i];
      i += 2;
    }
    if (f2 != 0.0F)
    {
      float f3 = f1 / f2;
      double d1 = Math.atan(f3);
      if ((paramInt > 2) && (Math.abs(d1) >= 3.141592653589793D / paramInt)) {
        return (float)Math.acos(f2);
      }
      d1 = Math.cos(d1);
      double d2 = f3;
      return (float)Math.acos(f2 * d1 + f1 * (d1 * d2));
    }
    return 1.5707964F;
  }
  
  private static void plot(float paramFloat1, float paramFloat2, float[] paramArrayOfFloat, int paramInt)
  {
    float f1 = paramFloat1;
    if (paramFloat1 < 0.0F) {
      f1 = 0.0F;
    }
    paramFloat1 = paramFloat2;
    if (paramFloat2 < 0.0F) {
      paramFloat1 = 0.0F;
    }
    int k = (int)Math.floor(f1);
    int i = (int)Math.ceil(f1);
    int m = (int)Math.floor(paramFloat1);
    int j = (int)Math.ceil(paramFloat1);
    if ((f1 == k) && (paramFloat1 == m))
    {
      paramInt = j * paramInt + i;
      if (paramArrayOfFloat[paramInt] < 1.0F) {
        paramArrayOfFloat[paramInt] = 1.0F;
      }
    }
    do
    {
      return;
      double d1 = Math.pow(k - f1, 2.0D);
      double d2 = Math.pow(m - paramFloat1, 2.0D);
      double d3 = Math.pow(i - f1, 2.0D);
      double d4 = Math.pow(j - paramFloat1, 2.0D);
      float f3 = (float)Math.sqrt(d1 + d2);
      float f2 = (float)Math.sqrt(d3 + d2);
      f1 = (float)Math.sqrt(d1 + d4);
      paramFloat1 = (float)Math.sqrt(d3 + d4);
      paramFloat2 = f3 + f2 + f1 + paramFloat1;
      f3 /= paramFloat2;
      int n = m * paramInt + k;
      if (f3 > paramArrayOfFloat[n]) {
        paramArrayOfFloat[n] = f3;
      }
      f2 /= paramFloat2;
      m = m * paramInt + i;
      if (f2 > paramArrayOfFloat[m]) {
        paramArrayOfFloat[m] = f2;
      }
      f1 /= paramFloat2;
      k = j * paramInt + k;
      if (f1 > paramArrayOfFloat[k]) {
        paramArrayOfFloat[k] = f1;
      }
      paramFloat1 /= paramFloat2;
      paramInt = j * paramInt + i;
    } while (paramFloat1 <= paramArrayOfFloat[paramInt]);
    paramArrayOfFloat[paramInt] = paramFloat1;
  }
  
  static float[] rotate(float[] paramArrayOfFloat, float paramFloat)
  {
    float f1 = (float)Math.cos(paramFloat);
    paramFloat = (float)Math.sin(paramFloat);
    int j = paramArrayOfFloat.length;
    int i = 0;
    while (i < j)
    {
      float f2 = paramArrayOfFloat[i];
      float f3 = paramArrayOfFloat[(i + 1)];
      float f4 = paramArrayOfFloat[i];
      float f5 = paramArrayOfFloat[(i + 1)];
      paramArrayOfFloat[i] = (f2 * f1 - f3 * paramFloat);
      paramArrayOfFloat[(i + 1)] = (f4 * paramFloat + f5 * f1);
      i += 2;
    }
    return paramArrayOfFloat;
  }
  
  static float[] scale(float[] paramArrayOfFloat, float paramFloat1, float paramFloat2)
  {
    int j = paramArrayOfFloat.length;
    int i = 0;
    while (i < j)
    {
      paramArrayOfFloat[i] *= paramFloat1;
      int k = i + 1;
      paramArrayOfFloat[k] *= paramFloat2;
      i += 2;
    }
    return paramArrayOfFloat;
  }
  
  public static float[] spatialSampling(Gesture paramGesture, int paramInt)
  {
    return spatialSampling(paramGesture, paramInt, false);
  }
  
  public static float[] spatialSampling(Gesture paramGesture, int paramInt, boolean paramBoolean)
  {
    float f6 = paramInt - 1;
    float[] arrayOfFloat1 = new float[paramInt * paramInt];
    Arrays.fill(arrayOfFloat1, 0.0F);
    Object localObject = paramGesture.getBoundingBox();
    float f1 = ((RectF)localObject).width();
    float f2 = ((RectF)localObject).height();
    float f3 = f6 / f1;
    float f4 = f6 / f2;
    label75:
    float f9;
    float f10;
    float f11;
    float f12;
    int k;
    int i;
    if (paramBoolean) {
      if (f3 < f4)
      {
        f1 = f3;
        f3 = f1;
        f2 = f1;
        f1 = f3;
        f9 = -((RectF)localObject).centerX();
        f10 = -((RectF)localObject).centerY();
        f11 = f6 / 2.0F;
        f12 = f6 / 2.0F;
        paramGesture = paramGesture.getStrokes();
        k = paramGesture.size();
        i = 0;
      }
    }
    for (;;)
    {
      if (i >= k) {
        break label758;
      }
      localObject = ((GestureStroke)paramGesture.get(i)).points;
      int m = localObject.length;
      float[] arrayOfFloat2 = new float[m];
      int j = 0;
      float f5;
      for (;;)
      {
        if (j < m)
        {
          arrayOfFloat2[j] = ((localObject[j] + f9) * f1 + f11);
          arrayOfFloat2[(j + 1)] = ((localObject[(j + 1)] + f10) * f2 + f12);
          j += 2;
          continue;
          f1 = f4;
          break;
          f2 = f1 / f2;
          f1 = f2;
          if (f2 > 1.0F) {
            f1 = 1.0F / f2;
          }
          if (f1 < 0.26F)
          {
            if (f3 < f4) {}
            for (f1 = f3;; f1 = f4)
            {
              f2 = f1;
              f3 = f1;
              f1 = f2;
              f2 = f3;
              break;
            }
          }
          if (f3 > f4)
          {
            f5 = f4 * NONUNIFORM_SCALE;
            f1 = f3;
            f2 = f4;
            if (f5 >= f3) {
              break label75;
            }
            f1 = f5;
            f2 = f4;
            break label75;
          }
          f5 = f3 * NONUNIFORM_SCALE;
          f1 = f3;
          f2 = f4;
          if (f5 >= f4) {
            break label75;
          }
          f2 = f5;
          f1 = f3;
          break label75;
        }
      }
      float f8 = -1.0F;
      float f7 = -1.0F;
      j = 0;
      while (j < m)
      {
        if (arrayOfFloat2[j] < 0.0F)
        {
          f4 = 0.0F;
          if (arrayOfFloat2[(j + 1)] >= 0.0F) {
            break label522;
          }
        }
        float f13;
        label522:
        for (f5 = 0.0F;; f5 = arrayOfFloat2[(j + 1)])
        {
          f3 = f4;
          if (f4 > f6) {
            f3 = f6;
          }
          f4 = f5;
          if (f5 > f6) {
            f4 = f6;
          }
          plot(f3, f4, arrayOfFloat1, paramInt);
          if (f8 == -1.0F) {
            break label732;
          }
          if (f8 <= f3) {
            break label534;
          }
          f5 = (float)Math.ceil(f3);
          f13 = (f7 - f4) / (f8 - f3);
          while (f5 < f8)
          {
            plot(f5, (f5 - f3) * f13 + f4, arrayOfFloat1, paramInt);
            f5 += 1.0F;
          }
          f4 = arrayOfFloat2[j];
          break;
        }
        label534:
        if (f8 < f3)
        {
          f5 = (float)Math.ceil(f8);
          f13 = (f7 - f4) / (f8 - f3);
          while (f5 < f3)
          {
            plot(f5, (f5 - f3) * f13 + f4, arrayOfFloat1, paramInt);
            f5 += 1.0F;
          }
        }
        if (f7 > f4)
        {
          f5 = (float)Math.ceil(f4);
          f8 = (f8 - f3) / (f7 - f4);
          while (f5 < f7)
          {
            plot((f5 - f4) * f8 + f3, f5, arrayOfFloat1, paramInt);
            f5 += 1.0F;
          }
        }
        if (f7 < f4)
        {
          f5 = (float)Math.ceil(f7);
          f7 = (f8 - f3) / (f7 - f4);
          while (f5 < f4)
          {
            plot((f5 - f4) * f7 + f3, f5, arrayOfFloat1, paramInt);
            f5 += 1.0F;
          }
        }
        label732:
        j += 2;
        f8 = f3;
        f7 = f4;
      }
      i += 1;
    }
    label758:
    return arrayOfFloat1;
  }
  
  static float squaredEuclideanDistance(float[] paramArrayOfFloat1, float[] paramArrayOfFloat2)
  {
    float f1 = 0.0F;
    int j = paramArrayOfFloat1.length;
    int i = 0;
    while (i < j)
    {
      float f2 = paramArrayOfFloat1[i] - paramArrayOfFloat2[i];
      f1 += f2 * f2;
      i += 1;
    }
    return f1 / j;
  }
  
  public static float[] temporalSampling(GestureStroke paramGestureStroke, int paramInt)
  {
    float f7 = paramGestureStroke.length / (paramInt - 1);
    int k = paramInt * 2;
    float[] arrayOfFloat = new float[k];
    float f1 = 0.0F;
    paramGestureStroke = paramGestureStroke.points;
    float f2 = paramGestureStroke[0];
    float f3 = paramGestureStroke[1];
    float f6 = Float.MIN_VALUE;
    float f4 = Float.MIN_VALUE;
    arrayOfFloat[0] = f2;
    arrayOfFloat[1] = f3;
    paramInt = 1 + 1;
    int j = 0;
    int m = paramGestureStroke.length / 2;
    for (;;)
    {
      int i;
      if (j < m)
      {
        f5 = f6;
        i = j;
        if (f6 != Float.MIN_VALUE) {
          break label150;
        }
        i = j + 1;
        if (i < m) {}
      }
      else
      {
        while (paramInt < k)
        {
          arrayOfFloat[paramInt] = f2;
          arrayOfFloat[(paramInt + 1)] = f3;
          paramInt += 2;
        }
      }
      float f5 = paramGestureStroke[(i * 2)];
      f4 = paramGestureStroke[(i * 2 + 1)];
      label150:
      float f9 = f5 - f2;
      f6 = f4 - f3;
      float f8 = (float)Math.hypot(f9, f6);
      if (f1 + f8 >= f7)
      {
        f1 = (f7 - f1) / f8;
        f2 += f1 * f9;
        f3 += f1 * f6;
        arrayOfFloat[paramInt] = f2;
        paramInt += 1;
        arrayOfFloat[paramInt] = f3;
        paramInt += 1;
        f1 = 0.0F;
        f6 = f5;
        j = i;
      }
      else
      {
        f2 = f5;
        f3 = f4;
        f6 = Float.MIN_VALUE;
        f4 = Float.MIN_VALUE;
        f1 += f8;
        j = i;
      }
    }
    return arrayOfFloat;
  }
  
  static float[] translate(float[] paramArrayOfFloat, float paramFloat1, float paramFloat2)
  {
    int j = paramArrayOfFloat.length;
    int i = 0;
    while (i < j)
    {
      paramArrayOfFloat[i] += paramFloat1;
      int k = i + 1;
      paramArrayOfFloat[k] += paramFloat2;
      i += 2;
    }
    return paramArrayOfFloat;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/gesture/GestureUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */