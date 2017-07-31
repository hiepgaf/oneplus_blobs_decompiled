package android.media;

public class AudioMixPort
  extends AudioPort
{
  private final int mIoHandle;
  
  AudioMixPort(AudioHandle paramAudioHandle, int paramInt1, int paramInt2, String paramString, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int[] paramArrayOfInt3, int[] paramArrayOfInt4, AudioGain[] paramArrayOfAudioGain)
  {
    super(paramAudioHandle, paramInt2, paramString, paramArrayOfInt1, paramArrayOfInt2, paramArrayOfInt3, paramArrayOfInt4, paramArrayOfAudioGain);
    this.mIoHandle = paramInt1;
  }
  
  public AudioMixPortConfig buildConfig(int paramInt1, int paramInt2, int paramInt3, AudioGainConfig paramAudioGainConfig)
  {
    return new AudioMixPortConfig(this, paramInt1, paramInt2, paramInt3, paramAudioGainConfig);
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject != null) && ((paramObject instanceof AudioMixPort)))
    {
      AudioMixPort localAudioMixPort = (AudioMixPort)paramObject;
      if (this.mIoHandle != localAudioMixPort.ioHandle()) {
        return false;
      }
    }
    else
    {
      return false;
    }
    return super.equals(paramObject);
  }
  
  public int ioHandle()
  {
    return this.mIoHandle;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/AudioMixPort.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */