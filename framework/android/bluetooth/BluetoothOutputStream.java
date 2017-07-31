package android.bluetooth;

import java.io.IOException;
import java.io.OutputStream;

final class BluetoothOutputStream
  extends OutputStream
{
  private BluetoothSocket mSocket;
  
  BluetoothOutputStream(BluetoothSocket paramBluetoothSocket)
  {
    this.mSocket = paramBluetoothSocket;
  }
  
  public void close()
    throws IOException
  {
    this.mSocket.close();
  }
  
  public void flush()
    throws IOException
  {
    this.mSocket.flush();
  }
  
  public void write(int paramInt)
    throws IOException
  {
    int i = (byte)paramInt;
    this.mSocket.write(new byte[] { i }, 0, 1);
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if (paramArrayOfByte == null) {
      throw new NullPointerException("buffer is null");
    }
    if (((paramInt1 | paramInt2) < 0) || (paramInt2 > paramArrayOfByte.length - paramInt1)) {
      throw new IndexOutOfBoundsException("invalid offset or length");
    }
    this.mSocket.write(paramArrayOfByte, paramInt1, paramInt2);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/bluetooth/BluetoothOutputStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */