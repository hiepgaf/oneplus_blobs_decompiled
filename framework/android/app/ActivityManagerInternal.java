package android.app;

import android.content.ComponentName;
import android.content.IIntentSender;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
import android.service.voice.IVoiceInteractionSession;
import com.android.internal.app.IVoiceInteractor;
import java.util.List;

public abstract class ActivityManagerInternal
{
  public static final int APP_TRANSITION_SAVED_SURFACE = 0;
  public static final int APP_TRANSITION_STARTING_WINDOW = 1;
  public static final int APP_TRANSITION_TIMEOUT = 3;
  public static final int APP_TRANSITION_WINDOWS_DRAWN = 2;
  
  public abstract SleepToken acquireSleepToken(String paramString);
  
  public abstract void addAppLockerPassedPackage(String paramString);
  
  public abstract String checkContentProviderAccess(String paramString, int paramInt);
  
  public abstract ComponentName getHomeActivityForUser(int paramInt);
  
  public abstract List<IBinder> getTopVisibleActivities();
  
  public abstract int getUidProcessState(int paramInt);
  
  public abstract void killForegroundAppsForUser(int paramInt);
  
  public abstract void notifyAppTransitionCancelled();
  
  public abstract void notifyAppTransitionFinished();
  
  public abstract void notifyAppTransitionStarting(int paramInt);
  
  public abstract void notifyDockedStackMinimizedChanged(boolean paramBoolean);
  
  public abstract void notifyStartingWindowDrawn();
  
  public abstract void onLocalVoiceInteractionStarted(IBinder paramIBinder, IVoiceInteractionSession paramIVoiceInteractionSession, IVoiceInteractor paramIVoiceInteractor);
  
  public abstract void onUserRemoved(int paramInt);
  
  public abstract void onWakefulnessChanged(int paramInt);
  
  public abstract void setPendingIntentWhitelistDuration(IIntentSender paramIIntentSender, long paramLong);
  
  public abstract int startActivitiesAsPackage(String paramString, int paramInt, Intent[] paramArrayOfIntent, Bundle paramBundle);
  
  public abstract int startIsolatedProcess(String paramString1, String[] paramArrayOfString, String paramString2, String paramString3, int paramInt, Runnable paramRunnable);
  
  public abstract void updatePersistentConfigurationForUser(Configuration paramConfiguration, int paramInt);
  
  public static abstract class SleepToken
  {
    public abstract void release();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/ActivityManagerInternal.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */