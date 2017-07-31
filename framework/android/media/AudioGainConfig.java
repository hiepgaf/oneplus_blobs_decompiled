package android.media;

public class AudioGainConfig
{
  private final int mChannelMask;
  AudioGain mGain;
  private final int mIndex;
  private final int mMode;
  private final int mRampDurationMs;
  private final int[] mValues;
  
  AudioGainConfig(int paramInt1, AudioGain paramAudioGain, int paramInt2, int paramInt3, int[] paramArrayOfInt, int paramInt4)
  {
    this.mIndex = paramInt1;
    this.mGain = paramAudioGain;
    this.mMode = paramInt2;
    this.mChannelMask = paramInt3;
    this.mValues = paramArrayOfInt;
    this.mRampDurationMs = paramInt4;
  }
  
  public int channelMask()
  {
    return this.mChannelMask;
  }
  
  int index()
  {
    return this.mIndex;
  }
  
  public int mode()
  {
    return this.mMode;
  }
  
  public int rampDurationMs()
  {
    return this.mRampDurationMs;
  }
  
  public int[] values()
  {
    return this.mValues;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/AudioGainConfig.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */