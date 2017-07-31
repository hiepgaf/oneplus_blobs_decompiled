package android.hardware.camera2;

import android.util.AndroidException;

public class CameraAccessException
  extends AndroidException
{
  public static final int CAMERA_DEPRECATED_HAL = 1000;
  public static final int CAMERA_DISABLED = 1;
  public static final int CAMERA_DISCONNECTED = 2;
  public static final int CAMERA_ERROR = 3;
  public static final int CAMERA_IN_USE = 4;
  public static final int MAX_CAMERAS_IN_USE = 5;
  private static final long serialVersionUID = 5630338637471475675L;
  private final int mReason;
  
  public CameraAccessException(int paramInt)
  {
    super(getDefaultMessage(paramInt));
    this.mReason = paramInt;
  }
  
  public CameraAccessException(int paramInt, String paramString)
  {
    super(getCombinedMessage(paramInt, paramString));
    this.mReason = paramInt;
  }
  
  public CameraAccessException(int paramInt, String paramString, Throwable paramThrowable)
  {
    super(getCombinedMessage(paramInt, paramString), paramThrowable);
    this.mReason = paramInt;
  }
  
  public CameraAccessException(int paramInt, Throwable paramThrowable)
  {
    super(getDefaultMessage(paramInt), paramThrowable);
    this.mReason = paramInt;
  }
  
  private static String getCombinedMessage(int paramInt, String paramString)
  {
    return String.format("%s (%d): %s", new Object[] { getProblemString(paramInt), Integer.valueOf(paramInt), paramString });
  }
  
  public static String getDefaultMessage(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return null;
    case 4: 
      return "The camera device is in use already";
    case 5: 
      return "The system-wide limit for number of open cameras has been reached, and more camera devices cannot be opened until previous instances are closed.";
    case 2: 
      return "The camera device is removable and has been disconnected from the Android device, or the camera service has shut down the connection due to a higher-priority access request for the camera device.";
    case 1: 
      return "The camera is disabled due to a device policy, and cannot be opened.";
    }
    return "The camera device is currently in the error state; no further calls to it will succeed.";
  }
  
  private static String getProblemString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return "<UNKNOWN ERROR>";
    case 4: 
      return "CAMERA_IN_USE";
    case 5: 
      return "MAX_CAMERAS_IN_USE";
    case 2: 
      return "CAMERA_DISCONNECTED";
    case 1: 
      return "CAMERA_DISABLED";
    case 3: 
      return "CAMERA_ERROR";
    }
    return "CAMERA_DEPRECATED_HAL";
  }
  
  public final int getReason()
  {
    return this.mReason;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/CameraAccessException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */