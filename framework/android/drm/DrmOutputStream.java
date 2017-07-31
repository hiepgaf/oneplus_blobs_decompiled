package android.drm;

import android.os.ParcelFileDescriptor;
import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;
import android.util.Log;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.OutputStream;
import java.net.UnknownServiceException;
import java.util.Arrays;
import libcore.io.IoBridge;
import libcore.io.Streams;

public class DrmOutputStream
  extends OutputStream
{
  private static final String TAG = "DrmOutputStream";
  private final DrmManagerClient mClient;
  private final FileDescriptor mFd;
  private final ParcelFileDescriptor mPfd;
  private int mSessionId = -1;
  
  public DrmOutputStream(DrmManagerClient paramDrmManagerClient, ParcelFileDescriptor paramParcelFileDescriptor, String paramString)
    throws IOException
  {
    this.mClient = paramDrmManagerClient;
    this.mPfd = paramParcelFileDescriptor;
    this.mFd = paramParcelFileDescriptor.getFileDescriptor();
    this.mSessionId = this.mClient.openConvertSession(paramString);
    if (this.mSessionId == -1) {
      throw new UnknownServiceException("Failed to open DRM session for " + paramString);
    }
  }
  
  public void close()
    throws IOException
  {
    if (this.mSessionId == -1) {
      Log.w("DrmOutputStream", "Closing stream without finishing");
    }
    this.mPfd.close();
  }
  
  public void finish()
    throws IOException
  {
    DrmConvertedStatus localDrmConvertedStatus = this.mClient.closeConvertSession(this.mSessionId);
    if (localDrmConvertedStatus.statusCode == 1) {
      try
      {
        Os.lseek(this.mFd, localDrmConvertedStatus.offset, OsConstants.SEEK_SET);
        IoBridge.write(this.mFd, localDrmConvertedStatus.convertedData, 0, localDrmConvertedStatus.convertedData.length);
        this.mSessionId = -1;
        return;
      }
      catch (ErrnoException localErrnoException)
      {
        for (;;)
        {
          localErrnoException.rethrowAsIOException();
        }
      }
    }
    throw new IOException("Unexpected DRM status: " + localDrmConvertedStatus.statusCode);
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
    if (paramInt2 == paramArrayOfByte.length) {}
    for (;;)
    {
      paramArrayOfByte = this.mClient.convertData(this.mSessionId, paramArrayOfByte);
      if (paramArrayOfByte.statusCode != 1) {
        break;
      }
      IoBridge.write(this.mFd, paramArrayOfByte.convertedData, 0, paramArrayOfByte.convertedData.length);
      return;
      byte[] arrayOfByte = new byte[paramInt2];
      System.arraycopy(paramArrayOfByte, paramInt1, arrayOfByte, 0, paramInt2);
      paramArrayOfByte = arrayOfByte;
    }
    throw new IOException("Unexpected DRM status: " + paramArrayOfByte.statusCode);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/drm/DrmOutputStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */