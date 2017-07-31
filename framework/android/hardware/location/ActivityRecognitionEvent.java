package android.hardware.location;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class ActivityRecognitionEvent
  implements Parcelable
{
  public static final Parcelable.Creator<ActivityRecognitionEvent> CREATOR = new Parcelable.Creator()
  {
    public ActivityRecognitionEvent createFromParcel(Parcel paramAnonymousParcel)
    {
      return new ActivityRecognitionEvent(paramAnonymousParcel.readString(), paramAnonymousParcel.readInt(), paramAnonymousParcel.readLong());
    }
    
    public ActivityRecognitionEvent[] newArray(int paramAnonymousInt)
    {
      return new ActivityRecognitionEvent[paramAnonymousInt];
    }
  };
  private final String mActivity;
  private final int mEventType;
  private final long mTimestampNs;
  
  public ActivityRecognitionEvent(String paramString, int paramInt, long paramLong)
  {
    this.mActivity = paramString;
    this.mEventType = paramInt;
    this.mTimestampNs = paramLong;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public String getActivity()
  {
    return this.mActivity;
  }
  
  public int getEventType()
  {
    return this.mEventType;
  }
  
  public long getTimestampNs()
  {
    return this.mTimestampNs;
  }
  
  public String toString()
  {
    return String.format("Activity='%s', EventType=%s, TimestampNs=%s", new Object[] { this.mActivity, Integer.valueOf(this.mEventType), Long.valueOf(this.mTimestampNs) });
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.mActivity);
    paramParcel.writeInt(this.mEventType);
    paramParcel.writeLong(this.mTimestampNs);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/location/ActivityRecognitionEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */