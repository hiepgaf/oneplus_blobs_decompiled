package android.hardware.camera2.params;

import android.hardware.camera2.utils.HashCodeHelpers;
import com.android.internal.util.Preconditions;
import java.util.Arrays;

public final class LensShadingMap
{
  public static final float MINIMUM_GAIN_FACTOR = 1.0F;
  private final int mColumns;
  private final float[] mElements;
  private final int mRows;
  
  public LensShadingMap(float[] paramArrayOfFloat, int paramInt1, int paramInt2)
  {
    this.mRows = Preconditions.checkArgumentPositive(paramInt1, "rows must be positive");
    this.mColumns = Preconditions.checkArgumentPositive(paramInt2, "columns must be positive");
    this.mElements = ((float[])Preconditions.checkNotNull(paramArrayOfFloat, "elements must not be null"));
    if (paramArrayOfFloat.length != getGainFactorCount()) {
      throw new IllegalArgumentException("elements must be " + getGainFactorCount() + " length, received " + paramArrayOfFloat.length);
    }
    Preconditions.checkArrayElementsInRange(paramArrayOfFloat, 1.0F, Float.MAX_VALUE, "elements");
  }
  
  public void copyGainFactors(float[] paramArrayOfFloat, int paramInt)
  {
    Preconditions.checkArgumentNonnegative(paramInt, "offset must not be negative");
    Preconditions.checkNotNull(paramArrayOfFloat, "destination must not be null");
    if (paramArrayOfFloat.length + paramInt < getGainFactorCount()) {
      throw new ArrayIndexOutOfBoundsException("destination too small to fit elements");
    }
    System.arraycopy(this.mElements, 0, paramArrayOfFloat, paramInt, getGainFactorCount());
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
    if ((paramObject instanceof LensShadingMap))
    {
      paramObject = (LensShadingMap)paramObject;
      boolean bool1 = bool2;
      if (this.mRows == ((LensShadingMap)paramObject).mRows)
      {
        bool1 = bool2;
        if (this.mColumns == ((LensShadingMap)paramObject).mColumns) {
          bool1 = Arrays.equals(this.mElements, ((LensShadingMap)paramObject).mElements);
        }
      }
      return bool1;
    }
    return false;
  }
  
  public int getColumnCount()
  {
    return this.mColumns;
  }
  
  public float getGainFactor(int paramInt1, int paramInt2, int paramInt3)
  {
    if ((paramInt1 < 0) || (paramInt1 > 4)) {
      throw new IllegalArgumentException("colorChannel out of range");
    }
    if ((paramInt2 < 0) || (paramInt2 >= this.mColumns)) {
      throw new IllegalArgumentException("column out of range");
    }
    if ((paramInt3 < 0) || (paramInt3 >= this.mRows)) {
      throw new IllegalArgumentException("row out of range");
    }
    return this.mElements[((this.mColumns * paramInt3 + paramInt2) * 4 + paramInt1)];
  }
  
  public int getGainFactorCount()
  {
    return this.mRows * this.mColumns * 4;
  }
  
  public RggbChannelVector getGainFactorVector(int paramInt1, int paramInt2)
  {
    if ((paramInt1 < 0) || (paramInt1 >= this.mColumns)) {
      throw new IllegalArgumentException("column out of range");
    }
    if ((paramInt2 < 0) || (paramInt2 >= this.mRows)) {
      throw new IllegalArgumentException("row out of range");
    }
    paramInt1 = (this.mColumns * paramInt2 + paramInt1) * 4;
    return new RggbChannelVector(this.mElements[(paramInt1 + 0)], this.mElements[(paramInt1 + 1)], this.mElements[(paramInt1 + 2)], this.mElements[(paramInt1 + 3)]);
  }
  
  public int getRowCount()
  {
    return this.mRows;
  }
  
  public int hashCode()
  {
    int i = HashCodeHelpers.hashCode(this.mElements);
    return HashCodeHelpers.hashCode(new int[] { this.mRows, this.mColumns, i });
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("LensShadingMap{");
    int i = 0;
    while (i < 4)
    {
      localStringBuilder.append(new String[] { "R:(", "G_even:(", "G_odd:(", "B:(" }[i]);
      int j = 0;
      while (j < this.mRows)
      {
        localStringBuilder.append("[");
        int k = 0;
        while (k < this.mColumns)
        {
          localStringBuilder.append(getGainFactor(i, k, j));
          if (k < this.mColumns - 1) {
            localStringBuilder.append(", ");
          }
          k += 1;
        }
        localStringBuilder.append("]");
        if (j < this.mRows - 1) {
          localStringBuilder.append(", ");
        }
        j += 1;
      }
      localStringBuilder.append(")");
      if (i < 3) {
        localStringBuilder.append(", ");
      }
      i += 1;
    }
    localStringBuilder.append("}");
    return localStringBuilder.toString();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/params/LensShadingMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */