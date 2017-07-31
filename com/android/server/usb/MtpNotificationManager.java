package com.android.server.usb;

import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbInterface;
import android.os.Bundle;
import android.os.UserHandle;

class MtpNotificationManager
{
  private static final String ACTION_OPEN_IN_APPS = "com.android.server.usb.ACTION_OPEN_IN_APPS";
  private static final int PROTOCOL_MTP = 0;
  private static final int PROTOCOL_PTP = 1;
  private static final int SUBCLASS_MTP = 255;
  private static final int SUBCLASS_STILL_IMAGE_CAPTURE = 1;
  private static final String TAG = "UsbMtpNotificationManager";
  private final Context mContext;
  private final OnOpenInAppListener mListener;
  
  MtpNotificationManager(Context paramContext, OnOpenInAppListener paramOnOpenInAppListener)
  {
    this.mContext = paramContext;
    this.mListener = paramOnOpenInAppListener;
    paramContext.registerReceiver(new Receiver(null), new IntentFilter("com.android.server.usb.ACTION_OPEN_IN_APPS"));
  }
  
  private static boolean isMtpDevice(UsbDevice paramUsbDevice)
  {
    int i = 0;
    while (i < paramUsbDevice.getInterfaceCount())
    {
      UsbInterface localUsbInterface = paramUsbDevice.getInterface(i);
      if ((localUsbInterface.getInterfaceClass() == 6) && (localUsbInterface.getInterfaceSubclass() == 1) && (localUsbInterface.getInterfaceProtocol() == 1)) {
        return true;
      }
      if ((localUsbInterface.getInterfaceClass() == 255) && (localUsbInterface.getInterfaceSubclass() == 255) && (localUsbInterface.getInterfaceProtocol() == 0) && ("MTP".equals(localUsbInterface.getName()))) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  static boolean shouldShowNotification(PackageManager paramPackageManager, UsbDevice paramUsbDevice)
  {
    if (!paramPackageManager.hasSystemFeature("android.hardware.type.automotive")) {
      return isMtpDevice(paramUsbDevice);
    }
    return false;
  }
  
  void hideNotification(int paramInt)
  {
    ((NotificationManager)this.mContext.getSystemService(NotificationManager.class)).cancel("UsbMtpNotificationManager", paramInt);
  }
  
  void showNotification(UsbDevice paramUsbDevice)
  {
    Object localObject2 = this.mContext.getResources();
    Object localObject1 = ((Resources)localObject2).getString(17040902, new Object[] { paramUsbDevice.getProductName() });
    localObject2 = ((Resources)localObject2).getString(17040903);
    localObject1 = new Notification.Builder(this.mContext).setContentTitle((CharSequence)localObject1).setContentText((CharSequence)localObject2).setSmallIcon(17303294).setCategory("sys");
    localObject2 = new Intent("com.android.server.usb.ACTION_OPEN_IN_APPS");
    ((Intent)localObject2).putExtra("device", paramUsbDevice);
    ((Intent)localObject2).addFlags(1342177280);
    ((Notification.Builder)localObject1).setContentIntent(PendingIntent.getBroadcastAsUser(this.mContext, paramUsbDevice.getDeviceId(), (Intent)localObject2, 134217728, UserHandle.SYSTEM));
    localObject1 = ((Notification.Builder)localObject1).build();
    ((Notification)localObject1).flags |= 0x100;
    ((NotificationManager)this.mContext.getSystemService(NotificationManager.class)).notify("UsbMtpNotificationManager", paramUsbDevice.getDeviceId(), (Notification)localObject1);
  }
  
  static abstract interface OnOpenInAppListener
  {
    public abstract void onOpenInApp(UsbDevice paramUsbDevice);
  }
  
  private class Receiver
    extends BroadcastReceiver
  {
    private Receiver() {}
    
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      paramContext = (UsbDevice)paramIntent.getExtras().getParcelable("device");
      if (paramContext == null) {
        return;
      }
      if (paramIntent.getAction().equals("com.android.server.usb.ACTION_OPEN_IN_APPS")) {
        MtpNotificationManager.-get0(MtpNotificationManager.this).onOpenInApp(paramContext);
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/usb/MtpNotificationManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */