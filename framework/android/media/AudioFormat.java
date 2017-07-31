package android.media;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.Arrays;
import java.util.Objects;

public final class AudioFormat
  implements Parcelable
{
  public static final int AUDIO_FORMAT_HAS_PROPERTY_CHANNEL_INDEX_MASK = 8;
  public static final int AUDIO_FORMAT_HAS_PROPERTY_CHANNEL_MASK = 4;
  public static final int AUDIO_FORMAT_HAS_PROPERTY_ENCODING = 1;
  public static final int AUDIO_FORMAT_HAS_PROPERTY_NONE = 0;
  public static final int AUDIO_FORMAT_HAS_PROPERTY_SAMPLE_RATE = 2;
  @Deprecated
  public static final int CHANNEL_CONFIGURATION_DEFAULT = 1;
  @Deprecated
  public static final int CHANNEL_CONFIGURATION_INVALID = 0;
  @Deprecated
  public static final int CHANNEL_CONFIGURATION_MONO = 2;
  @Deprecated
  public static final int CHANNEL_CONFIGURATION_STEREO = 3;
  public static final int CHANNEL_INVALID = 0;
  public static final int CHANNEL_IN_5POINT1 = 252;
  public static final int CHANNEL_IN_BACK = 32;
  public static final int CHANNEL_IN_BACK_PROCESSED = 512;
  public static final int CHANNEL_IN_DEFAULT = 1;
  public static final int CHANNEL_IN_FRONT = 16;
  public static final int CHANNEL_IN_FRONT_BACK = 48;
  public static final int CHANNEL_IN_FRONT_PROCESSED = 256;
  public static final int CHANNEL_IN_LEFT = 4;
  public static final int CHANNEL_IN_LEFT_PROCESSED = 64;
  public static final int CHANNEL_IN_MONO = 16;
  public static final int CHANNEL_IN_PRESSURE = 1024;
  public static final int CHANNEL_IN_RIGHT = 8;
  public static final int CHANNEL_IN_RIGHT_PROCESSED = 128;
  public static final int CHANNEL_IN_STEREO = 12;
  public static final int CHANNEL_IN_VOICE_DNLINK = 32768;
  public static final int CHANNEL_IN_VOICE_UPLINK = 16384;
  public static final int CHANNEL_IN_X_AXIS = 2048;
  public static final int CHANNEL_IN_Y_AXIS = 4096;
  public static final int CHANNEL_IN_Z_AXIS = 8192;
  public static final int CHANNEL_OUT_5POINT1 = 252;
  public static final int CHANNEL_OUT_5POINT1_SIDE = 6204;
  @Deprecated
  public static final int CHANNEL_OUT_7POINT1 = 1020;
  public static final int CHANNEL_OUT_7POINT1_SURROUND = 6396;
  public static final int CHANNEL_OUT_BACK_CENTER = 1024;
  public static final int CHANNEL_OUT_BACK_LEFT = 64;
  public static final int CHANNEL_OUT_BACK_RIGHT = 128;
  public static final int CHANNEL_OUT_DEFAULT = 1;
  public static final int CHANNEL_OUT_FRONT_CENTER = 16;
  public static final int CHANNEL_OUT_FRONT_LEFT = 4;
  public static final int CHANNEL_OUT_FRONT_LEFT_OF_CENTER = 256;
  public static final int CHANNEL_OUT_FRONT_RIGHT = 8;
  public static final int CHANNEL_OUT_FRONT_RIGHT_OF_CENTER = 512;
  public static final int CHANNEL_OUT_LOW_FREQUENCY = 32;
  public static final int CHANNEL_OUT_MONO = 4;
  public static final int CHANNEL_OUT_QUAD = 204;
  public static final int CHANNEL_OUT_QUAD_SIDE = 6156;
  public static final int CHANNEL_OUT_SIDE_LEFT = 2048;
  public static final int CHANNEL_OUT_SIDE_RIGHT = 4096;
  public static final int CHANNEL_OUT_STEREO = 12;
  public static final int CHANNEL_OUT_SURROUND = 1052;
  public static final int CHANNEL_OUT_TOP_BACK_CENTER = 262144;
  public static final int CHANNEL_OUT_TOP_BACK_LEFT = 131072;
  public static final int CHANNEL_OUT_TOP_BACK_RIGHT = 524288;
  public static final int CHANNEL_OUT_TOP_CENTER = 8192;
  public static final int CHANNEL_OUT_TOP_FRONT_CENTER = 32768;
  public static final int CHANNEL_OUT_TOP_FRONT_LEFT = 16384;
  public static final int CHANNEL_OUT_TOP_FRONT_RIGHT = 65536;
  public static final Parcelable.Creator<AudioFormat> CREATOR = new Parcelable.Creator()
  {
    public AudioFormat createFromParcel(Parcel paramAnonymousParcel)
    {
      return new AudioFormat(paramAnonymousParcel, null);
    }
    
    public AudioFormat[] newArray(int paramAnonymousInt)
    {
      return new AudioFormat[paramAnonymousInt];
    }
  };
  public static final int ENCODING_AAC_HE_V1 = 11;
  public static final int ENCODING_AAC_HE_V2 = 12;
  public static final int ENCODING_AAC_LC = 10;
  public static final int ENCODING_AC3 = 5;
  public static final int ENCODING_AMRNB = 100;
  public static final int ENCODING_AMRWB = 101;
  public static final int ENCODING_DEFAULT = 1;
  public static final int ENCODING_DOLBY_TRUEHD = 14;
  public static final int ENCODING_DTS = 7;
  public static final int ENCODING_DTS_HD = 8;
  public static final int ENCODING_EVRC = 102;
  public static final int ENCODING_EVRCB = 103;
  public static final int ENCODING_EVRCNW = 105;
  public static final int ENCODING_EVRCWB = 104;
  public static final int ENCODING_E_AC3 = 6;
  public static final int ENCODING_IEC61937 = 13;
  public static final int ENCODING_INVALID = 0;
  public static final int ENCODING_MP3 = 9;
  public static final int ENCODING_PCM_16BIT = 2;
  public static final int ENCODING_PCM_8BIT = 3;
  public static final int ENCODING_PCM_FLOAT = 4;
  public static final int SAMPLE_RATE_HZ_MAX = 192000;
  public static final int SAMPLE_RATE_HZ_MIN = 4000;
  public static final int SAMPLE_RATE_UNSPECIFIED = 0;
  private int mChannelIndexMask;
  private int mChannelMask;
  private int mEncoding;
  private int mPropertySetMask;
  private int mSampleRate;
  
  public AudioFormat()
  {
    throw new UnsupportedOperationException("There is no valid usage of this constructor");
  }
  
  private AudioFormat(int paramInt) {}
  
  private AudioFormat(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this.mEncoding = paramInt1;
    this.mSampleRate = paramInt2;
    this.mChannelMask = paramInt3;
    this.mChannelIndexMask = paramInt4;
    this.mPropertySetMask = 15;
  }
  
  private AudioFormat(Parcel paramParcel)
  {
    this.mPropertySetMask = paramParcel.readInt();
    this.mEncoding = paramParcel.readInt();
    this.mSampleRate = paramParcel.readInt();
    this.mChannelMask = paramParcel.readInt();
    this.mChannelIndexMask = paramParcel.readInt();
  }
  
  public static int channelCountFromInChannelMask(int paramInt)
  {
    return Integer.bitCount(paramInt);
  }
  
  public static int channelCountFromOutChannelMask(int paramInt)
  {
    return Integer.bitCount(paramInt);
  }
  
  public static int convertChannelOutMaskToNativeMask(int paramInt)
  {
    return paramInt >> 2;
  }
  
  public static int convertNativeChannelMaskToOutMask(int paramInt)
  {
    return paramInt << 2;
  }
  
  public static int[] filterPublicFormats(int[] paramArrayOfInt)
  {
    if (paramArrayOfInt == null) {
      return null;
    }
    paramArrayOfInt = Arrays.copyOf(paramArrayOfInt, paramArrayOfInt.length);
    int j = 0;
    int i = 0;
    while (i < paramArrayOfInt.length)
    {
      int k = j;
      if (isPublicEncoding(paramArrayOfInt[i]))
      {
        if (j != i) {
          paramArrayOfInt[j] = paramArrayOfInt[i];
        }
        k = j + 1;
      }
      i += 1;
      j = k;
    }
    return Arrays.copyOf(paramArrayOfInt, j);
  }
  
  public static int getBytesPerSample(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      throw new IllegalArgumentException("Bad audio format " + paramInt);
    case 3: 
      return 1;
    case 1: 
    case 2: 
    case 13: 
      return 2;
    case 4: 
      return 4;
    case 100: 
      return 32;
    case 101: 
      return 61;
    }
    return 23;
  }
  
  public static int inChannelMaskFromOutChannelMask(int paramInt)
    throws IllegalArgumentException
  {
    if (paramInt == 1) {
      throw new IllegalArgumentException("Illegal CHANNEL_OUT_DEFAULT channel mask for input.");
    }
    switch (channelCountFromOutChannelMask(paramInt))
    {
    default: 
      throw new IllegalArgumentException("Unsupported channel configuration for input.");
    case 1: 
      return 16;
    }
    return 12;
  }
  
  public static boolean isEncodingLinearFrames(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      throw new IllegalArgumentException("Bad audio format " + paramInt);
    case 1: 
    case 2: 
    case 3: 
    case 4: 
    case 13: 
      return true;
    }
    return false;
  }
  
  public static boolean isEncodingLinearPcm(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      throw new IllegalArgumentException("Bad audio format " + paramInt);
    case 1: 
    case 2: 
    case 3: 
    case 4: 
      return true;
    }
    return false;
  }
  
  public static boolean isPublicEncoding(int paramInt)
  {
    switch (paramInt)
    {
    case 9: 
    case 10: 
    case 11: 
    case 12: 
    default: 
      return false;
    }
    return true;
  }
  
  public static boolean isValidEncoding(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return false;
    }
    return true;
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
    if ((paramObject == null) || (getClass() != paramObject.getClass())) {
      return false;
    }
    paramObject = (AudioFormat)paramObject;
    if (this.mPropertySetMask != ((AudioFormat)paramObject).mPropertySetMask) {
      return false;
    }
    if ((((this.mPropertySetMask & 0x1) == 0) || (this.mEncoding == ((AudioFormat)paramObject).mEncoding)) && (((this.mPropertySetMask & 0x2) == 0) || (this.mSampleRate == ((AudioFormat)paramObject).mSampleRate)) && (((this.mPropertySetMask & 0x4) == 0) || (this.mChannelMask == ((AudioFormat)paramObject).mChannelMask))) {
      return ((this.mPropertySetMask & 0x8) == 0) || (this.mChannelIndexMask == ((AudioFormat)paramObject).mChannelIndexMask);
    }
    return false;
  }
  
  public int getChannelCount()
  {
    int j = Integer.bitCount(getChannelIndexMask());
    int k = channelCountFromOutChannelMask(getChannelMask());
    int i;
    if (k == 0) {
      i = j;
    }
    do
    {
      do
      {
        return i;
        i = k;
      } while (k == j);
      i = k;
    } while (j == 0);
    return 0;
  }
  
  public int getChannelIndexMask()
  {
    if ((this.mPropertySetMask & 0x8) == 0) {
      return 0;
    }
    return this.mChannelIndexMask;
  }
  
  public int getChannelMask()
  {
    if ((this.mPropertySetMask & 0x4) == 0) {
      return 0;
    }
    return this.mChannelMask;
  }
  
  public int getEncoding()
  {
    if ((this.mPropertySetMask & 0x1) == 0) {
      return 0;
    }
    return this.mEncoding;
  }
  
  public int getPropertySetMask()
  {
    return this.mPropertySetMask;
  }
  
  public int getSampleRate()
  {
    return this.mSampleRate;
  }
  
  public int hashCode()
  {
    return Objects.hash(new Object[] { Integer.valueOf(this.mPropertySetMask), Integer.valueOf(this.mSampleRate), Integer.valueOf(this.mEncoding), Integer.valueOf(this.mChannelMask), Integer.valueOf(this.mChannelIndexMask) });
  }
  
  public String toString()
  {
    return new String("AudioFormat: props=" + this.mPropertySetMask + " enc=" + this.mEncoding + " chan=0x" + Integer.toHexString(this.mChannelMask).toUpperCase() + " chan_index=0x" + Integer.toHexString(this.mChannelIndexMask).toUpperCase() + " rate=" + this.mSampleRate);
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mPropertySetMask);
    paramParcel.writeInt(this.mEncoding);
    paramParcel.writeInt(this.mSampleRate);
    paramParcel.writeInt(this.mChannelMask);
    paramParcel.writeInt(this.mChannelIndexMask);
  }
  
  public static class Builder
  {
    private int mChannelIndexMask = 0;
    private int mChannelMask = 0;
    private int mEncoding = 0;
    private int mPropertySetMask = 0;
    private int mSampleRate = 0;
    
    public Builder() {}
    
    public Builder(AudioFormat paramAudioFormat)
    {
      this.mEncoding = AudioFormat.-get2(paramAudioFormat);
      this.mSampleRate = AudioFormat.-get4(paramAudioFormat);
      this.mChannelMask = AudioFormat.-get1(paramAudioFormat);
      this.mChannelIndexMask = AudioFormat.-get0(paramAudioFormat);
      this.mPropertySetMask = AudioFormat.-get3(paramAudioFormat);
    }
    
    public AudioFormat build()
    {
      AudioFormat localAudioFormat = new AudioFormat(1980, null);
      AudioFormat.-set2(localAudioFormat, this.mEncoding);
      AudioFormat.-set4(localAudioFormat, this.mSampleRate);
      AudioFormat.-set1(localAudioFormat, this.mChannelMask);
      AudioFormat.-set0(localAudioFormat, this.mChannelIndexMask);
      AudioFormat.-set3(localAudioFormat, this.mPropertySetMask);
      return localAudioFormat;
    }
    
    public Builder setChannelIndexMask(int paramInt)
    {
      if (paramInt == 0) {
        throw new IllegalArgumentException("Invalid zero channel index mask");
      }
      if ((this.mChannelMask != 0) && (Integer.bitCount(paramInt) != Integer.bitCount(this.mChannelMask))) {
        throw new IllegalArgumentException("Mismatched channel count for index mask " + Integer.toHexString(paramInt).toUpperCase());
      }
      this.mChannelIndexMask = paramInt;
      this.mPropertySetMask |= 0x8;
      return this;
    }
    
    public Builder setChannelMask(int paramInt)
    {
      if (paramInt == 0) {
        throw new IllegalArgumentException("Invalid zero channel mask");
      }
      if ((this.mChannelIndexMask != 0) && (Integer.bitCount(paramInt) != Integer.bitCount(this.mChannelIndexMask))) {
        throw new IllegalArgumentException("Mismatched channel count for mask " + Integer.toHexString(paramInt).toUpperCase());
      }
      this.mChannelMask = paramInt;
      this.mPropertySetMask |= 0x4;
      return this;
    }
    
    public Builder setEncoding(int paramInt)
      throws IllegalArgumentException
    {
      switch (paramInt)
      {
      default: 
        throw new IllegalArgumentException("Invalid encoding " + paramInt);
      }
      for (this.mEncoding = 2;; this.mEncoding = paramInt)
      {
        this.mPropertySetMask |= 0x1;
        return this;
      }
    }
    
    public Builder setSampleRate(int paramInt)
      throws IllegalArgumentException
    {
      if (((paramInt < 4000) || (paramInt > 192000)) && (paramInt != 0)) {
        throw new IllegalArgumentException("Invalid sample rate " + paramInt);
      }
      this.mSampleRate = paramInt;
      this.mPropertySetMask |= 0x2;
      return this;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/AudioFormat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */