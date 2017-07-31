package android.mtp;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.os.CancellationSignal;
import android.os.CancellationSignal.OnCancelListener;
import android.os.ParcelFileDescriptor;
import android.os.UserManager;
import com.android.internal.util.Preconditions;
import java.io.IOException;

public final class MtpDevice
{
  private static final String TAG = "MtpDevice";
  private final UsbDevice mDevice;
  private long mNativeContext;
  
  static
  {
    System.loadLibrary("media_jni");
  }
  
  public MtpDevice(UsbDevice paramUsbDevice)
  {
    this.mDevice = paramUsbDevice;
  }
  
  private native void native_close();
  
  private native boolean native_delete_object(int paramInt);
  
  private native void native_discard_event_request(int paramInt);
  
  private native MtpDeviceInfo native_get_device_info();
  
  private native byte[] native_get_object(int paramInt, long paramLong);
  
  private native int[] native_get_object_handles(int paramInt1, int paramInt2, int paramInt3);
  
  private native MtpObjectInfo native_get_object_info(int paramInt);
  
  private native long native_get_object_size_long(int paramInt1, int paramInt2)
    throws IOException;
  
  private native int native_get_parent(int paramInt);
  
  private native long native_get_partial_object(int paramInt, long paramLong1, long paramLong2, byte[] paramArrayOfByte)
    throws IOException;
  
  private native int native_get_partial_object_64(int paramInt, long paramLong1, long paramLong2, byte[] paramArrayOfByte)
    throws IOException;
  
  private native int native_get_storage_id(int paramInt);
  
  private native int[] native_get_storage_ids();
  
  private native MtpStorageInfo native_get_storage_info(int paramInt);
  
  private native byte[] native_get_thumbnail(int paramInt);
  
  private native boolean native_import_file(int paramInt1, int paramInt2);
  
  private native boolean native_import_file(int paramInt, String paramString);
  
  private native boolean native_open(String paramString, int paramInt);
  
  private native MtpEvent native_reap_event_request(int paramInt)
    throws IOException;
  
  private native boolean native_send_object(int paramInt1, long paramLong, int paramInt2);
  
  private native MtpObjectInfo native_send_object_info(MtpObjectInfo paramMtpObjectInfo);
  
  private native int native_submit_event_request()
    throws IOException;
  
  public void close()
  {
    native_close();
  }
  
  public boolean deleteObject(int paramInt)
  {
    return native_delete_object(paramInt);
  }
  
  protected void finalize()
    throws Throwable
  {
    try
    {
      native_close();
      return;
    }
    finally
    {
      super.finalize();
    }
  }
  
  public int getDeviceId()
  {
    return this.mDevice.getDeviceId();
  }
  
  public MtpDeviceInfo getDeviceInfo()
  {
    return native_get_device_info();
  }
  
  public String getDeviceName()
  {
    return this.mDevice.getDeviceName();
  }
  
  public byte[] getObject(int paramInt1, int paramInt2)
  {
    Preconditions.checkArgumentNonnegative(paramInt2, "objectSize should not be negative");
    return native_get_object(paramInt1, paramInt2);
  }
  
  public int[] getObjectHandles(int paramInt1, int paramInt2, int paramInt3)
  {
    return native_get_object_handles(paramInt1, paramInt2, paramInt3);
  }
  
  public MtpObjectInfo getObjectInfo(int paramInt)
  {
    return native_get_object_info(paramInt);
  }
  
  public long getObjectSizeLong(int paramInt1, int paramInt2)
    throws IOException
  {
    return native_get_object_size_long(paramInt1, paramInt2);
  }
  
  public long getParent(int paramInt)
  {
    return native_get_parent(paramInt);
  }
  
  public long getPartialObject(int paramInt, long paramLong1, long paramLong2, byte[] paramArrayOfByte)
    throws IOException
  {
    return native_get_partial_object(paramInt, paramLong1, paramLong2, paramArrayOfByte);
  }
  
  public long getPartialObject64(int paramInt, long paramLong1, long paramLong2, byte[] paramArrayOfByte)
    throws IOException
  {
    return native_get_partial_object_64(paramInt, paramLong1, paramLong2, paramArrayOfByte);
  }
  
  public long getStorageId(int paramInt)
  {
    return native_get_storage_id(paramInt);
  }
  
  public int[] getStorageIds()
  {
    return native_get_storage_ids();
  }
  
  public MtpStorageInfo getStorageInfo(int paramInt)
  {
    return native_get_storage_info(paramInt);
  }
  
  public byte[] getThumbnail(int paramInt)
  {
    return native_get_thumbnail(paramInt);
  }
  
  public boolean importFile(int paramInt, ParcelFileDescriptor paramParcelFileDescriptor)
  {
    return native_import_file(paramInt, paramParcelFileDescriptor.getFd());
  }
  
  public boolean importFile(int paramInt, String paramString)
  {
    return native_import_file(paramInt, paramString);
  }
  
  public boolean open(UsbDeviceConnection paramUsbDeviceConnection)
  {
    boolean bool2 = false;
    Context localContext = paramUsbDeviceConnection.getContext();
    boolean bool1 = bool2;
    if (localContext != null)
    {
      bool1 = bool2;
      if (!((UserManager)localContext.getSystemService("user")).hasUserRestriction("no_usb_file_transfer")) {
        bool1 = native_open(this.mDevice.getDeviceName(), paramUsbDeviceConnection.getFileDescriptor());
      }
    }
    if (!bool1) {
      paramUsbDeviceConnection.close();
    }
    return bool1;
  }
  
  public MtpEvent readEvent(CancellationSignal paramCancellationSignal)
    throws IOException
  {
    boolean bool = false;
    final int i = native_submit_event_request();
    if (i >= 0) {
      bool = true;
    }
    Preconditions.checkState(bool, "Other thread is reading an event.");
    if (paramCancellationSignal != null) {
      paramCancellationSignal.setOnCancelListener(new CancellationSignal.OnCancelListener()
      {
        public void onCancel()
        {
          MtpDevice.-wrap0(MtpDevice.this, i);
        }
      });
    }
    try
    {
      MtpEvent localMtpEvent = native_reap_event_request(i);
      return localMtpEvent;
    }
    finally
    {
      if (paramCancellationSignal != null) {
        paramCancellationSignal.setOnCancelListener(null);
      }
    }
  }
  
  public boolean sendObject(int paramInt, long paramLong, ParcelFileDescriptor paramParcelFileDescriptor)
  {
    return native_send_object(paramInt, paramLong, paramParcelFileDescriptor.getFd());
  }
  
  public MtpObjectInfo sendObjectInfo(MtpObjectInfo paramMtpObjectInfo)
  {
    return native_send_object_info(paramMtpObjectInfo);
  }
  
  public String toString()
  {
    return this.mDevice.getDeviceName();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/mtp/MtpDevice.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */