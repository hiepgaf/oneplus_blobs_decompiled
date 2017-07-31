package android.app.usage;

import android.content.ComponentName;
import android.content.res.Configuration;

public abstract class UsageStatsManagerInternal
{
  public abstract void addAppIdleStateChangeListener(AppIdleStateChangeListener paramAppIdleStateChangeListener);
  
  public abstract void applyRestoredPayload(int paramInt, String paramString, byte[] paramArrayOfByte);
  
  public abstract byte[] getBackupPayload(int paramInt, String paramString);
  
  public abstract int[] getIdleUidsForUser(int paramInt);
  
  public abstract boolean isAppIdle(String paramString, int paramInt1, int paramInt2);
  
  public abstract boolean isAppIdleParoleOn();
  
  public abstract void prepareShutdown();
  
  public abstract void removeAppIdleStateChangeListener(AppIdleStateChangeListener paramAppIdleStateChangeListener);
  
  public abstract void reportConfigurationChange(Configuration paramConfiguration, int paramInt);
  
  public abstract void reportContentProviderUsage(String paramString1, String paramString2, int paramInt);
  
  public abstract void reportEvent(ComponentName paramComponentName, int paramInt1, int paramInt2);
  
  public abstract void reportEvent(String paramString, int paramInt1, int paramInt2);
  
  public abstract void reportShortcutUsage(String paramString1, String paramString2, int paramInt);
  
  public static abstract class AppIdleStateChangeListener
  {
    public abstract void onAppIdleStateChanged(String paramString, int paramInt, boolean paramBoolean);
    
    public abstract void onParoleStateChanged(boolean paramBoolean);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/usage/UsageStatsManagerInternal.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */