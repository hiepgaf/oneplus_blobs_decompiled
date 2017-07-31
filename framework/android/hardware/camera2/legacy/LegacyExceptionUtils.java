package android.hardware.camera2.legacy;

import android.os.ServiceSpecificException;
import android.system.OsConstants;
import android.util.AndroidException;

public class LegacyExceptionUtils
{
  public static final int ALREADY_EXISTS = -OsConstants.EEXIST;
  public static final int BAD_VALUE = -OsConstants.EINVAL;
  public static final int DEAD_OBJECT = -OsConstants.ENOSYS;
  public static final int INVALID_OPERATION = -OsConstants.EPIPE;
  public static final int NO_ERROR = 0;
  public static final int PERMISSION_DENIED = -OsConstants.EPERM;
  private static final String TAG = "LegacyExceptionUtils";
  public static final int TIMED_OUT = -OsConstants.ETIMEDOUT;
  
  private LegacyExceptionUtils()
  {
    throw new AssertionError();
  }
  
  public static int throwOnError(int paramInt)
    throws LegacyExceptionUtils.BufferQueueAbandonedException
  {
    if (paramInt == 0) {
      return 0;
    }
    if (paramInt == -OsConstants.ENODEV) {
      throw new BufferQueueAbandonedException();
    }
    if (paramInt < 0) {
      throw new UnsupportedOperationException("Unknown error " + paramInt);
    }
    return paramInt;
  }
  
  public static void throwOnServiceError(int paramInt)
  {
    if (paramInt >= 0) {
      return;
    }
    String str;
    if (paramInt == PERMISSION_DENIED)
    {
      paramInt = 1;
      str = "Lacking privileges to access camera service";
    }
    for (;;)
    {
      throw new ServiceSpecificException(paramInt, str);
      if (paramInt == ALREADY_EXISTS) {
        return;
      }
      if (paramInt == BAD_VALUE)
      {
        paramInt = 3;
        str = "Bad argument passed to camera service";
      }
      else if (paramInt == DEAD_OBJECT)
      {
        paramInt = 4;
        str = "Camera service not available";
      }
      else if (paramInt == TIMED_OUT)
      {
        paramInt = 10;
        str = "Operation timed out in camera service";
      }
      else if (paramInt == -OsConstants.EACCES)
      {
        paramInt = 6;
        str = "Camera disabled by policy";
      }
      else if (paramInt == -OsConstants.EBUSY)
      {
        paramInt = 7;
        str = "Camera already in use";
      }
      else if (paramInt == -OsConstants.EUSERS)
      {
        paramInt = 8;
        str = "Maximum number of cameras in use";
      }
      else if (paramInt == -OsConstants.ENODEV)
      {
        paramInt = 4;
        str = "Camera device not available";
      }
      else if (paramInt == -OsConstants.EOPNOTSUPP)
      {
        paramInt = 9;
        str = "Deprecated camera HAL does not support this";
      }
      else if (paramInt == INVALID_OPERATION)
      {
        paramInt = 10;
        str = "Illegal state encountered in camera service.";
      }
      else
      {
        int i = 10;
        str = "Unknown camera device error " + paramInt;
        paramInt = i;
      }
    }
  }
  
  public static class BufferQueueAbandonedException
    extends AndroidException
  {
    public BufferQueueAbandonedException() {}
    
    public BufferQueueAbandonedException(Exception paramException)
    {
      super();
    }
    
    public BufferQueueAbandonedException(String paramString)
    {
      super();
    }
    
    public BufferQueueAbandonedException(String paramString, Throwable paramThrowable)
    {
      super(paramThrowable);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/legacy/LegacyExceptionUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */