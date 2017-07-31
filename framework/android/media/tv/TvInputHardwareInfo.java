package android.media.tv;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.Log;

public final class TvInputHardwareInfo
  implements Parcelable
{
  public static final Parcelable.Creator<TvInputHardwareInfo> CREATOR = new Parcelable.Creator()
  {
    public TvInputHardwareInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      try
      {
        TvInputHardwareInfo localTvInputHardwareInfo = new TvInputHardwareInfo(null);
        localTvInputHardwareInfo.readFromParcel(paramAnonymousParcel);
        return localTvInputHardwareInfo;
      }
      catch (Exception paramAnonymousParcel)
      {
        Log.e("TvInputHardwareInfo", "Exception creating TvInputHardwareInfo from parcel", paramAnonymousParcel);
      }
      return null;
    }
    
    public TvInputHardwareInfo[] newArray(int paramAnonymousInt)
    {
      return new TvInputHardwareInfo[paramAnonymousInt];
    }
  };
  static final String TAG = "TvInputHardwareInfo";
  public static final int TV_INPUT_TYPE_COMPONENT = 6;
  public static final int TV_INPUT_TYPE_COMPOSITE = 3;
  public static final int TV_INPUT_TYPE_DISPLAY_PORT = 10;
  public static final int TV_INPUT_TYPE_DVI = 8;
  public static final int TV_INPUT_TYPE_HDMI = 9;
  public static final int TV_INPUT_TYPE_OTHER_HARDWARE = 1;
  public static final int TV_INPUT_TYPE_SCART = 5;
  public static final int TV_INPUT_TYPE_SVIDEO = 4;
  public static final int TV_INPUT_TYPE_TUNER = 2;
  public static final int TV_INPUT_TYPE_VGA = 7;
  private String mAudioAddress;
  private int mAudioType;
  private int mDeviceId;
  private int mHdmiPortId;
  private int mType;
  
  public int describeContents()
  {
    return 0;
  }
  
  public String getAudioAddress()
  {
    return this.mAudioAddress;
  }
  
  public int getAudioType()
  {
    return this.mAudioType;
  }
  
  public int getDeviceId()
  {
    return this.mDeviceId;
  }
  
  public int getHdmiPortId()
  {
    if (this.mType != 9) {
      throw new IllegalStateException();
    }
    return this.mHdmiPortId;
  }
  
  public int getType()
  {
    return this.mType;
  }
  
  public void readFromParcel(Parcel paramParcel)
  {
    this.mDeviceId = paramParcel.readInt();
    this.mType = paramParcel.readInt();
    this.mAudioType = paramParcel.readInt();
    this.mAudioAddress = paramParcel.readString();
    if (this.mType == 9) {
      this.mHdmiPortId = paramParcel.readInt();
    }
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder(128);
    localStringBuilder.append("TvInputHardwareInfo {id=").append(this.mDeviceId);
    localStringBuilder.append(", type=").append(this.mType);
    localStringBuilder.append(", audio_type=").append(this.mAudioType);
    localStringBuilder.append(", audio_addr=").append(this.mAudioAddress);
    if (this.mType == 9) {
      localStringBuilder.append(", hdmi_port=").append(this.mHdmiPortId);
    }
    localStringBuilder.append("}");
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mDeviceId);
    paramParcel.writeInt(this.mType);
    paramParcel.writeInt(this.mAudioType);
    paramParcel.writeString(this.mAudioAddress);
    if (this.mType == 9) {
      paramParcel.writeInt(this.mHdmiPortId);
    }
  }
  
  public static final class Builder
  {
    private String mAudioAddress = "";
    private int mAudioType = 0;
    private Integer mDeviceId = null;
    private Integer mHdmiPortId = null;
    private Integer mType = null;
    
    public Builder audioAddress(String paramString)
    {
      this.mAudioAddress = paramString;
      return this;
    }
    
    public Builder audioType(int paramInt)
    {
      this.mAudioType = paramInt;
      return this;
    }
    
    public TvInputHardwareInfo build()
    {
      if ((this.mDeviceId == null) || (this.mType == null)) {
        throw new UnsupportedOperationException();
      }
      if ((this.mType.intValue() == 9) && (this.mHdmiPortId == null)) {}
      while ((this.mType.intValue() != 9) && (this.mHdmiPortId != null)) {
        throw new UnsupportedOperationException();
      }
      TvInputHardwareInfo localTvInputHardwareInfo = new TvInputHardwareInfo(null);
      TvInputHardwareInfo.-set2(localTvInputHardwareInfo, this.mDeviceId.intValue());
      TvInputHardwareInfo.-set4(localTvInputHardwareInfo, this.mType.intValue());
      TvInputHardwareInfo.-set1(localTvInputHardwareInfo, this.mAudioType);
      if (TvInputHardwareInfo.-get0(localTvInputHardwareInfo) != 0) {
        TvInputHardwareInfo.-set0(localTvInputHardwareInfo, this.mAudioAddress);
      }
      if (this.mHdmiPortId != null) {
        TvInputHardwareInfo.-set3(localTvInputHardwareInfo, this.mHdmiPortId.intValue());
      }
      return localTvInputHardwareInfo;
    }
    
    public Builder deviceId(int paramInt)
    {
      this.mDeviceId = Integer.valueOf(paramInt);
      return this;
    }
    
    public Builder hdmiPortId(int paramInt)
    {
      this.mHdmiPortId = Integer.valueOf(paramInt);
      return this;
    }
    
    public Builder type(int paramInt)
    {
      this.mType = Integer.valueOf(paramInt);
      return this;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/tv/TvInputHardwareInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */