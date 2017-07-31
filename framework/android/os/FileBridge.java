package android.os;

import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;
import android.util.Log;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteOrder;
import java.util.Arrays;
import libcore.io.IoBridge;
import libcore.io.IoUtils;
import libcore.io.Memory;
import libcore.io.Streams;

public class FileBridge
  extends Thread
{
  private static final int CMD_CLOSE = 3;
  private static final int CMD_FSYNC = 2;
  private static final int CMD_WRITE = 1;
  private static final int MSG_LENGTH = 8;
  private static final String TAG = "FileBridge";
  private final FileDescriptor mClient = new FileDescriptor();
  private volatile boolean mClosed;
  private final FileDescriptor mServer = new FileDescriptor();
  private FileDescriptor mTarget;
  
  public FileBridge()
  {
    try
    {
      Os.socketpair(OsConstants.AF_UNIX, OsConstants.SOCK_STREAM, 0, this.mServer, this.mClient);
      return;
    }
    catch (ErrnoException localErrnoException)
    {
      throw new RuntimeException("Failed to create bridge");
    }
  }
  
  public void forceClose()
  {
    IoUtils.closeQuietly(this.mTarget);
    IoUtils.closeQuietly(this.mServer);
    IoUtils.closeQuietly(this.mClient);
    this.mClosed = true;
  }
  
  public FileDescriptor getClientSocket()
  {
    return this.mClient;
  }
  
  public boolean isClosed()
  {
    return this.mClosed;
  }
  
  public void run()
  {
    byte[] arrayOfByte1 = new byte[' '];
    int i;
    do
    {
      try
      {
        do
        {
          if (IoBridge.read(this.mServer, arrayOfByte1, 0, 8) != 8) {
            break label202;
          }
          i = Memory.peekInt(arrayOfByte1, 0, ByteOrder.BIG_ENDIAN);
          if (i != 1) {
            break;
          }
          i = Memory.peekInt(arrayOfByte1, 4, ByteOrder.BIG_ENDIAN);
        } while (i <= 0);
        j = IoBridge.read(this.mServer, arrayOfByte1, 0, Math.min(arrayOfByte1.length, i));
        if (j == -1) {
          throw new IOException("Unexpected EOF; still expected " + i + " bytes");
        }
      }
      catch (ErrnoException|IOException localErrnoException)
      {
        for (;;)
        {
          int j;
          Log.wtf("FileBridge", "Failed during bridge", localErrnoException);
          return;
          IoBridge.write(this.mTarget, localErrnoException, 0, j);
          i -= j;
          continue;
          if (i != 2) {
            break;
          }
          Os.fsync(this.mTarget);
          IoBridge.write(this.mServer, localErrnoException, 0, 8);
        }
      }
      finally
      {
        forceClose();
      }
    } while (i != 3);
    Os.fsync(this.mTarget);
    Os.close(this.mTarget);
    this.mClosed = true;
    IoBridge.write(this.mServer, arrayOfByte2, 0, 8);
    label202:
    forceClose();
  }
  
  public void setTargetFile(FileDescriptor paramFileDescriptor)
  {
    this.mTarget = paramFileDescriptor;
  }
  
  public static class FileBridgeOutputStream
    extends OutputStream
  {
    private final FileDescriptor mClient;
    private final ParcelFileDescriptor mClientPfd;
    private final byte[] mTemp = new byte[8];
    
    public FileBridgeOutputStream(ParcelFileDescriptor paramParcelFileDescriptor)
    {
      this.mClientPfd = paramParcelFileDescriptor;
      this.mClient = paramParcelFileDescriptor.getFileDescriptor();
    }
    
    public FileBridgeOutputStream(FileDescriptor paramFileDescriptor)
    {
      this.mClientPfd = null;
      this.mClient = paramFileDescriptor;
    }
    
    private void writeCommandAndBlock(int paramInt, String paramString)
      throws IOException
    {
      Memory.pokeInt(this.mTemp, 0, paramInt, ByteOrder.BIG_ENDIAN);
      IoBridge.write(this.mClient, this.mTemp, 0, 8);
      if ((IoBridge.read(this.mClient, this.mTemp, 0, 8) == 8) && (Memory.peekInt(this.mTemp, 0, ByteOrder.BIG_ENDIAN) == paramInt)) {
        return;
      }
      throw new IOException("Failed to execute " + paramString + " across bridge");
    }
    
    public void close()
      throws IOException
    {
      try
      {
        writeCommandAndBlock(3, "close()");
        return;
      }
      finally
      {
        IoBridge.closeAndSignalBlockedThreads(this.mClient);
        IoUtils.closeQuietly(this.mClientPfd);
      }
    }
    
    public void fsync()
      throws IOException
    {
      writeCommandAndBlock(2, "fsync()");
    }
    
    public void write(int paramInt)
      throws IOException
    {
      Streams.writeSingleByte(this, paramInt);
    }
    
    public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws IOException
    {
      Arrays.checkOffsetAndCount(paramArrayOfByte.length, paramInt1, paramInt2);
      Memory.pokeInt(this.mTemp, 0, 1, ByteOrder.BIG_ENDIAN);
      Memory.pokeInt(this.mTemp, 4, paramInt2, ByteOrder.BIG_ENDIAN);
      IoBridge.write(this.mClient, this.mTemp, 0, 8);
      IoBridge.write(this.mClient, paramArrayOfByte, paramInt1, paramInt2);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/FileBridge.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */