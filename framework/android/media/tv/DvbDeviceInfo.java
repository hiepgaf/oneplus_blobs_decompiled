package android.media.tv;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.Log;

public final class DvbDeviceInfo
  implements Parcelable
{
  public static final Parcelable.Creator<DvbDeviceInfo> CREATOR = new Parcelable.Creator()
  {
    public DvbDeviceInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      try
      {
        paramAnonymousParcel = new DvbDeviceInfo(paramAnonymousParcel, null);
        return paramAnonymousParcel;
      }
      catch (Exception paramAnonymousParcel)
      {
        Log.e("DvbDeviceInfo", "Exception creating DvbDeviceInfo from parcel", paramAnonymousParcel);
      }
      return null;
    }
    
    public DvbDeviceInfo[] newArray(int paramAnonymousInt)
    {
      return new DvbDeviceInfo[paramAnonymousInt];
    }
  };
  static final String TAG = "DvbDeviceInfo";
  private final int mAdapterId;
  private final int mDeviceId;
  
  public DvbDeviceInfo(int paramInt1, int paramInt2)
  {
    this.mAdapterId = paramInt1;
    this.mDeviceId = paramInt2;
  }
  
  private DvbDeviceInfo(Parcel paramParcel)
  {
    this.mAdapterId = paramParcel.readInt();
    this.mDeviceId = paramParcel.readInt();
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public int getAdapterId()
  {
    return this.mAdapterId;
  }
  
  public int getDeviceId()
  {
    return this.mDeviceId;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mAdapterId);
    paramParcel.writeInt(this.mDeviceId);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/tv/DvbDeviceInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */