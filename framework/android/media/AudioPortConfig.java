package android.media;

public class AudioPortConfig
{
  static final int CHANNEL_MASK = 2;
  static final int FORMAT = 4;
  static final int GAIN = 8;
  static final int SAMPLE_RATE = 1;
  private final int mChannelMask;
  int mConfigMask;
  private final int mFormat;
  private final AudioGainConfig mGain;
  final AudioPort mPort;
  private final int mSamplingRate;
  
  AudioPortConfig(AudioPort paramAudioPort, int paramInt1, int paramInt2, int paramInt3, AudioGainConfig paramAudioGainConfig)
  {
    this.mPort = paramAudioPort;
    this.mSamplingRate = paramInt1;
    this.mChannelMask = paramInt2;
    this.mFormat = paramInt3;
    this.mGain = paramAudioGainConfig;
    this.mConfigMask = 0;
  }
  
  public int channelMask()
  {
    return this.mChannelMask;
  }
  
  public int format()
  {
    return this.mFormat;
  }
  
  public AudioGainConfig gain()
  {
    return this.mGain;
  }
  
  public AudioPort port()
  {
    return this.mPort;
  }
  
  public int samplingRate()
  {
    return this.mSamplingRate;
  }
  
  public String toString()
  {
    return "{mPort:" + this.mPort + ", mSamplingRate:" + this.mSamplingRate + ", mChannelMask: " + this.mChannelMask + ", mFormat:" + this.mFormat + ", mGain:" + this.mGain + "}";
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/AudioPortConfig.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */