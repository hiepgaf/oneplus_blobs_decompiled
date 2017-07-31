package android.media;

public class AudioPatch
{
  private final AudioHandle mHandle;
  private final AudioPortConfig[] mSinks;
  private final AudioPortConfig[] mSources;
  
  AudioPatch(AudioHandle paramAudioHandle, AudioPortConfig[] paramArrayOfAudioPortConfig1, AudioPortConfig[] paramArrayOfAudioPortConfig2)
  {
    this.mHandle = paramAudioHandle;
    this.mSources = paramArrayOfAudioPortConfig1;
    this.mSinks = paramArrayOfAudioPortConfig2;
  }
  
  public int id()
  {
    return this.mHandle.id();
  }
  
  public AudioPortConfig[] sinks()
  {
    return this.mSinks;
  }
  
  public AudioPortConfig[] sources()
  {
    return this.mSources;
  }
  
  public String toString()
  {
    int j = 0;
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("mHandle: ");
    localStringBuilder.append(this.mHandle.toString());
    localStringBuilder.append(" mSources: {");
    AudioPortConfig[] arrayOfAudioPortConfig = this.mSources;
    int k = arrayOfAudioPortConfig.length;
    int i = 0;
    while (i < k)
    {
      localStringBuilder.append(arrayOfAudioPortConfig[i].toString());
      localStringBuilder.append(", ");
      i += 1;
    }
    localStringBuilder.append("} mSinks: {");
    arrayOfAudioPortConfig = this.mSinks;
    k = arrayOfAudioPortConfig.length;
    i = j;
    while (i < k)
    {
      localStringBuilder.append(arrayOfAudioPortConfig[i].toString());
      localStringBuilder.append(", ");
      i += 1;
    }
    localStringBuilder.append("}");
    return localStringBuilder.toString();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/AudioPatch.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */