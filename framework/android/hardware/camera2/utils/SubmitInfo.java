package android.hardware.camera2.utils;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class SubmitInfo
  implements Parcelable
{
  public static final Parcelable.Creator<SubmitInfo> CREATOR = new Parcelable.Creator()
  {
    public SubmitInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      return new SubmitInfo(paramAnonymousParcel, null);
    }
    
    public SubmitInfo[] newArray(int paramAnonymousInt)
    {
      return new SubmitInfo[paramAnonymousInt];
    }
  };
  private long mLastFrameNumber;
  private int mRequestId;
  
  public SubmitInfo()
  {
    this.mRequestId = -1;
    this.mLastFrameNumber = -1L;
  }
  
  public SubmitInfo(int paramInt, long paramLong)
  {
    this.mRequestId = paramInt;
    this.mLastFrameNumber = paramLong;
  }
  
  private SubmitInfo(Parcel paramParcel)
  {
    readFromParcel(paramParcel);
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public long getLastFrameNumber()
  {
    return this.mLastFrameNumber;
  }
  
  public int getRequestId()
  {
    return this.mRequestId;
  }
  
  public void readFromParcel(Parcel paramParcel)
  {
    this.mRequestId = paramParcel.readInt();
    this.mLastFrameNumber = paramParcel.readLong();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mRequestId);
    paramParcel.writeLong(this.mLastFrameNumber);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/utils/SubmitInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */