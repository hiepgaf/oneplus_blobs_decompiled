package android.hardware.usb;

import android.util.Log;
import java.nio.ByteBuffer;

public class UsbRequest
{
  private static final String TAG = "UsbRequest";
  private ByteBuffer mBuffer;
  private Object mClientData;
  private UsbEndpoint mEndpoint;
  private int mLength;
  private long mNativeContext;
  
  private native boolean native_cancel();
  
  private native void native_close();
  
  private native int native_dequeue_array(byte[] paramArrayOfByte, int paramInt, boolean paramBoolean);
  
  private native int native_dequeue_direct();
  
  private native boolean native_init(UsbDeviceConnection paramUsbDeviceConnection, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  private native boolean native_queue_array(byte[] paramArrayOfByte, int paramInt, boolean paramBoolean);
  
  private native boolean native_queue_direct(ByteBuffer paramByteBuffer, int paramInt, boolean paramBoolean);
  
  public boolean cancel()
  {
    return native_cancel();
  }
  
  public void close()
  {
    this.mEndpoint = null;
    native_close();
  }
  
  void dequeue()
  {
    boolean bool;
    if (this.mEndpoint.getDirection() == 0)
    {
      bool = true;
      if (!this.mBuffer.isDirect()) {
        break label63;
      }
    }
    label63:
    for (int i = native_dequeue_direct();; i = native_dequeue_array(this.mBuffer.array(), this.mLength, bool))
    {
      if (i >= 0) {
        this.mBuffer.position(Math.min(i, this.mLength));
      }
      this.mBuffer = null;
      this.mLength = 0;
      return;
      bool = false;
      break;
    }
  }
  
  protected void finalize()
    throws Throwable
  {
    try
    {
      if (this.mEndpoint != null)
      {
        Log.v("UsbRequest", "endpoint still open in finalize(): " + this);
        close();
      }
      return;
    }
    finally
    {
      super.finalize();
    }
  }
  
  public Object getClientData()
  {
    return this.mClientData;
  }
  
  public UsbEndpoint getEndpoint()
  {
    return this.mEndpoint;
  }
  
  public boolean initialize(UsbDeviceConnection paramUsbDeviceConnection, UsbEndpoint paramUsbEndpoint)
  {
    this.mEndpoint = paramUsbEndpoint;
    return native_init(paramUsbDeviceConnection, paramUsbEndpoint.getAddress(), paramUsbEndpoint.getAttributes(), paramUsbEndpoint.getMaxPacketSize(), paramUsbEndpoint.getInterval());
  }
  
  public boolean queue(ByteBuffer paramByteBuffer, int paramInt)
  {
    if (this.mEndpoint.getDirection() == 0)
    {
      bool = true;
      if (!paramByteBuffer.isDirect()) {
        break label48;
      }
    }
    for (boolean bool = native_queue_direct(paramByteBuffer, paramInt, bool);; bool = native_queue_array(paramByteBuffer.array(), paramInt, bool))
    {
      if (bool)
      {
        this.mBuffer = paramByteBuffer;
        this.mLength = paramInt;
      }
      return bool;
      bool = false;
      break;
      label48:
      if (!paramByteBuffer.hasArray()) {
        break label69;
      }
    }
    label69:
    throw new IllegalArgumentException("buffer is not direct and has no array");
  }
  
  public void setClientData(Object paramObject)
  {
    this.mClientData = paramObject;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/usb/UsbRequest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */