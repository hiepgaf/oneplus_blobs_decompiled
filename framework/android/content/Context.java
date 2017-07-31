package android.content;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Messenger;
import android.os.UserHandle;
import android.util.AttributeSet;
import android.view.Display;
import android.view.DisplayAdjustments;
import android.view.ViewDebug.ExportedProperty;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public abstract class Context
{
  public static final String ACCESSIBILITY_SERVICE = "accessibility";
  public static final String ACCOUNT_SERVICE = "account";
  public static final String ACTIVITY_SERVICE = "activity";
  public static final String ALARM_SERVICE = "alarm";
  public static final String APPWIDGET_SERVICE = "appwidget";
  public static final String APP_OPS_SERVICE = "appops";
  public static final String AUDIO_SERVICE = "audio";
  public static final String BACKUP_SERVICE = "backup";
  public static final String BATTERY_SERVICE = "batterymanager";
  public static final int BIND_ABOVE_CLIENT = 8;
  public static final int BIND_ADJUST_WITH_ACTIVITY = 128;
  public static final int BIND_ALLOW_OOM_MANAGEMENT = 16;
  public static final int BIND_ALLOW_WHITELIST_MANAGEMENT = 16777216;
  public static final int BIND_AUTO_CREATE = 1;
  public static final int BIND_DEBUG_UNBIND = 2;
  public static final int BIND_EXTERNAL_SERVICE = Integer.MIN_VALUE;
  public static final int BIND_FOREGROUND_SERVICE = 67108864;
  public static final int BIND_FOREGROUND_SERVICE_WHILE_AWAKE = 33554432;
  public static final int BIND_IMPORTANT = 64;
  public static final int BIND_NOT_FOREGROUND = 4;
  public static final int BIND_NOT_VISIBLE = 1073741824;
  public static final int BIND_SHOWING_UI = 536870912;
  public static final int BIND_TREAT_LIKE_ACTIVITY = 134217728;
  public static final int BIND_VISIBLE = 268435456;
  public static final int BIND_WAIVE_PRIORITY = 32;
  public static final String BLUETOOTH_SERVICE = "bluetooth";
  public static final String CAMERA_SERVICE = "camera";
  public static final String CAPTIONING_SERVICE = "captioning";
  public static final String CARRIER_CONFIG_SERVICE = "carrier_config";
  public static final String CLIPBOARD_SERVICE = "clipboard";
  public static final String CONNECTIVITY_SERVICE = "connectivity";
  public static final String CONSUMER_IR_SERVICE = "consumer_ir";
  public static final String CONTEXTHUB_SERVICE = "contexthub";
  public static final int CONTEXT_CREDENTIAL_PROTECTED_STORAGE = 16;
  public static final int CONTEXT_DEVICE_PROTECTED_STORAGE = 8;
  public static final int CONTEXT_IGNORE_SECURITY = 2;
  public static final int CONTEXT_INCLUDE_CODE = 1;
  public static final int CONTEXT_REGISTER_PACKAGE = 1073741824;
  public static final int CONTEXT_RESTRICTED = 4;
  public static final String COUNTRY_DETECTOR = "country_detector";
  public static final String DEVICE_IDLE_CONTROLLER = "deviceidle";
  public static final String DEVICE_POLICY_SERVICE = "device_policy";
  public static final String DISPLAY_SERVICE = "display";
  public static final String DOWNLOAD_SERVICE = "download";
  public static final String DROPBOX_SERVICE = "dropbox";
  public static final String ETHERNET_SERVICE = "ethernet";
  public static final String FINGERPRINT_SERVICE = "fingerprint";
  public static final String GATEKEEPER_SERVICE = "android.service.gatekeeper.IGateKeeperService";
  public static final String HARDWARE_PROPERTIES_SERVICE = "hardware_properties";
  public static final String HDMI_CONTROL_SERVICE = "hdmi_control";
  public static final String INPUT_METHOD_SERVICE = "input_method";
  public static final String INPUT_SERVICE = "input";
  public static final String JOB_SCHEDULER_SERVICE = "jobscheduler";
  public static final String KEYGUARD_SERVICE = "keyguard";
  public static final String LAUNCHER_APPS_SERVICE = "launcherapps";
  public static final String LAYOUT_INFLATER_SERVICE = "layout_inflater";
  public static final String LOCATION_SERVICE = "location";
  public static final String LONGSCREENSHOT_SERVICE = "longshot";
  public static final String MEDIA_PROJECTION_SERVICE = "media_projection";
  public static final String MEDIA_ROUTER_SERVICE = "media_router";
  public static final String MEDIA_SESSION_SERVICE = "media_session";
  public static final String MIDI_SERVICE = "midi";
  public static final int MODE_APPEND = 32768;
  public static final int MODE_ENABLE_WRITE_AHEAD_LOGGING = 8;
  @Deprecated
  public static final int MODE_MULTI_PROCESS = 4;
  public static final int MODE_NO_LOCALIZED_COLLATORS = 16;
  public static final int MODE_PRIVATE = 0;
  @Deprecated
  public static final int MODE_WORLD_READABLE = 1;
  @Deprecated
  public static final int MODE_WORLD_WRITEABLE = 2;
  public static final String NETWORKMANAGEMENT_SERVICE = "network_management";
  public static final String NETWORK_POLICY_SERVICE = "netpolicy";
  public static final String NETWORK_SCORE_SERVICE = "network_score";
  public static final String NETWORK_STATS_SERVICE = "netstats";
  public static final String NFC_SERVICE = "nfc";
  public static final String NIGHTDISPLAY_SERVICE = "nightdisplay";
  public static final String NOTIFICATION_SERVICE = "notification";
  public static final String NSD_SERVICE = "servicediscovery";
  public static final String ONEPLUS_NFC_SERVICE = "oneplus_nfc_service";
  public static final String PERSISTENT_DATA_BLOCK_SERVICE = "persistent_data_block";
  public static final String POWER_SERVICE = "power";
  public static final String PRINT_SERVICE = "print";
  public static final String RADIO_SERVICE = "radio";
  public static final String RECOVERY_SERVICE = "recovery";
  public static final String RESTRICTIONS_SERVICE = "restrictions";
  public static final String SEARCH_SERVICE = "search";
  public static final String SENSOR_SERVICE = "sensor";
  public static final String SERIAL_SERVICE = "serial";
  public static final String SHORTCUT_SERVICE = "shortcut";
  public static final String SIP_SERVICE = "sip";
  public static final String SMART_DISPLAY_SERVICE = "smartdisplay";
  public static final String SOUND_TRIGGER_SERVICE = "soundtrigger";
  public static final String STATUS_BAR_SERVICE = "statusbar";
  public static final String STORAGE_SERVICE = "storage";
  public static final String SYSTEM_HEALTH_SERVICE = "systemhealth";
  public static final String TELECOM_SERVICE = "telecom";
  public static final String TELEPHONY_SERVICE = "phone";
  public static final String TELEPHONY_SUBSCRIPTION_SERVICE = "telephony_subscription_service";
  public static final String TEXT_SERVICES_MANAGER_SERVICE = "textservices";
  public static final String THREEKEY_SERVICE = "threekey";
  public static final String TRUST_SERVICE = "trust";
  public static final String TV_INPUT_SERVICE = "tv_input";
  public static final String UI_MODE_SERVICE = "uimode";
  public static final String UPDATE_LOCK_SERVICE = "updatelock";
  public static final String USAGE_STATS_SERVICE = "usagestats";
  public static final String USB_SERVICE = "usb";
  public static final String USER_SERVICE = "user";
  public static final String VIBRATOR_SERVICE = "vibrator";
  public static final String VOICE_INTERACTION_MANAGER_SERVICE = "voiceinteraction";
  public static final String WALLPAPER_SERVICE = "wallpaper";
  public static final String WIFI_NAN_SERVICE = "wifinan";
  public static final String WIFI_P2P_SERVICE = "wifip2p";
  public static final String WIFI_RTT_SERVICE = "rttmanager";
  public static final String WIFI_SCANNING_SERVICE = "wifiscanner";
  public static final String WIFI_SERVICE = "wifi";
  public static final String WINDOW_SERVICE = "window";
  
  public abstract boolean bindService(Intent paramIntent, ServiceConnection paramServiceConnection, int paramInt);
  
  public boolean bindServiceAsUser(Intent paramIntent, ServiceConnection paramServiceConnection, int paramInt, Handler paramHandler, UserHandle paramUserHandle)
  {
    throw new RuntimeException("Not implemented. Must override in a subclass.");
  }
  
  public boolean bindServiceAsUser(Intent paramIntent, ServiceConnection paramServiceConnection, int paramInt, UserHandle paramUserHandle)
  {
    throw new RuntimeException("Not implemented. Must override in a subclass.");
  }
  
  public boolean canStartActivityForResult()
  {
    return false;
  }
  
  public abstract int checkCallingOrSelfPermission(String paramString);
  
  public abstract int checkCallingOrSelfUriPermission(Uri paramUri, int paramInt);
  
  public abstract int checkCallingPermission(String paramString);
  
  public abstract int checkCallingUriPermission(Uri paramUri, int paramInt);
  
  public abstract boolean checkNeedToShowPermissionRequst();
  
  public abstract int checkPermission(String paramString, int paramInt1, int paramInt2);
  
  public abstract int checkPermission(String paramString, int paramInt1, int paramInt2, IBinder paramIBinder);
  
  public abstract int checkSelfPermission(String paramString);
  
  public abstract int checkUriPermission(Uri paramUri, int paramInt1, int paramInt2, int paramInt3);
  
  public abstract int checkUriPermission(Uri paramUri, int paramInt1, int paramInt2, int paramInt3, IBinder paramIBinder);
  
  public abstract int checkUriPermission(Uri paramUri, String paramString1, String paramString2, int paramInt1, int paramInt2, int paramInt3);
  
  @Deprecated
  public abstract void clearWallpaper()
    throws IOException;
  
  public abstract Context createApplicationContext(ApplicationInfo paramApplicationInfo, int paramInt)
    throws PackageManager.NameNotFoundException;
  
  public abstract Context createConfigurationContext(Configuration paramConfiguration);
  
  @Deprecated
  public Context createCredentialEncryptedStorageContext()
  {
    return createCredentialProtectedStorageContext();
  }
  
  public abstract Context createCredentialProtectedStorageContext();
  
  @Deprecated
  public Context createDeviceEncryptedStorageContext()
  {
    return createDeviceProtectedStorageContext();
  }
  
  public abstract Context createDeviceProtectedStorageContext();
  
  public abstract Context createDisplayContext(Display paramDisplay);
  
  public abstract Context createPackageContext(String paramString, int paramInt)
    throws PackageManager.NameNotFoundException;
  
  public abstract Context createPackageContextAsUser(String paramString, int paramInt, UserHandle paramUserHandle)
    throws PackageManager.NameNotFoundException;
  
  public abstract String[] databaseList();
  
  public abstract boolean deleteDatabase(String paramString);
  
  public abstract boolean deleteFile(String paramString);
  
  public abstract boolean deleteSharedPreferences(String paramString);
  
  public abstract void enforceCallingOrSelfPermission(String paramString1, String paramString2);
  
  public abstract void enforceCallingOrSelfUriPermission(Uri paramUri, int paramInt, String paramString);
  
  public abstract void enforceCallingPermission(String paramString1, String paramString2);
  
  public abstract void enforceCallingUriPermission(Uri paramUri, int paramInt, String paramString);
  
  public abstract void enforcePermission(String paramString1, int paramInt1, int paramInt2, String paramString2);
  
  public abstract void enforceUriPermission(Uri paramUri, int paramInt1, int paramInt2, int paramInt3, String paramString);
  
  public abstract void enforceUriPermission(Uri paramUri, String paramString1, String paramString2, int paramInt1, int paramInt2, int paramInt3, String paramString3);
  
  public abstract String[] fileList();
  
  public abstract Context getApplicationContext();
  
  public abstract ApplicationInfo getApplicationInfo();
  
  public abstract AssetManager getAssets();
  
  public abstract String getBasePackageName();
  
  public abstract File getCacheDir();
  
  public abstract ClassLoader getClassLoader();
  
  public abstract File getCodeCacheDir();
  
  public final int getColor(int paramInt)
  {
    return getResources().getColor(paramInt, getTheme());
  }
  
  public final ColorStateList getColorStateList(int paramInt)
  {
    return getResources().getColorStateList(paramInt, getTheme());
  }
  
  public abstract ContentResolver getContentResolver();
  
  public abstract File getDataDir();
  
  public abstract File getDatabasePath(String paramString);
  
  public abstract File getDir(String paramString, int paramInt);
  
  public abstract Display getDisplay();
  
  public abstract DisplayAdjustments getDisplayAdjustments(int paramInt);
  
  public final Drawable getDrawable(int paramInt)
  {
    return getResources().getDrawable(paramInt, getTheme());
  }
  
  public abstract File getExternalCacheDir();
  
  public abstract File[] getExternalCacheDirs();
  
  public abstract File getExternalFilesDir(String paramString);
  
  public abstract File[] getExternalFilesDirs(String paramString);
  
  public abstract File[] getExternalMediaDirs();
  
  public abstract File getFileStreamPath(String paramString);
  
  public abstract File getFilesDir();
  
  public abstract Looper getMainLooper();
  
  public abstract File getNoBackupFilesDir();
  
  public abstract File getObbDir();
  
  public abstract File[] getObbDirs();
  
  public abstract String getOpPackageName();
  
  public abstract String getPackageCodePath();
  
  public abstract PackageManager getPackageManager();
  
  public abstract String getPackageName();
  
  public abstract String getPackageResourcePath();
  
  public abstract Messenger getPermissionService(int paramInt);
  
  public abstract Resources getResources();
  
  public abstract SharedPreferences getSharedPreferences(File paramFile, int paramInt);
  
  public abstract SharedPreferences getSharedPreferences(String paramString, int paramInt);
  
  public abstract File getSharedPreferencesPath(String paramString);
  
  @Deprecated
  public File getSharedPrefsFile(String paramString)
  {
    return getSharedPreferencesPath(paramString);
  }
  
  public final String getString(int paramInt)
  {
    return getResources().getString(paramInt);
  }
  
  public final String getString(int paramInt, Object... paramVarArgs)
  {
    return getResources().getString(paramInt, paramVarArgs);
  }
  
  public final <T> T getSystemService(Class<T> paramClass)
  {
    Object localObject = null;
    String str = getSystemServiceName(paramClass);
    paramClass = (Class<T>)localObject;
    if (str != null) {
      paramClass = getSystemService(str);
    }
    return paramClass;
  }
  
  public abstract Object getSystemService(String paramString);
  
  public abstract String getSystemServiceName(Class<?> paramClass);
  
  public final CharSequence getText(int paramInt)
  {
    return getResources().getText(paramInt);
  }
  
  @ViewDebug.ExportedProperty(deepExport=true)
  public abstract Resources.Theme getTheme();
  
  public int getThemeResId()
  {
    return 0;
  }
  
  public abstract int getUserId();
  
  @Deprecated
  public abstract Drawable getWallpaper();
  
  @Deprecated
  public abstract int getWallpaperDesiredMinimumHeight();
  
  @Deprecated
  public abstract int getWallpaperDesiredMinimumWidth();
  
  public abstract void grantUriPermission(String paramString, Uri paramUri, int paramInt);
  
  @Deprecated
  public boolean isCredentialEncryptedStorage()
  {
    return isCredentialProtectedStorage();
  }
  
  public abstract boolean isCredentialProtectedStorage();
  
  @Deprecated
  public boolean isDeviceEncryptedStorage()
  {
    return isDeviceProtectedStorage();
  }
  
  public abstract boolean isDeviceProtectedStorage();
  
  public boolean isRestricted()
  {
    return false;
  }
  
  @Deprecated
  public boolean migrateDatabaseFrom(Context paramContext, String paramString)
  {
    return moveDatabaseFrom(paramContext, paramString);
  }
  
  @Deprecated
  public boolean migrateSharedPreferencesFrom(Context paramContext, String paramString)
  {
    return moveSharedPreferencesFrom(paramContext, paramString);
  }
  
  public abstract boolean moveDatabaseFrom(Context paramContext, String paramString);
  
  public abstract boolean moveSharedPreferencesFrom(Context paramContext, String paramString);
  
  public final TypedArray obtainStyledAttributes(int paramInt, int[] paramArrayOfInt)
    throws Resources.NotFoundException
  {
    return getTheme().obtainStyledAttributes(paramInt, paramArrayOfInt);
  }
  
  public final TypedArray obtainStyledAttributes(AttributeSet paramAttributeSet, int[] paramArrayOfInt)
  {
    return getTheme().obtainStyledAttributes(paramAttributeSet, paramArrayOfInt, 0, 0);
  }
  
  public final TypedArray obtainStyledAttributes(AttributeSet paramAttributeSet, int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    return getTheme().obtainStyledAttributes(paramAttributeSet, paramArrayOfInt, paramInt1, paramInt2);
  }
  
  public final TypedArray obtainStyledAttributes(int[] paramArrayOfInt)
  {
    return getTheme().obtainStyledAttributes(paramArrayOfInt);
  }
  
  public abstract FileInputStream openFileInput(String paramString)
    throws FileNotFoundException;
  
  public abstract FileOutputStream openFileOutput(String paramString, int paramInt)
    throws FileNotFoundException;
  
  public abstract SQLiteDatabase openOrCreateDatabase(String paramString, int paramInt, SQLiteDatabase.CursorFactory paramCursorFactory);
  
  public abstract SQLiteDatabase openOrCreateDatabase(String paramString, int paramInt, SQLiteDatabase.CursorFactory paramCursorFactory, DatabaseErrorHandler paramDatabaseErrorHandler);
  
  @Deprecated
  public abstract Drawable peekWallpaper();
  
  public void registerComponentCallbacks(ComponentCallbacks paramComponentCallbacks)
  {
    getApplicationContext().registerComponentCallbacks(paramComponentCallbacks);
  }
  
  public abstract Intent registerReceiver(BroadcastReceiver paramBroadcastReceiver, IntentFilter paramIntentFilter);
  
  public abstract Intent registerReceiver(BroadcastReceiver paramBroadcastReceiver, IntentFilter paramIntentFilter, String paramString, Handler paramHandler);
  
  public abstract Intent registerReceiverAsUser(BroadcastReceiver paramBroadcastReceiver, UserHandle paramUserHandle, IntentFilter paramIntentFilter, String paramString, Handler paramHandler);
  
  @Deprecated
  public abstract void removeStickyBroadcast(Intent paramIntent);
  
  @Deprecated
  public abstract void removeStickyBroadcastAsUser(Intent paramIntent, UserHandle paramUserHandle);
  
  public abstract void revokeUriPermission(Uri paramUri, int paramInt);
  
  public abstract void sendBroadcast(Intent paramIntent);
  
  public abstract void sendBroadcast(Intent paramIntent, String paramString);
  
  public abstract void sendBroadcast(Intent paramIntent, String paramString, int paramInt);
  
  public abstract void sendBroadcast(Intent paramIntent, String paramString, Bundle paramBundle);
  
  public abstract void sendBroadcastAsUser(Intent paramIntent, UserHandle paramUserHandle);
  
  public abstract void sendBroadcastAsUser(Intent paramIntent, UserHandle paramUserHandle, String paramString);
  
  public abstract void sendBroadcastAsUser(Intent paramIntent, UserHandle paramUserHandle, String paramString, int paramInt);
  
  public abstract void sendBroadcastMultiplePermissions(Intent paramIntent, String[] paramArrayOfString);
  
  public abstract void sendOrderedBroadcast(Intent paramIntent, String paramString);
  
  public abstract void sendOrderedBroadcast(Intent paramIntent, String paramString1, int paramInt1, BroadcastReceiver paramBroadcastReceiver, Handler paramHandler, int paramInt2, String paramString2, Bundle paramBundle);
  
  public abstract void sendOrderedBroadcast(Intent paramIntent, String paramString1, BroadcastReceiver paramBroadcastReceiver, Handler paramHandler, int paramInt, String paramString2, Bundle paramBundle);
  
  public abstract void sendOrderedBroadcast(Intent paramIntent, String paramString1, Bundle paramBundle1, BroadcastReceiver paramBroadcastReceiver, Handler paramHandler, int paramInt, String paramString2, Bundle paramBundle2);
  
  public abstract void sendOrderedBroadcastAsUser(Intent paramIntent, UserHandle paramUserHandle, String paramString1, int paramInt1, BroadcastReceiver paramBroadcastReceiver, Handler paramHandler, int paramInt2, String paramString2, Bundle paramBundle);
  
  public abstract void sendOrderedBroadcastAsUser(Intent paramIntent, UserHandle paramUserHandle, String paramString1, int paramInt1, Bundle paramBundle1, BroadcastReceiver paramBroadcastReceiver, Handler paramHandler, int paramInt2, String paramString2, Bundle paramBundle2);
  
  public abstract void sendOrderedBroadcastAsUser(Intent paramIntent, UserHandle paramUserHandle, String paramString1, BroadcastReceiver paramBroadcastReceiver, Handler paramHandler, int paramInt, String paramString2, Bundle paramBundle);
  
  @Deprecated
  public abstract void sendStickyBroadcast(Intent paramIntent);
  
  @Deprecated
  public abstract void sendStickyBroadcastAsUser(Intent paramIntent, UserHandle paramUserHandle);
  
  @Deprecated
  public abstract void sendStickyBroadcastAsUser(Intent paramIntent, UserHandle paramUserHandle, Bundle paramBundle);
  
  @Deprecated
  public abstract void sendStickyOrderedBroadcast(Intent paramIntent, BroadcastReceiver paramBroadcastReceiver, Handler paramHandler, int paramInt, String paramString, Bundle paramBundle);
  
  @Deprecated
  public abstract void sendStickyOrderedBroadcastAsUser(Intent paramIntent, UserHandle paramUserHandle, BroadcastReceiver paramBroadcastReceiver, Handler paramHandler, int paramInt, String paramString, Bundle paramBundle);
  
  public abstract void setTheme(int paramInt);
  
  @Deprecated
  public abstract void setWallpaper(Bitmap paramBitmap)
    throws IOException;
  
  @Deprecated
  public abstract void setWallpaper(InputStream paramInputStream)
    throws IOException;
  
  public abstract void startActivities(Intent[] paramArrayOfIntent);
  
  public abstract void startActivities(Intent[] paramArrayOfIntent, Bundle paramBundle);
  
  public void startActivitiesAsUser(Intent[] paramArrayOfIntent, Bundle paramBundle, UserHandle paramUserHandle)
  {
    throw new RuntimeException("Not implemented. Must override in a subclass.");
  }
  
  public abstract void startActivity(Intent paramIntent);
  
  public abstract void startActivity(Intent paramIntent, Bundle paramBundle);
  
  public void startActivityAsUser(Intent paramIntent, Bundle paramBundle, UserHandle paramUserHandle)
  {
    throw new RuntimeException("Not implemented. Must override in a subclass.");
  }
  
  public void startActivityAsUser(Intent paramIntent, UserHandle paramUserHandle)
  {
    throw new RuntimeException("Not implemented. Must override in a subclass.");
  }
  
  public void startActivityForResult(String paramString, Intent paramIntent, int paramInt, Bundle paramBundle)
  {
    throw new RuntimeException("This method is only implemented for Activity-based Contexts. Check canStartActivityForResult() before calling.");
  }
  
  public abstract boolean startInstrumentation(ComponentName paramComponentName, String paramString, Bundle paramBundle);
  
  public abstract void startIntentSender(IntentSender paramIntentSender, Intent paramIntent, int paramInt1, int paramInt2, int paramInt3)
    throws IntentSender.SendIntentException;
  
  public abstract void startIntentSender(IntentSender paramIntentSender, Intent paramIntent, int paramInt1, int paramInt2, int paramInt3, Bundle paramBundle)
    throws IntentSender.SendIntentException;
  
  public abstract ComponentName startService(Intent paramIntent);
  
  public abstract ComponentName startServiceAsUser(Intent paramIntent, UserHandle paramUserHandle);
  
  public abstract boolean stopService(Intent paramIntent);
  
  public abstract boolean stopServiceAsUser(Intent paramIntent, UserHandle paramUserHandle);
  
  public abstract void unbindService(ServiceConnection paramServiceConnection);
  
  public void unregisterComponentCallbacks(ComponentCallbacks paramComponentCallbacks)
  {
    getApplicationContext().unregisterComponentCallbacks(paramComponentCallbacks);
  }
  
  public abstract void unregisterReceiver(BroadcastReceiver paramBroadcastReceiver);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/Context.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */