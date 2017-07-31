package com.android.server.usb;

import android.content.Context;
import android.content.res.Resources;
import android.hardware.usb.UsbConfiguration;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.util.IndentingPrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class UsbHostManager
{
  private static final boolean DEBUG = false;
  private static final String TAG = UsbHostManager.class.getSimpleName();
  private final Context mContext;
  @GuardedBy("mLock")
  private UsbSettingsManager mCurrentSettings;
  private final HashMap<String, UsbDevice> mDevices = new HashMap();
  private final String[] mHostBlacklist;
  private final Object mLock = new Object();
  private UsbConfiguration mNewConfiguration;
  private ArrayList<UsbConfiguration> mNewConfigurations;
  private UsbDevice mNewDevice;
  private ArrayList<UsbEndpoint> mNewEndpoints;
  private UsbInterface mNewInterface;
  private ArrayList<UsbInterface> mNewInterfaces;
  private final UsbAlsaManager mUsbAlsaManager;
  
  public UsbHostManager(Context paramContext, UsbAlsaManager paramUsbAlsaManager)
  {
    this.mContext = paramContext;
    this.mHostBlacklist = paramContext.getResources().getStringArray(17235999);
    this.mUsbAlsaManager = paramUsbAlsaManager;
  }
  
  private void addUsbConfiguration(int paramInt1, String paramString, int paramInt2, int paramInt3)
  {
    if (this.mNewConfiguration != null)
    {
      this.mNewConfiguration.setInterfaces((Parcelable[])this.mNewInterfaces.toArray(new UsbInterface[this.mNewInterfaces.size()]));
      this.mNewInterfaces.clear();
    }
    this.mNewConfiguration = new UsbConfiguration(paramInt1, paramString, paramInt2, paramInt3);
    this.mNewConfigurations.add(this.mNewConfiguration);
  }
  
  private void addUsbEndpoint(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this.mNewEndpoints.add(new UsbEndpoint(paramInt1, paramInt2, paramInt3, paramInt4));
  }
  
  private void addUsbInterface(int paramInt1, String paramString, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    if (this.mNewInterface != null)
    {
      this.mNewInterface.setEndpoints((Parcelable[])this.mNewEndpoints.toArray(new UsbEndpoint[this.mNewEndpoints.size()]));
      this.mNewEndpoints.clear();
    }
    this.mNewInterface = new UsbInterface(paramInt1, paramInt2, paramString, paramInt3, paramInt4, paramInt5);
    this.mNewInterfaces.add(this.mNewInterface);
  }
  
  private boolean beginUsbDeviceAdded(String paramString1, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, String paramString2, String paramString3, int paramInt6, String paramString4)
  {
    if ((isBlackListed(paramString1)) || (isBlackListed(paramInt3, paramInt4, paramInt5))) {
      return false;
    }
    synchronized (this.mLock)
    {
      if (this.mDevices.get(paramString1) != null)
      {
        Slog.w(TAG, "device already on mDevices list: " + paramString1);
        return false;
      }
      if (this.mNewDevice != null)
      {
        Slog.e(TAG, "mNewDevice is not null in endUsbDeviceAdded");
        return false;
      }
      this.mNewDevice = new UsbDevice(paramString1, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramString2, paramString3, Integer.toString(paramInt6 >> 8) + "." + (paramInt6 & 0xFF), paramString4);
      this.mNewConfigurations = new ArrayList();
      this.mNewInterfaces = new ArrayList();
      this.mNewEndpoints = new ArrayList();
      return true;
    }
  }
  
  private void endUsbDeviceAdded()
  {
    if (this.mNewInterface != null) {
      this.mNewInterface.setEndpoints((Parcelable[])this.mNewEndpoints.toArray(new UsbEndpoint[this.mNewEndpoints.size()]));
    }
    if (this.mNewConfiguration != null) {
      this.mNewConfiguration.setInterfaces((Parcelable[])this.mNewInterfaces.toArray(new UsbInterface[this.mNewInterfaces.size()]));
    }
    synchronized (this.mLock)
    {
      if (this.mNewDevice != null)
      {
        this.mNewDevice.setConfigurations((Parcelable[])this.mNewConfigurations.toArray(new UsbConfiguration[this.mNewConfigurations.size()]));
        this.mDevices.put(this.mNewDevice.getDeviceName(), this.mNewDevice);
        Slog.d(TAG, "Added device " + this.mNewDevice);
        getCurrentSettings().deviceAttached(this.mNewDevice);
        this.mUsbAlsaManager.usbDeviceAdded(this.mNewDevice);
        this.mNewDevice = null;
        this.mNewConfigurations = null;
        this.mNewInterfaces = null;
        this.mNewEndpoints = null;
        this.mNewConfiguration = null;
        this.mNewInterface = null;
        return;
      }
      Slog.e(TAG, "mNewDevice is null in endUsbDeviceAdded");
    }
  }
  
  private UsbSettingsManager getCurrentSettings()
  {
    synchronized (this.mLock)
    {
      UsbSettingsManager localUsbSettingsManager = this.mCurrentSettings;
      return localUsbSettingsManager;
    }
  }
  
  private boolean isBlackListed(int paramInt1, int paramInt2, int paramInt3)
  {
    if (paramInt1 == 9) {
      return true;
    }
    return (paramInt1 == 3) && (paramInt2 == 1);
  }
  
  private boolean isBlackListed(String paramString)
  {
    int j = this.mHostBlacklist.length;
    int i = 0;
    while (i < j)
    {
      if (paramString.startsWith(this.mHostBlacklist[i])) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  private native void monitorUsbHostBus();
  
  private native ParcelFileDescriptor nativeOpenDevice(String paramString);
  
  private void usbDeviceRemoved(String paramString)
  {
    synchronized (this.mLock)
    {
      paramString = (UsbDevice)this.mDevices.remove(paramString);
      if (paramString != null)
      {
        this.mUsbAlsaManager.usbDeviceRemoved(paramString);
        getCurrentSettings().deviceDetached(paramString);
      }
      return;
    }
  }
  
  public void dump(IndentingPrintWriter paramIndentingPrintWriter)
  {
    synchronized (this.mLock)
    {
      paramIndentingPrintWriter.println("USB Host State:");
      Iterator localIterator = this.mDevices.keySet().iterator();
      if (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        paramIndentingPrintWriter.println("  " + str + ": " + this.mDevices.get(str));
      }
    }
  }
  
  public void getDeviceList(Bundle paramBundle)
  {
    synchronized (this.mLock)
    {
      Iterator localIterator = this.mDevices.keySet().iterator();
      if (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        paramBundle.putParcelable(str, (Parcelable)this.mDevices.get(str));
      }
    }
  }
  
  public ParcelFileDescriptor openDevice(String paramString)
  {
    synchronized (this.mLock)
    {
      if (isBlackListed(paramString)) {
        throw new SecurityException("USB device is on a restricted bus");
      }
    }
    UsbDevice localUsbDevice = (UsbDevice)this.mDevices.get(paramString);
    if (localUsbDevice == null) {
      throw new IllegalArgumentException("device " + paramString + " does not exist or is restricted");
    }
    getCurrentSettings().checkPermission(localUsbDevice);
    paramString = nativeOpenDevice(paramString);
    return paramString;
  }
  
  public void setCurrentSettings(UsbSettingsManager paramUsbSettingsManager)
  {
    synchronized (this.mLock)
    {
      this.mCurrentSettings = paramUsbSettingsManager;
      return;
    }
  }
  
  public void systemReady()
  {
    synchronized (this.mLock)
    {
      new Thread(null, new Runnable()
      {
        public void run()
        {
          UsbHostManager.-wrap0(UsbHostManager.this);
        }
      }, "UsbService host thread").start();
      return;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/usb/UsbHostManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */