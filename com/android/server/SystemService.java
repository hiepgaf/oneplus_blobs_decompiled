package com.android.server;

import android.content.Context;
import android.os.IBinder;
import android.os.ServiceManager;

public abstract class SystemService
{
  public static final int PHASE_ACTIVITY_MANAGER_READY = 550;
  public static final int PHASE_BOOT_COMPLETED = 1000;
  public static final int PHASE_LOCK_SETTINGS_READY = 480;
  public static final int PHASE_SYSTEM_SERVICES_READY = 500;
  public static final int PHASE_THIRD_PARTY_APPS_CAN_START = 600;
  public static final int PHASE_WAIT_FOR_DEFAULT_DISPLAY = 100;
  private final Context mContext;
  
  public SystemService(Context paramContext)
  {
    this.mContext = paramContext;
  }
  
  private SystemServiceManager getManager()
  {
    return (SystemServiceManager)LocalServices.getService(SystemServiceManager.class);
  }
  
  protected final IBinder getBinderService(String paramString)
  {
    return ServiceManager.getService(paramString);
  }
  
  public final Context getContext()
  {
    return this.mContext;
  }
  
  protected final <T> T getLocalService(Class<T> paramClass)
  {
    return (T)LocalServices.getService(paramClass);
  }
  
  public final boolean isSafeMode()
  {
    return getManager().isSafeMode();
  }
  
  public void onBootPhase(int paramInt) {}
  
  public void onCleanupUser(int paramInt) {}
  
  public abstract void onStart();
  
  public void onStartUser(int paramInt) {}
  
  public void onStopUser(int paramInt) {}
  
  public void onSwitchUser(int paramInt) {}
  
  public void onUnlockUser(int paramInt) {}
  
  protected final void publishBinderService(String paramString, IBinder paramIBinder)
  {
    publishBinderService(paramString, paramIBinder, false);
  }
  
  protected final void publishBinderService(String paramString, IBinder paramIBinder, boolean paramBoolean)
  {
    ServiceManager.addService(paramString, paramIBinder, paramBoolean);
  }
  
  protected final <T> void publishLocalService(Class<T> paramClass, T paramT)
  {
    LocalServices.addService(paramClass, paramT);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/SystemService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */