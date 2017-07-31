package com.oneplus.sdk.utils;

import android.os.SystemProperties;
import android.util.BoostFramework;
import android.util.Log;

public class OpBoostFramework
{
  private static final boolean DBG = true;
  public static final int MAX_ACQUIRE_DURATION = 2000;
  public static final int MIN_ACQUIRE_DURATION = 0;
  public static final int REQUEST_FAILED_EXCEPTION = -4;
  public static final int REQUEST_FAILED_INVALID_DURATION = -2;
  public static final int REQUEST_FAILED_NATIVE = -1;
  public static final int REQUEST_FAILED_UNKNOWN_POLICY = -3;
  public static final int REQUEST_POLICY_PERFORMANCE = 0;
  public static final int REQUEST_SUCCEEDED = 0;
  private static final String TAG = "OpBoostFramework";
  private static BoostFramework sPerfBoostInstance = null;
  private static String sProjectName = SystemProperties.get("ro.boot.project_name");
  private static String sProjectName_old = SystemProperties.get("ro.prj_name");
  
  public OpBoostFramework()
  {
    if (sPerfBoostInstance == null) {
      sPerfBoostInstance = new BoostFramework();
    }
    MyLog.-wrap1("OpBoostFramework", "OpBoostFramework() : sPerfBoostInstance = " + sPerfBoostInstance);
  }
  
  public int acquireBoostFor(int paramInt1, int paramInt2)
  {
    int i = 0;
    String str;
    if (sProjectName_old.length() != 0)
    {
      str = sProjectName_old;
      MyLog.-wrap1("OpBoostFramework", "acquireBoostFor() : policy = " + paramInt1);
      MyLog.-wrap1("OpBoostFramework", "acquireBoostFor() : duration = " + paramInt2);
      MyLog.-wrap1("OpBoostFramework", "projectName = " + str);
      switch (paramInt1)
      {
      default: 
        paramInt1 = -3;
      }
    }
    for (;;)
    {
      MyLog.-wrap1("OpBoostFramework", "acquireBoostFor++++++() : ret = " + paramInt1);
      return paramInt1;
      str = sProjectName;
      break;
      if ((paramInt2 > 2000) || (paramInt2 < 0))
      {
        paramInt1 = -2;
      }
      else
      {
        try
        {
          if (!"14049".equals(str)) {
            break label251;
          }
          sPerfBoostInstance.perfLockAcquire(paramInt2, new int[] { 7681, 525, 19716, 7954 });
          paramInt1 = i;
        }
        catch (Exception localException)
        {
          Log.e("OpBoostFramework", "Exception " + localException);
          localException.printStackTrace();
          paramInt1 = -4;
        }
        continue;
        label251:
        if ("15801".equals(localException))
        {
          sPerfBoostInstance.perfLockAcquire(paramInt2, new int[] { 1082130432, 2100, 1082130688, 1600 });
          paramInt1 = i;
        }
        else if ("15811".equals(localException))
        {
          sPerfBoostInstance.perfLockAcquire(paramInt2, new int[] { 1082130432, 2400, 1082130688, 1600 });
          paramInt1 = i;
        }
        else if ("16859".equals(localException))
        {
          sPerfBoostInstance.perfLockAcquire(paramInt2, new int[] { 1082130432, 4094, 1082130688, 4094 });
          paramInt1 = i;
        }
        else
        {
          MyLog.-wrap0("OpBoostFramework", "Not suppoert perfLock");
          paramInt1 = i;
        }
      }
    }
  }
  
  public int releaseBoost()
  {
    String str;
    if (sProjectName_old.length() != 0) {
      str = sProjectName_old;
    }
    for (;;)
    {
      MyLog.-wrap1("OpBoostFramework", "projectName = " + str);
      try
      {
        if (("14049".equals(str)) || ("15801".equals(str)) || ("15811".equals(str)) || ("16859".equals(str)))
        {
          int i = sPerfBoostInstance.perfLockRelease();
          return i;
          str = sProjectName;
        }
        else
        {
          MyLog.-wrap0("OpBoostFramework", "Not suppoert perfLock");
          return 0;
        }
      }
      catch (Exception localException)
      {
        Log.e("OpBoostFramework", "Exception " + localException);
        localException.printStackTrace();
      }
    }
    return -4;
  }
  
  private static class MyLog
  {
    private static void d(String paramString1, String paramString2)
    {
      Log.d(paramString1, paramString2);
    }
    
    private static void e(String paramString1, String paramString2)
    {
      Log.e(paramString1, paramString2);
    }
    
    private static void i(String paramString1, String paramString2)
    {
      Log.i(paramString1, paramString2);
    }
    
    private static void v(String paramString1, String paramString2)
    {
      Log.v(paramString1, paramString2);
    }
    
    private static void w(String paramString1, String paramString2)
    {
      Log.w(paramString1, paramString2);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/sdk/utils/OpBoostFramework.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */