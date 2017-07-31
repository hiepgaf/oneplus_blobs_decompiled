package android.hardware;

import android.os.ParcelFileDescriptor;
import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.ByteBuffer;

public class SerialPort
{
  private static final String TAG = "SerialPort";
  private ParcelFileDescriptor mFileDescriptor;
  private final String mName;
  private int mNativeContext;
  
  public SerialPort(String paramString)
  {
    this.mName = paramString;
  }
  
  private native void native_close();
  
  private native void native_open(FileDescriptor paramFileDescriptor, int paramInt)
    throws IOException;
  
  private native int native_read_array(byte[] paramArrayOfByte, int paramInt)
    throws IOException;
  
  private native int native_read_direct(ByteBuffer paramByteBuffer, int paramInt)
    throws IOException;
  
  private native void native_send_break();
  
  private native void native_write_array(byte[] paramArrayOfByte, int paramInt)
    throws IOException;
  
  private native void native_write_direct(ByteBuffer paramByteBuffer, int paramInt)
    throws IOException;
  
  public void close()
    throws IOException
  {
    if (this.mFileDescriptor != null)
    {
      this.mFileDescriptor.close();
      this.mFileDescriptor = null;
    }
    native_close();
  }
  
  public String getName()
  {
    return this.mName;
  }
  
  public void open(ParcelFileDescriptor paramParcelFileDescriptor, int paramInt)
    throws IOException
  {
    native_open(paramParcelFileDescriptor.getFileDescriptor(), paramInt);
    this.mFileDescriptor = paramParcelFileDescriptor;
  }
  
  public int read(ByteBuffer paramByteBuffer)
    throws IOException
  {
    if (paramByteBuffer.isDirect()) {
      return native_read_direct(paramByteBuffer, paramByteBuffer.remaining());
    }
    if (paramByteBuffer.hasArray()) {
      return native_read_array(paramByteBuffer.array(), paramByteBuffer.remaining());
    }
    throw new IllegalArgumentException("buffer is not direct and has no array");
  }
  
  public void sendBreak()
  {
    native_send_break();
  }
  
  public void write(ByteBuffer paramByteBuffer, int paramInt)
    throws IOException
  {
    if (paramByteBuffer.isDirect())
    {
      native_write_direct(paramByteBuffer, paramInt);
      return;
    }
    if (paramByteBuffer.hasArray())
    {
      native_write_array(paramByteBuffer.array(), paramInt);
      return;
    }
    throw new IllegalArgumentException("buffer is not direct and has no array");
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/SerialPort.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */