package android.net;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class ConnectivityMetricsEvent
  implements Parcelable
{
  public static final Parcelable.Creator<ConnectivityMetricsEvent> CREATOR = new Parcelable.Creator()
  {
    public ConnectivityMetricsEvent createFromParcel(Parcel paramAnonymousParcel)
    {
      return new ConnectivityMetricsEvent(paramAnonymousParcel.readLong(), paramAnonymousParcel.readInt(), paramAnonymousParcel.readInt(), paramAnonymousParcel.readParcelable(null));
    }
    
    public ConnectivityMetricsEvent[] newArray(int paramAnonymousInt)
    {
      return new ConnectivityMetricsEvent[paramAnonymousInt];
    }
  };
  public final int componentTag;
  public final Parcelable data;
  public final int eventTag;
  public final long timestamp;
  
  public ConnectivityMetricsEvent(long paramLong, int paramInt1, int paramInt2, Parcelable paramParcelable)
  {
    this.timestamp = paramLong;
    this.componentTag = paramInt1;
    this.eventTag = paramInt2;
    this.data = paramParcelable;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public String toString()
  {
    return String.format("ConnectivityMetricsEvent(%tT.%tL, %d, %d): %s", new Object[] { Long.valueOf(this.timestamp), Long.valueOf(this.timestamp), Integer.valueOf(this.componentTag), Integer.valueOf(this.eventTag), this.data });
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeLong(this.timestamp);
    paramParcel.writeInt(this.componentTag);
    paramParcel.writeInt(this.eventTag);
    paramParcel.writeParcelable(this.data, 0);
  }
  
  public static final class Reference
    implements Parcelable
  {
    public static final Parcelable.Creator<Reference> CREATOR = new Parcelable.Creator()
    {
      public ConnectivityMetricsEvent.Reference createFromParcel(Parcel paramAnonymousParcel)
      {
        return new ConnectivityMetricsEvent.Reference(paramAnonymousParcel.readLong());
      }
      
      public ConnectivityMetricsEvent.Reference[] newArray(int paramAnonymousInt)
      {
        return new ConnectivityMetricsEvent.Reference[paramAnonymousInt];
      }
    };
    private long mValue;
    
    public Reference(long paramLong)
    {
      this.mValue = paramLong;
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public long getValue()
    {
      return this.mValue;
    }
    
    public void readFromParcel(Parcel paramParcel)
    {
      this.mValue = paramParcel.readLong();
    }
    
    public void setValue(long paramLong)
    {
      this.mValue = paramLong;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeLong(this.mValue);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/ConnectivityMetricsEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */