package android.media;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.Log;
import java.util.ArrayList;
import java.util.Objects;

public final class AudioRecordingConfiguration
  implements Parcelable
{
  public static final Parcelable.Creator<AudioRecordingConfiguration> CREATOR = new Parcelable.Creator()
  {
    public AudioRecordingConfiguration createFromParcel(Parcel paramAnonymousParcel)
    {
      return new AudioRecordingConfiguration(paramAnonymousParcel, null);
    }
    
    public AudioRecordingConfiguration[] newArray(int paramAnonymousInt)
    {
      return new AudioRecordingConfiguration[paramAnonymousInt];
    }
  };
  private static final String TAG = new String("AudioRecordingConfiguration");
  private final AudioFormat mClientFormat;
  private final int mClientSource;
  private final AudioFormat mDeviceFormat;
  private final int mPatchHandle;
  private final int mSessionId;
  
  public AudioRecordingConfiguration(int paramInt1, int paramInt2, AudioFormat paramAudioFormat1, AudioFormat paramAudioFormat2, int paramInt3)
  {
    this.mSessionId = paramInt1;
    this.mClientSource = paramInt2;
    this.mClientFormat = paramAudioFormat1;
    this.mDeviceFormat = paramAudioFormat2;
    this.mPatchHandle = paramInt3;
  }
  
  private AudioRecordingConfiguration(Parcel paramParcel)
  {
    this.mSessionId = paramParcel.readInt();
    this.mClientSource = paramParcel.readInt();
    this.mClientFormat = ((AudioFormat)AudioFormat.CREATOR.createFromParcel(paramParcel));
    this.mDeviceFormat = ((AudioFormat)AudioFormat.CREATOR.createFromParcel(paramParcel));
    this.mPatchHandle = paramParcel.readInt();
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool2 = false;
    if (this == paramObject) {
      return true;
    }
    if ((paramObject != null) && ((paramObject instanceof AudioRecordingConfiguration)))
    {
      paramObject = (AudioRecordingConfiguration)paramObject;
      boolean bool1 = bool2;
      if (this.mSessionId == ((AudioRecordingConfiguration)paramObject).mSessionId)
      {
        bool1 = bool2;
        if (this.mClientSource == ((AudioRecordingConfiguration)paramObject).mClientSource)
        {
          bool1 = bool2;
          if (this.mPatchHandle == ((AudioRecordingConfiguration)paramObject).mPatchHandle)
          {
            bool1 = bool2;
            if (this.mClientFormat.equals(((AudioRecordingConfiguration)paramObject).mClientFormat)) {
              bool1 = this.mDeviceFormat.equals(((AudioRecordingConfiguration)paramObject).mDeviceFormat);
            }
          }
        }
      }
      return bool1;
    }
    return false;
  }
  
  public AudioDeviceInfo getAudioDevice()
  {
    Object localObject = new ArrayList();
    if (AudioManager.listAudioPatches((ArrayList)localObject) != 0)
    {
      Log.e(TAG, "Error retrieving list of audio patches");
      return null;
    }
    int i = 0;
    while (i < ((ArrayList)localObject).size())
    {
      AudioPatch localAudioPatch = (AudioPatch)((ArrayList)localObject).get(i);
      if (localAudioPatch.id() == this.mPatchHandle)
      {
        localObject = localAudioPatch.sources();
        if ((localObject == null) || (localObject.length <= 0)) {
          break;
        }
        int j = localObject[0].port().id();
        localObject = AudioManager.getDevicesStatic(1);
        i = 0;
        while (i < localObject.length)
        {
          if (localObject[i].getId() == j) {
            return localObject[i];
          }
          i += 1;
        }
      }
      i += 1;
    }
    Log.e(TAG, "Couldn't find device for recording, did recording end already?");
    return null;
  }
  
  public int getClientAudioSessionId()
  {
    return this.mSessionId;
  }
  
  public int getClientAudioSource()
  {
    return this.mClientSource;
  }
  
  public AudioFormat getClientFormat()
  {
    return this.mClientFormat;
  }
  
  public AudioFormat getFormat()
  {
    return this.mDeviceFormat;
  }
  
  public int hashCode()
  {
    return Objects.hash(new Object[] { Integer.valueOf(this.mSessionId), Integer.valueOf(this.mClientSource) });
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mSessionId);
    paramParcel.writeInt(this.mClientSource);
    this.mClientFormat.writeToParcel(paramParcel, 0);
    this.mDeviceFormat.writeToParcel(paramParcel, 0);
    paramParcel.writeInt(this.mPatchHandle);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/AudioRecordingConfiguration.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */