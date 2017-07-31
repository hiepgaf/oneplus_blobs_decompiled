package android.hardware.camera2.params;

import android.graphics.PointF;
import android.hardware.camera2.utils.HashCodeHelpers;
import com.android.internal.util.Preconditions;
import java.util.Arrays;

public final class TonemapCurve
{
  public static final int CHANNEL_BLUE = 2;
  public static final int CHANNEL_GREEN = 1;
  public static final int CHANNEL_RED = 0;
  public static final float LEVEL_BLACK = 0.0F;
  public static final float LEVEL_WHITE = 1.0F;
  private static final int MIN_CURVE_LENGTH = 4;
  private static final int OFFSET_POINT_IN = 0;
  private static final int OFFSET_POINT_OUT = 1;
  public static final int POINT_SIZE = 2;
  private static final int TONEMAP_MIN_CURVE_POINTS = 2;
  private final float[] mBlue;
  private final float[] mGreen;
  private boolean mHashCalculated = false;
  private int mHashCode;
  private final float[] mRed;
  
  public TonemapCurve(float[] paramArrayOfFloat1, float[] paramArrayOfFloat2, float[] paramArrayOfFloat3)
  {
    Preconditions.checkNotNull(paramArrayOfFloat1, "red must not be null");
    Preconditions.checkNotNull(paramArrayOfFloat2, "green must not be null");
    Preconditions.checkNotNull(paramArrayOfFloat3, "blue must not be null");
    checkArgumentArrayLengthDivisibleBy(paramArrayOfFloat1, 2, "red");
    checkArgumentArrayLengthDivisibleBy(paramArrayOfFloat2, 2, "green");
    checkArgumentArrayLengthDivisibleBy(paramArrayOfFloat3, 2, "blue");
    checkArgumentArrayLengthNoLessThan(paramArrayOfFloat1, 4, "red");
    checkArgumentArrayLengthNoLessThan(paramArrayOfFloat2, 4, "green");
    checkArgumentArrayLengthNoLessThan(paramArrayOfFloat3, 4, "blue");
    Preconditions.checkArrayElementsInRange(paramArrayOfFloat1, 0.0F, 1.0F, "red");
    Preconditions.checkArrayElementsInRange(paramArrayOfFloat2, 0.0F, 1.0F, "green");
    Preconditions.checkArrayElementsInRange(paramArrayOfFloat3, 0.0F, 1.0F, "blue");
    this.mRed = Arrays.copyOf(paramArrayOfFloat1, paramArrayOfFloat1.length);
    this.mGreen = Arrays.copyOf(paramArrayOfFloat2, paramArrayOfFloat2.length);
    this.mBlue = Arrays.copyOf(paramArrayOfFloat3, paramArrayOfFloat3.length);
  }
  
  private static void checkArgumentArrayLengthDivisibleBy(float[] paramArrayOfFloat, int paramInt, String paramString)
  {
    if (paramArrayOfFloat.length % paramInt != 0) {
      throw new IllegalArgumentException(paramString + " size must be divisible by " + paramInt);
    }
  }
  
  private static void checkArgumentArrayLengthNoLessThan(float[] paramArrayOfFloat, int paramInt, String paramString)
  {
    if (paramArrayOfFloat.length < paramInt) {
      throw new IllegalArgumentException(paramString + " size must be at least " + paramInt);
    }
  }
  
  private static int checkArgumentColorChannel(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      throw new IllegalArgumentException("colorChannel out of range");
    }
    return paramInt;
  }
  
  private String curveToString(int paramInt)
  {
    checkArgumentColorChannel(paramInt);
    StringBuilder localStringBuilder = new StringBuilder("[");
    float[] arrayOfFloat = getCurve(paramInt);
    int j = arrayOfFloat.length / 2;
    int i = 0;
    paramInt = 0;
    while (i < j)
    {
      localStringBuilder.append("(");
      localStringBuilder.append(arrayOfFloat[paramInt]);
      localStringBuilder.append(", ");
      localStringBuilder.append(arrayOfFloat[(paramInt + 1)]);
      localStringBuilder.append("), ");
      i += 1;
      paramInt += 2;
    }
    localStringBuilder.setLength(localStringBuilder.length() - 2);
    localStringBuilder.append("]");
    return localStringBuilder.toString();
  }
  
  private float[] getCurve(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      throw new AssertionError("colorChannel out of range");
    case 0: 
      return this.mRed;
    case 1: 
      return this.mGreen;
    }
    return this.mBlue;
  }
  
  public void copyColorCurve(int paramInt1, float[] paramArrayOfFloat, int paramInt2)
  {
    Preconditions.checkArgumentNonnegative(paramInt2, "offset must not be negative");
    Preconditions.checkNotNull(paramArrayOfFloat, "destination must not be null");
    if (paramArrayOfFloat.length + paramInt2 < getPointCount(paramInt1) * 2) {
      throw new ArrayIndexOutOfBoundsException("destination too small to fit elements");
    }
    float[] arrayOfFloat = getCurve(paramInt1);
    System.arraycopy(arrayOfFloat, 0, paramArrayOfFloat, paramInt2, arrayOfFloat.length);
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool2 = false;
    if (paramObject == null) {
      return false;
    }
    if (this == paramObject) {
      return true;
    }
    if ((paramObject instanceof TonemapCurve))
    {
      paramObject = (TonemapCurve)paramObject;
      boolean bool1 = bool2;
      if (Arrays.equals(this.mRed, ((TonemapCurve)paramObject).mRed))
      {
        bool1 = bool2;
        if (Arrays.equals(this.mGreen, ((TonemapCurve)paramObject).mGreen)) {
          bool1 = Arrays.equals(this.mBlue, ((TonemapCurve)paramObject).mBlue);
        }
      }
      return bool1;
    }
    return false;
  }
  
  public PointF getPoint(int paramInt1, int paramInt2)
  {
    checkArgumentColorChannel(paramInt1);
    if ((paramInt2 < 0) || (paramInt2 >= getPointCount(paramInt1))) {
      throw new IllegalArgumentException("index out of range");
    }
    float[] arrayOfFloat = getCurve(paramInt1);
    return new PointF(arrayOfFloat[(paramInt2 * 2 + 0)], arrayOfFloat[(paramInt2 * 2 + 1)]);
  }
  
  public int getPointCount(int paramInt)
  {
    checkArgumentColorChannel(paramInt);
    return getCurve(paramInt).length / 2;
  }
  
  public int hashCode()
  {
    if (this.mHashCalculated) {
      return this.mHashCode;
    }
    this.mHashCode = HashCodeHelpers.hashCodeGeneric(new float[][] { this.mRed, this.mGreen, this.mBlue });
    this.mHashCalculated = true;
    return this.mHashCode;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder("TonemapCurve{");
    localStringBuilder.append("R:");
    localStringBuilder.append(curveToString(0));
    localStringBuilder.append(", G:");
    localStringBuilder.append(curveToString(1));
    localStringBuilder.append(", B:");
    localStringBuilder.append(curveToString(2));
    localStringBuilder.append("}");
    return localStringBuilder.toString();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/params/TonemapCurve.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */