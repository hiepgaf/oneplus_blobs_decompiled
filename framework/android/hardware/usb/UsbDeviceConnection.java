package android.hardware.usb;

import android.content.Context;
import android.os.ParcelFileDescriptor;
import java.io.FileDescriptor;

public class UsbDeviceConnection
{
  private static final String TAG = "UsbDeviceConnection";
  private Context mContext;
  private final UsbDevice mDevice;
  private long mNativeContext;
  
  public UsbDeviceConnection(UsbDevice paramUsbDevice)
  {
    this.mDevice = paramUsbDevice;
  }
  
  private static void checkBounds(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if (paramArrayOfByte != null) {}
    for (int i = paramArrayOfByte.length; (paramInt1 < 0) || (paramInt1 + paramInt2 > i); i = 0) {
      throw new IllegalArgumentException("Buffer start or length out of bounds.");
    }
  }
  
  private native int native_bulk_request(int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3, int paramInt4);
  
  private native boolean native_claim_interface(int paramInt, boolean paramBoolean);
  
  private native void native_close();
  
  private native int native_control_request(int paramInt1, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfByte, int paramInt5, int paramInt6, int paramInt7);
  
  private native byte[] native_get_desc();
  
  private native int native_get_fd();
  
  private native String native_get_serial();
  
  private native boolean native_open(String paramString, FileDescriptor paramFileDescriptor);
  
  private native boolean native_release_interface(int paramInt);
  
  private native UsbRequest native_request_wait();
  
  private native boolean native_set_configuration(int paramInt);
  
  private native boolean native_set_interface(int paramInt1, int paramInt2);
  
  public int bulkTransfer(UsbEndpoint paramUsbEndpoint, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    return bulkTransfer(paramUsbEndpoint, paramArrayOfByte, 0, paramInt1, paramInt2);
  }
  
  public int bulkTransfer(UsbEndpoint paramUsbEndpoint, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3)
  {
    checkBounds(paramArrayOfByte, paramInt1, paramInt2);
    return native_bulk_request(paramUsbEndpoint.getAddress(), paramArrayOfByte, paramInt1, paramInt2, paramInt3);
  }
  
  public boolean claimInterface(UsbInterface paramUsbInterface, boolean paramBoolean)
  {
    return native_claim_interface(paramUsbInterface.getId(), paramBoolean);
  }
  
  public void close()
  {
    native_close();
  }
  
  public int controlTransfer(int paramInt1, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfByte, int paramInt5, int paramInt6)
  {
    return controlTransfer(paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfByte, 0, paramInt5, paramInt6);
  }
  
  public int controlTransfer(int paramInt1, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfByte, int paramInt5, int paramInt6, int paramInt7)
  {
    checkBounds(paramArrayOfByte, paramInt5, paramInt6);
    return native_control_request(paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfByte, paramInt5, paramInt6, paramInt7);
  }
  
  public Context getContext()
  {
    return this.mContext;
  }
  
  public int getFileDescriptor()
  {
    return native_get_fd();
  }
  
  public byte[] getRawDescriptors()
  {
    return native_get_desc();
  }
  
  public String getSerial()
  {
    return native_get_serial();
  }
  
  boolean open(String paramString, ParcelFileDescriptor paramParcelFileDescriptor, Context paramContext)
  {
    this.mContext = paramContext.getApplicationContext();
    return native_open(paramString, paramParcelFileDescriptor.getFileDescriptor());
  }
  
  public boolean releaseInterface(UsbInterface paramUsbInterface)
  {
    return native_release_interface(paramUsbInterface.getId());
  }
  
  public UsbRequest requestWait()
  {
    UsbRequest localUsbRequest = native_request_wait();
    if (localUsbRequest != null) {
      localUsbRequest.dequeue();
    }
    return localUsbRequest;
  }
  
  public boolean setConfiguration(UsbConfiguration paramUsbConfiguration)
  {
    return native_set_configuration(paramUsbConfiguration.getId());
  }
  
  public boolean setInterface(UsbInterface paramUsbInterface)
  {
    return native_set_interface(paramUsbInterface.getId(), paramUsbInterface.getAlternateSetting());
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/usb/UsbDeviceConnection.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */