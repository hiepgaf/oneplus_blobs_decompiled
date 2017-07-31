package android.hardware.camera2.utils;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class LongParcelable
  implements Parcelable
{
  public static final Parcelable.Creator<LongParcelable> CREATOR = new Parcelable.Creator()
  {
    public LongParcelable createFromParcel(Parcel paramAnonymousParcel)
    {
      return new LongParcelable(paramAnonymousParcel, null);
    }
    
    public LongParcelable[] newArray(int paramAnonymousInt)
    {
      return new LongParcelable[paramAnonymousInt];
    }
  };
  private long number;
  
  public LongParcelable()
  {
    this.number = 0L;
  }
  
  public LongParcelable(long paramLong)
  {
    this.number = paramLong;
  }
  
  private LongParcelable(Parcel paramParcel)
  {
    readFromParcel(paramParcel);
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public long getNumber()
  {
    return this.number;
  }
  
  public void readFromParcel(Parcel paramParcel)
  {
    this.number = paramParcel.readLong();
  }
  
  public void setNumber(long paramLong)
  {
    this.number = paramLong;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeLong(this.number);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/utils/LongParcelable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */