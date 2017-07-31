package android.media;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseIntArray;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public final class AudioAttributes
  implements Parcelable
{
  private static final int ALL_PARCEL_FLAGS = 1;
  private static final int ATTR_PARCEL_IS_NULL_BUNDLE = -1977;
  private static final int ATTR_PARCEL_IS_VALID_BUNDLE = 1980;
  public static final int CONTENT_TYPE_MOVIE = 3;
  public static final int CONTENT_TYPE_MUSIC = 2;
  public static final int CONTENT_TYPE_SONIFICATION = 4;
  public static final int CONTENT_TYPE_SPEECH = 1;
  public static final int CONTENT_TYPE_UNKNOWN = 0;
  public static final Parcelable.Creator<AudioAttributes> CREATOR = new Parcelable.Creator()
  {
    public AudioAttributes createFromParcel(Parcel paramAnonymousParcel)
    {
      return new AudioAttributes(paramAnonymousParcel, null);
    }
    
    public AudioAttributes[] newArray(int paramAnonymousInt)
    {
      return new AudioAttributes[paramAnonymousInt];
    }
  };
  private static final int FLAG_ALL = 511;
  private static final int FLAG_ALL_PUBLIC = 273;
  public static final int FLAG_AUDIBILITY_ENFORCED = 1;
  public static final int FLAG_BEACON = 8;
  public static final int FLAG_BYPASS_INTERRUPTION_POLICY = 64;
  public static final int FLAG_BYPASS_MUTE = 128;
  public static final int FLAG_HW_AV_SYNC = 16;
  public static final int FLAG_HW_HOTWORD = 32;
  public static final int FLAG_LOW_LATENCY = 256;
  public static final int FLAG_SCO = 4;
  public static final int FLAG_SECURE = 2;
  public static final int FLATTEN_TAGS = 1;
  public static final int[] SDK_USAGES;
  public static final int SUPPRESSIBLE_CALL = 2;
  public static final int SUPPRESSIBLE_NOTIFICATION = 1;
  public static final SparseIntArray SUPPRESSIBLE_USAGES = new SparseIntArray();
  private static final String TAG = "AudioAttributes";
  public static final int USAGE_ALARM = 4;
  public static final int USAGE_ASSISTANCE_ACCESSIBILITY = 11;
  public static final int USAGE_ASSISTANCE_NAVIGATION_GUIDANCE = 12;
  public static final int USAGE_ASSISTANCE_SONIFICATION = 13;
  public static final int USAGE_GAME = 14;
  public static final int USAGE_MEDIA = 1;
  public static final int USAGE_NOTIFICATION = 5;
  public static final int USAGE_NOTIFICATION_COMMUNICATION_DELAYED = 9;
  public static final int USAGE_NOTIFICATION_COMMUNICATION_INSTANT = 8;
  public static final int USAGE_NOTIFICATION_COMMUNICATION_REQUEST = 7;
  public static final int USAGE_NOTIFICATION_EVENT = 10;
  public static final int USAGE_NOTIFICATION_RINGTONE = 6;
  public static final int USAGE_UNKNOWN = 0;
  public static final int USAGE_VIRTUAL_SOURCE = 15;
  public static final int USAGE_VOICE_COMMUNICATION = 2;
  public static final int USAGE_VOICE_COMMUNICATION_SIGNALLING = 3;
  private Bundle mBundle;
  private int mContentType = 0;
  private int mFlags = 0;
  private String mFormattedTags;
  private int mSource = -1;
  private HashSet<String> mTags;
  private int mUsage = 0;
  
  static
  {
    SUPPRESSIBLE_USAGES.put(5, 1);
    SUPPRESSIBLE_USAGES.put(6, 2);
    SUPPRESSIBLE_USAGES.put(7, 2);
    SUPPRESSIBLE_USAGES.put(8, 1);
    SUPPRESSIBLE_USAGES.put(9, 1);
    SUPPRESSIBLE_USAGES.put(10, 1);
    SDK_USAGES = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14 };
  }
  
  private AudioAttributes() {}
  
  private AudioAttributes(Parcel paramParcel)
  {
    this.mUsage = paramParcel.readInt();
    this.mContentType = paramParcel.readInt();
    this.mSource = paramParcel.readInt();
    this.mFlags = paramParcel.readInt();
    int i;
    if ((paramParcel.readInt() & 0x1) == 1)
    {
      i = 1;
      label68:
      this.mTags = new HashSet();
      if (i == 0) {
        break label154;
      }
      this.mFormattedTags = new String(paramParcel.readString());
      this.mTags.add(this.mFormattedTags);
    }
    for (;;)
    {
      switch (paramParcel.readInt())
      {
      default: 
        Log.e("AudioAttributes", "Illegal value unmarshalling AudioAttributes, can't initialize bundle");
        return;
        i = 0;
        break label68;
        label154:
        String[] arrayOfString = paramParcel.readStringArray();
        i = arrayOfString.length - 1;
        while (i >= 0)
        {
          this.mTags.add(arrayOfString[i]);
          i -= 1;
        }
        this.mFormattedTags = TextUtils.join(";", this.mTags);
      }
    }
    this.mBundle = null;
    return;
    this.mBundle = new Bundle(paramParcel.readBundle());
  }
  
  public static int getVolumeControlStream(AudioAttributes paramAudioAttributes)
  {
    if (paramAudioAttributes == null) {
      throw new IllegalArgumentException("Invalid null audio attributes");
    }
    return toVolumeStreamType(true, paramAudioAttributes);
  }
  
  public static int toLegacyStreamType(AudioAttributes paramAudioAttributes)
  {
    return toVolumeStreamType(false, paramAudioAttributes);
  }
  
  private static int toVolumeStreamType(boolean paramBoolean, AudioAttributes paramAudioAttributes)
  {
    if ((paramAudioAttributes.getFlags() & 0x1) == 1)
    {
      if (paramBoolean) {
        return 1;
      }
      return 7;
    }
    if ((paramAudioAttributes.getFlags() & 0x4) == 4)
    {
      if (paramBoolean) {
        return 0;
      }
      return 6;
    }
    switch (paramAudioAttributes.getUsage())
    {
    default: 
      if (paramBoolean) {
        throw new IllegalArgumentException("Unknown usage value " + paramAudioAttributes.getUsage() + " in audio attributes");
      }
      break;
    case 1: 
    case 11: 
    case 12: 
    case 14: 
      return 3;
    case 13: 
      return 1;
    case 2: 
      return 0;
    case 3: 
      if (paramBoolean) {
        return 0;
      }
      return 8;
    case 4: 
      return 4;
    case 6: 
      return 2;
    case 5: 
    case 7: 
    case 8: 
    case 9: 
    case 10: 
      return 5;
    case 0: 
      if (paramBoolean) {
        return Integer.MIN_VALUE;
      }
      return 3;
    }
    return 3;
  }
  
  public static int usageForLegacyStreamType(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return 0;
    case 0: 
      return 2;
    case 1: 
    case 7: 
      return 13;
    case 2: 
      return 6;
    case 3: 
      return 1;
    case 4: 
      return 4;
    case 5: 
      return 5;
    case 6: 
      return 2;
    case 8: 
      return 3;
    }
    return 11;
  }
  
  public static String usageToString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return new String("unknown usage " + paramInt);
    case 0: 
      return new String("USAGE_UNKNOWN");
    case 1: 
      return new String("USAGE_MEDIA");
    case 2: 
      return new String("USAGE_VOICE_COMMUNICATION");
    case 3: 
      return new String("USAGE_VOICE_COMMUNICATION_SIGNALLING");
    case 4: 
      return new String("USAGE_ALARM");
    case 5: 
      return new String("USAGE_NOTIFICATION");
    case 6: 
      return new String("USAGE_NOTIFICATION_RINGTONE");
    case 7: 
      return new String("USAGE_NOTIFICATION_COMMUNICATION_REQUEST");
    case 8: 
      return new String("USAGE_NOTIFICATION_COMMUNICATION_INSTANT");
    case 9: 
      return new String("USAGE_NOTIFICATION_COMMUNICATION_DELAYED");
    case 10: 
      return new String("USAGE_NOTIFICATION_EVENT");
    case 11: 
      return new String("USAGE_ASSISTANCE_ACCESSIBILITY");
    case 12: 
      return new String("USAGE_ASSISTANCE_NAVIGATION_GUIDANCE");
    case 13: 
      return new String("USAGE_ASSISTANCE_SONIFICATION");
    }
    return new String("USAGE_GAME");
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool2 = false;
    if (this == paramObject) {
      return true;
    }
    if ((paramObject == null) || (getClass() != paramObject.getClass())) {
      return false;
    }
    paramObject = (AudioAttributes)paramObject;
    boolean bool1 = bool2;
    if (this.mContentType == ((AudioAttributes)paramObject).mContentType)
    {
      bool1 = bool2;
      if (this.mFlags == ((AudioAttributes)paramObject).mFlags)
      {
        bool1 = bool2;
        if (this.mSource == ((AudioAttributes)paramObject).mSource)
        {
          bool1 = bool2;
          if (this.mUsage == ((AudioAttributes)paramObject).mUsage) {
            bool1 = this.mFormattedTags.equals(((AudioAttributes)paramObject).mFormattedTags);
          }
        }
      }
    }
    return bool1;
  }
  
  public int getAllFlags()
  {
    return this.mFlags & 0x1FF;
  }
  
  public Bundle getBundle()
  {
    if (this.mBundle == null) {
      return this.mBundle;
    }
    return new Bundle(this.mBundle);
  }
  
  public int getCapturePreset()
  {
    return this.mSource;
  }
  
  public int getContentType()
  {
    return this.mContentType;
  }
  
  public int getFlags()
  {
    return this.mFlags & 0x111;
  }
  
  public Set<String> getTags()
  {
    return Collections.unmodifiableSet(this.mTags);
  }
  
  public int getUsage()
  {
    return this.mUsage;
  }
  
  public int hashCode()
  {
    return Objects.hash(new Object[] { Integer.valueOf(this.mContentType), Integer.valueOf(this.mFlags), Integer.valueOf(this.mSource), Integer.valueOf(this.mUsage), this.mFormattedTags, this.mBundle });
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder().append("AudioAttributes: usage=").append(this.mUsage).append(" content=").append(this.mContentType).append(" flags=0x").append(Integer.toHexString(this.mFlags).toUpperCase()).append(" tags=").append(this.mFormattedTags).append(" bundle=");
    if (this.mBundle == null) {}
    for (String str = "null";; str = this.mBundle.toString()) {
      return new String(str);
    }
  }
  
  public String usageToString()
  {
    return usageToString(this.mUsage);
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mUsage);
    paramParcel.writeInt(this.mContentType);
    paramParcel.writeInt(this.mSource);
    paramParcel.writeInt(this.mFlags);
    paramParcel.writeInt(paramInt & 0x1);
    if ((paramInt & 0x1) == 0)
    {
      String[] arrayOfString = new String[this.mTags.size()];
      this.mTags.toArray(arrayOfString);
      paramParcel.writeStringArray(arrayOfString);
    }
    while (this.mBundle == null)
    {
      paramParcel.writeInt(63559);
      return;
      if ((paramInt & 0x1) == 1) {
        paramParcel.writeString(this.mFormattedTags);
      }
    }
    paramParcel.writeInt(1980);
    paramParcel.writeBundle(this.mBundle);
  }
  
  public static class Builder
  {
    private Bundle mBundle;
    private int mContentType = 0;
    private int mFlags = 0;
    private int mSource = -1;
    private HashSet<String> mTags = new HashSet();
    private int mUsage = 0;
    
    public Builder() {}
    
    public Builder(AudioAttributes paramAudioAttributes)
    {
      this.mUsage = AudioAttributes.-get3(paramAudioAttributes);
      this.mContentType = AudioAttributes.-get0(paramAudioAttributes);
      this.mFlags = AudioAttributes.-get1(paramAudioAttributes);
      this.mTags = ((HashSet)AudioAttributes.-get2(paramAudioAttributes).clone());
    }
    
    public Builder addBundle(Bundle paramBundle)
    {
      if (paramBundle == null) {
        throw new IllegalArgumentException("Illegal null bundle");
      }
      if (this.mBundle == null)
      {
        this.mBundle = new Bundle(paramBundle);
        return this;
      }
      this.mBundle.putAll(paramBundle);
      return this;
    }
    
    public Builder addTag(String paramString)
    {
      this.mTags.add(paramString);
      return this;
    }
    
    public AudioAttributes build()
    {
      AudioAttributes localAudioAttributes = new AudioAttributes(null);
      AudioAttributes.-set1(localAudioAttributes, this.mContentType);
      AudioAttributes.-set6(localAudioAttributes, this.mUsage);
      AudioAttributes.-set4(localAudioAttributes, this.mSource);
      AudioAttributes.-set2(localAudioAttributes, this.mFlags);
      AudioAttributes.-set5(localAudioAttributes, (HashSet)this.mTags.clone());
      AudioAttributes.-set3(localAudioAttributes, TextUtils.join(";", this.mTags));
      if (this.mBundle != null) {
        AudioAttributes.-set0(localAudioAttributes, new Bundle(this.mBundle));
      }
      return localAudioAttributes;
    }
    
    public Builder setCapturePreset(int paramInt)
    {
      switch (paramInt)
      {
      case 2: 
      case 3: 
      case 4: 
      case 8: 
      default: 
        Log.e("AudioAttributes", "Invalid capture preset " + paramInt + " for AudioAttributes");
        return this;
      }
      this.mSource = paramInt;
      return this;
    }
    
    public Builder setContentType(int paramInt)
    {
      switch (paramInt)
      {
      default: 
        this.mUsage = 0;
        return this;
      }
      this.mContentType = paramInt;
      return this;
    }
    
    public Builder setFlags(int paramInt)
    {
      this.mFlags |= paramInt & 0x1FF;
      return this;
    }
    
    public Builder setInternalCapturePreset(int paramInt)
    {
      if ((paramInt == 1999) || (paramInt == 8)) {}
      while (paramInt == 1998)
      {
        this.mSource = paramInt;
        return this;
      }
      setCapturePreset(paramInt);
      return this;
    }
    
    public Builder setInternalLegacyStreamType(int paramInt)
    {
      switch (paramInt)
      {
      default: 
        Log.e("AudioAttributes", "Invalid stream type " + paramInt + " for AudioAttributes");
      }
      for (;;)
      {
        this.mUsage = AudioAttributes.usageForLegacyStreamType(paramInt);
        return this;
        this.mContentType = 1;
        continue;
        this.mFlags |= 0x1;
        this.mContentType = 4;
        continue;
        this.mContentType = 4;
        continue;
        this.mContentType = 2;
        continue;
        this.mContentType = 4;
        continue;
        this.mContentType = 4;
        continue;
        this.mContentType = 1;
        this.mFlags |= 0x4;
        continue;
        this.mContentType = 4;
        continue;
        this.mContentType = 1;
      }
    }
    
    public Builder setLegacyStreamType(int paramInt)
    {
      return setInternalLegacyStreamType(paramInt);
    }
    
    public Builder setUsage(int paramInt)
    {
      switch (paramInt)
      {
      default: 
        this.mUsage = 0;
        return this;
      }
      this.mUsage = paramInt;
      return this;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/AudioAttributes.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */