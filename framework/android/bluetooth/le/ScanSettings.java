package android.bluetooth.le;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class ScanSettings
  implements Parcelable
{
  public static final int CALLBACK_TYPE_ALL_MATCHES = 1;
  public static final int CALLBACK_TYPE_FIRST_MATCH = 2;
  public static final int CALLBACK_TYPE_MATCH_LOST = 4;
  public static final Parcelable.Creator<ScanSettings> CREATOR = new Parcelable.Creator()
  {
    public ScanSettings createFromParcel(Parcel paramAnonymousParcel)
    {
      return new ScanSettings(paramAnonymousParcel, null);
    }
    
    public ScanSettings[] newArray(int paramAnonymousInt)
    {
      return new ScanSettings[paramAnonymousInt];
    }
  };
  public static final int MATCH_MODE_AGGRESSIVE = 1;
  public static final int MATCH_MODE_STICKY = 2;
  public static final int MATCH_NUM_FEW_ADVERTISEMENT = 2;
  public static final int MATCH_NUM_MAX_ADVERTISEMENT = 3;
  public static final int MATCH_NUM_ONE_ADVERTISEMENT = 1;
  public static final int SCAN_MODE_BALANCED = 1;
  public static final int SCAN_MODE_LOW_LATENCY = 2;
  public static final int SCAN_MODE_LOW_POWER = 0;
  public static final int SCAN_MODE_OPPORTUNISTIC = -1;
  public static final int SCAN_RESULT_TYPE_ABBREVIATED = 1;
  public static final int SCAN_RESULT_TYPE_FULL = 0;
  private int mCallbackType;
  private int mMatchMode;
  private int mNumOfMatchesPerFilter;
  private long mReportDelayMillis;
  private int mScanMode;
  private int mScanResultType;
  
  private ScanSettings(int paramInt1, int paramInt2, int paramInt3, long paramLong, int paramInt4, int paramInt5)
  {
    this.mScanMode = paramInt1;
    this.mCallbackType = paramInt2;
    this.mScanResultType = paramInt3;
    this.mReportDelayMillis = paramLong;
    this.mNumOfMatchesPerFilter = paramInt5;
    this.mMatchMode = paramInt4;
  }
  
  private ScanSettings(Parcel paramParcel)
  {
    this.mScanMode = paramParcel.readInt();
    this.mCallbackType = paramParcel.readInt();
    this.mScanResultType = paramParcel.readInt();
    this.mReportDelayMillis = paramParcel.readLong();
    this.mMatchMode = paramParcel.readInt();
    this.mNumOfMatchesPerFilter = paramParcel.readInt();
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public int getCallbackType()
  {
    return this.mCallbackType;
  }
  
  public int getMatchMode()
  {
    return this.mMatchMode;
  }
  
  public int getNumOfMatches()
  {
    return this.mNumOfMatchesPerFilter;
  }
  
  public long getReportDelayMillis()
  {
    return this.mReportDelayMillis;
  }
  
  public int getScanMode()
  {
    return this.mScanMode;
  }
  
  public int getScanResultType()
  {
    return this.mScanResultType;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mScanMode);
    paramParcel.writeInt(this.mCallbackType);
    paramParcel.writeInt(this.mScanResultType);
    paramParcel.writeLong(this.mReportDelayMillis);
    paramParcel.writeInt(this.mMatchMode);
    paramParcel.writeInt(this.mNumOfMatchesPerFilter);
  }
  
  public static final class Builder
  {
    private int mCallbackType = 1;
    private int mMatchMode = 1;
    private int mNumOfMatchesPerFilter = 3;
    private long mReportDelayMillis = 0L;
    private int mScanMode = 0;
    private int mScanResultType = 0;
    
    private boolean isValidCallbackType(int paramInt)
    {
      if ((paramInt == 1) || (paramInt == 2)) {}
      while (paramInt == 4) {
        return true;
      }
      return paramInt == 6;
    }
    
    public ScanSettings build()
    {
      return new ScanSettings(this.mScanMode, this.mCallbackType, this.mScanResultType, this.mReportDelayMillis, this.mMatchMode, this.mNumOfMatchesPerFilter, null);
    }
    
    public Builder setCallbackType(int paramInt)
    {
      if (!isValidCallbackType(paramInt)) {
        throw new IllegalArgumentException("invalid callback type - " + paramInt);
      }
      this.mCallbackType = paramInt;
      return this;
    }
    
    public Builder setMatchMode(int paramInt)
    {
      if ((paramInt < 1) || (paramInt > 2)) {
        throw new IllegalArgumentException("invalid matchMode " + paramInt);
      }
      this.mMatchMode = paramInt;
      return this;
    }
    
    public Builder setNumOfMatches(int paramInt)
    {
      if ((paramInt < 1) || (paramInt > 3)) {
        throw new IllegalArgumentException("invalid numOfMatches " + paramInt);
      }
      this.mNumOfMatchesPerFilter = paramInt;
      return this;
    }
    
    public Builder setReportDelay(long paramLong)
    {
      if (paramLong < 0L) {
        throw new IllegalArgumentException("reportDelay must be > 0");
      }
      this.mReportDelayMillis = paramLong;
      return this;
    }
    
    public Builder setScanMode(int paramInt)
    {
      if ((paramInt < -1) || (paramInt > 2)) {
        throw new IllegalArgumentException("invalid scan mode " + paramInt);
      }
      this.mScanMode = paramInt;
      return this;
    }
    
    public Builder setScanResultType(int paramInt)
    {
      if ((paramInt < 0) || (paramInt > 1)) {
        throw new IllegalArgumentException("invalid scanResultType - " + paramInt);
      }
      this.mScanResultType = paramInt;
      return this;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/bluetooth/le/ScanSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */