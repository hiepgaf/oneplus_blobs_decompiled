package android.app;

import android.content.ComponentName;
import android.content.IIntentReceiver;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.content.res.CompatibilityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug.MemoryInfo;
import android.os.IBinder;
import android.os.IInterface;
import android.os.ParcelFileDescriptor;
import android.os.PersistableBundle;
import android.os.RemoteException;
import com.android.internal.app.IVoiceInteractor;
import com.android.internal.content.ReferrerIntent;
import java.io.FileDescriptor;
import java.util.List;
import java.util.Map;

public abstract interface IApplicationThread
  extends IInterface
{
  public static final int BACKGROUND_VISIBLE_BEHIND_CHANGED_TRANSACTION = 54;
  public static final int BACKUP_MODE_FULL = 1;
  public static final int BACKUP_MODE_INCREMENTAL = 0;
  public static final int BACKUP_MODE_RESTORE = 2;
  public static final int BACKUP_MODE_RESTORE_FULL = 3;
  public static final int BIND_APPLICATION_TRANSACTION = 13;
  public static final int CANCEL_VISIBLE_BEHIND_TRANSACTION = 53;
  public static final int CLEAR_DNS_CACHE_TRANSACTION = 38;
  public static final int DEBUG_OFF = 0;
  public static final int DEBUG_ON = 1;
  public static final int DEBUG_WAIT = 2;
  public static final int DISPATCH_PACKAGE_BROADCAST_TRANSACTION = 34;
  public static final int DUMP_ACTIVITY_TRANSACTION = 37;
  public static final int DUMP_DB_INFO_TRANSACTION = 46;
  public static final int DUMP_GFX_INFO_TRANSACTION = 44;
  public static final int DUMP_HEAP_TRANSACTION = 36;
  public static final int DUMP_MEM_INFO_TRANSACTION = 43;
  public static final int DUMP_PROVIDER_TRANSACTION = 45;
  public static final int DUMP_SERVICE_TRANSACTION = 22;
  public static final int ENTER_ANIMATION_COMPLETE_TRANSACTION = 55;
  public static final int EXTERNAL_STORAGE_UNAVAILABLE = 1;
  public static final int NOTIFY_CLEARTEXT_NETWORK_TRANSACTION = 56;
  public static final int PACKAGE_REMOVED = 0;
  public static final int PACKAGE_REMOVED_DONT_KILL = 2;
  public static final int PACKAGE_REPLACED = 3;
  public static final int PROCESS_IN_BACKGROUND_TRANSACTION = 19;
  public static final int PROFILER_CONTROL_TRANSACTION = 28;
  public static final int REQUEST_ASSIST_CONTEXT_EXTRAS_TRANSACTION = 48;
  public static final int SCHEDULE_ACTIVITY_CONFIGURATION_CHANGED_TRANSACTION = 25;
  public static final int SCHEDULE_BIND_SERVICE_TRANSACTION = 20;
  public static final int SCHEDULE_CONFIGURATION_CHANGED_TRANSACTION = 16;
  public static final int SCHEDULE_CRASH_TRANSACTION = 35;
  public static final int SCHEDULE_CREATE_BACKUP_AGENT_TRANSACTION = 30;
  public static final int SCHEDULE_CREATE_SERVICE_TRANSACTION = 11;
  public static final int SCHEDULE_DESTROY_BACKUP_AGENT_TRANSACTION = 31;
  public static final int SCHEDULE_EXIT_TRANSACTION = 14;
  public static final int SCHEDULE_FINISH_ACTIVITY_TRANSACTION = 9;
  public static final int SCHEDULE_INSTALL_PROVIDER_TRANSACTION = 51;
  public static final int SCHEDULE_LAUNCH_ACTIVITY_TRANSACTION = 7;
  public static final int SCHEDULE_LOCAL_VOICE_INTERACTION_STARTED_TRANSACTION = 61;
  public static final int SCHEDULE_LOW_MEMORY_TRANSACTION = 24;
  public static final int SCHEDULE_MULTI_WINDOW_CHANGED_TRANSACTION = 59;
  public static final int SCHEDULE_NEW_INTENT_TRANSACTION = 8;
  public static final int SCHEDULE_ON_NEW_ACTIVITY_OPTIONS_TRANSACTION = 32;
  public static final int SCHEDULE_PAUSE_ACTIVITY_TRANSACTION = 1;
  public static final int SCHEDULE_PICTURE_IN_PICTURE_CHANGED_TRANSACTION = 60;
  public static final int SCHEDULE_PRELOAD = 103;
  public static final int SCHEDULE_RECEIVER_TRANSACTION = 10;
  public static final int SCHEDULE_REGISTERED_RECEIVER_TRANSACTION = 23;
  public static final int SCHEDULE_RELAUNCH_ACTIVITY_TRANSACTION = 26;
  public static final int SCHEDULE_RESUME_ACTIVITY_TRANSACTION = 5;
  public static final int SCHEDULE_SEND_RESULT_TRANSACTION = 6;
  public static final int SCHEDULE_SERVICE_ARGS_TRANSACTION = 17;
  public static final int SCHEDULE_SLEEPING_TRANSACTION = 27;
  public static final int SCHEDULE_STOP_ACTIVITY_TRANSACTION = 3;
  public static final int SCHEDULE_STOP_SERVICE_TRANSACTION = 12;
  public static final int SCHEDULE_SUICIDE_TRANSACTION = 33;
  public static final int SCHEDULE_TRANSLUCENT_CONVERSION_COMPLETE_TRANSACTION = 49;
  public static final int SCHEDULE_TRIM_MEMORY_TRANSACTION = 42;
  public static final int SCHEDULE_UNBIND_SERVICE_TRANSACTION = 21;
  public static final int SCHEDULE_WINDOW_VISIBILITY_TRANSACTION = 4;
  public static final int SET_CORE_SETTINGS_TRANSACTION = 40;
  public static final int SET_HTTP_PROXY_TRANSACTION = 39;
  public static final int SET_PROCESS_STATE_TRANSACTION = 50;
  public static final int SET_SCHEDULING_GROUP_TRANSACTION = 29;
  public static final int START_BINDER_TRACKING_TRANSACTION = 57;
  public static final int STOP_BINDER_TRACKING_AND_DUMP_TRANSACTION = 58;
  public static final int UNSTABLE_PROVIDER_DIED_TRANSACTION = 47;
  public static final int UPDATE_PACKAGE_COMPATIBILITY_INFO_TRANSACTION = 41;
  public static final int UPDATE_TIME_PREFS_TRANSACTION = 52;
  public static final int UPDATE_TIME_ZONE_TRANSACTION = 18;
  public static final String descriptor = "android.app.IApplicationThread";
  
  public abstract void bindApplication(String paramString, ApplicationInfo paramApplicationInfo, List<ProviderInfo> paramList, ComponentName paramComponentName, ProfilerInfo paramProfilerInfo, Bundle paramBundle1, IInstrumentationWatcher paramIInstrumentationWatcher, IUiAutomationConnection paramIUiAutomationConnection, int paramInt, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, Configuration paramConfiguration, CompatibilityInfo paramCompatibilityInfo, Map<String, IBinder> paramMap, Bundle paramBundle2)
    throws RemoteException;
  
  public abstract void clearDnsCache()
    throws RemoteException;
  
  public abstract void dispatchPackageBroadcast(int paramInt, String[] paramArrayOfString)
    throws RemoteException;
  
  public abstract void dumpActivity(FileDescriptor paramFileDescriptor, IBinder paramIBinder, String paramString, String[] paramArrayOfString)
    throws RemoteException;
  
  public abstract void dumpDbInfo(FileDescriptor paramFileDescriptor, String[] paramArrayOfString)
    throws RemoteException;
  
  public abstract void dumpGfxInfo(FileDescriptor paramFileDescriptor, String[] paramArrayOfString)
    throws RemoteException;
  
  public abstract void dumpHeap(boolean paramBoolean, String paramString, ParcelFileDescriptor paramParcelFileDescriptor)
    throws RemoteException;
  
  public abstract void dumpMemInfo(FileDescriptor paramFileDescriptor, Debug.MemoryInfo paramMemoryInfo, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, boolean paramBoolean5, String[] paramArrayOfString)
    throws RemoteException;
  
  public abstract void dumpProvider(FileDescriptor paramFileDescriptor, IBinder paramIBinder, String[] paramArrayOfString)
    throws RemoteException;
  
  public abstract void dumpService(FileDescriptor paramFileDescriptor, IBinder paramIBinder, String[] paramArrayOfString)
    throws RemoteException;
  
  public abstract void notifyCleartextNetwork(byte[] paramArrayOfByte)
    throws RemoteException;
  
  public abstract void processInBackground()
    throws RemoteException;
  
  public abstract void profilerControl(boolean paramBoolean, ProfilerInfo paramProfilerInfo, int paramInt)
    throws RemoteException;
  
  public abstract void requestAssistContextExtras(IBinder paramIBinder1, IBinder paramIBinder2, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void scheduleActivityConfigurationChanged(IBinder paramIBinder, Configuration paramConfiguration, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void scheduleBackgroundVisibleBehindChanged(IBinder paramIBinder, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void scheduleBindService(IBinder paramIBinder, Intent paramIntent, boolean paramBoolean, int paramInt)
    throws RemoteException;
  
  public abstract void scheduleCancelVisibleBehind(IBinder paramIBinder)
    throws RemoteException;
  
  public abstract void scheduleConfigurationChanged(Configuration paramConfiguration)
    throws RemoteException;
  
  public abstract void scheduleCrash(String paramString)
    throws RemoteException;
  
  public abstract void scheduleCreateBackupAgent(ApplicationInfo paramApplicationInfo, CompatibilityInfo paramCompatibilityInfo, int paramInt)
    throws RemoteException;
  
  public abstract void scheduleCreateService(IBinder paramIBinder, ServiceInfo paramServiceInfo, CompatibilityInfo paramCompatibilityInfo, int paramInt)
    throws RemoteException;
  
  public abstract void scheduleDestroyActivity(IBinder paramIBinder, boolean paramBoolean, int paramInt)
    throws RemoteException;
  
  public abstract void scheduleDestroyBackupAgent(ApplicationInfo paramApplicationInfo, CompatibilityInfo paramCompatibilityInfo)
    throws RemoteException;
  
  public abstract void scheduleEnterAnimationComplete(IBinder paramIBinder)
    throws RemoteException;
  
  public abstract void scheduleExit()
    throws RemoteException;
  
  public abstract void scheduleInstallProvider(ProviderInfo paramProviderInfo)
    throws RemoteException;
  
  public abstract void scheduleLaunchActivity(Intent paramIntent, IBinder paramIBinder, int paramInt1, ActivityInfo paramActivityInfo, Configuration paramConfiguration1, Configuration paramConfiguration2, CompatibilityInfo paramCompatibilityInfo, String paramString, IVoiceInteractor paramIVoiceInteractor, int paramInt2, Bundle paramBundle, PersistableBundle paramPersistableBundle, List<ResultInfo> paramList, List<ReferrerIntent> paramList1, boolean paramBoolean1, boolean paramBoolean2, ProfilerInfo paramProfilerInfo)
    throws RemoteException;
  
  public abstract void scheduleLocalVoiceInteractionStarted(IBinder paramIBinder, IVoiceInteractor paramIVoiceInteractor)
    throws RemoteException;
  
  public abstract void scheduleLowMemory()
    throws RemoteException;
  
  public abstract void scheduleMultiWindowModeChanged(IBinder paramIBinder, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void scheduleNewIntent(List<ReferrerIntent> paramList, IBinder paramIBinder, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void scheduleOnNewActivityOptions(IBinder paramIBinder, ActivityOptions paramActivityOptions)
    throws RemoteException;
  
  public abstract void schedulePauseActivity(IBinder paramIBinder, boolean paramBoolean1, boolean paramBoolean2, int paramInt, boolean paramBoolean3)
    throws RemoteException;
  
  public abstract void schedulePictureInPictureModeChanged(IBinder paramIBinder, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void schedulePreload(ApplicationInfo paramApplicationInfo, CompatibilityInfo paramCompatibilityInfo, Configuration paramConfiguration, Map<String, IBinder> paramMap)
    throws RemoteException;
  
  public abstract void scheduleReceiver(Intent paramIntent, ActivityInfo paramActivityInfo, CompatibilityInfo paramCompatibilityInfo, int paramInt1, String paramString, Bundle paramBundle, boolean paramBoolean, int paramInt2, int paramInt3, int paramInt4)
    throws RemoteException;
  
  public abstract void scheduleRegisteredReceiver(IIntentReceiver paramIIntentReceiver, Intent paramIntent, int paramInt1, String paramString, Bundle paramBundle, boolean paramBoolean1, boolean paramBoolean2, int paramInt2, int paramInt3)
    throws RemoteException;
  
  public abstract void scheduleRelaunchActivity(IBinder paramIBinder, List<ResultInfo> paramList, List<ReferrerIntent> paramList1, int paramInt, boolean paramBoolean1, Configuration paramConfiguration1, Configuration paramConfiguration2, boolean paramBoolean2)
    throws RemoteException;
  
  public abstract void scheduleResumeActivity(IBinder paramIBinder, int paramInt, boolean paramBoolean, Bundle paramBundle)
    throws RemoteException;
  
  public abstract void scheduleSendResult(IBinder paramIBinder, List<ResultInfo> paramList)
    throws RemoteException;
  
  public abstract void scheduleServiceArgs(IBinder paramIBinder, boolean paramBoolean, int paramInt1, int paramInt2, Intent paramIntent)
    throws RemoteException;
  
  public abstract void scheduleSleeping(IBinder paramIBinder, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void scheduleStopActivity(IBinder paramIBinder, boolean paramBoolean, int paramInt)
    throws RemoteException;
  
  public abstract void scheduleStopService(IBinder paramIBinder)
    throws RemoteException;
  
  public abstract void scheduleSuicide()
    throws RemoteException;
  
  public abstract void scheduleTranslucentConversionComplete(IBinder paramIBinder, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void scheduleTrimMemory(int paramInt)
    throws RemoteException;
  
  public abstract void scheduleUnbindService(IBinder paramIBinder, Intent paramIntent)
    throws RemoteException;
  
  public abstract void scheduleWindowVisibility(IBinder paramIBinder, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setCoreSettings(Bundle paramBundle)
    throws RemoteException;
  
  public abstract void setHttpProxy(String paramString1, String paramString2, String paramString3, Uri paramUri)
    throws RemoteException;
  
  public abstract void setProcessState(int paramInt)
    throws RemoteException;
  
  public abstract void setSchedulingGroup(int paramInt)
    throws RemoteException;
  
  public abstract void startBinderTracking()
    throws RemoteException;
  
  public abstract void stopBinderTrackingAndDump(FileDescriptor paramFileDescriptor)
    throws RemoteException;
  
  public abstract void unstableProviderDied(IBinder paramIBinder)
    throws RemoteException;
  
  public abstract void updatePackageCompatibilityInfo(String paramString, CompatibilityInfo paramCompatibilityInfo)
    throws RemoteException;
  
  public abstract void updateTimePrefs(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void updateTimeZone()
    throws RemoteException;
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/IApplicationThread.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */