package android.media;

public class AudioGain
{
  public static final int MODE_CHANNELS = 2;
  public static final int MODE_JOINT = 1;
  public static final int MODE_RAMP = 4;
  private final int mChannelMask;
  private final int mDefaultValue;
  private final int mIndex;
  private final int mMaxValue;
  private final int mMinValue;
  private final int mMode;
  private final int mRampDurationMaxMs;
  private final int mRampDurationMinMs;
  private final int mStepValue;
  
  AudioGain(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9)
  {
    this.mIndex = paramInt1;
    this.mMode = paramInt2;
    this.mChannelMask = paramInt3;
    this.mMinValue = paramInt4;
    this.mMaxValue = paramInt5;
    this.mDefaultValue = paramInt6;
    this.mStepValue = paramInt7;
    this.mRampDurationMinMs = paramInt8;
    this.mRampDurationMaxMs = paramInt9;
  }
  
  public AudioGainConfig buildConfig(int paramInt1, int paramInt2, int[] paramArrayOfInt, int paramInt3)
  {
    return new AudioGainConfig(this.mIndex, this, paramInt1, paramInt2, paramArrayOfInt, paramInt3);
  }
  
  public int channelMask()
  {
    return this.mChannelMask;
  }
  
  public int defaultValue()
  {
    return this.mDefaultValue;
  }
  
  public int maxValue()
  {
    return this.mMaxValue;
  }
  
  public int minValue()
  {
    return this.mMinValue;
  }
  
  public int mode()
  {
    return this.mMode;
  }
  
  public int rampDurationMaxMs()
  {
    return this.mRampDurationMaxMs;
  }
  
  public int rampDurationMinMs()
  {
    return this.mRampDurationMinMs;
  }
  
  public int stepValue()
  {
    return this.mStepValue;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/AudioGain.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */