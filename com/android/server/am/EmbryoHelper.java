package com.android.server.am;

import android.app.AppGlobals;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.ParceledListSlice;
import android.content.res.CompatibilityInfo;
import android.content.res.Configuration;
import android.os.IBinder;
import android.os.UserHandle;
import android.util.Log;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

class EmbryoHelper
{
  private static final String TAG = "EmbryoHelper";
  private static EmbryoHelper sInstance;
  private ActivityManagerService mAms;
  private Method mCompInfoForPackageMethod = null;
  private Method mGetCommonServicesMethod = null;
  private IPackageManager mPms;
  private Method mStartProcessMethod = null;
  
  EmbryoHelper(ActivityManagerService paramActivityManagerService)
  {
    this.mAms = paramActivityManagerService;
    sInstance = this;
  }
  
  public static EmbryoHelper getInstance()
  {
    return sInstance;
  }
  
  public boolean checkIfNewPackageIsLaunchable(ApplicationInfo paramApplicationInfo)
  {
    return checkIfPackageIsLaunchable(paramApplicationInfo.packageName);
  }
  
  public boolean checkIfPackageIsLaunchable(String paramString)
  {
    if (this.mPms == null) {
      this.mPms = AppGlobals.getPackageManager();
    }
    if (this.mPms == null)
    {
      Log.d("EmbryoHelper", "PM not ready.");
      return false;
    }
    Object localObject = new Intent("android.intent.action.MAIN", null);
    ((Intent)localObject).addCategory("android.intent.category.LAUNCHER");
    ((Intent)localObject).setPackage(paramString);
    try
    {
      localObject = this.mPms.queryIntentActivities((Intent)localObject, null, 0, UserHandle.getCallingUserId());
      if ((localObject != null) && (((ParceledListSlice)localObject).getList() != null))
      {
        int i = ((ParceledListSlice)localObject).getList().size();
        if (i >= 1) {
          return true;
        }
      }
      return false;
    }
    catch (Exception localException)
    {
      Log.d("EmbryoHelper", "checkIfPackageIsLaunchable failed. " + paramString, localException);
    }
    return false;
  }
  
  public boolean checkIfProcessExist(ApplicationInfo paramApplicationInfo)
  {
    boolean bool = true;
    synchronized (this.mAms)
    {
      ActivityManagerService.boostPriorityForLockedSection();
      paramApplicationInfo = this.mAms.getProcessRecordLocked(paramApplicationInfo.processName, paramApplicationInfo.uid, true);
      if (paramApplicationInfo != null)
      {
        ActivityManagerService.resetPriorityAfterLockedSection();
        return bool;
      }
      bool = false;
    }
  }
  
  public CompatibilityInfo compatibilityInfoForPackageLocked(ApplicationInfo paramApplicationInfo)
  {
    try
    {
      synchronized (this.mAms)
      {
        ActivityManagerService.boostPriorityForLockedSection();
        paramApplicationInfo = (CompatibilityInfo)this.mCompInfoForPackageMethod.invoke(this.mAms, new Object[] { paramApplicationInfo });
        ActivityManagerService.resetPriorityAfterLockedSection();
        return paramApplicationInfo;
      }
      return null;
    }
    catch (Exception paramApplicationInfo)
    {
      Log.d("EmbryoHelper", "compatibilityInfoForPackageLocked failed", paramApplicationInfo);
    }
  }
  
  public ApplicationInfo getApplicationInfo(String paramString, int paramInt1, int paramInt2)
  {
    if (this.mPms == null) {
      this.mPms = AppGlobals.getPackageManager();
    }
    if (this.mPms == null)
    {
      Log.d("EmbryoHelper", "PM not ready.");
      return null;
    }
    try
    {
      ApplicationInfo localApplicationInfo = this.mPms.getApplicationInfo(paramString, paramInt1, paramInt2);
      return localApplicationInfo;
    }
    catch (Exception localException)
    {
      Log.d("EmbryoHelper", "getApplicationInfo failed. " + paramString, localException);
    }
    return null;
  }
  
  public HashMap<String, IBinder> getCommonServicesLocked(boolean paramBoolean)
  {
    try
    {
      synchronized (this.mAms)
      {
        ActivityManagerService.boostPriorityForLockedSection();
        HashMap localHashMap = (HashMap)this.mGetCommonServicesMethod.invoke(this.mAms, new Object[] { Boolean.valueOf(paramBoolean) });
        ActivityManagerService.resetPriorityAfterLockedSection();
        return localHashMap;
      }
      return null;
    }
    catch (Exception localException)
    {
      Log.d("EmbryoHelper", "getCommonServicesLocked failed", localException);
    }
  }
  
  public Configuration getConfiguration()
  {
    synchronized (this.mAms)
    {
      ActivityManagerService.boostPriorityForLockedSection();
      Configuration localConfiguration = new Configuration(this.mAms.mConfiguration);
      ActivityManagerService.resetPriorityAfterLockedSection();
      return localConfiguration;
    }
  }
  
  boolean initEnvironment()
  {
    try
    {
      this.mStartProcessMethod = this.mAms.getClass().getDeclaredMethod("startProcessLocked", new Class[] { ProcessRecord.class, String.class, String.class });
      if (this.mStartProcessMethod == null)
      {
        Log.d("EmbryoHelper", "Embryo initEnvironment failed. step 1");
        return false;
      }
      this.mStartProcessMethod.setAccessible(true);
      this.mGetCommonServicesMethod = this.mAms.getClass().getDeclaredMethod("getCommonServicesLocked", new Class[] { Boolean.TYPE });
      if (this.mGetCommonServicesMethod == null)
      {
        Log.d("EmbryoHelper", "Embryo initEnvironment failed. step 2");
        return false;
      }
      this.mGetCommonServicesMethod.setAccessible(true);
      this.mCompInfoForPackageMethod = this.mAms.getClass().getDeclaredMethod("compatibilityInfoForPackageLocked", new Class[] { ApplicationInfo.class });
      if (this.mCompInfoForPackageMethod == null)
      {
        Log.d("EmbryoHelper", "Embryo initEnvironment failed. step 3");
        return false;
      }
      return true;
    }
    catch (Exception localException)
    {
      Log.d("EmbryoHelper", "Embryo initEnvironment failed. final", localException);
    }
    return false;
  }
  
  public boolean isPackageAvailable(String paramString, int paramInt)
  {
    if (this.mPms == null) {
      this.mPms = AppGlobals.getPackageManager();
    }
    if (this.mPms == null)
    {
      Log.d("EmbryoHelper", "PM not ready.");
      return false;
    }
    try
    {
      boolean bool = this.mPms.isPackageAvailable(paramString, paramInt);
      return bool;
    }
    catch (Exception localException)
    {
      Log.d("EmbryoHelper", "isPackageAvailable failed. " + paramString, localException);
    }
    return false;
  }
  
  public void startProcessLocked(ProcessRecord paramProcessRecord, String paramString1, String paramString2)
  {
    try
    {
      synchronized (this.mAms)
      {
        ActivityManagerService.boostPriorityForLockedSection();
        this.mStartProcessMethod.invoke(this.mAms, new Object[] { paramProcessRecord, paramString1, paramString2 });
        ActivityManagerService.resetPriorityAfterLockedSection();
        return;
      }
      return;
    }
    catch (Exception paramProcessRecord)
    {
      Log.d("EmbryoHelper", "startProcessLocked failed", paramProcessRecord);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/am/EmbryoHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */