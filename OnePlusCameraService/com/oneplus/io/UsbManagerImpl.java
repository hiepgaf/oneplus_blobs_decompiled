package com.oneplus.io;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.oneplus.base.Handle;
import com.oneplus.base.HandlerUtils;
import com.oneplus.base.Log;
import com.oneplus.base.component.BasicComponent;
import com.oneplus.base.component.ComponentOwner;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

final class UsbManagerImpl
  extends BasicComponent
  implements UsbManager
{
  private static final String ACTION_USB_PERMISSION = "android.mtp.MtpClient.action.USB_PERMISSION";
  private static final int MSG_DEVICE_OPENED = 10000;
  private static final Executor m_IOExecutor = Executors.newFixedThreadPool(1);
  private BroadcastReceiver m_BroadcastReceiver;
  private final Hashtable<UsbDevice, DeviceInfo> m_Devices = new Hashtable();
  private android.hardware.usb.UsbManager m_SysUsbManager;
  
  UsbManagerImpl(ComponentOwner paramComponentOwner)
  {
    super("USB manager", paramComponentOwner, true);
  }
  
  private void closeDevice(final OpenDeviceHandle paramOpenDeviceHandle)
  {
    verifyAccess();
    DeviceInfo localDeviceInfo = (DeviceInfo)this.m_Devices.get(OpenDeviceHandle.-get1(paramOpenDeviceHandle));
    if ((localDeviceInfo != null) && (localDeviceInfo.openHandles.remove(paramOpenDeviceHandle)) && (localDeviceInfo.openHandles.isEmpty()))
    {
      paramOpenDeviceHandle = localDeviceInfo.device.getDeviceName();
      final UsbDeviceConnection localUsbDeviceConnection = localDeviceInfo.connection;
      localDeviceInfo.connection = null;
      Log.w(this.TAG, "closeDevice() - Start closing connection for " + paramOpenDeviceHandle);
      m_IOExecutor.execute(new Runnable()
      {
        public void run()
        {
          try
          {
            Log.w(UsbManagerImpl.-get0(UsbManagerImpl.this), "closeDevice() - Close connection for " + paramOpenDeviceHandle + " [start]");
            localUsbDeviceConnection.close();
            Log.w(UsbManagerImpl.-get0(UsbManagerImpl.this), "closeDevice() - Close connection for " + paramOpenDeviceHandle + " [end]");
            return;
          }
          catch (Throwable localThrowable)
          {
            Log.e(UsbManagerImpl.-get0(UsbManagerImpl.this), "closeDevice() - Fail to close connection for " + paramOpenDeviceHandle, localThrowable);
          }
        }
      });
    }
  }
  
  private void onBroadcastReceived(Intent paramIntent)
  {
    String str = paramIntent.getAction();
    UsbDevice localUsbDevice = (UsbDevice)paramIntent.getParcelableExtra("device");
    if ((str == null) || (localUsbDevice == null)) {
      return;
    }
    if (str.equals("android.hardware.usb.action.USB_DEVICE_ATTACHED")) {
      onDeviceAttached(localUsbDevice);
    }
    do
    {
      return;
      if (str.equals("android.hardware.usb.action.USB_DEVICE_DETACHED"))
      {
        onDeviceDetached(localUsbDevice);
        return;
      }
    } while (!str.equals("android.mtp.MtpClient.action.USB_PERMISSION"));
    onPermissionRequested(localUsbDevice, paramIntent.getBooleanExtra("permission", false));
  }
  
  private void onDeviceAttached(UsbDevice paramUsbDevice)
  {
    Log.w(this.TAG, "onDeviceAttached() - " + paramUsbDevice.getDeviceName());
    synchronized (this.m_Devices)
    {
      if (this.m_Devices.containsKey(paramUsbDevice))
      {
        Log.w(this.TAG, "onDeviceAttached() - Duplicate device");
        return;
      }
      this.m_Devices.put(paramUsbDevice, new DeviceInfo(paramUsbDevice));
      setReadOnly(PROP_DEVICE_LIST, Collections.unmodifiableList(new ArrayList(this.m_Devices.keySet())));
      raise(EVENT_DEVICE_ATTACHED, new UsbDeviceEventArgs(paramUsbDevice));
      return;
    }
  }
  
  private void onDeviceDetached(UsbDevice paramUsbDevice)
  {
    Log.w(this.TAG, "onDeviceDetached() - " + paramUsbDevice.getDeviceName());
    DeviceInfo localDeviceInfo;
    synchronized (this.m_Devices)
    {
      localDeviceInfo = (DeviceInfo)this.m_Devices.remove(paramUsbDevice);
      if (localDeviceInfo == null)
      {
        Log.w(this.TAG, "onDeviceAttached() - Unknown device");
        return;
      }
      i = localDeviceInfo.permissionRequests.size() - 1;
      if (i >= 0)
      {
        ((PermissionRequestInfo)localDeviceInfo.permissionRequests.get(i)).callOnRejected(paramUsbDevice);
        i -= 1;
      }
    }
    int i = localDeviceInfo.openHandles.size() - 1;
    while (i >= 0)
    {
      ((OpenDeviceHandle)localDeviceInfo.openHandles.get(i)).complete();
      i -= 1;
    }
    setReadOnly(PROP_DEVICE_LIST, Collections.unmodifiableList(new ArrayList(this.m_Devices.keySet())));
    raise(EVENT_DEVICE_DETACHED, new UsbDeviceEventArgs(paramUsbDevice));
  }
  
  private void onDeviceOpened(UsbDevice paramUsbDevice, final UsbDeviceConnection paramUsbDeviceConnection)
  {
    DeviceInfo localDeviceInfo = (DeviceInfo)this.m_Devices.get(paramUsbDevice);
    if (localDeviceInfo == null)
    {
      Log.e(this.TAG, "onDeviceOpened() - Unknown device : " + paramUsbDevice.getDeviceName());
      if (paramUsbDeviceConnection != null) {
        m_IOExecutor.execute(new Runnable()
        {
          public void run()
          {
            try
            {
              paramUsbDeviceConnection.close();
              return;
            }
            catch (Throwable localThrowable)
            {
              Log.e(UsbManagerImpl.-get0(UsbManagerImpl.this), "onDeviceOpened() - Fail to close device", localThrowable);
            }
          }
        });
      }
      return;
    }
    if (paramUsbDeviceConnection != null) {}
    for (boolean bool = true;; bool = false)
    {
      Log.w(this.TAG, "onDeviceOpened() - Device : " + paramUsbDevice.getDeviceName() + ", success : " + bool);
      if (!bool) {
        break;
      }
      localDeviceInfo.connection = paramUsbDeviceConnection;
      i = localDeviceInfo.openHandles.size() - 1;
      while (i >= 0)
      {
        ((OpenDeviceHandle)localDeviceInfo.openHandles.get(i)).callOnOpened(paramUsbDeviceConnection);
        i -= 1;
      }
    }
    int i = localDeviceInfo.openHandles.size() - 1;
    while (i >= 0)
    {
      ((OpenDeviceHandle)localDeviceInfo.openHandles.get(i)).callOnFailed();
      i -= 1;
    }
  }
  
  private void onPermissionRequested(UsbDevice paramUsbDevice, boolean paramBoolean)
  {
    DeviceInfo localDeviceInfo = (DeviceInfo)this.m_Devices.get(paramUsbDevice);
    if (localDeviceInfo == null) {
      return;
    }
    Log.w(this.TAG, "onPermissionRequested() - Device : " + paramUsbDevice.getDeviceName() + ", requested : " + paramBoolean);
    if (paramBoolean)
    {
      i = localDeviceInfo.permissionRequests.size() - 1;
      while (i >= 0)
      {
        ((PermissionRequestInfo)localDeviceInfo.permissionRequests.get(i)).callOnRequested(paramUsbDevice);
        i -= 1;
      }
    }
    int i = localDeviceInfo.permissionRequests.size() - 1;
    while (i >= 0)
    {
      ((PermissionRequestInfo)localDeviceInfo.permissionRequests.get(i)).callOnRejected(paramUsbDevice);
      i -= 1;
    }
    localDeviceInfo.permissionRequests.clear();
  }
  
  protected void handleMessage(Message paramMessage)
  {
    switch (paramMessage.what)
    {
    default: 
      super.handleMessage(paramMessage);
      return;
    }
    paramMessage = (Object[])paramMessage.obj;
    onDeviceOpened((UsbDevice)paramMessage[0], (UsbDeviceConnection)paramMessage[1]);
  }
  
  public boolean isDeviceOpened(UsbDevice paramUsbDevice)
  {
    boolean bool2 = false;
    if (paramUsbDevice == null) {
      return false;
    }
    synchronized (this.m_Devices)
    {
      paramUsbDevice = (DeviceInfo)this.m_Devices.get(paramUsbDevice);
      boolean bool1 = bool2;
      if (paramUsbDevice != null)
      {
        paramUsbDevice = paramUsbDevice.connection;
        bool1 = bool2;
        if (paramUsbDevice != null) {
          bool1 = true;
        }
      }
      return bool1;
    }
  }
  
  protected void onDeinitialize()
  {
    this.m_SysUsbManager = null;
    ((Context)get(PROP_OWNER)).unregisterReceiver(this.m_BroadcastReceiver);
    super.onDeinitialize();
  }
  
  protected void onInitialize()
  {
    super.onInitialize();
    Object localObject1 = (Context)get(PROP_OWNER);
    this.m_SysUsbManager = ((android.hardware.usb.UsbManager)((Context)localObject1).getSystemService("usb"));
    this.m_BroadcastReceiver = new BroadcastReceiver()
    {
      public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
      {
        UsbManagerImpl.-wrap1(UsbManagerImpl.this, paramAnonymousIntent);
      }
    };
    Object localObject2 = new IntentFilter();
    ((IntentFilter)localObject2).addAction("android.hardware.usb.action.USB_DEVICE_ATTACHED");
    ((IntentFilter)localObject2).addAction("android.hardware.usb.action.USB_DEVICE_DETACHED");
    ((IntentFilter)localObject2).addAction("android.mtp.MtpClient.action.USB_PERMISSION");
    ((Context)localObject1).registerReceiver(this.m_BroadcastReceiver, (IntentFilter)localObject2);
    try
    {
      localObject1 = this.m_SysUsbManager.getDeviceList().values().iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (UsbDevice)((Iterator)localObject1).next();
        this.m_Devices.put(localObject2, new DeviceInfo((UsbDevice)localObject2));
      }
      setReadOnly(PROP_DEVICE_LIST, Collections.unmodifiableList(new ArrayList(this.m_Devices.keySet())));
    }
    catch (Throwable localThrowable)
    {
      Log.e(this.TAG, "onInitialize() - Fail to setup device list", localThrowable);
      return;
    }
  }
  
  public Handle openDevice(final UsbDevice paramUsbDevice, UsbManager.OpenDeviceCallback paramOpenDeviceCallback, Handler paramHandler, int paramInt)
  {
    verifyAccess();
    if (!isRunningOrInitializing(true)) {
      return null;
    }
    if (paramUsbDevice == null)
    {
      Log.e(this.TAG, "openDevice() - No device to open");
      return null;
    }
    if (paramOpenDeviceCallback == null)
    {
      Log.e(this.TAG, "openDevice() - No call-back");
      return null;
    }
    DeviceInfo localDeviceInfo = (DeviceInfo)this.m_Devices.get(paramUsbDevice);
    final String str = paramUsbDevice.getDeviceName();
    if (localDeviceInfo == null)
    {
      Log.e(this.TAG, "openDevice() - Unknown device : " + str);
      return null;
    }
    paramOpenDeviceCallback = new OpenDeviceHandle(paramUsbDevice, paramOpenDeviceCallback, paramHandler);
    localDeviceInfo.openHandles.add(paramOpenDeviceCallback);
    if (localDeviceInfo.connection != null)
    {
      Log.v(this.TAG, "openDevice() - Use current connection");
      paramOpenDeviceCallback.callOnOpened(localDeviceInfo.connection);
      return paramOpenDeviceCallback;
    }
    if (localDeviceInfo.openHandles.size() == 1)
    {
      Log.w(this.TAG, "openDevice() - Open " + str);
      m_IOExecutor.execute(new Runnable()
      {
        public void run()
        {
          try
          {
            Log.w(UsbManagerImpl.-get0(UsbManagerImpl.this), "openDevice() - Open " + str + " [start]");
            UsbDeviceConnection localUsbDeviceConnection = UsbManagerImpl.-get1(UsbManagerImpl.this).openDevice(paramUsbDevice);
            Log.w(UsbManagerImpl.-get0(UsbManagerImpl.this), "openDevice() - Open " + str + " [end]");
            HandlerUtils.sendMessage(UsbManagerImpl.this, 10000, new Object[] { paramUsbDevice, localUsbDeviceConnection });
            return;
          }
          catch (Throwable localThrowable)
          {
            for (;;)
            {
              Log.e(UsbManagerImpl.-get0(UsbManagerImpl.this), "openDevice() - Fail to open " + str, localThrowable);
              Object localObject = null;
            }
          }
        }
      });
    }
    return paramOpenDeviceCallback;
  }
  
  public void requestPermission(UsbDevice paramUsbDevice, UsbManager.PermissionCallback paramPermissionCallback, Handler paramHandler, int paramInt)
  {
    verifyAccess();
    if (!isRunningOrInitializing(true)) {
      return;
    }
    if (paramUsbDevice == null)
    {
      Log.e(this.TAG, "requestPermission() - No device");
      return;
    }
    paramPermissionCallback = new PermissionRequestInfo(paramPermissionCallback, paramHandler);
    paramHandler = (DeviceInfo)this.m_Devices.get(paramUsbDevice);
    if (paramHandler == null)
    {
      Log.e(this.TAG, "requestPermission() - Unknown device");
      paramPermissionCallback.callOnRejected(paramUsbDevice);
      return;
    }
    if (this.m_SysUsbManager.hasPermission(paramUsbDevice))
    {
      paramPermissionCallback.callOnRequested(paramUsbDevice);
      return;
    }
    paramHandler.permissionRequests.add(paramPermissionCallback);
    if (paramHandler.permissionRequests.size() == 1)
    {
      Log.w(this.TAG, "requestPermission() - Device : " + paramUsbDevice.getDeviceName());
      paramPermissionCallback = (Context)get(PROP_OWNER);
      this.m_SysUsbManager.requestPermission(paramUsbDevice, PendingIntent.getBroadcast(paramPermissionCallback, 0, new Intent("android.mtp.MtpClient.action.USB_PERMISSION"), 0));
    }
  }
  
  private static final class DeviceInfo
  {
    public volatile UsbDeviceConnection connection;
    public final UsbDevice device;
    public final List<UsbManagerImpl.OpenDeviceHandle> openHandles = new ArrayList();
    public final List<UsbManagerImpl.PermissionRequestInfo> permissionRequests = new ArrayList();
    
    public DeviceInfo(UsbDevice paramUsbDevice)
    {
      this.device = paramUsbDevice;
    }
  }
  
  private final class OpenDeviceHandle
    extends Handle
  {
    private final UsbManager.OpenDeviceCallback callback;
    private final Handler callbackHandler;
    private final UsbDevice device;
    
    public OpenDeviceHandle(UsbDevice paramUsbDevice, UsbManager.OpenDeviceCallback paramOpenDeviceCallback, Handler paramHandler)
    {
      super();
      this.device = paramUsbDevice;
      this.callback = paramOpenDeviceCallback;
      this.callbackHandler = paramHandler;
    }
    
    public void callOnFailed()
    {
      if (this.callback == null) {
        return;
      }
      if ((this.callbackHandler != null) && (this.callbackHandler.getLooper().getThread() != Thread.currentThread()))
      {
        this.callbackHandler.post(new Runnable()
        {
          public void run()
          {
            UsbManagerImpl.OpenDeviceHandle.-get0(UsbManagerImpl.OpenDeviceHandle.this).onFailed(UsbManagerImpl.OpenDeviceHandle.-get1(UsbManagerImpl.OpenDeviceHandle.this));
          }
        });
        return;
      }
      this.callback.onFailed(this.device);
    }
    
    public void callOnOpened(final UsbDeviceConnection paramUsbDeviceConnection)
    {
      if (this.callback == null) {
        return;
      }
      if ((this.callbackHandler != null) && (this.callbackHandler.getLooper().getThread() != Thread.currentThread()))
      {
        this.callbackHandler.post(new Runnable()
        {
          public void run()
          {
            UsbManagerImpl.OpenDeviceHandle.-get0(UsbManagerImpl.OpenDeviceHandle.this).onOpened(UsbManagerImpl.OpenDeviceHandle.-get1(UsbManagerImpl.OpenDeviceHandle.this), paramUsbDeviceConnection);
          }
        });
        return;
      }
      this.callback.onOpened(this.device, paramUsbDeviceConnection);
    }
    
    public void complete()
    {
      closeDirectly();
    }
    
    protected void onClose(int paramInt)
    {
      UsbManagerImpl.-wrap0(UsbManagerImpl.this, this);
    }
  }
  
  private static final class PermissionRequestInfo
  {
    private final UsbManager.PermissionCallback callback;
    private final Handler callbackHandler;
    
    public PermissionRequestInfo(UsbManager.PermissionCallback paramPermissionCallback, Handler paramHandler)
    {
      this.callback = paramPermissionCallback;
      this.callbackHandler = paramHandler;
    }
    
    public void callOnRejected(final UsbDevice paramUsbDevice)
    {
      if (this.callback == null) {
        return;
      }
      if ((this.callbackHandler != null) && (this.callbackHandler.getLooper().getThread() != Thread.currentThread()))
      {
        this.callbackHandler.post(new Runnable()
        {
          public void run()
          {
            UsbManagerImpl.PermissionRequestInfo.-get0(UsbManagerImpl.PermissionRequestInfo.this).onPermissionRejected(paramUsbDevice);
          }
        });
        return;
      }
      this.callback.onPermissionRejected(paramUsbDevice);
    }
    
    public void callOnRequested(final UsbDevice paramUsbDevice)
    {
      if (this.callback == null) {
        return;
      }
      if ((this.callbackHandler != null) && (this.callbackHandler.getLooper().getThread() != Thread.currentThread()))
      {
        this.callbackHandler.post(new Runnable()
        {
          public void run()
          {
            UsbManagerImpl.PermissionRequestInfo.-get0(UsbManagerImpl.PermissionRequestInfo.this).onPermissionRequested(paramUsbDevice);
          }
        });
        return;
      }
      this.callback.onPermissionRequested(paramUsbDevice);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/io/UsbManagerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */