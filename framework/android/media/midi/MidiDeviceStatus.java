package android.media.midi;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class MidiDeviceStatus
  implements Parcelable
{
  public static final Parcelable.Creator<MidiDeviceStatus> CREATOR = new Parcelable.Creator()
  {
    public MidiDeviceStatus createFromParcel(Parcel paramAnonymousParcel)
    {
      return new MidiDeviceStatus((MidiDeviceInfo)paramAnonymousParcel.readParcelable(MidiDeviceInfo.class.getClassLoader()), paramAnonymousParcel.createBooleanArray(), paramAnonymousParcel.createIntArray());
    }
    
    public MidiDeviceStatus[] newArray(int paramAnonymousInt)
    {
      return new MidiDeviceStatus[paramAnonymousInt];
    }
  };
  private static final String TAG = "MidiDeviceStatus";
  private final MidiDeviceInfo mDeviceInfo;
  private final boolean[] mInputPortOpen;
  private final int[] mOutputPortOpenCount;
  
  public MidiDeviceStatus(MidiDeviceInfo paramMidiDeviceInfo)
  {
    this.mDeviceInfo = paramMidiDeviceInfo;
    this.mInputPortOpen = new boolean[paramMidiDeviceInfo.getInputPortCount()];
    this.mOutputPortOpenCount = new int[paramMidiDeviceInfo.getOutputPortCount()];
  }
  
  public MidiDeviceStatus(MidiDeviceInfo paramMidiDeviceInfo, boolean[] paramArrayOfBoolean, int[] paramArrayOfInt)
  {
    this.mDeviceInfo = paramMidiDeviceInfo;
    this.mInputPortOpen = new boolean[paramArrayOfBoolean.length];
    System.arraycopy(paramArrayOfBoolean, 0, this.mInputPortOpen, 0, paramArrayOfBoolean.length);
    this.mOutputPortOpenCount = new int[paramArrayOfInt.length];
    System.arraycopy(paramArrayOfInt, 0, this.mOutputPortOpenCount, 0, paramArrayOfInt.length);
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public MidiDeviceInfo getDeviceInfo()
  {
    return this.mDeviceInfo;
  }
  
  public int getOutputPortOpenCount(int paramInt)
  {
    return this.mOutputPortOpenCount[paramInt];
  }
  
  public boolean isInputPortOpen(int paramInt)
  {
    return this.mInputPortOpen[paramInt];
  }
  
  public String toString()
  {
    int k = this.mDeviceInfo.getInputPortCount();
    int j = this.mDeviceInfo.getOutputPortCount();
    StringBuilder localStringBuilder = new StringBuilder("mInputPortOpen=[");
    int i = 0;
    while (i < k)
    {
      localStringBuilder.append(this.mInputPortOpen[i]);
      if (i < k - 1) {
        localStringBuilder.append(",");
      }
      i += 1;
    }
    localStringBuilder.append("] mOutputPortOpenCount=[");
    i = 0;
    while (i < j)
    {
      localStringBuilder.append(this.mOutputPortOpenCount[i]);
      if (i < j - 1) {
        localStringBuilder.append(",");
      }
      i += 1;
    }
    localStringBuilder.append("]");
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeParcelable(this.mDeviceInfo, paramInt);
    paramParcel.writeBooleanArray(this.mInputPortOpen);
    paramParcel.writeIntArray(this.mOutputPortOpenCount);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/midi/MidiDeviceStatus.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */