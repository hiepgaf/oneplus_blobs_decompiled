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

public final class BluetoothA2dpSink
  implements BluetoothProfile
{
  public static final String ACTION_AUDIO_CONFIG_CHANGED = "android.bluetooth.a2dp-sink.profile.action.AUDIO_CONFIG_CHANGED";
  public static final String ACTION_CONNECTION_STATE_CHANGED = "android.bluetooth.a2dp-sink.profile.action.CONNECTION_STATE_CHANGED";
  public static final String ACTION_PLAYING_STATE_CHANGED = "android.bluetooth.a2dp-sink.profile.action.PLAYING_STATE_CHANGED";
  private static final boolean DBG = true;
  public static final String EXTRA_AUDIO_CONFIG = "android.bluetooth.a2dp-sink.profile.extra.AUDIO_CONFIG";
  public static final int STATE_NOT_PLAYING = 11;
  public static final int STATE_PLAYING = 10;
  private static final String TAG = "BluetoothA2dpSink";
  private static final boolean VDBG = false;
  private BluetoothAdapter mAdapter;
  private final IBluetoothStateChangeCallback mBluetoothStateChangeCallback = new IBluetoothStateChangeCallback.Stub()
  {
    public void onBluetoothStateChange(boolean paramAnonymousBoolean)
    {
      Log.d("BluetoothA2dpSink", "onBluetoothStateChange: up=" + paramAnonymousBoolean);
      if (!paramAnonymousBoolean) {}
      for (;;)
      {
        synchronized (BluetoothA2dpSink.-get0(BluetoothA2dpSink.this))
        {
          try
          {
            BluetoothA2dpSink.-set0(BluetoothA2dpSink.this, null);
            BluetoothA2dpSink.-get1(BluetoothA2dpSink.this).unbindService(BluetoothA2dpSink.-get0(BluetoothA2dpSink.this));
            return;
          }
          catch (Exception localException2)
          {
            Log.e("BluetoothA2dpSink", "", localException2);
            continue;
          }
        }
        ServiceConnection localServiceConnection = BluetoothA2dpSink.-get0(BluetoothA2dpSink.this);
        ??? = localServiceConnection;
        try
        {
          if (BluetoothA2dpSink.-get2(BluetoothA2dpSink.this) != null) {
            continue;
          }
          BluetoothA2dpSink.this.doBind();
          ??? = localServiceConnection;
        }
        catch (Exception localException1)
        {
          Log.e("BluetoothA2dpSink", "", localException1);
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
      Log.d("BluetoothA2dpSink", "Proxy object connected");
      BluetoothA2dpSink.-set0(BluetoothA2dpSink.this, IBluetoothA2dpSink.Stub.asInterface(paramAnonymousIBinder));
      if (BluetoothA2dpSink.-get3(BluetoothA2dpSink.this) != null) {
        BluetoothA2dpSink.-get3(BluetoothA2dpSink.this).onServiceConnected(11, BluetoothA2dpSink.this);
      }
    }
    
    public void onServiceDisconnected(ComponentName paramAnonymousComponentName)
    {
      Log.d("BluetoothA2dpSink", "Proxy object disconnected");
      BluetoothA2dpSink.-set0(BluetoothA2dpSink.this, null);
      if (BluetoothA2dpSink.-get3(BluetoothA2dpSink.this) != null) {
        BluetoothA2dpSink.-get3(BluetoothA2dpSink.this).onServiceDisconnected(11);
      }
    }
  };
  private Context mContext;
  private IBluetoothA2dpSink mService;
  private BluetoothProfile.ServiceListener mServiceListener;
  
  BluetoothA2dpSink(Context paramContext, BluetoothProfile.ServiceListener paramServiceListener)
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
        Log.e("BluetoothA2dpSink", "", paramContext);
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
    Log.d("BluetoothA2dpSink", paramString);
  }
  
  public static String stateToString(int paramInt)
  {
    switch (paramInt)
    {
    case 4: 
    case 5: 
    case 6: 
    case 7: 
    case 8: 
    case 9: 
    default: 
      return "<unknown state " + paramInt + ">";
    case 0: 
      return "disconnected";
    case 1: 
      return "connecting";
    case 2: 
      return "connected";
    case 3: 
      return "disconnecting";
    case 10: 
      return "playing";
    }
    return "not playing";
  }
  
  void close()
  {
    this.mServiceListener = null;
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
          IBluetoothA2dpSink localIBluetoothA2dpSink = this.mService;
          if (localIBluetoothA2dpSink != null) {}
          try
          {
            this.mService = null;
            this.mContext.unbindService(this.mConnection);
            return;
            localException1 = localException1;
            Log.e("BluetoothA2dpSink", "", localException1);
          }
          catch (Exception localException2)
          {
            for (;;)
            {
              Log.e("BluetoothA2dpSink", "", localException2);
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
        Log.e("BluetoothA2dpSink", "Stack:" + Log.getStackTraceString(new Throwable()));
        return false;
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothA2dpSink", "Proxy not attached to service");
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
        Log.e("BluetoothA2dpSink", "Stack:" + Log.getStackTraceString(new Throwable()));
        return false;
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothA2dpSink", "Proxy not attached to service");
    }
    return false;
  }
  
  boolean doBind()
  {
    Intent localIntent = new Intent(IBluetoothA2dpSink.class.getName());
    ComponentName localComponentName = localIntent.resolveSystemService(this.mContext.getPackageManager(), 0);
    localIntent.setComponent(localComponentName);
    if ((localComponentName != null) && (this.mContext.bindServiceAsUser(localIntent, this.mConnection, 0, Process.myUserHandle()))) {
      return true;
    }
    Log.e("BluetoothA2dpSink", "Could not bind to Bluetooth A2DP Service with " + localIntent);
    return false;
  }
  
  public void finalize()
  {
    close();
  }
  
  public BluetoothAudioConfig getAudioConfig(BluetoothDevice paramBluetoothDevice)
  {
    if ((this.mService != null) && (isEnabled()) && (isValidDevice(paramBluetoothDevice))) {
      try
      {
        paramBluetoothDevice = this.mService.getAudioConfig(paramBluetoothDevice);
        return paramBluetoothDevice;
      }
      catch (RemoteException paramBluetoothDevice)
      {
        Log.e("BluetoothA2dpSink", "Stack:" + Log.getStackTraceString(new Throwable()));
        return null;
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothA2dpSink", "Proxy not attached to service");
    }
    return null;
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
        Log.e("BluetoothA2dpSink", "Stack:" + Log.getStackTraceString(new Throwable()));
        return new ArrayList();
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothA2dpSink", "Proxy not attached to service");
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
        Log.e("BluetoothA2dpSink", "Stack:" + Log.getStackTraceString(new Throwable()));
        return 0;
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothA2dpSink", "Proxy not attached to service");
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
        Log.e("BluetoothA2dpSink", "Stack:" + Log.getStackTraceString(new Throwable()));
        return new ArrayList();
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothA2dpSink", "Proxy not attached to service");
    }
    return new ArrayList();
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
        Log.e("BluetoothA2dpSink", "Stack:" + Log.getStackTraceString(new Throwable()));
        return 0;
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothA2dpSink", "Proxy not attached to service");
    }
    return 0;
  }
  
  public boolean isA2dpPlaying(BluetoothDevice paramBluetoothDevice)
  {
    if ((this.mService != null) && (isEnabled()) && (isValidDevice(paramBluetoothDevice))) {
      try
      {
        boolean bool = this.mService.isA2dpPlaying(paramBluetoothDevice);
        return bool;
      }
      catch (RemoteException paramBluetoothDevice)
      {
        Log.e("BluetoothA2dpSink", "Stack:" + Log.getStackTraceString(new Throwable()));
        return false;
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothA2dpSink", "Proxy not attached to service");
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
        Log.e("BluetoothA2dpSink", "Stack:" + Log.getStackTraceString(new Throwable()));
        return false;
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothA2dpSink", "Proxy not attached to service");
    }
    return false;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/bluetooth/BluetoothA2dpSink.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */