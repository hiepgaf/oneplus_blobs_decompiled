package com.oneplus.io;

import android.hardware.usb.UsbDevice;
import com.oneplus.base.EventArgs;

public class UsbDeviceEventArgs
  extends EventArgs
{
  private final UsbDevice m_Device;
  
  public UsbDeviceEventArgs(UsbDevice paramUsbDevice)
  {
    this.m_Device = paramUsbDevice;
  }
  
  public UsbDevice getDevice()
  {
    return this.m_Device;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/io/UsbDeviceEventArgs.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */