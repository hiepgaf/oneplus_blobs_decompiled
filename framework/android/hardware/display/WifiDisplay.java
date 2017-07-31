package android.hardware.display;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import libcore.util.Objects;

public final class WifiDisplay
  implements Parcelable
{
  public static final Parcelable.Creator<WifiDisplay> CREATOR = new Parcelable.Creator()
  {
    public WifiDisplay createFromParcel(Parcel paramAnonymousParcel)
    {
      String str1 = paramAnonymousParcel.readString();
      String str2 = paramAnonymousParcel.readString();
      String str3 = paramAnonymousParcel.readString();
      boolean bool1;
      boolean bool2;
      if (paramAnonymousParcel.readInt() != 0)
      {
        bool1 = true;
        if (paramAnonymousParcel.readInt() == 0) {
          break label69;
        }
        bool2 = true;
        label36:
        if (paramAnonymousParcel.readInt() == 0) {
          break label74;
        }
      }
      label69:
      label74:
      for (boolean bool3 = true;; bool3 = false)
      {
        return new WifiDisplay(str1, str2, str3, bool1, bool2, bool3);
        bool1 = false;
        break;
        bool2 = false;
        break label36;
      }
    }
    
    public WifiDisplay[] newArray(int paramAnonymousInt)
    {
      if (paramAnonymousInt == 0) {
        return WifiDisplay.EMPTY_ARRAY;
      }
      return new WifiDisplay[paramAnonymousInt];
    }
  };
  public static final WifiDisplay[] EMPTY_ARRAY = new WifiDisplay[0];
  private final boolean mCanConnect;
  private final String mDeviceAddress;
  private final String mDeviceAlias;
  private final String mDeviceName;
  private final boolean mIsAvailable;
  private final boolean mIsRemembered;
  
  public WifiDisplay(String paramString1, String paramString2, String paramString3, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    if (paramString1 == null) {
      throw new IllegalArgumentException("deviceAddress must not be null");
    }
    if (paramString2 == null) {
      throw new IllegalArgumentException("deviceName must not be null");
    }
    this.mDeviceAddress = paramString1;
    this.mDeviceName = paramString2;
    this.mDeviceAlias = paramString3;
    this.mIsAvailable = paramBoolean1;
    this.mCanConnect = paramBoolean2;
    this.mIsRemembered = paramBoolean3;
  }
  
  public boolean canConnect()
  {
    return this.mCanConnect;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(WifiDisplay paramWifiDisplay)
  {
    if ((paramWifiDisplay != null) && (this.mDeviceAddress.equals(paramWifiDisplay.mDeviceAddress)) && (this.mDeviceName.equals(paramWifiDisplay.mDeviceName))) {
      return Objects.equal(this.mDeviceAlias, paramWifiDisplay.mDeviceAlias);
    }
    return false;
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof WifiDisplay)) {
      return equals((WifiDisplay)paramObject);
    }
    return false;
  }
  
  public String getDeviceAddress()
  {
    return this.mDeviceAddress;
  }
  
  public String getDeviceAlias()
  {
    return this.mDeviceAlias;
  }
  
  public String getDeviceName()
  {
    return this.mDeviceName;
  }
  
  public String getFriendlyDisplayName()
  {
    if (this.mDeviceAlias != null) {
      return this.mDeviceAlias;
    }
    return this.mDeviceName;
  }
  
  public boolean hasSameAddress(WifiDisplay paramWifiDisplay)
  {
    if (paramWifiDisplay != null) {
      return this.mDeviceAddress.equals(paramWifiDisplay.mDeviceAddress);
    }
    return false;
  }
  
  public int hashCode()
  {
    return this.mDeviceAddress.hashCode();
  }
  
  public boolean isAvailable()
  {
    return this.mIsAvailable;
  }
  
  public boolean isRemembered()
  {
    return this.mIsRemembered;
  }
  
  public String toString()
  {
    String str2 = this.mDeviceName + " (" + this.mDeviceAddress + ")";
    String str1 = str2;
    if (this.mDeviceAlias != null) {
      str1 = str2 + ", alias " + this.mDeviceAlias;
    }
    return str1 + ", isAvailable " + this.mIsAvailable + ", canConnect " + this.mCanConnect + ", isRemembered " + this.mIsRemembered;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = 1;
    paramParcel.writeString(this.mDeviceAddress);
    paramParcel.writeString(this.mDeviceName);
    paramParcel.writeString(this.mDeviceAlias);
    if (this.mIsAvailable)
    {
      paramInt = 1;
      paramParcel.writeInt(paramInt);
      if (!this.mCanConnect) {
        break label74;
      }
      paramInt = 1;
      label49:
      paramParcel.writeInt(paramInt);
      if (!this.mIsRemembered) {
        break label79;
      }
    }
    label74:
    label79:
    for (paramInt = i;; paramInt = 0)
    {
      paramParcel.writeInt(paramInt);
      return;
      paramInt = 0;
      break;
      paramInt = 0;
      break label49;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/display/WifiDisplay.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */