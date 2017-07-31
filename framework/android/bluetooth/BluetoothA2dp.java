package android.bluetooth;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.util.Log;
import com.android.internal.annotations.GuardedBy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

public final class BluetoothA2dp
  implements BluetoothProfile
{
  public static final String ACTION_AVRCP_CONNECTION_STATE_CHANGED = "android.bluetooth.a2dp.profile.action.AVRCP_CONNECTION_STATE_CHANGED";
  public static final String ACTION_CODEC_SELECTION_CHANGED = "android.bluetooth.a2dp.profile.action.CODEC_SELECTION_CHANGED";
  public static final String ACTION_CONNECTION_STATE_CHANGED = "android.bluetooth.a2dp.profile.action.CONNECTION_STATE_CHANGED";
  public static final String ACTION_PLAYING_STATE_CHANGED = "android.bluetooth.a2dp.profile.action.PLAYING_STATE_CHANGED";
  public static final int APTX_CODEC_SELECTED = 13;
  public static final int APTX_HD_CODEC_SELECTED = 14;
  private static final boolean DBG = true;
  public static final int SBC_CODEC_SELECTED = 12;
  public static final int STATE_NOT_PLAYING = 11;
  public static final int STATE_PLAYING = 10;
  private static final String TAG = "BluetoothA2dp";
  private static final boolean VDBG = false;
  private BluetoothAdapter mAdapter;
  private final IBluetoothStateChangeCallback mBluetoothStateChangeCallback = new IBluetoothStateChangeCallback.Stub()
  {
    public void onBluetoothStateChange(boolean paramAnonymousBoolean)
    {
      Log.d("BluetoothA2dp", "onBluetoothStateChange: up=" + paramAnonymousBoolean);
      if (!paramAnonymousBoolean) {
        try
        {
          BluetoothA2dp.-get4(BluetoothA2dp.this).writeLock().lock();
          BluetoothA2dp.-set0(BluetoothA2dp.this, null);
          BluetoothA2dp.-get1(BluetoothA2dp.this).unbindService(BluetoothA2dp.-get0(BluetoothA2dp.this));
          return;
        }
        catch (Exception localException1)
        {
          Log.e("BluetoothA2dp", "", localException1);
          return;
        }
        finally
        {
          BluetoothA2dp.-get4(BluetoothA2dp.this).writeLock().unlock();
        }
      }
      try
      {
        BluetoothA2dp.-get4(BluetoothA2dp.this).readLock().lock();
        if (BluetoothA2dp.-get2(BluetoothA2dp.this) == null) {
          BluetoothA2dp.this.doBind();
        }
        return;
      }
      catch (Exception localException2)
      {
        Log.e("BluetoothA2dp", "", localException2);
        return;
      }
      finally
      {
        BluetoothA2dp.-get4(BluetoothA2dp.this).readLock().unlock();
      }
    }
  };
  private final ServiceConnection mConnection = new ServiceConnection()
  {
    public void onServiceConnected(ComponentName paramAnonymousComponentName, IBinder paramAnonymousIBinder)
    {
      Log.d("BluetoothA2dp", "Proxy object connected");
      try
      {
        BluetoothA2dp.-get4(BluetoothA2dp.this).writeLock().lock();
        BluetoothA2dp.-set0(BluetoothA2dp.this, IBluetoothA2dp.Stub.asInterface(paramAnonymousIBinder));
        BluetoothA2dp.-get4(BluetoothA2dp.this).writeLock().unlock();
        if (BluetoothA2dp.-get3(BluetoothA2dp.this) != null) {
          BluetoothA2dp.-get3(BluetoothA2dp.this).onServiceConnected(2, BluetoothA2dp.this);
        }
        return;
      }
      finally
      {
        BluetoothA2dp.-get4(BluetoothA2dp.this).writeLock().unlock();
      }
    }
    
    public void onServiceDisconnected(ComponentName paramAnonymousComponentName)
    {
      Log.d("BluetoothA2dp", "Proxy object disconnected");
      try
      {
        BluetoothA2dp.-get4(BluetoothA2dp.this).writeLock().lock();
        BluetoothA2dp.-set0(BluetoothA2dp.this, null);
        BluetoothA2dp.-get4(BluetoothA2dp.this).writeLock().unlock();
        if (BluetoothA2dp.-get3(BluetoothA2dp.this) != null) {
          BluetoothA2dp.-get3(BluetoothA2dp.this).onServiceDisconnected(2);
        }
        return;
      }
      finally
      {
        BluetoothA2dp.-get4(BluetoothA2dp.this).writeLock().unlock();
      }
    }
  };
  private Context mContext;
  @GuardedBy("mServiceLock")
  private IBluetoothA2dp mService;
  private BluetoothProfile.ServiceListener mServiceListener;
  private final ReentrantReadWriteLock mServiceLock = new ReentrantReadWriteLock();
  
  BluetoothA2dp(Context paramContext, BluetoothProfile.ServiceListener paramServiceListener)
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
        Log.e("BluetoothA2dp", "", paramContext);
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
    Log.d("BluetoothA2dp", paramString);
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
    case 11: 
      return "not playing";
    case 12: 
      return "SBC";
    case 13: 
      return "aptX";
    }
    return "aptX HD";
  }
  
  public void adjustAvrcpAbsoluteVolume(int paramInt)
  {
    Log.d("BluetoothA2dp", "adjustAvrcpAbsoluteVolume");
    try
    {
      this.mServiceLock.readLock().lock();
      if ((this.mService != null) && (isEnabled())) {
        this.mService.adjustAvrcpAbsoluteVolume(paramInt);
      }
      if (this.mService == null) {
        Log.w("BluetoothA2dp", "Proxy not attached to service");
      }
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("BluetoothA2dp", "Error talking to BT service in adjustAvrcpAbsoluteVolume()", localRemoteException);
      return;
    }
    finally
    {
      this.mServiceLock.readLock().unlock();
    }
  }
  
  void close()
  {
    this.mServiceListener = null;
    IBluetoothManager localIBluetoothManager = this.mAdapter.getBluetoothManager();
    if (localIBluetoothManager != null) {}
    try
    {
      localIBluetoothManager.unregisterStateChangeCallback(this.mBluetoothStateChangeCallback);
    }
    catch (Exception localException1)
    {
      for (;;)
      {
        try
        {
          this.mServiceLock.writeLock().lock();
          if (this.mService != null)
          {
            this.mService = null;
            this.mContext.unbindService(this.mConnection);
          }
          return;
        }
        catch (Exception localException2)
        {
          Log.e("BluetoothA2dp", "", localException2);
          return;
        }
        finally
        {
          this.mServiceLock.writeLock().unlock();
        }
        localException1 = localException1;
        Log.e("BluetoothA2dp", "", localException1);
      }
    }
  }
  
  public boolean connect(BluetoothDevice paramBluetoothDevice)
  {
    log("connect(" + paramBluetoothDevice + ")");
    try
    {
      this.mServiceLock.readLock().lock();
      if ((this.mService != null) && (isEnabled()) && (isValidDevice(paramBluetoothDevice)))
      {
        boolean bool = this.mService.connect(paramBluetoothDevice);
        return bool;
      }
      if (this.mService == null) {
        Log.w("BluetoothA2dp", "Proxy not attached to service");
      }
      return false;
    }
    catch (RemoteException paramBluetoothDevice)
    {
      Log.e("BluetoothA2dp", "Stack:" + Log.getStackTraceString(new Throwable()));
      return false;
    }
    finally
    {
      this.mServiceLock.readLock().unlock();
    }
  }
  
  public void disableAptXHD()
  {
    log("disableAptXHD");
    if ((this.mService != null) && (isEnabled())) {
      try
      {
        this.mService.disableAptXHD();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Log.e("BluetoothA2dp", "Stack:" + Log.getStackTraceString(new Throwable()));
        return;
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothA2dp", "Proxy not attached to service");
    }
  }
  
  public boolean disconnect(BluetoothDevice paramBluetoothDevice)
  {
    log("disconnect(" + paramBluetoothDevice + ")");
    try
    {
      this.mServiceLock.readLock().lock();
      if ((this.mService != null) && (isEnabled()) && (isValidDevice(paramBluetoothDevice)))
      {
        boolean bool = this.mService.disconnect(paramBluetoothDevice);
        return bool;
      }
      if (this.mService == null) {
        Log.w("BluetoothA2dp", "Proxy not attached to service");
      }
      return false;
    }
    catch (RemoteException paramBluetoothDevice)
    {
      Log.e("BluetoothA2dp", "Stack:" + Log.getStackTraceString(new Throwable()));
      return false;
    }
    finally
    {
      this.mServiceLock.readLock().unlock();
    }
  }
  
  boolean doBind()
  {
    Intent localIntent = new Intent(IBluetoothA2dp.class.getName());
    ComponentName localComponentName = localIntent.resolveSystemService(this.mContext.getPackageManager(), 0);
    localIntent.setComponent(localComponentName);
    if ((localComponentName != null) && (this.mContext.bindServiceAsUser(localIntent, this.mConnection, 0, Process.myUserHandle()))) {
      return true;
    }
    Log.e("BluetoothA2dp", "Could not bind to Bluetooth A2DP Service with " + localIntent);
    return false;
  }
  
  public void enableAptXHD()
  {
    log("enableAptXHD");
    if ((this.mService != null) && (isEnabled())) {
      try
      {
        this.mService.enableAptXHD();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Log.e("BluetoothA2dp", "Stack:" + Log.getStackTraceString(new Throwable()));
        return;
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothA2dp", "Proxy not attached to service");
    }
  }
  
  public void finalize() {}
  
  public List<BluetoothDevice> getConnectedDevices()
  {
    try
    {
      this.mServiceLock.readLock().lock();
      if ((this.mService != null) && (isEnabled()))
      {
        localObject1 = this.mService.getConnectedDevices();
        return (List<BluetoothDevice>)localObject1;
      }
      if (this.mService == null) {
        Log.w("BluetoothA2dp", "Proxy not attached to service");
      }
      Object localObject1 = new ArrayList();
      return (List<BluetoothDevice>)localObject1;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("BluetoothA2dp", "Stack:" + Log.getStackTraceString(new Throwable()));
      ArrayList localArrayList = new ArrayList();
      return localArrayList;
    }
    finally
    {
      this.mServiceLock.readLock().unlock();
    }
  }
  
  public int getConnectionState(BluetoothDevice paramBluetoothDevice)
  {
    try
    {
      this.mServiceLock.readLock().lock();
      if ((this.mService != null) && (isEnabled()) && (isValidDevice(paramBluetoothDevice)))
      {
        int i = this.mService.getConnectionState(paramBluetoothDevice);
        return i;
      }
      if (this.mService == null) {
        Log.w("BluetoothA2dp", "Proxy not attached to service");
      }
      return 0;
    }
    catch (RemoteException paramBluetoothDevice)
    {
      Log.e("BluetoothA2dp", "Stack:" + Log.getStackTraceString(new Throwable()));
      return 0;
    }
    finally
    {
      this.mServiceLock.readLock().unlock();
    }
  }
  
  public List<BluetoothDevice> getDevicesMatchingConnectionStates(int[] paramArrayOfInt)
  {
    try
    {
      this.mServiceLock.readLock().lock();
      if ((this.mService != null) && (isEnabled()))
      {
        paramArrayOfInt = this.mService.getDevicesMatchingConnectionStates(paramArrayOfInt);
        return paramArrayOfInt;
      }
      if (this.mService == null) {
        Log.w("BluetoothA2dp", "Proxy not attached to service");
      }
      paramArrayOfInt = new ArrayList();
      return paramArrayOfInt;
    }
    catch (RemoteException paramArrayOfInt)
    {
      Log.e("BluetoothA2dp", "Stack:" + Log.getStackTraceString(new Throwable()));
      paramArrayOfInt = new ArrayList();
      return paramArrayOfInt;
    }
    finally
    {
      this.mServiceLock.readLock().unlock();
    }
  }
  
  public int getPriority(BluetoothDevice paramBluetoothDevice)
  {
    try
    {
      this.mServiceLock.readLock().lock();
      if ((this.mService != null) && (isEnabled()) && (isValidDevice(paramBluetoothDevice)))
      {
        int i = this.mService.getPriority(paramBluetoothDevice);
        return i;
      }
      if (this.mService == null) {
        Log.w("BluetoothA2dp", "Proxy not attached to service");
      }
      return 0;
    }
    catch (RemoteException paramBluetoothDevice)
    {
      Log.e("BluetoothA2dp", "Stack:" + Log.getStackTraceString(new Throwable()));
      return 0;
    }
    finally
    {
      this.mServiceLock.readLock().unlock();
    }
  }
  
  public boolean isA2dpPlaying(BluetoothDevice paramBluetoothDevice)
  {
    try
    {
      this.mServiceLock.readLock().lock();
      if ((this.mService != null) && (isEnabled()) && (isValidDevice(paramBluetoothDevice)))
      {
        boolean bool = this.mService.isA2dpPlaying(paramBluetoothDevice);
        return bool;
      }
      if (this.mService == null) {
        Log.w("BluetoothA2dp", "Proxy not attached to service");
      }
      return false;
    }
    catch (RemoteException paramBluetoothDevice)
    {
      Log.e("BluetoothA2dp", "Stack:" + Log.getStackTraceString(new Throwable()));
      return false;
    }
    finally
    {
      this.mServiceLock.readLock().unlock();
    }
  }
  
  public boolean isAptXHDAvailable(BluetoothDevice paramBluetoothDevice)
  {
    log("isAptXHDAvailablet(" + paramBluetoothDevice + ")");
    if ((this.mService != null) && (isEnabled()) && (isValidDevice(paramBluetoothDevice))) {
      try
      {
        boolean bool = this.mService.isAptXHDAvailable(paramBluetoothDevice);
        return bool;
      }
      catch (RemoteException paramBluetoothDevice)
      {
        Log.e("BluetoothA2dp", "Stack:" + Log.getStackTraceString(new Throwable()));
        return false;
      }
    }
    if (this.mService == null) {
      Log.w("BluetoothA2dp", "Proxy not attached to service");
    }
    return false;
  }
  
  public boolean isAvrcpAbsoluteVolumeSupported()
  {
    Log.d("BluetoothA2dp", "isAvrcpAbsoluteVolumeSupported");
    try
    {
      this.mServiceLock.readLock().lock();
      if ((this.mService != null) && (isEnabled()))
      {
        boolean bool = this.mService.isAvrcpAbsoluteVolumeSupported();
        return bool;
      }
      if (this.mService == null) {
        Log.w("BluetoothA2dp", "Proxy not attached to service");
      }
      return false;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("BluetoothA2dp", "Error talking to BT service in isAvrcpAbsoluteVolumeSupported()", localRemoteException);
      return false;
    }
    finally
    {
      this.mServiceLock.readLock().unlock();
    }
  }
  
  public void setAvrcpAbsoluteVolume(int paramInt)
  {
    Log.d("BluetoothA2dp", "setAvrcpAbsoluteVolume");
    try
    {
      this.mServiceLock.readLock().lock();
      if ((this.mService != null) && (isEnabled())) {
        this.mService.setAvrcpAbsoluteVolume(paramInt);
      }
      if (this.mService == null) {
        Log.w("BluetoothA2dp", "Proxy not attached to service");
      }
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("BluetoothA2dp", "Error talking to BT service in setAvrcpAbsoluteVolume()", localRemoteException);
      return;
    }
    finally
    {
      this.mServiceLock.readLock().unlock();
    }
  }
  
  public boolean setPriority(BluetoothDevice paramBluetoothDevice, int paramInt)
  {
    log("setPriority(" + paramBluetoothDevice + ", " + paramInt + ")");
    try
    {
      this.mServiceLock.readLock().lock();
      if ((this.mService != null) && (isEnabled()))
      {
        boolean bool = isValidDevice(paramBluetoothDevice);
        if (bool)
        {
          if ((paramInt != 0) && (paramInt != 100)) {
            return false;
          }
          bool = this.mService.setPriority(paramBluetoothDevice, paramInt);
          return bool;
        }
      }
      if (this.mService == null) {
        Log.w("BluetoothA2dp", "Proxy not attached to service");
      }
      return false;
    }
    catch (RemoteException paramBluetoothDevice)
    {
      Log.e("BluetoothA2dp", "Stack:" + Log.getStackTraceString(new Throwable()));
      return false;
    }
    finally
    {
      this.mServiceLock.readLock().unlock();
    }
  }
  
  public boolean shouldSendVolumeKeys(BluetoothDevice paramBluetoothDevice)
  {
    if ((isEnabled()) && (isValidDevice(paramBluetoothDevice)))
    {
      paramBluetoothDevice = paramBluetoothDevice.getUuids();
      if (paramBluetoothDevice == null) {
        return false;
      }
      int j = paramBluetoothDevice.length;
      int i = 0;
      while (i < j)
      {
        if (BluetoothUuid.isAvrcpTarget(paramBluetoothDevice[i])) {
          return true;
        }
        i += 1;
      }
    }
    return false;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/bluetooth/BluetoothA2dp.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */