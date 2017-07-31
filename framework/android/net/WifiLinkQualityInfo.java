package android.net;

import android.os.Parcel;

public class WifiLinkQualityInfo
  extends LinkQualityInfo
{
  private String mBssid;
  private int mRssi = Integer.MAX_VALUE;
  private long mTxBad = Long.MAX_VALUE;
  private long mTxGood = Long.MAX_VALUE;
  private int mType = Integer.MAX_VALUE;
  
  public static WifiLinkQualityInfo createFromParcelBody(Parcel paramParcel)
  {
    WifiLinkQualityInfo localWifiLinkQualityInfo = new WifiLinkQualityInfo();
    localWifiLinkQualityInfo.initializeFromParcel(paramParcel);
    localWifiLinkQualityInfo.mType = paramParcel.readInt();
    localWifiLinkQualityInfo.mRssi = paramParcel.readInt();
    localWifiLinkQualityInfo.mTxGood = paramParcel.readLong();
    localWifiLinkQualityInfo.mTxBad = paramParcel.readLong();
    localWifiLinkQualityInfo.mBssid = paramParcel.readString();
    return localWifiLinkQualityInfo;
  }
  
  public String getBssid()
  {
    return this.mBssid;
  }
  
  public int getRssi()
  {
    return this.mRssi;
  }
  
  public long getTxBad()
  {
    return this.mTxBad;
  }
  
  public long getTxGood()
  {
    return this.mTxGood;
  }
  
  public int getType()
  {
    return this.mType;
  }
  
  public void setBssid(String paramString)
  {
    this.mBssid = paramString;
  }
  
  public void setRssi(int paramInt)
  {
    this.mRssi = paramInt;
  }
  
  public void setTxBad(long paramLong)
  {
    this.mTxBad = paramLong;
  }
  
  public void setTxGood(long paramLong)
  {
    this.mTxGood = paramLong;
  }
  
  public void setType(int paramInt)
  {
    this.mType = paramInt;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    super.writeToParcel(paramParcel, paramInt, 2);
    paramParcel.writeInt(this.mType);
    paramParcel.writeInt(this.mRssi);
    paramParcel.writeLong(this.mTxGood);
    paramParcel.writeLong(this.mTxBad);
    paramParcel.writeString(this.mBssid);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/WifiLinkQualityInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */