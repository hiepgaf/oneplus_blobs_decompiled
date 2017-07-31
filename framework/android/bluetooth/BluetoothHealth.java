package android.bluetooth;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.Process;
import android.os.RemoteException;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public final class BluetoothHealth
  implements BluetoothProfile
{
  public static final int APP_CONFIG_REGISTRATION_FAILURE = 1;
  public static final int APP_CONFIG_REGISTRATION_SUCCESS = 0;
  public static final int APP_CONFIG_UNREGISTRATION_FAILURE = 3;
  public static final int APP_CONFIG_UNREGISTRATION_SUCCESS = 2;
  public static final int CHANNEL_TYPE_ANY = 12;
  public static final int CHANNEL_TYPE_RELIABLE = 10;
  public static final int CHANNEL_TYPE_STREAMING = 11;
  private static final boolean DBG = true;
  public static final int HEALTH_OPERATION_ERROR = 6001;
  public static final int HEALTH_OPERATION_GENERIC_FAILURE = 6003;
  public static final int HEALTH_OPERATION_INVALID_ARGS = 6002;
  public static final int HEALTH_OPERATION_NOT_ALLOWED = 6005;
  public static final int HEALTH_OPERATION_NOT_FOUND = 6004;
  public static final int HEALTH_OPERATION_SUCCESS = 6000;
  public static final int SINK_ROLE = 2;
  public static final int SOURCE_ROLE = 1;
  public static final int STATE_CHANNEL_CONNECTED = 2;
  public static final int STATE_CHANNEL_CONNECTING = 1;
  public static final int STATE_CHANNEL_DISCONNECTED = 0;
  public static final int STATE_CHANNEL_DISCONNECTING = 3;
  private static final String TAG = "BluetoothHealth";
  private static final boolean VDBG = false;
  BluetoothAdapter mAdapter;
  private final IBluetoothStateChangeCallback mBluetoothStateChangeCallback = new IBluetoothStateChangeCallback.Stub()
  {
    public void onBluetoothStateChange(boolean paramAnonymousBoolean)
    {
      Log.d("BluetoothHealth", "onBluetoothStateChange: up=" + paramAnonymousBoolean);
      if (!paramAnonymousBoolean) {}
      for (;;)
      {
        synchronized (BluetoothHealth.-get0(BluetoothHealth.this))
        {
          try
          {
            BluetoothHealth.-set0(BluetoothHealth.this, null);
            BluetoothHealth.-get1(BluetoothHealth.this).unbindService(BluetoothHealth.-get0(BluetoothHealth.this));
            return;
          }
          catch (Exception localException2)
          {
            Log.e("BluetoothHealth", "", localException2);
            continue;
          }
        }
        ServiceConnection localServiceConnection = BluetoothHealth.-get0(BluetoothHealth.this);
        ??? = localServiceConnection;
        try
        {
          if (BluetoothHealth.-get2(BluetoothHealth.this) != null) {
            continue;
          }
          BluetoothHealth.this.doBind();
          ??? = localServiceConnection;
        }
        catch (Exception localException1)
        {
          Log.e("BluetoothHealth", "", localException1);
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
      Log.d("BluetoothHealth", "Proxy object connected");
      BluetoothHealth.-set0(BluetoothHealth.this, IBluetoothHealth.Stub.asInterface(paramAnonymousIBinder));
      if (BluetoothHealth.-get3(BluetoothHealth.this) != null) {
        BluetoothHealth.-get3(BluetoothHealth.this).onServiceConnected(3, BluetoothHealth.this);
      }
    }
    
    public void onServiceDisconnected(ComponentName paramAnonymousComponentName)
    {
      Log.d("BluetoothHealth", "Proxy object disconnected");
      BluetoothHealth.-set0(BluetoothHealth.this, null);
      if (BluetoothHealth.-get3(BluetoothHealth.this) != null) {
        BluetoothHealth.-get3(BluetoothHealth.this).onServiceDisconnected(3);
      }
    }
  };
  private Context mContext;
  private IBluetoothHealth mService;
  private BluetoothProfile.ServiceListener mServiceListener;
  
  BluetoothHealth(Context paramContext, BluetoothProfile.ServiceListener paramServiceListener)
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
        Log.e("BluetoothHealth", "", paramContext);
      }
    }
  }
  
  private boolean checkAppParam(String paramString, int paramInt1, int paramInt2, BluetoothHealthCallback paramBluetoothHealthCallback)
  {
    if ((paramString == null) || ((paramInt1 != 1) && (paramInt1 != 2))) {}
    while (((paramInt2 != 10) && (paramInt2 != 11) && (paramInt2 != 12)) || (paramBluetoothHealthCallback == null)) {
      return false;
    }
    return (paramInt1 != 1) || (paramInt2 != 12);
  }
  
  private boolean isEnabled()
  {
    BluetoothAdapter localBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    if ((localBluetoothAdapter != null) && (localBluetoothAdapter.getState() == 12)) {
      return true;
    }
    log("Bluetooth is Not enabled");
    return false;
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
    Log.d("BluetoothHealth", paramString);
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
          IBluetoothHealth localIBluetoothHealth = this.mService;
          if (localIBluetoothHealth != null) {}
          try
          {
            this.mService = null;
            this.mContext.unbindService(this.mConnection);
            this.mServiceListener = null;
            return;
            localException1 = localException1;
            Log.e("BluetoothHealth", "", localException1);
          }
          catch (Exception localException2)
          {
            for (;;)
            {
              Log.e("BluetoothHealth", "", localException2);
            }
          }
        }
      }
    }
  }
  
  public boolean connectChannelToSink(BluetoothDevice paramBluetoothDevice, BluetoothHealthAppConfiguration paramBluetoothHealthAppConfiguration, int paramInt)
  {
    if ((this.mService != null) && (isEnabled()) && (isValidDevice(paramBluetoothDevice)) && (paramBluetoothHealthAppConfiguration != null)) {
      try
      {
        boolean bool = this.mService.connectChannelToSink(paramBluetoothDevice, paramBluetoothHealthAppConfiguration, paramInt);
        return bool;
      }
      catch (RemoteException paramBluetoothDevice)
      {
        Log.e("BluetoothHealth", paramBluetoothDevice.toString());
      }
    }
    for (;;)
    {
      return false;
      Log.w("BluetoothHealth", "Proxy not attached to service");
      Log.d("BluetoothHealth", Log.getStackTraceString(new Throwable()));
    }
  }
  
  public boolean connectChannelToSource(BluetoothDevice paramBluetoothDevice, BluetoothHealthAppConfiguration paramBluetoothHealthAppConfiguration)
  {
    if ((this.mService != null) && (isEnabled()) && (isValidDevice(paramBluetoothDevice)) && (paramBluetoothHealthAppConfiguration != null)) {
      try
      {
        boolean bool = this.mService.connectChannelToSource(paramBluetoothDevice, paramBluetoothHealthAppConfiguration);
        return bool;
      }
      catch (RemoteException paramBluetoothDevice)
      {
        Log.e("BluetoothHealth", paramBluetoothDevice.toString());
      }
    }
    for (;;)
    {
      return false;
      Log.w("BluetoothHealth", "Proxy not attached to service");
      Log.d("BluetoothHealth", Log.getStackTraceString(new Throwable()));
    }
  }
  
  public boolean disconnectChannel(BluetoothDevice paramBluetoothDevice, BluetoothHealthAppConfiguration paramBluetoothHealthAppConfiguration, int paramInt)
  {
    if ((this.mService != null) && (isEnabled()) && (isValidDevice(paramBluetoothDevice)) && (paramBluetoothHealthAppConfiguration != null)) {
      try
      {
        boolean bool = this.mService.disconnectChannel(paramBluetoothDevice, paramBluetoothHealthAppConfiguration, paramInt);
        return bool;
      }
      catch (RemoteException paramBluetoothDevice)
      {
        Log.e("BluetoothHealth", paramBluetoothDevice.toString());
      }
    }
    for (;;)
    {
      return false;
      Log.w("BluetoothHealth", "Proxy not attached to service");
      Log.d("BluetoothHealth", Log.getStackTraceString(new Throwable()));
    }
  }
  
  boolean doBind()
  {
    Intent localIntent = new Intent(IBluetoothHealth.class.getName());
    ComponentName localComponentName = localIntent.resolveSystemService(this.mContext.getPackageManager(), 0);
    localIntent.setComponent(localComponentName);
    if ((localComponentName != null) && (this.mContext.bindServiceAsUser(localIntent, this.mConnection, 0, Process.myUserHandle()))) {
      return true;
    }
    Log.e("BluetoothHealth", "Could not bind to Bluetooth Health Service with " + localIntent);
    return false;
  }
  
  public List<BluetoothDevice> getConnectedDevices()
  {
    if ((this.mService != null) && (isEnabled())) {
      try
      {
        List localList = this.mService.getConnectedHealthDevices();
        return localList;
      }
      catch (RemoteException localRemoteException)
      {
        Log.e("BluetoothHealth", "Stack:" + Log.getStackTraceString(new Throwable()));
        return new ArrayList();
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothHealth", "Proxy not attached to service");
    }
    return new ArrayList();
  }
  
  public int getConnectionState(BluetoothDevice paramBluetoothDevice)
  {
    if ((this.mService != null) && (isEnabled()) && (isValidDevice(paramBluetoothDevice))) {
      try
      {
        int i = this.mService.getHealthDeviceConnectionState(paramBluetoothDevice);
        return i;
      }
      catch (RemoteException paramBluetoothDevice)
      {
        Log.e("BluetoothHealth", paramBluetoothDevice.toString());
      }
    }
    for (;;)
    {
      return 0;
      Log.w("BluetoothHealth", "Proxy not attached to service");
      Log.d("BluetoothHealth", Log.getStackTraceString(new Throwable()));
    }
  }
  
  public List<BluetoothDevice> getDevicesMatchingConnectionStates(int[] paramArrayOfInt)
  {
    if ((this.mService != null) && (isEnabled())) {
      try
      {
        paramArrayOfInt = this.mService.getHealthDevicesMatchingConnectionStates(paramArrayOfInt);
        return paramArrayOfInt;
      }
      catch (RemoteException paramArrayOfInt)
      {
        Log.e("BluetoothHealth", "Stack:" + Log.getStackTraceString(new Throwable()));
        return new ArrayList();
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothHealth", "Proxy not attached to service");
    }
    return new ArrayList();
  }
  
  public ParcelFileDescriptor getMainChannelFd(BluetoothDevice paramBluetoothDevice, BluetoothHealthAppConfiguration paramBluetoothHealthAppConfiguration)
  {
    if ((this.mService != null) && (isEnabled()) && (isValidDevice(paramBluetoothDevice)) && (paramBluetoothHealthAppConfiguration != null)) {
      try
      {
        paramBluetoothDevice = this.mService.getMainChannelFd(paramBluetoothDevice, paramBluetoothHealthAppConfiguration);
        return paramBluetoothDevice;
      }
      catch (RemoteException paramBluetoothDevice)
      {
        Log.e("BluetoothHealth", paramBluetoothDevice.toString());
        return null;
      }
    }
    Log.w("BluetoothHealth", "Proxy not attached to service");
    Log.d("BluetoothHealth", Log.getStackTraceString(new Throwable()));
    return null;
  }
  
  public boolean registerAppConfiguration(String paramString, int paramInt1, int paramInt2, int paramInt3, BluetoothHealthCallback paramBluetoothHealthCallback)
  {
    if ((isEnabled()) && (checkAppParam(paramString, paramInt2, paramInt3, paramBluetoothHealthCallback)))
    {
      paramBluetoothHealthCallback = new BluetoothHealthCallbackWrapper(paramBluetoothHealthCallback);
      paramString = new BluetoothHealthAppConfiguration(paramString, paramInt1, paramInt2, paramInt3);
      if (this.mService == null) {}
    }
    else
    {
      try
      {
        boolean bool = this.mService.registerAppConfiguration(paramString, paramBluetoothHealthCallback);
        return bool;
      }
      catch (RemoteException paramString)
      {
        Log.e("BluetoothHealth", paramString.toString());
        return false;
      }
      return false;
    }
    Log.w("BluetoothHealth", "Proxy not attached to service");
    Log.d("BluetoothHealth", Log.getStackTraceString(new Throwable()));
    return false;
  }
  
  public boolean registerSinkAppConfiguration(String paramString, int paramInt, BluetoothHealthCallback paramBluetoothHealthCallback)
  {
    if ((!isEnabled()) || (paramString == null)) {
      return false;
    }
    return registerAppConfiguration(paramString, paramInt, 2, 12, paramBluetoothHealthCallback);
  }
  
  public boolean unregisterAppConfiguration(BluetoothHealthAppConfiguration paramBluetoothHealthAppConfiguration)
  {
    if ((this.mService != null) && (isEnabled()) && (paramBluetoothHealthAppConfiguration != null)) {
      try
      {
        boolean bool = this.mService.unregisterAppConfiguration(paramBluetoothHealthAppConfiguration);
        return bool;
      }
      catch (RemoteException paramBluetoothHealthAppConfiguration)
      {
        Log.e("BluetoothHealth", paramBluetoothHealthAppConfiguration.toString());
        return false;
      }
    }
    Log.w("BluetoothHealth", "Proxy not attached to service");
    Log.d("BluetoothHealth", Log.getStackTraceString(new Throwable()));
    return false;
  }
  
  private static class BluetoothHealthCallbackWrapper
    extends IBluetoothHealthCallback.Stub
  {
    private BluetoothHealthCallback mCallback;
    
    public BluetoothHealthCallbackWrapper(BluetoothHealthCallback paramBluetoothHealthCallback)
    {
      this.mCallback = paramBluetoothHealthCallback;
    }
    
    public void onHealthAppConfigurationStatusChange(BluetoothHealthAppConfiguration paramBluetoothHealthAppConfiguration, int paramInt)
    {
      this.mCallback.onHealthAppConfigurationStatusChange(paramBluetoothHealthAppConfiguration, paramInt);
    }
    
    public void onHealthChannelStateChange(BluetoothHealthAppConfiguration paramBluetoothHealthAppConfiguration, BluetoothDevice paramBluetoothDevice, int paramInt1, int paramInt2, ParcelFileDescriptor paramParcelFileDescriptor, int paramInt3)
    {
      this.mCallback.onHealthChannelStateChange(paramBluetoothHealthAppConfiguration, paramBluetoothDevice, paramInt1, paramInt2, paramParcelFileDescriptor, paramInt3);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/bluetooth/BluetoothHealth.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */