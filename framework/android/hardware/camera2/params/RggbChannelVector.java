package android.hardware.camera2.params;

import com.android.internal.util.Preconditions;

public final class RggbChannelVector
{
  public static final int BLUE = 3;
  public static final int COUNT = 4;
  public static final int GREEN_EVEN = 1;
  public static final int GREEN_ODD = 2;
  public static final int RED = 0;
  private final float mBlue;
  private final float mGreenEven;
  private final float mGreenOdd;
  private final float mRed;
  
  public RggbChannelVector(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    this.mRed = Preconditions.checkArgumentFinite(paramFloat1, "red");
    this.mGreenEven = Preconditions.checkArgumentFinite(paramFloat2, "greenEven");
    this.mGreenOdd = Preconditions.checkArgumentFinite(paramFloat3, "greenOdd");
    this.mBlue = Preconditions.checkArgumentFinite(paramFloat4, "blue");
  }
  
  private String toShortString()
  {
    return String.format("{R:%f, G_even:%f, G_odd:%f, B:%f}", new Object[] { Float.valueOf(this.mRed), Float.valueOf(this.mGreenEven), Float.valueOf(this.mGreenOdd), Float.valueOf(this.mBlue) });
  }
  
  public void copyTo(float[] paramArrayOfFloat, int paramInt)
  {
    Preconditions.checkNotNull(paramArrayOfFloat, "destination must not be null");
    if (paramArrayOfFloat.length - paramInt < 4) {
      throw new ArrayIndexOutOfBoundsException("destination too small to fit elements");
    }
    paramArrayOfFloat[(paramInt + 0)] = this.mRed;
    paramArrayOfFloat[(paramInt + 1)] = this.mGreenEven;
    paramArrayOfFloat[(paramInt + 2)] = this.mGreenOdd;
    paramArrayOfFloat[(paramInt + 3)] = this.mBlue;
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    if (this == paramObject) {
      return true;
    }
    if ((paramObject instanceof RggbChannelVector))
    {
      paramObject = (RggbChannelVector)paramObject;
      if ((this.mRed == ((RggbChannelVector)paramObject).mRed) && (this.mGreenEven == ((RggbChannelVector)paramObject).mGreenEven) && (this.mGreenOdd == ((RggbChannelVector)paramObject).mGreenOdd)) {
        return this.mBlue == ((RggbChannelVector)paramObject).mBlue;
      }
      return false;
    }
    return false;
  }
  
  public float getBlue()
  {
    return this.mBlue;
  }
  
  public float getComponent(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= 4)) {
      throw new IllegalArgumentException("Color channel out of range");
    }
    switch (paramInt)
    {
    default: 
      throw new AssertionError("Unhandled case " + paramInt);
    case 0: 
      return this.mRed;
    case 1: 
      return this.mGreenEven;
    case 2: 
      return this.mGreenOdd;
    }
    return this.mBlue;
  }
  
  public float getGreenEven()
  {
    return this.mGreenEven;
  }
  
  public float getGreenOdd()
  {
    return this.mGreenOdd;
  }
  
  public final float getRed()
  {
    return this.mRed;
  }
  
  public int hashCode()
  {
    return Float.floatToIntBits(this.mRed) ^ Float.floatToIntBits(this.mGreenEven) ^ Float.floatToIntBits(this.mGreenOdd) ^ Float.floatToIntBits(this.mBlue);
  }
  
  public String toString()
  {
    return String.format("RggbChannelVector%s", new Object[] { toShortString() });
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/params/RggbChannelVector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */