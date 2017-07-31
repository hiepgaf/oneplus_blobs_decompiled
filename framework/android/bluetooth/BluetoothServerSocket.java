package android.bluetooth;

import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;
import android.util.Log;
import java.io.Closeable;
import java.io.IOException;

public final class BluetoothServerSocket
  implements Closeable
{
  private static final String TAG = "BluetoothServerSocket";
  private int mChannel;
  private Handler mHandler;
  private int mMessage;
  final BluetoothSocket mSocket;
  
  BluetoothServerSocket(int paramInt1, boolean paramBoolean1, boolean paramBoolean2, int paramInt2)
    throws IOException
  {
    this.mChannel = paramInt2;
    this.mSocket = new BluetoothSocket(paramInt1, -1, paramBoolean1, paramBoolean2, null, paramInt2, null);
    if (paramInt2 == -2) {
      this.mSocket.setExcludeSdp(true);
    }
  }
  
  BluetoothServerSocket(int paramInt1, boolean paramBoolean1, boolean paramBoolean2, int paramInt2, boolean paramBoolean3, boolean paramBoolean4)
    throws IOException
  {
    this.mChannel = paramInt2;
    this.mSocket = new BluetoothSocket(paramInt1, -1, paramBoolean1, paramBoolean2, null, paramInt2, null, paramBoolean3, paramBoolean4);
    if (paramInt2 == -2) {
      this.mSocket.setExcludeSdp(true);
    }
  }
  
  BluetoothServerSocket(int paramInt, boolean paramBoolean1, boolean paramBoolean2, ParcelUuid paramParcelUuid)
    throws IOException
  {
    this.mSocket = new BluetoothSocket(paramInt, -1, paramBoolean1, paramBoolean2, null, -1, paramParcelUuid);
    this.mChannel = this.mSocket.getPort();
  }
  
  public BluetoothSocket accept()
    throws IOException
  {
    return accept(-1);
  }
  
  public BluetoothSocket accept(int paramInt)
    throws IOException
  {
    return this.mSocket.accept(paramInt);
  }
  
  public void close()
    throws IOException
  {
    try
    {
      if (this.mHandler != null) {
        this.mHandler.obtainMessage(this.mMessage).sendToTarget();
      }
      this.mSocket.close();
      return;
    }
    finally {}
  }
  
  public int getChannel()
  {
    return this.mChannel;
  }
  
  void setChannel(int paramInt)
  {
    if ((this.mSocket != null) && (this.mSocket.getPort() != paramInt)) {
      Log.w("BluetoothServerSocket", "The port set is different that the underlying port. mSocket.getPort(): " + this.mSocket.getPort() + " requested newChannel: " + paramInt);
    }
    this.mChannel = paramInt;
  }
  
  void setCloseHandler(Handler paramHandler, int paramInt)
  {
    try
    {
      this.mHandler = paramHandler;
      this.mMessage = paramInt;
      return;
    }
    finally
    {
      paramHandler = finally;
      throw paramHandler;
    }
  }
  
  void setServiceName(String paramString)
  {
    this.mSocket.setServiceName(paramString);
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("ServerSocket: Type: ");
    switch (this.mSocket.getConnectionType())
    {
    }
    for (;;)
    {
      localStringBuilder.append(" Channel: ").append(this.mChannel);
      return localStringBuilder.toString();
      localStringBuilder.append("TYPE_RFCOMM");
      continue;
      localStringBuilder.append("TYPE_L2CAP");
      continue;
      localStringBuilder.append("TYPE_SCO");
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/bluetooth/BluetoothServerSocket.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */