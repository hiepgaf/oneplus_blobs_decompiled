package android.net.wifi.p2p;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

public class WifiP2pDeviceList
  implements Parcelable
{
  public static final Parcelable.Creator<WifiP2pDeviceList> CREATOR = new Parcelable.Creator()
  {
    public WifiP2pDeviceList createFromParcel(Parcel paramAnonymousParcel)
    {
      WifiP2pDeviceList localWifiP2pDeviceList = new WifiP2pDeviceList();
      int j = paramAnonymousParcel.readInt();
      int i = 0;
      while (i < j)
      {
        localWifiP2pDeviceList.update((WifiP2pDevice)paramAnonymousParcel.readParcelable(null));
        i += 1;
      }
      return localWifiP2pDeviceList;
    }
    
    public WifiP2pDeviceList[] newArray(int paramAnonymousInt)
    {
      return new WifiP2pDeviceList[paramAnonymousInt];
    }
  };
  private final HashMap<String, WifiP2pDevice> mDevices = new HashMap();
  
  public WifiP2pDeviceList() {}
  
  public WifiP2pDeviceList(WifiP2pDeviceList paramWifiP2pDeviceList)
  {
    if (paramWifiP2pDeviceList != null)
    {
      paramWifiP2pDeviceList = paramWifiP2pDeviceList.getDeviceList().iterator();
      while (paramWifiP2pDeviceList.hasNext())
      {
        WifiP2pDevice localWifiP2pDevice = (WifiP2pDevice)paramWifiP2pDeviceList.next();
        this.mDevices.put(localWifiP2pDevice.deviceAddress, new WifiP2pDevice(localWifiP2pDevice));
      }
    }
  }
  
  public WifiP2pDeviceList(ArrayList<WifiP2pDevice> paramArrayList)
  {
    paramArrayList = paramArrayList.iterator();
    while (paramArrayList.hasNext())
    {
      WifiP2pDevice localWifiP2pDevice = (WifiP2pDevice)paramArrayList.next();
      if (localWifiP2pDevice.deviceAddress != null) {
        this.mDevices.put(localWifiP2pDevice.deviceAddress, new WifiP2pDevice(localWifiP2pDevice));
      }
    }
  }
  
  private void validateDevice(WifiP2pDevice paramWifiP2pDevice)
  {
    if (paramWifiP2pDevice == null) {
      throw new IllegalArgumentException("Null device");
    }
    if (TextUtils.isEmpty(paramWifiP2pDevice.deviceAddress)) {
      throw new IllegalArgumentException("Empty deviceAddress");
    }
  }
  
  private void validateDeviceAddress(String paramString)
  {
    if (TextUtils.isEmpty(paramString)) {
      throw new IllegalArgumentException("Empty deviceAddress");
    }
  }
  
  public boolean clear()
  {
    if (this.mDevices.isEmpty()) {
      return false;
    }
    this.mDevices.clear();
    return true;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public WifiP2pDevice get(String paramString)
  {
    validateDeviceAddress(paramString);
    return (WifiP2pDevice)this.mDevices.get(paramString);
  }
  
  public Collection<WifiP2pDevice> getDeviceList()
  {
    return Collections.unmodifiableCollection(this.mDevices.values());
  }
  
  public boolean isGroupOwner(String paramString)
  {
    validateDeviceAddress(paramString);
    WifiP2pDevice localWifiP2pDevice = (WifiP2pDevice)this.mDevices.get(paramString);
    if (localWifiP2pDevice == null) {
      throw new IllegalArgumentException("Device not found " + paramString);
    }
    return localWifiP2pDevice.isGroupOwner();
  }
  
  public WifiP2pDevice remove(String paramString)
  {
    validateDeviceAddress(paramString);
    return (WifiP2pDevice)this.mDevices.remove(paramString);
  }
  
  public boolean remove(WifiP2pDevice paramWifiP2pDevice)
  {
    validateDevice(paramWifiP2pDevice);
    return this.mDevices.remove(paramWifiP2pDevice.deviceAddress) != null;
  }
  
  public boolean remove(WifiP2pDeviceList paramWifiP2pDeviceList)
  {
    boolean bool = false;
    paramWifiP2pDeviceList = paramWifiP2pDeviceList.mDevices.values().iterator();
    while (paramWifiP2pDeviceList.hasNext()) {
      if (remove((WifiP2pDevice)paramWifiP2pDeviceList.next())) {
        bool = true;
      }
    }
    return bool;
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    Iterator localIterator = this.mDevices.values().iterator();
    while (localIterator.hasNext())
    {
      WifiP2pDevice localWifiP2pDevice = (WifiP2pDevice)localIterator.next();
      localStringBuffer.append("\n").append(localWifiP2pDevice);
    }
    return localStringBuffer.toString();
  }
  
  public void update(WifiP2pDevice paramWifiP2pDevice)
  {
    updateSupplicantDetails(paramWifiP2pDevice);
    ((WifiP2pDevice)this.mDevices.get(paramWifiP2pDevice.deviceAddress)).status = paramWifiP2pDevice.status;
  }
  
  public void updateGroupCapability(String paramString, int paramInt)
  {
    validateDeviceAddress(paramString);
    paramString = (WifiP2pDevice)this.mDevices.get(paramString);
    if (paramString != null) {
      paramString.groupCapability = paramInt;
    }
  }
  
  public void updateStatus(String paramString, int paramInt)
  {
    validateDeviceAddress(paramString);
    paramString = (WifiP2pDevice)this.mDevices.get(paramString);
    if (paramString != null) {
      paramString.status = paramInt;
    }
  }
  
  public void updateSupplicantDetails(WifiP2pDevice paramWifiP2pDevice)
  {
    validateDevice(paramWifiP2pDevice);
    WifiP2pDevice localWifiP2pDevice = (WifiP2pDevice)this.mDevices.get(paramWifiP2pDevice.deviceAddress);
    if (localWifiP2pDevice != null)
    {
      localWifiP2pDevice.deviceName = paramWifiP2pDevice.deviceName;
      localWifiP2pDevice.primaryDeviceType = paramWifiP2pDevice.primaryDeviceType;
      localWifiP2pDevice.secondaryDeviceType = paramWifiP2pDevice.secondaryDeviceType;
      localWifiP2pDevice.wpsConfigMethodsSupported = paramWifiP2pDevice.wpsConfigMethodsSupported;
      localWifiP2pDevice.deviceCapability = paramWifiP2pDevice.deviceCapability;
      localWifiP2pDevice.groupCapability = paramWifiP2pDevice.groupCapability;
      localWifiP2pDevice.wfdInfo = paramWifiP2pDevice.wfdInfo;
      return;
    }
    this.mDevices.put(paramWifiP2pDevice.deviceAddress, paramWifiP2pDevice);
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mDevices.size());
    Iterator localIterator = this.mDevices.values().iterator();
    while (localIterator.hasNext()) {
      paramParcel.writeParcelable((WifiP2pDevice)localIterator.next(), paramInt);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/wifi/p2p/WifiP2pDeviceList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */