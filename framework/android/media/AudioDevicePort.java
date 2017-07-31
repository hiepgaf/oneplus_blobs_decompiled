package android.media;

public class AudioDevicePort
  extends AudioPort
{
  private final String mAddress;
  private final int mType;
  
  AudioDevicePort(AudioHandle paramAudioHandle, String paramString1, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int[] paramArrayOfInt3, int[] paramArrayOfInt4, AudioGain[] paramArrayOfAudioGain, int paramInt, String paramString2) {}
  
  public String address()
  {
    return this.mAddress;
  }
  
  public AudioDevicePortConfig buildConfig(int paramInt1, int paramInt2, int paramInt3, AudioGainConfig paramAudioGainConfig)
  {
    return new AudioDevicePortConfig(this, paramInt1, paramInt2, paramInt3, paramAudioGainConfig);
  }
  
  public boolean equals(Object paramObject)
  {
    AudioDevicePort localAudioDevicePort;
    if ((paramObject != null) && ((paramObject instanceof AudioDevicePort)))
    {
      localAudioDevicePort = (AudioDevicePort)paramObject;
      if (this.mType != localAudioDevicePort.type()) {
        return false;
      }
    }
    else
    {
      return false;
    }
    if ((this.mAddress == null) && (localAudioDevicePort.address() != null)) {
      return false;
    }
    if (!this.mAddress.equals(localAudioDevicePort.address())) {
      return false;
    }
    return super.equals(paramObject);
  }
  
  public String toString()
  {
    if (this.mRole == 1) {}
    for (String str = AudioSystem.getInputDeviceName(this.mType);; str = AudioSystem.getOutputDeviceName(this.mType)) {
      return "{" + super.toString() + ", mType: " + str + ", mAddress: " + this.mAddress + "}";
    }
  }
  
  public int type()
  {
    return this.mType;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/AudioDevicePort.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */