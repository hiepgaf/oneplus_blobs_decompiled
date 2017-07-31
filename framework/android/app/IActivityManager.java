package android.app;

import android.app.assist.AssistContent;
import android.app.assist.AssistStructure;
import android.content.ComponentName;
import android.content.ContentProviderNative;
import android.content.IContentProvider;
import android.content.IIntentReceiver;
import android.content.IIntentSender;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.UriPermission;
import android.content.pm.ApplicationInfo;
import android.content.pm.ConfigurationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.ParceledListSlice;
import android.content.pm.ProviderInfo;
import android.content.pm.UserInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug.MemoryInfo;
import android.os.IBinder;
import android.os.IInterface;
import android.os.IProgressListener;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.PersistableBundle;
import android.os.RemoteException;
import android.os.StrictMode.ViolationInfo;
import android.service.voice.IVoiceInteractionSession;
import com.android.internal.app.IVoiceInteractor;
import com.android.internal.os.IResultReceiver;
import java.util.List;

public abstract interface IActivityManager
  extends IInterface
{
  public static final int ACTIVITY_DESTROYED_TRANSACTION = 62;
  public static final int ACTIVITY_IDLE_TRANSACTION = 18;
  public static final int ACTIVITY_PAUSED_TRANSACTION = 19;
  public static final int ACTIVITY_RELAUNCHED_TRANSACTION = 357;
  public static final int ACTIVITY_RESUMED_TRANSACTION = 39;
  public static final int ACTIVITY_SLEPT_TRANSACTION = 123;
  public static final int ACTIVITY_STOPPED_TRANSACTION = 20;
  public static final int ADD_APP_TASK_TRANSACTION = 234;
  public static final int ADD_PACKAGE_DEPENDENCY_TRANSACTION = 95;
  public static final int APP_NOT_RESPONDING_VIA_PROVIDER_TRANSACTION = 183;
  public static final int ATTACH_APPLICATION_TRANSACTION = 17;
  public static final int BACKGROUND_RESOURCES_RELEASED_TRANSACTION = 228;
  public static final int BACKUP_AGENT_CREATED_TRANSACTION = 91;
  public static final int BIND_SERVICE_TRANSACTION = 36;
  public static final int BOOT_ANIMATION_COMPLETE_TRANSACTION = 238;
  public static final int BROADCAST_INTENT_TRANSACTION = 14;
  public static final int CANCEL_INTENT_SENDER_TRANSACTION = 64;
  public static final int CAN_BYPASS_WORK_CHALLENGE = 381;
  public static final int CHECK_GRANT_URI_PERMISSION_TRANSACTION = 119;
  public static final int CHECK_PERMISSION_TRANSACTION = 53;
  public static final int CHECK_PERMISSION_WITH_TOKEN_TRANSACTION = 242;
  public static final int CHECK_URI_PERMISSION_TRANSACTION = 54;
  public static final int CLEAR_APP_DATA_TRANSACTION = 78;
  public static final int CLEAR_GRANTED_URI_PERMISSIONS_TRANSACTION = 362;
  public static final int CLEAR_PENDING_BACKUP_TRANSACTION = 160;
  public static final int CLOSE_SYSTEM_DIALOGS_TRANSACTION = 97;
  public static final int CONVERT_FROM_TRANSLUCENT_TRANSACTION = 174;
  public static final int CONVERT_TO_TRANSLUCENT_TRANSACTION = 175;
  public static final int CRASH_APPLICATION_TRANSACTION = 114;
  public static final int CREATE_STACK_ON_DISPLAY = 282;
  public static final int CREATE_VIRTUAL_ACTIVITY_CONTAINER_TRANSACTION = 168;
  public static final int DELETE_ACTIVITY_CONTAINER_TRANSACTION = 186;
  public static final int DUMP_HEAP_FINISHED_TRANSACTION = 289;
  public static final int DUMP_HEAP_TRANSACTION = 120;
  public static final int ENTER_PICTURE_IN_PICTURE_TRANSACTION = 356;
  public static final int ENTER_SAFE_MODE_TRANSACTION = 66;
  public static final int EXIT_FREEFORM_MODE_TRANSACTION = 345;
  public static final int FINISH_ACTIVITY_AFFINITY_TRANSACTION = 149;
  public static final int FINISH_ACTIVITY_TRANSACTION = 11;
  public static final int FINISH_HEAVY_WEIGHT_APP_TRANSACTION = 109;
  public static final int FINISH_INSTRUMENTATION_TRANSACTION = 45;
  public static final int FINISH_NOT_ORDER_RECEIVER_TRANSACTION = 391;
  public static final int FINISH_RECEIVER_TRANSACTION = 16;
  public static final int FINISH_SUB_ACTIVITY_TRANSACTION = 32;
  public static final int FINISH_VOICE_TASK_TRANSACTION = 224;
  public static final int FORCE_STOP_PACKAGE_TRANSACTION = 79;
  public static final int GET_ACTIVITY_CLASS_FOR_TOKEN_TRANSACTION = 49;
  public static final int GET_ACTIVITY_DISPLAY_ID_TRANSACTION = 185;
  public static final int GET_ACTIVITY_OPTIONS_TRANSACTION = 220;
  public static final int GET_ACTIVITY_STACK_ID_TRANSACTION = 344;
  public static final int GET_ALL_APP_BOOT_MODES_TRANSACTION = 321;
  public static final int GET_ALL_APP_CONTROL_MODES_TRANSACTION = 702;
  public static final int GET_ALL_STACK_INFOS_TRANSACTION = 171;
  public static final int GET_APP_BOOT_MODE_TRANSACTION = 322;
  public static final int GET_APP_BOOT_STATE_TRANSACTION = 324;
  public static final int GET_APP_CONTROL_MODE_TRANSACTION = 703;
  public static final int GET_APP_CONTROL_STATE_TRANSACTION = 705;
  public static final int GET_APP_START_MODE_TRANSACTION = 351;
  public static final int GET_APP_TASKS_TRANSACTION = 221;
  public static final int GET_APP_TASK_THUMBNAIL_SIZE_TRANSACTION = 235;
  public static final int GET_ASSIST_CONTEXT_EXTRAS_TRANSACTION = 162;
  public static final int GET_BG_MONITOR_MODE_TRANSACTION = 334;
  public static final int GET_BG_POWER_HUNGRY_LIST_TRANSACTION = 331;
  public static final int GET_CALLEE_PACKAGE_ARRAY_TRANSACTION = 327;
  public static final int GET_CALLER_PACKAGE_ARRAY_TRANSACTION = 326;
  public static final int GET_CALLING_ACTIVITY_TRANSACTION = 22;
  public static final int GET_CALLING_PACKAGE_TRANSACTION = 21;
  public static final int GET_CONFIGURATION_TRANSACTION = 46;
  public static final int GET_CONTENT_PROVIDER_EXTERNAL_TRANSACTION = 141;
  public static final int GET_CONTENT_PROVIDER_TRANSACTION = 29;
  public static final int GET_CURRENT_USER_TRANSACTION = 145;
  public static final int GET_DEVICE_CONFIGURATION_TRANSACTION = 84;
  public static final int GET_FOCUSED_STACK_ID_TRANSACTION = 283;
  public static final int GET_FRONT_ACTIVITY_SCREEN_COMPAT_MODE_TRANSACTION = 124;
  public static final int GET_GRANTED_URI_PERMISSIONS_TRANSACTION = 361;
  public static final int GET_INTENT_FOR_INTENT_SENDER_TRANSACTION = 161;
  public static final int GET_INTENT_SENDER_TRANSACTION = 63;
  public static final int GET_LAUNCHED_FROM_PACKAGE_TRANSACTION = 164;
  public static final int GET_LAUNCHED_FROM_UID_TRANSACTION = 150;
  public static final int GET_LOCK_TASK_MODE_STATE_TRANSACTION = 287;
  public static final int GET_MEMORY_INFO_TRANSACTION = 76;
  public static final int GET_MEMORY_TRIM_LEVEL_TRANSACTION = 370;
  public static final int GET_MY_MEMORY_STATE_TRANSACTION = 143;
  public static final int GET_PACKAGE_ASK_SCREEN_COMPAT_TRANSACTION = 128;
  public static final int GET_PACKAGE_FOR_INTENT_SENDER_TRANSACTION = 65;
  public static final int GET_PACKAGE_FOR_TOKEN_TRANSACTION = 50;
  public static final int GET_PACKAGE_PROCESS_STATE_TRANSACTION = 294;
  public static final int GET_PACKAGE_SCREEN_COMPAT_MODE_TRANSACTION = 126;
  public static final int GET_PERMISSION_SERVICE_BINDER_TRANSACTION = 303;
  public static final int GET_PERSISTED_URI_PERMISSIONS_TRANSACTION = 182;
  public static final int GET_PROCESSES_IN_ERROR_STATE_TRANSACTION = 77;
  public static final int GET_PROCESS_LIMIT_TRANSACTION = 52;
  public static final int GET_PROCESS_MEMORY_INFO_TRANSACTION = 98;
  public static final int GET_PROCESS_PSS_TRANSACTION = 137;
  public static final int GET_PROVIDER_MIME_TYPE_TRANSACTION = 115;
  public static final int GET_RECENT_TASKS_TRANSACTION = 60;
  public static final int GET_REQUESTED_ORIENTATION_TRANSACTION = 71;
  public static final int GET_RUNNING_APP_PROCESSES_TRANSACTION = 83;
  public static final int GET_RUNNING_EXTERNAL_APPLICATIONS_TRANSACTION = 108;
  public static final int GET_RUNNING_SERVICE_CONTROL_PANEL_TRANSACTION = 33;
  public static final int GET_RUNNING_USER_IDS_TRANSACTION = 157;
  public static final int GET_SERVICES_TRANSACTION = 81;
  public static final int GET_STACK_INFO_TRANSACTION = 173;
  public static final int GET_TAG_FOR_INTENT_SENDER_TRANSACTION = 211;
  public static final int GET_TASKS_TRANSACTION = 23;
  public static final int GET_TASK_BOUNDS_TRANSACTION = 184;
  public static final int GET_TASK_DESCRIPTION_ICON_TRANSACTION = 239;
  public static final int GET_TASK_FOR_ACTIVITY_TRANSACTION = 27;
  public static final int GET_TASK_THUMBNAIL_TRANSACTION = 82;
  public static final int GET_UID_FOR_INTENT_SENDER_TRANSACTION = 93;
  public static final int GET_URI_PERMISSION_OWNER_FOR_ACTIVITY_TRANSACTION = 358;
  public static final int GRANT_URI_PERMISSION_FROM_OWNER_TRANSACTION = 117;
  public static final int GRANT_URI_PERMISSION_TRANSACTION = 55;
  public static final int HANDLE_APPLICATION_CRASH_TRANSACTION = 2;
  public static final int HANDLE_APPLICATION_STRICT_MODE_VIOLATION_TRANSACTION = 110;
  public static final int HANDLE_APPLICATION_WTF_TRANSACTION = 102;
  public static final int HANDLE_INCOMING_USER_TRANSACTION = 94;
  public static final int HANG_TRANSACTION = 167;
  public static final int INPUT_DISPATCHING_TIMED_OUT_TRANSACTION = 159;
  public static final int IN_MULTI_WINDOW_TRANSACTION = 353;
  public static final int IN_PICTURE_IN_PICTURE_TRANSACTION = 354;
  public static final int IS_APP_FOREGROUND_TRANSACTION = 363;
  public static final int IS_APP_LOCKED_TRANSACTION = 669;
  public static final int IS_BACKGROUND_VISIBLE_BEHIND_TRANSACTION = 227;
  public static final int IS_IMMERSIVE_TRANSACTION = 111;
  public static final int IS_INTENT_SENDER_AN_ACTIVITY_TRANSACTION = 152;
  public static final int IS_INTENT_SENDER_TARGETED_TO_PACKAGE_TRANSACTION = 135;
  public static final int IS_IN_HOME_STACK_TRANSACTION = 213;
  public static final int IS_IN_LOCK_TASK_MODE_TRANSACTION = 217;
  public static final int IS_KEYGUARD_DONE_TRANSACTION = 670;
  public static final int IS_REQUEST_PERMISSION_TRANSACTION = 307;
  public static final int IS_ROOT_VOICE_INTERACTION_TRANSACTION = 302;
  public static final int IS_SCREEN_CAPTURE_ALLOWED_ON_CURRENT_ACTIVITY_TRANSACTION = 300;
  public static final int IS_TOP_ACTIVITY_IMMERSIVE_TRANSACTION = 113;
  public static final int IS_TOP_OF_TASK_TRANSACTION = 225;
  public static final int IS_USER_A_MONKEY_TRANSACTION = 104;
  public static final int IS_USER_RUNNING_TRANSACTION = 122;
  public static final int IS_VR_PACKAGE_ENABLED_TRANSACTION = 372;
  public static final int KEYGUARD_GOING_AWAY_TRANSACTION = 297;
  public static final int KEYGUARD_WAITING_FOR_ACTIVITY_DRAWN_TRANSACTION = 232;
  public static final int KILL_ALL_BACKGROUND_PROCESSES_TRANSACTION = 140;
  public static final int KILL_APPLICATION_PROCESS_TRANSACTION = 99;
  public static final int KILL_APPLICATION_TRANSACTION = 96;
  public static final int KILL_BACKGROUND_PROCESSES_TRANSACTION = 103;
  public static final int KILL_PACKAGE_DEPENDENTS_TRANSACTION = 355;
  public static final int KILL_PIDS_TRANSACTION = 80;
  public static final int KILL_PROCESSES_BELOW_FOREGROUND_TRANSACTION = 144;
  public static final int KILL_UID_TRANSACTION = 165;
  public static final int LAUNCH_ASSIST_INTENT_TRANSACTION = 240;
  public static final int MOVE_ACTIVITY_TASK_TO_BACK_TRANSACTION = 75;
  public static final int MOVE_TASKS_TO_FULLSCREEN_STACK_TRANSACTION = 349;
  public static final int MOVE_TASK_BACKWARDS_TRANSACTION = 26;
  public static final int MOVE_TASK_TO_DOCKED_STACK_TRANSACTION = 347;
  public static final int MOVE_TASK_TO_FRONT_TRANSACTION = 24;
  public static final int MOVE_TASK_TO_STACK_TRANSACTION = 169;
  public static final int MOVE_TOP_ACTIVITY_TO_PINNED_STACK_TRANSACTION = 350;
  public static final int NAVIGATE_UP_TO_TRANSACTION = 147;
  public static final int NEW_URI_PERMISSION_OWNER_TRANSACTION = 116;
  public static final int NOTE_ALARM_FINISH_TRANSACTION = 293;
  public static final int NOTE_ALARM_START_TRANSACTION = 292;
  public static final int NOTE_WAKEUP_ALARM_TRANSACTION = 68;
  public static final int NOTIFY_ACTIVITY_DRAWN_TRANSACTION = 176;
  public static final int NOTIFY_CLEARTEXT_NETWORK_TRANSACTION = 281;
  public static final int NOTIFY_ENTER_ANIMATION_COMPLETE_TRANSACTION = 231;
  public static final int NOTIFY_LAUNCH_TASK_BEHIND_COMPLETE_TRANSACTION = 229;
  public static final int NOTIFY_LOCKED_PROFILE = 374;
  public static final int NOTIFY_PINNED_STACK_ANIMATION_ENDED_TRANSACTION = 367;
  public static final int OPEN_CONTENT_URI_TRANSACTION = 5;
  public static final int OVERRIDE_PENDING_TRANSITION_TRANSACTION = 101;
  public static final int PEEK_SERVICE_TRANSACTION = 85;
  public static final int PERFORM_IDLE_MAINTENANCE_TRANSACTION = 179;
  public static final int POSITION_TASK_IN_STACK_TRANSACTION = 343;
  public static final int PROFILE_CONTROL_TRANSACTION = 86;
  public static final int PUBLISH_CONTENT_PROVIDERS_TRANSACTION = 30;
  public static final int PUBLISH_SERVICE_TRANSACTION = 38;
  public static final int REF_CONTENT_PROVIDER_TRANSACTION = 31;
  public static final int REGISTER_PROCESS_OBSERVER_TRANSACTION = 133;
  public static final int REGISTER_RECEIVER_TRANSACTION = 12;
  public static final int REGISTER_TASK_STACK_LISTENER_TRANSACTION = 243;
  public static final int REGISTER_UID_OBSERVER_TRANSACTION = 298;
  public static final int REGISTER_USER_SWITCH_OBSERVER_TRANSACTION = 155;
  public static final int RELEASE_ACTIVITY_INSTANCE_TRANSACTION = 236;
  public static final int RELEASE_PERSISTABLE_URI_PERMISSION_TRANSACTION = 181;
  public static final int RELEASE_SOME_ACTIVITIES_TRANSACTION = 237;
  public static final int REMOVE_CONTENT_PROVIDER_EXTERNAL_TRANSACTION = 142;
  public static final int REMOVE_CONTENT_PROVIDER_TRANSACTION = 69;
  public static final int REMOVE_STACK = 368;
  public static final int REMOVE_TASK_TRANSACTION = 132;
  public static final int REPORT_ACTIVITY_FULLY_DRAWN_TRANSACTION = 177;
  public static final int REPORT_ASSIST_CONTEXT_EXTRAS_TRANSACTION = 163;
  public static final int REPORT_SIZE_CONFIGURATIONS = 346;
  public static final int REQUEST_ASSIST_CONTEXT_EXTRAS_TRANSACTION = 285;
  public static final int REQUEST_BUG_REPORT_TRANSACTION = 158;
  public static final int REQUEST_VISIBLE_BEHIND_TRANSACTION = 226;
  public static final int RESIZE_DOCKED_STACK_TRANSACTION = 359;
  public static final int RESIZE_PINNED_STACK_TRANSACTION = 371;
  public static final int RESIZE_STACK_TRANSACTION = 170;
  public static final int RESIZE_TASK_TRANSACTION = 286;
  public static final int RESTART_TRANSACTION = 178;
  public static final int RESUME_APP_SWITCHES_TRANSACTION = 89;
  public static final int REVOKE_URI_PERMISSION_FROM_OWNER_TRANSACTION = 118;
  public static final int REVOKE_URI_PERMISSION_TRANSACTION = 56;
  public static final int SEND_IDLE_JOB_TRIGGER_TRANSACTION = 376;
  public static final int SEND_INTENT_SENDER_TRANSACTION = 377;
  public static final int SERVICE_DONE_EXECUTING_TRANSACTION = 61;
  public static final int SET_ACTIVITY_CONTROLLER_TRANSACTION = 57;
  public static final int SET_ALWAYS_FINISH_TRANSACTION = 43;
  public static final int SET_APP_BOOT_MODE_TRANSACTION = 323;
  public static final int SET_APP_BOOT_STATE_TRANSACTION = 325;
  public static final int SET_APP_CONTROL_MODE_TRANSACTION = 704;
  public static final int SET_APP_CONTROL_STATE_TRANSACTION = 706;
  public static final int SET_BG_MONITOR_MODE_TRANSACTION = 332;
  public static final int SET_DEBUG_APP_TRANSACTION = 42;
  public static final int SET_DUMP_HEAP_DEBUG_LIMIT_TRANSACTION = 288;
  public static final int SET_FOCUSED_STACK_TRANSACTION = 172;
  public static final int SET_FOCUSED_TASK_TRANSACTION = 131;
  public static final int SET_FRONT_ACTIVITY_SCREEN_COMPAT_MODE_TRANSACTION = 125;
  public static final int SET_HAS_TOP_UI = 380;
  public static final int SET_IGNORED_ANR_PROCESS_TRANSACTION = 305;
  public static final int SET_IMMERSIVE_TRANSACTION = 112;
  public static final int SET_KEYGUARD_DONE_TRANSACTION = 668;
  public static final int SET_LENIENT_BACKGROUND_CHECK_TRANSACTION = 369;
  public static final int SET_LOCK_SCREEN_SHOWN_TRANSACTION = 148;
  public static final int SET_PACKAGE_ASK_SCREEN_COMPAT_TRANSACTION = 129;
  public static final int SET_PACKAGE_SCREEN_COMPAT_MODE_TRANSACTION = 127;
  public static final int SET_PERMISSION_SERVICE_BINDER_TRANSACTION = 304;
  public static final int SET_PROCESS_FOREGROUND_TRANSACTION = 73;
  public static final int SET_PROCESS_LIMIT_TRANSACTION = 51;
  public static final int SET_PROCESS_MEMORY_TRIM_TRANSACTION = 187;
  public static final int SET_RENDER_THREAD_TRANSACTION = 379;
  public static final int SET_REQUESTED_ORIENTATION_TRANSACTION = 70;
  public static final int SET_SERVICE_FOREGROUND_TRANSACTION = 74;
  public static final int SET_TASK_DESCRIPTION_TRANSACTION = 218;
  public static final int SET_TASK_RESIZEABLE_TRANSACTION = 284;
  public static final int SET_USER_IS_MONKEY_TRANSACTION = 166;
  public static final int SET_VOICE_KEEP_AWAKE_TRANSACTION = 290;
  public static final int SET_VR_MODE_TRANSACTION = 360;
  public static final int SET_VR_THREAD_TRANSACTION = 378;
  public static final int SHOULD_UP_RECREATE_TASK_TRANSACTION = 146;
  public static final int SHOW_ASSIST_FROM_ACTIVITY_TRANSACTION = 301;
  public static final int SHOW_BOOT_MESSAGE_TRANSACTION = 138;
  public static final int SHOW_LOCK_TASK_ESCAPE_MESSAGE_TRANSACTION = 295;
  public static final int SHOW_WAITING_FOR_DEBUGGER_TRANSACTION = 58;
  public static final int SHUTDOWN_TRANSACTION = 87;
  public static final int SIGNAL_PERSISTENT_PROCESSES_TRANSACTION = 59;
  public static final int START_ACTIVITIES_TRANSACTION = 121;
  public static final int START_ACTIVITY_AND_WAIT_TRANSACTION = 105;
  public static final int START_ACTIVITY_AS_CALLER_TRANSACTION = 233;
  public static final int START_ACTIVITY_AS_USER_TRANSACTION = 153;
  public static final int START_ACTIVITY_FROM_RECENTS_TRANSACTION = 230;
  public static final int START_ACTIVITY_INTENT_SENDER_TRANSACTION = 100;
  public static final int START_ACTIVITY_TRANSACTION = 3;
  public static final int START_ACTIVITY_WITH_CONFIG_TRANSACTION = 107;
  public static final int START_BACKUP_AGENT_TRANSACTION = 90;
  public static final int START_BINDER_TRACKING_TRANSACTION = 341;
  public static final int START_CONFIRM_DEVICE_CREDENTIAL_INTENT = 375;
  public static final int START_INSTRUMENTATION_TRANSACTION = 44;
  public static final int START_IN_PLACE_ANIMATION_TRANSACTION = 241;
  public static final int START_LOCAL_VOICE_INTERACTION_TRANSACTION = 364;
  public static final int START_LOCK_TASK_BY_TASK_ID_TRANSACTION = 214;
  public static final int START_LOCK_TASK_BY_TOKEN_TRANSACTION = 215;
  public static final int START_NEXT_MATCHING_ACTIVITY_TRANSACTION = 67;
  public static final int START_SERVICE_TRANSACTION = 34;
  public static final int START_SYSTEM_LOCK_TASK_TRANSACTION = 222;
  public static final int START_USER_IN_BACKGROUND_TRANSACTION = 212;
  public static final int START_VOICE_ACTIVITY_TRANSACTION = 219;
  public static final int STOP_APP_SWITCHES_TRANSACTION = 88;
  public static final int STOP_BG_POWER_HUNGRY_APP_TRANSACTION = 333;
  public static final int STOP_BINDER_TRACKING_AND_DUMP_TRANSACTION = 342;
  public static final int STOP_LOCAL_VOICE_INTERACTION_TRANSACTION = 365;
  public static final int STOP_LOCK_TASK_MODE_TRANSACTION = 216;
  public static final int STOP_SERVICE_TOKEN_TRANSACTION = 48;
  public static final int STOP_SERVICE_TRANSACTION = 35;
  public static final int STOP_SYSTEM_LOCK_TASK_TRANSACTION = 223;
  public static final int STOP_USER_TRANSACTION = 154;
  public static final int SUPPORTS_LOCAL_VOICE_INTERACTION_TRANSACTION = 366;
  public static final int SUPPRESS_RESIZE_CONFIG_CHANGES_TRANSACTION = 348;
  public static final int SWAP_DOCKED_AND_FULLSCREEN_STACK = 373;
  public static final int SWITCH_USER_TRANSACTION = 130;
  public static final int TAKE_PERSISTABLE_URI_PERMISSION_TRANSACTION = 180;
  public static final int UNBIND_BACKUP_AGENT_TRANSACTION = 92;
  public static final int UNBIND_FINISHED_TRANSACTION = 72;
  public static final int UNBIND_SERVICE_TRANSACTION = 37;
  public static final int UNBROADCAST_INTENT_TRANSACTION = 15;
  public static final int UNHANDLED_BACK_TRANSACTION = 4;
  public static final int UNLOCK_USER_TRANSACTION = 352;
  public static final int UNREGISTER_PROCESS_OBSERVER_TRANSACTION = 134;
  public static final int UNREGISTER_RECEIVER_TRANSACTION = 13;
  public static final int UNREGISTER_UID_OBSERVER_TRANSACTION = 299;
  public static final int UNREGISTER_USER_SWITCH_OBSERVER_TRANSACTION = 156;
  public static final int UNSTABLE_PROVIDER_DIED_TRANSACTION = 151;
  public static final int UPDATE_ACCESIBILITY_SERVICE_FLAG = 328;
  public static final int UPDATE_CONFIGURATION_TRANSACTION = 47;
  public static final int UPDATE_DEVICE_OWNER_TRANSACTION = 296;
  public static final int UPDATE_LOCK_TASK_PACKAGES_TRANSACTION = 291;
  public static final int UPDATE_PERSISTENT_CONFIGURATION_TRANSACTION = 136;
  public static final int WILL_ACTIVITY_BE_VISIBLE_TRANSACTION = 106;
  public static final String descriptor = "android.app.IActivityManager";
  
  public abstract void activityDestroyed(IBinder paramIBinder)
    throws RemoteException;
  
  public abstract void activityIdle(IBinder paramIBinder, Configuration paramConfiguration, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void activityPaused(IBinder paramIBinder)
    throws RemoteException;
  
  public abstract void activityRelaunched(IBinder paramIBinder)
    throws RemoteException;
  
  public abstract void activityResumed(IBinder paramIBinder)
    throws RemoteException;
  
  public abstract void activitySlept(IBinder paramIBinder)
    throws RemoteException;
  
  public abstract void activityStopped(IBinder paramIBinder, Bundle paramBundle, PersistableBundle paramPersistableBundle, CharSequence paramCharSequence)
    throws RemoteException;
  
  public abstract int addAppTask(IBinder paramIBinder, Intent paramIntent, ActivityManager.TaskDescription paramTaskDescription, Bitmap paramBitmap)
    throws RemoteException;
  
  public abstract void addPackageDependency(String paramString)
    throws RemoteException;
  
  public abstract void appNotRespondingViaProvider(IBinder paramIBinder)
    throws RemoteException;
  
  public abstract void attachApplication(IApplicationThread paramIApplicationThread)
    throws RemoteException;
  
  public abstract void backgroundResourcesReleased(IBinder paramIBinder)
    throws RemoteException;
  
  public abstract void backupAgentCreated(String paramString, IBinder paramIBinder)
    throws RemoteException;
  
  public abstract boolean bindBackupAgent(String paramString, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract int bindService(IApplicationThread paramIApplicationThread, IBinder paramIBinder, Intent paramIntent, String paramString1, IServiceConnection paramIServiceConnection, int paramInt1, String paramString2, int paramInt2)
    throws RemoteException;
  
  public abstract void bootAnimationComplete()
    throws RemoteException;
  
  public abstract int broadcastIntent(IApplicationThread paramIApplicationThread, Intent paramIntent, String paramString1, IIntentReceiver paramIIntentReceiver, int paramInt1, String paramString2, Bundle paramBundle1, String[] paramArrayOfString, int paramInt2, Bundle paramBundle2, boolean paramBoolean1, boolean paramBoolean2, int paramInt3)
    throws RemoteException;
  
  public abstract boolean canBypassWorkChallenge(PendingIntent paramPendingIntent)
    throws RemoteException;
  
  public abstract void cancelIntentSender(IIntentSender paramIIntentSender)
    throws RemoteException;
  
  public abstract int checkGrantUriPermission(int paramInt1, String paramString, Uri paramUri, int paramInt2, int paramInt3)
    throws RemoteException;
  
  public abstract int checkPermission(String paramString, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract int checkPermissionWithToken(String paramString, int paramInt1, int paramInt2, IBinder paramIBinder)
    throws RemoteException;
  
  public abstract int checkUriPermission(Uri paramUri, int paramInt1, int paramInt2, int paramInt3, int paramInt4, IBinder paramIBinder)
    throws RemoteException;
  
  public abstract boolean clearApplicationUserData(String paramString, IPackageDataObserver paramIPackageDataObserver, int paramInt)
    throws RemoteException;
  
  public abstract void clearGrantedUriPermissions(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract void clearPendingBackup()
    throws RemoteException;
  
  public abstract void closeSystemDialogs(String paramString)
    throws RemoteException;
  
  public abstract boolean convertFromTranslucent(IBinder paramIBinder)
    throws RemoteException;
  
  public abstract boolean convertToTranslucent(IBinder paramIBinder, ActivityOptions paramActivityOptions)
    throws RemoteException;
  
  public abstract void crashApplication(int paramInt1, int paramInt2, String paramString1, String paramString2)
    throws RemoteException;
  
  public abstract IActivityContainer createStackOnDisplay(int paramInt)
    throws RemoteException;
  
  public abstract IActivityContainer createVirtualActivityContainer(IBinder paramIBinder, IActivityContainerCallback paramIActivityContainerCallback)
    throws RemoteException;
  
  public abstract void deleteActivityContainer(IActivityContainer paramIActivityContainer)
    throws RemoteException;
  
  public abstract boolean dumpHeap(String paramString1, int paramInt, boolean paramBoolean, String paramString2, ParcelFileDescriptor paramParcelFileDescriptor)
    throws RemoteException;
  
  public abstract void dumpHeapFinished(String paramString)
    throws RemoteException;
  
  public abstract void enterPictureInPictureMode(IBinder paramIBinder)
    throws RemoteException;
  
  public abstract void enterSafeMode()
    throws RemoteException;
  
  public abstract void exitFreeformMode(IBinder paramIBinder)
    throws RemoteException;
  
  public abstract boolean finishActivity(IBinder paramIBinder, int paramInt1, Intent paramIntent, int paramInt2)
    throws RemoteException;
  
  public abstract boolean finishActivityAffinity(IBinder paramIBinder)
    throws RemoteException;
  
  public abstract void finishHeavyWeightApp()
    throws RemoteException;
  
  public abstract void finishInstrumentation(IApplicationThread paramIApplicationThread, int paramInt, Bundle paramBundle)
    throws RemoteException;
  
  public abstract void finishNotOrderReceiver(IBinder paramIBinder, int paramInt1, int paramInt2, String paramString, Bundle paramBundle, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void finishReceiver(IBinder paramIBinder, int paramInt1, String paramString, Bundle paramBundle, boolean paramBoolean, int paramInt2)
    throws RemoteException;
  
  public abstract void finishSubActivity(IBinder paramIBinder, String paramString, int paramInt)
    throws RemoteException;
  
  public abstract void finishVoiceTask(IVoiceInteractionSession paramIVoiceInteractionSession)
    throws RemoteException;
  
  public abstract void forceStopPackage(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract ComponentName getActivityClassForToken(IBinder paramIBinder)
    throws RemoteException;
  
  public abstract int getActivityDisplayId(IBinder paramIBinder)
    throws RemoteException;
  
  public abstract ActivityOptions getActivityOptions(IBinder paramIBinder)
    throws RemoteException;
  
  public abstract int getActivityStackId(IBinder paramIBinder)
    throws RemoteException;
  
  public abstract List<ActivityManager.AppBootMode> getAllAppBootModes(int paramInt)
    throws RemoteException;
  
  public abstract List<ActivityManager.AppControlMode> getAllAppControlModes(int paramInt)
    throws RemoteException;
  
  public abstract List<ActivityManager.StackInfo> getAllStackInfos()
    throws RemoteException;
  
  public abstract int getAppBootMode(String paramString)
    throws RemoteException;
  
  public abstract boolean getAppBootState()
    throws RemoteException;
  
  public abstract int getAppControlMode(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract int getAppControlState(int paramInt)
    throws RemoteException;
  
  public abstract int getAppStartMode(int paramInt, String paramString)
    throws RemoteException;
  
  public abstract Point getAppTaskThumbnailSize()
    throws RemoteException;
  
  public abstract List<IAppTask> getAppTasks(String paramString)
    throws RemoteException;
  
  public abstract Bundle getAssistContextExtras(int paramInt)
    throws RemoteException;
  
  public abstract boolean getBgMonitorMode()
    throws RemoteException;
  
  public abstract List<ActivityManager.HighPowerApp> getBgPowerHungryList()
    throws RemoteException;
  
  public abstract String[] getCalleePackageArray(String paramString)
    throws RemoteException;
  
  public abstract String[] getCallerPackageArray(String paramString)
    throws RemoteException;
  
  public abstract ComponentName getCallingActivity(IBinder paramIBinder)
    throws RemoteException;
  
  public abstract String getCallingPackage(IBinder paramIBinder)
    throws RemoteException;
  
  public abstract Configuration getConfiguration()
    throws RemoteException;
  
  public abstract ContentProviderHolder getContentProvider(IApplicationThread paramIApplicationThread, String paramString, int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public abstract ContentProviderHolder getContentProviderExternal(String paramString, int paramInt, IBinder paramIBinder)
    throws RemoteException;
  
  public abstract UserInfo getCurrentUser()
    throws RemoteException;
  
  public abstract ConfigurationInfo getDeviceConfigurationInfo()
    throws RemoteException;
  
  public abstract int getFocusedStackId()
    throws RemoteException;
  
  public abstract int getFrontActivityScreenCompatMode()
    throws RemoteException;
  
  public abstract ParceledListSlice<UriPermission> getGrantedUriPermissions(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract Intent getIntentForIntentSender(IIntentSender paramIIntentSender)
    throws RemoteException;
  
  public abstract IIntentSender getIntentSender(int paramInt1, String paramString1, IBinder paramIBinder, String paramString2, int paramInt2, Intent[] paramArrayOfIntent, String[] paramArrayOfString, int paramInt3, Bundle paramBundle, int paramInt4)
    throws RemoteException;
  
  public abstract String getLaunchedFromPackage(IBinder paramIBinder)
    throws RemoteException;
  
  public abstract int getLaunchedFromUid(IBinder paramIBinder)
    throws RemoteException;
  
  public abstract int getLockTaskModeState()
    throws RemoteException;
  
  public abstract void getMemoryInfo(ActivityManager.MemoryInfo paramMemoryInfo)
    throws RemoteException;
  
  public abstract int getMemoryTrimLevel()
    throws RemoteException;
  
  public abstract void getMyMemoryState(ActivityManager.RunningAppProcessInfo paramRunningAppProcessInfo)
    throws RemoteException;
  
  public abstract boolean getPackageAskScreenCompat(String paramString)
    throws RemoteException;
  
  public abstract String getPackageForIntentSender(IIntentSender paramIIntentSender)
    throws RemoteException;
  
  public abstract String getPackageForToken(IBinder paramIBinder)
    throws RemoteException;
  
  public abstract int getPackageProcessState(String paramString1, String paramString2)
    throws RemoteException;
  
  public abstract int getPackageScreenCompatMode(String paramString)
    throws RemoteException;
  
  public abstract IBinder getPermissionServiceBinderProxy(int paramInt)
    throws RemoteException;
  
  public abstract ParceledListSlice<UriPermission> getPersistedUriPermissions(String paramString, boolean paramBoolean)
    throws RemoteException;
  
  public abstract int getProcessLimit()
    throws RemoteException;
  
  public abstract Debug.MemoryInfo[] getProcessMemoryInfo(int[] paramArrayOfInt)
    throws RemoteException;
  
  public abstract long[] getProcessPss(int[] paramArrayOfInt)
    throws RemoteException;
  
  public abstract List<ActivityManager.ProcessErrorStateInfo> getProcessesInErrorState()
    throws RemoteException;
  
  public abstract String getProviderMimeType(Uri paramUri, int paramInt)
    throws RemoteException;
  
  public abstract ParceledListSlice<ActivityManager.RecentTaskInfo> getRecentTasks(int paramInt1, int paramInt2, int paramInt3)
    throws RemoteException;
  
  public abstract int getRequestedOrientation(IBinder paramIBinder)
    throws RemoteException;
  
  public abstract List<ActivityManager.RunningAppProcessInfo> getRunningAppProcesses()
    throws RemoteException;
  
  public abstract List<ApplicationInfo> getRunningExternalApplications()
    throws RemoteException;
  
  public abstract PendingIntent getRunningServiceControlPanel(ComponentName paramComponentName)
    throws RemoteException;
  
  public abstract int[] getRunningUserIds()
    throws RemoteException;
  
  public abstract List<ActivityManager.RunningServiceInfo> getServices(int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract ActivityManager.StackInfo getStackInfo(int paramInt)
    throws RemoteException;
  
  public abstract String getTagForIntentSender(IIntentSender paramIIntentSender, String paramString)
    throws RemoteException;
  
  public abstract Rect getTaskBounds(int paramInt)
    throws RemoteException;
  
  public abstract Bitmap getTaskDescriptionIcon(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract int getTaskForActivity(IBinder paramIBinder, boolean paramBoolean)
    throws RemoteException;
  
  public abstract ActivityManager.TaskThumbnail getTaskThumbnail(int paramInt)
    throws RemoteException;
  
  public abstract List<ActivityManager.RunningTaskInfo> getTasks(int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract int getUidForIntentSender(IIntentSender paramIIntentSender)
    throws RemoteException;
  
  public abstract IBinder getUriPermissionOwnerForActivity(IBinder paramIBinder)
    throws RemoteException;
  
  public abstract void grantUriPermission(IApplicationThread paramIApplicationThread, String paramString, Uri paramUri, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void grantUriPermissionFromOwner(IBinder paramIBinder, int paramInt1, String paramString, Uri paramUri, int paramInt2, int paramInt3, int paramInt4)
    throws RemoteException;
  
  public abstract void handleApplicationCrash(IBinder paramIBinder, ApplicationErrorReport.CrashInfo paramCrashInfo)
    throws RemoteException;
  
  public abstract void handleApplicationStrictModeViolation(IBinder paramIBinder, int paramInt, StrictMode.ViolationInfo paramViolationInfo)
    throws RemoteException;
  
  public abstract boolean handleApplicationWtf(IBinder paramIBinder, String paramString, boolean paramBoolean, ApplicationErrorReport.CrashInfo paramCrashInfo)
    throws RemoteException;
  
  public abstract int handleIncomingUser(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean1, boolean paramBoolean2, String paramString1, String paramString2)
    throws RemoteException;
  
  public abstract void hang(IBinder paramIBinder, boolean paramBoolean)
    throws RemoteException;
  
  public abstract long inputDispatchingTimedOut(int paramInt, boolean paramBoolean, String paramString)
    throws RemoteException;
  
  public abstract boolean isAppForeground(int paramInt)
    throws RemoteException;
  
  public abstract boolean isAppLocked(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract boolean isAssistDataAllowedOnCurrentActivity()
    throws RemoteException;
  
  public abstract boolean isBackgroundVisibleBehind(IBinder paramIBinder)
    throws RemoteException;
  
  public abstract boolean isImmersive(IBinder paramIBinder)
    throws RemoteException;
  
  public abstract boolean isInHomeStack(int paramInt)
    throws RemoteException;
  
  public abstract boolean isInLockTaskMode()
    throws RemoteException;
  
  public abstract boolean isInMultiWindowMode(IBinder paramIBinder)
    throws RemoteException;
  
  public abstract boolean isInPictureInPictureMode(IBinder paramIBinder)
    throws RemoteException;
  
  public abstract boolean isIntentSenderAnActivity(IIntentSender paramIIntentSender)
    throws RemoteException;
  
  public abstract boolean isIntentSenderTargetedToPackage(IIntentSender paramIIntentSender)
    throws RemoteException;
  
  public abstract boolean isKeyguardDone()
    throws RemoteException;
  
  public abstract void isRequestPermission(boolean paramBoolean)
    throws RemoteException;
  
  public abstract boolean isRootVoiceInteraction(IBinder paramIBinder)
    throws RemoteException;
  
  public abstract boolean isTopActivityImmersive()
    throws RemoteException;
  
  public abstract boolean isTopOfTask(IBinder paramIBinder)
    throws RemoteException;
  
  public abstract boolean isUserAMonkey()
    throws RemoteException;
  
  public abstract boolean isUserRunning(int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract boolean isVrModePackageEnabled(ComponentName paramComponentName)
    throws RemoteException;
  
  public abstract void keyguardGoingAway(int paramInt)
    throws RemoteException;
  
  public abstract void keyguardWaitingForActivityDrawn()
    throws RemoteException;
  
  public abstract void killAllBackgroundProcesses()
    throws RemoteException;
  
  public abstract void killApplication(String paramString1, int paramInt1, int paramInt2, String paramString2)
    throws RemoteException;
  
  public abstract void killApplicationProcess(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract void killBackgroundProcesses(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract void killPackageDependents(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract boolean killPids(int[] paramArrayOfInt, String paramString, boolean paramBoolean)
    throws RemoteException;
  
  public abstract boolean killProcessesBelowForeground(String paramString)
    throws RemoteException;
  
  public abstract void killUid(int paramInt1, int paramInt2, String paramString)
    throws RemoteException;
  
  public abstract boolean launchAssistIntent(Intent paramIntent, int paramInt1, String paramString, int paramInt2, Bundle paramBundle)
    throws RemoteException;
  
  public abstract boolean moveActivityTaskToBack(IBinder paramIBinder, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void moveTaskBackwards(int paramInt)
    throws RemoteException;
  
  public abstract boolean moveTaskToDockedStack(int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2, Rect paramRect, boolean paramBoolean3)
    throws RemoteException;
  
  public abstract void moveTaskToFront(int paramInt1, int paramInt2, Bundle paramBundle)
    throws RemoteException;
  
  public abstract void moveTaskToStack(int paramInt1, int paramInt2, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void moveTasksToFullscreenStack(int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public abstract boolean moveTopActivityToPinnedStack(int paramInt, Rect paramRect)
    throws RemoteException;
  
  public abstract boolean navigateUpTo(IBinder paramIBinder, Intent paramIntent1, int paramInt, Intent paramIntent2)
    throws RemoteException;
  
  public abstract IBinder newUriPermissionOwner(String paramString)
    throws RemoteException;
  
  public abstract void noteAlarmFinish(IIntentSender paramIIntentSender, int paramInt, String paramString)
    throws RemoteException;
  
  public abstract void noteAlarmStart(IIntentSender paramIIntentSender, int paramInt, String paramString)
    throws RemoteException;
  
  public abstract void noteWakeupAlarm(IIntentSender paramIIntentSender, int paramInt, String paramString1, String paramString2)
    throws RemoteException;
  
  public abstract void notifyActivityDrawn(IBinder paramIBinder)
    throws RemoteException;
  
  public abstract void notifyCleartextNetwork(int paramInt, byte[] paramArrayOfByte)
    throws RemoteException;
  
  public abstract void notifyEnterAnimationComplete(IBinder paramIBinder)
    throws RemoteException;
  
  public abstract void notifyLaunchTaskBehindComplete(IBinder paramIBinder)
    throws RemoteException;
  
  public abstract void notifyLockedProfile(int paramInt)
    throws RemoteException;
  
  public abstract void notifyPinnedStackAnimationEnded()
    throws RemoteException;
  
  public abstract ParcelFileDescriptor openContentUri(Uri paramUri)
    throws RemoteException;
  
  public abstract void overridePendingTransition(IBinder paramIBinder, String paramString, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract IBinder peekService(Intent paramIntent, String paramString1, String paramString2)
    throws RemoteException;
  
  public abstract void performIdleMaintenance()
    throws RemoteException;
  
  public abstract void positionTaskInStack(int paramInt1, int paramInt2, int paramInt3)
    throws RemoteException;
  
  public abstract boolean profileControl(String paramString, int paramInt1, boolean paramBoolean, ProfilerInfo paramProfilerInfo, int paramInt2)
    throws RemoteException;
  
  public abstract void publishContentProviders(IApplicationThread paramIApplicationThread, List<ContentProviderHolder> paramList)
    throws RemoteException;
  
  public abstract void publishService(IBinder paramIBinder1, Intent paramIntent, IBinder paramIBinder2)
    throws RemoteException;
  
  public abstract boolean refContentProvider(IBinder paramIBinder, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void registerProcessObserver(IProcessObserver paramIProcessObserver)
    throws RemoteException;
  
  public abstract Intent registerReceiver(IApplicationThread paramIApplicationThread, String paramString1, IIntentReceiver paramIIntentReceiver, IntentFilter paramIntentFilter, String paramString2, int paramInt)
    throws RemoteException;
  
  public abstract void registerTaskStackListener(ITaskStackListener paramITaskStackListener)
    throws RemoteException;
  
  public abstract void registerUidObserver(IUidObserver paramIUidObserver, int paramInt)
    throws RemoteException;
  
  public abstract void registerUserSwitchObserver(IUserSwitchObserver paramIUserSwitchObserver, String paramString)
    throws RemoteException;
  
  public abstract boolean releaseActivityInstance(IBinder paramIBinder)
    throws RemoteException;
  
  public abstract void releasePersistableUriPermission(Uri paramUri, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void releaseSomeActivities(IApplicationThread paramIApplicationThread)
    throws RemoteException;
  
  public abstract void removeContentProvider(IBinder paramIBinder, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void removeContentProviderExternal(String paramString, IBinder paramIBinder)
    throws RemoteException;
  
  public abstract void removeStack(int paramInt)
    throws RemoteException;
  
  public abstract boolean removeTask(int paramInt)
    throws RemoteException;
  
  public abstract void reportActivityFullyDrawn(IBinder paramIBinder)
    throws RemoteException;
  
  public abstract void reportAssistContextExtras(IBinder paramIBinder, Bundle paramBundle, AssistStructure paramAssistStructure, AssistContent paramAssistContent, Uri paramUri)
    throws RemoteException;
  
  public abstract void reportSizeConfigurations(IBinder paramIBinder, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int[] paramArrayOfInt3)
    throws RemoteException;
  
  public abstract boolean requestAssistContextExtras(int paramInt, IResultReceiver paramIResultReceiver, Bundle paramBundle, IBinder paramIBinder, boolean paramBoolean1, boolean paramBoolean2)
    throws RemoteException;
  
  public abstract void requestBugReport(int paramInt)
    throws RemoteException;
  
  public abstract boolean requestVisibleBehind(IBinder paramIBinder, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void resizeDockedStack(Rect paramRect1, Rect paramRect2, Rect paramRect3, Rect paramRect4, Rect paramRect5)
    throws RemoteException;
  
  public abstract void resizePinnedStack(Rect paramRect1, Rect paramRect2)
    throws RemoteException;
  
  public abstract void resizeStack(int paramInt1, Rect paramRect, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, int paramInt2)
    throws RemoteException;
  
  public abstract void resizeTask(int paramInt1, Rect paramRect, int paramInt2)
    throws RemoteException;
  
  public abstract void restart()
    throws RemoteException;
  
  public abstract void resumeAppSwitches()
    throws RemoteException;
  
  public abstract void revokeUriPermission(IApplicationThread paramIApplicationThread, Uri paramUri, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void revokeUriPermissionFromOwner(IBinder paramIBinder, Uri paramUri, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void sendIdleJobTrigger()
    throws RemoteException;
  
  public abstract int sendIntentSender(IIntentSender paramIIntentSender, int paramInt, Intent paramIntent, String paramString1, IIntentReceiver paramIIntentReceiver, String paramString2, Bundle paramBundle)
    throws RemoteException;
  
  public abstract void serviceDoneExecuting(IBinder paramIBinder, int paramInt1, int paramInt2, int paramInt3)
    throws RemoteException;
  
  public abstract void setActivityController(IActivityController paramIActivityController, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setAlwaysFinish(boolean paramBoolean)
    throws RemoteException;
  
  public abstract int setAppBootMode(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract void setAppBootState(boolean paramBoolean)
    throws RemoteException;
  
  public abstract int setAppControlMode(String paramString, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract int setAppControlState(int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void setBgMonitorMode(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setDebugApp(String paramString, boolean paramBoolean1, boolean paramBoolean2)
    throws RemoteException;
  
  public abstract void setDumpHeapDebugLimit(String paramString1, int paramInt, long paramLong, String paramString2)
    throws RemoteException;
  
  public abstract void setFocusedStack(int paramInt)
    throws RemoteException;
  
  public abstract void setFocusedTask(int paramInt)
    throws RemoteException;
  
  public abstract void setFrontActivityScreenCompatMode(int paramInt)
    throws RemoteException;
  
  public abstract void setHasTopUi(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setIgnoredAnrProcess(String paramString)
    throws RemoteException;
  
  public abstract void setImmersive(IBinder paramIBinder, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setKeyguardDone(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setLenientBackgroundCheck(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setLockScreenShown(boolean paramBoolean1, boolean paramBoolean2)
    throws RemoteException;
  
  public abstract void setPackageAskScreenCompat(String paramString, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setPackageScreenCompatMode(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract void setPermissionServiceBinderProxy(IBinder paramIBinder, int paramInt)
    throws RemoteException;
  
  public abstract void setProcessForeground(IBinder paramIBinder, int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setProcessLimit(int paramInt)
    throws RemoteException;
  
  public abstract boolean setProcessMemoryTrimLevel(String paramString, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void setRenderThread(int paramInt)
    throws RemoteException;
  
  public abstract void setRequestedOrientation(IBinder paramIBinder, int paramInt)
    throws RemoteException;
  
  public abstract void setServiceForeground(ComponentName paramComponentName, IBinder paramIBinder, int paramInt1, Notification paramNotification, int paramInt2)
    throws RemoteException;
  
  public abstract void setTaskDescription(IBinder paramIBinder, ActivityManager.TaskDescription paramTaskDescription)
    throws RemoteException;
  
  public abstract void setTaskResizeable(int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract void setUserIsMonkey(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setVoiceKeepAwake(IVoiceInteractionSession paramIVoiceInteractionSession, boolean paramBoolean)
    throws RemoteException;
  
  public abstract int setVrMode(IBinder paramIBinder, boolean paramBoolean, ComponentName paramComponentName)
    throws RemoteException;
  
  public abstract void setVrThread(int paramInt)
    throws RemoteException;
  
  public abstract boolean shouldUpRecreateTask(IBinder paramIBinder, String paramString)
    throws RemoteException;
  
  public abstract boolean showAssistFromActivity(IBinder paramIBinder, Bundle paramBundle)
    throws RemoteException;
  
  public abstract void showBootMessage(CharSequence paramCharSequence, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void showLockTaskEscapeMessage(IBinder paramIBinder)
    throws RemoteException;
  
  public abstract void showWaitingForDebugger(IApplicationThread paramIApplicationThread, boolean paramBoolean)
    throws RemoteException;
  
  public abstract boolean shutdown(int paramInt)
    throws RemoteException;
  
  public abstract void signalPersistentProcesses(int paramInt)
    throws RemoteException;
  
  public abstract int startActivities(IApplicationThread paramIApplicationThread, String paramString, Intent[] paramArrayOfIntent, String[] paramArrayOfString, IBinder paramIBinder, Bundle paramBundle, int paramInt)
    throws RemoteException;
  
  public abstract int startActivity(IApplicationThread paramIApplicationThread, String paramString1, Intent paramIntent, String paramString2, IBinder paramIBinder, String paramString3, int paramInt1, int paramInt2, ProfilerInfo paramProfilerInfo, Bundle paramBundle)
    throws RemoteException;
  
  public abstract WaitResult startActivityAndWait(IApplicationThread paramIApplicationThread, String paramString1, Intent paramIntent, String paramString2, IBinder paramIBinder, String paramString3, int paramInt1, int paramInt2, ProfilerInfo paramProfilerInfo, Bundle paramBundle, int paramInt3)
    throws RemoteException;
  
  public abstract int startActivityAsCaller(IApplicationThread paramIApplicationThread, String paramString1, Intent paramIntent, String paramString2, IBinder paramIBinder, String paramString3, int paramInt1, int paramInt2, ProfilerInfo paramProfilerInfo, Bundle paramBundle, boolean paramBoolean, int paramInt3)
    throws RemoteException;
  
  public abstract int startActivityAsUser(IApplicationThread paramIApplicationThread, String paramString1, Intent paramIntent, String paramString2, IBinder paramIBinder, String paramString3, int paramInt1, int paramInt2, ProfilerInfo paramProfilerInfo, Bundle paramBundle, int paramInt3)
    throws RemoteException;
  
  public abstract int startActivityFromRecents(int paramInt, Bundle paramBundle)
    throws RemoteException;
  
  public abstract int startActivityIntentSender(IApplicationThread paramIApplicationThread, IntentSender paramIntentSender, Intent paramIntent, String paramString1, IBinder paramIBinder, String paramString2, int paramInt1, int paramInt2, int paramInt3, Bundle paramBundle)
    throws RemoteException;
  
  public abstract int startActivityWithConfig(IApplicationThread paramIApplicationThread, String paramString1, Intent paramIntent, String paramString2, IBinder paramIBinder, String paramString3, int paramInt1, int paramInt2, Configuration paramConfiguration, Bundle paramBundle, int paramInt3)
    throws RemoteException;
  
  public abstract boolean startBinderTracking()
    throws RemoteException;
  
  public abstract void startConfirmDeviceCredentialIntent(Intent paramIntent)
    throws RemoteException;
  
  public abstract void startInPlaceAnimationOnFrontMostApplication(ActivityOptions paramActivityOptions)
    throws RemoteException;
  
  public abstract boolean startInstrumentation(ComponentName paramComponentName, String paramString1, int paramInt1, Bundle paramBundle, IInstrumentationWatcher paramIInstrumentationWatcher, IUiAutomationConnection paramIUiAutomationConnection, int paramInt2, String paramString2)
    throws RemoteException;
  
  public abstract void startLocalVoiceInteraction(IBinder paramIBinder, Bundle paramBundle)
    throws RemoteException;
  
  public abstract void startLockTaskMode(int paramInt)
    throws RemoteException;
  
  public abstract void startLockTaskMode(IBinder paramIBinder)
    throws RemoteException;
  
  public abstract boolean startNextMatchingActivity(IBinder paramIBinder, Intent paramIntent, Bundle paramBundle)
    throws RemoteException;
  
  public abstract ComponentName startService(IApplicationThread paramIApplicationThread, Intent paramIntent, String paramString1, String paramString2, int paramInt)
    throws RemoteException;
  
  public abstract void startSystemLockTaskMode(int paramInt)
    throws RemoteException;
  
  public abstract boolean startUserInBackground(int paramInt)
    throws RemoteException;
  
  public abstract int startVoiceActivity(String paramString1, int paramInt1, int paramInt2, Intent paramIntent, String paramString2, IVoiceInteractionSession paramIVoiceInteractionSession, IVoiceInteractor paramIVoiceInteractor, int paramInt3, ProfilerInfo paramProfilerInfo, Bundle paramBundle, int paramInt4)
    throws RemoteException;
  
  public abstract void stopAppSwitches()
    throws RemoteException;
  
  public abstract void stopBgPowerHungryApp(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract boolean stopBinderTrackingAndDump(ParcelFileDescriptor paramParcelFileDescriptor)
    throws RemoteException;
  
  public abstract void stopLocalVoiceInteraction(IBinder paramIBinder)
    throws RemoteException;
  
  public abstract void stopLockTaskMode()
    throws RemoteException;
  
  public abstract int stopService(IApplicationThread paramIApplicationThread, Intent paramIntent, String paramString, int paramInt)
    throws RemoteException;
  
  public abstract boolean stopServiceToken(ComponentName paramComponentName, IBinder paramIBinder, int paramInt)
    throws RemoteException;
  
  public abstract void stopSystemLockTaskMode()
    throws RemoteException;
  
  public abstract int stopUser(int paramInt, boolean paramBoolean, IStopUserCallback paramIStopUserCallback)
    throws RemoteException;
  
  public abstract boolean supportsLocalVoiceInteraction()
    throws RemoteException;
  
  public abstract void suppressResizeConfigChanges(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void swapDockedAndFullscreenStack()
    throws RemoteException;
  
  public abstract boolean switchUser(int paramInt)
    throws RemoteException;
  
  public abstract void takePersistableUriPermission(Uri paramUri, int paramInt1, int paramInt2)
    throws RemoteException;
  
  public abstract boolean testIsSystemReady();
  
  public abstract void unbindBackupAgent(ApplicationInfo paramApplicationInfo)
    throws RemoteException;
  
  public abstract void unbindFinished(IBinder paramIBinder, Intent paramIntent, boolean paramBoolean)
    throws RemoteException;
  
  public abstract boolean unbindService(IServiceConnection paramIServiceConnection)
    throws RemoteException;
  
  public abstract void unbroadcastIntent(IApplicationThread paramIApplicationThread, Intent paramIntent, int paramInt)
    throws RemoteException;
  
  public abstract void unhandledBack()
    throws RemoteException;
  
  public abstract boolean unlockUser(int paramInt, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, IProgressListener paramIProgressListener)
    throws RemoteException;
  
  public abstract void unregisterProcessObserver(IProcessObserver paramIProcessObserver)
    throws RemoteException;
  
  public abstract void unregisterReceiver(IIntentReceiver paramIIntentReceiver)
    throws RemoteException;
  
  public abstract void unregisterUidObserver(IUidObserver paramIUidObserver)
    throws RemoteException;
  
  public abstract void unregisterUserSwitchObserver(IUserSwitchObserver paramIUserSwitchObserver)
    throws RemoteException;
  
  public abstract void unstableProviderDied(IBinder paramIBinder)
    throws RemoteException;
  
  public abstract void updateAccesibilityServiceFlag(String paramString, int paramInt)
    throws RemoteException;
  
  public abstract void updateConfiguration(Configuration paramConfiguration)
    throws RemoteException;
  
  public abstract void updateDeviceOwner(String paramString)
    throws RemoteException;
  
  public abstract void updateLockTaskPackages(int paramInt, String[] paramArrayOfString)
    throws RemoteException;
  
  public abstract void updatePersistentConfiguration(Configuration paramConfiguration)
    throws RemoteException;
  
  public abstract boolean willActivityBeVisible(IBinder paramIBinder)
    throws RemoteException;
  
  public static class ContentProviderHolder
    implements Parcelable
  {
    public static final Parcelable.Creator<ContentProviderHolder> CREATOR = new Parcelable.Creator()
    {
      public IActivityManager.ContentProviderHolder createFromParcel(Parcel paramAnonymousParcel)
      {
        return new IActivityManager.ContentProviderHolder(paramAnonymousParcel, null);
      }
      
      public IActivityManager.ContentProviderHolder[] newArray(int paramAnonymousInt)
      {
        return new IActivityManager.ContentProviderHolder[paramAnonymousInt];
      }
    };
    public IBinder connection;
    public final ProviderInfo info;
    public boolean noReleaseNeeded;
    public IContentProvider provider;
    
    public ContentProviderHolder(ProviderInfo paramProviderInfo)
    {
      this.info = paramProviderInfo;
    }
    
    private ContentProviderHolder(Parcel paramParcel)
    {
      this.info = ((ProviderInfo)ProviderInfo.CREATOR.createFromParcel(paramParcel));
      this.provider = ContentProviderNative.asInterface(paramParcel.readStrongBinder());
      this.connection = paramParcel.readStrongBinder();
      if (paramParcel.readInt() != 0) {}
      for (boolean bool = true;; bool = false)
      {
        this.noReleaseNeeded = bool;
        return;
      }
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramInt = 0;
      this.info.writeToParcel(paramParcel, 0);
      if (this.provider != null) {
        paramParcel.writeStrongBinder(this.provider.asBinder());
      }
      for (;;)
      {
        paramParcel.writeStrongBinder(this.connection);
        if (this.noReleaseNeeded) {
          paramInt = 1;
        }
        paramParcel.writeInt(paramInt);
        return;
        paramParcel.writeStrongBinder(null);
      }
    }
  }
  
  public static class WaitResult
    implements Parcelable
  {
    public static final Parcelable.Creator<WaitResult> CREATOR = new Parcelable.Creator()
    {
      public IActivityManager.WaitResult createFromParcel(Parcel paramAnonymousParcel)
      {
        return new IActivityManager.WaitResult(paramAnonymousParcel, null);
      }
      
      public IActivityManager.WaitResult[] newArray(int paramAnonymousInt)
      {
        return new IActivityManager.WaitResult[paramAnonymousInt];
      }
    };
    public int result;
    public long thisTime;
    public boolean timeout;
    public long totalTime;
    public ComponentName who;
    
    public WaitResult() {}
    
    private WaitResult(Parcel paramParcel)
    {
      this.result = paramParcel.readInt();
      if (paramParcel.readInt() != 0) {
        bool = true;
      }
      this.timeout = bool;
      this.who = ComponentName.readFromParcel(paramParcel);
      this.thisTime = paramParcel.readLong();
      this.totalTime = paramParcel.readLong();
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeInt(this.result);
      if (this.timeout) {}
      for (paramInt = 1;; paramInt = 0)
      {
        paramParcel.writeInt(paramInt);
        ComponentName.writeToParcel(this.who, paramParcel);
        paramParcel.writeLong(this.thisTime);
        paramParcel.writeLong(this.totalTime);
        return;
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/IActivityManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */