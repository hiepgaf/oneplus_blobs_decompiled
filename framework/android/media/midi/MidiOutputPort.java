package android.media.midi;

import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.ParcelFileDescriptor.AutoCloseInputStream;
import android.os.RemoteException;
import android.util.Log;
import com.android.internal.midi.MidiDispatcher;
import dalvik.system.CloseGuard;
import java.io.Closeable;
import java.io.FileInputStream;
import java.io.IOException;
import libcore.io.IoUtils;

public final class MidiOutputPort
  extends MidiSender
  implements Closeable
{
  private static final String TAG = "MidiOutputPort";
  private IMidiDeviceServer mDeviceServer;
  private final MidiDispatcher mDispatcher = new MidiDispatcher();
  private final CloseGuard mGuard = CloseGuard.get();
  private final FileInputStream mInputStream;
  private boolean mIsClosed;
  private final int mPortNumber;
  private final Thread mThread = new Thread()
  {
    public void run()
    {
      byte[] arrayOfByte = new byte['Ѐ'];
      for (;;)
      {
        try
        {
          i = MidiOutputPort.-get1(MidiOutputPort.this).read(arrayOfByte);
          if (i < 0) {
            return;
          }
          j = MidiPortImpl.getPacketType(arrayOfByte, i);
          switch (j)
          {
          case 1: 
            Log.e("MidiOutputPort", "Unknown packet type " + j);
            continue;
          }
        }
        catch (IOException localIOException)
        {
          int i;
          Log.e("MidiOutputPort", "read failed");
          return;
          int j = MidiPortImpl.getDataOffset(localIOException, i);
          int k = MidiPortImpl.getDataSize(localIOException, i);
          long l = MidiPortImpl.getPacketTimestamp(localIOException, i);
          MidiOutputPort.-get0(MidiOutputPort.this).send(localIOException, j, k, l);
          continue;
        }
        finally
        {
          IoUtils.closeQuietly(MidiOutputPort.-get1(MidiOutputPort.this));
        }
        MidiOutputPort.-get0(MidiOutputPort.this).flush();
      }
    }
  };
  private final IBinder mToken;
  
  MidiOutputPort(IMidiDeviceServer paramIMidiDeviceServer, IBinder paramIBinder, ParcelFileDescriptor paramParcelFileDescriptor, int paramInt)
  {
    this.mDeviceServer = paramIMidiDeviceServer;
    this.mToken = paramIBinder;
    this.mPortNumber = paramInt;
    this.mInputStream = new ParcelFileDescriptor.AutoCloseInputStream(paramParcelFileDescriptor);
    this.mThread.start();
    this.mGuard.open("close");
  }
  
  MidiOutputPort(ParcelFileDescriptor paramParcelFileDescriptor, int paramInt)
  {
    this(null, null, paramParcelFileDescriptor, paramInt);
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
      this.mInputStream.close();
      IMidiDeviceServer localIMidiDeviceServer = this.mDeviceServer;
      if (localIMidiDeviceServer != null) {}
      try
      {
        this.mDeviceServer.closePort(this.mToken);
        this.mIsClosed = true;
        return;
      }
      catch (RemoteException localRemoteException)
      {
        for (;;)
        {
          Log.e("MidiOutputPort", "RemoteException in MidiOutputPort.close()");
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
      this.mDeviceServer = null;
      close();
      return;
    }
    finally
    {
      super.finalize();
    }
  }
  
  public final int getPortNumber()
  {
    return this.mPortNumber;
  }
  
  public void onConnect(MidiReceiver paramMidiReceiver)
  {
    this.mDispatcher.getSender().connect(paramMidiReceiver);
  }
  
  public void onDisconnect(MidiReceiver paramMidiReceiver)
  {
    this.mDispatcher.getSender().disconnect(paramMidiReceiver);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/midi/MidiOutputPort.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */