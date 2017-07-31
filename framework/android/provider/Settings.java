package android.provider;

import android.app.ActivityThread;
import android.app.AppOpsManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.IContentProvider;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.Bundle;
import android.os.LocaleList;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.AndroidException;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import android.util.MemoryIntArray;
import android.util.SeempLog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.util.ArrayUtils;
import com.android.internal.widget.ILockSettings;
import com.android.internal.widget.ILockSettings.Stub;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class Settings
{
  public static final String ACTION_ACCESSIBILITY_SETTINGS = "android.settings.ACCESSIBILITY_SETTINGS";
  public static final String ACTION_ADD_ACCOUNT = "android.settings.ADD_ACCOUNT_SETTINGS";
  public static final String ACTION_AIRPLANE_MODE_SETTINGS = "android.settings.AIRPLANE_MODE_SETTINGS";
  public static final String ACTION_APN_SETTINGS = "android.settings.APN_SETTINGS";
  public static final String ACTION_APPLICATION_DETAILS_SETTINGS = "android.settings.APPLICATION_DETAILS_SETTINGS";
  public static final String ACTION_APPLICATION_DEVELOPMENT_SETTINGS = "android.settings.APPLICATION_DEVELOPMENT_SETTINGS";
  public static final String ACTION_APPLICATION_SETTINGS = "android.settings.APPLICATION_SETTINGS";
  public static final String ACTION_APP_NOTIFICATION_REDACTION = "android.settings.ACTION_APP_NOTIFICATION_REDACTION";
  public static final String ACTION_APP_NOTIFICATION_SETTINGS = "android.settings.APP_NOTIFICATION_SETTINGS";
  public static final String ACTION_APP_OPS_SETTINGS = "android.settings.APP_OPS_SETTINGS";
  public static final String ACTION_BATTERY_SAVER_SETTINGS = "android.settings.BATTERY_SAVER_SETTINGS";
  public static final String ACTION_BLUETOOTH_APTX_HD = "android.settings.BLUETOOTH_APTX_HD";
  public static final String ACTION_BLUETOOTH_SETTINGS = "android.settings.BLUETOOTH_SETTINGS";
  public static final String ACTION_CAPTIONING_SETTINGS = "android.settings.CAPTIONING_SETTINGS";
  public static final String ACTION_CAST_SETTINGS = "android.settings.CAST_SETTINGS";
  public static final String ACTION_CONDITION_PROVIDER_SETTINGS = "android.settings.ACTION_CONDITION_PROVIDER_SETTINGS";
  public static final String ACTION_DATA_ROAMING_SETTINGS = "android.settings.DATA_ROAMING_SETTINGS";
  public static final String ACTION_DATE_SETTINGS = "android.settings.DATE_SETTINGS";
  public static final String ACTION_DEVICE_INFO_SETTINGS = "android.settings.DEVICE_INFO_SETTINGS";
  public static final String ACTION_DISPLAY_SETTINGS = "android.settings.DISPLAY_SETTINGS";
  public static final String ACTION_DREAM_SETTINGS = "android.settings.DREAM_SETTINGS";
  public static final String ACTION_HARD_KEYBOARD_SETTINGS = "android.settings.HARD_KEYBOARD_SETTINGS";
  public static final String ACTION_HOME_SETTINGS = "android.settings.HOME_SETTINGS";
  public static final String ACTION_IGNORE_BACKGROUND_DATA_RESTRICTIONS_SETTINGS = "android.settings.IGNORE_BACKGROUND_DATA_RESTRICTIONS_SETTINGS";
  public static final String ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS = "android.settings.IGNORE_BATTERY_OPTIMIZATION_SETTINGS";
  public static final String ACTION_INPUT_METHOD_SETTINGS = "android.settings.INPUT_METHOD_SETTINGS";
  public static final String ACTION_INPUT_METHOD_SUBTYPE_SETTINGS = "android.settings.INPUT_METHOD_SUBTYPE_SETTINGS";
  public static final String ACTION_INTERNAL_STORAGE_SETTINGS = "android.settings.INTERNAL_STORAGE_SETTINGS";
  public static final String ACTION_LOCALE_SETTINGS = "android.settings.LOCALE_SETTINGS";
  public static final String ACTION_LOCATION_SOURCE_SETTINGS = "android.settings.LOCATION_SOURCE_SETTINGS";
  public static final String ACTION_MANAGE_ALL_APPLICATIONS_SETTINGS = "android.settings.MANAGE_ALL_APPLICATIONS_SETTINGS";
  public static final String ACTION_MANAGE_APPLICATIONS_SETTINGS = "android.settings.MANAGE_APPLICATIONS_SETTINGS";
  public static final String ACTION_MANAGE_DEFAULT_APPS_SETTINGS = "android.settings.MANAGE_DEFAULT_APPS_SETTINGS";
  public static final String ACTION_MANAGE_OVERLAY_PERMISSION = "android.settings.action.MANAGE_OVERLAY_PERMISSION";
  public static final String ACTION_MANAGE_WRITE_SETTINGS = "android.settings.action.MANAGE_WRITE_SETTINGS";
  public static final String ACTION_MEMORY_CARD_SETTINGS = "android.settings.MEMORY_CARD_SETTINGS";
  public static final String ACTION_MONITORING_CERT_INFO = "com.android.settings.MONITORING_CERT_INFO";
  public static final String ACTION_NETWORK_OPERATOR_SETTINGS = "android.settings.NETWORK_OPERATOR_SETTINGS";
  public static final String ACTION_NFCSHARING_SETTINGS = "android.settings.NFCSHARING_SETTINGS";
  public static final String ACTION_NFC_PAYMENT_SETTINGS = "android.settings.NFC_PAYMENT_SETTINGS";
  public static final String ACTION_NFC_SETTINGS = "android.settings.NFC_SETTINGS";
  public static final String ACTION_NIGHT_DISPLAY_SETTINGS = "android.settings.NIGHT_DISPLAY_SETTINGS";
  public static final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";
  public static final String ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS = "android.settings.NOTIFICATION_POLICY_ACCESS_SETTINGS";
  public static final String ACTION_NOTIFICATION_SETTINGS = "android.settings.NOTIFICATION_SETTINGS";
  public static final String ACTION_OEM_BLUETOOTH_SETTINGS = "android.settings.OEM_BLUETOOTH_SETTINGS";
  public static final String ACTION_OEM_WIFI_SETTINGS = "android.settings.OEM_WIFI_SETTINGS";
  public static final String ACTION_PAIRING_SETTINGS = "android.settings.PAIRING_SETTINGS";
  public static final String ACTION_PRINT_SETTINGS = "android.settings.ACTION_PRINT_SETTINGS";
  public static final String ACTION_PRIVACY_SETTINGS = "android.settings.PRIVACY_SETTINGS";
  public static final String ACTION_QUICK_LAUNCH_SETTINGS = "android.settings.QUICK_LAUNCH_SETTINGS";
  public static final String ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS = "android.settings.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS";
  public static final String ACTION_SEARCH_SETTINGS = "android.search.action.SEARCH_SETTINGS";
  public static final String ACTION_SECURITY_SETTINGS = "android.settings.SECURITY_SETTINGS";
  public static final String ACTION_SETTINGS = "android.settings.SETTINGS";
  public static final String ACTION_SHOW_ADMIN_SUPPORT_DETAILS = "android.settings.SHOW_ADMIN_SUPPORT_DETAILS";
  public static final String ACTION_SHOW_INPUT_METHOD_PICKER = "android.settings.SHOW_INPUT_METHOD_PICKER";
  public static final String ACTION_SHOW_REGULATORY_INFO = "android.settings.SHOW_REGULATORY_INFO";
  public static final String ACTION_SHOW_REMOTE_BUGREPORT_DIALOG = "android.settings.SHOW_REMOTE_BUGREPORT_DIALOG";
  public static final String ACTION_SOUND_SETTINGS = "android.settings.SOUND_SETTINGS";
  public static final String ACTION_STORAGE_MANAGER_SETTINGS = "android.settings.STORAGE_MANAGER_SETTINGS";
  public static final String ACTION_SYNC_SETTINGS = "android.settings.SYNC_SETTINGS";
  public static final String ACTION_SYSTEM_UPDATE_SETTINGS = "android.settings.SYSTEM_UPDATE_SETTINGS";
  public static final String ACTION_TETHER_PROVISIONING = "android.settings.TETHER_PROVISIONING_UI";
  public static final String ACTION_TRUSTED_CREDENTIALS_USER = "com.android.settings.TRUSTED_CREDENTIALS_USER";
  public static final String ACTION_USAGE_ACCESS_SETTINGS = "android.settings.USAGE_ACCESS_SETTINGS";
  public static final String ACTION_USER_DICTIONARY_INSERT = "com.android.settings.USER_DICTIONARY_INSERT";
  public static final String ACTION_USER_DICTIONARY_SETTINGS = "android.settings.USER_DICTIONARY_SETTINGS";
  public static final String ACTION_USER_SETTINGS = "android.settings.USER_SETTINGS";
  public static final String ACTION_VOICE_CONTROL_AIRPLANE_MODE = "android.settings.VOICE_CONTROL_AIRPLANE_MODE";
  public static final String ACTION_VOICE_CONTROL_BATTERY_SAVER_MODE = "android.settings.VOICE_CONTROL_BATTERY_SAVER_MODE";
  public static final String ACTION_VOICE_CONTROL_DO_NOT_DISTURB_MODE = "android.settings.VOICE_CONTROL_DO_NOT_DISTURB_MODE";
  public static final String ACTION_VOICE_INPUT_SETTINGS = "android.settings.VOICE_INPUT_SETTINGS";
  public static final String ACTION_VPN_SETTINGS = "android.settings.VPN_SETTINGS";
  public static final String ACTION_VR_LISTENER_SETTINGS = "android.settings.VR_LISTENER_SETTINGS";
  public static final String ACTION_WEBVIEW_SETTINGS = "android.settings.WEBVIEW_SETTINGS";
  public static final String ACTION_WIFI_IP_SETTINGS = "android.settings.WIFI_IP_SETTINGS";
  public static final String ACTION_WIFI_SETTINGS = "android.settings.WIFI_SETTINGS";
  public static final String ACTION_WIRELESS_SETTINGS = "android.settings.WIRELESS_SETTINGS";
  public static final String ACTION_ZEN_MODE_AUTOMATION_SETTINGS = "android.settings.ZEN_MODE_AUTOMATION_SETTINGS";
  public static final String ACTION_ZEN_MODE_EVENT_RULE_SETTINGS = "android.settings.ZEN_MODE_EVENT_RULE_SETTINGS";
  public static final String ACTION_ZEN_MODE_EXTERNAL_RULE_SETTINGS = "android.settings.ZEN_MODE_EXTERNAL_RULE_SETTINGS";
  public static final String ACTION_ZEN_MODE_PRIORITY_SETTINGS = "android.settings.ZEN_MODE_PRIORITY_SETTINGS";
  public static final String ACTION_ZEN_MODE_SCHEDULE_RULE_SETTINGS = "android.settings.ZEN_MODE_SCHEDULE_RULE_SETTINGS";
  public static final String ACTION_ZEN_MODE_SETTINGS = "android.settings.ZEN_MODE_SETTINGS";
  public static final String AUTHORITY = "settings";
  public static final String CALL_METHOD_GENERATION_INDEX_KEY = "_generation_index";
  public static final String CALL_METHOD_GENERATION_KEY = "_generation";
  public static final String CALL_METHOD_GET_GLOBAL = "GET_global";
  public static final String CALL_METHOD_GET_SECURE = "GET_secure";
  public static final String CALL_METHOD_GET_SYSTEM = "GET_system";
  public static final String CALL_METHOD_PUT_GLOBAL = "PUT_global";
  public static final String CALL_METHOD_PUT_SECURE = "PUT_secure";
  public static final String CALL_METHOD_PUT_SYSTEM = "PUT_system";
  public static final String CALL_METHOD_TRACK_GENERATION_KEY = "_track_generation";
  public static final String CALL_METHOD_USER_KEY = "_user";
  public static final String DEVICE_NAME_SETTINGS = "android.settings.DEVICE_NAME";
  public static final String EXTRA_ACCOUNT_TYPES = "account_types";
  public static final String EXTRA_AIRPLANE_MODE_ENABLED = "airplane_mode_enabled";
  public static final String EXTRA_APP_PACKAGE = "app_package";
  public static final String EXTRA_APP_UID = "app_uid";
  public static final String EXTRA_AUTHORITIES = "authorities";
  public static final String EXTRA_BATTERY_SAVER_MODE_ENABLED = "android.settings.extra.battery_saver_mode_enabled";
  public static final String EXTRA_DO_NOT_DISTURB_MODE_ENABLED = "android.settings.extra.do_not_disturb_mode_enabled";
  public static final String EXTRA_DO_NOT_DISTURB_MODE_MINUTES = "android.settings.extra.do_not_disturb_mode_minutes";
  public static final String EXTRA_INPUT_DEVICE_IDENTIFIER = "input_device_identifier";
  public static final String EXTRA_INPUT_METHOD_ID = "input_method_id";
  public static final String EXTRA_NUMBER_OF_CERTIFICATES = "android.settings.extra.number_of_certificates";
  public static final String INTENT_CATEGORY_USAGE_ACCESS_CONFIG = "android.intent.category.USAGE_ACCESS_CONFIG";
  private static final String JID_RESOURCE_PREFIX = "android";
  private static final boolean LOCAL_LOGV = false;
  public static final String METADATA_USAGE_ACCESS_REASON = "android.settings.metadata.USAGE_ACCESS_REASON";
  private static final String[] PM_CHANGE_NETWORK_STATE = { "android.permission.CHANGE_NETWORK_STATE", "android.permission.WRITE_SETTINGS" };
  private static final String[] PM_SYSTEM_ALERT_WINDOW = { "android.permission.SYSTEM_ALERT_WINDOW" };
  private static final String[] PM_WRITE_SETTINGS;
  private static final String TAG = "Settings";
  private static final Object mLocationSettingsLock = new Object();
  
  static
  {
    PM_WRITE_SETTINGS = new String[] { "android.permission.WRITE_SETTINGS" };
  }
  
  public static boolean canDrawOverlays(Context paramContext)
  {
    return isCallingPackageAllowedToDrawOverlays(paramContext, Process.myUid(), paramContext.getOpPackageName(), false);
  }
  
  public static boolean checkAndNoteChangeNetworkStateOperation(Context paramContext, int paramInt, String paramString, boolean paramBoolean)
  {
    if (paramContext.checkCallingOrSelfPermission("android.permission.CHANGE_NETWORK_STATE") == 0) {
      return true;
    }
    return isCallingPackageAllowedToPerformAppOpsProtectedOperation(paramContext, paramInt, paramString, paramBoolean, 23, PM_CHANGE_NETWORK_STATE, true);
  }
  
  public static boolean checkAndNoteDrawOverlaysOperation(Context paramContext, int paramInt, String paramString, boolean paramBoolean)
  {
    return isCallingPackageAllowedToPerformAppOpsProtectedOperation(paramContext, paramInt, paramString, paramBoolean, 24, PM_SYSTEM_ALERT_WINDOW, true);
  }
  
  public static boolean checkAndNoteWriteSettingsOperation(Context paramContext, int paramInt, String paramString, boolean paramBoolean)
  {
    return isCallingPackageAllowedToPerformAppOpsProtectedOperation(paramContext, paramInt, paramString, paramBoolean, 23, PM_WRITE_SETTINGS, true);
  }
  
  public static String getGTalkDeviceId(long paramLong)
  {
    return "android-" + Long.toHexString(paramLong);
  }
  
  public static String getPackageNameForUid(Context paramContext, int paramInt)
  {
    if (paramInt == 1000) {
      return "android";
    }
    paramContext = paramContext.getPackageManager().getPackagesForUid(paramInt);
    if (paramContext == null) {
      return null;
    }
    return paramContext[0];
  }
  
  public static boolean isCallingPackageAllowedToDrawOverlays(Context paramContext, int paramInt, String paramString, boolean paramBoolean)
  {
    return isCallingPackageAllowedToPerformAppOpsProtectedOperation(paramContext, paramInt, paramString, paramBoolean, 24, PM_SYSTEM_ALERT_WINDOW, false);
  }
  
  public static boolean isCallingPackageAllowedToPerformAppOpsProtectedOperation(Context paramContext, int paramInt1, String paramString, boolean paramBoolean1, int paramInt2, String[] paramArrayOfString, boolean paramBoolean2)
  {
    if (paramString == null) {
      return false;
    }
    Object localObject = (AppOpsManager)paramContext.getSystemService("appops");
    if (paramBoolean2)
    {
      paramInt1 = ((AppOpsManager)localObject).noteOpNoThrow(paramInt2, paramInt1, paramString);
      switch (paramInt1)
      {
      }
    }
    for (;;)
    {
      if (paramBoolean1) {
        break label118;
      }
      return false;
      paramInt1 = ((AppOpsManager)localObject).checkOpNoThrow(paramInt2, paramInt1, paramString);
      break;
      return true;
      paramInt1 = 0;
      paramInt2 = paramArrayOfString.length;
      while (paramInt1 < paramInt2)
      {
        if (paramContext.checkCallingOrSelfPermission(paramArrayOfString[paramInt1]) == 0) {
          return true;
        }
        paramInt1 += 1;
      }
    }
    label118:
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append(paramString);
    ((StringBuilder)localObject).append(" was not granted ");
    if (paramArrayOfString.length > 1)
    {
      ((StringBuilder)localObject).append(" either of these permissions: ");
      paramInt1 = 0;
      label161:
      if (paramInt1 >= paramArrayOfString.length) {
        break label224;
      }
      ((StringBuilder)localObject).append(paramArrayOfString[paramInt1]);
      if (paramInt1 != paramArrayOfString.length - 1) {
        break label217;
      }
    }
    label217:
    for (paramContext = ".";; paramContext = ", ")
    {
      ((StringBuilder)localObject).append(paramContext);
      paramInt1 += 1;
      break label161;
      ((StringBuilder)localObject).append(" this permission: ");
      break;
    }
    label224:
    throw new SecurityException(((StringBuilder)localObject).toString());
  }
  
  public static boolean isCallingPackageAllowedToWriteSettings(Context paramContext, int paramInt, String paramString, boolean paramBoolean)
  {
    return isCallingPackageAllowedToPerformAppOpsProtectedOperation(paramContext, paramInt, paramString, paramBoolean, 23, PM_WRITE_SETTINGS, false);
  }
  
  public static final class Bookmarks
    implements BaseColumns
  {
    public static final Uri CONTENT_URI = Uri.parse("content://settings/bookmarks");
    public static final String FOLDER = "folder";
    public static final String ID = "_id";
    public static final String INTENT = "intent";
    public static final String ORDERING = "ordering";
    public static final String SHORTCUT = "shortcut";
    private static final String TAG = "Bookmarks";
    public static final String TITLE = "title";
    private static final String[] sIntentProjection = { "intent" };
    private static final String[] sShortcutProjection = { "_id", "shortcut" };
    private static final String sShortcutSelection = "shortcut=?";
    
    public static Uri add(ContentResolver paramContentResolver, Intent paramIntent, String paramString1, String paramString2, char paramChar, int paramInt)
    {
      if (paramChar != 0) {
        paramContentResolver.delete(CONTENT_URI, "shortcut=?", new String[] { String.valueOf(paramChar) });
      }
      ContentValues localContentValues = new ContentValues();
      if (paramString1 != null) {
        localContentValues.put("title", paramString1);
      }
      if (paramString2 != null) {
        localContentValues.put("folder", paramString2);
      }
      localContentValues.put("intent", paramIntent.toUri(0));
      if (paramChar != 0) {
        localContentValues.put("shortcut", Integer.valueOf(paramChar));
      }
      localContentValues.put("ordering", Integer.valueOf(paramInt));
      return paramContentResolver.insert(CONTENT_URI, localContentValues);
    }
    
    /* Error */
    public static Intent getIntentForShortcut(ContentResolver paramContentResolver, char paramChar)
    {
      // Byte code:
      //   0: aconst_null
      //   1: astore_3
      //   2: aload_0
      //   3: getstatic 50	android/provider/Settings$Bookmarks:CONTENT_URI	Landroid/net/Uri;
      //   6: getstatic 54	android/provider/Settings$Bookmarks:sIntentProjection	[Ljava/lang/String;
      //   9: ldc 38
      //   11: iconst_1
      //   12: anewarray 52	java/lang/String
      //   15: dup
      //   16: iconst_0
      //   17: iload_1
      //   18: invokestatic 66	java/lang/String:valueOf	(I)Ljava/lang/String;
      //   21: aastore
      //   22: ldc 24
      //   24: invokevirtual 106	android/content/ContentResolver:query	(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
      //   27: astore 4
      //   29: aload_3
      //   30: astore_0
      //   31: aload_0
      //   32: ifnonnull +69 -> 101
      //   35: aload 4
      //   37: invokeinterface 112 1 0
      //   42: istore_2
      //   43: iload_2
      //   44: ifeq +57 -> 101
      //   47: aload 4
      //   49: aload 4
      //   51: ldc 21
      //   53: invokeinterface 116 2 0
      //   58: invokeinterface 119 2 0
      //   63: iconst_0
      //   64: invokestatic 123	android/content/Intent:parseUri	(Ljava/lang/String;I)Landroid/content/Intent;
      //   67: astore_3
      //   68: aload_3
      //   69: astore_0
      //   70: goto -39 -> 31
      //   73: astore_3
      //   74: ldc 29
      //   76: ldc 125
      //   78: aload_3
      //   79: invokestatic 131	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   82: pop
      //   83: goto -52 -> 31
      //   86: astore_0
      //   87: aload 4
      //   89: ifnull +10 -> 99
      //   92: aload 4
      //   94: invokeinterface 134 1 0
      //   99: aload_0
      //   100: athrow
      //   101: aload 4
      //   103: ifnull +10 -> 113
      //   106: aload 4
      //   108: invokeinterface 134 1 0
      //   113: aload_0
      //   114: areturn
      //   115: astore_3
      //   116: goto -85 -> 31
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	119	0	paramContentResolver	ContentResolver
      //   0	119	1	paramChar	char
      //   42	2	2	bool	boolean
      //   1	68	3	localIntent	Intent
      //   73	6	3	localIllegalArgumentException	IllegalArgumentException
      //   115	1	3	localURISyntaxException	URISyntaxException
      //   27	80	4	localCursor	Cursor
      // Exception table:
      //   from	to	target	type
      //   47	68	73	java/lang/IllegalArgumentException
      //   35	43	86	finally
      //   47	68	86	finally
      //   74	83	86	finally
      //   47	68	115	java/net/URISyntaxException
    }
    
    public static CharSequence getLabelForFolder(Resources paramResources, String paramString)
    {
      return paramString;
    }
    
    public static CharSequence getTitle(Context paramContext, Cursor paramCursor)
    {
      int i = paramCursor.getColumnIndex("title");
      int j = paramCursor.getColumnIndex("intent");
      if ((i == -1) || (j == -1)) {
        throw new IllegalArgumentException("The cursor must contain the TITLE and INTENT columns.");
      }
      String str = paramCursor.getString(i);
      if (!TextUtils.isEmpty(str)) {
        return str;
      }
      paramCursor = paramCursor.getString(j);
      if (TextUtils.isEmpty(paramCursor)) {
        return "";
      }
      try
      {
        paramCursor = Intent.parseUri(paramCursor, 0);
        paramContext = paramContext.getPackageManager();
        paramCursor = paramContext.resolveActivity(paramCursor, 0);
        if (paramCursor != null) {
          return paramCursor.loadLabel(paramContext);
        }
      }
      catch (URISyntaxException paramContext)
      {
        return "";
      }
      return "";
    }
  }
  
  private static final class GenerationTracker
  {
    private final MemoryIntArray mArray;
    private int mCurrentGeneration;
    private final Runnable mErrorHandler;
    private final int mIndex;
    
    public GenerationTracker(MemoryIntArray paramMemoryIntArray, int paramInt1, int paramInt2, Runnable paramRunnable)
    {
      this.mArray = paramMemoryIntArray;
      this.mIndex = paramInt1;
      this.mErrorHandler = paramRunnable;
      this.mCurrentGeneration = paramInt2;
    }
    
    private int readCurrentGeneration()
    {
      try
      {
        int i = this.mArray.get(this.mIndex);
        return i;
      }
      catch (IOException localIOException)
      {
        Log.e("Settings", "Error getting current generation", localIOException);
        if (this.mErrorHandler != null) {
          this.mErrorHandler.run();
        }
      }
      return -1;
    }
    
    public void destroy()
    {
      try
      {
        this.mArray.close();
        return;
      }
      catch (IOException localIOException)
      {
        do
        {
          Log.e("Settings", "Error closing backing array", localIOException);
        } while (this.mErrorHandler == null);
        this.mErrorHandler.run();
      }
    }
    
    public boolean isGenerationChanged()
    {
      int i = readCurrentGeneration();
      if (i >= 0)
      {
        if (i == this.mCurrentGeneration) {
          return false;
        }
        this.mCurrentGeneration = i;
      }
      return true;
    }
  }
  
  public static final class Global
    extends Settings.NameValueTable
  {
    public static final String ADB_ENABLED = "adb_enabled";
    public static final String ADD_USERS_WHEN_LOCKED = "add_users_when_locked";
    public static final String AIRPLANE_MODE_ON = "airplane_mode_on";
    public static final String AIRPLANE_MODE_RADIOS = "airplane_mode_radios";
    public static final String AIRPLANE_MODE_TOGGLEABLE_RADIOS = "airplane_mode_toggleable_radios";
    public static final String ALARM_MANAGER_CONSTANTS = "alarm_manager_constants";
    public static final String ALLOW_USER_SWITCHING_WHEN_SYSTEM_USER_LOCKED = "allow_user_switching_when_system_user_locked";
    public static final String ALWAYS_FINISH_ACTIVITIES = "always_finish_activities";
    public static final String ANIMATOR_DURATION_SCALE = "animator_duration_scale";
    public static final String APN_DB_UPDATE_CONTENT_URL = "apn_db_content_url";
    public static final String APN_DB_UPDATE_METADATA_URL = "apn_db_metadata_url";
    public static final String APP_IDLE_CONSTANTS = "app_idle_constants";
    public static final String ASSISTED_GPS_ENABLED = "assisted_gps_enabled";
    public static final String AUDIO_SAFE_VOLUME_STATE = "audio_safe_volume_state";
    public static final String AUTO_TIME = "auto_time";
    public static final String AUTO_TIME_ZONE = "auto_time_zone";
    public static final String BATTERY_DISCHARGE_DURATION_THRESHOLD = "battery_discharge_duration_threshold";
    public static final String BATTERY_DISCHARGE_THRESHOLD = "battery_discharge_threshold";
    public static final String BLE_SCAN_ALWAYS_AVAILABLE = "ble_scan_always_enabled";
    public static final String BLUETOOTH_A2DP_SINK_PRIORITY_PREFIX = "bluetooth_a2dp_sink_priority_";
    public static final String BLUETOOTH_A2DP_SRC_PRIORITY_PREFIX = "bluetooth_a2dp_src_priority_";
    public static final String BLUETOOTH_DISABLED_PROFILES = "bluetooth_disabled_profiles";
    public static final String BLUETOOTH_HEADSET_PRIORITY_PREFIX = "bluetooth_headset_priority_";
    public static final String BLUETOOTH_INPUT_DEVICE_PRIORITY_PREFIX = "bluetooth_input_device_priority_";
    public static final String BLUETOOTH_INTEROPERABILITY_LIST = "bluetooth_interoperability_list";
    public static final String BLUETOOTH_MAP_PRIORITY_PREFIX = "bluetooth_map_priority_";
    public static final String BLUETOOTH_ON = "bluetooth_on";
    public static final String BLUETOOTH_PBAP_CLIENT_PRIORITY_PREFIX = "bluetooth_pbap_client_priority_";
    public static final String BLUETOOTH_SAP_PRIORITY_PREFIX = "bluetooth_sap_priority_";
    public static final String BOOT_COUNT = "boot_count";
    public static final String BUGREPORT_IN_POWER_MENU = "bugreport_in_power_menu";
    public static final String CALL_AUTO_RETRY = "call_auto_retry";
    public static final String CAPTIVE_PORTAL_CN_HTTP_URL = "captive_portal_cn_http_url";
    public static final String CAPTIVE_PORTAL_DETECTION_ENABLED = "captive_portal_detection_enabled";
    public static final String CAPTIVE_PORTAL_FALLBACK_URL = "captive_portal_fallback_url";
    public static final String CAPTIVE_PORTAL_HTTPS_URL = "captive_portal_https_url";
    public static final String CAPTIVE_PORTAL_HTTP_URL = "captive_portal_http_url";
    public static final String CAPTIVE_PORTAL_SERVER = "captive_portal_server";
    public static final String CAPTIVE_PORTAL_USER_AGENT = "captive_portal_user_agent";
    public static final String CAPTIVE_PORTAL_USE_HTTPS = "captive_portal_use_https";
    public static final String CARRIER_APP_WHITELIST = "carrier_app_whitelist";
    public static final String CAR_DOCK_SOUND = "car_dock_sound";
    public static final String CAR_UNDOCK_SOUND = "car_undock_sound";
    public static final String CDMA_CELL_BROADCAST_SMS = "cdma_cell_broadcast_sms";
    public static final String CDMA_ROAMING_MODE = "roaming_settings";
    public static final String CDMA_SUBSCRIPTION_MODE = "subscription_mode";
    public static final String CELL_ON = "cell_on";
    public static final String CERT_PIN_UPDATE_CONTENT_URL = "cert_pin_content_url";
    public static final String CERT_PIN_UPDATE_METADATA_URL = "cert_pin_metadata_url";
    public static final String CHARGING_SOUNDS_ENABLED = "charging_sounds_enabled";
    public static final String COMPATIBILITY_MODE = "compatibility_mode";
    public static final String CONNECTIVITY_CHANGE_DELAY = "connectivity_change_delay";
    public static final String CONNECTIVITY_SAMPLING_INTERVAL_IN_SECONDS = "connectivity_sampling_interval_in_seconds";
    @Deprecated
    public static final String CONTACT_METADATA_SYNC = "contact_metadata_sync";
    public static final String CONTACT_METADATA_SYNC_ENABLED = "contact_metadata_sync_enabled";
    public static final Uri CONTENT_URI = Uri.parse("content://settings/global");
    public static final String DATABASE_DOWNGRADE_REASON = "database_downgrade_reason";
    public static final String DATA_ACTIVITY_TIMEOUT_MOBILE = "data_activity_timeout_mobile";
    public static final String DATA_ACTIVITY_TIMEOUT_WIFI = "data_activity_timeout_wifi";
    public static final String DATA_ROAMING = "data_roaming";
    public static final String DATA_STALL_ALARM_AGGRESSIVE_DELAY_IN_MS = "data_stall_alarm_aggressive_delay_in_ms";
    public static final String DATA_STALL_ALARM_NON_AGGRESSIVE_DELAY_IN_MS = "data_stall_alarm_non_aggressive_delay_in_ms";
    public static final String DEBUG_APP = "debug_app";
    public static final String DEBUG_VIEW_ATTRIBUTES = "debug_view_attributes";
    public static final String DEFAULT_DNS_SERVER = "default_dns_server";
    public static final String DEFAULT_INSTALL_LOCATION = "default_install_location";
    public static final String DESK_DOCK_SOUND = "desk_dock_sound";
    public static final String DESK_UNDOCK_SOUND = "desk_undock_sound";
    public static final String DEVELOPMENT_ENABLE_FREEFORM_WINDOWS_SUPPORT = "enable_freeform_support";
    public static final String DEVELOPMENT_FORCE_RESIZABLE_ACTIVITIES = "force_resizable_activities";
    public static final String DEVELOPMENT_FORCE_RTL = "debug.force_rtl";
    public static final String DEVELOPMENT_SETTINGS_ENABLED = "development_settings_enabled";
    public static final String DEVICE_DEMO_MODE = "device_demo_mode";
    public static final String DEVICE_IDLE_CONSTANTS = "device_idle_constants";
    public static final String DEVICE_IDLE_CONSTANTS_WATCH = "device_idle_constants_watch";
    public static final String DEVICE_NAME = "device_name";
    public static final String DEVICE_PROVISIONED = "device_provisioned";
    public static final String DEVICE_PROVISIONING_MOBILE_DATA_ENABLED = "device_provisioning_mobile_data";
    public static final String DISK_FREE_CHANGE_REPORTING_THRESHOLD = "disk_free_change_reporting_threshold";
    public static final String DISPLAY_SCALING_FORCE = "display_scaling_force";
    public static final String DISPLAY_SIZE_FORCED = "display_size_forced";
    public static final String DNS_RESOLVER_MAX_SAMPLES = "dns_resolver_max_samples";
    public static final String DNS_RESOLVER_MIN_SAMPLES = "dns_resolver_min_samples";
    public static final String DNS_RESOLVER_SAMPLE_VALIDITY_SECONDS = "dns_resolver_sample_validity_seconds";
    public static final String DNS_RESOLVER_SUCCESS_THRESHOLD_PERCENT = "dns_resolver_success_threshold_percent";
    public static final String DOCK_AUDIO_MEDIA_ENABLED = "dock_audio_media_enabled";
    public static final String DOCK_SOUNDS_ENABLED = "dock_sounds_enabled";
    public static final String DOWNLOAD_MAX_BYTES_OVER_MOBILE = "download_manager_max_bytes_over_mobile";
    public static final String DOWNLOAD_RECOMMENDED_MAX_BYTES_OVER_MOBILE = "download_manager_recommended_max_bytes_over_mobile";
    public static final String DROPBOX_AGE_SECONDS = "dropbox_age_seconds";
    public static final String DROPBOX_MAX_FILES = "dropbox_max_files";
    public static final String DROPBOX_QUOTA_KB = "dropbox_quota_kb";
    public static final String DROPBOX_QUOTA_PERCENT = "dropbox_quota_percent";
    public static final String DROPBOX_RESERVE_PERCENT = "dropbox_reserve_percent";
    public static final String DROPBOX_TAG_PREFIX = "dropbox:";
    public static final String EMERGENCY_AFFORDANCE_NEEDED = "emergency_affordance_needed";
    public static final String EMERGENCY_TONE = "emergency_tone";
    public static final String ENABLE_ACCESSIBILITY_GLOBAL_GESTURE_ENABLED = "enable_accessibility_global_gesture_enabled";
    public static final String ENABLE_CELLULAR_ON_BOOT = "enable_cellular_on_boot";
    public static final String ENABLE_EPHEMERAL_FEATURE = "enable_ephemeral_feature";
    public static final String ENCODED_SURROUND_OUTPUT = "encoded_surround_output";
    public static final int ENCODED_SURROUND_OUTPUT_ALWAYS = 2;
    public static final int ENCODED_SURROUND_OUTPUT_AUTO = 0;
    public static final int ENCODED_SURROUND_OUTPUT_NEVER = 1;
    public static final String ENHANCED_4G_MODE_ENABLED = "volte_vt_enabled";
    public static final String EPHEMERAL_COOKIE_MAX_SIZE_BYTES = "ephemeral_cookie_max_size_bytes";
    public static final String EPHEMERAL_HASH_PREFIX_COUNT = "ephemeral_hash_prefix_count";
    public static final String EPHEMERAL_HASH_PREFIX_MASK = "ephemeral_hash_prefix_mask";
    public static final String ERROR_LOGCAT_PREFIX = "logcat_for_";
    public static final String FANCY_IME_ANIMATIONS = "fancy_ime_animations";
    public static final String FORCE_ALLOW_ON_EXTERNAL = "force_allow_on_external";
    public static final String FSTRIM_MANDATORY_INTERVAL = "fstrim_mandatory_interval";
    public static final String GLOBAL_HTTP_PROXY_EXCLUSION_LIST = "global_http_proxy_exclusion_list";
    public static final String GLOBAL_HTTP_PROXY_HOST = "global_http_proxy_host";
    public static final String GLOBAL_HTTP_PROXY_PAC = "global_proxy_pac_url";
    public static final String GLOBAL_HTTP_PROXY_PORT = "global_http_proxy_port";
    public static final String GPRS_REGISTER_CHECK_PERIOD_MS = "gprs_register_check_period_ms";
    public static final String HAS_NEW_VERSION_TO_UPDATE = "has_new_version_to_update";
    public static final String HDMI_CONTROL_AUTO_DEVICE_OFF_ENABLED = "hdmi_control_auto_device_off_enabled";
    public static final String HDMI_CONTROL_AUTO_WAKEUP_ENABLED = "hdmi_control_auto_wakeup_enabled";
    public static final String HDMI_CONTROL_ENABLED = "hdmi_control_enabled";
    public static final String HDMI_SYSTEM_AUDIO_ENABLED = "hdmi_system_audio_enabled";
    public static final String HEADS_UP_NOTIFICATIONS_ENABLED = "heads_up_notifications_enabled";
    public static final int HEADS_UP_OFF = 0;
    public static final int HEADS_UP_ON = 1;
    public static final String HOTSPOT_AUTO_SHUT_DOWN = "hotspot_auto_shut_down";
    public static final String HTTP_PROXY = "http_proxy";
    public static final String INET_CONDITION_DEBOUNCE_DOWN_DELAY = "inet_condition_debounce_down_delay";
    public static final String INET_CONDITION_DEBOUNCE_UP_DELAY = "inet_condition_debounce_up_delay";
    @Deprecated
    public static final String INSTALL_NON_MARKET_APPS = "install_non_market_apps";
    public static final String INTENT_FIREWALL_UPDATE_CONTENT_URL = "intent_firewall_content_url";
    public static final String INTENT_FIREWALL_UPDATE_METADATA_URL = "intent_firewall_metadata_url";
    public static final String JOB_SCHEDULER_CONSTANTS = "job_scheduler_constants";
    public static final String LENIENT_BACKGROUND_CHECK = "lenient_background_check";
    public static final String LOCK_SOUND = "lock_sound";
    public static final String LOW_BATTERY_SOUND = "low_battery_sound";
    public static final String LOW_BATTERY_SOUND_TIMEOUT = "low_battery_sound_timeout";
    public static final String LOW_POWER_MODE = "low_power";
    public static final String LOW_POWER_MODE_TRIGGER_LEVEL = "low_power_trigger_level";
    public static final String LTE_SERVICE_FORCED = "lte_service_forced";
    public static final String MAX_NOTIFICATION_ENQUEUE_RATE = "max_notification_enqueue_rate";
    public static final String MDC_INITIAL_MAX_RETRY = "mdc_initial_max_retry";
    public static final String MHL_INPUT_SWITCHING_ENABLED = "mhl_input_switching_enabled";
    public static final String MHL_POWER_CHARGE_ENABLED = "mhl_power_charge_enabled";
    public static final String MOBILE_DATA = "mobile_data";
    public static final String MOBILE_DATA_ALWAYS_ON = "mobile_data_always_on";
    public static final String MODE_RINGER = "mode_ringer";
    private static final HashSet<String> MOVED_TO_SECURE;
    public static final String MULTI_SIM_DATA_CALL_SUBSCRIPTION = "multi_sim_data_call";
    public static final String MULTI_SIM_SMS_PROMPT = "multi_sim_sms_prompt";
    public static final String MULTI_SIM_SMS_SUBSCRIPTION = "multi_sim_sms";
    public static final String[] MULTI_SIM_USER_PREFERRED_SUBS = { "user_preferred_sub1", "user_preferred_sub2", "user_preferred_sub3" };
    public static final String MULTI_SIM_VOICE_CALL_SUBSCRIPTION = "multi_sim_voice_call";
    public static final String MULTI_SIM_VOICE_PROMPT = "multi_sim_voice_prompt";
    public static final String NETSTATS_DEV_BUCKET_DURATION = "netstats_dev_bucket_duration";
    public static final String NETSTATS_DEV_DELETE_AGE = "netstats_dev_delete_age";
    public static final String NETSTATS_DEV_PERSIST_BYTES = "netstats_dev_persist_bytes";
    public static final String NETSTATS_DEV_ROTATE_AGE = "netstats_dev_rotate_age";
    public static final String NETSTATS_ENABLED = "netstats_enabled";
    public static final String NETSTATS_GLOBAL_ALERT_BYTES = "netstats_global_alert_bytes";
    public static final String NETSTATS_POLL_INTERVAL = "netstats_poll_interval";
    public static final String NETSTATS_SAMPLE_ENABLED = "netstats_sample_enabled";
    public static final String NETSTATS_TIME_CACHE_MAX_AGE = "netstats_time_cache_max_age";
    public static final String NETSTATS_UID_BUCKET_DURATION = "netstats_uid_bucket_duration";
    public static final String NETSTATS_UID_DELETE_AGE = "netstats_uid_delete_age";
    public static final String NETSTATS_UID_PERSIST_BYTES = "netstats_uid_persist_bytes";
    public static final String NETSTATS_UID_ROTATE_AGE = "netstats_uid_rotate_age";
    public static final String NETSTATS_UID_TAG_BUCKET_DURATION = "netstats_uid_tag_bucket_duration";
    public static final String NETSTATS_UID_TAG_DELETE_AGE = "netstats_uid_tag_delete_age";
    public static final String NETSTATS_UID_TAG_PERSIST_BYTES = "netstats_uid_tag_persist_bytes";
    public static final String NETSTATS_UID_TAG_ROTATE_AGE = "netstats_uid_tag_rotate_age";
    public static final String NETWORK_AVOID_BAD_WIFI = "network_avoid_bad_wifi";
    public static final String NETWORK_PREFERENCE = "network_preference";
    public static final String NETWORK_SCORER_APP = "network_scorer_app";
    public static final String NETWORK_SCORING_PROVISIONED = "network_scoring_provisioned";
    public static final String NETWORK_SWITCH_NOTIFICATION_DAILY_LIMIT = "network_switch_notification_daily_limit";
    public static final String NETWORK_SWITCH_NOTIFICATION_RATE_LIMIT_MILLIS = "network_switch_notification_rate_limit_millis";
    public static final String NEW_CONTACT_AGGREGATOR = "new_contact_aggregator";
    public static final String NITZ_UPDATE_DIFF = "nitz_update_diff";
    public static final String NITZ_UPDATE_SPACING = "nitz_update_spacing";
    public static final String NSD_ON = "nsd_on";
    public static final String NTP_SERVER = "ntp_server";
    public static final String NTP_TIMEOUT = "ntp_timeout";
    public static final String OTA_DISABLE_AUTOMATIC_UPDATE = "ota_disable_automatic_update";
    public static final String OVERLAY_DISPLAY_DEVICES = "overlay_display_devices";
    public static final String PACKAGE_VERIFIER_DEFAULT_RESPONSE = "verifier_default_response";
    public static final String PACKAGE_VERIFIER_ENABLE = "package_verifier_enable";
    public static final String PACKAGE_VERIFIER_INCLUDE_ADB = "verifier_verify_adb_installs";
    public static final String PACKAGE_VERIFIER_SETTING_VISIBLE = "verifier_setting_visible";
    public static final String PACKAGE_VERIFIER_TIMEOUT = "verifier_timeout";
    public static final String PAC_CHANGE_DELAY = "pac_change_delay";
    public static final String PDP_WATCHDOG_ERROR_POLL_COUNT = "pdp_watchdog_error_poll_count";
    public static final String PDP_WATCHDOG_ERROR_POLL_INTERVAL_MS = "pdp_watchdog_error_poll_interval_ms";
    public static final String PDP_WATCHDOG_LONG_POLL_INTERVAL_MS = "pdp_watchdog_long_poll_interval_ms";
    public static final String PDP_WATCHDOG_MAX_PDP_RESET_FAIL_COUNT = "pdp_watchdog_max_pdp_reset_fail_count";
    public static final String PDP_WATCHDOG_POLL_INTERVAL_MS = "pdp_watchdog_poll_interval_ms";
    public static final String PDP_WATCHDOG_TRIGGER_PACKET_COUNT = "pdp_watchdog_trigger_packet_count";
    public static final String POLICY_CONTROL = "policy_control";
    public static final String PORTAL_NOTIFICATION_ENABLED = "portal_notification_enable";
    public static final String POWER_SOUNDS_ENABLED = "power_sounds_enabled";
    public static final String PREFERRED_NETWORK_MODE = "preferred_network_mode";
    public static final String PROVISIONING_APN_ALARM_DELAY_IN_MS = "provisioning_apn_alarm_delay_in_ms";
    public static final String RADIO_BLUETOOTH = "bluetooth";
    public static final String RADIO_CELL = "cell";
    public static final String RADIO_NFC = "nfc";
    public static final String RADIO_WIFI = "wifi";
    public static final String RADIO_WIMAX = "wimax";
    public static final String READ_EXTERNAL_STORAGE_ENFORCED_DEFAULT = "read_external_storage_enforced_default";
    public static final String REQUIRE_PASSWORD_TO_DECRYPT = "require_password_to_decrypt";
    public static final String RETAIL_DEMO_MODE_CONSTANTS = "retail_demo_mode_constants";
    public static final String SAFE_BOOT_DISALLOWED = "safe_boot_disallowed";
    public static final String SAMPLING_PROFILER_MS = "sampling_profiler_ms";
    public static final String SELINUX_STATUS = "selinux_status";
    public static final String SELINUX_UPDATE_CONTENT_URL = "selinux_content_url";
    public static final String SELINUX_UPDATE_METADATA_URL = "selinux_metadata_url";
    public static final String SEND_ACTION_APP_ERROR = "send_action_app_error";
    public static final String[] SETTINGS_TO_BACKUP = { "bugreport_in_power_menu", "stay_on_while_plugged_in", "auto_time", "auto_time_zone", "power_sounds_enabled", "dock_sounds_enabled", "charging_sounds_enabled", "usb_mass_storage_enabled", "enable_accessibility_global_gesture_enabled", "wifi_networks_available_notification_on", "wifi_networks_available_repeat_delay", "wifi_watchdog_poor_network_test_enabled", "wifi_num_open_networks_kept", "emergency_tone", "call_auto_retry", "dock_audio_media_enabled", "encoded_surround_output", "low_power_trigger_level" };
    public static final String SETUP_PREPAID_DATA_SERVICE_URL = "setup_prepaid_data_service_url";
    public static final String SETUP_PREPAID_DETECTION_REDIR_HOST = "setup_prepaid_detection_redir_host";
    public static final String SETUP_PREPAID_DETECTION_TARGET_URL = "setup_prepaid_detection_target_url";
    public static final String SET_GLOBAL_HTTP_PROXY = "set_global_http_proxy";
    public static final String SET_INSTALL_LOCATION = "set_install_location";
    public static final String SHORTCUT_MANAGER_CONSTANTS = "shortcut_manager_constants";
    public static final String SHOW_NFC_TIPS = "show_nfc_tips";
    @Deprecated
    public static final String SHOW_PROCESSES = "show_processes";
    public static final String SMS_OUTGOING_CHECK_INTERVAL_MS = "sms_outgoing_check_interval_ms";
    public static final String SMS_OUTGOING_CHECK_MAX_COUNT = "sms_outgoing_check_max_count";
    public static final String SMS_SHORT_CODES_UPDATE_CONTENT_URL = "sms_short_codes_content_url";
    public static final String SMS_SHORT_CODES_UPDATE_METADATA_URL = "sms_short_codes_metadata_url";
    public static final String SMS_SHORT_CODE_CONFIRMATION = "sms_short_code_confirmation";
    public static final String SMS_SHORT_CODE_RULE = "sms_short_code_rule";
    public static final String STAY_ON_WHILE_PLUGGED_IN = "stay_on_while_plugged_in";
    public static final String STORAGE_BENCHMARK_INTERVAL = "storage_benchmark_interval";
    public static final String SYNC_MAX_RETRY_DELAY_IN_SECONDS = "sync_max_retry_delay_in_seconds";
    public static final String SYS_FREE_STORAGE_LOG_INTERVAL = "sys_free_storage_log_interval";
    public static final String SYS_STORAGE_FULL_THRESHOLD_BYTES = "sys_storage_full_threshold_bytes";
    public static final String SYS_STORAGE_THRESHOLD_MAX_BYTES = "sys_storage_threshold_max_bytes";
    public static final String SYS_STORAGE_THRESHOLD_PERCENTAGE = "sys_storage_threshold_percentage";
    public static final String TCP_DEFAULT_INIT_RWND = "tcp_default_init_rwnd";
    public static final String TETHER_DUN_APN = "tether_dun_apn";
    public static final String TETHER_DUN_REQUIRED = "tether_dun_required";
    public static final String TETHER_SUPPORTED = "tether_supported";
    public static final String THEATER_MODE_ON = "theater_mode_on";
    public static final int THREEKEY_MODE_DOWN = 3;
    public static final int THREEKEY_MODE_INVAILD = -1;
    public static final int THREEKEY_MODE_MIDDLE = 2;
    public static final int THREEKEY_MODE_UP = 1;
    public static final String THREE_KEY_MODE = "three_Key_mode";
    public static final String TRANSITION_ANIMATION_SCALE = "transition_animation_scale";
    public static final String TRUSTED_SOUND = "trusted_sound";
    public static final String TZINFO_UPDATE_CONTENT_URL = "tzinfo_content_url";
    public static final String TZINFO_UPDATE_METADATA_URL = "tzinfo_metadata_url";
    public static final String UNINSTALLED_EPHEMERAL_APP_CACHE_DURATION_MILLIS = "uninstalled_ephemeral_app_cache_duration_millis";
    public static final String UNLOCK_SOUND = "unlock_sound";
    public static final String USB_MASS_STORAGE_ENABLED = "usb_mass_storage_enabled";
    public static final String USE_GOOGLE_MAIL = "use_google_mail";
    public static final String VOLTE_PREFERRED_ON = "volte_preferred_on";
    public static final String VT_IMS_ENABLED = "vt_ims_enabled";
    public static final String WAIT_FOR_DEBUGGER = "wait_for_debugger";
    public static final String WEBVIEW_DATA_REDUCTION_PROXY_KEY = "webview_data_reduction_proxy_key";
    public static final String WEBVIEW_FALLBACK_LOGIC_ENABLED = "webview_fallback_logic_enabled";
    public static final String WEBVIEW_MULTIPROCESS = "webview_multiprocess";
    public static final String WEBVIEW_PROVIDER = "webview_provider";
    public static final String WFC_IMS_ENABLED = "wfc_ims_enabled";
    public static final String WFC_IMS_MODE = "wfc_ims_mode";
    public static final String WFC_IMS_ROAMING_ENABLED = "wfc_ims_roaming_enabled";
    public static final String WFC_IMS_ROAMING_MODE = "wfc_ims_roaming_mode";
    public static final String WIFI_AUTO_CHANGE_TO_MOBILE_DATA = "wifi_auto_change_to_mobile_data";
    public static final String WIFI_AUTO_CONNECT_TYPE = "wifi_auto_connect_type";
    public static final String WIFI_BOUNCE_DELAY_OVERRIDE_MS = "wifi_bounce_delay_override_ms";
    public static final String WIFI_COUNTRY_CODE = "wifi_country_code";
    public static final String WIFI_DEVICE_OWNER_CONFIGS_LOCKDOWN = "wifi_device_owner_configs_lockdown";
    public static final String WIFI_DISPLAY_CERTIFICATION_ON = "wifi_display_certification_on";
    public static final String WIFI_DISPLAY_ON = "wifi_display_on";
    public static final String WIFI_DISPLAY_WPS_CONFIG = "wifi_display_wps_config";
    public static final String WIFI_ENHANCED_AUTO_JOIN = "wifi_enhanced_auto_join";
    public static final String WIFI_EPHEMERAL_OUT_OF_RANGE_TIMEOUT_MS = "wifi_ephemeral_out_of_range_timeout_ms";
    public static final String WIFI_FRAMEWORK_SCAN_INTERVAL_MS = "wifi_framework_scan_interval_ms";
    public static final String WIFI_FREQUENCY_BAND = "wifi_frequency_band";
    public static final String WIFI_HOTSPOT2_ENABLED = "wifi_hotspot2_enabled";
    public static final String WIFI_HOTSPOT2_REL1_ENABLED = "wifi_hotspot2_rel1_enabled";
    public static final String WIFI_IDLE_MS = "wifi_idle_ms";
    public static final String WIFI_IPV6_SUPPORTED = "wifi_ipv6_supported";
    public static final String WIFI_MAX_DHCP_RETRY_COUNT = "wifi_max_dhcp_retry_count";
    public static final String WIFI_MOBILE_DATA_TRANSITION_WAKELOCK_TIMEOUT_MS = "wifi_mobile_data_transition_wakelock_timeout_ms";
    public static final String WIFI_NETWORKS_AVAILABLE_NOTIFICATION_ON = "wifi_networks_available_notification_on";
    public static final String WIFI_NETWORKS_AVAILABLE_REPEAT_DELAY = "wifi_networks_available_repeat_delay";
    public static final String WIFI_NETWORK_SHOW_RSSI = "wifi_network_show_rssi";
    public static final String WIFI_NUM_OPEN_NETWORKS_KEPT = "wifi_num_open_networks_kept";
    public static final String WIFI_ON = "wifi_on";
    public static final String WIFI_P2P_DEVICE_NAME = "wifi_p2p_device_name";
    public static final String WIFI_REENABLE_DELAY_MS = "wifi_reenable_delay";
    public static final String WIFI_SAVED_STATE = "wifi_saved_state";
    public static final String WIFI_SCAN_ALWAYS_AVAILABLE = "wifi_scan_always_enabled";
    public static final String WIFI_SCAN_INTERVAL_WHEN_P2P_CONNECTED_MS = "wifi_scan_interval_p2p_connected_ms";
    public static final String WIFI_SHOULD_SWITCH_NETWORK = "wifi_should_switch_network";
    public static final String WIFI_SLEEP_POLICY = "wifi_sleep_policy";
    public static final int WIFI_SLEEP_POLICY_DEFAULT = 0;
    public static final int WIFI_SLEEP_POLICY_NEVER = 2;
    public static final int WIFI_SLEEP_POLICY_NEVER_WHILE_PLUGGED = 1;
    public static final String WIFI_SUPPLICANT_SCAN_INTERVAL_MS = "wifi_supplicant_scan_interval_ms";
    public static final String WIFI_SUSPEND_OPTIMIZATIONS_ENABLED = "wifi_suspend_optimizations_enabled";
    public static final String WIFI_VERBOSE_LOGGING_ENABLED = "wifi_verbose_logging_enabled";
    public static final String WIFI_WATCHDOG_ON = "wifi_watchdog_on";
    public static final String WIFI_WATCHDOG_POOR_NETWORK_TEST_ENABLED = "wifi_watchdog_poor_network_test_enabled";
    public static final String WIMAX_NETWORKS_AVAILABLE_NOTIFICATION_ON = "wimax_networks_available_notification_on";
    public static final String WINDOW_ANIMATION_SCALE = "window_animation_scale";
    public static final String WIRELESS_CHARGING_STARTED_SOUND = "wireless_charging_started_sound";
    public static final String WTF_IS_FATAL = "wtf_is_fatal";
    public static final String ZEN_MODE = "zen_mode";
    public static final int ZEN_MODE_ALARMS = 3;
    public static final String ZEN_MODE_CONFIG_ETAG = "zen_mode_config_etag";
    public static final int ZEN_MODE_IMPORTANT_INTERRUPTIONS = 1;
    public static final int ZEN_MODE_NO_INTERRUPTIONS = 2;
    public static final int ZEN_MODE_OFF = 0;
    public static final String ZEN_MODE_RINGER_LEVEL = "zen_mode_ringer_level";
    private static Settings.NameValueCache sNameValueCache = new Settings.NameValueCache(CONTENT_URI, "GET_global", "PUT_global");
    
    static
    {
      MOVED_TO_SECURE = new HashSet(1);
      MOVED_TO_SECURE.add("install_non_market_apps");
    }
    
    public static final String getBluetoothA2dpSinkPriorityKey(String paramString)
    {
      return "bluetooth_a2dp_sink_priority_" + paramString.toUpperCase(Locale.ROOT);
    }
    
    public static final String getBluetoothA2dpSrcPriorityKey(String paramString)
    {
      return "bluetooth_a2dp_src_priority_" + paramString.toUpperCase(Locale.ROOT);
    }
    
    public static final String getBluetoothHeadsetPriorityKey(String paramString)
    {
      return "bluetooth_headset_priority_" + paramString.toUpperCase(Locale.ROOT);
    }
    
    public static final String getBluetoothInputDevicePriorityKey(String paramString)
    {
      return "bluetooth_input_device_priority_" + paramString.toUpperCase(Locale.ROOT);
    }
    
    public static final String getBluetoothMapPriorityKey(String paramString)
    {
      return "bluetooth_map_priority_" + paramString.toUpperCase(Locale.ROOT);
    }
    
    public static final String getBluetoothPbapClientPriorityKey(String paramString)
    {
      return "bluetooth_pbap_client_priority_" + paramString.toUpperCase(Locale.ROOT);
    }
    
    public static final String getBluetoothSapPriorityKey(String paramString)
    {
      return "bluetooth_sap_priority_" + paramString.toUpperCase(Locale.ROOT);
    }
    
    public static float getFloat(ContentResolver paramContentResolver, String paramString)
      throws Settings.SettingNotFoundException
    {
      paramContentResolver = getString(paramContentResolver, paramString);
      if (paramContentResolver == null) {
        throw new Settings.SettingNotFoundException(paramString);
      }
      try
      {
        float f = Float.parseFloat(paramContentResolver);
        return f;
      }
      catch (NumberFormatException paramContentResolver)
      {
        throw new Settings.SettingNotFoundException(paramString);
      }
    }
    
    public static float getFloat(ContentResolver paramContentResolver, String paramString, float paramFloat)
    {
      paramContentResolver = getString(paramContentResolver, paramString);
      float f = paramFloat;
      if (paramContentResolver != null) {}
      try
      {
        f = Float.parseFloat(paramContentResolver);
        return f;
      }
      catch (NumberFormatException paramContentResolver) {}
      return paramFloat;
    }
    
    public static int getInt(ContentResolver paramContentResolver, String paramString)
      throws Settings.SettingNotFoundException
    {
      paramContentResolver = getString(paramContentResolver, paramString);
      try
      {
        int i = Integer.parseInt(paramContentResolver);
        return i;
      }
      catch (NumberFormatException paramContentResolver)
      {
        throw new Settings.SettingNotFoundException(paramString);
      }
    }
    
    public static int getInt(ContentResolver paramContentResolver, String paramString, int paramInt)
    {
      paramContentResolver = getString(paramContentResolver, paramString);
      int i = paramInt;
      if (paramContentResolver != null) {}
      try
      {
        i = Integer.parseInt(paramContentResolver);
        return i;
      }
      catch (NumberFormatException paramContentResolver) {}
      return paramInt;
    }
    
    public static long getLong(ContentResolver paramContentResolver, String paramString)
      throws Settings.SettingNotFoundException
    {
      paramContentResolver = getString(paramContentResolver, paramString);
      try
      {
        long l = Long.parseLong(paramContentResolver);
        return l;
      }
      catch (NumberFormatException paramContentResolver)
      {
        throw new Settings.SettingNotFoundException(paramString);
      }
    }
    
    public static long getLong(ContentResolver paramContentResolver, String paramString, long paramLong)
    {
      paramContentResolver = getString(paramContentResolver, paramString);
      if (paramContentResolver != null) {}
      try
      {
        long l = Long.parseLong(paramContentResolver);
        return l;
      }
      catch (NumberFormatException paramContentResolver) {}
      return paramLong;
      return paramLong;
    }
    
    public static void getMovedToSecureSettings(Set<String> paramSet)
    {
      paramSet.addAll(MOVED_TO_SECURE);
    }
    
    public static String getString(ContentResolver paramContentResolver, String paramString)
    {
      return getStringForUser(paramContentResolver, paramString, UserHandle.myUserId());
    }
    
    public static String getStringForUser(ContentResolver paramContentResolver, String paramString, int paramInt)
    {
      if (MOVED_TO_SECURE.contains(paramString))
      {
        Log.w("Settings", "Setting " + paramString + " has moved from android.provider.Settings.Global" + " to android.provider.Settings.Secure, returning read-only value.");
        return Settings.Secure.getStringForUser(paramContentResolver, paramString, paramInt);
      }
      return sNameValueCache.getStringForUser(paramContentResolver, paramString, paramInt);
    }
    
    public static Uri getUriFor(String paramString)
    {
      return getUriFor(CONTENT_URI, paramString);
    }
    
    public static boolean isValidZenMode(int paramInt)
    {
      switch (paramInt)
      {
      default: 
        return false;
      }
      return true;
    }
    
    public static boolean putFloat(ContentResolver paramContentResolver, String paramString, float paramFloat)
    {
      return putString(paramContentResolver, paramString, Float.toString(paramFloat));
    }
    
    public static boolean putInt(ContentResolver paramContentResolver, String paramString, int paramInt)
    {
      return putString(paramContentResolver, paramString, Integer.toString(paramInt));
    }
    
    public static boolean putLong(ContentResolver paramContentResolver, String paramString, long paramLong)
    {
      return putString(paramContentResolver, paramString, Long.toString(paramLong));
    }
    
    public static boolean putString(ContentResolver paramContentResolver, String paramString1, String paramString2)
    {
      return putStringForUser(paramContentResolver, paramString1, paramString2, UserHandle.myUserId());
    }
    
    public static boolean putStringForUser(ContentResolver paramContentResolver, String paramString1, String paramString2, int paramInt)
    {
      if (MOVED_TO_SECURE.contains(paramString1))
      {
        Log.w("Settings", "Setting " + paramString1 + " has moved from android.provider.Settings.Global" + " to android.provider.Settings.Secure, value is unchanged.");
        return Settings.Secure.putStringForUser(paramContentResolver, paramString1, paramString2, paramInt);
      }
      return sNameValueCache.putStringForUser(paramContentResolver, paramString1, paramString2, paramInt);
    }
    
    public static String zenModeToString(int paramInt)
    {
      if (paramInt == 1) {
        return "ZEN_MODE_IMPORTANT_INTERRUPTIONS";
      }
      if (paramInt == 3) {
        return "ZEN_MODE_ALARMS";
      }
      if (paramInt == 2) {
        return "ZEN_MODE_NO_INTERRUPTIONS";
      }
      return "ZEN_MODE_OFF";
    }
  }
  
  private static class NameValueCache
  {
    private static final boolean DEBUG = false;
    private static final String NAME_EQ_PLACEHOLDER = "name=?";
    private static final String[] SELECT_VALUE = { "value" };
    private final String mCallGetCommand;
    private final String mCallSetCommand;
    private IContentProvider mContentProvider = null;
    @GuardedBy("this")
    private Settings.GenerationTracker mGenerationTracker;
    private final Uri mUri;
    private final HashMap<String, String> mValues = new HashMap();
    
    public NameValueCache(Uri paramUri, String paramString1, String paramString2)
    {
      this.mUri = paramUri;
      this.mCallGetCommand = paramString1;
      this.mCallSetCommand = paramString2;
    }
    
    private IContentProvider lazyGetProvider(ContentResolver paramContentResolver)
    {
      try
      {
        IContentProvider localIContentProvider2 = this.mContentProvider;
        IContentProvider localIContentProvider1 = localIContentProvider2;
        if (localIContentProvider2 == null)
        {
          localIContentProvider1 = paramContentResolver.acquireProvider(this.mUri.getAuthority());
          this.mContentProvider = localIContentProvider1;
        }
        return localIContentProvider1;
      }
      finally {}
    }
    
    /* Error */
    public String getStringForUser(ContentResolver paramContentResolver, String paramString, int paramInt)
    {
      // Byte code:
      //   0: iload_3
      //   1: invokestatic 103	android/os/UserHandle:myUserId	()I
      //   4: if_icmpne +246 -> 250
      //   7: iconst_1
      //   8: istore 4
      //   10: iload 4
      //   12: ifeq +31 -> 43
      //   15: aload_0
      //   16: monitorenter
      //   17: aload_0
      //   18: getfield 85	android/provider/Settings$NameValueCache:mGenerationTracker	Landroid/provider/Settings$GenerationTracker;
      //   21: ifnull +20 -> 41
      //   24: aload_0
      //   25: getfield 85	android/provider/Settings$NameValueCache:mGenerationTracker	Landroid/provider/Settings$GenerationTracker;
      //   28: invokevirtual 107	android/provider/Settings$GenerationTracker:isGenerationChanged	()Z
      //   31: ifeq +225 -> 256
      //   34: aload_0
      //   35: getfield 50	android/provider/Settings$NameValueCache:mValues	Ljava/util/HashMap;
      //   38: invokevirtual 93	java/util/HashMap:clear	()V
      //   41: aload_0
      //   42: monitorexit
      //   43: aload_0
      //   44: aload_1
      //   45: invokespecial 109	android/provider/Settings$NameValueCache:lazyGetProvider	(Landroid/content/ContentResolver;)Landroid/content/IContentProvider;
      //   48: astore 7
      //   50: aload_0
      //   51: getfield 56	android/provider/Settings$NameValueCache:mCallGetCommand	Ljava/lang/String;
      //   54: ifnull +246 -> 300
      //   57: iload 4
      //   59: ifne +524 -> 583
      //   62: new 111	android/os/Bundle
      //   65: dup
      //   66: invokespecial 112	android/os/Bundle:<init>	()V
      //   69: astore 5
      //   71: aload 5
      //   73: astore 6
      //   75: aload 5
      //   77: ldc 114
      //   79: iload_3
      //   80: invokevirtual 118	android/os/Bundle:putInt	(Ljava/lang/String;I)V
      //   83: iconst_0
      //   84: istore_3
      //   85: aload 5
      //   87: astore 6
      //   89: aload_0
      //   90: monitorenter
      //   91: iload 4
      //   93: ifeq +195 -> 288
      //   96: aload_0
      //   97: getfield 85	android/provider/Settings$NameValueCache:mGenerationTracker	Landroid/provider/Settings$GenerationTracker;
      //   100: ifnonnull +188 -> 288
      //   103: iconst_1
      //   104: istore_3
      //   105: aload 5
      //   107: ifnonnull +473 -> 580
      //   110: new 111	android/os/Bundle
      //   113: dup
      //   114: invokespecial 112	android/os/Bundle:<init>	()V
      //   117: astore 5
      //   119: aload 5
      //   121: ldc 120
      //   123: aconst_null
      //   124: invokevirtual 124	android/os/Bundle:putString	(Ljava/lang/String;Ljava/lang/String;)V
      //   127: aload_0
      //   128: monitorexit
      //   129: aload 7
      //   131: aload_1
      //   132: invokevirtual 127	android/content/ContentResolver:getPackageName	()Ljava/lang/String;
      //   135: aload_0
      //   136: getfield 56	android/provider/Settings$NameValueCache:mCallGetCommand	Ljava/lang/String;
      //   139: aload_2
      //   140: aload 5
      //   142: invokeinterface 133 5 0
      //   147: astore 5
      //   149: aload 5
      //   151: ifnull +149 -> 300
      //   154: aload 5
      //   156: ldc 38
      //   158: invokevirtual 137	android/os/Bundle:getString	(Ljava/lang/String;)Ljava/lang/String;
      //   161: astore 6
      //   163: iload 4
      //   165: ifeq +82 -> 247
      //   168: aload_0
      //   169: monitorenter
      //   170: iload_3
      //   171: ifeq +63 -> 234
      //   174: aload 5
      //   176: ldc 120
      //   178: invokevirtual 141	android/os/Bundle:getParcelable	(Ljava/lang/String;)Landroid/os/Parcelable;
      //   181: checkcast 143	android/util/MemoryIntArray
      //   184: astore 8
      //   186: aload 5
      //   188: ldc -111
      //   190: iconst_m1
      //   191: invokevirtual 149	android/os/Bundle:getInt	(Ljava/lang/String;I)I
      //   194: istore_3
      //   195: aload 8
      //   197: ifnull +37 -> 234
      //   200: iload_3
      //   201: iflt +33 -> 234
      //   204: aload_0
      //   205: new 87	android/provider/Settings$GenerationTracker
      //   208: dup
      //   209: aload 8
      //   211: iload_3
      //   212: aload 5
      //   214: ldc -105
      //   216: iconst_0
      //   217: invokevirtual 149	android/os/Bundle:getInt	(Ljava/lang/String;I)I
      //   220: new 9	android/provider/Settings$NameValueCache$-java_lang_String_getStringForUser_android_content_ContentResolver_cr_java_lang_String_name_int_userHandle_LambdaImpl0
      //   223: dup
      //   224: aload_0
      //   225: invokespecial 154	android/provider/Settings$NameValueCache$-java_lang_String_getStringForUser_android_content_ContentResolver_cr_java_lang_String_name_int_userHandle_LambdaImpl0:<init>	(Landroid/provider/Settings$NameValueCache;)V
      //   228: invokespecial 157	android/provider/Settings$GenerationTracker:<init>	(Landroid/util/MemoryIntArray;IILjava/lang/Runnable;)V
      //   231: putfield 85	android/provider/Settings$NameValueCache:mGenerationTracker	Landroid/provider/Settings$GenerationTracker;
      //   234: aload_0
      //   235: getfield 50	android/provider/Settings$NameValueCache:mValues	Ljava/util/HashMap;
      //   238: aload_2
      //   239: aload 6
      //   241: invokevirtual 161	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
      //   244: pop
      //   245: aload_0
      //   246: monitorexit
      //   247: aload 6
      //   249: areturn
      //   250: iconst_0
      //   251: istore 4
      //   253: goto -243 -> 10
      //   256: aload_0
      //   257: getfield 50	android/provider/Settings$NameValueCache:mValues	Ljava/util/HashMap;
      //   260: aload_2
      //   261: invokevirtual 165	java/util/HashMap:containsKey	(Ljava/lang/Object;)Z
      //   264: ifeq -223 -> 41
      //   267: aload_0
      //   268: getfield 50	android/provider/Settings$NameValueCache:mValues	Ljava/util/HashMap;
      //   271: aload_2
      //   272: invokevirtual 169	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
      //   275: checkcast 37	java/lang/String
      //   278: astore_1
      //   279: aload_0
      //   280: monitorexit
      //   281: aload_1
      //   282: areturn
      //   283: astore_1
      //   284: aload_0
      //   285: monitorexit
      //   286: aload_1
      //   287: athrow
      //   288: goto -161 -> 127
      //   291: astore 5
      //   293: aload_0
      //   294: monitorexit
      //   295: aload 5
      //   297: athrow
      //   298: astore 5
      //   300: aconst_null
      //   301: astore 6
      //   303: aconst_null
      //   304: astore 5
      //   306: aload 7
      //   308: aload_1
      //   309: invokevirtual 127	android/content/ContentResolver:getPackageName	()Ljava/lang/String;
      //   312: aload_0
      //   313: getfield 54	android/provider/Settings$NameValueCache:mUri	Landroid/net/Uri;
      //   316: getstatic 40	android/provider/Settings$NameValueCache:SELECT_VALUE	[Ljava/lang/String;
      //   319: ldc 17
      //   321: iconst_1
      //   322: anewarray 37	java/lang/String
      //   325: dup
      //   326: iconst_0
      //   327: aload_2
      //   328: aastore
      //   329: aconst_null
      //   330: aconst_null
      //   331: invokeinterface 173 8 0
      //   336: astore_1
      //   337: aload_1
      //   338: ifnonnull +65 -> 403
      //   341: aload_1
      //   342: astore 5
      //   344: aload_1
      //   345: astore 6
      //   347: ldc 75
      //   349: new 175	java/lang/StringBuilder
      //   352: dup
      //   353: invokespecial 176	java/lang/StringBuilder:<init>	()V
      //   356: ldc -78
      //   358: invokevirtual 182	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   361: aload_2
      //   362: invokevirtual 182	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   365: ldc -72
      //   367: invokevirtual 182	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   370: aload_0
      //   371: getfield 54	android/provider/Settings$NameValueCache:mUri	Landroid/net/Uri;
      //   374: invokevirtual 187	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
      //   377: invokevirtual 190	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   380: invokestatic 193	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
      //   383: pop
      //   384: aload_1
      //   385: ifnull +9 -> 394
      //   388: aload_1
      //   389: invokeinterface 198 1 0
      //   394: aconst_null
      //   395: areturn
      //   396: astore 5
      //   398: aload_0
      //   399: monitorexit
      //   400: aload 5
      //   402: athrow
      //   403: aload_1
      //   404: astore 5
      //   406: aload_1
      //   407: astore 6
      //   409: aload_1
      //   410: invokeinterface 201 1 0
      //   415: ifeq +58 -> 473
      //   418: aload_1
      //   419: astore 5
      //   421: aload_1
      //   422: astore 6
      //   424: aload_1
      //   425: iconst_0
      //   426: invokeinterface 204 2 0
      //   431: astore 7
      //   433: aload_1
      //   434: astore 5
      //   436: aload_1
      //   437: astore 6
      //   439: aload_0
      //   440: monitorenter
      //   441: aload_0
      //   442: getfield 50	android/provider/Settings$NameValueCache:mValues	Ljava/util/HashMap;
      //   445: aload_2
      //   446: aload 7
      //   448: invokevirtual 161	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
      //   451: pop
      //   452: aload_1
      //   453: astore 5
      //   455: aload_1
      //   456: astore 6
      //   458: aload_0
      //   459: monitorexit
      //   460: aload_1
      //   461: ifnull +9 -> 470
      //   464: aload_1
      //   465: invokeinterface 198 1 0
      //   470: aload 7
      //   472: areturn
      //   473: aconst_null
      //   474: astore 7
      //   476: goto -43 -> 433
      //   479: astore 7
      //   481: aload_1
      //   482: astore 5
      //   484: aload_1
      //   485: astore 6
      //   487: aload_0
      //   488: monitorexit
      //   489: aload_1
      //   490: astore 5
      //   492: aload_1
      //   493: astore 6
      //   495: aload 7
      //   497: athrow
      //   498: astore_1
      //   499: aload 5
      //   501: astore 6
      //   503: ldc 75
      //   505: new 175	java/lang/StringBuilder
      //   508: dup
      //   509: invokespecial 176	java/lang/StringBuilder:<init>	()V
      //   512: ldc -78
      //   514: invokevirtual 182	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   517: aload_2
      //   518: invokevirtual 182	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   521: ldc -72
      //   523: invokevirtual 182	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   526: aload_0
      //   527: getfield 54	android/provider/Settings$NameValueCache:mUri	Landroid/net/Uri;
      //   530: invokevirtual 187	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
      //   533: invokevirtual 190	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   536: aload_1
      //   537: invokestatic 207	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   540: pop
      //   541: aload 5
      //   543: ifnull +10 -> 553
      //   546: aload 5
      //   548: invokeinterface 198 1 0
      //   553: aconst_null
      //   554: areturn
      //   555: astore_1
      //   556: aload 6
      //   558: ifnull +10 -> 568
      //   561: aload 6
      //   563: invokeinterface 198 1 0
      //   568: aload_1
      //   569: athrow
      //   570: astore 5
      //   572: goto -272 -> 300
      //   575: astore 5
      //   577: goto -284 -> 293
      //   580: goto -461 -> 119
      //   583: aconst_null
      //   584: astore 5
      //   586: goto -503 -> 83
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	589	0	this	NameValueCache
      //   0	589	1	paramContentResolver	ContentResolver
      //   0	589	2	paramString	String
      //   0	589	3	paramInt	int
      //   8	244	4	i	int
      //   69	144	5	localBundle	Bundle
      //   291	5	5	localObject1	Object
      //   298	1	5	localRemoteException1	RemoteException
      //   304	39	5	localContentResolver1	ContentResolver
      //   396	5	5	localObject2	Object
      //   404	143	5	localContentResolver2	ContentResolver
      //   570	1	5	localRemoteException2	RemoteException
      //   575	1	5	localObject3	Object
      //   584	1	5	localObject4	Object
      //   73	489	6	localObject5	Object
      //   48	427	7	localObject6	Object
      //   479	17	7	localObject7	Object
      //   184	26	8	localMemoryIntArray	MemoryIntArray
      // Exception table:
      //   from	to	target	type
      //   17	41	283	finally
      //   256	279	283	finally
      //   96	103	291	finally
      //   110	119	291	finally
      //   62	71	298	android/os/RemoteException
      //   127	149	298	android/os/RemoteException
      //   154	163	298	android/os/RemoteException
      //   168	170	298	android/os/RemoteException
      //   245	247	298	android/os/RemoteException
      //   293	298	298	android/os/RemoteException
      //   398	403	298	android/os/RemoteException
      //   174	195	396	finally
      //   204	234	396	finally
      //   234	245	396	finally
      //   441	452	479	finally
      //   306	337	498	android/os/RemoteException
      //   347	384	498	android/os/RemoteException
      //   409	418	498	android/os/RemoteException
      //   424	433	498	android/os/RemoteException
      //   439	441	498	android/os/RemoteException
      //   458	460	498	android/os/RemoteException
      //   487	489	498	android/os/RemoteException
      //   495	498	498	android/os/RemoteException
      //   306	337	555	finally
      //   347	384	555	finally
      //   409	418	555	finally
      //   424	433	555	finally
      //   439	441	555	finally
      //   458	460	555	finally
      //   487	489	555	finally
      //   495	498	555	finally
      //   503	541	555	finally
      //   75	83	570	android/os/RemoteException
      //   89	91	570	android/os/RemoteException
      //   119	127	575	finally
    }
    
    public boolean putStringForUser(ContentResolver paramContentResolver, String paramString1, String paramString2, int paramInt)
    {
      try
      {
        Bundle localBundle = new Bundle();
        localBundle.putString("value", paramString2);
        localBundle.putInt("_user", paramInt);
        lazyGetProvider(paramContentResolver).call(paramContentResolver.getPackageName(), this.mCallSetCommand, paramString1, localBundle);
        return true;
      }
      catch (RemoteException paramContentResolver)
      {
        Log.w("Settings", "Can't set key " + paramString1 + " in " + this.mUri, paramContentResolver);
      }
      return false;
    }
  }
  
  public static class NameValueTable
    implements BaseColumns
  {
    public static final String NAME = "name";
    public static final String VALUE = "value";
    
    public static Uri getUriFor(Uri paramUri, String paramString)
    {
      return Uri.withAppendedPath(paramUri, paramString);
    }
    
    protected static boolean putString(ContentResolver paramContentResolver, Uri paramUri, String paramString1, String paramString2)
    {
      try
      {
        ContentValues localContentValues = new ContentValues();
        localContentValues.put("name", paramString1);
        localContentValues.put("value", paramString2);
        paramContentResolver.insert(paramUri, localContentValues);
        return true;
      }
      catch (SQLException paramContentResolver)
      {
        Log.w("Settings", "Can't set key " + paramString1 + " in " + paramUri, paramContentResolver);
      }
      return false;
    }
  }
  
  public static final class Secure
    extends Settings.NameValueTable
  {
    public static final String ACCESSIBILITY_AUTOCLICK_DELAY = "accessibility_autoclick_delay";
    public static final String ACCESSIBILITY_AUTOCLICK_ENABLED = "accessibility_autoclick_enabled";
    public static final String ACCESSIBILITY_CAPTIONING_BACKGROUND_COLOR = "accessibility_captioning_background_color";
    public static final String ACCESSIBILITY_CAPTIONING_EDGE_COLOR = "accessibility_captioning_edge_color";
    public static final String ACCESSIBILITY_CAPTIONING_EDGE_TYPE = "accessibility_captioning_edge_type";
    public static final String ACCESSIBILITY_CAPTIONING_ENABLED = "accessibility_captioning_enabled";
    public static final String ACCESSIBILITY_CAPTIONING_FONT_SCALE = "accessibility_captioning_font_scale";
    public static final String ACCESSIBILITY_CAPTIONING_FOREGROUND_COLOR = "accessibility_captioning_foreground_color";
    public static final String ACCESSIBILITY_CAPTIONING_LOCALE = "accessibility_captioning_locale";
    public static final String ACCESSIBILITY_CAPTIONING_PRESET = "accessibility_captioning_preset";
    public static final String ACCESSIBILITY_CAPTIONING_TYPEFACE = "accessibility_captioning_typeface";
    public static final String ACCESSIBILITY_CAPTIONING_WINDOW_COLOR = "accessibility_captioning_window_color";
    public static final String ACCESSIBILITY_DISPLAY_DALTONIZER = "accessibility_display_daltonizer";
    public static final String ACCESSIBILITY_DISPLAY_DALTONIZER_ENABLED = "accessibility_display_daltonizer_enabled";
    public static final String ACCESSIBILITY_DISPLAY_INVERSION_ENABLED = "accessibility_display_inversion_enabled";
    public static final String ACCESSIBILITY_DISPLAY_MAGNIFICATION_AUTO_UPDATE = "accessibility_display_magnification_auto_update";
    public static final String ACCESSIBILITY_DISPLAY_MAGNIFICATION_ENABLED = "accessibility_display_magnification_enabled";
    public static final String ACCESSIBILITY_DISPLAY_MAGNIFICATION_SCALE = "accessibility_display_magnification_scale";
    public static final String ACCESSIBILITY_ENABLED = "accessibility_enabled";
    public static final String ACCESSIBILITY_HIGH_TEXT_CONTRAST_ENABLED = "high_text_contrast_enabled";
    public static final String ACCESSIBILITY_LARGE_POINTER_ICON = "accessibility_large_pointer_icon";
    public static final String ACCESSIBILITY_SCREEN_READER_URL = "accessibility_script_injection_url";
    public static final String ACCESSIBILITY_SCRIPT_INJECTION = "accessibility_script_injection";
    public static final String ACCESSIBILITY_SOFT_KEYBOARD_MODE = "accessibility_soft_keyboard_mode";
    public static final String ACCESSIBILITY_SPEAK_PASSWORD = "speak_password";
    public static final String ACCESSIBILITY_WEB_CONTENT_KEY_BINDINGS = "accessibility_web_content_key_bindings";
    @Deprecated
    public static final String ADB_ENABLED = "adb_enabled";
    public static final String ADVANCED_REBOOT = "advanced_reboot";
    public static final String ALLOWED_GEOLOCATION_ORIGINS = "allowed_geolocation_origins";
    @Deprecated
    public static final String ALLOW_MOCK_LOCATION = "mock_location";
    public static final String ALWAYS_ON_VPN_APP = "always_on_vpn_app";
    public static final String ALWAYS_ON_VPN_LOCKDOWN = "always_on_vpn_lockdown";
    public static final String ANDROID_ID = "android_id";
    public static final String ANR_SHOW_BACKGROUND = "anr_show_background";
    public static final String ASSISTANT = "assistant";
    public static final String ASSIST_DISCLOSURE_ENABLED = "assist_disclosure_enabled";
    public static final String ASSIST_SCREENSHOT_ENABLED = "assist_screenshot_enabled";
    public static final String ASSIST_STRUCTURE_ENABLED = "assist_structure_enabled";
    public static final String AUTOMATIC_STORAGE_MANAGER_BYTES_CLEARED = "automatic_storage_manager_bytes_cleared";
    public static final String AUTOMATIC_STORAGE_MANAGER_DAYS_TO_RETAIN = "automatic_storage_manager_days_to_retain";
    public static final int AUTOMATIC_STORAGE_MANAGER_DAYS_TO_RETAIN_DEFAULT = 90;
    public static final String AUTOMATIC_STORAGE_MANAGER_ENABLED = "automatic_storage_manager_enabled";
    public static final String AUTOMATIC_STORAGE_MANAGER_LAST_RUN = "automatic_storage_manager_last_run";
    @Deprecated
    public static final String BACKGROUND_DATA = "background_data";
    public static final String BACKUP_AUTO_RESTORE = "backup_auto_restore";
    public static final String BACKUP_ENABLED = "backup_enabled";
    public static final String BACKUP_PROVISIONED = "backup_provisioned";
    public static final String BACKUP_TRANSPORT = "backup_transport";
    public static final String BAR_SERVICE_COMPONENT = "bar_service_component";
    public static final String BLUETOOTH_APTX_HD = "bluetooth_aptx_hd";
    public static final String BLUETOOTH_HCI_LOG = "bluetooth_hci_log";
    @Deprecated
    public static final String BLUETOOTH_ON = "bluetooth_on";
    public static final String BRIGHTNESS_USE_TWILIGHT = "brightness_use_twilight";
    @Deprecated
    public static final String BUGREPORT_IN_POWER_MENU = "bugreport_in_power_menu";
    public static final String CAMERA_DOUBLE_TAP_POWER_GESTURE_DISABLED = "camera_double_tap_power_gesture_disabled";
    public static final String CAMERA_DOUBLE_TWIST_TO_FLIP_ENABLED = "camera_double_twist_to_flip_enabled";
    public static final String CAMERA_GESTURE_DISABLED = "camera_gesture_disabled";
    public static final String CARRIER_APPS_HANDLED = "carrier_apps_handled";
    private static final Set<String> CLONE_TO_MANAGED_PROFILE;
    public static final String COMPLETED_CATEGORY_PREFIX = "suggested.completed_category.";
    public static final String CONNECTIVITY_RELEASE_PENDING_INTENT_DELAY_MS = "connectivity_release_pending_intent_delay_ms";
    public static final Uri CONTENT_URI = Uri.parse("content://settings/secure");
    @Deprecated
    public static final String DATA_ROAMING = "data_roaming";
    public static final String DEFAULT_INPUT_METHOD = "default_input_method";
    public static final String DEMO_USER_SETUP_COMPLETE = "demo_user_setup_complete";
    @Deprecated
    public static final String DEVELOPMENT_SETTINGS_ENABLED = "development_settings_enabled";
    @Deprecated
    public static final String DEVICE_PROVISIONED = "device_provisioned";
    public static final String DIALER_DEFAULT_APPLICATION = "dialer_default_application";
    public static final String DISABLED_PRINT_SERVICES = "disabled_print_services";
    public static final String DISABLED_SYSTEM_INPUT_METHODS = "disabled_system_input_methods";
    public static final String DISPLAY_DENSITY_FORCED = "display_density_forced";
    public static final String DOUBLE_TAP_TO_WAKE = "double_tap_to_wake";
    public static final String DOZE_ENABLED = "doze_enabled";
    public static final String DOZE_PULSE_ON_DOUBLE_TAP = "doze_pulse_on_double_tap";
    public static final String DOZE_PULSE_ON_PICK_UP = "doze_pulse_on_pick_up";
    public static final String EMERGENCY_ASSISTANCE_APPLICATION = "emergency_assistance_application";
    public static final String ENABLED_ACCESSIBILITY_SERVICES = "enabled_accessibility_services";
    public static final String ENABLED_INPUT_METHODS = "enabled_input_methods";
    public static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    public static final String ENABLED_NOTIFICATION_POLICY_ACCESS_PACKAGES = "enabled_notification_policy_access_packages";
    public static final String ENABLED_PRINT_SERVICES = "enabled_print_services";
    public static final String ENABLED_VR_LISTENERS = "enabled_vr_listeners";
    public static final String ENHANCED_VOICE_PRIVACY_ENABLED = "enhanced_voice_privacy_enabled";
    public static final String HOTSPOT_AUTO_SHUT_DOWN = "hotspot_auto_shut_down";
    @Deprecated
    public static final String HTTP_PROXY = "http_proxy";
    public static final String IMMERSIVE_MODE_CONFIRMATIONS = "immersive_mode_confirmations";
    public static final String INCALL_POWER_BUTTON_BEHAVIOR = "incall_power_button_behavior";
    public static final int INCALL_POWER_BUTTON_BEHAVIOR_DEFAULT = 1;
    public static final int INCALL_POWER_BUTTON_BEHAVIOR_HANGUP = 2;
    public static final int INCALL_POWER_BUTTON_BEHAVIOR_SCREEN_OFF = 1;
    public static final String INPUT_METHODS_SUBTYPE_HISTORY = "input_methods_subtype_history";
    public static final String INPUT_METHOD_SELECTOR_VISIBILITY = "input_method_selector_visibility";
    public static final String INSTALL_NON_MARKET_APPS = "install_non_market_apps";
    public static final String LAST_SETUP_SHOWN = "last_setup_shown";
    public static final String LOCATION_MODE = "location_mode";
    public static final int LOCATION_MODE_BATTERY_SAVING = 2;
    public static final int LOCATION_MODE_HIGH_ACCURACY = 3;
    public static final int LOCATION_MODE_OFF = 0;
    public static final int LOCATION_MODE_PREVIOUS = -1;
    public static final int LOCATION_MODE_SENSORS_ONLY = 1;
    public static final String LOCATION_PREVIOUS_MODE = "location_previous_mode";
    @Deprecated
    public static final String LOCATION_PROVIDERS_ALLOWED = "location_providers_allowed";
    @Deprecated
    public static final String LOCK_BIOMETRIC_WEAK_FLAGS = "lock_biometric_weak_flags";
    @Deprecated
    public static final String LOCK_PATTERN_ENABLED = "lock_pattern_autolock";
    @Deprecated
    public static final String LOCK_PATTERN_TACTILE_FEEDBACK_ENABLED = "lock_pattern_tactile_feedback_enabled";
    @Deprecated
    public static final String LOCK_PATTERN_VISIBLE = "lock_pattern_visible_pattern";
    public static final String LOCK_SCREEN_ALLOW_PRIVATE_NOTIFICATIONS = "lock_screen_allow_private_notifications";
    public static final String LOCK_SCREEN_ALLOW_REMOTE_INPUT = "lock_screen_allow_remote_input";
    @Deprecated
    public static final String LOCK_SCREEN_APPWIDGET_IDS = "lock_screen_appwidget_ids";
    @Deprecated
    public static final String LOCK_SCREEN_FALLBACK_APPWIDGET_ID = "lock_screen_fallback_appwidget_id";
    public static final String LOCK_SCREEN_LOCK_AFTER_TIMEOUT = "lock_screen_lock_after_timeout";
    public static final String LOCK_SCREEN_OWNER_INFO = "lock_screen_owner_info";
    public static final String LOCK_SCREEN_OWNER_INFO_ENABLED = "lock_screen_owner_info_enabled";
    public static final String LOCK_SCREEN_SHOW_NOTIFICATIONS = "lock_screen_show_notifications";
    @Deprecated
    public static final String LOCK_SCREEN_STICKY_APPWIDGET = "lock_screen_sticky_appwidget";
    public static final String LOCK_TO_APP_EXIT_LOCKED = "lock_to_app_exit_locked";
    @Deprecated
    public static final String LOGGING_ID = "logging_id";
    public static final String LONG_PRESS_TIMEOUT = "long_press_timeout";
    public static final String MANAGED_PROFILE_CONTACT_REMOTE_SEARCH = "managed_profile_contact_remote_search";
    public static final String MOUNT_PLAY_NOTIFICATION_SND = "mount_play_not_snd";
    public static final String MOUNT_UMS_AUTOSTART = "mount_ums_autostart";
    public static final String MOUNT_UMS_NOTIFY_ENABLED = "mount_ums_notify_enabled";
    public static final String MOUNT_UMS_PROMPT = "mount_ums_prompt";
    private static final HashSet<String> MOVED_TO_GLOBAL;
    private static final HashSet<String> MOVED_TO_LOCK_SETTINGS;
    @Deprecated
    public static final String NETWORK_PREFERENCE = "network_preference";
    public static final String NFC_PAYMENT_DEFAULT_COMPONENT = "nfc_payment_default_component";
    public static final String NFC_PAYMENT_FOREGROUND = "nfc_payment_foreground";
    public static final String NIGHT_DISPLAY_ACTIVATED = "night_display_activated";
    public static final String NIGHT_DISPLAY_AUTO_MODE = "night_display_auto_mode";
    public static final String NIGHT_DISPLAY_CUSTOM_END_TIME = "night_display_custom_end_time";
    public static final String NIGHT_DISPLAY_CUSTOM_START_TIME = "night_display_custom_start_time";
    public static final String OP_QUICKPAY_DEFAULT_WAY = "op_quickpay_default_way";
    public static final String OP_QUICKPAY_ENABLE = "op_quickpay_enable";
    public static final String PACKAGE_VERIFIER_USER_CONSENT = "package_verifier_user_consent";
    public static final String PARENTAL_CONTROL_ENABLED = "parental_control_enabled";
    public static final String PARENTAL_CONTROL_LAST_UPDATE = "parental_control_last_update";
    public static final String PARENTAL_CONTROL_REDIRECT_URL = "parental_control_redirect_url";
    public static final String PAYMENT_SERVICE_SEARCH_URI = "payment_service_search_uri";
    public static final String PREFERRED_TTY_MODE = "preferred_tty_mode";
    public static final String PRINT_SERVICE_SEARCH_URI = "print_service_search_uri";
    public static final String QS_TILES = "sysui_qs_tiles";
    public static final String SCREENSAVER_ACTIVATE_ON_DOCK = "screensaver_activate_on_dock";
    public static final String SCREENSAVER_ACTIVATE_ON_SLEEP = "screensaver_activate_on_sleep";
    public static final String SCREENSAVER_COMPONENTS = "screensaver_components";
    public static final String SCREENSAVER_DEFAULT_COMPONENT = "screensaver_default_component";
    public static final String SCREENSAVER_ENABLED = "screensaver_enabled";
    public static final String SEARCH_GLOBAL_SEARCH_ACTIVITY = "search_global_search_activity";
    public static final String SEARCH_MAX_RESULTS_PER_SOURCE = "search_max_results_per_source";
    public static final String SEARCH_MAX_RESULTS_TO_DISPLAY = "search_max_results_to_display";
    public static final String SEARCH_MAX_SHORTCUTS_RETURNED = "search_max_shortcuts_returned";
    public static final String SEARCH_MAX_SOURCE_EVENT_AGE_MILLIS = "search_max_source_event_age_millis";
    public static final String SEARCH_MAX_STAT_AGE_MILLIS = "search_max_stat_age_millis";
    public static final String SEARCH_MIN_CLICKS_FOR_SOURCE_RANKING = "search_min_clicks_for_source_ranking";
    public static final String SEARCH_MIN_IMPRESSIONS_FOR_SOURCE_RANKING = "search_min_impressions_for_source_ranking";
    public static final String SEARCH_NUM_PROMOTED_SOURCES = "search_num_promoted_sources";
    public static final String SEARCH_PER_SOURCE_CONCURRENT_QUERY_LIMIT = "search_per_source_concurrent_query_limit";
    public static final String SEARCH_PREFILL_MILLIS = "search_prefill_millis";
    public static final String SEARCH_PROMOTED_SOURCE_DEADLINE_MILLIS = "search_promoted_source_deadline_millis";
    public static final String SEARCH_QUERY_THREAD_CORE_POOL_SIZE = "search_query_thread_core_pool_size";
    public static final String SEARCH_QUERY_THREAD_MAX_POOL_SIZE = "search_query_thread_max_pool_size";
    public static final String SEARCH_SHORTCUT_REFRESH_CORE_POOL_SIZE = "search_shortcut_refresh_core_pool_size";
    public static final String SEARCH_SHORTCUT_REFRESH_MAX_POOL_SIZE = "search_shortcut_refresh_max_pool_size";
    public static final String SEARCH_SOURCE_TIMEOUT_MILLIS = "search_source_timeout_millis";
    public static final String SEARCH_THREAD_KEEPALIVE_SECONDS = "search_thread_keepalive_seconds";
    public static final String SEARCH_WEB_RESULTS_OVERRIDE_LIMIT = "search_web_results_override_limit";
    public static final String SELECTED_INPUT_METHOD_SUBTYPE = "selected_input_method_subtype";
    public static final String SELECTED_SPELL_CHECKER = "selected_spell_checker";
    public static final String SELECTED_SPELL_CHECKER_SUBTYPE = "selected_spell_checker_subtype";
    public static final String SETTINGS_CLASSNAME = "settings_classname";
    public static final String[] SETTINGS_TO_BACKUP;
    public static final String SHOW_IME_WITH_HARD_KEYBOARD = "show_ime_with_hard_keyboard";
    public static final int SHOW_MODE_AUTO = 0;
    public static final int SHOW_MODE_HIDDEN = 1;
    public static final String SHOW_NOTE_ABOUT_NOTIFICATION_HIDING = "show_note_about_notification_hiding";
    public static final String SKIP_FIRST_USE_HINTS = "skip_first_use_hints";
    public static final String SLEEP_TIMEOUT = "sleep_timeout";
    public static final String SMS_DEFAULT_APPLICATION = "sms_default_application";
    public static final String SPELL_CHECKER_ENABLED = "spell_checker_enabled";
    public static final String SYSTEM_NAVIGATION_KEYS_ENABLED = "system_navigation_keys_enabled";
    public static final String TOUCH_EXPLORATION_ENABLED = "touch_exploration_enabled";
    public static final String TOUCH_EXPLORATION_GRANTED_ACCESSIBILITY_SERVICES = "touch_exploration_granted_accessibility_services";
    public static final String TRUST_AGENTS_INITIALIZED = "trust_agents_initialized";
    @Deprecated
    public static final String TTS_DEFAULT_COUNTRY = "tts_default_country";
    @Deprecated
    public static final String TTS_DEFAULT_LANG = "tts_default_lang";
    public static final String TTS_DEFAULT_LOCALE = "tts_default_locale";
    public static final String TTS_DEFAULT_PITCH = "tts_default_pitch";
    public static final String TTS_DEFAULT_RATE = "tts_default_rate";
    public static final String TTS_DEFAULT_SYNTH = "tts_default_synth";
    @Deprecated
    public static final String TTS_DEFAULT_VARIANT = "tts_default_variant";
    public static final String TTS_ENABLED_PLUGINS = "tts_enabled_plugins";
    @Deprecated
    public static final String TTS_USE_DEFAULTS = "tts_use_defaults";
    public static final String TTY_MODE_ENABLED = "tty_mode_enabled";
    public static final String TV_INPUT_CUSTOM_LABELS = "tv_input_custom_labels";
    public static final String TV_INPUT_HIDDEN_INPUTS = "tv_input_hidden_inputs";
    public static final String TWILIGHT_MODE = "twilight_mode";
    public static final String UI_NIGHT_MODE = "ui_night_mode";
    public static final String UNSAFE_VOLUME_MUSIC_ACTIVE_MS = "unsafe_volume_music_active_ms";
    public static final String USB_AUDIO_AUTOMATIC_ROUTING_DISABLED = "usb_audio_automatic_routing_disabled";
    @Deprecated
    public static final String USB_MASS_STORAGE_ENABLED = "usb_mass_storage_enabled";
    public static final String USER_SETUP_COMPLETE = "user_setup_complete";
    @Deprecated
    public static final String USE_GOOGLE_MAIL = "use_google_mail";
    public static final String VENDOR_APP_INSTALLED = "vendor_app_installed";
    public static final String VOICE_INTERACTION_SERVICE = "voice_interaction_service";
    public static final String VOICE_RECOGNITION_SERVICE = "voice_recognition_service";
    public static final String VOLUME_CONTROLLER_SERVICE_COMPONENT = "volume_controller_service_component";
    public static final String VR_DISPLAY_MODE = "vr_display_mode";
    public static final int VR_DISPLAY_MODE_LOW_PERSISTENCE = 0;
    public static final int VR_DISPLAY_MODE_OFF = 1;
    public static final String WAKE_GESTURE_ENABLED = "wake_gesture_enabled";
    public static final String WEB_ACTION_ENABLED = "web_action_enabled";
    public static final String WIFI_DISCONNECT_DELAY_DURATION = "wifi_disconnect_delay_duration";
    @Deprecated
    public static final String WIFI_IDLE_MS = "wifi_idle_ms";
    @Deprecated
    public static final String WIFI_MAX_DHCP_RETRY_COUNT = "wifi_max_dhcp_retry_count";
    @Deprecated
    public static final String WIFI_MOBILE_DATA_TRANSITION_WAKELOCK_TIMEOUT_MS = "wifi_mobile_data_transition_wakelock_timeout_ms";
    @Deprecated
    public static final String WIFI_NETWORKS_AVAILABLE_NOTIFICATION_ON = "wifi_networks_available_notification_on";
    @Deprecated
    public static final String WIFI_NETWORKS_AVAILABLE_REPEAT_DELAY = "wifi_networks_available_repeat_delay";
    @Deprecated
    public static final String WIFI_NUM_OPEN_NETWORKS_KEPT = "wifi_num_open_networks_kept";
    @Deprecated
    public static final String WIFI_ON = "wifi_on";
    @Deprecated
    public static final String WIFI_WATCHDOG_ACCEPTABLE_PACKET_LOSS_PERCENTAGE = "wifi_watchdog_acceptable_packet_loss_percentage";
    @Deprecated
    public static final String WIFI_WATCHDOG_AP_COUNT = "wifi_watchdog_ap_count";
    @Deprecated
    public static final String WIFI_WATCHDOG_BACKGROUND_CHECK_DELAY_MS = "wifi_watchdog_background_check_delay_ms";
    @Deprecated
    public static final String WIFI_WATCHDOG_BACKGROUND_CHECK_ENABLED = "wifi_watchdog_background_check_enabled";
    @Deprecated
    public static final String WIFI_WATCHDOG_BACKGROUND_CHECK_TIMEOUT_MS = "wifi_watchdog_background_check_timeout_ms";
    @Deprecated
    public static final String WIFI_WATCHDOG_INITIAL_IGNORED_PING_COUNT = "wifi_watchdog_initial_ignored_ping_count";
    @Deprecated
    public static final String WIFI_WATCHDOG_MAX_AP_CHECKS = "wifi_watchdog_max_ap_checks";
    @Deprecated
    public static final String WIFI_WATCHDOG_ON = "wifi_watchdog_on";
    @Deprecated
    public static final String WIFI_WATCHDOG_PING_COUNT = "wifi_watchdog_ping_count";
    @Deprecated
    public static final String WIFI_WATCHDOG_PING_DELAY_MS = "wifi_watchdog_ping_delay_ms";
    @Deprecated
    public static final String WIFI_WATCHDOG_PING_TIMEOUT_MS = "wifi_watchdog_ping_timeout_ms";
    @Deprecated
    public static final String WIFI_WATCHDOG_WATCH_LIST = "wifi_watchdog_watch_list";
    private static boolean sIsSystemProcess;
    private static ILockSettings sLockSettings;
    private static final Settings.NameValueCache sNameValueCache = new Settings.NameValueCache(CONTENT_URI, "GET_secure", "PUT_secure");
    
    static
    {
      sLockSettings = null;
      MOVED_TO_LOCK_SETTINGS = new HashSet(3);
      MOVED_TO_LOCK_SETTINGS.add("lock_pattern_autolock");
      MOVED_TO_LOCK_SETTINGS.add("lock_pattern_visible_pattern");
      MOVED_TO_LOCK_SETTINGS.add("lock_pattern_tactile_feedback_enabled");
      MOVED_TO_GLOBAL = new HashSet();
      MOVED_TO_GLOBAL.add("adb_enabled");
      MOVED_TO_GLOBAL.add("assisted_gps_enabled");
      MOVED_TO_GLOBAL.add("bluetooth_on");
      MOVED_TO_GLOBAL.add("bugreport_in_power_menu");
      MOVED_TO_GLOBAL.add("cdma_cell_broadcast_sms");
      MOVED_TO_GLOBAL.add("roaming_settings");
      MOVED_TO_GLOBAL.add("subscription_mode");
      MOVED_TO_GLOBAL.add("data_activity_timeout_mobile");
      MOVED_TO_GLOBAL.add("data_activity_timeout_wifi");
      MOVED_TO_GLOBAL.add("data_roaming");
      MOVED_TO_GLOBAL.add("development_settings_enabled");
      MOVED_TO_GLOBAL.add("device_provisioned");
      MOVED_TO_GLOBAL.add("display_size_forced");
      MOVED_TO_GLOBAL.add("download_manager_max_bytes_over_mobile");
      MOVED_TO_GLOBAL.add("download_manager_recommended_max_bytes_over_mobile");
      MOVED_TO_GLOBAL.add("mobile_data");
      MOVED_TO_GLOBAL.add("netstats_dev_bucket_duration");
      MOVED_TO_GLOBAL.add("netstats_dev_delete_age");
      MOVED_TO_GLOBAL.add("netstats_dev_persist_bytes");
      MOVED_TO_GLOBAL.add("netstats_dev_rotate_age");
      MOVED_TO_GLOBAL.add("netstats_enabled");
      MOVED_TO_GLOBAL.add("netstats_global_alert_bytes");
      MOVED_TO_GLOBAL.add("netstats_poll_interval");
      MOVED_TO_GLOBAL.add("netstats_sample_enabled");
      MOVED_TO_GLOBAL.add("netstats_time_cache_max_age");
      MOVED_TO_GLOBAL.add("netstats_uid_bucket_duration");
      MOVED_TO_GLOBAL.add("netstats_uid_delete_age");
      MOVED_TO_GLOBAL.add("netstats_uid_persist_bytes");
      MOVED_TO_GLOBAL.add("netstats_uid_rotate_age");
      MOVED_TO_GLOBAL.add("netstats_uid_tag_bucket_duration");
      MOVED_TO_GLOBAL.add("netstats_uid_tag_delete_age");
      MOVED_TO_GLOBAL.add("netstats_uid_tag_persist_bytes");
      MOVED_TO_GLOBAL.add("netstats_uid_tag_rotate_age");
      MOVED_TO_GLOBAL.add("network_preference");
      MOVED_TO_GLOBAL.add("nitz_update_diff");
      MOVED_TO_GLOBAL.add("nitz_update_spacing");
      MOVED_TO_GLOBAL.add("ntp_server");
      MOVED_TO_GLOBAL.add("ntp_timeout");
      MOVED_TO_GLOBAL.add("pdp_watchdog_error_poll_count");
      MOVED_TO_GLOBAL.add("pdp_watchdog_long_poll_interval_ms");
      MOVED_TO_GLOBAL.add("pdp_watchdog_max_pdp_reset_fail_count");
      MOVED_TO_GLOBAL.add("pdp_watchdog_poll_interval_ms");
      MOVED_TO_GLOBAL.add("pdp_watchdog_trigger_packet_count");
      MOVED_TO_GLOBAL.add("sampling_profiler_ms");
      MOVED_TO_GLOBAL.add("setup_prepaid_data_service_url");
      MOVED_TO_GLOBAL.add("setup_prepaid_detection_redir_host");
      MOVED_TO_GLOBAL.add("setup_prepaid_detection_target_url");
      MOVED_TO_GLOBAL.add("tether_dun_apn");
      MOVED_TO_GLOBAL.add("tether_dun_required");
      MOVED_TO_GLOBAL.add("tether_supported");
      MOVED_TO_GLOBAL.add("usb_mass_storage_enabled");
      MOVED_TO_GLOBAL.add("use_google_mail");
      MOVED_TO_GLOBAL.add("wifi_country_code");
      MOVED_TO_GLOBAL.add("wifi_framework_scan_interval_ms");
      MOVED_TO_GLOBAL.add("wifi_frequency_band");
      MOVED_TO_GLOBAL.add("wifi_idle_ms");
      MOVED_TO_GLOBAL.add("wifi_max_dhcp_retry_count");
      MOVED_TO_GLOBAL.add("wifi_mobile_data_transition_wakelock_timeout_ms");
      MOVED_TO_GLOBAL.add("wifi_networks_available_notification_on");
      MOVED_TO_GLOBAL.add("wifi_networks_available_repeat_delay");
      MOVED_TO_GLOBAL.add("wifi_num_open_networks_kept");
      MOVED_TO_GLOBAL.add("wifi_on");
      MOVED_TO_GLOBAL.add("wifi_p2p_device_name");
      MOVED_TO_GLOBAL.add("wifi_saved_state");
      MOVED_TO_GLOBAL.add("wifi_hotspot2_enabled");
      MOVED_TO_GLOBAL.add("wifi_hotspot2_rel1_enabled");
      MOVED_TO_GLOBAL.add("wifi_supplicant_scan_interval_ms");
      MOVED_TO_GLOBAL.add("wifi_suspend_optimizations_enabled");
      MOVED_TO_GLOBAL.add("wifi_verbose_logging_enabled");
      MOVED_TO_GLOBAL.add("wifi_enhanced_auto_join");
      MOVED_TO_GLOBAL.add("wifi_network_show_rssi");
      MOVED_TO_GLOBAL.add("wifi_watchdog_on");
      MOVED_TO_GLOBAL.add("wifi_watchdog_poor_network_test_enabled");
      MOVED_TO_GLOBAL.add("wimax_networks_available_notification_on");
      MOVED_TO_GLOBAL.add("package_verifier_enable");
      MOVED_TO_GLOBAL.add("verifier_timeout");
      MOVED_TO_GLOBAL.add("verifier_default_response");
      MOVED_TO_GLOBAL.add("data_stall_alarm_non_aggressive_delay_in_ms");
      MOVED_TO_GLOBAL.add("data_stall_alarm_aggressive_delay_in_ms");
      MOVED_TO_GLOBAL.add("gprs_register_check_period_ms");
      MOVED_TO_GLOBAL.add("wtf_is_fatal");
      MOVED_TO_GLOBAL.add("battery_discharge_duration_threshold");
      MOVED_TO_GLOBAL.add("battery_discharge_threshold");
      MOVED_TO_GLOBAL.add("send_action_app_error");
      MOVED_TO_GLOBAL.add("dropbox_age_seconds");
      MOVED_TO_GLOBAL.add("dropbox_max_files");
      MOVED_TO_GLOBAL.add("dropbox_quota_kb");
      MOVED_TO_GLOBAL.add("dropbox_quota_percent");
      MOVED_TO_GLOBAL.add("dropbox_reserve_percent");
      MOVED_TO_GLOBAL.add("dropbox:");
      MOVED_TO_GLOBAL.add("logcat_for_");
      MOVED_TO_GLOBAL.add("sys_free_storage_log_interval");
      MOVED_TO_GLOBAL.add("disk_free_change_reporting_threshold");
      MOVED_TO_GLOBAL.add("sys_storage_threshold_percentage");
      MOVED_TO_GLOBAL.add("sys_storage_threshold_max_bytes");
      MOVED_TO_GLOBAL.add("sys_storage_full_threshold_bytes");
      MOVED_TO_GLOBAL.add("sync_max_retry_delay_in_seconds");
      MOVED_TO_GLOBAL.add("connectivity_change_delay");
      MOVED_TO_GLOBAL.add("captive_portal_detection_enabled");
      MOVED_TO_GLOBAL.add("captive_portal_server");
      MOVED_TO_GLOBAL.add("nsd_on");
      MOVED_TO_GLOBAL.add("set_install_location");
      MOVED_TO_GLOBAL.add("default_install_location");
      MOVED_TO_GLOBAL.add("inet_condition_debounce_up_delay");
      MOVED_TO_GLOBAL.add("inet_condition_debounce_down_delay");
      MOVED_TO_GLOBAL.add("read_external_storage_enforced_default");
      MOVED_TO_GLOBAL.add("http_proxy");
      MOVED_TO_GLOBAL.add("global_http_proxy_host");
      MOVED_TO_GLOBAL.add("global_http_proxy_port");
      MOVED_TO_GLOBAL.add("global_http_proxy_exclusion_list");
      MOVED_TO_GLOBAL.add("set_global_http_proxy");
      MOVED_TO_GLOBAL.add("default_dns_server");
      MOVED_TO_GLOBAL.add("preferred_network_mode");
      MOVED_TO_GLOBAL.add("webview_data_reduction_proxy_key");
      MOVED_TO_GLOBAL.add("wifi_auto_connect_type");
      SETTINGS_TO_BACKUP = new String[] { "bugreport_in_power_menu", "mock_location", "parental_control_enabled", "parental_control_redirect_url", "usb_mass_storage_enabled", "accessibility_display_inversion_enabled", "accessibility_display_daltonizer", "accessibility_display_daltonizer_enabled", "accessibility_display_magnification_enabled", "accessibility_display_magnification_scale", "accessibility_display_magnification_auto_update", "accessibility_script_injection", "accessibility_web_content_key_bindings", "enabled_accessibility_services", "enabled_notification_listeners", "enabled_vr_listeners", "enabled_input_methods", "touch_exploration_granted_accessibility_services", "touch_exploration_enabled", "accessibility_enabled", "speak_password", "high_text_contrast_enabled", "accessibility_captioning_preset", "accessibility_captioning_enabled", "accessibility_captioning_locale", "accessibility_captioning_background_color", "accessibility_captioning_foreground_color", "accessibility_captioning_edge_type", "accessibility_captioning_edge_color", "accessibility_captioning_typeface", "accessibility_captioning_font_scale", "accessibility_captioning_window_color", "tts_use_defaults", "tts_default_rate", "tts_default_pitch", "tts_default_synth", "tts_default_lang", "tts_default_country", "tts_enabled_plugins", "tts_default_locale", "show_ime_with_hard_keyboard", "wifi_networks_available_notification_on", "wifi_networks_available_repeat_delay", "wifi_num_open_networks_kept", "selected_spell_checker", "selected_spell_checker_subtype", "spell_checker_enabled", "mount_play_not_snd", "mount_ums_autostart", "mount_ums_prompt", "mount_ums_notify_enabled", "sleep_timeout", "double_tap_to_wake", "wake_gesture_enabled", "long_press_timeout", "camera_gesture_disabled", "accessibility_autoclick_enabled", "accessibility_autoclick_delay", "accessibility_large_pointer_icon", "preferred_tty_mode", "enhanced_voice_privacy_enabled", "tty_mode_enabled", "incall_power_button_behavior", "wifi_disconnect_delay_duration", "night_display_custom_start_time", "night_display_custom_end_time", "night_display_auto_mode", "night_display_activated", "camera_double_twist_to_flip_enabled", "camera_double_tap_power_gesture_disabled", "system_navigation_keys_enabled", "sysui_qs_tiles", "doze_enabled", "doze_pulse_on_pick_up", "advanced_reboot", "doze_pulse_on_double_tap", "op_quickpay_enable", "op_quickpay_default_way" };
      CLONE_TO_MANAGED_PROFILE = new ArraySet();
      CLONE_TO_MANAGED_PROFILE.add("accessibility_enabled");
      CLONE_TO_MANAGED_PROFILE.add("mock_location");
      CLONE_TO_MANAGED_PROFILE.add("allowed_geolocation_origins");
      CLONE_TO_MANAGED_PROFILE.add("default_input_method");
      CLONE_TO_MANAGED_PROFILE.add("enabled_accessibility_services");
      CLONE_TO_MANAGED_PROFILE.add("enabled_input_methods");
      CLONE_TO_MANAGED_PROFILE.add("location_mode");
      CLONE_TO_MANAGED_PROFILE.add("location_previous_mode");
      CLONE_TO_MANAGED_PROFILE.add("location_providers_allowed");
      CLONE_TO_MANAGED_PROFILE.add("selected_input_method_subtype");
      CLONE_TO_MANAGED_PROFILE.add("selected_spell_checker");
      CLONE_TO_MANAGED_PROFILE.add("selected_spell_checker_subtype");
    }
    
    public static void getCloneToManagedProfileSettings(Set<String> paramSet)
    {
      paramSet.addAll(CLONE_TO_MANAGED_PROFILE);
    }
    
    public static float getFloat(ContentResolver paramContentResolver, String paramString)
      throws Settings.SettingNotFoundException
    {
      return getFloatForUser(paramContentResolver, paramString, UserHandle.myUserId());
    }
    
    public static float getFloat(ContentResolver paramContentResolver, String paramString, float paramFloat)
    {
      return getFloatForUser(paramContentResolver, paramString, paramFloat, UserHandle.myUserId());
    }
    
    public static float getFloatForUser(ContentResolver paramContentResolver, String paramString, float paramFloat, int paramInt)
    {
      paramContentResolver = getStringForUser(paramContentResolver, paramString, paramInt);
      float f = paramFloat;
      if (paramContentResolver != null) {}
      try
      {
        f = Float.parseFloat(paramContentResolver);
        return f;
      }
      catch (NumberFormatException paramContentResolver) {}
      return paramFloat;
    }
    
    public static float getFloatForUser(ContentResolver paramContentResolver, String paramString, int paramInt)
      throws Settings.SettingNotFoundException
    {
      paramContentResolver = getStringForUser(paramContentResolver, paramString, paramInt);
      if (paramContentResolver == null) {
        throw new Settings.SettingNotFoundException(paramString);
      }
      try
      {
        float f = Float.parseFloat(paramContentResolver);
        return f;
      }
      catch (NumberFormatException paramContentResolver)
      {
        throw new Settings.SettingNotFoundException(paramString);
      }
    }
    
    public static int getInt(ContentResolver paramContentResolver, String paramString)
      throws Settings.SettingNotFoundException
    {
      return getIntForUser(paramContentResolver, paramString, UserHandle.myUserId());
    }
    
    public static int getInt(ContentResolver paramContentResolver, String paramString, int paramInt)
    {
      return getIntForUser(paramContentResolver, paramString, paramInt, UserHandle.myUserId());
    }
    
    public static int getIntForUser(ContentResolver paramContentResolver, String paramString, int paramInt)
      throws Settings.SettingNotFoundException
    {
      if ("location_mode".equals(paramString)) {
        return getLocationModeForUser(paramContentResolver, paramInt);
      }
      paramContentResolver = getStringForUser(paramContentResolver, paramString, paramInt);
      try
      {
        paramInt = Integer.parseInt(paramContentResolver);
        return paramInt;
      }
      catch (NumberFormatException paramContentResolver)
      {
        throw new Settings.SettingNotFoundException(paramString);
      }
    }
    
    public static int getIntForUser(ContentResolver paramContentResolver, String paramString, int paramInt1, int paramInt2)
    {
      if ("location_mode".equals(paramString)) {
        return getLocationModeForUser(paramContentResolver, paramInt2);
      }
      paramContentResolver = getStringForUser(paramContentResolver, paramString, paramInt2);
      paramInt2 = paramInt1;
      if (paramContentResolver != null) {}
      try
      {
        paramInt2 = Integer.parseInt(paramContentResolver);
        return paramInt2;
      }
      catch (NumberFormatException paramContentResolver) {}
      return paramInt1;
    }
    
    private static final int getLocationModeForUser(ContentResolver paramContentResolver, int paramInt)
    {
      synchronized ()
      {
        boolean bool1 = isLocationProviderEnabledForUser(paramContentResolver, "gps", paramInt);
        boolean bool2 = isLocationProviderEnabledForUser(paramContentResolver, "network", paramInt);
        if ((bool1) && (bool2)) {
          return 3;
        }
        if (bool1) {
          return 1;
        }
        if (bool2) {
          return 2;
        }
        return 0;
      }
    }
    
    public static long getLong(ContentResolver paramContentResolver, String paramString)
      throws Settings.SettingNotFoundException
    {
      return getLongForUser(paramContentResolver, paramString, UserHandle.myUserId());
    }
    
    public static long getLong(ContentResolver paramContentResolver, String paramString, long paramLong)
    {
      return getLongForUser(paramContentResolver, paramString, paramLong, UserHandle.myUserId());
    }
    
    public static long getLongForUser(ContentResolver paramContentResolver, String paramString, int paramInt)
      throws Settings.SettingNotFoundException
    {
      paramContentResolver = getStringForUser(paramContentResolver, paramString, paramInt);
      try
      {
        long l = Long.parseLong(paramContentResolver);
        return l;
      }
      catch (NumberFormatException paramContentResolver)
      {
        throw new Settings.SettingNotFoundException(paramString);
      }
    }
    
    public static long getLongForUser(ContentResolver paramContentResolver, String paramString, long paramLong, int paramInt)
    {
      paramContentResolver = getStringForUser(paramContentResolver, paramString, paramInt);
      if (paramContentResolver != null) {}
      try
      {
        long l = Long.parseLong(paramContentResolver);
        return l;
      }
      catch (NumberFormatException paramContentResolver) {}
      return paramLong;
      return paramLong;
    }
    
    public static void getMovedToGlobalSettings(Set<String> paramSet)
    {
      paramSet.addAll(MOVED_TO_GLOBAL);
    }
    
    public static String getString(ContentResolver paramContentResolver, String paramString)
    {
      return getStringForUser(paramContentResolver, paramString, UserHandle.myUserId());
    }
    
    public static String getStringForUser(ContentResolver paramContentResolver, String paramString, int paramInt)
    {
      if (MOVED_TO_GLOBAL.contains(paramString))
      {
        Log.w("Settings", "Setting " + paramString + " has moved from android.provider.Settings.Secure" + " to android.provider.Settings.Global.");
        return Settings.Global.getStringForUser(paramContentResolver, paramString, paramInt);
      }
      if (MOVED_TO_LOCK_SETTINGS.contains(paramString)) {}
      for (;;)
      {
        try
        {
          if (sLockSettings == null)
          {
            sLockSettings = ILockSettings.Stub.asInterface(ServiceManager.getService("lock_settings"));
            if (Process.myUid() == 1000)
            {
              bool = true;
              sIsSystemProcess = bool;
            }
          }
          else
          {
            if ((sLockSettings != null) && (!sIsSystemProcess)) {
              break label141;
            }
            return sNameValueCache.getStringForUser(paramContentResolver, paramString, paramInt);
          }
          boolean bool = false;
          continue;
          localObject = ActivityThread.currentApplication();
        }
        finally {}
        label141:
        Object localObject;
        int i;
        if ((localObject != null) && (((Application)localObject).getApplicationInfo() != null)) {
          if (((Application)localObject).getApplicationInfo().targetSdkVersion <= 22) {
            i = 1;
          }
        }
        while (i != 0)
        {
          try
          {
            localObject = sLockSettings.getString(paramString, "0", paramInt);
            return (String)localObject;
          }
          catch (RemoteException localRemoteException) {}
          i = 0;
          continue;
          i = 0;
        }
        throw new SecurityException("Settings.Secure." + paramString + " is deprecated and no longer accessible." + " See API documentation for potential replacements.");
      }
    }
    
    public static Uri getUriFor(String paramString)
    {
      if (MOVED_TO_GLOBAL.contains(paramString))
      {
        Log.w("Settings", "Setting " + paramString + " has moved from android.provider.Settings.Secure" + " to android.provider.Settings.Global, returning global URI.");
        return Settings.Global.getUriFor(Settings.Global.CONTENT_URI, paramString);
      }
      return getUriFor(CONTENT_URI, paramString);
    }
    
    @Deprecated
    public static final boolean isLocationProviderEnabled(ContentResolver paramContentResolver, String paramString)
    {
      return isLocationProviderEnabledForUser(paramContentResolver, paramString, UserHandle.myUserId());
    }
    
    @Deprecated
    public static final boolean isLocationProviderEnabledForUser(ContentResolver paramContentResolver, String paramString, int paramInt)
    {
      return TextUtils.delimitedStringContains(getStringForUser(paramContentResolver, "location_providers_allowed", paramInt), ',', paramString);
    }
    
    public static boolean putFloat(ContentResolver paramContentResolver, String paramString, float paramFloat)
    {
      return putFloatForUser(paramContentResolver, paramString, paramFloat, UserHandle.myUserId());
    }
    
    public static boolean putFloatForUser(ContentResolver paramContentResolver, String paramString, float paramFloat, int paramInt)
    {
      return putStringForUser(paramContentResolver, paramString, Float.toString(paramFloat), paramInt);
    }
    
    public static boolean putInt(ContentResolver paramContentResolver, String paramString, int paramInt)
    {
      return putIntForUser(paramContentResolver, paramString, paramInt, UserHandle.myUserId());
    }
    
    public static boolean putIntForUser(ContentResolver paramContentResolver, String paramString, int paramInt1, int paramInt2)
    {
      return putStringForUser(paramContentResolver, paramString, Integer.toString(paramInt1), paramInt2);
    }
    
    public static boolean putLong(ContentResolver paramContentResolver, String paramString, long paramLong)
    {
      return putLongForUser(paramContentResolver, paramString, paramLong, UserHandle.myUserId());
    }
    
    public static boolean putLongForUser(ContentResolver paramContentResolver, String paramString, long paramLong, int paramInt)
    {
      return putStringForUser(paramContentResolver, paramString, Long.toString(paramLong), paramInt);
    }
    
    public static boolean putString(ContentResolver paramContentResolver, String paramString1, String paramString2)
    {
      return putStringForUser(paramContentResolver, paramString1, paramString2, UserHandle.myUserId());
    }
    
    public static boolean putStringForUser(ContentResolver paramContentResolver, String paramString1, String paramString2, int paramInt)
    {
      if ("location_mode".equals(paramString1)) {
        return setLocationModeForUser(paramContentResolver, Integer.parseInt(paramString2), paramInt);
      }
      if (MOVED_TO_GLOBAL.contains(paramString1))
      {
        Log.w("Settings", "Setting " + paramString1 + " has moved from android.provider.Settings.System" + " to android.provider.Settings.Global");
        return Settings.Global.putStringForUser(paramContentResolver, paramString1, paramString2, paramInt);
      }
      return sNameValueCache.putStringForUser(paramContentResolver, paramString1, paramString2, paramInt);
    }
    
    private static final boolean restoreLocationModeForUser(ContentResolver paramContentResolver, int paramInt)
    {
      int j = getIntForUser(paramContentResolver, "location_previous_mode", 3, paramInt);
      int i = j;
      if (j == 0) {
        i = 3;
      }
      return setLocationModeForUser(paramContentResolver, i, paramInt);
    }
    
    private static final boolean saveLocationModeForUser(ContentResolver paramContentResolver, int paramInt)
    {
      return putIntForUser(paramContentResolver, "location_previous_mode", getLocationModeForUser(paramContentResolver, paramInt), paramInt);
    }
    
    private static final boolean setLocationModeForUser(ContentResolver paramContentResolver, int paramInt1, int paramInt2)
    {
      Object localObject = Settings.-get0();
      boolean bool1 = false;
      boolean bool2 = false;
      switch (paramInt1)
      {
      default: 
        try
        {
          throw new IllegalArgumentException("Invalid location mode: " + paramInt1);
        }
        finally {}
      case -1: 
        bool1 = restoreLocationModeForUser(paramContentResolver, paramInt2);
        return bool1;
      case 0: 
        saveLocationModeForUser(paramContentResolver, paramInt2);
        bool2 = setLocationProviderEnabledForUser(paramContentResolver, "network", bool2, paramInt2);
        bool1 = setLocationProviderEnabledForUser(paramContentResolver, "gps", bool1, paramInt2);
        if (!bool1) {}
        break;
      }
      for (bool1 = bool2;; bool1 = false)
      {
        return bool1;
        bool1 = true;
        break;
        bool2 = true;
        break;
        bool1 = true;
        bool2 = true;
        break;
      }
    }
    
    @Deprecated
    public static final void setLocationProviderEnabled(ContentResolver paramContentResolver, String paramString, boolean paramBoolean)
    {
      setLocationProviderEnabledForUser(paramContentResolver, paramString, paramBoolean, UserHandle.myUserId());
    }
    
    @Deprecated
    public static final boolean setLocationProviderEnabledForUser(ContentResolver paramContentResolver, String paramString, boolean paramBoolean, int paramInt)
    {
      localObject = Settings.-get0();
      if (paramBoolean) {}
      for (;;)
      {
        try
        {
          paramString = "+" + paramString;
          paramBoolean = putStringForUser(paramContentResolver, "location_providers_allowed", paramString, paramInt);
          return paramBoolean;
        }
        finally {}
        paramString = "-" + paramString;
      }
    }
  }
  
  public static class SettingNotFoundException
    extends AndroidException
  {
    public SettingNotFoundException(String paramString)
    {
      super();
    }
  }
  
  public static final class System
    extends Settings.NameValueTable
  {
    public static final String ACCELEROMETER_ROTATION = "accelerometer_rotation";
    public static final Validator ACCELEROMETER_ROTATION_VALIDATOR;
    public static final String ACC_ANTI_MISOPERATION_SCREEN = "oem_acc_anti_misoperation_screen";
    public static final String ACC_BREATH_LIGHT = "oem_acc_breath_light";
    public static final String ACC_NIGHT_MODE = "oem_acc_night_mode";
    public static final String ACC_SENSOR_ROTATE_SILENT = "oem_acc_sensor_rotate_silent";
    public static final String ACC_SENSOR_THREE_FINGER = "oem_acc_sensor_three_finger";
    @Deprecated
    public static final String ADB_ENABLED = "adb_enabled";
    public static final String ADVANCED_SETTINGS = "advanced_settings";
    public static final int ADVANCED_SETTINGS_DEFAULT = 0;
    private static final Validator ADVANCED_SETTINGS_VALIDATOR;
    @Deprecated
    public static final String AIRPLANE_MODE_ON = "airplane_mode_on";
    @Deprecated
    public static final String AIRPLANE_MODE_RADIOS = "airplane_mode_radios";
    @Deprecated
    public static final String AIRPLANE_MODE_TOGGLEABLE_RADIOS = "airplane_mode_toggleable_radios";
    public static final String ALARM_ALERT = "alarm_alert";
    public static final String ALARM_ALERT_CACHE = "alarm_alert_cache";
    public static final Uri ALARM_ALERT_CACHE_URI;
    private static final Validator ALARM_ALERT_VALIDATOR;
    @Deprecated
    public static final String ALWAYS_FINISH_ACTIVITIES = "always_finish_activities";
    @Deprecated
    public static final String ANDROID_ID = "android_id";
    @Deprecated
    public static final String ANIMATOR_DURATION_SCALE = "animator_duration_scale";
    public static final String APPEND_FOR_LAST_AUDIBLE = "_last_audible";
    public static final String APP_SETTINGS_VERSION_CONTROL = "oem_app_version_control";
    @Deprecated
    public static final String AUTO_TIME = "auto_time";
    @Deprecated
    public static final String AUTO_TIME_ZONE = "auto_time_zone";
    public static final String BATTERY_LED_CHARGING = "battery_led_charging";
    public static final String BATTERY_LED_LOW_POWER = "battery_led_low_power";
    public static final String BATTERY_LIGHT_FULL_COLOR = "battery_light_full_color";
    public static final String BATTERY_LIGHT_LOW_COLOR = "battery_light_low_color";
    public static final String BATTERY_LIGHT_MEDIUM_COLOR = "battery_light_medium_color";
    public static final String BLUETOOTH_DISCOVERABILITY = "bluetooth_discoverability";
    public static final String BLUETOOTH_DISCOVERABILITY_TIMEOUT = "bluetooth_discoverability_timeout";
    private static final Validator BLUETOOTH_DISCOVERABILITY_TIMEOUT_VALIDATOR;
    private static final Validator BLUETOOTH_DISCOVERABILITY_VALIDATOR;
    @Deprecated
    public static final String BLUETOOTH_ON = "bluetooth_on";
    public static final String BUTTONS_BRIGHTNESS = "buttons_brightness";
    public static final String BUTTONS_FORCE_HOME_ENABLED = "buttons_force_home_enabled";
    public static final String BUTTONS_SHOW_ON_SCREEN_NAVKEYS = "buttons_show_on_screen_navkeys";
    @Deprecated
    public static final String CAR_DOCK_SOUND = "car_dock_sound";
    @Deprecated
    public static final String CAR_UNDOCK_SOUND = "car_undock_sound";
    private static final Set<String> CLONE_TO_MANAGED_PROFILE;
    public static final Uri CONTENT_URI = Uri.parse("content://settings/system");
    @Deprecated
    public static final String DATA_ROAMING = "data_roaming";
    public static final String DATE_FORMAT = "date_format";
    public static final Validator DATE_FORMAT_VALIDATOR;
    @Deprecated
    public static final String DEBUG_APP = "debug_app";
    public static final Uri DEFAULT_ALARM_ALERT_URI;
    private static final float DEFAULT_FONT_SCALE = 1.0F;
    public static final Uri DEFAULT_MMS_NOTIFICATION_URI;
    public static final Uri DEFAULT_NOTIFICATION_URI;
    public static final String DEFAULT_RINGTONE = "ringtone_default";
    public static final Uri DEFAULT_RINGTONE_URI;
    public static final Uri DEFAULT_RINGTONE_URI_2;
    public static final String DEFAULT_SMS_RINGTONE = "default_sms_ringtone";
    @Deprecated
    public static final String DESK_DOCK_SOUND = "desk_dock_sound";
    @Deprecated
    public static final String DESK_UNDOCK_SOUND = "desk_undock_sound";
    @Deprecated
    public static final String DEVICE_PROVISIONED = "device_provisioned";
    @Deprecated
    public static final String DIM_SCREEN = "dim_screen";
    private static final Validator DIM_SCREEN_VALIDATOR;
    public static final String DISPLAY_CTRL_PSENSOR_POSITIVE = "display_ctrl_psensor_positive";
    @Deprecated
    public static final String DOCK_SOUNDS_ENABLED = "dock_sounds_enabled";
    public static final String DOZE_MODE_ENABLED = "doze_mode_enabaled";
    public static final String DOZE_MODE_POLICY = "doze_mode_policy";
    public static final String DTMF_TONE_TYPE_WHEN_DIALING = "dtmf_tone_type";
    public static final Validator DTMF_TONE_TYPE_WHEN_DIALING_VALIDATOR;
    public static final String DTMF_TONE_WHEN_DIALING = "dtmf_tone";
    public static final Validator DTMF_TONE_WHEN_DIALING_VALIDATOR;
    public static final String EGG_MODE = "egg_mode";
    public static final Validator EGG_MODE_VALIDATOR;
    public static final String END_BUTTON_BEHAVIOR = "end_button_behavior";
    public static final int END_BUTTON_BEHAVIOR_DEFAULT = 2;
    public static final int END_BUTTON_BEHAVIOR_HOME = 1;
    public static final int END_BUTTON_BEHAVIOR_SLEEP = 2;
    private static final Validator END_BUTTON_BEHAVIOR_VALIDATOR;
    public static final String FONT_SCALE = "font_scale";
    private static final Validator FONT_SCALE_VALIDATOR;
    public static final String GAME_MODE_BLOCK_NOTIFICATION = "game_mode_block_notification";
    public static final String GAME_MODE_LOCK_BUTTONS = "game_mode_lock_buttons";
    public static final String GAME_MODE_STATUS = "game_mode_status";
    public static final String GAME_MODE_STATUS_AUTO = "game_mode_status_auto";
    public static final String GAME_MODE_STATUS_MANUAL = "game_mode_status_manual";
    public static final String HAND_PULL_ENABLE = "oem_hand_pull_enable";
    public static final String HAPTIC_FEEDBACK_ENABLED = "haptic_feedback_enabled";
    public static final Validator HAPTIC_FEEDBACK_ENABLED_VALIDATOR;
    public static final String HEARING_AID = "hearing_aid";
    public static final Validator HEARING_AID_VALIDATOR;
    public static final String HIDE_ROTATION_LOCK_TOGGLE_FOR_ACCESSIBILITY = "hide_rotation_lock_toggle_for_accessibility";
    public static final Validator HIDE_ROTATION_LOCK_TOGGLE_FOR_ACCESSIBILITY_VALIDATOR;
    public static final String HOTSPOT_START_INIT_DATE = "hotspot_start_init_date";
    @Deprecated
    public static final String HTTP_PROXY = "http_proxy";
    @Deprecated
    public static final String INSTALL_NON_MARKET_APPS = "install_non_market_apps";
    public static final String KEY_APP_SWITCH_DOUBLE_TAP_ACTION = "key_app_switch_double_tap_action";
    public static final String KEY_APP_SWITCH_LONG_PRESS_ACTION = "key_app_switch_long_press_action";
    public static final String KEY_BACK_DOUBLE_TAP_ACTION = "key_back_double_tap_action";
    public static final String KEY_BACK_LONG_PRESS_ACTION = "key_back_long_press_action";
    public static final String KEY_CURRENT_CONTENT_COLOR = "oem_launcher_content_color_key";
    public static final String KEY_CURRENT_MAIN_COLOR = "oem_launcher_main_color_key";
    public static final String KEY_HOME_DOUBLE_TAP_ACTION = "key_home_double_tap_action";
    public static final String KEY_HOME_LONG_PRESS_ACTION = "key_home_long_press_action";
    public static final String KEY_MENU_DOUBLE_TAP_ACTION = "key_menu_double_tap_action";
    public static final String KEY_MENU_LONG_PRESS_ACTION = "key_menu_long_press_action";
    @Deprecated
    public static final String LOCATION_PROVIDERS_ALLOWED = "location_providers_allowed";
    public static final String LOCKSCREEN_DISABLED = "lockscreen.disabled";
    public static final Validator LOCKSCREEN_DISABLED_VALIDATOR;
    public static final String LOCKSCREEN_SOUNDS_ENABLED = "lockscreen_sounds_enabled";
    public static final Validator LOCKSCREEN_SOUNDS_ENABLED_VALIDATOR;
    @Deprecated
    public static final String LOCK_PATTERN_ENABLED = "lock_pattern_autolock";
    @Deprecated
    public static final String LOCK_PATTERN_TACTILE_FEEDBACK_ENABLED = "lock_pattern_tactile_feedback_enabled";
    @Deprecated
    public static final String LOCK_PATTERN_VISIBLE = "lock_pattern_visible_pattern";
    @Deprecated
    public static final String LOCK_SOUND = "lock_sound";
    public static final String LOCK_TO_APP_ENABLED = "lock_to_app_enabled";
    public static final Validator LOCK_TO_APP_ENABLED_VALIDATOR;
    @Deprecated
    public static final String LOGGING_ID = "logging_id";
    @Deprecated
    public static final String LOW_BATTERY_SOUND = "low_battery_sound";
    public static final String MASTER_MONO = "master_mono";
    private static final Validator MASTER_MONO_VALIDATOR;
    public static final int MAX_NUM_RINGTONES = 2;
    public static final String MEDIA_BUTTON_RECEIVER = "media_button_receiver";
    private static final Validator MEDIA_BUTTON_RECEIVER_VALIDATOR;
    public static final String MIGRATE_LOCKSCREEN_WALLPAPER = "migrate_lockscreen_wallpaper";
    public static final String MMS_NOTIFICATION_CACHE = "mms_notification_cache";
    public static final Uri MMS_NOTIFICATION_CACHE_URI;
    public static final String MMS_NOTIFICATION_SOUND = "mms_notification";
    private static final Validator MMS_NOTIFICATION_SOUND_VALIDATOR;
    @Deprecated
    public static final String MODE_RINGER = "mode_ringer";
    public static final String MODE_RINGER_STREAMS_AFFECTED = "mode_ringer_streams_affected";
    private static final Validator MODE_RINGER_STREAMS_AFFECTED_VALIDATOR;
    private static final HashSet<String> MOVED_TO_GLOBAL;
    private static final HashSet<String> MOVED_TO_SECURE;
    private static final HashSet<String> MOVED_TO_SECURE_THEN_GLOBAL;
    public static final String MUTE = "mute";
    public static final String MUTE_STREAMS_AFFECTED = "mute_streams_affected";
    private static final Validator MUTE_STREAMS_AFFECTED_VALIDATOR;
    @Deprecated
    public static final String NETWORK_PREFERENCE = "network_preference";
    @Deprecated
    public static final String NEXT_ALARM_FORMATTED = "next_alarm_formatted";
    private static final Validator NEXT_ALARM_FORMATTED_VALIDATOR;
    @Deprecated
    public static final String NOTIFICATIONS_USE_RING_VOLUME = "notifications_use_ring_volume";
    private static final Validator NOTIFICATIONS_USE_RING_VOLUME_VALIDATOR;
    public static final String NOTIFICATION_LIGHT_PULSE = "notification_light_pulse";
    public static final String NOTIFICATION_LIGHT_PULSE_COLOR = "notification_light_pulse_color";
    public static final Validator NOTIFICATION_LIGHT_PULSE_VALIDATOR;
    public static final String NOTIFICATION_SOUND = "notification_sound";
    public static final String NOTIFICATION_SOUND_CACHE = "notification_sound_cache";
    public static final Uri NOTIFICATION_SOUND_CACHE_URI;
    private static final Validator NOTIFICATION_SOUND_VALIDATOR;
    public static final String OEM_ACC_BACKGAP_THEME = "oem_acc_backgap_theme";
    public static final String OEM_ACC_BLACKSCREEN_GESTRUE_ENABLE = "oem_acc_blackscreen_gestrue_enable";
    public static final String OEM_ACC_BLACKSCREEN_GESTURE_M = "oem_acc_blackscreen_gesture_m";
    public static final String OEM_ACC_BLACKSCREEN_GESTURE_O = "oem_acc_blackscreen_gesture_o";
    public static final String OEM_ACC_BLACKSCREEN_GESTURE_S = "oem_acc_blackscreen_gesture_s";
    public static final String OEM_ACC_BLACKSCREEN_GESTURE_V = "oem_acc_blackscreen_gesture_v";
    public static final String OEM_ACC_BLACKSCREEN_GESTURE_W = "oem_acc_blackscreen_gesture_w";
    public static final String OEM_ACC_BLACKSCREEN_MASTER_SWITCH = "oem_acc_blackscreen_master_switch";
    public static final String OEM_ACC_BLACK_MODE = "oem_black_mode";
    public static final String OEM_ACC_CONTROL_MUSIC = "oem_acc_control_music";
    public static final String OEM_ACC_CONTROL_NEXT = "oem_acc_control_next";
    public static final String OEM_ACC_CONTROL_PAUSE = "oem_acc_control_pause";
    public static final String OEM_ACC_CONTROL_PLAY = "oem_acc_control_play";
    public static final String OEM_ACC_CONTROL_PREV = "oem_acc_control_prev";
    public static final String OEM_ACC_DOUBLECLICK_LIGHTSCREEN = "oem_acc_doubleclick_lightscreen";
    public static final String OEM_ACC_FINGERPRINT_ENROLLING = "oem_acc_fingerprint_enrolling";
    public static final String OEM_ACC_KEY_DEFINE = "oem_acc_key_define";
    public static final String OEM_ACC_KEY_LOCK_MODE = "oem_acc_key_lock_mode";
    public static final String OEM_ACC_NOBLOCK_MODE = "oem_acc_noblock_mode";
    public static final String OEM_ACC_OPEN_FLASHLIGHT = "oem_acc_open_flashlight";
    public static final String OEM_ACC_STARTUP_CAMERA = "oem_acc_startup_camera";
    public static final String OEM_ALLOW_LED_LIGHT = "oem_allow_led_light";
    public static final String OEM_ALLOW_SUSPEND_NOTIFICATION = "oem_allow_suspend_notification";
    public static final String OEM_H_SYSTEM_CTS_VERSION = "oem_h_system_cts_vertion";
    public static final String OEM_OTG_READ = "oem_otg_read";
    public static final String OEM_SCREENSHOT_SOUND_ENABLE = "oem_screenshot_sound_enable";
    public static final String OEM_ZEN_MEDIA_SWITCH = "oem_zen_media_switch";
    @Deprecated
    public static final String PARENTAL_CONTROL_ENABLED = "parental_control_enabled";
    @Deprecated
    public static final String PARENTAL_CONTROL_LAST_UPDATE = "parental_control_last_update";
    @Deprecated
    public static final String PARENTAL_CONTROL_REDIRECT_URL = "parental_control_redirect_url";
    public static final String POINTER_LOCATION = "pointer_location";
    public static final Validator POINTER_LOCATION_VALIDATOR;
    public static final String POINTER_SPEED = "pointer_speed";
    public static final Validator POINTER_SPEED_VALIDATOR;
    @Deprecated
    public static final String POWER_SOUNDS_ENABLED = "power_sounds_enabled";
    public static final Set<String> PRIVATE_SETTINGS;
    public static final Set<String> PUBLIC_SETTINGS;
    public static final String QUICK_UNLOCK_ENABLED = "quick_unlock_enabled";
    @Deprecated
    public static final String RADIO_BLUETOOTH = "bluetooth";
    @Deprecated
    public static final String RADIO_CELL = "cell";
    @Deprecated
    public static final String RADIO_NFC = "nfc";
    @Deprecated
    public static final String RADIO_WIFI = "wifi";
    @Deprecated
    public static final String RADIO_WIMAX = "wimax";
    public static final String READING_MODE_STATUS = "reading_mode_status";
    public static final String READING_MODE_STATUS_AUTO = "rading_mode_status_auto";
    public static final String READING_MODE_STATUS_MANUAL = "reading_mode_status_manual";
    public static final String RINGTONE = "ringtone";
    public static final String RINGTONE_2 = "ringtone_2";
    public static final String RINGTONE_2_CACHE = "ringtone_2_cache";
    public static final Uri RINGTONE_2_CACHE_URI;
    private static final Validator RINGTONE_2_VALIDATOR;
    public static final String RINGTONE_CACHE = "ringtone_cache";
    public static final Uri RINGTONE_CACHE_URI;
    private static final Validator RINGTONE_VALIDATOR;
    public static final String SCREEN_AUTO_BRIGHTNESS_ADJ = "screen_auto_brightness_adj";
    private static final Validator SCREEN_AUTO_BRIGHTNESS_ADJ_VALIDATOR;
    public static final String SCREEN_BRIGHTNESS = "screen_brightness";
    public static final String SCREEN_BRIGHTNESS_MODE = "screen_brightness_mode";
    public static final int SCREEN_BRIGHTNESS_MODE_AUTOMATIC = 1;
    public static final int SCREEN_BRIGHTNESS_MODE_MANUAL = 0;
    private static final Validator SCREEN_BRIGHTNESS_MODE_VALIDATOR;
    private static final Validator SCREEN_BRIGHTNESS_VALIDATOR;
    public static final String SCREEN_OFF_TIMEOUT = "screen_off_timeout";
    private static final Validator SCREEN_OFF_TIMEOUT_VALIDATOR;
    @Deprecated
    public static final String SETTINGS_CLASSNAME = "settings_classname";
    public static final String[] SETTINGS_TO_BACKUP;
    public static final String SETUP_WIZARD_HAS_RUN = "setup_wizard_has_run";
    public static final Validator SETUP_WIZARD_HAS_RUN_VALIDATOR;
    public static final String SHOW_GTALK_SERVICE_STATUS = "SHOW_GTALK_SERVICE_STATUS";
    private static final Validator SHOW_GTALK_SERVICE_STATUS_VALIDATOR;
    @Deprecated
    public static final String SHOW_PROCESSES = "show_processes";
    public static final String SHOW_TOUCHES = "show_touches";
    public static final Validator SHOW_TOUCHES_VALIDATOR;
    @Deprecated
    public static final String SHOW_WEB_SUGGESTIONS = "show_web_suggestions";
    public static final Validator SHOW_WEB_SUGGESTIONS_VALIDATOR;
    public static final String SHUTDOWN_RING = "oem_shutdown_ring";
    public static final String SHUTDOWN_TIMER = "oem_shutdown_timer";
    public static final String SIP_ADDRESS_ONLY = "SIP_ADDRESS_ONLY";
    public static final Validator SIP_ADDRESS_ONLY_VALIDATOR;
    public static final String SIP_ALWAYS = "SIP_ALWAYS";
    public static final Validator SIP_ALWAYS_VALIDATOR;
    @Deprecated
    public static final String SIP_ASK_ME_EACH_TIME = "SIP_ASK_ME_EACH_TIME";
    public static final Validator SIP_ASK_ME_EACH_TIME_VALIDATOR;
    public static final String SIP_CALL_OPTIONS = "sip_call_options";
    public static final Validator SIP_CALL_OPTIONS_VALIDATOR;
    public static final String SIP_RECEIVE_CALLS = "sip_receive_calls";
    public static final Validator SIP_RECEIVE_CALLS_VALIDATOR;
    public static final String SMS_RINGTONE = "sms_ringtone";
    public static final String SOUND_EFFECTS_ENABLED = "sound_effects_enabled";
    public static final Validator SOUND_EFFECTS_ENABLED_VALIDATOR;
    public static final String STARTUP_TIMER = "oem_startup_timer";
    public static final String STATUS_BAR_BATTERY_STYLE = "status_bar_battery_style";
    public static final String STATUS_BAR_SHOW_BATTERY_PERCENT = "status_bar_show_battery_percent";
    @Deprecated
    public static final String STAY_ON_WHILE_PLUGGED_IN = "stay_on_while_plugged_in";
    public static final String SYSTEM_LOCALES = "system_locales";
    public static final String SYS_PROP_SHOW_NAVIGATION_BAR = "sys.settings_show_navigation_bar";
    public static final String SYS_SHOW_BATTERY_PERCENT = "status_bar_show_battery_percent";
    public static final String TEMP_DISABLE_SCENE_SCREEN_EFFECT = "temp_disable_scene_screen_effect";
    public static final String TEXT_AUTO_CAPS = "auto_caps";
    private static final Validator TEXT_AUTO_CAPS_VALIDATOR;
    public static final String TEXT_AUTO_PUNCTUATE = "auto_punctuate";
    private static final Validator TEXT_AUTO_PUNCTUATE_VALIDATOR;
    public static final String TEXT_AUTO_REPLACE = "auto_replace";
    private static final Validator TEXT_AUTO_REPLACE_VALIDATOR;
    public static final String TEXT_SHOW_PASSWORD = "show_password";
    private static final Validator TEXT_SHOW_PASSWORD_VALIDATOR;
    public static final String THREE_SWIPE_SCREEN_SHOT = "three_swipe_screen_shot";
    public static final String TIME_12_24 = "time_12_24";
    public static final Validator TIME_12_24_VALIDATOR;
    @Deprecated
    public static final String TRANSITION_ANIMATION_SCALE = "transition_animation_scale";
    public static final String TTY_MODE = "tty_mode";
    public static final Validator TTY_MODE_VALIDATOR;
    @Deprecated
    public static final String UNLOCK_SOUND = "unlock_sound";
    @Deprecated
    public static final String USB_MASS_STORAGE_ENABLED = "usb_mass_storage_enabled";
    public static final String USER_ROTATION = "user_rotation";
    public static final Validator USER_ROTATION_VALIDATOR;
    @Deprecated
    public static final String USE_GOOGLE_MAIL = "use_google_mail";
    public static final Map<String, Validator> VALIDATORS;
    public static final String VIBRATE_INPUT_DEVICES = "vibrate_input_devices";
    private static final Validator VIBRATE_INPUT_DEVICES_VALIDATOR;
    public static final String VIBRATE_IN_SILENT = "vibrate_in_silent";
    private static final Validator VIBRATE_IN_SILENT_VALIDATOR;
    public static final String VIBRATE_ON = "vibrate_on";
    private static final Validator VIBRATE_ON_VALIDATOR;
    public static final String VIBRATE_WHEN_MUTE = "oem_vibrate_under_silent";
    public static final String VIBRATE_WHEN_RINGING = "vibrate_when_ringing";
    public static final Validator VIBRATE_WHEN_RINGING_VALIDATOR;
    public static final String VOLUME_ALARM = "volume_alarm";
    public static final String VOLUME_BLUETOOTH_SCO = "volume_bluetooth_sco";
    public static final String VOLUME_MASTER = "volume_master";
    public static final String VOLUME_MUSIC = "volume_music";
    public static final String VOLUME_NOTIFICATION = "volume_notification";
    public static final String VOLUME_RING = "volume_ring";
    public static final String[] VOLUME_SETTINGS;
    public static final String VOLUME_SYSTEM = "volume_system";
    public static final String VOLUME_VOICE = "volume_voice";
    public static final String VPN_START_INIT_DATE = "vpn_start_init_date";
    @Deprecated
    public static final String WAIT_FOR_DEBUGGER = "wait_for_debugger";
    @Deprecated
    public static final String WALLPAPER_ACTIVITY = "wallpaper_activity";
    private static final Validator WALLPAPER_ACTIVITY_VALIDATOR;
    public static final String WHEN_TO_MAKE_WIFI_CALLS = "when_to_make_wifi_calls";
    @Deprecated
    public static final String WIFI_MAX_DHCP_RETRY_COUNT = "wifi_max_dhcp_retry_count";
    @Deprecated
    public static final String WIFI_MOBILE_DATA_TRANSITION_WAKELOCK_TIMEOUT_MS = "wifi_mobile_data_transition_wakelock_timeout_ms";
    @Deprecated
    public static final String WIFI_NETWORKS_AVAILABLE_NOTIFICATION_ON = "wifi_networks_available_notification_on";
    @Deprecated
    public static final String WIFI_NETWORKS_AVAILABLE_REPEAT_DELAY = "wifi_networks_available_repeat_delay";
    @Deprecated
    public static final String WIFI_NUM_OPEN_NETWORKS_KEPT = "wifi_num_open_networks_kept";
    @Deprecated
    public static final String WIFI_ON = "wifi_on";
    @Deprecated
    public static final String WIFI_SLEEP_POLICY = "wifi_sleep_policy";
    @Deprecated
    public static final int WIFI_SLEEP_POLICY_DEFAULT = 0;
    @Deprecated
    public static final int WIFI_SLEEP_POLICY_NEVER = 2;
    @Deprecated
    public static final int WIFI_SLEEP_POLICY_NEVER_WHILE_PLUGGED = 1;
    @Deprecated
    public static final String WIFI_STATIC_DNS1 = "wifi_static_dns1";
    private static final Validator WIFI_STATIC_DNS1_VALIDATOR;
    @Deprecated
    public static final String WIFI_STATIC_DNS2 = "wifi_static_dns2";
    private static final Validator WIFI_STATIC_DNS2_VALIDATOR;
    @Deprecated
    public static final String WIFI_STATIC_GATEWAY = "wifi_static_gateway";
    private static final Validator WIFI_STATIC_GATEWAY_VALIDATOR;
    @Deprecated
    public static final String WIFI_STATIC_IP = "wifi_static_ip";
    private static final Validator WIFI_STATIC_IP_VALIDATOR;
    @Deprecated
    public static final String WIFI_STATIC_NETMASK = "wifi_static_netmask";
    private static final Validator WIFI_STATIC_NETMASK_VALIDATOR;
    @Deprecated
    public static final String WIFI_USE_STATIC_IP = "wifi_use_static_ip";
    private static final Validator WIFI_USE_STATIC_IP_VALIDATOR;
    @Deprecated
    public static final String WIFI_WATCHDOG_ACCEPTABLE_PACKET_LOSS_PERCENTAGE = "wifi_watchdog_acceptable_packet_loss_percentage";
    @Deprecated
    public static final String WIFI_WATCHDOG_AP_COUNT = "wifi_watchdog_ap_count";
    @Deprecated
    public static final String WIFI_WATCHDOG_BACKGROUND_CHECK_DELAY_MS = "wifi_watchdog_background_check_delay_ms";
    @Deprecated
    public static final String WIFI_WATCHDOG_BACKGROUND_CHECK_ENABLED = "wifi_watchdog_background_check_enabled";
    @Deprecated
    public static final String WIFI_WATCHDOG_BACKGROUND_CHECK_TIMEOUT_MS = "wifi_watchdog_background_check_timeout_ms";
    @Deprecated
    public static final String WIFI_WATCHDOG_INITIAL_IGNORED_PING_COUNT = "wifi_watchdog_initial_ignored_ping_count";
    @Deprecated
    public static final String WIFI_WATCHDOG_MAX_AP_CHECKS = "wifi_watchdog_max_ap_checks";
    @Deprecated
    public static final String WIFI_WATCHDOG_ON = "wifi_watchdog_on";
    @Deprecated
    public static final String WIFI_WATCHDOG_PING_COUNT = "wifi_watchdog_ping_count";
    @Deprecated
    public static final String WIFI_WATCHDOG_PING_DELAY_MS = "wifi_watchdog_ping_delay_ms";
    @Deprecated
    public static final String WIFI_WATCHDOG_PING_TIMEOUT_MS = "wifi_watchdog_ping_timeout_ms";
    @Deprecated
    public static final String WINDOW_ANIMATION_SCALE = "window_animation_scale";
    public static final String WINDOW_ORIENTATION_LISTENER_LOG = "window_orientation_listener_log";
    public static final Validator WINDOW_ORIENTATION_LISTENER_LOG_VALIDATOR;
    public static final String ZEN_MODE_ALARMS_PRIORITY_MANUAL = "oem_zen_alarms_priority_switch";
    public static final String ZEN_MODE_ALARMS_SILENT_MANUAL = "oem_zen_alarms_silent_switch";
    public static final String ZEN_MODE_MEDIA_PRIORITY_MANUAL = "oem_zen_media_priority_switch";
    public static final String ZEN_MODE_MEDIA_SILENT_MANUAL = "oem_zen_media_silent_switch";
    private static final Validator sBooleanValidator;
    private static final Validator sLenientIpAddressValidator;
    private static final Settings.NameValueCache sNameValueCache = new Settings.NameValueCache(CONTENT_URI, "GET_system", "PUT_system");
    private static final Validator sNonNegativeIntegerValidator;
    private static final Validator sUriValidator;
    
    static
    {
      MOVED_TO_SECURE = new HashSet(30);
      MOVED_TO_SECURE.add("android_id");
      MOVED_TO_SECURE.add("http_proxy");
      MOVED_TO_SECURE.add("location_providers_allowed");
      MOVED_TO_SECURE.add("lock_biometric_weak_flags");
      MOVED_TO_SECURE.add("lock_pattern_autolock");
      MOVED_TO_SECURE.add("lock_pattern_visible_pattern");
      MOVED_TO_SECURE.add("lock_pattern_tactile_feedback_enabled");
      MOVED_TO_SECURE.add("logging_id");
      MOVED_TO_SECURE.add("parental_control_enabled");
      MOVED_TO_SECURE.add("parental_control_last_update");
      MOVED_TO_SECURE.add("parental_control_redirect_url");
      MOVED_TO_SECURE.add("settings_classname");
      MOVED_TO_SECURE.add("use_google_mail");
      MOVED_TO_SECURE.add("wifi_networks_available_notification_on");
      MOVED_TO_SECURE.add("wifi_networks_available_repeat_delay");
      MOVED_TO_SECURE.add("wifi_num_open_networks_kept");
      MOVED_TO_SECURE.add("wifi_on");
      MOVED_TO_SECURE.add("wifi_watchdog_acceptable_packet_loss_percentage");
      MOVED_TO_SECURE.add("wifi_watchdog_ap_count");
      MOVED_TO_SECURE.add("wifi_watchdog_background_check_delay_ms");
      MOVED_TO_SECURE.add("wifi_watchdog_background_check_enabled");
      MOVED_TO_SECURE.add("wifi_watchdog_background_check_timeout_ms");
      MOVED_TO_SECURE.add("wifi_watchdog_initial_ignored_ping_count");
      MOVED_TO_SECURE.add("wifi_watchdog_max_ap_checks");
      MOVED_TO_SECURE.add("wifi_watchdog_on");
      MOVED_TO_SECURE.add("wifi_watchdog_ping_count");
      MOVED_TO_SECURE.add("wifi_watchdog_ping_delay_ms");
      MOVED_TO_SECURE.add("wifi_watchdog_ping_timeout_ms");
      MOVED_TO_SECURE.add("install_non_market_apps");
      MOVED_TO_GLOBAL = new HashSet();
      MOVED_TO_SECURE_THEN_GLOBAL = new HashSet();
      MOVED_TO_SECURE_THEN_GLOBAL.add("adb_enabled");
      MOVED_TO_SECURE_THEN_GLOBAL.add("bluetooth_on");
      MOVED_TO_SECURE_THEN_GLOBAL.add("data_roaming");
      MOVED_TO_SECURE_THEN_GLOBAL.add("device_provisioned");
      MOVED_TO_SECURE_THEN_GLOBAL.add("usb_mass_storage_enabled");
      MOVED_TO_SECURE_THEN_GLOBAL.add("http_proxy");
      MOVED_TO_GLOBAL.add("airplane_mode_on");
      MOVED_TO_GLOBAL.add("airplane_mode_radios");
      MOVED_TO_GLOBAL.add("airplane_mode_toggleable_radios");
      MOVED_TO_GLOBAL.add("auto_time");
      MOVED_TO_GLOBAL.add("auto_time_zone");
      MOVED_TO_GLOBAL.add("car_dock_sound");
      MOVED_TO_GLOBAL.add("car_undock_sound");
      MOVED_TO_GLOBAL.add("desk_dock_sound");
      MOVED_TO_GLOBAL.add("desk_undock_sound");
      MOVED_TO_GLOBAL.add("dock_sounds_enabled");
      MOVED_TO_GLOBAL.add("lock_sound");
      MOVED_TO_GLOBAL.add("unlock_sound");
      MOVED_TO_GLOBAL.add("low_battery_sound");
      MOVED_TO_GLOBAL.add("power_sounds_enabled");
      MOVED_TO_GLOBAL.add("stay_on_while_plugged_in");
      MOVED_TO_GLOBAL.add("wifi_sleep_policy");
      MOVED_TO_GLOBAL.add("mode_ringer");
      MOVED_TO_GLOBAL.add("window_animation_scale");
      MOVED_TO_GLOBAL.add("transition_animation_scale");
      MOVED_TO_GLOBAL.add("animator_duration_scale");
      MOVED_TO_GLOBAL.add("fancy_ime_animations");
      MOVED_TO_GLOBAL.add("compatibility_mode");
      MOVED_TO_GLOBAL.add("emergency_tone");
      MOVED_TO_GLOBAL.add("call_auto_retry");
      MOVED_TO_GLOBAL.add("debug_app");
      MOVED_TO_GLOBAL.add("wait_for_debugger");
      MOVED_TO_GLOBAL.add("always_finish_activities");
      MOVED_TO_GLOBAL.add("tzinfo_content_url");
      MOVED_TO_GLOBAL.add("tzinfo_metadata_url");
      MOVED_TO_GLOBAL.add("selinux_content_url");
      MOVED_TO_GLOBAL.add("selinux_metadata_url");
      MOVED_TO_GLOBAL.add("sms_short_codes_content_url");
      MOVED_TO_GLOBAL.add("sms_short_codes_metadata_url");
      MOVED_TO_GLOBAL.add("cert_pin_content_url");
      MOVED_TO_GLOBAL.add("cert_pin_metadata_url");
      sBooleanValidator = new DiscreteValueValidator(new String[] { "0", "1" });
      sNonNegativeIntegerValidator = new Validator()
      {
        public boolean validate(String paramAnonymousString)
        {
          boolean bool = false;
          try
          {
            int i = Integer.parseInt(paramAnonymousString);
            if (i >= 0) {
              bool = true;
            }
            return bool;
          }
          catch (NumberFormatException paramAnonymousString) {}
          return false;
        }
      };
      sUriValidator = new Validator()
      {
        public boolean validate(String paramAnonymousString)
        {
          try
          {
            Uri.decode(paramAnonymousString);
            return true;
          }
          catch (IllegalArgumentException paramAnonymousString) {}
          return false;
        }
      };
      sLenientIpAddressValidator = new Validator()
      {
        private static final int MAX_IPV6_LENGTH = 45;
        
        public boolean validate(String paramAnonymousString)
        {
          return paramAnonymousString.length() <= 45;
        }
      };
      END_BUTTON_BEHAVIOR_VALIDATOR = new InclusiveIntegerRangeValidator(0, 3);
      ADVANCED_SETTINGS_VALIDATOR = sBooleanValidator;
      WIFI_USE_STATIC_IP_VALIDATOR = sBooleanValidator;
      WIFI_STATIC_IP_VALIDATOR = sLenientIpAddressValidator;
      WIFI_STATIC_GATEWAY_VALIDATOR = sLenientIpAddressValidator;
      WIFI_STATIC_NETMASK_VALIDATOR = sLenientIpAddressValidator;
      WIFI_STATIC_DNS1_VALIDATOR = sLenientIpAddressValidator;
      WIFI_STATIC_DNS2_VALIDATOR = sLenientIpAddressValidator;
      BLUETOOTH_DISCOVERABILITY_VALIDATOR = new InclusiveIntegerRangeValidator(0, 2);
      BLUETOOTH_DISCOVERABILITY_TIMEOUT_VALIDATOR = sNonNegativeIntegerValidator;
      NEXT_ALARM_FORMATTED_VALIDATOR = new Validator()
      {
        private static final int MAX_LENGTH = 1000;
        
        public boolean validate(String paramAnonymousString)
        {
          return (paramAnonymousString == null) || (paramAnonymousString.length() < 1000);
        }
      };
      FONT_SCALE_VALIDATOR = new Validator()
      {
        public boolean validate(String paramAnonymousString)
        {
          boolean bool = false;
          try
          {
            float f = Float.parseFloat(paramAnonymousString);
            if (f >= 0.0F) {
              bool = true;
            }
            return bool;
          }
          catch (NumberFormatException paramAnonymousString) {}
          return false;
        }
      };
      DIM_SCREEN_VALIDATOR = sBooleanValidator;
      SCREEN_OFF_TIMEOUT_VALIDATOR = sNonNegativeIntegerValidator;
      SCREEN_BRIGHTNESS_VALIDATOR = new InclusiveIntegerRangeValidator(0, 255);
      SCREEN_BRIGHTNESS_MODE_VALIDATOR = sBooleanValidator;
      SCREEN_AUTO_BRIGHTNESS_ADJ_VALIDATOR = new InclusiveFloatRangeValidator(-1.0F, 255.0F);
      MODE_RINGER_STREAMS_AFFECTED_VALIDATOR = sNonNegativeIntegerValidator;
      MUTE_STREAMS_AFFECTED_VALIDATOR = sNonNegativeIntegerValidator;
      VIBRATE_ON_VALIDATOR = sBooleanValidator;
      VIBRATE_INPUT_DEVICES_VALIDATOR = sBooleanValidator;
      MASTER_MONO_VALIDATOR = sBooleanValidator;
      NOTIFICATIONS_USE_RING_VOLUME_VALIDATOR = sBooleanValidator;
      VIBRATE_IN_SILENT_VALIDATOR = sBooleanValidator;
      VOLUME_SETTINGS = new String[] { "volume_voice", "volume_system", "volume_ring", "volume_music", "volume_alarm", "volume_notification", "volume_bluetooth_sco" };
      RINGTONE_VALIDATOR = sUriValidator;
      DEFAULT_RINGTONE_URI = getUriFor("ringtone");
      RINGTONE_CACHE_URI = getUriFor("ringtone_cache");
      RINGTONE_2_VALIDATOR = sUriValidator;
      DEFAULT_RINGTONE_URI_2 = getUriFor("ringtone_2");
      RINGTONE_2_CACHE_URI = getUriFor("ringtone_2_cache");
      NOTIFICATION_SOUND_VALIDATOR = sUriValidator;
      DEFAULT_NOTIFICATION_URI = getUriFor("notification_sound");
      NOTIFICATION_SOUND_CACHE_URI = getUriFor("notification_sound_cache");
      MMS_NOTIFICATION_SOUND_VALIDATOR = sUriValidator;
      DEFAULT_MMS_NOTIFICATION_URI = getUriFor("mms_notification");
      MMS_NOTIFICATION_CACHE_URI = getUriFor("mms_notification_cache");
      ALARM_ALERT_VALIDATOR = sUriValidator;
      DEFAULT_ALARM_ALERT_URI = getUriFor("alarm_alert");
      ALARM_ALERT_CACHE_URI = getUriFor("alarm_alert_cache");
      MEDIA_BUTTON_RECEIVER_VALIDATOR = new Validator()
      {
        public boolean validate(String paramAnonymousString)
        {
          try
          {
            ComponentName.unflattenFromString(paramAnonymousString);
            return true;
          }
          catch (NullPointerException paramAnonymousString) {}
          return false;
        }
      };
      TEXT_AUTO_REPLACE_VALIDATOR = sBooleanValidator;
      TEXT_AUTO_CAPS_VALIDATOR = sBooleanValidator;
      TEXT_AUTO_PUNCTUATE_VALIDATOR = sBooleanValidator;
      TEXT_SHOW_PASSWORD_VALIDATOR = sBooleanValidator;
      SHOW_GTALK_SERVICE_STATUS_VALIDATOR = sBooleanValidator;
      WALLPAPER_ACTIVITY_VALIDATOR = new Validator()
      {
        private static final int MAX_LENGTH = 1000;
        
        public boolean validate(String paramAnonymousString)
        {
          boolean bool = false;
          if ((paramAnonymousString != null) && (paramAnonymousString.length() > 1000)) {
            return false;
          }
          if (ComponentName.unflattenFromString(paramAnonymousString) != null) {
            bool = true;
          }
          return bool;
        }
      };
      TIME_12_24_VALIDATOR = new DiscreteValueValidator(new String[] { "12", "24" });
      DATE_FORMAT_VALIDATOR = new Validator()
      {
        public boolean validate(String paramAnonymousString)
        {
          try
          {
            new SimpleDateFormat(paramAnonymousString);
            return true;
          }
          catch (IllegalArgumentException paramAnonymousString) {}
          return false;
        }
      };
      SETUP_WIZARD_HAS_RUN_VALIDATOR = sBooleanValidator;
      ACCELEROMETER_ROTATION_VALIDATOR = sBooleanValidator;
      USER_ROTATION_VALIDATOR = new InclusiveIntegerRangeValidator(0, 3);
      HIDE_ROTATION_LOCK_TOGGLE_FOR_ACCESSIBILITY_VALIDATOR = sBooleanValidator;
      VIBRATE_WHEN_RINGING_VALIDATOR = sBooleanValidator;
      DTMF_TONE_WHEN_DIALING_VALIDATOR = sBooleanValidator;
      DTMF_TONE_TYPE_WHEN_DIALING_VALIDATOR = sBooleanValidator;
      HEARING_AID_VALIDATOR = sBooleanValidator;
      TTY_MODE_VALIDATOR = new InclusiveIntegerRangeValidator(0, 3);
      SOUND_EFFECTS_ENABLED_VALIDATOR = sBooleanValidator;
      HAPTIC_FEEDBACK_ENABLED_VALIDATOR = sBooleanValidator;
      SHOW_WEB_SUGGESTIONS_VALIDATOR = sBooleanValidator;
      NOTIFICATION_LIGHT_PULSE_VALIDATOR = sBooleanValidator;
      POINTER_LOCATION_VALIDATOR = sBooleanValidator;
      SHOW_TOUCHES_VALIDATOR = sBooleanValidator;
      WINDOW_ORIENTATION_LISTENER_LOG_VALIDATOR = sBooleanValidator;
      LOCKSCREEN_SOUNDS_ENABLED_VALIDATOR = sBooleanValidator;
      LOCKSCREEN_DISABLED_VALIDATOR = sBooleanValidator;
      SIP_RECEIVE_CALLS_VALIDATOR = sBooleanValidator;
      SIP_CALL_OPTIONS_VALIDATOR = new DiscreteValueValidator(new String[] { "SIP_ALWAYS", "SIP_ADDRESS_ONLY" });
      SIP_ALWAYS_VALIDATOR = sBooleanValidator;
      SIP_ADDRESS_ONLY_VALIDATOR = sBooleanValidator;
      SIP_ASK_ME_EACH_TIME_VALIDATOR = sBooleanValidator;
      POINTER_SPEED_VALIDATOR = new InclusiveFloatRangeValidator(-7.0F, 7.0F);
      LOCK_TO_APP_ENABLED_VALIDATOR = sBooleanValidator;
      EGG_MODE_VALIDATOR = new Validator()
      {
        public boolean validate(String paramAnonymousString)
        {
          boolean bool = false;
          try
          {
            long l = Long.parseLong(paramAnonymousString);
            if (l >= 0L) {
              bool = true;
            }
            return bool;
          }
          catch (NumberFormatException paramAnonymousString) {}
          return false;
        }
      };
      SETTINGS_TO_BACKUP = new String[] { "stay_on_while_plugged_in", "wifi_use_static_ip", "wifi_static_ip", "wifi_static_gateway", "wifi_static_netmask", "wifi_static_dns1", "wifi_static_dns2", "bluetooth_discoverability", "bluetooth_discoverability_timeout", "font_scale", "dim_screen", "screen_off_timeout", "screen_brightness", "screen_brightness_mode", "screen_auto_brightness_adj", "vibrate_input_devices", "mode_ringer_streams_affected", "auto_replace", "auto_caps", "auto_punctuate", "show_password", "auto_time", "auto_time_zone", "time_12_24", "date_format", "dtmf_tone", "dtmf_tone_type", "hearing_aid", "tty_mode", "master_mono", "sound_effects_enabled", "haptic_feedback_enabled", "power_sounds_enabled", "dock_sounds_enabled", "lockscreen_sounds_enabled", "show_web_suggestions", "sip_call_options", "sip_receive_calls", "pointer_speed", "vibrate_when_ringing", "ringtone", "lock_to_app_enabled", "notification_sound", "accelerometer_rotation" };
      PUBLIC_SETTINGS = new ArraySet();
      PUBLIC_SETTINGS.add("end_button_behavior");
      PUBLIC_SETTINGS.add("wifi_use_static_ip");
      PUBLIC_SETTINGS.add("wifi_static_ip");
      PUBLIC_SETTINGS.add("wifi_static_gateway");
      PUBLIC_SETTINGS.add("wifi_static_netmask");
      PUBLIC_SETTINGS.add("wifi_static_dns1");
      PUBLIC_SETTINGS.add("wifi_static_dns2");
      PUBLIC_SETTINGS.add("bluetooth_discoverability");
      PUBLIC_SETTINGS.add("bluetooth_discoverability_timeout");
      PUBLIC_SETTINGS.add("next_alarm_formatted");
      PUBLIC_SETTINGS.add("font_scale");
      PUBLIC_SETTINGS.add("dim_screen");
      PUBLIC_SETTINGS.add("screen_off_timeout");
      PUBLIC_SETTINGS.add("screen_brightness");
      PUBLIC_SETTINGS.add("screen_brightness_mode");
      PUBLIC_SETTINGS.add("mode_ringer_streams_affected");
      PUBLIC_SETTINGS.add("mute_streams_affected");
      PUBLIC_SETTINGS.add("vibrate_on");
      PUBLIC_SETTINGS.add("volume_ring");
      PUBLIC_SETTINGS.add("volume_system");
      PUBLIC_SETTINGS.add("volume_voice");
      PUBLIC_SETTINGS.add("volume_music");
      PUBLIC_SETTINGS.add("volume_alarm");
      PUBLIC_SETTINGS.add("volume_notification");
      PUBLIC_SETTINGS.add("volume_bluetooth_sco");
      PUBLIC_SETTINGS.add("ringtone");
      PUBLIC_SETTINGS.add("ringtone_2");
      PUBLIC_SETTINGS.add("notification_sound");
      PUBLIC_SETTINGS.add("mms_notification");
      PUBLIC_SETTINGS.add("alarm_alert");
      PUBLIC_SETTINGS.add("auto_replace");
      PUBLIC_SETTINGS.add("auto_caps");
      PUBLIC_SETTINGS.add("auto_punctuate");
      PUBLIC_SETTINGS.add("show_password");
      PUBLIC_SETTINGS.add("SHOW_GTALK_SERVICE_STATUS");
      PUBLIC_SETTINGS.add("wallpaper_activity");
      PUBLIC_SETTINGS.add("time_12_24");
      PUBLIC_SETTINGS.add("date_format");
      PUBLIC_SETTINGS.add("setup_wizard_has_run");
      PUBLIC_SETTINGS.add("accelerometer_rotation");
      PUBLIC_SETTINGS.add("user_rotation");
      PUBLIC_SETTINGS.add("dtmf_tone");
      PUBLIC_SETTINGS.add("sound_effects_enabled");
      PUBLIC_SETTINGS.add("haptic_feedback_enabled");
      PUBLIC_SETTINGS.add("show_web_suggestions");
      PUBLIC_SETTINGS.add("vibrate_when_ringing");
      PRIVATE_SETTINGS = new ArraySet();
      PRIVATE_SETTINGS.add("wifi_use_static_ip");
      PRIVATE_SETTINGS.add("end_button_behavior");
      PRIVATE_SETTINGS.add("advanced_settings");
      PRIVATE_SETTINGS.add("screen_auto_brightness_adj");
      PRIVATE_SETTINGS.add("vibrate_input_devices");
      PRIVATE_SETTINGS.add("volume_master");
      PRIVATE_SETTINGS.add("master_mono");
      PRIVATE_SETTINGS.add("notifications_use_ring_volume");
      PRIVATE_SETTINGS.add("vibrate_in_silent");
      PRIVATE_SETTINGS.add("media_button_receiver");
      PRIVATE_SETTINGS.add("hide_rotation_lock_toggle_for_accessibility");
      PRIVATE_SETTINGS.add("dtmf_tone_type");
      PRIVATE_SETTINGS.add("hearing_aid");
      PRIVATE_SETTINGS.add("tty_mode");
      PRIVATE_SETTINGS.add("notification_light_pulse");
      PRIVATE_SETTINGS.add("pointer_location");
      PRIVATE_SETTINGS.add("show_touches");
      PRIVATE_SETTINGS.add("window_orientation_listener_log");
      PRIVATE_SETTINGS.add("power_sounds_enabled");
      PRIVATE_SETTINGS.add("dock_sounds_enabled");
      PRIVATE_SETTINGS.add("lockscreen_sounds_enabled");
      PRIVATE_SETTINGS.add("lockscreen.disabled");
      PRIVATE_SETTINGS.add("low_battery_sound");
      PRIVATE_SETTINGS.add("desk_dock_sound");
      PRIVATE_SETTINGS.add("desk_undock_sound");
      PRIVATE_SETTINGS.add("car_dock_sound");
      PRIVATE_SETTINGS.add("car_undock_sound");
      PRIVATE_SETTINGS.add("lock_sound");
      PRIVATE_SETTINGS.add("unlock_sound");
      PRIVATE_SETTINGS.add("sip_receive_calls");
      PRIVATE_SETTINGS.add("sip_call_options");
      PRIVATE_SETTINGS.add("SIP_ALWAYS");
      PRIVATE_SETTINGS.add("SIP_ADDRESS_ONLY");
      PRIVATE_SETTINGS.add("SIP_ASK_ME_EACH_TIME");
      PRIVATE_SETTINGS.add("pointer_speed");
      PRIVATE_SETTINGS.add("lock_to_app_enabled");
      PRIVATE_SETTINGS.add("egg_mode");
      VALIDATORS = new ArrayMap();
      VALIDATORS.put("end_button_behavior", END_BUTTON_BEHAVIOR_VALIDATOR);
      VALIDATORS.put("wifi_use_static_ip", WIFI_USE_STATIC_IP_VALIDATOR);
      VALIDATORS.put("bluetooth_discoverability", BLUETOOTH_DISCOVERABILITY_VALIDATOR);
      VALIDATORS.put("bluetooth_discoverability_timeout", BLUETOOTH_DISCOVERABILITY_TIMEOUT_VALIDATOR);
      VALIDATORS.put("next_alarm_formatted", NEXT_ALARM_FORMATTED_VALIDATOR);
      VALIDATORS.put("font_scale", FONT_SCALE_VALIDATOR);
      VALIDATORS.put("dim_screen", DIM_SCREEN_VALIDATOR);
      VALIDATORS.put("screen_off_timeout", SCREEN_OFF_TIMEOUT_VALIDATOR);
      VALIDATORS.put("screen_brightness", SCREEN_BRIGHTNESS_VALIDATOR);
      VALIDATORS.put("screen_brightness_mode", SCREEN_BRIGHTNESS_MODE_VALIDATOR);
      VALIDATORS.put("mode_ringer_streams_affected", MODE_RINGER_STREAMS_AFFECTED_VALIDATOR);
      VALIDATORS.put("mute_streams_affected", MUTE_STREAMS_AFFECTED_VALIDATOR);
      VALIDATORS.put("vibrate_on", VIBRATE_ON_VALIDATOR);
      VALIDATORS.put("ringtone", RINGTONE_VALIDATOR);
      VALIDATORS.put("notification_sound", NOTIFICATION_SOUND_VALIDATOR);
      VALIDATORS.put("mms_notification", MMS_NOTIFICATION_SOUND_VALIDATOR);
      VALIDATORS.put("alarm_alert", ALARM_ALERT_VALIDATOR);
      VALIDATORS.put("auto_replace", TEXT_AUTO_REPLACE_VALIDATOR);
      VALIDATORS.put("auto_caps", TEXT_AUTO_CAPS_VALIDATOR);
      VALIDATORS.put("auto_punctuate", TEXT_AUTO_PUNCTUATE_VALIDATOR);
      VALIDATORS.put("show_password", TEXT_SHOW_PASSWORD_VALIDATOR);
      VALIDATORS.put("SHOW_GTALK_SERVICE_STATUS", SHOW_GTALK_SERVICE_STATUS_VALIDATOR);
      VALIDATORS.put("wallpaper_activity", WALLPAPER_ACTIVITY_VALIDATOR);
      VALIDATORS.put("time_12_24", TIME_12_24_VALIDATOR);
      VALIDATORS.put("date_format", DATE_FORMAT_VALIDATOR);
      VALIDATORS.put("setup_wizard_has_run", SETUP_WIZARD_HAS_RUN_VALIDATOR);
      VALIDATORS.put("accelerometer_rotation", ACCELEROMETER_ROTATION_VALIDATOR);
      VALIDATORS.put("user_rotation", USER_ROTATION_VALIDATOR);
      VALIDATORS.put("dtmf_tone", DTMF_TONE_WHEN_DIALING_VALIDATOR);
      VALIDATORS.put("sound_effects_enabled", SOUND_EFFECTS_ENABLED_VALIDATOR);
      VALIDATORS.put("haptic_feedback_enabled", HAPTIC_FEEDBACK_ENABLED_VALIDATOR);
      VALIDATORS.put("show_web_suggestions", SHOW_WEB_SUGGESTIONS_VALIDATOR);
      VALIDATORS.put("wifi_use_static_ip", WIFI_USE_STATIC_IP_VALIDATOR);
      VALIDATORS.put("end_button_behavior", END_BUTTON_BEHAVIOR_VALIDATOR);
      VALIDATORS.put("advanced_settings", ADVANCED_SETTINGS_VALIDATOR);
      VALIDATORS.put("screen_auto_brightness_adj", SCREEN_AUTO_BRIGHTNESS_ADJ_VALIDATOR);
      VALIDATORS.put("vibrate_input_devices", VIBRATE_INPUT_DEVICES_VALIDATOR);
      VALIDATORS.put("master_mono", MASTER_MONO_VALIDATOR);
      VALIDATORS.put("notifications_use_ring_volume", NOTIFICATIONS_USE_RING_VOLUME_VALIDATOR);
      VALIDATORS.put("vibrate_in_silent", VIBRATE_IN_SILENT_VALIDATOR);
      VALIDATORS.put("media_button_receiver", MEDIA_BUTTON_RECEIVER_VALIDATOR);
      VALIDATORS.put("hide_rotation_lock_toggle_for_accessibility", HIDE_ROTATION_LOCK_TOGGLE_FOR_ACCESSIBILITY_VALIDATOR);
      VALIDATORS.put("vibrate_when_ringing", VIBRATE_WHEN_RINGING_VALIDATOR);
      VALIDATORS.put("dtmf_tone_type", DTMF_TONE_TYPE_WHEN_DIALING_VALIDATOR);
      VALIDATORS.put("hearing_aid", HEARING_AID_VALIDATOR);
      VALIDATORS.put("tty_mode", TTY_MODE_VALIDATOR);
      VALIDATORS.put("notification_light_pulse", NOTIFICATION_LIGHT_PULSE_VALIDATOR);
      VALIDATORS.put("pointer_location", POINTER_LOCATION_VALIDATOR);
      VALIDATORS.put("show_touches", SHOW_TOUCHES_VALIDATOR);
      VALIDATORS.put("window_orientation_listener_log", WINDOW_ORIENTATION_LISTENER_LOG_VALIDATOR);
      VALIDATORS.put("lockscreen_sounds_enabled", LOCKSCREEN_SOUNDS_ENABLED_VALIDATOR);
      VALIDATORS.put("lockscreen.disabled", LOCKSCREEN_DISABLED_VALIDATOR);
      VALIDATORS.put("sip_receive_calls", SIP_RECEIVE_CALLS_VALIDATOR);
      VALIDATORS.put("sip_call_options", SIP_CALL_OPTIONS_VALIDATOR);
      VALIDATORS.put("SIP_ALWAYS", SIP_ALWAYS_VALIDATOR);
      VALIDATORS.put("SIP_ADDRESS_ONLY", SIP_ADDRESS_ONLY_VALIDATOR);
      VALIDATORS.put("SIP_ASK_ME_EACH_TIME", SIP_ASK_ME_EACH_TIME_VALIDATOR);
      VALIDATORS.put("pointer_speed", POINTER_SPEED_VALIDATOR);
      VALIDATORS.put("lock_to_app_enabled", LOCK_TO_APP_ENABLED_VALIDATOR);
      VALIDATORS.put("egg_mode", EGG_MODE_VALIDATOR);
      VALIDATORS.put("wifi_static_ip", WIFI_STATIC_IP_VALIDATOR);
      VALIDATORS.put("wifi_static_gateway", WIFI_STATIC_GATEWAY_VALIDATOR);
      VALIDATORS.put("wifi_static_netmask", WIFI_STATIC_NETMASK_VALIDATOR);
      VALIDATORS.put("wifi_static_dns1", WIFI_STATIC_DNS1_VALIDATOR);
      VALIDATORS.put("wifi_static_dns2", WIFI_STATIC_DNS2_VALIDATOR);
      CLONE_TO_MANAGED_PROFILE = new ArraySet();
      CLONE_TO_MANAGED_PROFILE.add("date_format");
      CLONE_TO_MANAGED_PROFILE.add("haptic_feedback_enabled");
      CLONE_TO_MANAGED_PROFILE.add("sound_effects_enabled");
      CLONE_TO_MANAGED_PROFILE.add("show_password");
      CLONE_TO_MANAGED_PROFILE.add("time_12_24");
    }
    
    public static void adjustConfigurationForUser(ContentResolver paramContentResolver, Configuration paramConfiguration, int paramInt, boolean paramBoolean)
    {
      paramConfiguration.fontScale = getFloatForUser(paramContentResolver, "font_scale", 1.0F, paramInt);
      if (paramConfiguration.fontScale < 0.0F) {
        paramConfiguration.fontScale = 1.0F;
      }
      String str = getStringForUser(paramContentResolver, "system_locales", paramInt);
      if (str != null) {
        paramConfiguration.setLocales(LocaleList.forLanguageTags(str));
      }
      while (!paramBoolean) {
        return;
      }
      putStringForUser(paramContentResolver, "system_locales", paramConfiguration.getLocales().toLanguageTags(), paramInt);
    }
    
    public static boolean canWrite(Context paramContext)
    {
      return Settings.isCallingPackageAllowedToWriteSettings(paramContext, Process.myUid(), paramContext.getOpPackageName(), false);
    }
    
    public static void clearConfiguration(Configuration paramConfiguration)
    {
      paramConfiguration.fontScale = 0.0F;
      if ((paramConfiguration.userSetLocale) || (paramConfiguration.getLocales().isEmpty())) {
        return;
      }
      paramConfiguration.clearLocales();
    }
    
    public static void getCloneToManagedProfileSettings(Set<String> paramSet)
    {
      paramSet.addAll(CLONE_TO_MANAGED_PROFILE);
    }
    
    public static void getConfiguration(ContentResolver paramContentResolver, Configuration paramConfiguration)
    {
      adjustConfigurationForUser(paramContentResolver, paramConfiguration, UserHandle.myUserId(), false);
    }
    
    public static float getFloat(ContentResolver paramContentResolver, String paramString)
      throws Settings.SettingNotFoundException
    {
      return getFloatForUser(paramContentResolver, paramString, UserHandle.myUserId());
    }
    
    public static float getFloat(ContentResolver paramContentResolver, String paramString, float paramFloat)
    {
      return getFloatForUser(paramContentResolver, paramString, paramFloat, UserHandle.myUserId());
    }
    
    public static float getFloatForUser(ContentResolver paramContentResolver, String paramString, float paramFloat, int paramInt)
    {
      paramContentResolver = getStringForUser(paramContentResolver, paramString, paramInt);
      float f = paramFloat;
      if (paramContentResolver != null) {}
      try
      {
        f = Float.parseFloat(paramContentResolver);
        return f;
      }
      catch (NumberFormatException paramContentResolver) {}
      return paramFloat;
    }
    
    public static float getFloatForUser(ContentResolver paramContentResolver, String paramString, int paramInt)
      throws Settings.SettingNotFoundException
    {
      paramContentResolver = getStringForUser(paramContentResolver, paramString, paramInt);
      if (paramContentResolver == null) {
        throw new Settings.SettingNotFoundException(paramString);
      }
      try
      {
        float f = Float.parseFloat(paramContentResolver);
        return f;
      }
      catch (NumberFormatException paramContentResolver)
      {
        throw new Settings.SettingNotFoundException(paramString);
      }
    }
    
    public static int getInt(ContentResolver paramContentResolver, String paramString)
      throws Settings.SettingNotFoundException
    {
      return getIntForUser(paramContentResolver, paramString, UserHandle.myUserId());
    }
    
    public static int getInt(ContentResolver paramContentResolver, String paramString, int paramInt)
    {
      return getIntForUser(paramContentResolver, paramString, paramInt, UserHandle.myUserId());
    }
    
    public static int getIntForUser(ContentResolver paramContentResolver, String paramString, int paramInt)
      throws Settings.SettingNotFoundException
    {
      paramContentResolver = getStringForUser(paramContentResolver, paramString, paramInt);
      try
      {
        paramInt = Integer.parseInt(paramContentResolver);
        return paramInt;
      }
      catch (NumberFormatException paramContentResolver)
      {
        throw new Settings.SettingNotFoundException(paramString);
      }
    }
    
    public static int getIntForUser(ContentResolver paramContentResolver, String paramString, int paramInt1, int paramInt2)
    {
      paramContentResolver = getStringForUser(paramContentResolver, paramString, paramInt2);
      paramInt2 = paramInt1;
      if (paramContentResolver != null) {}
      try
      {
        paramInt2 = Integer.parseInt(paramContentResolver);
        return paramInt2;
      }
      catch (NumberFormatException paramContentResolver) {}
      return paramInt1;
    }
    
    public static long getLong(ContentResolver paramContentResolver, String paramString)
      throws Settings.SettingNotFoundException
    {
      return getLongForUser(paramContentResolver, paramString, UserHandle.myUserId());
    }
    
    public static long getLong(ContentResolver paramContentResolver, String paramString, long paramLong)
    {
      return getLongForUser(paramContentResolver, paramString, paramLong, UserHandle.myUserId());
    }
    
    public static long getLongForUser(ContentResolver paramContentResolver, String paramString, int paramInt)
      throws Settings.SettingNotFoundException
    {
      paramContentResolver = getStringForUser(paramContentResolver, paramString, paramInt);
      try
      {
        long l = Long.parseLong(paramContentResolver);
        return l;
      }
      catch (NumberFormatException paramContentResolver)
      {
        throw new Settings.SettingNotFoundException(paramString);
      }
    }
    
    public static long getLongForUser(ContentResolver paramContentResolver, String paramString, long paramLong, int paramInt)
    {
      paramContentResolver = getStringForUser(paramContentResolver, paramString, paramInt);
      if (paramContentResolver != null) {}
      try
      {
        long l = Long.parseLong(paramContentResolver);
        return l;
      }
      catch (NumberFormatException paramContentResolver) {}
      return paramLong;
      return paramLong;
    }
    
    public static void getMovedToGlobalSettings(Set<String> paramSet)
    {
      paramSet.addAll(MOVED_TO_GLOBAL);
      paramSet.addAll(MOVED_TO_SECURE_THEN_GLOBAL);
    }
    
    public static void getMovedToSecureSettings(Set<String> paramSet)
    {
      paramSet.addAll(MOVED_TO_SECURE);
    }
    
    public static void getNonLegacyMovedKeys(HashSet<String> paramHashSet)
    {
      paramHashSet.addAll(MOVED_TO_GLOBAL);
    }
    
    @Deprecated
    public static boolean getShowGTalkServiceStatus(ContentResolver paramContentResolver)
    {
      return getShowGTalkServiceStatusForUser(paramContentResolver, UserHandle.myUserId());
    }
    
    public static boolean getShowGTalkServiceStatusForUser(ContentResolver paramContentResolver, int paramInt)
    {
      boolean bool = false;
      if (getIntForUser(paramContentResolver, "SHOW_GTALK_SERVICE_STATUS", 0, paramInt) != 0) {
        bool = true;
      }
      return bool;
    }
    
    public static String getString(ContentResolver paramContentResolver, String paramString)
    {
      return getStringForUser(paramContentResolver, paramString, UserHandle.myUserId());
    }
    
    public static String getStringForUser(ContentResolver paramContentResolver, String paramString, int paramInt)
    {
      SeempLog.record(SeempLog.getSeempGetApiIdFromValue(paramString));
      if (MOVED_TO_SECURE.contains(paramString))
      {
        Log.w("Settings", "Setting " + paramString + " has moved from android.provider.Settings.System" + " to android.provider.Settings.Secure, returning read-only value.");
        return Settings.Secure.getStringForUser(paramContentResolver, paramString, paramInt);
      }
      if ((MOVED_TO_GLOBAL.contains(paramString)) || (MOVED_TO_SECURE_THEN_GLOBAL.contains(paramString)))
      {
        Log.w("Settings", "Setting " + paramString + " has moved from android.provider.Settings.System" + " to android.provider.Settings.Global, returning read-only value.");
        return Settings.Global.getStringForUser(paramContentResolver, paramString, paramInt);
      }
      return sNameValueCache.getStringForUser(paramContentResolver, paramString, paramInt);
    }
    
    public static Uri getUriFor(String paramString)
    {
      if (MOVED_TO_SECURE.contains(paramString))
      {
        Log.w("Settings", "Setting " + paramString + " has moved from android.provider.Settings.System" + " to android.provider.Settings.Secure, returning Secure URI.");
        return Settings.Secure.getUriFor(Settings.Secure.CONTENT_URI, paramString);
      }
      if ((MOVED_TO_GLOBAL.contains(paramString)) || (MOVED_TO_SECURE_THEN_GLOBAL.contains(paramString)))
      {
        Log.w("Settings", "Setting " + paramString + " has moved from android.provider.Settings.System" + " to android.provider.Settings.Global, returning read-only global URI.");
        return Settings.Global.getUriFor(Settings.Global.CONTENT_URI, paramString);
      }
      return getUriFor(CONTENT_URI, paramString);
    }
    
    public static boolean hasInterestingConfigurationChanges(int paramInt)
    {
      return ((0x40000000 & paramInt) != 0) || ((paramInt & 0x4) != 0);
    }
    
    public static boolean putConfiguration(ContentResolver paramContentResolver, Configuration paramConfiguration)
    {
      return putConfigurationForUser(paramContentResolver, paramConfiguration, UserHandle.myUserId());
    }
    
    public static boolean putConfigurationForUser(ContentResolver paramContentResolver, Configuration paramConfiguration, int paramInt)
    {
      if (putFloatForUser(paramContentResolver, "font_scale", paramConfiguration.fontScale, paramInt)) {
        return putStringForUser(paramContentResolver, "system_locales", paramConfiguration.getLocales().toLanguageTags(), paramInt);
      }
      return false;
    }
    
    public static boolean putFloat(ContentResolver paramContentResolver, String paramString, float paramFloat)
    {
      return putFloatForUser(paramContentResolver, paramString, paramFloat, UserHandle.myUserId());
    }
    
    public static boolean putFloatForUser(ContentResolver paramContentResolver, String paramString, float paramFloat, int paramInt)
    {
      return putStringForUser(paramContentResolver, paramString, Float.toString(paramFloat), paramInt);
    }
    
    public static boolean putInt(ContentResolver paramContentResolver, String paramString, int paramInt)
    {
      return putIntForUser(paramContentResolver, paramString, paramInt, UserHandle.myUserId());
    }
    
    public static boolean putIntForUser(ContentResolver paramContentResolver, String paramString, int paramInt1, int paramInt2)
    {
      return putStringForUser(paramContentResolver, paramString, Integer.toString(paramInt1), paramInt2);
    }
    
    public static boolean putLong(ContentResolver paramContentResolver, String paramString, long paramLong)
    {
      return putLongForUser(paramContentResolver, paramString, paramLong, UserHandle.myUserId());
    }
    
    public static boolean putLongForUser(ContentResolver paramContentResolver, String paramString, long paramLong, int paramInt)
    {
      return putStringForUser(paramContentResolver, paramString, Long.toString(paramLong), paramInt);
    }
    
    public static boolean putString(ContentResolver paramContentResolver, String paramString1, String paramString2)
    {
      return putStringForUser(paramContentResolver, paramString1, paramString2, UserHandle.myUserId());
    }
    
    public static boolean putStringForUser(ContentResolver paramContentResolver, String paramString1, String paramString2, int paramInt)
    {
      SeempLog.record(SeempLog.getSeempPutApiIdFromValue(paramString1));
      if (MOVED_TO_SECURE.contains(paramString1))
      {
        Log.w("Settings", "Setting " + paramString1 + " has moved from android.provider.Settings.System" + " to android.provider.Settings.Secure, value is unchanged.");
        return false;
      }
      if ((MOVED_TO_GLOBAL.contains(paramString1)) || (MOVED_TO_SECURE_THEN_GLOBAL.contains(paramString1)))
      {
        Log.w("Settings", "Setting " + paramString1 + " has moved from android.provider.Settings.System" + " to android.provider.Settings.Global, value is unchanged.");
        return false;
      }
      return sNameValueCache.putStringForUser(paramContentResolver, paramString1, paramString2, paramInt);
    }
    
    @Deprecated
    public static void setShowGTalkServiceStatus(ContentResolver paramContentResolver, boolean paramBoolean)
    {
      setShowGTalkServiceStatusForUser(paramContentResolver, paramBoolean, UserHandle.myUserId());
    }
    
    @Deprecated
    public static void setShowGTalkServiceStatusForUser(ContentResolver paramContentResolver, boolean paramBoolean, int paramInt)
    {
      if (paramBoolean) {}
      for (int i = 1;; i = 0)
      {
        putIntForUser(paramContentResolver, "SHOW_GTALK_SERVICE_STATUS", i, paramInt);
        return;
      }
    }
    
    private static final class DiscreteValueValidator
      implements Settings.System.Validator
    {
      private final String[] mValues;
      
      public DiscreteValueValidator(String[] paramArrayOfString)
      {
        this.mValues = paramArrayOfString;
      }
      
      public boolean validate(String paramString)
      {
        return ArrayUtils.contains(this.mValues, paramString);
      }
    }
    
    private static final class InclusiveFloatRangeValidator
      implements Settings.System.Validator
    {
      private final float mMax;
      private final float mMin;
      
      public InclusiveFloatRangeValidator(float paramFloat1, float paramFloat2)
      {
        this.mMin = paramFloat1;
        this.mMax = paramFloat2;
      }
      
      public boolean validate(String paramString)
      {
        boolean bool2 = false;
        try
        {
          float f1 = Float.parseFloat(paramString);
          boolean bool1 = bool2;
          if (f1 >= this.mMin)
          {
            float f2 = this.mMax;
            bool1 = bool2;
            if (f1 <= f2) {
              bool1 = true;
            }
          }
          return bool1;
        }
        catch (NumberFormatException paramString) {}
        return false;
      }
    }
    
    private static final class InclusiveIntegerRangeValidator
      implements Settings.System.Validator
    {
      private final int mMax;
      private final int mMin;
      
      public InclusiveIntegerRangeValidator(int paramInt1, int paramInt2)
      {
        this.mMin = paramInt1;
        this.mMax = paramInt2;
      }
      
      public boolean validate(String paramString)
      {
        boolean bool2 = false;
        try
        {
          int i = Integer.parseInt(paramString);
          boolean bool1 = bool2;
          if (i >= this.mMin)
          {
            int j = this.mMax;
            bool1 = bool2;
            if (i <= j) {
              bool1 = true;
            }
          }
          return bool1;
        }
        catch (NumberFormatException paramString) {}
        return false;
      }
    }
    
    public static abstract interface Validator
    {
      public abstract boolean validate(String paramString);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/provider/Settings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */