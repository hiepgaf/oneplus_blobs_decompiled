package android.media;

public class AudioDevicePortConfig
  extends AudioPortConfig
{
  AudioDevicePortConfig(AudioDevicePort paramAudioDevicePort, int paramInt1, int paramInt2, int paramInt3, AudioGainConfig paramAudioGainConfig)
  {
    super(paramAudioDevicePort, paramInt1, paramInt2, paramInt3, paramAudioGainConfig);
  }
  
  AudioDevicePortConfig(AudioDevicePortConfig paramAudioDevicePortConfig)
  {
    this(paramAudioDevicePortConfig.port(), paramAudioDevicePortConfig.samplingRate(), paramAudioDevicePortConfig.channelMask(), paramAudioDevicePortConfig.format(), paramAudioDevicePortConfig.gain());
  }
  
  public AudioDevicePort port()
  {
    return (AudioDevicePort)this.mPort;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/AudioDevicePortConfig.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */