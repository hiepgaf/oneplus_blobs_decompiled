package android.bluetooth;

import java.io.IOException;
import java.io.InputStream;

final class BluetoothInputStream
  extends InputStream
{
  private BluetoothSocket mSocket;
  
  BluetoothInputStream(BluetoothSocket paramBluetoothSocket)
  {
    this.mSocket = paramBluetoothSocket;
  }
  
  public int available()
    throws IOException
  {
    return this.mSocket.available();
  }
  
  public void close()
    throws IOException
  {
    this.mSocket.close();
  }
  
  public int read()
    throws IOException
  {
    byte[] arrayOfByte = new byte[1];
    if (this.mSocket.read(arrayOfByte, 0, 1) == 1) {
      return arrayOfByte[0] & 0xFF;
    }
    return -1;
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if (paramArrayOfByte == null) {
      throw new NullPointerException("byte array is null");
    }
    if (((paramInt1 | paramInt2) < 0) || (paramInt2 > paramArrayOfByte.length - paramInt1)) {
      throw new ArrayIndexOutOfBoundsException("invalid offset or length");
    }
    return this.mSocket.read(paramArrayOfByte, paramInt1, paramInt2);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/bluetooth/BluetoothInputStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */