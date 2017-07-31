package android.bluetooth;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public final class BluetoothInputDevice
  implements BluetoothProfile
{
  public static final String ACTION_CONNECTION_STATE_CHANGED = "android.bluetooth.input.profile.action.CONNECTION_STATE_CHANGED";
  public static final String ACTION_HANDSHAKE = "android.bluetooth.input.profile.action.HANDSHAKE";
  public static final String ACTION_IDLE_TIME_CHANGED = "codeaurora.bluetooth.input.profile.action.IDLE_TIME_CHANGED";
  public static final String ACTION_PROTOCOL_MODE_CHANGED = "android.bluetooth.input.profile.action.PROTOCOL_MODE_CHANGED";
  public static final String ACTION_REPORT = "android.bluetooth.input.profile.action.REPORT";
  public static final String ACTION_VIRTUAL_UNPLUG_STATUS = "android.bluetooth.input.profile.action.VIRTUAL_UNPLUG_STATUS";
  private static final boolean DBG = true;
  public static final String EXTRA_IDLE_TIME = "codeaurora.bluetooth.BluetoothInputDevice.extra.IDLE_TIME";
  public static final String EXTRA_PROTOCOL_MODE = "android.bluetooth.BluetoothInputDevice.extra.PROTOCOL_MODE";
  public static final String EXTRA_REPORT = "android.bluetooth.BluetoothInputDevice.extra.REPORT";
  public static final String EXTRA_REPORT_BUFFER_SIZE = "android.bluetooth.BluetoothInputDevice.extra.REPORT_BUFFER_SIZE";
  public static final String EXTRA_REPORT_ID = "android.bluetooth.BluetoothInputDevice.extra.REPORT_ID";
  public static final String EXTRA_REPORT_TYPE = "android.bluetooth.BluetoothInputDevice.extra.REPORT_TYPE";
  public static final String EXTRA_STATUS = "android.bluetooth.BluetoothInputDevice.extra.STATUS";
  public static final String EXTRA_VIRTUAL_UNPLUG_STATUS = "android.bluetooth.BluetoothInputDevice.extra.VIRTUAL_UNPLUG_STATUS";
  public static final int INPUT_CONNECT_FAILED_ALREADY_CONNECTED = 5001;
  public static final int INPUT_CONNECT_FAILED_ATTEMPT_FAILED = 5002;
  public static final int INPUT_DISCONNECT_FAILED_NOT_CONNECTED = 5000;
  public static final int INPUT_OPERATION_GENERIC_FAILURE = 5003;
  public static final int INPUT_OPERATION_SUCCESS = 5004;
  public static final int PROTOCOL_BOOT_MODE = 1;
  public static final int PROTOCOL_REPORT_MODE = 0;
  public static final int PROTOCOL_UNSUPPORTED_MODE = 255;
  public static final byte REPORT_TYPE_FEATURE = 3;
  public static final byte REPORT_TYPE_INPUT = 1;
  public static final byte REPORT_TYPE_OUTPUT = 2;
  private static final String TAG = "BluetoothInputDevice";
  private static final boolean VDBG = false;
  public static final int VIRTUAL_UNPLUG_STATUS_FAIL = 1;
  public static final int VIRTUAL_UNPLUG_STATUS_SUCCESS = 0;
  private BluetoothAdapter mAdapter;
  private final IBluetoothStateChangeCallback mBluetoothStateChangeCallback = new IBluetoothStateChangeCallback.Stub()
  {
    public void onBluetoothStateChange(boolean paramAnonymousBoolean)
    {
      Log.d("BluetoothInputDevice", "onBluetoothStateChange: up=" + paramAnonymousBoolean);
      if (!paramAnonymousBoolean) {}
      for (;;)
      {
        synchronized (BluetoothInputDevice.-get0(BluetoothInputDevice.this))
        {
          try
          {
            BluetoothInputDevice.-set0(BluetoothInputDevice.this, null);
            BluetoothInputDevice.-get1(BluetoothInputDevice.this).unbindService(BluetoothInputDevice.-get0(BluetoothInputDevice.this));
            return;
          }
          catch (Exception localException2)
          {
            Log.e("BluetoothInputDevice", "", localException2);
            continue;
          }
        }
        ServiceConnection localServiceConnection = BluetoothInputDevice.-get0(BluetoothInputDevice.this);
        ??? = localServiceConnection;
        try
        {
          if (BluetoothInputDevice.-get2(BluetoothInputDevice.this) != null) {
            continue;
          }
          BluetoothInputDevice.this.doBind();
          ??? = localServiceConnection;
        }
        catch (Exception localException1)
        {
          Log.e("BluetoothInputDevice", "", localException1);
          Object localObject2 = localServiceConnection;
        }
        finally {}
      }
    }
  };
  private final ServiceConnection mConnection = new ServiceConnection()
  {
    public void onServiceConnected(ComponentName paramAnonymousComponentName, IBinder paramAnonymousIBinder)
    {
      Log.d("BluetoothInputDevice", "Proxy object connected");
      BluetoothInputDevice.-set0(BluetoothInputDevice.this, IBluetoothInputDevice.Stub.asInterface(paramAnonymousIBinder));
      if (BluetoothInputDevice.-get3(BluetoothInputDevice.this) != null) {
        BluetoothInputDevice.-get3(BluetoothInputDevice.this).onServiceConnected(4, BluetoothInputDevice.this);
      }
    }
    
    public void onServiceDisconnected(ComponentName paramAnonymousComponentName)
    {
      Log.d("BluetoothInputDevice", "Proxy object disconnected");
      BluetoothInputDevice.-set0(BluetoothInputDevice.this, null);
      if (BluetoothInputDevice.-get3(BluetoothInputDevice.this) != null) {
        BluetoothInputDevice.-get3(BluetoothInputDevice.this).onServiceDisconnected(4);
      }
    }
  };
  private Context mContext;
  private IBluetoothInputDevice mService;
  private BluetoothProfile.ServiceListener mServiceListener;
  
  BluetoothInputDevice(Context paramContext, BluetoothProfile.ServiceListener paramServiceListener)
  {
    this.mContext = paramContext;
    this.mServiceListener = paramServiceListener;
    this.mAdapter = BluetoothAdapter.getDefaultAdapter();
    paramContext = this.mAdapter.getBluetoothManager();
    if (paramContext != null) {}
    try
    {
      paramContext.registerStateChangeCallback(this.mBluetoothStateChangeCallback);
      doBind();
      return;
    }
    catch (RemoteException paramContext)
    {
      for (;;)
      {
        Log.e("BluetoothInputDevice", "", paramContext);
      }
    }
  }
  
  private boolean isEnabled()
  {
    return this.mAdapter.getState() == 12;
  }
  
  private boolean isValidDevice(BluetoothDevice paramBluetoothDevice)
  {
    if (paramBluetoothDevice == null) {
      return false;
    }
    return BluetoothAdapter.checkBluetoothAddress(paramBluetoothDevice.getAddress());
  }
  
  private static void log(String paramString)
  {
    Log.d("BluetoothInputDevice", paramString);
  }
  
  void close()
  {
    ??? = this.mAdapter.getBluetoothManager();
    if (??? != null) {}
    try
    {
      ((IBluetoothManager)???).unregisterStateChangeCallback(this.mBluetoothStateChangeCallback);
    }
    catch (Exception localException1)
    {
      synchronized (this.mConnection)
      {
        for (;;)
        {
          IBluetoothInputDevice localIBluetoothInputDevice = this.mService;
          if (localIBluetoothInputDevice != null) {}
          try
          {
            this.mService = null;
            this.mContext.unbindService(this.mConnection);
            this.mServiceListener = null;
            return;
            localException1 = localException1;
            Log.e("BluetoothInputDevice", "", localException1);
          }
          catch (Exception localException2)
          {
            for (;;)
            {
              Log.e("BluetoothInputDevice", "", localException2);
            }
          }
        }
      }
    }
  }
  
  public boolean connect(BluetoothDevice paramBluetoothDevice)
  {
    log("connect(" + paramBluetoothDevice + ")");
    if ((this.mService != null) && (isEnabled()) && (isValidDevice(paramBluetoothDevice))) {
      try
      {
        boolean bool = this.mService.connect(paramBluetoothDevice);
        return bool;
      }
      catch (RemoteException paramBluetoothDevice)
      {
        Log.e("BluetoothInputDevice", "Stack:" + Log.getStackTraceString(new Throwable()));
        return false;
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothInputDevice", "Proxy not attached to service");
    }
    return false;
  }
  
  public boolean disconnect(BluetoothDevice paramBluetoothDevice)
  {
    log("disconnect(" + paramBluetoothDevice + ")");
    if ((this.mService != null) && (isEnabled()) && (isValidDevice(paramBluetoothDevice))) {
      try
      {
        boolean bool = this.mService.disconnect(paramBluetoothDevice);
        return bool;
      }
      catch (RemoteException paramBluetoothDevice)
      {
        Log.e("BluetoothInputDevice", "Stack:" + Log.getStackTraceString(new Throwable()));
        return false;
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothInputDevice", "Proxy not attached to service");
    }
    return false;
  }
  
  boolean doBind()
  {
    Intent localIntent = new Intent(IBluetoothInputDevice.class.getName());
    ComponentName localComponentName = localIntent.resolveSystemService(this.mContext.getPackageManager(), 0);
    localIntent.setComponent(localComponentName);
    if ((localComponentName != null) && (this.mContext.bindServiceAsUser(localIntent, this.mConnection, 0, Process.myUserHandle()))) {
      return true;
    }
    Log.e("BluetoothInputDevice", "Could not bind to Bluetooth HID Service with " + localIntent);
    return false;
  }
  
  public List<BluetoothDevice> getConnectedDevices()
  {
    if ((this.mService != null) && (isEnabled())) {
      try
      {
        List localList = this.mService.getConnectedDevices();
        return localList;
      }
      catch (RemoteException localRemoteException)
      {
        Log.e("BluetoothInputDevice", "Stack:" + Log.getStackTraceString(new Throwable()));
        return new ArrayList();
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothInputDevice", "Proxy not attached to service");
    }
    return new ArrayList();
  }
  
  public int getConnectionState(BluetoothDevice paramBluetoothDevice)
  {
    if ((this.mService != null) && (isEnabled()) && (isValidDevice(paramBluetoothDevice))) {
      try
      {
        int i = this.mService.getConnectionState(paramBluetoothDevice);
        return i;
      }
      catch (RemoteException paramBluetoothDevice)
      {
        Log.e("BluetoothInputDevice", "Stack:" + Log.getStackTraceString(new Throwable()));
        return 0;
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothInputDevice", "Proxy not attached to service");
    }
    return 0;
  }
  
  public List<BluetoothDevice> getDevicesMatchingConnectionStates(int[] paramArrayOfInt)
  {
    if ((this.mService != null) && (isEnabled())) {
      try
      {
        paramArrayOfInt = this.mService.getDevicesMatchingConnectionStates(paramArrayOfInt);
        return paramArrayOfInt;
      }
      catch (RemoteException paramArrayOfInt)
      {
        Log.e("BluetoothInputDevice", "Stack:" + Log.getStackTraceString(new Throwable()));
        return new ArrayList();
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothInputDevice", "Proxy not attached to service");
    }
    return new ArrayList();
  }
  
  public boolean getIdleTime(BluetoothDevice paramBluetoothDevice)
  {
    log("getIdletime(" + paramBluetoothDevice + ")");
    if ((this.mService != null) && (isEnabled()) && (isValidDevice(paramBluetoothDevice))) {
      try
      {
        boolean bool = this.mService.getIdleTime(paramBluetoothDevice);
        return bool;
      }
      catch (RemoteException paramBluetoothDevice)
      {
        Log.e("BluetoothInputDevice", "Stack:" + Log.getStackTraceString(new Throwable()));
        return false;
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothInputDevice", "Proxy not attached to service");
    }
    return false;
  }
  
  public int getPriority(BluetoothDevice paramBluetoothDevice)
  {
    if ((this.mService != null) && (isEnabled()) && (isValidDevice(paramBluetoothDevice))) {
      try
      {
        int i = this.mService.getPriority(paramBluetoothDevice);
        return i;
      }
      catch (RemoteException paramBluetoothDevice)
      {
        Log.e("BluetoothInputDevice", "Stack:" + Log.getStackTraceString(new Throwable()));
        return 0;
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothInputDevice", "Proxy not attached to service");
    }
    return 0;
  }
  
  public boolean getProtocolMode(BluetoothDevice paramBluetoothDevice)
  {
    if ((this.mService != null) && (isEnabled()) && (isValidDevice(paramBluetoothDevice))) {
      try
      {
        boolean bool = this.mService.getProtocolMode(paramBluetoothDevice);
        return bool;
      }
      catch (RemoteException paramBluetoothDevice)
      {
        Log.e("BluetoothInputDevice", "Stack:" + Log.getStackTraceString(new Throwable()));
        return false;
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothInputDevice", "Proxy not attached to service");
    }
    return false;
  }
  
  public boolean getReport(BluetoothDevice paramBluetoothDevice, byte paramByte1, byte paramByte2, int paramInt)
  {
    if ((this.mService != null) && (isEnabled()) && (isValidDevice(paramBluetoothDevice))) {
      try
      {
        boolean bool = this.mService.getReport(paramBluetoothDevice, paramByte1, paramByte2, paramInt);
        return bool;
      }
      catch (RemoteException paramBluetoothDevice)
      {
        Log.e("BluetoothInputDevice", "Stack:" + Log.getStackTraceString(new Throwable()));
        return false;
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothInputDevice", "Proxy not attached to service");
    }
    return false;
  }
  
  public boolean sendData(BluetoothDevice paramBluetoothDevice, String paramString)
  {
    log("sendData(" + paramBluetoothDevice + "), report=" + paramString);
    if ((this.mService != null) && (isEnabled()) && (isValidDevice(paramBluetoothDevice))) {
      try
      {
        boolean bool = this.mService.sendData(paramBluetoothDevice, paramString);
        return bool;
      }
      catch (RemoteException paramBluetoothDevice)
      {
        Log.e("BluetoothInputDevice", "Stack:" + Log.getStackTraceString(new Throwable()));
        return false;
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothInputDevice", "Proxy not attached to service");
    }
    return false;
  }
  
  public boolean setIdleTime(BluetoothDevice paramBluetoothDevice, byte paramByte)
  {
    log("setIdletime(" + paramBluetoothDevice + "), idleTime=" + paramByte);
    if ((this.mService != null) && (isEnabled()) && (isValidDevice(paramBluetoothDevice))) {
      try
      {
        boolean bool = this.mService.setIdleTime(paramBluetoothDevice, paramByte);
        return bool;
      }
      catch (RemoteException paramBluetoothDevice)
      {
        Log.e("BluetoothInputDevice", "Stack:" + Log.getStackTraceString(new Throwable()));
        return false;
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothInputDevice", "Proxy not attached to service");
    }
    return false;
  }
  
  public boolean setPriority(BluetoothDevice paramBluetoothDevice, int paramInt)
  {
    log("setPriority(" + paramBluetoothDevice + ", " + paramInt + ")");
    if ((this.mService != null) && (isEnabled()) && (isValidDevice(paramBluetoothDevice)))
    {
      if ((paramInt != 0) && (paramInt != 100)) {
        return false;
      }
      try
      {
        boolean bool = this.mService.setPriority(paramBluetoothDevice, paramInt);
        return bool;
      }
      catch (RemoteException paramBluetoothDevice)
      {
        Log.e("BluetoothInputDevice", "Stack:" + Log.getStackTraceString(new Throwable()));
        return false;
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothInputDevice", "Proxy not attached to service");
    }
    return false;
  }
  
  public boolean setProtocolMode(BluetoothDevice paramBluetoothDevice, int paramInt)
  {
    log("setProtocolMode(" + paramBluetoothDevice + ")");
    if ((this.mService != null) && (isEnabled()) && (isValidDevice(paramBluetoothDevice))) {
      try
      {
        boolean bool = this.mService.setProtocolMode(paramBluetoothDevice, paramInt);
        return bool;
      }
      catch (RemoteException paramBluetoothDevice)
      {
        Log.e("BluetoothInputDevice", "Stack:" + Log.getStackTraceString(new Throwable()));
        return false;
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothInputDevice", "Proxy not attached to service");
    }
    return false;
  }
  
  public boolean setReport(BluetoothDevice paramBluetoothDevice, byte paramByte, String paramString)
  {
    if ((this.mService != null) && (isEnabled()) && (isValidDevice(paramBluetoothDevice))) {
      try
      {
        boolean bool = this.mService.setReport(paramBluetoothDevice, paramByte, paramString);
        return bool;
      }
      catch (RemoteException paramBluetoothDevice)
      {
        Log.e("BluetoothInputDevice", "Stack:" + Log.getStackTraceString(new Throwable()));
        return false;
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothInputDevice", "Proxy not attached to service");
    }
    return false;
  }
  
  public boolean virtualUnplug(BluetoothDevice paramBluetoothDevice)
  {
    log("virtualUnplug(" + paramBluetoothDevice + ")");
    if ((this.mService != null) && (isEnabled()) && (isValidDevice(paramBluetoothDevice))) {
      try
      {
        boolean bool = this.mService.virtualUnplug(paramBluetoothDevice);
        return bool;
      }
      catch (RemoteException paramBluetoothDevice)
      {
        Log.e("BluetoothInputDevice", "Stack:" + Log.getStackTraceString(new Throwable()));
        return false;
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothInputDevice", "Proxy not attached to service");
    }
    return false;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/bluetooth/BluetoothInputDevice.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */