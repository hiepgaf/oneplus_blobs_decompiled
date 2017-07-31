package android.bluetooth;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.UUID;

public final class BluetoothHeadsetClientCall
  implements Parcelable
{
  public static final int CALL_STATE_ACTIVE = 0;
  public static final int CALL_STATE_ALERTING = 3;
  public static final int CALL_STATE_DIALING = 2;
  public static final int CALL_STATE_HELD = 1;
  public static final int CALL_STATE_HELD_BY_RESPONSE_AND_HOLD = 6;
  public static final int CALL_STATE_INCOMING = 4;
  public static final int CALL_STATE_TERMINATED = 7;
  public static final int CALL_STATE_WAITING = 5;
  public static final Parcelable.Creator<BluetoothHeadsetClientCall> CREATOR = new Parcelable.Creator()
  {
    public BluetoothHeadsetClientCall createFromParcel(Parcel paramAnonymousParcel)
    {
      boolean bool2 = true;
      BluetoothDevice localBluetoothDevice = (BluetoothDevice)paramAnonymousParcel.readParcelable(null);
      int i = paramAnonymousParcel.readInt();
      UUID localUUID = UUID.fromString(paramAnonymousParcel.readString());
      int j = paramAnonymousParcel.readInt();
      String str = paramAnonymousParcel.readString();
      boolean bool1;
      if (paramAnonymousParcel.readInt() == 1)
      {
        bool1 = true;
        if (paramAnonymousParcel.readInt() != 1) {
          break label83;
        }
      }
      for (;;)
      {
        return new BluetoothHeadsetClientCall(localBluetoothDevice, i, localUUID, j, str, bool1, bool2);
        bool1 = false;
        break;
        label83:
        bool2 = false;
      }
    }
    
    public BluetoothHeadsetClientCall[] newArray(int paramAnonymousInt)
    {
      return new BluetoothHeadsetClientCall[paramAnonymousInt];
    }
  };
  private final BluetoothDevice mDevice;
  private final int mId;
  private boolean mMultiParty;
  private String mNumber;
  private final boolean mOutgoing;
  private int mState;
  private final UUID mUUID;
  
  public BluetoothHeadsetClientCall(BluetoothDevice paramBluetoothDevice, int paramInt1, int paramInt2, String paramString, boolean paramBoolean1, boolean paramBoolean2)
  {
    this(paramBluetoothDevice, paramInt1, UUID.randomUUID(), paramInt2, paramString, paramBoolean1, paramBoolean2);
  }
  
  public BluetoothHeadsetClientCall(BluetoothDevice paramBluetoothDevice, int paramInt1, UUID paramUUID, int paramInt2, String paramString, boolean paramBoolean1, boolean paramBoolean2)
  {
    this.mDevice = paramBluetoothDevice;
    this.mId = paramInt1;
    this.mUUID = paramUUID;
    this.mState = paramInt2;
    if (paramString != null) {}
    for (;;)
    {
      this.mNumber = paramString;
      this.mMultiParty = paramBoolean1;
      this.mOutgoing = paramBoolean2;
      return;
      paramString = "";
    }
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public BluetoothDevice getDevice()
  {
    return this.mDevice;
  }
  
  public int getId()
  {
    return this.mId;
  }
  
  public String getNumber()
  {
    return this.mNumber;
  }
  
  public int getState()
  {
    return this.mState;
  }
  
  public UUID getUUID()
  {
    return this.mUUID;
  }
  
  public boolean isMultiParty()
  {
    return this.mMultiParty;
  }
  
  public boolean isOutgoing()
  {
    return this.mOutgoing;
  }
  
  public void setMultiParty(boolean paramBoolean)
  {
    this.mMultiParty = paramBoolean;
  }
  
  public void setNumber(String paramString)
  {
    this.mNumber = paramString;
  }
  
  public void setState(int paramInt)
  {
    this.mState = paramInt;
  }
  
  public String toString()
  {
    return toString(false);
  }
  
  public String toString(boolean paramBoolean)
  {
    StringBuilder localStringBuilder = new StringBuilder("BluetoothHeadsetClientCall{mDevice: ");
    if (paramBoolean)
    {
      localObject = this.mDevice;
      localStringBuilder.append(localObject);
      localStringBuilder.append(", mId: ");
      localStringBuilder.append(this.mId);
      localStringBuilder.append(", mUUID: ");
      localStringBuilder.append(this.mUUID);
      localStringBuilder.append(", mState: ");
      switch (this.mState)
      {
      default: 
        localStringBuilder.append(this.mState);
        label125:
        localStringBuilder.append(", mNumber: ");
        if (!paramBoolean) {
          break;
        }
      }
    }
    for (Object localObject = this.mNumber;; localObject = Integer.valueOf(this.mNumber.hashCode()))
    {
      localStringBuilder.append(localObject);
      localStringBuilder.append(", mMultiParty: ");
      localStringBuilder.append(this.mMultiParty);
      localStringBuilder.append(", mOutgoing: ");
      localStringBuilder.append(this.mOutgoing);
      localStringBuilder.append("}");
      return localStringBuilder.toString();
      localObject = Integer.valueOf(this.mDevice.hashCode());
      break;
      localStringBuilder.append("ACTIVE");
      break label125;
      localStringBuilder.append("HELD");
      break label125;
      localStringBuilder.append("DIALING");
      break label125;
      localStringBuilder.append("ALERTING");
      break label125;
      localStringBuilder.append("INCOMING");
      break label125;
      localStringBuilder.append("WAITING");
      break label125;
      localStringBuilder.append("HELD_BY_RESPONSE_AND_HOLD");
      break label125;
      localStringBuilder.append("TERMINATED");
      break label125;
    }
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = 1;
    paramParcel.writeParcelable(this.mDevice, 0);
    paramParcel.writeInt(this.mId);
    paramParcel.writeString(this.mUUID.toString());
    paramParcel.writeInt(this.mState);
    paramParcel.writeString(this.mNumber);
    if (this.mMultiParty)
    {
      paramInt = 1;
      paramParcel.writeInt(paramInt);
      if (!this.mOutgoing) {
        break label80;
      }
    }
    label80:
    for (paramInt = i;; paramInt = 0)
    {
      paramParcel.writeInt(paramInt);
      return;
      paramInt = 0;
      break;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/bluetooth/BluetoothHeadsetClientCall.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */