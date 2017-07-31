package android.hardware.camera2.params;

import android.hardware.camera2.utils.HashCodeHelpers;
import android.util.Rational;
import com.android.internal.util.Preconditions;
import java.util.Arrays;

public final class ColorSpaceTransform
{
  private static final int COLUMNS = 3;
  private static final int COUNT = 9;
  private static final int COUNT_INT = 18;
  private static final int OFFSET_DENOMINATOR = 1;
  private static final int OFFSET_NUMERATOR = 0;
  private static final int RATIONAL_SIZE = 2;
  private static final int ROWS = 3;
  private final int[] mElements;
  
  public ColorSpaceTransform(int[] paramArrayOfInt)
  {
    Preconditions.checkNotNull(paramArrayOfInt, "elements must not be null");
    if (paramArrayOfInt.length != 18) {
      throw new IllegalArgumentException("elements must be 18 length");
    }
    int i = 0;
    while (i < paramArrayOfInt.length)
    {
      Preconditions.checkNotNull(paramArrayOfInt, "element " + i + " must not be null");
      i += 1;
    }
    this.mElements = Arrays.copyOf(paramArrayOfInt, paramArrayOfInt.length);
  }
  
  public ColorSpaceTransform(Rational[] paramArrayOfRational)
  {
    Preconditions.checkNotNull(paramArrayOfRational, "elements must not be null");
    if (paramArrayOfRational.length != 9) {
      throw new IllegalArgumentException("elements must be 9 length");
    }
    this.mElements = new int[18];
    int i = 0;
    while (i < paramArrayOfRational.length)
    {
      Preconditions.checkNotNull(paramArrayOfRational, "element[" + i + "] must not be null");
      this.mElements[(i * 2 + 0)] = paramArrayOfRational[i].getNumerator();
      this.mElements[(i * 2 + 1)] = paramArrayOfRational[i].getDenominator();
      i += 1;
    }
  }
  
  private String toShortString()
  {
    StringBuilder localStringBuilder = new StringBuilder("(");
    int i = 0;
    int j = 0;
    while (i < 3)
    {
      localStringBuilder.append("[");
      int k = 0;
      while (k < 3)
      {
        int m = this.mElements[(j + 0)];
        int n = this.mElements[(j + 1)];
        localStringBuilder.append(m);
        localStringBuilder.append("/");
        localStringBuilder.append(n);
        if (k < 2) {
          localStringBuilder.append(", ");
        }
        k += 1;
        j += 2;
      }
      localStringBuilder.append("]");
      if (i < 2) {
        localStringBuilder.append(", ");
      }
      i += 1;
    }
    localStringBuilder.append(")");
    return localStringBuilder.toString();
  }
  
  public void copyElements(int[] paramArrayOfInt, int paramInt)
  {
    Preconditions.checkArgumentNonnegative(paramInt, "offset must not be negative");
    Preconditions.checkNotNull(paramArrayOfInt, "destination must not be null");
    if (paramArrayOfInt.length - paramInt < 18) {
      throw new ArrayIndexOutOfBoundsException("destination too small to fit elements");
    }
    int i = 0;
    while (i < 18)
    {
      paramArrayOfInt[(i + paramInt)] = this.mElements[i];
      i += 1;
    }
  }
  
  public void copyElements(Rational[] paramArrayOfRational, int paramInt)
  {
    Preconditions.checkArgumentNonnegative(paramInt, "offset must not be negative");
    Preconditions.checkNotNull(paramArrayOfRational, "destination must not be null");
    if (paramArrayOfRational.length - paramInt < 9) {
      throw new ArrayIndexOutOfBoundsException("destination too small to fit elements");
    }
    int j = 0;
    int i = 0;
    while (j < 9)
    {
      paramArrayOfRational[(j + paramInt)] = new Rational(this.mElements[(i + 0)], this.mElements[(i + 1)]);
      j += 1;
      i += 2;
    }
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    if (this == paramObject) {
      return true;
    }
    if ((paramObject instanceof ColorSpaceTransform))
    {
      paramObject = (ColorSpaceTransform)paramObject;
      int j = 0;
      int i = 0;
      while (j < 9)
      {
        int k = this.mElements[(i + 0)];
        int m = this.mElements[(i + 1)];
        int n = paramObject.mElements[(i + 0)];
        int i1 = paramObject.mElements[(i + 1)];
        if (!new Rational(k, m).equals(new Rational(n, i1))) {
          return false;
        }
        j += 1;
        i += 2;
      }
      return true;
    }
    return false;
  }
  
  public Rational getElement(int paramInt1, int paramInt2)
  {
    if ((paramInt1 < 0) || (paramInt1 >= 3)) {
      throw new IllegalArgumentException("column out of range");
    }
    if ((paramInt2 < 0) || (paramInt2 >= 3)) {
      throw new IllegalArgumentException("row out of range");
    }
    return new Rational(this.mElements[((paramInt2 * 3 + paramInt1) * 2 + 0)], this.mElements[((paramInt2 * 3 + paramInt1) * 2 + 1)]);
  }
  
  public int hashCode()
  {
    return HashCodeHelpers.hashCode(this.mElements);
  }
  
  public String toString()
  {
    return String.format("ColorSpaceTransform%s", new Object[] { toShortString() });
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/params/ColorSpaceTransform.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */