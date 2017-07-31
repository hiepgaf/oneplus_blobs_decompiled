package android.media.midi;

import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.util.Log;
import dalvik.system.CloseGuard;
import java.io.Closeable;
import java.io.FileOutputStream;
import java.io.IOException;
import libcore.io.IoUtils;

public final class MidiInputPort
  extends MidiReceiver
  implements Closeable
{
  private static final String TAG = "MidiInputPort";
  private final byte[] mBuffer = new byte['Ѐ'];
  private IMidiDeviceServer mDeviceServer;
  private final CloseGuard mGuard = CloseGuard.get();
  private boolean mIsClosed;
  private FileOutputStream mOutputStream;
  private ParcelFileDescriptor mParcelFileDescriptor;
  private final int mPortNumber;
  private final IBinder mToken;
  
  MidiInputPort(IMidiDeviceServer paramIMidiDeviceServer, IBinder paramIBinder, ParcelFileDescriptor paramParcelFileDescriptor, int paramInt)
  {
    super(1015);
    this.mDeviceServer = paramIMidiDeviceServer;
    this.mToken = paramIBinder;
    this.mParcelFileDescriptor = paramParcelFileDescriptor;
    this.mPortNumber = paramInt;
    this.mOutputStream = new FileOutputStream(paramParcelFileDescriptor.getFileDescriptor());
    this.mGuard.open("close");
  }
  
  MidiInputPort(ParcelFileDescriptor paramParcelFileDescriptor, int paramInt)
  {
    this(null, null, paramParcelFileDescriptor, paramInt);
  }
  
  ParcelFileDescriptor claimFileDescriptor()
  {
    synchronized (this.mGuard)
    {
      synchronized (this.mBuffer)
      {
        ParcelFileDescriptor localParcelFileDescriptor = this.mParcelFileDescriptor;
        if (localParcelFileDescriptor == null) {
          return null;
        }
        IoUtils.closeQuietly(this.mOutputStream);
        this.mParcelFileDescriptor = null;
        this.mOutputStream = null;
        this.mIsClosed = true;
        return localParcelFileDescriptor;
      }
    }
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
      synchronized (this.mBuffer)
      {
        if (this.mParcelFileDescriptor != null)
        {
          this.mParcelFileDescriptor.close();
          this.mParcelFileDescriptor = null;
        }
        if (this.mOutputStream != null)
        {
          this.mOutputStream.close();
          this.mOutputStream = null;
        }
        ??? = this.mDeviceServer;
        if (??? == null) {}
      }
    }
    try
    {
      this.mDeviceServer.closePort(this.mToken);
      this.mIsClosed = true;
      return;
      localObject3 = finally;
      throw ((Throwable)localObject3);
      localObject2 = finally;
      throw ((Throwable)localObject2);
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        Log.e("MidiInputPort", "RemoteException in MidiInputPort.close()");
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
  
  IMidiDeviceServer getDeviceServer()
  {
    return this.mDeviceServer;
  }
  
  public final int getPortNumber()
  {
    return this.mPortNumber;
  }
  
  IBinder getToken()
  {
    return this.mToken;
  }
  
  public void onFlush()
    throws IOException
  {
    synchronized (this.mBuffer)
    {
      if (this.mOutputStream == null) {
        throw new IOException("MidiInputPort is closed");
      }
    }
    int i = MidiPortImpl.packFlush(this.mBuffer);
    this.mOutputStream.write(this.mBuffer, 0, i);
  }
  
  public void onSend(byte[] paramArrayOfByte, int paramInt1, int paramInt2, long paramLong)
    throws IOException
  {
    if ((paramInt1 < 0) || (paramInt2 < 0)) {}
    while (paramInt1 + paramInt2 > paramArrayOfByte.length) {
      throw new IllegalArgumentException("offset or count out of range");
    }
    if (paramInt2 > 1015) {
      throw new IllegalArgumentException("count exceeds max message size");
    }
    synchronized (this.mBuffer)
    {
      if (this.mOutputStream == null) {
        throw new IOException("MidiInputPort is closed");
      }
    }
    paramInt1 = MidiPortImpl.packData(paramArrayOfByte, paramInt1, paramInt2, paramLong, this.mBuffer);
    this.mOutputStream.write(this.mBuffer, 0, paramInt1);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/midi/MidiInputPort.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */