package android.net;

import android.os.Parcel;

public class MobileLinkQualityInfo
  extends LinkQualityInfo
{
  private int mCdmaDbm = Integer.MAX_VALUE;
  private int mCdmaEcio = Integer.MAX_VALUE;
  private int mEvdoDbm = Integer.MAX_VALUE;
  private int mEvdoEcio = Integer.MAX_VALUE;
  private int mEvdoSnr = Integer.MAX_VALUE;
  private int mGsmErrorRate = Integer.MAX_VALUE;
  private int mLteCqi = Integer.MAX_VALUE;
  private int mLteRsrp = Integer.MAX_VALUE;
  private int mLteRsrq = Integer.MAX_VALUE;
  private int mLteRssnr = Integer.MAX_VALUE;
  private int mLteSignalStrength = Integer.MAX_VALUE;
  private int mMobileNetworkType = Integer.MAX_VALUE;
  private int mRssi = Integer.MAX_VALUE;
  
  public static MobileLinkQualityInfo createFromParcelBody(Parcel paramParcel)
  {
    MobileLinkQualityInfo localMobileLinkQualityInfo = new MobileLinkQualityInfo();
    localMobileLinkQualityInfo.initializeFromParcel(paramParcel);
    localMobileLinkQualityInfo.mMobileNetworkType = paramParcel.readInt();
    localMobileLinkQualityInfo.mRssi = paramParcel.readInt();
    localMobileLinkQualityInfo.mGsmErrorRate = paramParcel.readInt();
    localMobileLinkQualityInfo.mCdmaDbm = paramParcel.readInt();
    localMobileLinkQualityInfo.mCdmaEcio = paramParcel.readInt();
    localMobileLinkQualityInfo.mEvdoDbm = paramParcel.readInt();
    localMobileLinkQualityInfo.mEvdoEcio = paramParcel.readInt();
    localMobileLinkQualityInfo.mEvdoSnr = paramParcel.readInt();
    localMobileLinkQualityInfo.mLteSignalStrength = paramParcel.readInt();
    localMobileLinkQualityInfo.mLteRsrp = paramParcel.readInt();
    localMobileLinkQualityInfo.mLteRsrq = paramParcel.readInt();
    localMobileLinkQualityInfo.mLteRssnr = paramParcel.readInt();
    localMobileLinkQualityInfo.mLteCqi = paramParcel.readInt();
    return localMobileLinkQualityInfo;
  }
  
  public int getCdmaDbm()
  {
    return this.mCdmaDbm;
  }
  
  public int getCdmaEcio()
  {
    return this.mCdmaEcio;
  }
  
  public int getEvdoDbm()
  {
    return this.mEvdoDbm;
  }
  
  public int getEvdoEcio()
  {
    return this.mEvdoEcio;
  }
  
  public int getEvdoSnr()
  {
    return this.mEvdoSnr;
  }
  
  public int getGsmErrorRate()
  {
    return this.mGsmErrorRate;
  }
  
  public int getLteCqi()
  {
    return this.mLteCqi;
  }
  
  public int getLteRsrp()
  {
    return this.mLteRsrp;
  }
  
  public int getLteRsrq()
  {
    return this.mLteRsrq;
  }
  
  public int getLteRssnr()
  {
    return this.mLteRssnr;
  }
  
  public int getLteSignalStrength()
  {
    return this.mLteSignalStrength;
  }
  
  public int getMobileNetworkType()
  {
    return this.mMobileNetworkType;
  }
  
  public int getRssi()
  {
    return this.mRssi;
  }
  
  public void setCdmaDbm(int paramInt)
  {
    this.mCdmaDbm = paramInt;
  }
  
  public void setCdmaEcio(int paramInt)
  {
    this.mCdmaEcio = paramInt;
  }
  
  public void setEvdoDbm(int paramInt)
  {
    this.mEvdoDbm = paramInt;
  }
  
  public void setEvdoEcio(int paramInt)
  {
    this.mEvdoEcio = paramInt;
  }
  
  public void setEvdoSnr(int paramInt)
  {
    this.mEvdoSnr = paramInt;
  }
  
  public void setGsmErrorRate(int paramInt)
  {
    this.mGsmErrorRate = paramInt;
  }
  
  public void setLteCqi(int paramInt)
  {
    this.mLteCqi = paramInt;
  }
  
  public void setLteRsrp(int paramInt)
  {
    this.mLteRsrp = paramInt;
  }
  
  public void setLteRsrq(int paramInt)
  {
    this.mLteRsrq = paramInt;
  }
  
  public void setLteRssnr(int paramInt)
  {
    this.mLteRssnr = paramInt;
  }
  
  public void setLteSignalStrength(int paramInt)
  {
    this.mLteSignalStrength = paramInt;
  }
  
  public void setMobileNetworkType(int paramInt)
  {
    this.mMobileNetworkType = paramInt;
  }
  
  public void setRssi(int paramInt)
  {
    this.mRssi = paramInt;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    super.writeToParcel(paramParcel, paramInt, 3);
    paramParcel.writeInt(this.mMobileNetworkType);
    paramParcel.writeInt(this.mRssi);
    paramParcel.writeInt(this.mGsmErrorRate);
    paramParcel.writeInt(this.mCdmaDbm);
    paramParcel.writeInt(this.mCdmaEcio);
    paramParcel.writeInt(this.mEvdoDbm);
    paramParcel.writeInt(this.mEvdoEcio);
    paramParcel.writeInt(this.mEvdoSnr);
    paramParcel.writeInt(this.mLteSignalStrength);
    paramParcel.writeInt(this.mLteRsrp);
    paramParcel.writeInt(this.mLteRsrq);
    paramParcel.writeInt(this.mLteRssnr);
    paramParcel.writeInt(this.mLteCqi);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/MobileLinkQualityInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */