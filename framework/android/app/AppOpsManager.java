package android.app;

import android.content.Context;
import android.os.Binder;
import android.os.Build;
import android.os.Debug;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.Process;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.ArrayMap;
import android.util.Slog;
import com.android.internal.app.IAppOpsCallback;
import com.android.internal.app.IAppOpsCallback.Stub;
import com.android.internal.app.IAppOpsService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AppOpsManager
{
  public static final int MODE_ALLOWED = 0;
  public static final int MODE_DEFAULT = 3;
  public static final int MODE_ERRORED = 2;
  public static final int MODE_IGNORED = 1;
  public static final String OPSTR_ACTIVATE_VPN = "android:activate_vpn";
  public static final String OPSTR_ADD_VOICEMAIL = "android:add_voicemail";
  public static final String OPSTR_BLUETOOTH_ADMIN = "android:bluetooth_admin";
  public static final String OPSTR_BODY_SENSORS = "android:body_sensors";
  public static final String OPSTR_CALL_PHONE = "android:call_phone";
  public static final String OPSTR_CAMERA = "android:camera";
  public static final String OPSTR_CHANGE_WIFI_STATE = "android:change_wifi_state";
  public static final String OPSTR_COARSE_LOCATION = "android:coarse_location";
  public static final String OPSTR_FINE_LOCATION = "android:fine_location";
  public static final String OPSTR_GET_ACCOUNTS = "android:get_accounts";
  public static final String OPSTR_GET_USAGE_STATS = "android:get_usage_stats";
  public static final String OPSTR_MOCK_LOCATION = "android:mock_location";
  public static final String OPSTR_MONITOR_HIGH_POWER_LOCATION = "android:monitor_location_high_power";
  public static final String OPSTR_MONITOR_LOCATION = "android:monitor_location";
  public static final String OPSTR_PROCESS_OUTGOING_CALLS = "android:process_outgoing_calls";
  public static final String OPSTR_READ_CALENDAR = "android:read_calendar";
  public static final String OPSTR_READ_CALL_LOG = "android:read_call_log";
  public static final String OPSTR_READ_CELL_BROADCASTS = "android:read_cell_broadcasts";
  public static final String OPSTR_READ_CONTACTS = "android:read_contacts";
  public static final String OPSTR_READ_EXTERNAL_STORAGE = "android:read_external_storage";
  public static final String OPSTR_READ_PHONE_STATE = "android:read_phone_state";
  public static final String OPSTR_READ_SMS = "android:read_sms";
  public static final String OPSTR_RECEIVE_MMS = "android:receive_mms";
  public static final String OPSTR_RECEIVE_SMS = "android:receive_sms";
  public static final String OPSTR_RECEIVE_WAP_PUSH = "android:receive_wap_push";
  public static final String OPSTR_RECORD_AUDIO = "android:record_audio";
  public static final String OPSTR_SEND_SMS = "android:send_sms";
  public static final String OPSTR_SYSTEM_ALERT_WINDOW = "android:system_alert_window";
  public static final String OPSTR_USE_FINGERPRINT = "android:use_fingerprint";
  public static final String OPSTR_USE_SIP = "android:use_sip";
  public static final String OPSTR_WRITE_CALENDAR = "android:write_calendar";
  public static final String OPSTR_WRITE_CALL_LOG = "android:write_call_log";
  public static final String OPSTR_WRITE_CONTACTS = "android:write_contacts";
  public static final String OPSTR_WRITE_EXTERNAL_STORAGE = "android:write_external_storage";
  public static final String OPSTR_WRITE_SETTINGS = "android:write_settings";
  public static final int OP_ACCESS_NOTIFICATIONS = 25;
  public static final int OP_ACTIVATE_VPN = 47;
  public static final int OP_ADD_VOICEMAIL = 52;
  public static final int OP_ASSIST_SCREENSHOT = 50;
  public static final int OP_ASSIST_STRUCTURE = 49;
  public static final int OP_AUDIO_ALARM_VOLUME = 37;
  public static final int OP_AUDIO_BLUETOOTH_VOLUME = 39;
  public static final int OP_AUDIO_MASTER_VOLUME = 33;
  public static final int OP_AUDIO_MEDIA_VOLUME = 36;
  public static final int OP_AUDIO_NOTIFICATION_VOLUME = 38;
  public static final int OP_AUDIO_RING_VOLUME = 35;
  public static final int OP_AUDIO_VOICE_VOLUME = 34;
  public static final int OP_BLUETOOTH_ADMIN = 66;
  public static final int OP_BODY_SENSORS = 56;
  public static final int OP_CALL_PHONE = 13;
  public static final int OP_CAMERA = 26;
  public static final int OP_CHANGE_WIFI_STATE = 65;
  public static final int OP_COARSE_LOCATION = 0;
  public static final int OP_FINE_LOCATION = 1;
  public static final int OP_GAME_MODE_APP = 68;
  public static final int OP_GET_ACCOUNTS = 62;
  public static final int OP_GET_USAGE_STATS = 43;
  public static final int OP_GPS = 2;
  public static final int OP_LOCK_APP = 63;
  public static final int OP_MOCK_LOCATION = 58;
  public static final int OP_MONITOR_HIGH_POWER_LOCATION = 42;
  public static final int OP_MONITOR_LOCATION = 41;
  public static final int OP_MUTE_MICROPHONE = 44;
  public static final int OP_NEIGHBORING_CELLS = 12;
  public static final int OP_NONE = -1;
  public static final int OP_PLAY_AUDIO = 28;
  public static final int OP_POST_NOTIFICATION = 11;
  public static final int OP_PROCESS_OUTGOING_CALLS = 54;
  public static final int OP_PROJECT_MEDIA = 46;
  public static final int OP_READ_CALENDAR = 8;
  public static final int OP_READ_CALL_LOG = 6;
  public static final int OP_READ_CELL_BROADCASTS = 57;
  public static final int OP_READ_CLIPBOARD = 29;
  public static final int OP_READ_CONTACTS = 4;
  public static final int OP_READ_EXTERNAL_STORAGE = 59;
  public static final int OP_READ_ICC_SMS = 21;
  public static final int OP_READ_MODE_APP = 67;
  public static final int OP_READ_PHONE_STATE = 51;
  public static final int OP_READ_SMS = 14;
  public static final int OP_RECEIVE_EMERGECY_SMS = 17;
  public static final int OP_RECEIVE_MMS = 18;
  public static final int OP_RECEIVE_SMS = 16;
  public static final int OP_RECEIVE_WAP_PUSH = 19;
  public static final int OP_RECORD_AUDIO = 27;
  public static final int OP_RUN_IN_BACKGROUND = 64;
  public static final int OP_SEND_SMS = 20;
  public static final int OP_SYSTEM_ALERT_WINDOW = 24;
  public static final int OP_TAKE_AUDIO_FOCUS = 32;
  public static final int OP_TAKE_MEDIA_BUTTONS = 31;
  public static final int OP_TOAST_WINDOW = 45;
  public static final int OP_TURN_SCREEN_ON = 61;
  public static final int OP_USE_FINGERPRINT = 55;
  public static final int OP_USE_SIP = 53;
  public static final int OP_VIBRATE = 3;
  public static final int OP_WAKE_LOCK = 40;
  public static final int OP_WIFI_SCAN = 10;
  public static final int OP_WRITE_CALENDAR = 9;
  public static final int OP_WRITE_CALL_LOG = 7;
  public static final int OP_WRITE_CLIPBOARD = 30;
  public static final int OP_WRITE_CONTACTS = 5;
  public static final int OP_WRITE_EXTERNAL_STORAGE = 60;
  public static final int OP_WRITE_ICC_SMS = 22;
  public static final int OP_WRITE_SETTINGS = 23;
  public static final int OP_WRITE_SMS = 15;
  public static final int OP_WRITE_WALLPAPER = 48;
  private static final int[] RUNTIME_PERMISSIONS_OPS;
  public static final int _NUM_OP = 69;
  private static boolean[] sOpAllowSystemRestrictionBypass;
  private static int[] sOpDefaultMode;
  private static boolean[] sOpDisableReset;
  private static String[] sOpNames;
  private static String[] sOpPerms;
  private static String[] sOpRestrictions;
  private static HashMap<String, Integer> sOpStrToOp;
  private static String[] sOpToString;
  private static int[] sOpToSwitch;
  private static HashMap<String, Integer> sRuntimePermToOp;
  static IBinder sToken;
  final Context mContext;
  final ArrayMap<OnOpChangedListener, IAppOpsCallback> mModeWatchers = new ArrayMap();
  final IAppOpsService mService;
  
  static
  {
    int j = 0;
    RUNTIME_PERMISSIONS_OPS = new int[] { 4, 5, 62, 8, 9, 20, 16, 14, 19, 18, 57, 59, 60, 0, 1, 51, 13, 6, 7, 52, 53, 54, 27, 26, 56 };
    sOpToSwitch = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 11, 0, 13, 14, 15, 16, 16, 16, 16, 20, 14, 15, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 1, 1, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68 };
    sOpToString = new String[] { "android:coarse_location", "android:fine_location", null, null, "android:read_contacts", "android:write_contacts", "android:read_call_log", "android:write_call_log", "android:read_calendar", "android:write_calendar", null, null, null, "android:call_phone", "android:read_sms", null, "android:receive_sms", null, "android:receive_mms", "android:receive_wap_push", "android:send_sms", null, null, "android:write_settings", "android:system_alert_window", null, "android:camera", "android:record_audio", null, null, null, null, null, null, null, null, null, null, null, null, null, "android:monitor_location", "android:monitor_location_high_power", "android:get_usage_stats", null, null, null, "android:activate_vpn", null, null, null, "android:read_phone_state", "android:add_voicemail", "android:use_sip", "android:process_outgoing_calls", "android:use_fingerprint", "android:body_sensors", "android:read_cell_broadcasts", "android:mock_location", "android:read_external_storage", "android:write_external_storage", null, "android:get_accounts", null, null, "android:change_wifi_state", "android:bluetooth_admin", null, null };
    sOpNames = new String[] { "COARSE_LOCATION", "FINE_LOCATION", "GPS", "VIBRATE", "READ_CONTACTS", "WRITE_CONTACTS", "READ_CALL_LOG", "WRITE_CALL_LOG", "READ_CALENDAR", "WRITE_CALENDAR", "WIFI_SCAN", "POST_NOTIFICATION", "NEIGHBORING_CELLS", "CALL_PHONE", "READ_SMS", "WRITE_SMS", "RECEIVE_SMS", "RECEIVE_EMERGECY_SMS", "RECEIVE_MMS", "RECEIVE_WAP_PUSH", "SEND_SMS", "READ_ICC_SMS", "WRITE_ICC_SMS", "WRITE_SETTINGS", "SYSTEM_ALERT_WINDOW", "ACCESS_NOTIFICATIONS", "CAMERA", "RECORD_AUDIO", "PLAY_AUDIO", "READ_CLIPBOARD", "WRITE_CLIPBOARD", "TAKE_MEDIA_BUTTONS", "TAKE_AUDIO_FOCUS", "AUDIO_MASTER_VOLUME", "AUDIO_VOICE_VOLUME", "AUDIO_RING_VOLUME", "AUDIO_MEDIA_VOLUME", "AUDIO_ALARM_VOLUME", "AUDIO_NOTIFICATION_VOLUME", "AUDIO_BLUETOOTH_VOLUME", "WAKE_LOCK", "MONITOR_LOCATION", "MONITOR_HIGH_POWER_LOCATION", "GET_USAGE_STATS", "MUTE_MICROPHONE", "TOAST_WINDOW", "PROJECT_MEDIA", "ACTIVATE_VPN", "WRITE_WALLPAPER", "ASSIST_STRUCTURE", "ASSIST_SCREENSHOT", "OP_READ_PHONE_STATE", "ADD_VOICEMAIL", "USE_SIP", "PROCESS_OUTGOING_CALLS", "USE_FINGERPRINT", "BODY_SENSORS", "READ_CELL_BROADCASTS", "MOCK_LOCATION", "READ_EXTERNAL_STORAGE", "WRITE_EXTERNAL_STORAGE", "TURN_ON_SCREEN", "GET_ACCOUNTS", "LOCK_APP", "RUN_IN_BACKGROUND", "CHANGE_WIFI_STATE", "BLUETOOTH_ADMIN", "READ_MODE_APP", "GAME_MODE_APP" };
    sOpPerms = new String[] { "android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION", null, "android.permission.VIBRATE", "android.permission.READ_CONTACTS", "android.permission.WRITE_CONTACTS", "android.permission.READ_CALL_LOG", "android.permission.WRITE_CALL_LOG", "android.permission.READ_CALENDAR", "android.permission.WRITE_CALENDAR", "android.permission.ACCESS_WIFI_STATE", null, null, "android.permission.CALL_PHONE", "android.permission.READ_SMS", null, "android.permission.RECEIVE_SMS", "android.permission.RECEIVE_EMERGENCY_BROADCAST", "android.permission.RECEIVE_MMS", "android.permission.RECEIVE_WAP_PUSH", "android.permission.SEND_SMS", "android.permission.READ_SMS", null, "android.permission.WRITE_SETTINGS", "android.permission.SYSTEM_ALERT_WINDOW", "android.permission.ACCESS_NOTIFICATIONS", "android.permission.CAMERA", "android.permission.RECORD_AUDIO", null, null, null, null, null, null, null, null, null, null, null, null, "android.permission.WAKE_LOCK", null, null, "android.permission.PACKAGE_USAGE_STATS", null, null, null, null, null, null, null, "android.permission.READ_PHONE_STATE", "com.android.voicemail.permission.ADD_VOICEMAIL", "android.permission.USE_SIP", "android.permission.PROCESS_OUTGOING_CALLS", "android.permission.USE_FINGERPRINT", "android.permission.BODY_SENSORS", "android.permission.READ_CELL_BROADCASTS", null, "android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE", null, "android.permission.GET_ACCOUNTS", null, null, "android.permission.CHANGE_WIFI_STATE", "android.permission.BLUETOOTH_ADMIN", null, null };
    sOpRestrictions = new String[] { "no_share_location", "no_share_location", "no_share_location", null, null, null, "no_outgoing_calls", "no_outgoing_calls", null, null, "no_share_location", null, null, null, "no_sms", "no_sms", "no_sms", null, "no_sms", null, "no_sms", "no_sms", "no_sms", null, "no_create_windows", null, "no_camera", "no_record_audio", null, null, null, null, null, "no_adjust_volume", "no_adjust_volume", "no_adjust_volume", "no_adjust_volume", "no_adjust_volume", "no_adjust_volume", "no_adjust_volume", null, "no_share_location", "no_share_location", null, "no_unmute_microphone", "no_create_windows", null, null, "no_wallpaper", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null };
    sOpAllowSystemRestrictionBypass = new boolean[] { 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
    sOpDefaultMode = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 3, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1 };
    sOpDisableReset = new boolean[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1 };
    sOpStrToOp = new HashMap();
    sRuntimePermToOp = new HashMap();
    if (sOpToSwitch.length != 69) {
      throw new IllegalStateException("sOpToSwitch length " + sOpToSwitch.length + " should be " + 69);
    }
    if (sOpToString.length != 69) {
      throw new IllegalStateException("sOpToString length " + sOpToString.length + " should be " + 69);
    }
    if (sOpNames.length != 69) {
      throw new IllegalStateException("sOpNames length " + sOpNames.length + " should be " + 69);
    }
    if (sOpPerms.length != 69) {
      throw new IllegalStateException("sOpPerms length " + sOpPerms.length + " should be " + 69);
    }
    if (sOpDefaultMode.length != 69) {
      throw new IllegalStateException("sOpDefaultMode length " + sOpDefaultMode.length + " should be " + 69);
    }
    if (sOpDisableReset.length != 69) {
      throw new IllegalStateException("sOpDisableReset length " + sOpDisableReset.length + " should be " + 69);
    }
    if (sOpRestrictions.length != 69) {
      throw new IllegalStateException("sOpRestrictions length " + sOpRestrictions.length + " should be " + 69);
    }
    if (sOpAllowSystemRestrictionBypass.length != 69) {
      throw new IllegalStateException("sOpAllowSYstemRestrictionsBypass length " + sOpRestrictions.length + " should be " + 69);
    }
    int i = 0;
    while (i < 69)
    {
      if (sOpToString[i] != null) {
        sOpStrToOp.put(sOpToString[i], Integer.valueOf(i));
      }
      i += 1;
    }
    int[] arrayOfInt = RUNTIME_PERMISSIONS_OPS;
    int k = arrayOfInt.length;
    i = j;
    while (i < k)
    {
      j = arrayOfInt[i];
      if (sOpPerms[j] != null) {
        sRuntimePermToOp.put(sOpPerms[j], Integer.valueOf(j));
      }
      i += 1;
    }
  }
  
  AppOpsManager(Context paramContext, IAppOpsService paramIAppOpsService)
  {
    this.mContext = paramContext;
    this.mService = paramIAppOpsService;
  }
  
  private String buildSecurityExceptionMsg(int paramInt1, int paramInt2, String paramString)
  {
    return paramString + " from uid " + paramInt2 + " not allowed to perform " + sOpNames[paramInt1];
  }
  
  /* Error */
  public static IBinder getToken(IAppOpsService paramIAppOpsService)
  {
    // Byte code:
    //   0: ldc 2
    //   2: monitorenter
    //   3: getstatic 631	android/app/AppOpsManager:sToken	Landroid/os/IBinder;
    //   6: ifnull +12 -> 18
    //   9: getstatic 631	android/app/AppOpsManager:sToken	Landroid/os/IBinder;
    //   12: astore_0
    //   13: ldc 2
    //   15: monitorexit
    //   16: aload_0
    //   17: areturn
    //   18: aload_0
    //   19: new 633	android/os/Binder
    //   22: dup
    //   23: invokespecial 634	android/os/Binder:<init>	()V
    //   26: invokeinterface 639 2 0
    //   31: putstatic 631	android/app/AppOpsManager:sToken	Landroid/os/IBinder;
    //   34: getstatic 631	android/app/AppOpsManager:sToken	Landroid/os/IBinder;
    //   37: astore_0
    //   38: ldc 2
    //   40: monitorexit
    //   41: aload_0
    //   42: areturn
    //   43: astore_0
    //   44: aload_0
    //   45: invokevirtual 643	android/os/RemoteException:rethrowFromSystemServer	()Ljava/lang/RuntimeException;
    //   48: athrow
    //   49: astore_0
    //   50: ldc 2
    //   52: monitorexit
    //   53: aload_0
    //   54: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	55	0	paramIAppOpsService	IAppOpsService
    // Exception table:
    //   from	to	target	type
    //   18	34	43	android/os/RemoteException
    //   3	13	49	finally
    //   18	34	49	finally
    //   34	38	49	finally
    //   44	49	49	finally
  }
  
  public static boolean opAllowSystemBypassRestriction(int paramInt)
  {
    return sOpAllowSystemRestrictionBypass[paramInt];
  }
  
  public static boolean opAllowsReset(int paramInt)
  {
    return sOpDisableReset[paramInt] == 0;
  }
  
  public static int opToDefaultMode(int paramInt)
  {
    return sOpDefaultMode[paramInt];
  }
  
  public static String opToName(int paramInt)
  {
    if (paramInt == -1) {
      return "NONE";
    }
    if (paramInt < sOpNames.length) {
      return sOpNames[paramInt];
    }
    return "Unknown(" + paramInt + ")";
  }
  
  public static String opToPermission(int paramInt)
  {
    return sOpPerms[paramInt];
  }
  
  public static String opToRestriction(int paramInt)
  {
    return sOpRestrictions[paramInt];
  }
  
  public static int opToSwitch(int paramInt)
  {
    return sOpToSwitch[paramInt];
  }
  
  public static String permissionToOp(String paramString)
  {
    paramString = (Integer)sRuntimePermToOp.get(paramString);
    if (paramString == null) {
      return null;
    }
    return sOpToString[paramString.intValue()];
  }
  
  public static int permissionToOpCode(String paramString)
  {
    paramString = (Integer)sRuntimePermToOp.get(paramString);
    if (paramString != null) {
      return paramString.intValue();
    }
    return -1;
  }
  
  public static int strDebugOpToOp(String paramString)
  {
    int i = 0;
    while (i < sOpNames.length)
    {
      if (sOpNames[i].equals(paramString)) {
        return i;
      }
      i += 1;
    }
    throw new IllegalArgumentException("Unknown operation string: " + paramString);
  }
  
  public static int strOpToOp(String paramString)
  {
    Integer localInteger = (Integer)sOpStrToOp.get(paramString);
    if (localInteger == null) {
      throw new IllegalArgumentException("Unknown operation string: " + paramString);
    }
    return localInteger.intValue();
  }
  
  public int checkAudioOp(int paramInt1, int paramInt2, int paramInt3, String paramString)
  {
    try
    {
      paramInt2 = this.mService.checkAudioOperation(paramInt1, paramInt2, paramInt3, paramString);
      if (paramInt2 == 2) {
        throw new SecurityException(buildSecurityExceptionMsg(paramInt1, paramInt3, paramString));
      }
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
    return paramInt2;
  }
  
  public int checkAudioOpNoThrow(int paramInt1, int paramInt2, int paramInt3, String paramString)
  {
    try
    {
      paramInt1 = this.mService.checkAudioOperation(paramInt1, paramInt2, paramInt3, paramString);
      return paramInt1;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public int checkOp(int paramInt1, int paramInt2, String paramString)
  {
    int i;
    try
    {
      i = this.mService.checkOperation(paramInt1, paramInt2, paramString);
      if (i == 2) {
        throw new SecurityException(buildSecurityExceptionMsg(paramInt1, paramInt2, paramString));
      }
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
    return i;
  }
  
  public int checkOp(String paramString1, int paramInt, String paramString2)
  {
    return checkOp(strOpToOp(paramString1), paramInt, paramString2);
  }
  
  public int checkOpNoThrow(int paramInt1, int paramInt2, String paramString)
  {
    try
    {
      paramInt1 = this.mService.checkOperation(paramInt1, paramInt2, paramString);
      return paramInt1;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public int checkOpNoThrow(String paramString1, int paramInt, String paramString2)
  {
    return checkOpNoThrow(strOpToOp(paramString1), paramInt, paramString2);
  }
  
  public void checkPackage(int paramInt, String paramString)
  {
    try
    {
      if (this.mService.checkPackage(paramInt, paramString) != 0) {
        throw new SecurityException("Package " + paramString + " does not belong to " + paramInt);
      }
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public void finishOp(int paramInt)
  {
    finishOp(paramInt, Process.myUid(), this.mContext.getOpPackageName());
  }
  
  public void finishOp(int paramInt1, int paramInt2, String paramString)
  {
    try
    {
      this.mService.finishOperation(getToken(this.mService), paramInt1, paramInt2, paramString);
      return;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public void finishOp(String paramString1, int paramInt, String paramString2)
  {
    finishOp(strOpToOp(paramString1), paramInt, paramString2);
  }
  
  public List<PackageOps> getOpsForPackage(int paramInt, String paramString, int[] paramArrayOfInt)
  {
    try
    {
      paramString = this.mService.getOpsForPackage(paramInt, paramString, paramArrayOfInt);
      return paramString;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public List<PackageOps> getPackagesForOps(int[] paramArrayOfInt)
  {
    try
    {
      paramArrayOfInt = this.mService.getPackagesForOps(paramArrayOfInt);
      return paramArrayOfInt;
    }
    catch (RemoteException paramArrayOfInt)
    {
      throw paramArrayOfInt.rethrowFromSystemServer();
    }
  }
  
  public int noteOp(int paramInt)
  {
    return noteOp(paramInt, Process.myUid(), this.mContext.getOpPackageName());
  }
  
  public int noteOp(int paramInt1, int paramInt2, String paramString)
  {
    int i;
    try
    {
      i = this.mService.noteOperation(paramInt1, paramInt2, paramString);
      if (i == 2) {
        throw new SecurityException(buildSecurityExceptionMsg(paramInt1, paramInt2, paramString));
      }
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
    return i;
  }
  
  public int noteOp(String paramString1, int paramInt, String paramString2)
  {
    return noteOp(strOpToOp(paramString1), paramInt, paramString2);
  }
  
  public int noteOpNoThrow(int paramInt1, int paramInt2, String paramString)
  {
    try
    {
      paramInt1 = this.mService.noteOperation(paramInt1, paramInt2, paramString);
      return paramInt1;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public int noteOpNoThrow(String paramString1, int paramInt, String paramString2)
  {
    return noteOpNoThrow(strOpToOp(paramString1), paramInt, paramString2);
  }
  
  public int noteProxyOp(int paramInt, String paramString)
  {
    int i = noteProxyOpNoThrow(paramInt, paramString);
    if (i == 2) {
      throw new SecurityException("Proxy package " + this.mContext.getOpPackageName() + " from uid " + Process.myUid() + " or calling package " + paramString + " from uid " + Binder.getCallingUid() + " not allowed to perform " + sOpNames[paramInt]);
    }
    return i;
  }
  
  public int noteProxyOp(String paramString1, String paramString2)
  {
    return noteProxyOp(strOpToOp(paramString1), paramString2);
  }
  
  public int noteProxyOpNoThrow(int paramInt, String paramString)
  {
    try
    {
      paramInt = this.mService.noteProxyOperation(paramInt, this.mContext.getOpPackageName(), Binder.getCallingUid(), paramString);
      return paramInt;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public int noteProxyOpNoThrow(String paramString1, String paramString2)
  {
    return noteProxyOpNoThrow(strOpToOp(paramString1), paramString2);
  }
  
  public void resetAllModes()
  {
    try
    {
      this.mService.resetAllModes(UserHandle.myUserId(), null);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void setMode(int paramInt1, int paramInt2, String paramString, int paramInt3)
  {
    try
    {
      if (Build.DEBUG_ONEPLUS) {
        Slog.d("AppOpsManager", "[scene]  setMode code=" + paramInt1 + ", uid=" + paramInt2 + ", packageName=" + paramString + ", mode=" + paramInt3 + " Callers=" + Debug.getCallers(6));
      }
      this.mService.setMode(paramInt1, paramInt2, paramString, paramInt3);
      return;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public void setRestriction(int paramInt1, int paramInt2, int paramInt3, String[] paramArrayOfString)
  {
    try
    {
      int i = Binder.getCallingUid();
      this.mService.setAudioRestriction(paramInt1, paramInt2, i, paramInt3, paramArrayOfString);
      return;
    }
    catch (RemoteException paramArrayOfString)
    {
      throw paramArrayOfString.rethrowFromSystemServer();
    }
  }
  
  public void setUidMode(int paramInt1, int paramInt2, int paramInt3)
  {
    try
    {
      if (Build.DEBUG_ONEPLUS) {
        Slog.d("AppOpsManager", "[scene]  setUidMode code=" + paramInt1 + ", uid=" + paramInt2 + ", mode=" + paramInt3 + " Callers=" + Debug.getCallers(6));
      }
      this.mService.setUidMode(paramInt1, paramInt2, paramInt3);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void setUidMode(String paramString, int paramInt1, int paramInt2)
  {
    try
    {
      if (Build.DEBUG_ONEPLUS) {
        Slog.d("AppOpsManager", "[scene]  setUidMode appOp=" + paramString + ", uid=" + paramInt1 + ", mode=" + paramInt2 + " Callers=" + Debug.getCallers(6));
      }
      this.mService.setUidMode(strOpToOp(paramString), paramInt1, paramInt2);
      return;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public void setUserRestriction(int paramInt, boolean paramBoolean, IBinder paramIBinder)
  {
    setUserRestriction(paramInt, paramBoolean, paramIBinder, null);
  }
  
  public void setUserRestriction(int paramInt, boolean paramBoolean, IBinder paramIBinder, String[] paramArrayOfString)
  {
    setUserRestrictionForUser(paramInt, paramBoolean, paramIBinder, paramArrayOfString, this.mContext.getUserId());
  }
  
  public void setUserRestrictionForUser(int paramInt1, boolean paramBoolean, IBinder paramIBinder, String[] paramArrayOfString, int paramInt2)
  {
    try
    {
      this.mService.setUserRestriction(paramInt1, paramBoolean, paramIBinder, paramInt2, paramArrayOfString);
      return;
    }
    catch (RemoteException paramIBinder)
    {
      throw paramIBinder.rethrowFromSystemServer();
    }
  }
  
  public int startOp(int paramInt)
  {
    return startOp(paramInt, Process.myUid(), this.mContext.getOpPackageName());
  }
  
  public int startOp(int paramInt1, int paramInt2, String paramString)
  {
    int i;
    try
    {
      i = this.mService.startOperation(getToken(this.mService), paramInt1, paramInt2, paramString);
      if (i == 2) {
        throw new SecurityException(buildSecurityExceptionMsg(paramInt1, paramInt2, paramString));
      }
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
    return i;
  }
  
  public int startOp(String paramString1, int paramInt, String paramString2)
  {
    return startOp(strOpToOp(paramString1), paramInt, paramString2);
  }
  
  public int startOpNoThrow(int paramInt1, int paramInt2, String paramString)
  {
    try
    {
      paramInt1 = this.mService.startOperation(getToken(this.mService), paramInt1, paramInt2, paramString);
      return paramInt1;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public int startOpNoThrow(String paramString1, int paramInt, String paramString2)
  {
    return startOpNoThrow(strOpToOp(paramString1), paramInt, paramString2);
  }
  
  public void startWatchingMode(int paramInt, String paramString, final OnOpChangedListener paramOnOpChangedListener)
  {
    synchronized (this.mModeWatchers)
    {
      IAppOpsCallback localIAppOpsCallback = (IAppOpsCallback)this.mModeWatchers.get(paramOnOpChangedListener);
      Object localObject = localIAppOpsCallback;
      if (localIAppOpsCallback == null)
      {
        localObject = new IAppOpsCallback.Stub()
        {
          public void opChanged(int paramAnonymousInt1, int paramAnonymousInt2, String paramAnonymousString)
          {
            if ((paramOnOpChangedListener instanceof AppOpsManager.OnOpChangedInternalListener)) {
              ((AppOpsManager.OnOpChangedInternalListener)paramOnOpChangedListener).onOpChanged(paramAnonymousInt1, paramAnonymousString);
            }
            if (AppOpsManager.-get0()[paramAnonymousInt1] != null) {
              paramOnOpChangedListener.onOpChanged(AppOpsManager.-get0()[paramAnonymousInt1], paramAnonymousString);
            }
          }
        };
        this.mModeWatchers.put(paramOnOpChangedListener, localObject);
      }
      try
      {
        this.mService.startWatchingMode(paramInt, paramString, (IAppOpsCallback)localObject);
        return;
      }
      catch (RemoteException paramString)
      {
        throw paramString.rethrowFromSystemServer();
      }
    }
  }
  
  public void startWatchingMode(String paramString1, String paramString2, OnOpChangedListener paramOnOpChangedListener)
  {
    startWatchingMode(strOpToOp(paramString1), paramString2, paramOnOpChangedListener);
  }
  
  public void stopWatchingMode(OnOpChangedListener paramOnOpChangedListener)
  {
    synchronized (this.mModeWatchers)
    {
      paramOnOpChangedListener = (IAppOpsCallback)this.mModeWatchers.get(paramOnOpChangedListener);
      if (paramOnOpChangedListener != null) {}
      try
      {
        this.mService.stopWatchingMode(paramOnOpChangedListener);
        return;
      }
      catch (RemoteException paramOnOpChangedListener)
      {
        throw paramOnOpChangedListener.rethrowFromSystemServer();
      }
    }
  }
  
  public static class OnOpChangedInternalListener
    implements AppOpsManager.OnOpChangedListener
  {
    public void onOpChanged(int paramInt, String paramString) {}
    
    public void onOpChanged(String paramString1, String paramString2) {}
  }
  
  public static abstract interface OnOpChangedListener
  {
    public abstract void onOpChanged(String paramString1, String paramString2);
  }
  
  public static class OpEntry
    implements Parcelable
  {
    public static final Parcelable.Creator<OpEntry> CREATOR = new Parcelable.Creator()
    {
      public AppOpsManager.OpEntry createFromParcel(Parcel paramAnonymousParcel)
      {
        return new AppOpsManager.OpEntry(paramAnonymousParcel);
      }
      
      public AppOpsManager.OpEntry[] newArray(int paramAnonymousInt)
      {
        return new AppOpsManager.OpEntry[paramAnonymousInt];
      }
    };
    private final int mDuration;
    private final int mMode;
    private final int mOp;
    private final String mProxyPackageName;
    private final int mProxyUid;
    private final long mRejectTime;
    private final long mTime;
    
    public OpEntry(int paramInt1, int paramInt2, long paramLong1, long paramLong2, int paramInt3, int paramInt4, String paramString)
    {
      this.mOp = paramInt1;
      this.mMode = paramInt2;
      this.mTime = paramLong1;
      this.mRejectTime = paramLong2;
      this.mDuration = paramInt3;
      this.mProxyUid = paramInt4;
      this.mProxyPackageName = paramString;
    }
    
    OpEntry(Parcel paramParcel)
    {
      this.mOp = paramParcel.readInt();
      this.mMode = paramParcel.readInt();
      this.mTime = paramParcel.readLong();
      this.mRejectTime = paramParcel.readLong();
      this.mDuration = paramParcel.readInt();
      this.mProxyUid = paramParcel.readInt();
      this.mProxyPackageName = paramParcel.readString();
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public int getDuration()
    {
      if (this.mDuration == -1) {
        return (int)(System.currentTimeMillis() - this.mTime);
      }
      return this.mDuration;
    }
    
    public int getMode()
    {
      return this.mMode;
    }
    
    public int getOp()
    {
      return this.mOp;
    }
    
    public String getProxyPackageName()
    {
      return this.mProxyPackageName;
    }
    
    public int getProxyUid()
    {
      return this.mProxyUid;
    }
    
    public long getRejectTime()
    {
      return this.mRejectTime;
    }
    
    public long getTime()
    {
      return this.mTime;
    }
    
    public boolean isRunning()
    {
      return this.mDuration == -1;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeInt(this.mOp);
      paramParcel.writeInt(this.mMode);
      paramParcel.writeLong(this.mTime);
      paramParcel.writeLong(this.mRejectTime);
      paramParcel.writeInt(this.mDuration);
      paramParcel.writeInt(this.mProxyUid);
      paramParcel.writeString(this.mProxyPackageName);
    }
  }
  
  public static class PackageOps
    implements Parcelable
  {
    public static final Parcelable.Creator<PackageOps> CREATOR = new Parcelable.Creator()
    {
      public AppOpsManager.PackageOps createFromParcel(Parcel paramAnonymousParcel)
      {
        return new AppOpsManager.PackageOps(paramAnonymousParcel);
      }
      
      public AppOpsManager.PackageOps[] newArray(int paramAnonymousInt)
      {
        return new AppOpsManager.PackageOps[paramAnonymousInt];
      }
    };
    private final List<AppOpsManager.OpEntry> mEntries;
    private final String mPackageName;
    private final int mUid;
    
    PackageOps(Parcel paramParcel)
    {
      this.mPackageName = paramParcel.readString();
      this.mUid = paramParcel.readInt();
      this.mEntries = new ArrayList();
      int j = paramParcel.readInt();
      int i = 0;
      while (i < j)
      {
        this.mEntries.add((AppOpsManager.OpEntry)AppOpsManager.OpEntry.CREATOR.createFromParcel(paramParcel));
        i += 1;
      }
    }
    
    public PackageOps(String paramString, int paramInt, List<AppOpsManager.OpEntry> paramList)
    {
      this.mPackageName = paramString;
      this.mUid = paramInt;
      this.mEntries = paramList;
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public List<AppOpsManager.OpEntry> getOps()
    {
      return this.mEntries;
    }
    
    public String getPackageName()
    {
      return this.mPackageName;
    }
    
    public int getUid()
    {
      return this.mUid;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeString(this.mPackageName);
      paramParcel.writeInt(this.mUid);
      paramParcel.writeInt(this.mEntries.size());
      int i = 0;
      while (i < this.mEntries.size())
      {
        ((AppOpsManager.OpEntry)this.mEntries.get(i)).writeToParcel(paramParcel, paramInt);
        i += 1;
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/AppOpsManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */