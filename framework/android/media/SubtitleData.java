package android.media;

import android.os.Parcel;

public final class SubtitleData
{
  private static final String TAG = "SubtitleData";
  private byte[] mData;
  private long mDurationUs;
  private long mStartTimeUs;
  private int mTrackIndex;
  
  public SubtitleData(Parcel paramParcel)
  {
    if (!parseParcel(paramParcel)) {
      throw new IllegalArgumentException("parseParcel() fails");
    }
  }
  
  private boolean parseParcel(Parcel paramParcel)
  {
    paramParcel.setDataPosition(0);
    if (paramParcel.dataAvail() == 0) {
      return false;
    }
    this.mTrackIndex = paramParcel.readInt();
    this.mStartTimeUs = paramParcel.readLong();
    this.mDurationUs = paramParcel.readLong();
    this.mData = new byte[paramParcel.readInt()];
    paramParcel.readByteArray(this.mData);
    return true;
  }
  
  public byte[] getData()
  {
    return this.mData;
  }
  
  public long getDurationUs()
  {
    return this.mDurationUs;
  }
  
  public long getStartTimeUs()
  {
    return this.mStartTimeUs;
  }
  
  public int getTrackIndex()
  {
    return this.mTrackIndex;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/SubtitleData.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */