package android.hardware.usb;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class UsbPortStatus
  implements Parcelable
{
  public static final Parcelable.Creator<UsbPortStatus> CREATOR = new Parcelable.Creator()
  {
    public UsbPortStatus createFromParcel(Parcel paramAnonymousParcel)
    {
      return new UsbPortStatus(paramAnonymousParcel.readInt(), paramAnonymousParcel.readInt(), paramAnonymousParcel.readInt(), paramAnonymousParcel.readInt());
    }
    
    public UsbPortStatus[] newArray(int paramAnonymousInt)
    {
      return new UsbPortStatus[paramAnonymousInt];
    }
  };
  private final int mCurrentDataRole;
  private final int mCurrentMode;
  private final int mCurrentPowerRole;
  private final int mSupportedRoleCombinations;
  
  public UsbPortStatus(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this.mCurrentMode = paramInt1;
    this.mCurrentPowerRole = paramInt2;
    this.mCurrentDataRole = paramInt3;
    this.mSupportedRoleCombinations = paramInt4;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public int getCurrentDataRole()
  {
    return this.mCurrentDataRole;
  }
  
  public int getCurrentMode()
  {
    return this.mCurrentMode;
  }
  
  public int getCurrentPowerRole()
  {
    return this.mCurrentPowerRole;
  }
  
  public int getSupportedRoleCombinations()
  {
    return this.mSupportedRoleCombinations;
  }
  
  public boolean isConnected()
  {
    boolean bool = false;
    if (this.mCurrentMode != 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean isRoleCombinationSupported(int paramInt1, int paramInt2)
  {
    boolean bool = false;
    if ((this.mSupportedRoleCombinations & UsbPort.combineRolesAsBit(paramInt1, paramInt2)) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public String toString()
  {
    return "UsbPortStatus{connected=" + isConnected() + ", currentMode=" + UsbPort.modeToString(this.mCurrentMode) + ", currentPowerRole=" + UsbPort.powerRoleToString(this.mCurrentPowerRole) + ", currentDataRole=" + UsbPort.dataRoleToString(this.mCurrentDataRole) + ", supportedRoleCombinations=" + UsbPort.roleCombinationsToString(this.mSupportedRoleCombinations) + "}";
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mCurrentMode);
    paramParcel.writeInt(this.mCurrentPowerRole);
    paramParcel.writeInt(this.mCurrentDataRole);
    paramParcel.writeInt(this.mSupportedRoleCombinations);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/usb/UsbPortStatus.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */