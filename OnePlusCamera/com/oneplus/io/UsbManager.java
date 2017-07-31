package com.oneplus.io;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.os.Handler;
import com.oneplus.base.EventKey;
import com.oneplus.base.Handle;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.component.Component;
import java.util.Collections;
import java.util.List;

public abstract interface UsbManager
  extends Component
{
  public static final EventKey<UsbDeviceEventArgs> EVENT_DEVICE_ATTACHED = new EventKey("DeviceAttached", UsbDeviceEventArgs.class, UsbManager.class);
  public static final EventKey<UsbDeviceEventArgs> EVENT_DEVICE_DETACHED = new EventKey("DeviceDetached", UsbDeviceEventArgs.class, UsbManager.class);
  public static final PropertyKey<List<UsbDevice>> PROP_DEVICE_LIST = new PropertyKey("DeviceList", List.class, UsbManager.class, Collections.EMPTY_LIST);
  
  public abstract boolean isDeviceOpened(UsbDevice paramUsbDevice);
  
  public abstract Handle openDevice(UsbDevice paramUsbDevice, OpenDeviceCallback paramOpenDeviceCallback, Handler paramHandler, int paramInt);
  
  public abstract void requestPermission(UsbDevice paramUsbDevice, PermissionCallback paramPermissionCallback, Handler paramHandler, int paramInt);
  
  public static abstract interface OpenDeviceCallback
  {
    public abstract void onFailed(UsbDevice paramUsbDevice);
    
    public abstract void onOpened(UsbDevice paramUsbDevice, UsbDeviceConnection paramUsbDeviceConnection);
  }
  
  public static abstract interface PermissionCallback
  {
    public abstract void onPermissionRejected(UsbDevice paramUsbDevice);
    
    public abstract void onPermissionRequested(UsbDevice paramUsbDevice);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/io/UsbManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */