package android.hardware.camera2.params;

import android.hardware.camera2.utils.HashCodeHelpers;
import com.android.internal.util.Preconditions;
import java.util.Arrays;

public final class ReprocessFormatsMap
{
  private final int[] mEntry;
  private final int mInputCount;
  
  public ReprocessFormatsMap(int[] paramArrayOfInt)
  {
    Preconditions.checkNotNull(paramArrayOfInt, "entry must not be null");
    int k = 0;
    int i = paramArrayOfInt.length;
    int j = 0;
    while (j < paramArrayOfInt.length)
    {
      int i1 = StreamConfigurationMap.checkArgumentFormatInternal(paramArrayOfInt[j]);
      i -= 1;
      j += 1;
      if (i < 1) {
        throw new IllegalArgumentException(String.format("Input %x had no output format length listed", new Object[] { Integer.valueOf(i1) }));
      }
      int i2 = paramArrayOfInt[j];
      int m = i - 1;
      int n = j + 1;
      i = 0;
      while (i < i2)
      {
        StreamConfigurationMap.checkArgumentFormatInternal(paramArrayOfInt[(n + i)]);
        i += 1;
      }
      j = n;
      i = m;
      if (i2 > 0)
      {
        if (m < i2) {
          throw new IllegalArgumentException(String.format("Input %x had too few output formats listed (actual: %d, expected: %d)", new Object[] { Integer.valueOf(i1), Integer.valueOf(m), Integer.valueOf(i2) }));
        }
        j = n + i2;
        i = m - i2;
      }
      k += 1;
    }
    this.mEntry = paramArrayOfInt;
    this.mInputCount = k;
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    if (this == paramObject) {
      return true;
    }
    if ((paramObject instanceof ReprocessFormatsMap))
    {
      paramObject = (ReprocessFormatsMap)paramObject;
      return Arrays.equals(this.mEntry, ((ReprocessFormatsMap)paramObject).mEntry);
    }
    return false;
  }
  
  public int[] getInputs()
  {
    int[] arrayOfInt = new int[this.mInputCount];
    int i = this.mEntry.length;
    int j = 0;
    int k = 0;
    while (j < this.mEntry.length)
    {
      int i1 = this.mEntry[j];
      i -= 1;
      j += 1;
      if (i < 1) {
        throw new AssertionError(String.format("Input %x had no output format length listed", new Object[] { Integer.valueOf(i1) }));
      }
      int i2 = this.mEntry[j];
      int m = i - 1;
      int n = j + 1;
      j = n;
      i = m;
      if (i2 > 0)
      {
        if (m < i2) {
          throw new AssertionError(String.format("Input %x had too few output formats listed (actual: %d, expected: %d)", new Object[] { Integer.valueOf(i1), Integer.valueOf(m), Integer.valueOf(i2) }));
        }
        j = n + i2;
        i = m - i2;
      }
      arrayOfInt[k] = i1;
      k += 1;
    }
    return StreamConfigurationMap.imageFormatToPublic(arrayOfInt);
  }
  
  public int[] getOutputs(int paramInt)
  {
    int j = this.mEntry.length;
    int i = 0;
    while (i < this.mEntry.length)
    {
      int k = this.mEntry[i];
      int m = j - 1;
      i += 1;
      if (m < 1) {
        throw new AssertionError(String.format("Input %x had no output format length listed", new Object[] { Integer.valueOf(paramInt) }));
      }
      j = this.mEntry[i];
      m -= 1;
      i += 1;
      if ((j > 0) && (m < j)) {
        throw new AssertionError(String.format("Input %x had too few output formats listed (actual: %d, expected: %d)", new Object[] { Integer.valueOf(paramInt), Integer.valueOf(m), Integer.valueOf(j) }));
      }
      if (k == paramInt)
      {
        int[] arrayOfInt = new int[j];
        paramInt = 0;
        while (paramInt < j)
        {
          arrayOfInt[paramInt] = this.mEntry[(i + paramInt)];
          paramInt += 1;
        }
        return StreamConfigurationMap.imageFormatToPublic(arrayOfInt);
      }
      i += j;
      j = m - j;
    }
    throw new IllegalArgumentException(String.format("Input format %x was not one in #getInputs", new Object[] { Integer.valueOf(paramInt) }));
  }
  
  public int hashCode()
  {
    return HashCodeHelpers.hashCode(this.mEntry);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/params/ReprocessFormatsMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */