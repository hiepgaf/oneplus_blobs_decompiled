package com.android.server;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Binder;
import android.os.CpuUsageInfo;
import android.os.IHardwarePropertiesManager.Stub;
import android.os.UserHandle;
import com.android.server.vr.VrManagerInternal;

public class HardwarePropertiesManagerService
  extends IHardwarePropertiesManager.Stub
{
  private final Context mContext;
  private final Object mLock = new Object();
  
  public HardwarePropertiesManagerService(Context arg1)
  {
    this.mContext = ???;
    synchronized (this.mLock)
    {
      nativeInit();
      return;
    }
  }
  
  private void enforceHardwarePropertiesRetrievalAllowed(String paramString)
    throws SecurityException
  {
    Object localObject = this.mContext.getPackageManager();
    try
    {
      i = ((PackageManager)localObject).getPackageUid(paramString, 0);
      if (Binder.getCallingUid() != i) {
        throw new SecurityException("The caller has faked the package name.");
      }
    }
    catch (PackageManager.NameNotFoundException paramString)
    {
      throw new SecurityException("The caller has faked the package name.");
    }
    int i = UserHandle.getUserId(i);
    localObject = (VrManagerInternal)LocalServices.getService(VrManagerInternal.class);
    DevicePolicyManager localDevicePolicyManager = (DevicePolicyManager)this.mContext.getSystemService(DevicePolicyManager.class);
    if ((localDevicePolicyManager.isDeviceOwnerApp(paramString)) || (localDevicePolicyManager.isProfileOwnerApp(paramString))) {}
    while (((VrManagerInternal)localObject).isCurrentVrListener(paramString, i)) {
      return;
    }
    throw new SecurityException("The caller is not a device or profile owner or bound VrListenerService.");
  }
  
  private static native CpuUsageInfo[] nativeGetCpuUsages();
  
  private static native float[] nativeGetDeviceTemperatures(int paramInt1, int paramInt2);
  
  private static native float[] nativeGetFanSpeeds();
  
  private static native void nativeInit();
  
  public CpuUsageInfo[] getCpuUsages(String arg1)
    throws SecurityException
  {
    enforceHardwarePropertiesRetrievalAllowed(???);
    synchronized (this.mLock)
    {
      CpuUsageInfo[] arrayOfCpuUsageInfo = nativeGetCpuUsages();
      return arrayOfCpuUsageInfo;
    }
  }
  
  public float[] getDeviceTemperatures(String arg1, int paramInt1, int paramInt2)
    throws SecurityException
  {
    enforceHardwarePropertiesRetrievalAllowed(???);
    synchronized (this.mLock)
    {
      float[] arrayOfFloat = nativeGetDeviceTemperatures(paramInt1, paramInt2);
      return arrayOfFloat;
    }
  }
  
  public float[] getFanSpeeds(String arg1)
    throws SecurityException
  {
    enforceHardwarePropertiesRetrievalAllowed(???);
    synchronized (this.mLock)
    {
      float[] arrayOfFloat = nativeGetFanSpeeds();
      return arrayOfFloat;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/HardwarePropertiesManagerService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */