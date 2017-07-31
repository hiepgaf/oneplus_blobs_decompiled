package android.media;

public class AudioMixPortConfig
  extends AudioPortConfig
{
  AudioMixPortConfig(AudioMixPort paramAudioMixPort, int paramInt1, int paramInt2, int paramInt3, AudioGainConfig paramAudioGainConfig)
  {
    super(paramAudioMixPort, paramInt1, paramInt2, paramInt3, paramAudioGainConfig);
  }
  
  public AudioMixPort port()
  {
    return (AudioMixPort)this.mPort;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/AudioMixPortConfig.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */