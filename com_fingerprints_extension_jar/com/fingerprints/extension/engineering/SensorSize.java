package com.fingerprints.extension.engineering;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.fingerprints.extension.util.Logger;

public class SensorSize
  implements Parcelable
{
  public static final Parcelable.Creator<SensorSize> CREATOR = new Parcelable.Creator()
  {
    public SensorSize createFromParcel(Parcel paramAnonymousParcel)
    {
      return new SensorSize(paramAnonymousParcel, null);
    }
    
    public SensorSize[] newArray(int paramAnonymousInt)
    {
      return new SensorSize[paramAnonymousInt];
    }
  };
  public int mHeight;
  private Logger mLogger = new Logger(getClass().getSimpleName());
  public int mWidth;
  
  public SensorSize() {}
  
  private SensorSize(Parcel paramParcel)
  {
    this.mWidth = paramParcel.readInt();
    this.mHeight = paramParcel.readInt();
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public void print()
  {
    this.mLogger.d("mWidth: " + this.mWidth + " mHeight: " + this.mHeight);
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mWidth);
    paramParcel.writeInt(this.mHeight);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/fingerprints/extension/engineering/SensorSize.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */