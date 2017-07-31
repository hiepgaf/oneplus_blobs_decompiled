package android.media.midi;

import android.bluetooth.BluetoothDevice;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import java.util.concurrent.ConcurrentHashMap;

public final class MidiManager
{
  public static final String BLUETOOTH_MIDI_SERVICE_CLASS = "com.android.bluetoothmidiservice.BluetoothMidiService";
  public static final String BLUETOOTH_MIDI_SERVICE_INTENT = "android.media.midi.BluetoothMidiService";
  public static final String BLUETOOTH_MIDI_SERVICE_PACKAGE = "com.android.bluetoothmidiservice";
  private static final String TAG = "MidiManager";
  private ConcurrentHashMap<DeviceCallback, DeviceListener> mDeviceListeners = new ConcurrentHashMap();
  private final IMidiManager mService;
  private final IBinder mToken = new Binder();
  
  public MidiManager(IMidiManager paramIMidiManager)
  {
    this.mService = paramIMidiManager;
  }
  
  private void sendOpenDeviceResponse(final MidiDevice paramMidiDevice, final OnDeviceOpenedListener paramOnDeviceOpenedListener, Handler paramHandler)
  {
    if (paramHandler != null)
    {
      paramHandler.post(new Runnable()
      {
        public void run()
        {
          paramOnDeviceOpenedListener.onDeviceOpened(paramMidiDevice);
        }
      });
      return;
    }
    paramOnDeviceOpenedListener.onDeviceOpened(paramMidiDevice);
  }
  
  public MidiDeviceServer createDeviceServer(MidiReceiver[] paramArrayOfMidiReceiver, int paramInt1, String[] paramArrayOfString1, String[] paramArrayOfString2, Bundle paramBundle, int paramInt2, MidiDeviceServer.Callback paramCallback)
  {
    try
    {
      paramCallback = new MidiDeviceServer(this.mService, paramArrayOfMidiReceiver, paramInt1, paramCallback);
      if (this.mService.registerDeviceServer(paramCallback.getBinderInterface(), paramArrayOfMidiReceiver.length, paramInt1, paramArrayOfString1, paramArrayOfString2, paramBundle, paramInt2) == null)
      {
        Log.e("MidiManager", "registerVirtualDevice failed");
        return null;
      }
      return paramCallback;
    }
    catch (RemoteException paramArrayOfMidiReceiver)
    {
      throw paramArrayOfMidiReceiver.rethrowFromSystemServer();
    }
  }
  
  public MidiDeviceInfo[] getDevices()
  {
    try
    {
      MidiDeviceInfo[] arrayOfMidiDeviceInfo = this.mService.getDevices();
      return arrayOfMidiDeviceInfo;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void openBluetoothDevice(BluetoothDevice paramBluetoothDevice, final OnDeviceOpenedListener paramOnDeviceOpenedListener, final Handler paramHandler)
  {
    paramOnDeviceOpenedListener = new IMidiDeviceOpenCallback.Stub()
    {
      public void onDeviceOpened(IMidiDeviceServer paramAnonymousIMidiDeviceServer, IBinder paramAnonymousIBinder)
      {
        if (paramAnonymousIMidiDeviceServer != null) {}
        for (;;)
        {
          try
          {
            paramAnonymousIMidiDeviceServer = new MidiDevice(paramAnonymousIMidiDeviceServer.getDeviceInfo(), paramAnonymousIMidiDeviceServer, MidiManager.-get0(MidiManager.this), MidiManager.-get1(MidiManager.this), paramAnonymousIBinder);
            MidiManager.-wrap0(MidiManager.this, paramAnonymousIMidiDeviceServer, paramOnDeviceOpenedListener, paramHandler);
            return;
          }
          catch (RemoteException paramAnonymousIMidiDeviceServer)
          {
            Log.e("MidiManager", "remote exception in getDeviceInfo()");
            paramAnonymousIMidiDeviceServer = null;
            continue;
          }
          paramAnonymousIMidiDeviceServer = null;
        }
      }
    };
    try
    {
      this.mService.openBluetoothDevice(this.mToken, paramBluetoothDevice, paramOnDeviceOpenedListener);
      return;
    }
    catch (RemoteException paramBluetoothDevice)
    {
      throw paramBluetoothDevice.rethrowFromSystemServer();
    }
  }
  
  public void openDevice(final MidiDeviceInfo paramMidiDeviceInfo, final OnDeviceOpenedListener paramOnDeviceOpenedListener, final Handler paramHandler)
  {
    paramOnDeviceOpenedListener = new IMidiDeviceOpenCallback.Stub()
    {
      public void onDeviceOpened(IMidiDeviceServer paramAnonymousIMidiDeviceServer, IBinder paramAnonymousIBinder)
      {
        if (paramAnonymousIMidiDeviceServer != null) {}
        for (paramAnonymousIMidiDeviceServer = new MidiDevice(paramMidiDeviceInfo, paramAnonymousIMidiDeviceServer, MidiManager.-get0(MidiManager.this), MidiManager.-get1(MidiManager.this), paramAnonymousIBinder);; paramAnonymousIMidiDeviceServer = null)
        {
          MidiManager.-wrap0(MidiManager.this, paramAnonymousIMidiDeviceServer, paramOnDeviceOpenedListener, paramHandler);
          return;
        }
      }
    };
    try
    {
      this.mService.openDevice(this.mToken, paramMidiDeviceInfo, paramOnDeviceOpenedListener);
      return;
    }
    catch (RemoteException paramMidiDeviceInfo)
    {
      throw paramMidiDeviceInfo.rethrowFromSystemServer();
    }
  }
  
  public void registerDeviceCallback(DeviceCallback paramDeviceCallback, Handler paramHandler)
  {
    paramHandler = new DeviceListener(paramDeviceCallback, paramHandler);
    try
    {
      this.mService.registerListener(this.mToken, paramHandler);
      this.mDeviceListeners.put(paramDeviceCallback, paramHandler);
      return;
    }
    catch (RemoteException paramDeviceCallback)
    {
      throw paramDeviceCallback.rethrowFromSystemServer();
    }
  }
  
  public void unregisterDeviceCallback(DeviceCallback paramDeviceCallback)
  {
    paramDeviceCallback = (DeviceListener)this.mDeviceListeners.remove(paramDeviceCallback);
    if (paramDeviceCallback != null) {}
    try
    {
      this.mService.unregisterListener(this.mToken, paramDeviceCallback);
      return;
    }
    catch (RemoteException paramDeviceCallback)
    {
      throw paramDeviceCallback.rethrowFromSystemServer();
    }
  }
  
  public static class DeviceCallback
  {
    public void onDeviceAdded(MidiDeviceInfo paramMidiDeviceInfo) {}
    
    public void onDeviceRemoved(MidiDeviceInfo paramMidiDeviceInfo) {}
    
    public void onDeviceStatusChanged(MidiDeviceStatus paramMidiDeviceStatus) {}
  }
  
  private class DeviceListener
    extends IMidiDeviceListener.Stub
  {
    private final MidiManager.DeviceCallback mCallback;
    private final Handler mHandler;
    
    public DeviceListener(MidiManager.DeviceCallback paramDeviceCallback, Handler paramHandler)
    {
      this.mCallback = paramDeviceCallback;
      this.mHandler = paramHandler;
    }
    
    public void onDeviceAdded(final MidiDeviceInfo paramMidiDeviceInfo)
    {
      if (this.mHandler != null)
      {
        this.mHandler.post(new Runnable()
        {
          public void run()
          {
            MidiManager.DeviceListener.-get0(MidiManager.DeviceListener.this).onDeviceAdded(paramMidiDeviceInfo);
          }
        });
        return;
      }
      this.mCallback.onDeviceAdded(paramMidiDeviceInfo);
    }
    
    public void onDeviceRemoved(final MidiDeviceInfo paramMidiDeviceInfo)
    {
      if (this.mHandler != null)
      {
        this.mHandler.post(new Runnable()
        {
          public void run()
          {
            MidiManager.DeviceListener.-get0(MidiManager.DeviceListener.this).onDeviceRemoved(paramMidiDeviceInfo);
          }
        });
        return;
      }
      this.mCallback.onDeviceRemoved(paramMidiDeviceInfo);
    }
    
    public void onDeviceStatusChanged(final MidiDeviceStatus paramMidiDeviceStatus)
    {
      if (this.mHandler != null)
      {
        this.mHandler.post(new Runnable()
        {
          public void run()
          {
            MidiManager.DeviceListener.-get0(MidiManager.DeviceListener.this).onDeviceStatusChanged(paramMidiDeviceStatus);
          }
        });
        return;
      }
      this.mCallback.onDeviceStatusChanged(paramMidiDeviceStatus);
    }
  }
  
  public static abstract interface OnDeviceOpenedListener
  {
    public abstract void onDeviceOpened(MidiDevice paramMidiDevice);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/midi/MidiManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */