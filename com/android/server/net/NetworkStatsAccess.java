package com.android.server.net;

import android.annotation.IntDef;
import android.app.AppOpsManager;
import android.app.admin.DevicePolicyManagerInternal;
import android.content.Context;
import android.os.UserHandle;
import android.telephony.TelephonyManager;
import com.android.server.LocalServices;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class NetworkStatsAccess
{
  public static int checkAccessLevel(Context paramContext, int paramInt, String paramString)
  {
    DevicePolicyManagerInternal localDevicePolicyManagerInternal = (DevicePolicyManagerInternal)LocalServices.getService(DevicePolicyManagerInternal.class);
    TelephonyManager localTelephonyManager = (TelephonyManager)paramContext.getSystemService("phone");
    int i;
    if (localTelephonyManager != null) {
      if (localTelephonyManager.checkCarrierPrivilegesForPackage(paramString) == 1)
      {
        i = 1;
        if (localDevicePolicyManagerInternal == null) {
          break label84;
        }
      }
    }
    label84:
    for (boolean bool = localDevicePolicyManagerInternal.isActiveAdminWithPolicy(paramInt, -2);; bool = false)
    {
      if ((i == 0) && (!bool) && (UserHandle.getAppId(paramInt) != 1000)) {
        break label90;
      }
      return 3;
      i = 0;
      break;
      i = 0;
      break;
    }
    label90:
    if ((hasAppOpsPermission(paramContext, paramInt, paramString)) || (paramContext.checkCallingOrSelfPermission("android.permission.READ_NETWORK_USAGE_HISTORY") == 0)) {
      return 2;
    }
    if (localDevicePolicyManagerInternal != null) {}
    for (bool = localDevicePolicyManagerInternal.isActiveAdminWithPolicy(paramInt, -1); bool; bool = false) {
      return 1;
    }
    return 0;
  }
  
  private static boolean hasAppOpsPermission(Context paramContext, int paramInt, String paramString)
  {
    if (paramString != null)
    {
      paramInt = ((AppOpsManager)paramContext.getSystemService("appops")).checkOp(43, paramInt, paramString);
      if (paramInt == 3) {
        return paramContext.checkCallingPermission("android.permission.PACKAGE_USAGE_STATS") == 0;
      }
      return paramInt == 0;
    }
    return false;
  }
  
  public static boolean isAccessibleToUser(int paramInt1, int paramInt2, int paramInt3)
  {
    switch (paramInt3)
    {
    default: 
      if (paramInt1 == paramInt2) {
        return true;
      }
      break;
    case 3: 
      return true;
    case 2: 
      if ((paramInt1 == 1000) || (paramInt1 == -4)) {}
      while ((paramInt1 == -5) || (paramInt1 == -1) || (UserHandle.getUserId(paramInt1) == UserHandle.getUserId(paramInt2))) {
        return true;
      }
      return false;
    case 1: 
      if ((paramInt1 == 1000) || (paramInt1 == -4)) {}
      while ((paramInt1 == -5) || (UserHandle.getUserId(paramInt1) == UserHandle.getUserId(paramInt2))) {
        return true;
      }
      return false;
    }
    return false;
  }
  
  @Retention(RetentionPolicy.SOURCE)
  @IntDef({0L, 1L, 2L, 3L})
  public static @interface Level
  {
    public static final int DEFAULT = 0;
    public static final int DEVICE = 3;
    public static final int DEVICESUMMARY = 2;
    public static final int USER = 1;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/net/NetworkStatsAccess.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */