package com.android.server.usb;

import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbPort;
import android.hardware.usb.UsbPortStatus;
import android.os.Bundle;
import android.os.FileUtils;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UEventObserver;
import android.os.UEventObserver.UEvent;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.provider.Settings.Global;
import android.util.Pair;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.os.SomeArgs;
import com.android.internal.util.IndentingPrintWriter;
import com.android.server.FgThread;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class UsbDeviceManager
{
  private static final int ACCESSORY_REQUEST_TIMEOUT = 10000;
  private static final String ACCESSORY_START_MATCH = "DEVPATH=/devices/virtual/misc/usb_accessory";
  private static final int AUDIO_MODE_SOURCE = 1;
  private static final String AUDIO_SOURCE_PCM_PATH = "/sys/class/android_usb/android0/f_audio_source/pcm";
  private static final String BOOT_MODE_PROPERTY = "ro.bootmode";
  private static final boolean DEBUG = true;
  private static final String FUNCTIONS_PATH = "/sys/class/android_usb/android0/functions";
  private static final String MIDI_ALSA_PATH = "/sys/class/android_usb/android0/f_midi/alsa";
  private static final int MSG_BOOT_COMPLETED = 4;
  private static final int MSG_ENABLE_ADB = 1;
  private static final int MSG_SET_CURRENT_FUNCTIONS = 2;
  private static final int MSG_SET_USB_DATA_UNLOCKED = 6;
  private static final int MSG_SYSTEM_READY = 3;
  private static final int MSG_UPDATE_HOST_STATE = 8;
  private static final int MSG_UPDATE_STATE = 0;
  private static final int MSG_UPDATE_USER_RESTRICTIONS = 7;
  private static final int MSG_USER_SWITCHED = 5;
  private static final String RNDIS_ETH_ADDR_PATH = "/sys/class/android_usb/android0/f_rndis/ethaddr";
  private static final String STATE_PATH = "/sys/class/android_usb/android0/state";
  private static final String TAG = "UsbDeviceManager";
  private static final int UPDATE_DELAY = 1000;
  private static final String USB_CONFIG_PROPERTY = "sys.usb.config";
  private static final String USB_PERSISTENT_CONFIG_PROPERTY = "persist.sys.usb.config";
  private static final String USB_PERSISTENT_ONEPLUS_PROPERTY = "persist.sys.usb.oneplusConfig";
  private static final String USB_STATE_MATCH = "DEVPATH=/devices/virtual/android_usb/android0";
  private static final String USB_STATE_PROPERTY = "sys.usb.state";
  private long mAccessoryModeRequestTime = 0L;
  private String[] mAccessoryStrings;
  private boolean mAdbEnabled;
  private boolean mAudioSourceEnabled;
  private boolean mBootCompleted;
  private Intent mBroadcastedIntent;
  private final ContentResolver mContentResolver;
  private final Context mContext;
  @GuardedBy("mLock")
  private UsbSettingsManager mCurrentSettings;
  private UsbDebuggingManager mDebuggingManager;
  private UsbHandler mHandler;
  private final boolean mHasUsbAccessory;
  private final BroadcastReceiver mHostReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      paramAnonymousContext = (UsbPort)paramAnonymousIntent.getParcelableExtra("port");
      paramAnonymousIntent = (UsbPortStatus)paramAnonymousIntent.getParcelableExtra("portStatus");
      UsbDeviceManager.-get9(UsbDeviceManager.this).updateHostState(paramAnonymousContext, paramAnonymousIntent);
    }
  };
  private final Object mLock = new Object();
  private int mMidiCard;
  private int mMidiDevice;
  private boolean mMidiEnabled;
  private NotificationManager mNotificationManager;
  private Map<String, List<Pair<String, String>>> mOemModeMap;
  private final UEventObserver mUEventObserver = new UEventObserver()
  {
    public void onUEvent(UEventObserver.UEvent paramAnonymousUEvent)
    {
      Slog.v("UsbDeviceManager", "USB UEVENT: " + paramAnonymousUEvent.toString());
      String str = paramAnonymousUEvent.get("USB_STATE");
      paramAnonymousUEvent = paramAnonymousUEvent.get("ACCESSORY");
      if (str != null) {
        UsbDeviceManager.-get9(UsbDeviceManager.this).updateState(str);
      }
      while (!"START".equals(paramAnonymousUEvent)) {
        return;
      }
      Slog.d("UsbDeviceManager", "got accessory start");
      UsbDeviceManager.-wrap2(UsbDeviceManager.this);
    }
  };
  private final UsbAlsaManager mUsbAlsaManager;
  private boolean mUseUsbNotification;
  
  public UsbDeviceManager(Context paramContext, UsbAlsaManager paramUsbAlsaManager)
  {
    this.mContext = paramContext;
    this.mUsbAlsaManager = paramUsbAlsaManager;
    this.mContentResolver = paramContext.getContentResolver();
    this.mHasUsbAccessory = this.mContext.getPackageManager().hasSystemFeature("android.hardware.usb.accessory");
    initRndisAddress();
    readOemUsbOverrideConfig();
    this.mHandler = new UsbHandler(FgThread.get().getLooper());
    if (nativeIsStartRequested())
    {
      Slog.d("UsbDeviceManager", "accessory attached at boot");
      startAccessoryMode();
    }
    boolean bool1 = SystemProperties.getBoolean("ro.adb.secure", false);
    boolean bool2 = "1".equals(SystemProperties.get("vold.decrypt"));
    if ((!bool1) || (bool2)) {}
    for (;;)
    {
      this.mContext.registerReceiver(this.mHostReceiver, new IntentFilter("android.hardware.usb.action.USB_PORT_CHANGED"));
      return;
      this.mDebuggingManager = new UsbDebuggingManager(paramContext);
    }
  }
  
  private String applyOemOverrideFunction(String paramString)
  {
    if ((paramString == null) || (this.mOemModeMap == null)) {
      return paramString;
    }
    Object localObject = SystemProperties.get("ro.bootmode", "unknown");
    localObject = (List)this.mOemModeMap.get(localObject);
    if (localObject != null)
    {
      localObject = ((Iterable)localObject).iterator();
      while (((Iterator)localObject).hasNext())
      {
        Pair localPair = (Pair)((Iterator)localObject).next();
        if (((String)localPair.first).equals(paramString))
        {
          Slog.d("UsbDeviceManager", "OEM USB override: " + (String)localPair.first + " ==> " + (String)localPair.second);
          return (String)localPair.second;
        }
      }
    }
    return paramString;
  }
  
  private UsbSettingsManager getCurrentSettings()
  {
    synchronized (this.mLock)
    {
      UsbSettingsManager localUsbSettingsManager = this.mCurrentSettings;
      return localUsbSettingsManager;
    }
  }
  
  private static void initRndisAddress()
  {
    Object localObject = new int[6];
    localObject[0] = 2;
    String str = SystemProperties.get("ro.serialno", "1234567890ABCDEF");
    int j = str.length();
    int i = 0;
    while (i < j)
    {
      int k = i % 5 + 1;
      localObject[k] ^= str.charAt(i);
      i += 1;
    }
    localObject = String.format(Locale.US, "%02X:%02X:%02X:%02X:%02X:%02X", new Object[] { Integer.valueOf(localObject[0]), Integer.valueOf(localObject[1]), Integer.valueOf(localObject[2]), Integer.valueOf(localObject[3]), Integer.valueOf(localObject[4]), Integer.valueOf(localObject[5]) });
    try
    {
      FileUtils.stringToFile("/sys/class/android_usb/android0/f_rndis/ethaddr", (String)localObject);
      return;
    }
    catch (IOException localIOException)
    {
      Slog.e("UsbDeviceManager", "failed to write to /sys/class/android_usb/android0/f_rndis/ethaddr");
    }
  }
  
  private native String[] nativeGetAccessoryStrings();
  
  private native int nativeGetAudioMode();
  
  private native boolean nativeIsStartRequested();
  
  private native ParcelFileDescriptor nativeOpenAccessory();
  
  private void readOemUsbOverrideConfig()
  {
    String[] arrayOfString1 = this.mContext.getResources().getStringArray(17236020);
    if (arrayOfString1 != null)
    {
      int j = arrayOfString1.length;
      int i = 0;
      while (i < j)
      {
        String[] arrayOfString2 = arrayOfString1[i].split(":");
        if (arrayOfString2.length == 3)
        {
          if (this.mOemModeMap == null) {
            this.mOemModeMap = new HashMap();
          }
          List localList = (List)this.mOemModeMap.get(arrayOfString2[0]);
          Object localObject = localList;
          if (localList == null)
          {
            localObject = new LinkedList();
            this.mOemModeMap.put(arrayOfString2[0], localObject);
          }
          ((List)localObject).add(new Pair(arrayOfString2[1], arrayOfString2[2]));
        }
        i += 1;
      }
    }
  }
  
  private void startAccessoryMode()
  {
    int i = 1;
    if (!this.mHasUsbAccessory) {
      return;
    }
    this.mAccessoryStrings = nativeGetAccessoryStrings();
    int j;
    label53:
    String str;
    if (nativeGetAudioMode() == 1)
    {
      j = 1;
      if ((this.mAccessoryStrings == null) || (this.mAccessoryStrings[0] == null)) {
        break label94;
      }
      if (this.mAccessoryStrings[1] == null) {
        break label89;
      }
      str = null;
      if ((i == 0) || (j == 0)) {
        break label99;
      }
      str = "accessory,audio_source";
    }
    for (;;)
    {
      if (str != null)
      {
        this.mAccessoryModeRequestTime = SystemClock.elapsedRealtime();
        setCurrentFunctions(str);
      }
      return;
      j = 0;
      break;
      label89:
      i = 0;
      break label53;
      label94:
      i = 0;
      break label53;
      label99:
      if (i != 0) {
        str = "accessory";
      } else if (j != 0) {
        str = "audio_source";
      }
    }
  }
  
  public void allowUsbDebugging(boolean paramBoolean, String paramString)
  {
    if (this.mDebuggingManager != null) {
      this.mDebuggingManager.allowUsbDebugging(paramBoolean, paramString);
    }
  }
  
  public void bootCompleted()
  {
    Slog.d("UsbDeviceManager", "boot completed");
    this.mHandler.sendEmptyMessage(4);
  }
  
  public void clearUsbDebuggingKeys()
  {
    if (this.mDebuggingManager != null)
    {
      this.mDebuggingManager.clearUsbDebuggingKeys();
      return;
    }
    throw new RuntimeException("Cannot clear Usb Debugging keys, UsbDebuggingManager not enabled");
  }
  
  public void denyUsbDebugging()
  {
    if (this.mDebuggingManager != null) {
      this.mDebuggingManager.denyUsbDebugging();
    }
  }
  
  public void dump(IndentingPrintWriter paramIndentingPrintWriter)
  {
    if (this.mHandler != null) {
      this.mHandler.dump(paramIndentingPrintWriter);
    }
    if (this.mDebuggingManager != null) {
      this.mDebuggingManager.dump(paramIndentingPrintWriter);
    }
  }
  
  public UsbAccessory getCurrentAccessory()
  {
    return this.mHandler.getCurrentAccessory();
  }
  
  public boolean isFunctionEnabled(String paramString)
  {
    return UsbManager.containsFunction(SystemProperties.get("sys.usb.config"), paramString);
  }
  
  public boolean isUsbDataUnlocked()
  {
    return this.mHandler.isUsbDataUnlocked();
  }
  
  public ParcelFileDescriptor openAccessory(UsbAccessory paramUsbAccessory)
  {
    UsbAccessory localUsbAccessory = this.mHandler.getCurrentAccessory();
    if (localUsbAccessory == null) {
      throw new IllegalArgumentException("no accessory attached");
    }
    if (!localUsbAccessory.equals(paramUsbAccessory)) {
      throw new IllegalArgumentException(paramUsbAccessory.toString() + " does not match current accessory " + localUsbAccessory);
    }
    getCurrentSettings().checkPermission(paramUsbAccessory);
    return nativeOpenAccessory();
  }
  
  public void setCurrentFunctions(String paramString)
  {
    Slog.d("UsbDeviceManager", "setCurrentFunctions(" + paramString + ")");
    this.mHandler.sendMessage(2, paramString);
  }
  
  public void setCurrentUser(int paramInt, UsbSettingsManager paramUsbSettingsManager)
  {
    synchronized (this.mLock)
    {
      this.mCurrentSettings = paramUsbSettingsManager;
      this.mHandler.obtainMessage(5, paramInt, 0).sendToTarget();
      return;
    }
  }
  
  public void setUsbDataUnlocked(boolean paramBoolean)
  {
    Slog.d("UsbDeviceManager", "setUsbDataUnlocked(" + paramBoolean + ")");
    this.mHandler.sendMessage(6, paramBoolean);
  }
  
  public void systemReady()
  {
    int i = 0;
    Slog.d("UsbDeviceManager", "systemReady");
    this.mNotificationManager = ((NotificationManager)this.mContext.getSystemService("notification"));
    Object localObject = StorageManager.from(this.mContext).getPrimaryVolume();
    if (localObject != null)
    {
      bool = ((StorageVolume)localObject).allowMassStorage();
      if (bool) {
        break label234;
      }
    }
    label234:
    for (boolean bool = this.mContext.getResources().getBoolean(17956903);; bool = false)
    {
      this.mUseUsbNotification = bool;
      try
      {
        localObject = this.mContentResolver;
        if (this.mAdbEnabled) {
          i = 1;
        }
        Settings.Global.putInt((ContentResolver)localObject, "adb_enabled", i);
      }
      catch (SecurityException localSecurityException)
      {
        for (;;)
        {
          String str2;
          String str1;
          Slog.d("UsbDeviceManager", "ADB_ENABLED is restricted.");
        }
      }
      this.mHandler.sendEmptyMessage(3);
      str2 = SystemProperties.get("persist.sys.usb.oneplusConfig", "true");
      str1 = "";
      localObject = str1;
      if (str2 != null)
      {
        localObject = str1;
        if (str2.equals("true"))
        {
          SystemProperties.set("persist.sys.usb.oneplusConfig", "false");
          str1 = SystemProperties.get("persist.sys.usb.config", "none");
          localObject = str1;
          if (str1 != null)
          {
            localObject = str1;
            if (str1.equals("diag"))
            {
              SystemProperties.set("persist.sys.usb.config", "none");
              localObject = str1;
            }
          }
        }
      }
      Slog.i("UsbDeviceManager", "systemReady func = " + (String)localObject + " onePlusFunc = " + str2);
      return;
      bool = false;
      break;
    }
  }
  
  public void updateUserRestrictions()
  {
    this.mHandler.sendEmptyMessage(7);
  }
  
  private class AdbSettingsObserver
    extends ContentObserver
  {
    public AdbSettingsObserver()
    {
      super();
    }
    
    public void onChange(boolean paramBoolean)
    {
      if (Settings.Global.getInt(UsbDeviceManager.-get6(UsbDeviceManager.this), "adb_enabled", 0) > 0) {}
      for (paramBoolean = true;; paramBoolean = false)
      {
        UsbDeviceManager.-get9(UsbDeviceManager.this).sendMessage(1, paramBoolean);
        return;
      }
    }
  }
  
  private final class UsbHandler
    extends Handler
  {
    private boolean mAdbNotificationShown;
    private boolean mConfigured;
    private boolean mConnected;
    private UsbAccessory mCurrentAccessory;
    private String mCurrentFunctions;
    private boolean mCurrentFunctionsApplied;
    private int mCurrentUser = 55536;
    private boolean mHostConnected;
    private boolean mSinkPower;
    private boolean mSourcePower;
    private boolean mUsbDataUnlocked;
    private int mUsbNotificationId;
    
    public UsbHandler(Looper paramLooper)
    {
      super();
      try
      {
        this.mCurrentFunctions = SystemProperties.get("sys.usb.config", "none");
        if ("none".equals(this.mCurrentFunctions)) {
          this.mCurrentFunctions = "mtp";
        }
        this.mCurrentFunctionsApplied = this.mCurrentFunctions.equals(SystemProperties.get("sys.usb.state"));
        UsbDeviceManager.-set1(UsbDeviceManager.this, UsbManager.containsFunction(getDefaultFunctions(), "adb"));
        setEnabledFunctions(null, false);
        if (UsbDeviceManager.-get7(UsbDeviceManager.this).getResources().getBoolean(17957085))
        {
          boolean bool1 = UsbManager.containsFunction(getDefaultFunctions(), "mtp");
          boolean bool2 = UsbManager.containsFunction(getDefaultFunctions(), "ptp");
          if ((bool1) || (bool2)) {
            this.mUsbDataUnlocked = true;
          }
        }
        updateState(FileUtils.readTextFile(new File("/sys/class/android_usb/android0/state"), 0, null).trim());
        UsbDeviceManager.-get6(UsbDeviceManager.this).registerContentObserver(Settings.Global.getUriFor("adb_enabled"), false, new UsbDeviceManager.AdbSettingsObserver(UsbDeviceManager.this));
        UsbDeviceManager.-get14(UsbDeviceManager.this).startObserving("DEVPATH=/devices/virtual/android_usb/android0");
        UsbDeviceManager.-get14(UsbDeviceManager.this).startObserving("DEVPATH=/devices/virtual/misc/usb_accessory");
        return;
      }
      catch (Exception this$1)
      {
        Slog.e("UsbDeviceManager", "Error initializing UsbHandler", UsbDeviceManager.this);
      }
    }
    
    private String applyAdbFunction(String paramString)
    {
      if ((!UsbDeviceManager.-get2(UsbDeviceManager.this)) || (UsbManager.containsFunction(paramString, "charging"))) {
        return UsbManager.removeFunction(paramString, "adb");
      }
      return UsbManager.addFunction(paramString, "adb");
    }
    
    private String getDefaultFunctions()
    {
      String str2 = SystemProperties.get("persist.sys.usb.config", "none");
      String str1 = str2;
      if ("none".equals(str2)) {
        str1 = "mtp";
      }
      return str1;
    }
    
    private boolean isUsbStateChanged(Intent paramIntent)
    {
      Object localObject = paramIntent.getExtras().keySet();
      String str;
      if (UsbDeviceManager.-get5(UsbDeviceManager.this) == null)
      {
        localObject = ((Iterable)localObject).iterator();
        do
        {
          if (!((Iterator)localObject).hasNext()) {
            break;
          }
          str = (String)((Iterator)localObject).next();
        } while ((!paramIntent.getBooleanExtra(str, false)) || ("mtp".equals(str)));
        return true;
      }
      if (!((Set)localObject).equals(UsbDeviceManager.-get5(UsbDeviceManager.this).getExtras().keySet())) {
        return true;
      }
      localObject = ((Iterable)localObject).iterator();
      while (((Iterator)localObject).hasNext())
      {
        str = (String)((Iterator)localObject).next();
        if (paramIntent.getBooleanExtra(str, false) != UsbDeviceManager.-get5(UsbDeviceManager.this).getBooleanExtra(str, false)) {
          return true;
        }
      }
      return false;
    }
    
    private boolean isUsbTransferAllowed()
    {
      return !((UserManager)UsbDeviceManager.-get7(UsbDeviceManager.this).getSystemService("user")).hasUserRestriction("no_usb_file_transfer");
    }
    
    private void setAdbEnabled(boolean paramBoolean)
    {
      Slog.d("UsbDeviceManager", "setAdbEnabled: " + paramBoolean);
      if (paramBoolean != UsbDeviceManager.-get2(UsbDeviceManager.this))
      {
        UsbDeviceManager.-set1(UsbDeviceManager.this, paramBoolean);
        String str1 = getDefaultFunctions();
        String str2 = applyAdbFunction(str1);
        if (!str1.equals(str2)) {
          SystemProperties.set("persist.sys.usb.config", str2);
        }
        setEnabledFunctions(this.mCurrentFunctions, false);
        updateAdbNotification();
      }
      if (UsbDeviceManager.-get8(UsbDeviceManager.this) != null) {
        UsbDeviceManager.-get8(UsbDeviceManager.this).setAdbEnabled(UsbDeviceManager.-get2(UsbDeviceManager.this));
      }
    }
    
    private void setEnabledFunctions(String paramString, boolean paramBoolean)
    {
      Slog.d("UsbDeviceManager", "setEnabledFunctions functions=" + paramString + ", " + "forceRestart=" + paramBoolean);
      String str = this.mCurrentFunctions;
      boolean bool = this.mCurrentFunctionsApplied;
      if (trySetEnabledFunctions(paramString, paramBoolean)) {
        return;
      }
      if ((!bool) || (str.equals(paramString))) {}
      do
      {
        Slog.e("UsbDeviceManager", "Failsafe 2: Restoring default USB functions.");
        if (!trySetEnabledFunctions(null, false)) {
          break;
        }
        return;
        Slog.e("UsbDeviceManager", "Failsafe 1: Restoring previous USB functions.");
      } while (!trySetEnabledFunctions(str, false));
      return;
      Slog.e("UsbDeviceManager", "Failsafe 3: Restoring empty function list (with ADB if enabled).");
      if (trySetEnabledFunctions("none", false)) {
        return;
      }
      Slog.e("UsbDeviceManager", "Unable to set any USB functions!");
    }
    
    private boolean setUsbConfig(String paramString)
    {
      Slog.d("UsbDeviceManager", "setUsbConfig(" + paramString + ")");
      SystemProperties.set("sys.usb.config", paramString);
      return waitForState(paramString);
    }
    
    private void setUsbDataUnlocked(boolean paramBoolean)
    {
      Slog.d("UsbDeviceManager", "setUsbDataUnlocked: " + paramBoolean);
      this.mUsbDataUnlocked = paramBoolean;
      updateUsbNotification();
      updateUsbStateBroadcastIfNeeded();
      setEnabledFunctions(this.mCurrentFunctions, true);
    }
    
    private boolean trySetEnabledFunctions(String paramString, boolean paramBoolean)
    {
      String str = paramString;
      if (paramString == null) {
        str = getDefaultFunctions();
      }
      paramString = applyAdbFunction(str);
      paramString = UsbDeviceManager.-wrap1(UsbDeviceManager.this, paramString);
      if ((!this.mCurrentFunctions.equals(paramString)) || (!this.mCurrentFunctionsApplied) || (paramBoolean))
      {
        Slog.i("UsbDeviceManager", "Setting USB config to " + paramString);
        this.mCurrentFunctions = paramString;
        this.mCurrentFunctionsApplied = false;
        setUsbConfig("none");
        if (!setUsbConfig(paramString))
        {
          Slog.e("UsbDeviceManager", "Failed to switch USB config to " + paramString);
          return false;
        }
        this.mCurrentFunctionsApplied = true;
      }
      return true;
    }
    
    private void updateAdbNotification()
    {
      if (UsbDeviceManager.-get13(UsbDeviceManager.this) == null) {
        return;
      }
      if ((UsbDeviceManager.-get2(UsbDeviceManager.this)) && (this.mConnected))
      {
        if (("0".equals(SystemProperties.get("persist.adb.notify"))) || (SystemProperties.getBoolean("sys.debug.watchdog", false))) {
          return;
        }
        if (!this.mAdbNotificationShown)
        {
          localObject2 = UsbDeviceManager.-get7(UsbDeviceManager.this).getResources();
          localObject1 = ((Resources)localObject2).getText(17040433);
          localObject2 = ((Resources)localObject2).getText(17040434);
          localObject3 = Intent.makeRestartActivityTask(new ComponentName("com.android.settings", "com.android.settings.DevelopmentSettings"));
          localObject3 = PendingIntent.getActivityAsUser(UsbDeviceManager.-get7(UsbDeviceManager.this), 0, (Intent)localObject3, 0, null, UserHandle.CURRENT);
          localObject1 = new Notification.Builder(UsbDeviceManager.-get7(UsbDeviceManager.this)).setSmallIcon(17303263).setWhen(0L).setOngoing(true).setTicker((CharSequence)localObject1).setDefaults(0).setPriority(0).setColor(UsbDeviceManager.-get7(UsbDeviceManager.this).getColor(17170523)).setContentTitle((CharSequence)localObject1).setContentText((CharSequence)localObject2).setContentIntent((PendingIntent)localObject3).setVisibility(1).build();
          this.mAdbNotificationShown = true;
          UsbDeviceManager.-get13(UsbDeviceManager.this).notifyAsUser(null, 17040433, (Notification)localObject1, UserHandle.ALL);
        }
      }
      while (!this.mAdbNotificationShown)
      {
        Object localObject2;
        Object localObject1;
        Object localObject3;
        return;
      }
      this.mAdbNotificationShown = false;
      UsbDeviceManager.-get13(UsbDeviceManager.this).cancelAsUser(null, 17040433, UserHandle.ALL);
    }
    
    /* Error */
    private void updateAudioSourceFunction()
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 48	com/android/server/usb/UsbDeviceManager$UsbHandler:mCurrentFunctions	Ljava/lang/String;
      //   4: ldc_w 447
      //   7: invokestatic 75	android/hardware/usb/UsbManager:containsFunction	(Ljava/lang/String;Ljava/lang/String;)Z
      //   10: istore 6
      //   12: iload 6
      //   14: aload_0
      //   15: getfield 31	com/android/server/usb/UsbDeviceManager$UsbHandler:this$0	Lcom/android/server/usb/UsbDeviceManager;
      //   18: invokestatic 450	com/android/server/usb/UsbDeviceManager:-get3	(Lcom/android/server/usb/UsbDeviceManager;)Z
      //   21: if_icmpeq +105 -> 126
      //   24: iconst_m1
      //   25: istore_2
      //   26: iconst_m1
      //   27: istore 5
      //   29: iload_2
      //   30: istore_1
      //   31: iload 5
      //   33: istore 4
      //   35: iload 6
      //   37: ifeq +64 -> 101
      //   40: aconst_null
      //   41: astore 7
      //   43: aconst_null
      //   44: astore 9
      //   46: new 452	java/util/Scanner
      //   49: dup
      //   50: new 106	java/io/File
      //   53: dup
      //   54: ldc_w 454
      //   57: invokespecial 111	java/io/File:<init>	(Ljava/lang/String;)V
      //   60: invokespecial 457	java/util/Scanner:<init>	(Ljava/io/File;)V
      //   63: astore 8
      //   65: aload 8
      //   67: invokevirtual 461	java/util/Scanner:nextInt	()I
      //   70: istore_3
      //   71: iload_3
      //   72: istore_2
      //   73: aload 8
      //   75: invokevirtual 461	java/util/Scanner:nextInt	()I
      //   78: istore_1
      //   79: iload_1
      //   80: istore_2
      //   81: iload_3
      //   82: istore_1
      //   83: iload_2
      //   84: istore 4
      //   86: aload 8
      //   88: ifnull +13 -> 101
      //   91: aload 8
      //   93: invokevirtual 464	java/util/Scanner:close	()V
      //   96: iload_2
      //   97: istore 4
      //   99: iload_3
      //   100: istore_1
      //   101: aload_0
      //   102: getfield 31	com/android/server/usb/UsbDeviceManager$UsbHandler:this$0	Lcom/android/server/usb/UsbDeviceManager;
      //   105: invokestatic 468	com/android/server/usb/UsbDeviceManager:-get15	(Lcom/android/server/usb/UsbDeviceManager;)Lcom/android/server/usb/UsbAlsaManager;
      //   108: iload 6
      //   110: iload_1
      //   111: iload 4
      //   113: invokevirtual 474	com/android/server/usb/UsbAlsaManager:setAccessoryAudioState	(ZII)V
      //   116: aload_0
      //   117: getfield 31	com/android/server/usb/UsbDeviceManager$UsbHandler:this$0	Lcom/android/server/usb/UsbDeviceManager;
      //   120: iload 6
      //   122: invokestatic 477	com/android/server/usb/UsbDeviceManager:-set2	(Lcom/android/server/usb/UsbDeviceManager;Z)Z
      //   125: pop
      //   126: return
      //   127: astore 7
      //   129: aload 9
      //   131: astore 8
      //   133: aload 7
      //   135: astore 9
      //   137: aload 8
      //   139: astore 7
      //   141: ldc -95
      //   143: ldc_w 479
      //   146: aload 9
      //   148: invokestatic 169	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   151: pop
      //   152: iload_2
      //   153: istore_1
      //   154: iload 5
      //   156: istore 4
      //   158: aload 8
      //   160: ifnull -59 -> 101
      //   163: aload 8
      //   165: invokevirtual 464	java/util/Scanner:close	()V
      //   168: iload_2
      //   169: istore_1
      //   170: iload 5
      //   172: istore 4
      //   174: goto -73 -> 101
      //   177: astore 8
      //   179: aload 7
      //   181: ifnull +8 -> 189
      //   184: aload 7
      //   186: invokevirtual 464	java/util/Scanner:close	()V
      //   189: aload 8
      //   191: athrow
      //   192: astore 9
      //   194: aload 8
      //   196: astore 7
      //   198: aload 9
      //   200: astore 8
      //   202: goto -23 -> 179
      //   205: astore 9
      //   207: goto -70 -> 137
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	210	0	this	UsbHandler
      //   30	140	1	i	int
      //   25	144	2	j	int
      //   70	30	3	k	int
      //   33	140	4	m	int
      //   27	144	5	n	int
      //   10	111	6	bool	boolean
      //   41	1	7	localObject1	Object
      //   127	7	7	localFileNotFoundException1	java.io.FileNotFoundException
      //   139	58	7	localObject2	Object
      //   63	101	8	localObject3	Object
      //   177	18	8	localObject4	Object
      //   200	1	8	localObject5	Object
      //   44	103	9	localFileNotFoundException2	java.io.FileNotFoundException
      //   192	7	9	localObject6	Object
      //   205	1	9	localFileNotFoundException3	java.io.FileNotFoundException
      // Exception table:
      //   from	to	target	type
      //   46	65	127	java/io/FileNotFoundException
      //   46	65	177	finally
      //   141	152	177	finally
      //   65	71	192	finally
      //   73	79	192	finally
      //   65	71	205	java/io/FileNotFoundException
      //   73	79	205	java/io/FileNotFoundException
    }
    
    private void updateCurrentAccessory()
    {
      int i;
      if (UsbDeviceManager.-get0(UsbDeviceManager.this) > 0L) {
        if (SystemClock.elapsedRealtime() < UsbDeviceManager.-get0(UsbDeviceManager.this) + 10000L)
        {
          i = 1;
          if ((!this.mConfigured) || (i == 0)) {
            break label145;
          }
          if (UsbDeviceManager.-get1(UsbDeviceManager.this) == null) {
            break label135;
          }
          this.mCurrentAccessory = new UsbAccessory(UsbDeviceManager.-get1(UsbDeviceManager.this));
          Slog.d("UsbDeviceManager", "entering USB accessory mode: " + this.mCurrentAccessory);
          if (UsbDeviceManager.-get4(UsbDeviceManager.this)) {
            UsbDeviceManager.-wrap0(UsbDeviceManager.this).accessoryAttached(this.mCurrentAccessory);
          }
        }
      }
      label135:
      label145:
      do
      {
        do
        {
          return;
          i = 0;
          break;
          i = 0;
          break;
          Slog.e("UsbDeviceManager", "nativeGetAccessoryStrings failed");
          return;
        } while (i != 0);
        Slog.d("UsbDeviceManager", "exited USB accessory mode");
        setEnabledFunctions(null, false);
      } while (this.mCurrentAccessory == null);
      if (UsbDeviceManager.-get4(UsbDeviceManager.this)) {
        UsbDeviceManager.-wrap0(UsbDeviceManager.this).accessoryDetached(this.mCurrentAccessory);
      }
      this.mCurrentAccessory = null;
      UsbDeviceManager.-set0(UsbDeviceManager.this, null);
    }
    
    /* Error */
    private void updateMidiFunction()
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 48	com/android/server/usb/UsbDeviceManager$UsbHandler:mCurrentFunctions	Ljava/lang/String;
      //   4: ldc_w 537
      //   7: invokestatic 75	android/hardware/usb/UsbManager:containsFunction	(Ljava/lang/String;Ljava/lang/String;)Z
      //   10: istore_2
      //   11: iload_2
      //   12: aload_0
      //   13: getfield 31	com/android/server/usb/UsbDeviceManager$UsbHandler:this$0	Lcom/android/server/usb/UsbDeviceManager;
      //   16: invokestatic 540	com/android/server/usb/UsbDeviceManager:-get12	(Lcom/android/server/usb/UsbDeviceManager;)Z
      //   19: if_icmpeq +82 -> 101
      //   22: iload_2
      //   23: istore_1
      //   24: iload_2
      //   25: ifeq +67 -> 92
      //   28: aconst_null
      //   29: astore_3
      //   30: aconst_null
      //   31: astore 5
      //   33: new 452	java/util/Scanner
      //   36: dup
      //   37: new 106	java/io/File
      //   40: dup
      //   41: ldc_w 542
      //   44: invokespecial 111	java/io/File:<init>	(Ljava/lang/String;)V
      //   47: invokespecial 457	java/util/Scanner:<init>	(Ljava/io/File;)V
      //   50: astore 4
      //   52: aload_0
      //   53: getfield 31	com/android/server/usb/UsbDeviceManager$UsbHandler:this$0	Lcom/android/server/usb/UsbDeviceManager;
      //   56: aload 4
      //   58: invokevirtual 461	java/util/Scanner:nextInt	()I
      //   61: invokestatic 546	com/android/server/usb/UsbDeviceManager:-set5	(Lcom/android/server/usb/UsbDeviceManager;I)I
      //   64: pop
      //   65: aload_0
      //   66: getfield 31	com/android/server/usb/UsbDeviceManager$UsbHandler:this$0	Lcom/android/server/usb/UsbDeviceManager;
      //   69: aload 4
      //   71: invokevirtual 461	java/util/Scanner:nextInt	()I
      //   74: invokestatic 549	com/android/server/usb/UsbDeviceManager:-set6	(Lcom/android/server/usb/UsbDeviceManager;I)I
      //   77: pop
      //   78: iload_2
      //   79: istore_1
      //   80: aload 4
      //   82: ifnull +10 -> 92
      //   85: aload 4
      //   87: invokevirtual 464	java/util/Scanner:close	()V
      //   90: iload_2
      //   91: istore_1
      //   92: aload_0
      //   93: getfield 31	com/android/server/usb/UsbDeviceManager$UsbHandler:this$0	Lcom/android/server/usb/UsbDeviceManager;
      //   96: iload_1
      //   97: invokestatic 552	com/android/server/usb/UsbDeviceManager:-set7	(Lcom/android/server/usb/UsbDeviceManager;Z)Z
      //   100: pop
      //   101: aload_0
      //   102: getfield 31	com/android/server/usb/UsbDeviceManager$UsbHandler:this$0	Lcom/android/server/usb/UsbDeviceManager;
      //   105: invokestatic 468	com/android/server/usb/UsbDeviceManager:-get15	(Lcom/android/server/usb/UsbDeviceManager;)Lcom/android/server/usb/UsbAlsaManager;
      //   108: astore_3
      //   109: aload_0
      //   110: getfield 31	com/android/server/usb/UsbDeviceManager$UsbHandler:this$0	Lcom/android/server/usb/UsbDeviceManager;
      //   113: invokestatic 540	com/android/server/usb/UsbDeviceManager:-get12	(Lcom/android/server/usb/UsbDeviceManager;)Z
      //   116: ifeq +82 -> 198
      //   119: aload_0
      //   120: getfield 494	com/android/server/usb/UsbDeviceManager$UsbHandler:mConfigured	Z
      //   123: istore_1
      //   124: aload_3
      //   125: iload_1
      //   126: aload_0
      //   127: getfield 31	com/android/server/usb/UsbDeviceManager$UsbHandler:this$0	Lcom/android/server/usb/UsbDeviceManager;
      //   130: invokestatic 556	com/android/server/usb/UsbDeviceManager:-get10	(Lcom/android/server/usb/UsbDeviceManager;)I
      //   133: aload_0
      //   134: getfield 31	com/android/server/usb/UsbDeviceManager$UsbHandler:this$0	Lcom/android/server/usb/UsbDeviceManager;
      //   137: invokestatic 559	com/android/server/usb/UsbDeviceManager:-get11	(Lcom/android/server/usb/UsbDeviceManager;)I
      //   140: invokevirtual 562	com/android/server/usb/UsbAlsaManager:setPeripheralMidiState	(ZII)V
      //   143: return
      //   144: astore_3
      //   145: aload 5
      //   147: astore 4
      //   149: aload_3
      //   150: astore 5
      //   152: aload 4
      //   154: astore_3
      //   155: ldc -95
      //   157: ldc_w 564
      //   160: aload 5
      //   162: invokestatic 169	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   165: pop
      //   166: iconst_0
      //   167: istore_2
      //   168: iload_2
      //   169: istore_1
      //   170: aload 4
      //   172: ifnull -80 -> 92
      //   175: aload 4
      //   177: invokevirtual 464	java/util/Scanner:close	()V
      //   180: iload_2
      //   181: istore_1
      //   182: goto -90 -> 92
      //   185: astore 4
      //   187: aload_3
      //   188: ifnull +7 -> 195
      //   191: aload_3
      //   192: invokevirtual 464	java/util/Scanner:close	()V
      //   195: aload 4
      //   197: athrow
      //   198: iconst_0
      //   199: istore_1
      //   200: goto -76 -> 124
      //   203: astore 5
      //   205: aload 4
      //   207: astore_3
      //   208: aload 5
      //   210: astore 4
      //   212: goto -25 -> 187
      //   215: astore 5
      //   217: goto -65 -> 152
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	220	0	this	UsbHandler
      //   23	177	1	bool1	boolean
      //   10	171	2	bool2	boolean
      //   29	96	3	localUsbAlsaManager	UsbAlsaManager
      //   144	6	3	localFileNotFoundException1	java.io.FileNotFoundException
      //   154	54	3	localObject1	Object
      //   50	126	4	localObject2	Object
      //   185	21	4	localObject3	Object
      //   210	1	4	localObject4	Object
      //   31	130	5	localFileNotFoundException2	java.io.FileNotFoundException
      //   203	6	5	localObject5	Object
      //   215	1	5	localFileNotFoundException3	java.io.FileNotFoundException
      // Exception table:
      //   from	to	target	type
      //   33	52	144	java/io/FileNotFoundException
      //   33	52	185	finally
      //   155	166	185	finally
      //   52	78	203	finally
      //   52	78	215	java/io/FileNotFoundException
    }
    
    private void updateUsbFunctions()
    {
      updateAudioSourceFunction();
      updateMidiFunction();
    }
    
    private void updateUsbNotification()
    {
      if ((UsbDeviceManager.-get13(UsbDeviceManager.this) == null) || (!UsbDeviceManager.-get16(UsbDeviceManager.this)) || ("0".equals(SystemProperties.get("persist.charging.notify")))) {
        return;
      }
      int j = 0;
      Object localObject2 = UsbDeviceManager.-get7(UsbDeviceManager.this).getResources();
      int i;
      if (this.mConnected) {
        if (!this.mUsbDataUnlocked) {
          if (this.mSourcePower) {
            i = 0;
          }
        }
      }
      for (;;)
      {
        if (i != this.mUsbNotificationId)
        {
          if (this.mUsbNotificationId != 0)
          {
            UsbDeviceManager.-get13(UsbDeviceManager.this).cancelAsUser(null, this.mUsbNotificationId, UserHandle.ALL);
            this.mUsbNotificationId = 0;
          }
          if (i != 0)
          {
            Object localObject1 = ((Resources)localObject2).getText(17040432);
            localObject2 = ((Resources)localObject2).getText(i);
            Object localObject3 = Intent.makeRestartActivityTask(new ComponentName("com.android.settings", "com.android.settings.deviceinfo.UsbModeChooserActivity"));
            localObject3 = PendingIntent.getActivityAsUser(UsbDeviceManager.-get7(UsbDeviceManager.this), 0, (Intent)localObject3, 0, null, UserHandle.CURRENT);
            Slog.d("UsbDeviceManager", "updateUsbNotification: mConnected=" + this.mConnected + " mUsbDataUnlocked=" + this.mUsbDataUnlocked + " mSourcePower=" + this.mSourcePower + " mHostConnected=" + this.mHostConnected + " mSinkPower=" + this.mSinkPower + " mCurrentFunctions=" + this.mCurrentFunctions);
            localObject1 = new Notification.Builder(UsbDeviceManager.-get7(UsbDeviceManager.this)).setSmallIcon(17303263).setWhen(0L).setOngoing(true).setTicker((CharSequence)localObject2).setDefaults(0).setPriority(-2).setColor(UsbDeviceManager.-get7(UsbDeviceManager.this).getColor(17170523)).setContentTitle((CharSequence)localObject2).setContentText((CharSequence)localObject1).setContentIntent((PendingIntent)localObject3).setVisibility(1).build();
            UsbDeviceManager.-get13(UsbDeviceManager.this).notifyAsUser(null, i, (Notification)localObject1, UserHandle.ALL);
            this.mUsbNotificationId = i;
          }
        }
        return;
        i = 17040426;
        continue;
        if (UsbManager.containsFunction(this.mCurrentFunctions, "mtp"))
        {
          i = 17040428;
        }
        else if (UsbManager.containsFunction(this.mCurrentFunctions, "ptp"))
        {
          i = 17040429;
        }
        else if (UsbManager.containsFunction(this.mCurrentFunctions, "midi"))
        {
          i = 17040430;
        }
        else if (UsbManager.containsFunction(this.mCurrentFunctions, "accessory"))
        {
          i = 17040431;
        }
        else if (this.mSourcePower)
        {
          i = 0;
        }
        else
        {
          i = 17040426;
          continue;
          if (this.mSourcePower)
          {
            i = 0;
          }
          else
          {
            i = j;
            if (this.mHostConnected)
            {
              i = j;
              if (this.mSinkPower) {
                i = 17040426;
              }
            }
          }
        }
      }
    }
    
    private void updateUsbStateBroadcastIfNeeded()
    {
      Intent localIntent = new Intent("android.hardware.usb.action.USB_STATE");
      localIntent.addFlags(805306368);
      localIntent.putExtra("connected", this.mConnected);
      localIntent.putExtra("host_connected", this.mHostConnected);
      localIntent.putExtra("configured", this.mConfigured);
      boolean bool;
      int i;
      label97:
      String str;
      if (isUsbTransferAllowed())
      {
        bool = this.mUsbDataUnlocked;
        localIntent.putExtra("unlocked", bool);
        if (this.mCurrentFunctions == null) {
          break label143;
        }
        String[] arrayOfString = this.mCurrentFunctions.split(",");
        i = 0;
        if (i >= arrayOfString.length) {
          break label143;
        }
        str = arrayOfString[i];
        if (!"none".equals(str)) {
          break label132;
        }
      }
      for (;;)
      {
        i += 1;
        break label97;
        bool = false;
        break;
        label132:
        localIntent.putExtra(str, true);
      }
      label143:
      if (!isUsbStateChanged(localIntent))
      {
        Slog.d("UsbDeviceManager", "skip broadcasting " + localIntent + " extras: " + localIntent.getExtras());
        return;
      }
      Slog.d("UsbDeviceManager", "broadcasting " + localIntent + " extras: " + localIntent.getExtras());
      UsbDeviceManager.-get7(UsbDeviceManager.this).sendStickyBroadcastAsUser(localIntent, UserHandle.ALL);
      UsbDeviceManager.-set4(UsbDeviceManager.this, localIntent);
    }
    
    private boolean waitForState(String paramString)
    {
      String str = null;
      int i = 0;
      while (i < 20)
      {
        str = SystemProperties.get("sys.usb.state");
        if (paramString.equals(str)) {
          return true;
        }
        SystemClock.sleep(50L);
        i += 1;
      }
      Slog.e("UsbDeviceManager", "waitForState(" + paramString + ") FAILED: got " + str);
      return false;
    }
    
    public void dump(IndentingPrintWriter paramIndentingPrintWriter)
    {
      paramIndentingPrintWriter.println("USB Device State:");
      paramIndentingPrintWriter.println("  mCurrentFunctions: " + this.mCurrentFunctions);
      paramIndentingPrintWriter.println("  mCurrentFunctionsApplied: " + this.mCurrentFunctionsApplied);
      paramIndentingPrintWriter.println("  mConnected: " + this.mConnected);
      paramIndentingPrintWriter.println("  mConfigured: " + this.mConfigured);
      paramIndentingPrintWriter.println("  mUsbDataUnlocked: " + this.mUsbDataUnlocked);
      paramIndentingPrintWriter.println("  mCurrentAccessory: " + this.mCurrentAccessory);
      paramIndentingPrintWriter.println("  mHostConnected: " + this.mHostConnected);
      paramIndentingPrintWriter.println("  mSourcePower: " + this.mSourcePower);
      paramIndentingPrintWriter.println("  mSinkPower: " + this.mSinkPower);
      try
      {
        paramIndentingPrintWriter.println("  Kernel state: " + FileUtils.readTextFile(new File("/sys/class/android_usb/android0/state"), 0, null).trim());
        paramIndentingPrintWriter.println("  Kernel function list: " + FileUtils.readTextFile(new File("/sys/class/android_usb/android0/functions"), 0, null).trim());
        return;
      }
      catch (IOException localIOException)
      {
        paramIndentingPrintWriter.println("IOException: " + localIOException);
      }
    }
    
    public UsbAccessory getCurrentAccessory()
    {
      return this.mCurrentAccessory;
    }
    
    public void handleMessage(Message paramMessage)
    {
      boolean bool2 = true;
      boolean bool1 = true;
      boolean bool4 = true;
      boolean bool3 = true;
      switch (paramMessage.what)
      {
      }
      label91:
      label157:
      label162:
      label176:
      label196:
      label211:
      label259:
      label264:
      do
      {
        do
        {
          for (;;)
          {
            return;
            if (paramMessage.arg1 == 1)
            {
              bool1 = true;
              this.mConnected = bool1;
              if (paramMessage.arg2 != 1) {
                break label157;
              }
              bool1 = bool3;
              this.mConfigured = bool1;
              if (!this.mConnected) {
                this.mUsbDataUnlocked = false;
              }
              updateUsbNotification();
              updateAdbNotification();
              if (!UsbManager.containsFunction(this.mCurrentFunctions, "accessory")) {
                break label162;
              }
              updateCurrentAccessory();
            }
            for (;;)
            {
              if (!UsbDeviceManager.-get4(UsbDeviceManager.this)) {
                break label176;
              }
              updateUsbStateBroadcastIfNeeded();
              updateUsbFunctions();
              return;
              bool1 = false;
              break;
              bool1 = false;
              break label91;
              if (!this.mConnected) {
                setEnabledFunctions(null, false);
              }
            }
          }
          paramMessage = (SomeArgs)paramMessage.obj;
          if (paramMessage.argi1 == 1)
          {
            bool1 = true;
            this.mHostConnected = bool1;
            if (paramMessage.argi2 != 1) {
              break label259;
            }
            bool1 = true;
            this.mSourcePower = bool1;
            if (paramMessage.argi3 != 1) {
              break label264;
            }
          }
          for (bool1 = bool2;; bool1 = false)
          {
            this.mSinkPower = bool1;
            paramMessage.recycle();
            updateUsbNotification();
            if (!UsbDeviceManager.-get4(UsbDeviceManager.this)) {
              break;
            }
            updateUsbStateBroadcastIfNeeded();
            return;
            bool1 = false;
            break label196;
            bool1 = false;
            break label211;
          }
          if (paramMessage.arg1 == 1) {}
          for (;;)
          {
            setAdbEnabled(bool1);
            return;
            bool1 = false;
          }
          setEnabledFunctions((String)paramMessage.obj, false);
          return;
          setEnabledFunctions(this.mCurrentFunctions, false);
          return;
          if (paramMessage.arg1 == 1) {}
          for (bool1 = bool4;; bool1 = false)
          {
            setUsbDataUnlocked(bool1);
            return;
          }
          updateUsbNotification();
          updateAdbNotification();
          updateUsbStateBroadcastIfNeeded();
          updateUsbFunctions();
          return;
          UsbDeviceManager.-set3(UsbDeviceManager.this, true);
          if (this.mCurrentAccessory != null) {
            UsbDeviceManager.-wrap0(UsbDeviceManager.this).accessoryAttached(this.mCurrentAccessory);
          }
        } while (UsbDeviceManager.-get8(UsbDeviceManager.this) == null);
        UsbDeviceManager.-get8(UsbDeviceManager.this).setAdbEnabled(UsbDeviceManager.-get2(UsbDeviceManager.this));
        return;
      } while (this.mCurrentUser == paramMessage.arg1);
      if (!UsbManager.containsFunction(this.mCurrentFunctions, "mtp")) {}
      for (bool1 = UsbManager.containsFunction(this.mCurrentFunctions, "ptp");; bool1 = true)
      {
        if ((this.mUsbDataUnlocked) && (bool1) && (this.mCurrentUser != 55536))
        {
          Slog.v("UsbDeviceManager", "Current user switched to " + this.mCurrentUser + "; resetting USB host stack for MTP or PTP");
          this.mUsbDataUnlocked = false;
          setEnabledFunctions(this.mCurrentFunctions, true);
        }
        this.mCurrentUser = paramMessage.arg1;
        return;
      }
    }
    
    public boolean isUsbDataUnlocked()
    {
      StringBuilder localStringBuilder = new StringBuilder().append("isUsbDataUnlocked: ");
      if (this.mUsbDataUnlocked) {}
      for (String str = "1";; str = "0")
      {
        Slog.i("UsbDeviceManager", str);
        return this.mUsbDataUnlocked;
      }
    }
    
    public void sendMessage(int paramInt, Object paramObject)
    {
      removeMessages(paramInt);
      Message localMessage = Message.obtain(this, paramInt);
      localMessage.obj = paramObject;
      sendMessage(localMessage);
    }
    
    public void sendMessage(int paramInt, boolean paramBoolean)
    {
      removeMessages(paramInt);
      Message localMessage = Message.obtain(this, paramInt);
      if (paramBoolean) {}
      for (paramInt = 1;; paramInt = 0)
      {
        localMessage.arg1 = paramInt;
        sendMessage(localMessage);
        return;
      }
    }
    
    public void updateHostState(UsbPort paramUsbPort, UsbPortStatus paramUsbPortStatus)
    {
      int m = 1;
      int k;
      int j;
      if (paramUsbPortStatus.getCurrentDataRole() == 1)
      {
        k = 1;
        if (paramUsbPortStatus.getCurrentPowerRole() != 1) {
          break label132;
        }
        j = 1;
        label25:
        if (paramUsbPortStatus.getCurrentPowerRole() != 2) {
          break label138;
        }
        i = 1;
        label35:
        Slog.i("UsbDeviceManager", "updateHostState " + paramUsbPort + " status=" + paramUsbPortStatus);
        paramUsbPort = SomeArgs.obtain();
        if (k == 0) {
          break label143;
        }
        k = 1;
        label83:
        paramUsbPort.argi1 = k;
        if (j == 0) {
          break label149;
        }
        j = 1;
        label97:
        paramUsbPort.argi2 = j;
        if (i == 0) {
          break label155;
        }
      }
      label132:
      label138:
      label143:
      label149:
      label155:
      for (int i = m;; i = 0)
      {
        paramUsbPort.argi3 = i;
        obtainMessage(8, paramUsbPort).sendToTarget();
        return;
        k = 0;
        break;
        j = 0;
        break label25;
        i = 0;
        break label35;
        k = 0;
        break label83;
        j = 0;
        break label97;
      }
    }
    
    public void updateState(String paramString)
    {
      int k = 0;
      int j;
      int i;
      if ("DISCONNECTED".equals(paramString))
      {
        j = 0;
        i = 0;
      }
      for (;;)
      {
        removeMessages(0);
        paramString = Message.obtain(this, 0);
        paramString.arg1 = j;
        paramString.arg2 = i;
        i = k;
        if (j == 0) {
          i = 1000;
        }
        sendMessageDelayed(paramString, i);
        return;
        if ("CONNECTED".equals(paramString))
        {
          j = 1;
          i = 0;
        }
        else
        {
          if (!"CONFIGURED".equals(paramString)) {
            break;
          }
          j = 1;
          i = 1;
        }
      }
      Slog.e("UsbDeviceManager", "unknown state " + paramString);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/usb/UsbDeviceManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */