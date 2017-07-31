package android.media.midi;

import android.os.Binder;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.Process;
import android.os.RemoteException;
import android.util.Log;
import dalvik.system.CloseGuard;
import java.io.Closeable;
import java.io.IOException;
import libcore.io.IoUtils;

public final class MidiDevice
  implements Closeable
{
  private static final String TAG = "MidiDevice";
  private final IBinder mClientToken;
  private final MidiDeviceInfo mDeviceInfo;
  private final IMidiDeviceServer mDeviceServer;
  private final IBinder mDeviceToken;
  private final CloseGuard mGuard = CloseGuard.get();
  private boolean mIsDeviceClosed;
  private final IMidiManager mMidiManager;
  
  MidiDevice(MidiDeviceInfo paramMidiDeviceInfo, IMidiDeviceServer paramIMidiDeviceServer, IMidiManager paramIMidiManager, IBinder paramIBinder1, IBinder paramIBinder2)
  {
    this.mDeviceInfo = paramMidiDeviceInfo;
    this.mDeviceServer = paramIMidiDeviceServer;
    this.mMidiManager = paramIMidiManager;
    this.mClientToken = paramIBinder1;
    this.mDeviceToken = paramIBinder2;
    this.mGuard.open("close");
  }
  
  public void close()
    throws IOException
  {
    synchronized (this.mGuard)
    {
      if (!this.mIsDeviceClosed)
      {
        this.mGuard.close();
        this.mIsDeviceClosed = true;
      }
      try
      {
        this.mMidiManager.closeDevice(this.mClientToken, this.mDeviceToken);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        for (;;)
        {
          Log.e("MidiDevice", "RemoteException in closeDevice");
        }
      }
    }
  }
  
  public MidiConnection connectPorts(MidiInputPort paramMidiInputPort, int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= this.mDeviceInfo.getOutputPortCount())) {
      throw new IllegalArgumentException("outputPortNumber out of range");
    }
    if (this.mIsDeviceClosed) {
      return null;
    }
    ParcelFileDescriptor localParcelFileDescriptor = paramMidiInputPort.claimFileDescriptor();
    if (localParcelFileDescriptor == null) {
      return null;
    }
    try
    {
      Binder localBinder = new Binder();
      if (this.mDeviceServer.connectPorts(localBinder, localParcelFileDescriptor, paramInt) != Process.myPid()) {
        IoUtils.closeQuietly(localParcelFileDescriptor);
      }
      paramMidiInputPort = new MidiConnection(localBinder, paramMidiInputPort);
      return paramMidiInputPort;
    }
    catch (RemoteException paramMidiInputPort)
    {
      Log.e("MidiDevice", "RemoteException in connectPorts");
    }
    return null;
  }
  
  protected void finalize()
    throws Throwable
  {
    try
    {
      this.mGuard.warnIfOpen();
      close();
      return;
    }
    finally
    {
      super.finalize();
    }
  }
  
  public MidiDeviceInfo getInfo()
  {
    return this.mDeviceInfo;
  }
  
  public MidiInputPort openInputPort(int paramInt)
  {
    if (this.mIsDeviceClosed) {
      return null;
    }
    try
    {
      Object localObject = new Binder();
      ParcelFileDescriptor localParcelFileDescriptor = this.mDeviceServer.openInputPort((IBinder)localObject, paramInt);
      if (localParcelFileDescriptor == null) {
        return null;
      }
      localObject = new MidiInputPort(this.mDeviceServer, (IBinder)localObject, localParcelFileDescriptor, paramInt);
      return (MidiInputPort)localObject;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("MidiDevice", "RemoteException in openInputPort");
    }
    return null;
  }
  
  public MidiOutputPort openOutputPort(int paramInt)
  {
    if (this.mIsDeviceClosed) {
      return null;
    }
    try
    {
      Object localObject = new Binder();
      ParcelFileDescriptor localParcelFileDescriptor = this.mDeviceServer.openOutputPort((IBinder)localObject, paramInt);
      if (localParcelFileDescriptor == null) {
        return null;
      }
      localObject = new MidiOutputPort(this.mDeviceServer, (IBinder)localObject, localParcelFileDescriptor, paramInt);
      return (MidiOutputPort)localObject;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("MidiDevice", "RemoteException in openOutputPort");
    }
    return null;
  }
  
  public String toString()
  {
    return "MidiDevice: " + this.mDeviceInfo.toString();
  }
  
  public class MidiConnection
    implements Closeable
  {
    private final CloseGuard mGuard = CloseGuard.get();
    private final IMidiDeviceServer mInputPortDeviceServer;
    private final IBinder mInputPortToken;
    private boolean mIsClosed;
    private final IBinder mOutputPortToken;
    
    MidiConnection(IBinder paramIBinder, MidiInputPort paramMidiInputPort)
    {
      this.mInputPortDeviceServer = paramMidiInputPort.getDeviceServer();
      this.mInputPortToken = paramMidiInputPort.getToken();
      this.mOutputPortToken = paramIBinder;
      this.mGuard.open("close");
    }
    
    public void close()
      throws IOException
    {
      synchronized (this.mGuard)
      {
        boolean bool = this.mIsClosed;
        if (bool) {
          return;
        }
        this.mGuard.close();
        try
        {
          this.mInputPortDeviceServer.closePort(this.mInputPortToken);
          MidiDevice.-get0(MidiDevice.this).closePort(this.mOutputPortToken);
          this.mIsClosed = true;
          return;
        }
        catch (RemoteException localRemoteException)
        {
          for (;;)
          {
            Log.e("MidiDevice", "RemoteException in MidiConnection.close");
          }
        }
      }
    }
    
    protected void finalize()
      throws Throwable
    {
      try
      {
        this.mGuard.warnIfOpen();
        close();
        return;
      }
      finally
      {
        super.finalize();
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/midi/MidiDevice.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */