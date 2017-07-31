package android.net;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class LinkQualityInfo
  implements Parcelable
{
  public static final Parcelable.Creator<LinkQualityInfo> CREATOR = new Parcelable.Creator()
  {
    public LinkQualityInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      int i = paramAnonymousParcel.readInt();
      if (i == 1)
      {
        LinkQualityInfo localLinkQualityInfo = new LinkQualityInfo();
        localLinkQualityInfo.initializeFromParcel(paramAnonymousParcel);
        return localLinkQualityInfo;
      }
      if (i == 2) {
        return WifiLinkQualityInfo.createFromParcelBody(paramAnonymousParcel);
      }
      if (i == 3) {
        return MobileLinkQualityInfo.createFromParcelBody(paramAnonymousParcel);
      }
      return null;
    }
    
    public LinkQualityInfo[] newArray(int paramAnonymousInt)
    {
      return new LinkQualityInfo[paramAnonymousInt];
    }
  };
  public static final int NORMALIZED_MAX_SIGNAL_STRENGTH = 99;
  public static final int NORMALIZED_MIN_SIGNAL_STRENGTH = 0;
  public static final int NORMALIZED_SIGNAL_STRENGTH_RANGE = 100;
  protected static final int OBJECT_TYPE_LINK_QUALITY_INFO = 1;
  protected static final int OBJECT_TYPE_MOBILE_LINK_QUALITY_INFO = 3;
  protected static final int OBJECT_TYPE_WIFI_LINK_QUALITY_INFO = 2;
  public static final int UNKNOWN_INT = Integer.MAX_VALUE;
  public static final long UNKNOWN_LONG = Long.MAX_VALUE;
  private int mDataSampleDuration = Integer.MAX_VALUE;
  private long mLastDataSampleTime = Long.MAX_VALUE;
  private int mNetworkType = -1;
  private int mNormalizedSignalStrength = Integer.MAX_VALUE;
  private long mPacketCount = Long.MAX_VALUE;
  private long mPacketErrorCount = Long.MAX_VALUE;
  private int mTheoreticalLatency = Integer.MAX_VALUE;
  private int mTheoreticalRxBandwidth = Integer.MAX_VALUE;
  private int mTheoreticalTxBandwidth = Integer.MAX_VALUE;
  
  public int describeContents()
  {
    return 0;
  }
  
  public int getDataSampleDuration()
  {
    return this.mDataSampleDuration;
  }
  
  public long getLastDataSampleTime()
  {
    return this.mLastDataSampleTime;
  }
  
  public int getNetworkType()
  {
    return this.mNetworkType;
  }
  
  public int getNormalizedSignalStrength()
  {
    return this.mNormalizedSignalStrength;
  }
  
  public long getPacketCount()
  {
    return this.mPacketCount;
  }
  
  public long getPacketErrorCount()
  {
    return this.mPacketErrorCount;
  }
  
  public int getTheoreticalLatency()
  {
    return this.mTheoreticalLatency;
  }
  
  public int getTheoreticalRxBandwidth()
  {
    return this.mTheoreticalRxBandwidth;
  }
  
  public int getTheoreticalTxBandwidth()
  {
    return this.mTheoreticalTxBandwidth;
  }
  
  protected void initializeFromParcel(Parcel paramParcel)
  {
    this.mNetworkType = paramParcel.readInt();
    this.mNormalizedSignalStrength = paramParcel.readInt();
    this.mPacketCount = paramParcel.readLong();
    this.mPacketErrorCount = paramParcel.readLong();
    this.mTheoreticalTxBandwidth = paramParcel.readInt();
    this.mTheoreticalRxBandwidth = paramParcel.readInt();
    this.mTheoreticalLatency = paramParcel.readInt();
    this.mLastDataSampleTime = paramParcel.readLong();
    this.mDataSampleDuration = paramParcel.readInt();
  }
  
  public void setDataSampleDuration(int paramInt)
  {
    this.mDataSampleDuration = paramInt;
  }
  
  public void setLastDataSampleTime(long paramLong)
  {
    this.mLastDataSampleTime = paramLong;
  }
  
  public void setNetworkType(int paramInt)
  {
    this.mNetworkType = paramInt;
  }
  
  public void setNormalizedSignalStrength(int paramInt)
  {
    this.mNormalizedSignalStrength = paramInt;
  }
  
  public void setPacketCount(long paramLong)
  {
    this.mPacketCount = paramLong;
  }
  
  public void setPacketErrorCount(long paramLong)
  {
    this.mPacketErrorCount = paramLong;
  }
  
  public void setTheoreticalLatency(int paramInt)
  {
    this.mTheoreticalLatency = paramInt;
  }
  
  public void setTheoreticalRxBandwidth(int paramInt)
  {
    this.mTheoreticalRxBandwidth = paramInt;
  }
  
  public void setTheoreticalTxBandwidth(int paramInt)
  {
    this.mTheoreticalTxBandwidth = paramInt;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    writeToParcel(paramParcel, paramInt, 1);
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt1, int paramInt2)
  {
    paramParcel.writeInt(paramInt2);
    paramParcel.writeInt(this.mNetworkType);
    paramParcel.writeInt(this.mNormalizedSignalStrength);
    paramParcel.writeLong(this.mPacketCount);
    paramParcel.writeLong(this.mPacketErrorCount);
    paramParcel.writeInt(this.mTheoreticalTxBandwidth);
    paramParcel.writeInt(this.mTheoreticalRxBandwidth);
    paramParcel.writeInt(this.mTheoreticalLatency);
    paramParcel.writeLong(this.mLastDataSampleTime);
    paramParcel.writeInt(this.mDataSampleDuration);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/LinkQualityInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */