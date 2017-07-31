package android.hardware.hdmi;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class HdmiHotplugEvent
  implements Parcelable
{
  public static final Parcelable.Creator<HdmiHotplugEvent> CREATOR = new Parcelable.Creator()
  {
    public HdmiHotplugEvent createFromParcel(Parcel paramAnonymousParcel)
    {
      int i = paramAnonymousParcel.readInt();
      if (paramAnonymousParcel.readByte() == 1) {}
      for (boolean bool = true;; bool = false) {
        return new HdmiHotplugEvent(i, bool);
      }
    }
    
    public HdmiHotplugEvent[] newArray(int paramAnonymousInt)
    {
      return new HdmiHotplugEvent[paramAnonymousInt];
    }
  };
  private final boolean mConnected;
  private final int mPort;
  
  public HdmiHotplugEvent(int paramInt, boolean paramBoolean)
  {
    this.mPort = paramInt;
    this.mConnected = paramBoolean;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public int getPort()
  {
    return this.mPort;
  }
  
  public boolean isConnected()
  {
    return this.mConnected;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mPort);
    if (this.mConnected) {}
    for (paramInt = 1;; paramInt = 0)
    {
      paramParcel.writeByte((byte)paramInt);
      return;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/hdmi/HdmiHotplugEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */