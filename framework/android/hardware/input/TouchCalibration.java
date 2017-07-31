package android.hardware.input;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class TouchCalibration
  implements Parcelable
{
  public static final Parcelable.Creator<TouchCalibration> CREATOR = new Parcelable.Creator()
  {
    public TouchCalibration createFromParcel(Parcel paramAnonymousParcel)
    {
      return new TouchCalibration(paramAnonymousParcel);
    }
    
    public TouchCalibration[] newArray(int paramAnonymousInt)
    {
      return new TouchCalibration[paramAnonymousInt];
    }
  };
  public static final TouchCalibration IDENTITY = new TouchCalibration();
  private final float mXOffset;
  private final float mXScale;
  private final float mXYMix;
  private final float mYOffset;
  private final float mYScale;
  private final float mYXMix;
  
  public TouchCalibration()
  {
    this(1.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F);
  }
  
  public TouchCalibration(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6)
  {
    this.mXScale = paramFloat1;
    this.mXYMix = paramFloat2;
    this.mXOffset = paramFloat3;
    this.mYXMix = paramFloat4;
    this.mYScale = paramFloat5;
    this.mYOffset = paramFloat6;
  }
  
  public TouchCalibration(Parcel paramParcel)
  {
    this.mXScale = paramParcel.readFloat();
    this.mXYMix = paramParcel.readFloat();
    this.mXOffset = paramParcel.readFloat();
    this.mYXMix = paramParcel.readFloat();
    this.mYScale = paramParcel.readFloat();
    this.mYOffset = paramParcel.readFloat();
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if ((paramObject instanceof TouchCalibration))
    {
      paramObject = (TouchCalibration)paramObject;
      if ((((TouchCalibration)paramObject).mXScale == this.mXScale) && (((TouchCalibration)paramObject).mXYMix == this.mXYMix) && (((TouchCalibration)paramObject).mXOffset == this.mXOffset) && (((TouchCalibration)paramObject).mYXMix == this.mYXMix) && (((TouchCalibration)paramObject).mYScale == this.mYScale)) {
        return ((TouchCalibration)paramObject).mYOffset == this.mYOffset;
      }
      return false;
    }
    return false;
  }
  
  public float[] getAffineTransform()
  {
    return new float[] { this.mXScale, this.mXYMix, this.mXOffset, this.mYXMix, this.mYScale, this.mYOffset };
  }
  
  public int hashCode()
  {
    return Float.floatToIntBits(this.mXScale) ^ Float.floatToIntBits(this.mXYMix) ^ Float.floatToIntBits(this.mXOffset) ^ Float.floatToIntBits(this.mYXMix) ^ Float.floatToIntBits(this.mYScale) ^ Float.floatToIntBits(this.mYOffset);
  }
  
  public String toString()
  {
    return String.format("[%f, %f, %f, %f, %f, %f]", new Object[] { Float.valueOf(this.mXScale), Float.valueOf(this.mXYMix), Float.valueOf(this.mXOffset), Float.valueOf(this.mYXMix), Float.valueOf(this.mYScale), Float.valueOf(this.mYOffset) });
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeFloat(this.mXScale);
    paramParcel.writeFloat(this.mXYMix);
    paramParcel.writeFloat(this.mXOffset);
    paramParcel.writeFloat(this.mYXMix);
    paramParcel.writeFloat(this.mYScale);
    paramParcel.writeFloat(this.mYOffset);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/input/TouchCalibration.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */