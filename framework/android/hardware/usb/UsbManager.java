package android.hardware.usb;

import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.os.Process;
import android.os.RemoteException;
import android.util.Log;
import com.android.internal.util.Preconditions;
import java.util.HashMap;
import java.util.Iterator;

public class UsbManager
{
  public static final String ACTION_USB_ACCESSORY_ATTACHED = "android.hardware.usb.action.USB_ACCESSORY_ATTACHED";
  public static final String ACTION_USB_ACCESSORY_DETACHED = "android.hardware.usb.action.USB_ACCESSORY_DETACHED";
  public static final String ACTION_USB_DEVICE_ATTACHED = "android.hardware.usb.action.USB_DEVICE_ATTACHED";
  public static final String ACTION_USB_DEVICE_DETACHED = "android.hardware.usb.action.USB_DEVICE_DETACHED";
  public static final String ACTION_USB_PORT_CHANGED = "android.hardware.usb.action.USB_PORT_CHANGED";
  public static final String ACTION_USB_STATE = "android.hardware.usb.action.USB_STATE";
  public static final String EXTRA_ACCESSORY = "accessory";
  public static final String EXTRA_DEVICE = "device";
  public static final String EXTRA_PERMISSION_GRANTED = "permission";
  public static final String EXTRA_PORT = "port";
  public static final String EXTRA_PORT_STATUS = "portStatus";
  private static final String TAG = "UsbManager";
  public static final String USB_CONFIGURED = "configured";
  public static final String USB_CONNECTED = "connected";
  public static final String USB_DATA_UNLOCKED = "unlocked";
  public static final String USB_FUNCTION_ACCESSORY = "accessory";
  public static final String USB_FUNCTION_ADB = "adb";
  public static final String USB_FUNCTION_AUDIO_SOURCE = "audio_source";
  public static final String USB_FUNCTION_CHARGING = "charging";
  public static final String USB_FUNCTION_MIDI = "midi";
  public static final String USB_FUNCTION_MTP = "mtp";
  public static final String USB_FUNCTION_NONE = "none";
  public static final String USB_FUNCTION_PTP = "ptp";
  public static final String USB_FUNCTION_RNDIS = "rndis";
  public static final String USB_HOST_CONNECTED = "host_connected";
  private final Context mContext;
  private final IUsbManager mService;
  
  public UsbManager(Context paramContext, IUsbManager paramIUsbManager)
  {
    this.mContext = paramContext;
    this.mService = paramIUsbManager;
  }
  
  public static String addFunction(String paramString1, String paramString2)
  {
    if ("none".equals(paramString1)) {
      return paramString2;
    }
    String str = paramString1;
    if (!containsFunction(paramString1, paramString2))
    {
      str = paramString1;
      if (paramString1.length() > 0) {
        str = paramString1 + ",";
      }
      str = str + paramString2;
    }
    return str;
  }
  
  public static boolean containsFunction(String paramString1, String paramString2)
  {
    int i = paramString1.indexOf(paramString2);
    if (i < 0) {
      return false;
    }
    if ((i > 0) && (paramString1.charAt(i - 1) != ',')) {
      return false;
    }
    i += paramString2.length();
    return (i >= paramString1.length()) || (paramString1.charAt(i) == ',');
  }
  
  public static String removeFunction(String paramString1, String paramString2)
  {
    paramString1 = paramString1.split(",");
    int i = 0;
    while (i < paramString1.length)
    {
      if (paramString2.equals(paramString1[i])) {
        paramString1[i] = null;
      }
      i += 1;
    }
    if ((paramString1.length == 1) && (paramString1[0] == null)) {
      return "none";
    }
    paramString2 = new StringBuilder();
    i = 0;
    while (i < paramString1.length)
    {
      String str = paramString1[i];
      if (str != null)
      {
        if (paramString2.length() > 0) {
          paramString2.append(",");
        }
        paramString2.append(str);
      }
      i += 1;
    }
    return paramString2.toString();
  }
  
  public UsbAccessory[] getAccessoryList()
  {
    try
    {
      UsbAccessory localUsbAccessory = this.mService.getCurrentAccessory();
      if (localUsbAccessory == null) {
        return null;
      }
      return new UsbAccessory[] { localUsbAccessory };
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public HashMap<String, UsbDevice> getDeviceList()
  {
    Bundle localBundle = new Bundle();
    try
    {
      this.mService.getDeviceList(localBundle);
      HashMap localHashMap = new HashMap();
      Iterator localIterator = localBundle.keySet().iterator();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        localHashMap.put(str, (UsbDevice)localBundle.get(str));
      }
      return localHashMap;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public UsbPortStatus getPortStatus(UsbPort paramUsbPort)
  {
    Preconditions.checkNotNull(paramUsbPort, "port must not be null");
    try
    {
      paramUsbPort = this.mService.getPortStatus(paramUsbPort.getId());
      return paramUsbPort;
    }
    catch (RemoteException paramUsbPort)
    {
      throw paramUsbPort.rethrowFromSystemServer();
    }
  }
  
  public UsbPort[] getPorts()
  {
    try
    {
      UsbPort[] arrayOfUsbPort = this.mService.getPorts();
      return arrayOfUsbPort;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void grantPermission(UsbDevice paramUsbDevice)
  {
    try
    {
      this.mService.grantDevicePermission(paramUsbDevice, Process.myUid());
      return;
    }
    catch (RemoteException paramUsbDevice)
    {
      throw paramUsbDevice.rethrowFromSystemServer();
    }
  }
  
  public void grantPermission(UsbDevice paramUsbDevice, String paramString)
  {
    try
    {
      int i = this.mContext.getPackageManager().getPackageUidAsUser(paramString, this.mContext.getUserId());
      this.mService.grantDevicePermission(paramUsbDevice, i);
      return;
    }
    catch (RemoteException paramUsbDevice)
    {
      throw paramUsbDevice.rethrowFromSystemServer();
    }
    catch (PackageManager.NameNotFoundException paramUsbDevice)
    {
      Log.e("UsbManager", "Package " + paramString + " not found.", paramUsbDevice);
    }
  }
  
  public boolean hasPermission(UsbAccessory paramUsbAccessory)
  {
    try
    {
      boolean bool = this.mService.hasAccessoryPermission(paramUsbAccessory);
      return bool;
    }
    catch (RemoteException paramUsbAccessory)
    {
      throw paramUsbAccessory.rethrowFromSystemServer();
    }
  }
  
  public boolean hasPermission(UsbDevice paramUsbDevice)
  {
    try
    {
      boolean bool = this.mService.hasDevicePermission(paramUsbDevice);
      return bool;
    }
    catch (RemoteException paramUsbDevice)
    {
      throw paramUsbDevice.rethrowFromSystemServer();
    }
  }
  
  public boolean isFunctionEnabled(String paramString)
  {
    try
    {
      boolean bool = this.mService.isFunctionEnabled(paramString);
      return bool;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public boolean isUsbDataUnlocked()
  {
    try
    {
      boolean bool = this.mService.isUsbDataUnlocked();
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("UsbManager", "RemoteException in isUsbDataUnlocked", localRemoteException);
    }
    return false;
  }
  
  public ParcelFileDescriptor openAccessory(UsbAccessory paramUsbAccessory)
  {
    try
    {
      paramUsbAccessory = this.mService.openAccessory(paramUsbAccessory);
      return paramUsbAccessory;
    }
    catch (RemoteException paramUsbAccessory)
    {
      throw paramUsbAccessory.rethrowFromSystemServer();
    }
  }
  
  public UsbDeviceConnection openDevice(UsbDevice paramUsbDevice)
  {
    try
    {
      String str = paramUsbDevice.getDeviceName();
      ParcelFileDescriptor localParcelFileDescriptor = this.mService.openDevice(str);
      if (localParcelFileDescriptor != null)
      {
        paramUsbDevice = new UsbDeviceConnection(paramUsbDevice);
        boolean bool = paramUsbDevice.open(str, localParcelFileDescriptor, this.mContext);
        localParcelFileDescriptor.close();
        if (bool) {
          return paramUsbDevice;
        }
      }
    }
    catch (Exception paramUsbDevice)
    {
      Log.e("UsbManager", "exception in UsbManager.openDevice", paramUsbDevice);
    }
    return null;
  }
  
  public void requestPermission(UsbAccessory paramUsbAccessory, PendingIntent paramPendingIntent)
  {
    try
    {
      this.mService.requestAccessoryPermission(paramUsbAccessory, this.mContext.getPackageName(), paramPendingIntent);
      return;
    }
    catch (RemoteException paramUsbAccessory)
    {
      throw paramUsbAccessory.rethrowFromSystemServer();
    }
  }
  
  public void requestPermission(UsbDevice paramUsbDevice, PendingIntent paramPendingIntent)
  {
    try
    {
      this.mService.requestDevicePermission(paramUsbDevice, this.mContext.getPackageName(), paramPendingIntent);
      return;
    }
    catch (RemoteException paramUsbDevice)
    {
      throw paramUsbDevice.rethrowFromSystemServer();
    }
  }
  
  public void setCurrentFunction(String paramString)
  {
    try
    {
      this.mService.setCurrentFunction(paramString);
      return;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public void setPortRoles(UsbPort paramUsbPort, int paramInt1, int paramInt2)
  {
    Preconditions.checkNotNull(paramUsbPort, "port must not be null");
    UsbPort.checkRoles(paramInt1, paramInt2);
    try
    {
      this.mService.setPortRoles(paramUsbPort.getId(), paramInt1, paramInt2);
      return;
    }
    catch (RemoteException paramUsbPort)
    {
      throw paramUsbPort.rethrowFromSystemServer();
    }
  }
  
  public void setUsbDataUnlocked(boolean paramBoolean)
  {
    try
    {
      this.mService.setUsbDataUnlocked(paramBoolean);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/usb/UsbManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */