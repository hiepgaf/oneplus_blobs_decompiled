package android.media;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.Objects;

public final class AudioFocusInfo
  implements Parcelable
{
  public static final Parcelable.Creator<AudioFocusInfo> CREATOR = new Parcelable.Creator()
  {
    public AudioFocusInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      return new AudioFocusInfo((AudioAttributes)AudioAttributes.CREATOR.createFromParcel(paramAnonymousParcel), paramAnonymousParcel.readString(), paramAnonymousParcel.readString(), paramAnonymousParcel.readInt(), paramAnonymousParcel.readInt(), paramAnonymousParcel.readInt());
    }
    
    public AudioFocusInfo[] newArray(int paramAnonymousInt)
    {
      return new AudioFocusInfo[paramAnonymousInt];
    }
  };
  private AudioAttributes mAttributes;
  private String mClientId;
  private int mFlags;
  private int mGainRequest;
  private int mLossReceived;
  private String mPackageName;
  
  public AudioFocusInfo(AudioAttributes paramAudioAttributes, String paramString1, String paramString2, int paramInt1, int paramInt2, int paramInt3)
  {
    AudioAttributes localAudioAttributes = paramAudioAttributes;
    if (paramAudioAttributes == null) {
      localAudioAttributes = new AudioAttributes.Builder().build();
    }
    this.mAttributes = localAudioAttributes;
    paramAudioAttributes = paramString1;
    if (paramString1 == null) {
      paramAudioAttributes = "";
    }
    this.mClientId = paramAudioAttributes;
    paramAudioAttributes = paramString2;
    if (paramString2 == null) {
      paramAudioAttributes = "";
    }
    this.mPackageName = paramAudioAttributes;
    this.mGainRequest = paramInt1;
    this.mLossReceived = paramInt2;
    this.mFlags = paramInt3;
  }
  
  public void clearLossReceived()
  {
    this.mLossReceived = 0;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (paramObject == null) {
      return false;
    }
    if (getClass() != paramObject.getClass()) {
      return false;
    }
    paramObject = (AudioFocusInfo)paramObject;
    if (!this.mAttributes.equals(((AudioFocusInfo)paramObject).mAttributes)) {
      return false;
    }
    if (!this.mClientId.equals(((AudioFocusInfo)paramObject).mClientId)) {
      return false;
    }
    if (!this.mPackageName.equals(((AudioFocusInfo)paramObject).mPackageName)) {
      return false;
    }
    if (this.mGainRequest != ((AudioFocusInfo)paramObject).mGainRequest) {
      return false;
    }
    if (this.mLossReceived != ((AudioFocusInfo)paramObject).mLossReceived) {
      return false;
    }
    return this.mFlags == ((AudioFocusInfo)paramObject).mFlags;
  }
  
  public AudioAttributes getAttributes()
  {
    return this.mAttributes;
  }
  
  public String getClientId()
  {
    return this.mClientId;
  }
  
  public int getFlags()
  {
    return this.mFlags;
  }
  
  public int getGainRequest()
  {
    return this.mGainRequest;
  }
  
  public int getLossReceived()
  {
    return this.mLossReceived;
  }
  
  public String getPackageName()
  {
    return this.mPackageName;
  }
  
  public int hashCode()
  {
    return Objects.hash(new Object[] { this.mAttributes, this.mClientId, this.mPackageName, Integer.valueOf(this.mGainRequest), Integer.valueOf(this.mFlags) });
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    this.mAttributes.writeToParcel(paramParcel, paramInt);
    paramParcel.writeString(this.mClientId);
    paramParcel.writeString(this.mPackageName);
    paramParcel.writeInt(this.mGainRequest);
    paramParcel.writeInt(this.mLossReceived);
    paramParcel.writeInt(this.mFlags);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/AudioFocusInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */