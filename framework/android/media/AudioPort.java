package android.media;

public class AudioPort
{
  public static final int ROLE_NONE = 0;
  public static final int ROLE_SINK = 2;
  public static final int ROLE_SOURCE = 1;
  private static final String TAG = "AudioPort";
  public static final int TYPE_DEVICE = 1;
  public static final int TYPE_NONE = 0;
  public static final int TYPE_SESSION = 3;
  public static final int TYPE_SUBMIX = 2;
  private AudioPortConfig mActiveConfig;
  private final int[] mChannelIndexMasks;
  private final int[] mChannelMasks;
  private final int[] mFormats;
  private final AudioGain[] mGains;
  AudioHandle mHandle;
  private final String mName;
  protected final int mRole;
  private final int[] mSamplingRates;
  
  AudioPort(AudioHandle paramAudioHandle, int paramInt, String paramString, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int[] paramArrayOfInt3, int[] paramArrayOfInt4, AudioGain[] paramArrayOfAudioGain)
  {
    this.mHandle = paramAudioHandle;
    this.mRole = paramInt;
    this.mName = paramString;
    this.mSamplingRates = paramArrayOfInt1;
    this.mChannelMasks = paramArrayOfInt2;
    this.mChannelIndexMasks = paramArrayOfInt3;
    this.mFormats = paramArrayOfInt4;
    this.mGains = paramArrayOfAudioGain;
  }
  
  public AudioPortConfig activeConfig()
  {
    return this.mActiveConfig;
  }
  
  public AudioPortConfig buildConfig(int paramInt1, int paramInt2, int paramInt3, AudioGainConfig paramAudioGainConfig)
  {
    return new AudioPortConfig(this, paramInt1, paramInt2, paramInt3, paramAudioGainConfig);
  }
  
  public int[] channelIndexMasks()
  {
    return this.mChannelIndexMasks;
  }
  
  public int[] channelMasks()
  {
    return this.mChannelMasks;
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject != null) && ((paramObject instanceof AudioPort)))
    {
      paramObject = (AudioPort)paramObject;
      return this.mHandle.equals(((AudioPort)paramObject).handle());
    }
    return false;
  }
  
  public int[] formats()
  {
    return this.mFormats;
  }
  
  AudioGain gain(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= this.mGains.length)) {
      return null;
    }
    return this.mGains[paramInt];
  }
  
  public AudioGain[] gains()
  {
    return this.mGains;
  }
  
  AudioHandle handle()
  {
    return this.mHandle;
  }
  
  public int hashCode()
  {
    return this.mHandle.hashCode();
  }
  
  public int id()
  {
    return this.mHandle.id();
  }
  
  public String name()
  {
    return this.mName;
  }
  
  public int role()
  {
    return this.mRole;
  }
  
  public int[] samplingRates()
  {
    return this.mSamplingRates;
  }
  
  public String toString()
  {
    String str = Integer.toString(this.mRole);
    switch (this.mRole)
    {
    }
    for (;;)
    {
      return "{mHandle: " + this.mHandle + ", mRole: " + str + "}";
      str = "NONE";
      continue;
      str = "SOURCE";
      continue;
      str = "SINK";
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/AudioPort.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */