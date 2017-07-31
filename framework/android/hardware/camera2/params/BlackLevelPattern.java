package android.hardware.camera2.params;

import com.android.internal.util.Preconditions;
import java.util.Arrays;

public final class BlackLevelPattern
{
  public static final int COUNT = 4;
  private final int[] mCfaOffsets;
  
  public BlackLevelPattern(int[] paramArrayOfInt)
  {
    if (paramArrayOfInt == null) {
      throw new NullPointerException("Null offsets array passed to constructor");
    }
    if (paramArrayOfInt.length < 4) {
      throw new IllegalArgumentException("Invalid offsets array length");
    }
    this.mCfaOffsets = Arrays.copyOf(paramArrayOfInt, 4);
  }
  
  public void copyTo(int[] paramArrayOfInt, int paramInt)
  {
    Preconditions.checkNotNull(paramArrayOfInt, "destination must not be null");
    if (paramInt < 0) {
      throw new IllegalArgumentException("Null offset passed to copyTo");
    }
    if (paramArrayOfInt.length - paramInt < 4) {
      throw new ArrayIndexOutOfBoundsException("destination too small to fit elements");
    }
    int i = 0;
    while (i < 4)
    {
      paramArrayOfInt[(paramInt + i)] = this.mCfaOffsets[i];
      i += 1;
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
    if ((paramObject instanceof BlackLevelPattern)) {
      return Arrays.equals(((BlackLevelPattern)paramObject).mCfaOffsets, this.mCfaOffsets);
    }
    return false;
  }
  
  public int getOffsetForIndex(int paramInt1, int paramInt2)
  {
    if ((paramInt2 < 0) || (paramInt1 < 0)) {
      throw new IllegalArgumentException("column, row arguments must be positive");
    }
    return this.mCfaOffsets[((paramInt2 & 0x1) << 1 | paramInt1 & 0x1)];
  }
  
  public int hashCode()
  {
    return Arrays.hashCode(this.mCfaOffsets);
  }
  
  public String toString()
  {
    return String.format("BlackLevelPattern([%d, %d], [%d, %d])", new Object[] { Integer.valueOf(this.mCfaOffsets[0]), Integer.valueOf(this.mCfaOffsets[1]), Integer.valueOf(this.mCfaOffsets[2]), Integer.valueOf(this.mCfaOffsets[3]) });
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/params/BlackLevelPattern.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */