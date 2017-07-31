package android.media;

import android.os.Parcel;

public final class TimedMetaData
{
  private static final String TAG = "TimedMetaData";
  private byte[] mMetaData;
  private long mTimestampUs;
  
  private TimedMetaData(Parcel paramParcel)
  {
    if (!parseParcel(paramParcel)) {
      throw new IllegalArgumentException("parseParcel() fails");
    }
  }
  
  static TimedMetaData createTimedMetaDataFromParcel(Parcel paramParcel)
  {
    return new TimedMetaData(paramParcel);
  }
  
  private boolean parseParcel(Parcel paramParcel)
  {
    paramParcel.setDataPosition(0);
    if (paramParcel.dataAvail() == 0) {
      return false;
    }
    this.mTimestampUs = paramParcel.readLong();
    this.mMetaData = new byte[paramParcel.readInt()];
    paramParcel.readByteArray(this.mMetaData);
    return true;
  }
  
  public byte[] getMetaData()
  {
    return this.mMetaData;
  }
  
  public long getTimestamp()
  {
    return this.mTimestampUs;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/TimedMetaData.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */