package com.android.server.display;

import android.content.Context;
import android.hardware.display.ISDService.Stub;
import android.util.Slog;

public final class SDService
  extends ISDService.Stub
{
  private static String TAG = "SDService";
  private Context mContext;
  private final long mNativeObject;
  
  public SDService(Context paramContext)
  {
    this.mContext = paramContext;
    this.mNativeObject = nativeCreate();
  }
  
  private static native long nativeCreate();
  
  private static native void nativeDestory(long paramLong);
  
  private static native void nativeEnableColorBalance(int paramInt);
  
  private static native void nativeEnableMode(int paramInt);
  
  private static native void nativeSetUsrColorBalanceConfig(double paramDouble1, double paramDouble2, double paramDouble3);
  
  private static native void nativeSetUsrSharpness(long paramLong, int paramInt);
  
  public void SetUsrColorBalanceConfig(double paramDouble1, double paramDouble2, double paramDouble3)
  {
    nativeSetUsrColorBalanceConfig(paramDouble1, paramDouble2, paramDouble3);
  }
  
  public void SetUsrSharpness(int paramInt)
  {
    nativeSetUsrSharpness(this.mNativeObject, paramInt);
  }
  
  public void enableColorBalance(int paramInt)
  {
    nativeEnableColorBalance(paramInt);
  }
  
  public void enableMode(int paramInt)
  {
    nativeEnableMode(paramInt);
  }
  
  protected void finalize()
    throws Throwable
  {
    try
    {
      if (this.mNativeObject != 0L) {
        nativeDestory(this.mNativeObject);
      }
      return;
    }
    finally
    {
      super.finalize();
    }
  }
  
  public void systemRunning()
  {
    Slog.d(TAG, "[systemRunning]");
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/display/SDService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */