package com.android.server;

import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.Settings.System;
import android.util.Log;
import android.view.WindowManager.LayoutParams;
import android.widget.RemoteViews;
import android.widget.Toast;

public class OemSceneModeController
{
  private static final String ACTION_DISABLE_GAME_MODE = "com.oem.intent.action.DISABLE_GAME_MODE";
  private static boolean DBG = Build.DEBUG_ONEPLUS;
  private static final String GAME_MODE_EVER_ENABLED_KEY = "persist.sys.oem.gamemode_dirty";
  public static final int MODE_GAMEING_NO_DISTURB = 1;
  public static final int MODE_READING = 0;
  private static final int MSG_GAME_AUTO_CHANGED = 3;
  private static final int MSG_GAME_MANUAL_CHANGED = 2;
  private static final int MSG_READ_AUTO_CHANGED = 1;
  private static final int MSG_READ_MANUAL_CHANGED = 0;
  private static final int MSG_START_MONITOR = 4;
  private static final int MSG_START_MONITOR_PASSIVE = 7;
  private static final int MSG_STOP_MONITOR = 5;
  private static final int MSG_STOP_MONITOR_PASSIVE = 6;
  private static final int NOTIFICATION_GAME_MODE_ENABLED = 5566;
  private static final String NOTIFY_TAG = "SceneModeController";
  public static final int SWITCHER_PASSIVE = 1;
  public static final int SWITCHER_PROACTIVE = 0;
  private static final String TAG = "OemSceneModeController";
  private static final Uri URI_GAME_AUTO = Settings.System.getUriFor("game_mode_status_auto");
  private static final Uri URI_GAME_MANUAL;
  private static final Uri URI_READ_AUTO;
  private static final Uri URI_READ_MANUAL = Settings.System.getUriFor("reading_mode_status_manual");
  private static final String VALUE_FORCE_OFF = "force-off";
  private static final String VALUE_FORCE_ON = "force-on";
  private static final String VALUE_OFF = "0";
  private static final String VALUE_ON = "1";
  final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if (OemSceneModeController.-get0()) {
        Log.d("OemSceneModeController", "[scene] onReceive: " + paramAnonymousIntent.getAction());
      }
      if (paramAnonymousIntent.getAction().equals("com.oem.intent.action.DISABLE_GAME_MODE")) {
        Settings.System.putStringForUser(OemSceneModeController.-get4(OemSceneModeController.this), "game_mode_status_manual", "force-off", -2);
      }
    }
  };
  private Context mContext;
  private boolean mGameModeAuto = false;
  private GameModeAutoContentObserver mGameModeAutoObserver;
  private boolean mGameModeManual = false;
  private GameModeManualContentObserver mGameModeManualObserver;
  private boolean mGameModeStatus = false;
  private Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      switch (paramAnonymousMessage.what)
      {
      }
      for (;;)
      {
        OemSceneModeController.-wrap5(OemSceneModeController.this);
        return;
        OemSceneModeController.-wrap4(OemSceneModeController.this);
        continue;
        OemSceneModeController.-wrap3(OemSceneModeController.this);
        continue;
        OemSceneModeController.-wrap2(OemSceneModeController.this);
        continue;
        OemSceneModeController.-wrap1(OemSceneModeController.this);
        continue;
        OemSceneModeController.this.handleStartMonitor();
        continue;
        OemSceneModeController.this.handleStopMonitor();
        continue;
        OemSceneModeController.this.handleStopMonitorPassive();
        continue;
        OemSceneModeController.this.handleStartMonitorPassive();
      }
    }
  };
  private boolean mIsMonitoringPassiveProvider = false;
  private boolean mIsMonitoringProactiveProvider = false;
  private OemSceneButtonController mOemSceneButtonController;
  private boolean mReadModeAuto = false;
  private ReadModeAutoContentObserver mReadModeAutoObserver;
  private boolean mReadModeManual = false;
  private ReadModeManualContentObserver mReadModeManualObserver;
  private boolean mReadModeStatus = false;
  private ContentResolver mResolver;
  
  static
  {
    URI_READ_AUTO = Settings.System.getUriFor("rading_mode_status_auto");
    URI_GAME_MANUAL = Settings.System.getUriFor("game_mode_status_manual");
  }
  
  public OemSceneModeController(Context paramContext)
  {
    this.mContext = paramContext;
    this.mResolver = paramContext.getContentResolver();
    this.mReadModeManualObserver = new ReadModeManualContentObserver(this.mContext, this.mHandler);
    this.mReadModeAutoObserver = new ReadModeAutoContentObserver(this.mContext, this.mHandler);
    this.mGameModeManualObserver = new GameModeManualContentObserver(this.mContext, this.mHandler);
    this.mGameModeAutoObserver = new GameModeAutoContentObserver(this.mContext, this.mHandler);
    IntentFilter localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("com.oem.intent.action.DISABLE_GAME_MODE");
    this.mContext.registerReceiver(this.mBroadcastReceiver, localIntentFilter);
    this.mResolver.registerContentObserver(URI_READ_MANUAL, false, this.mReadModeManualObserver, -1);
    this.mResolver.registerContentObserver(URI_READ_AUTO, false, this.mReadModeAutoObserver, -1);
    this.mResolver.registerContentObserver(URI_GAME_MANUAL, false, this.mGameModeManualObserver, -1);
    this.mResolver.registerContentObserver(URI_GAME_AUTO, false, this.mGameModeAutoObserver, -1);
    this.mOemSceneButtonController = new OemSceneButtonController(paramContext);
  }
  
  private boolean getGameModeAuto()
  {
    if (!"force-on".equals(Settings.System.getStringForUser(this.mResolver, "game_mode_status_auto", -2))) {
      return "1".equals(Settings.System.getStringForUser(this.mResolver, "game_mode_status_auto", -2));
    }
    return true;
  }
  
  private boolean getGameModeButtonBlocked()
  {
    return "1".equals(Settings.System.getStringForUser(this.mResolver, "game_mode_lock_buttons", -2));
  }
  
  private boolean getGameModeHeadUpBlocked()
  {
    return "1".equals(Settings.System.getStringForUser(this.mResolver, "game_mode_block_notification", -2));
  }
  
  private boolean getGameModeManual()
  {
    if (!"force-on".equals(Settings.System.getStringForUser(this.mResolver, "game_mode_status_manual", -2))) {
      return "1".equals(Settings.System.getStringForUser(this.mResolver, "game_mode_status_manual", -2));
    }
    return true;
  }
  
  private boolean getGameModeStatus()
  {
    return "1".equals(Settings.System.getStringForUser(this.mResolver, "game_mode_status", -2));
  }
  
  private boolean getReadModeAuto()
  {
    if (!"force-on".equals(Settings.System.getStringForUser(this.mResolver, "rading_mode_status_auto", -2))) {
      return "1".equals(Settings.System.getStringForUser(this.mResolver, "rading_mode_status_auto", -2));
    }
    return true;
  }
  
  private boolean getReadModeManual()
  {
    if (!"force-on".equals(Settings.System.getStringForUser(this.mResolver, "reading_mode_status_manual", -2))) {
      return "1".equals(Settings.System.getStringForUser(this.mResolver, "reading_mode_status_manual", -2));
    }
    return true;
  }
  
  private boolean getReadModeStatus()
  {
    return "1".equals(Settings.System.getStringForUser(this.mResolver, "reading_mode_status", -2));
  }
  
  private void handleGameAutoChanged()
  {
    boolean bool1 = getGameModeAuto();
    boolean bool2 = getGameModeManual();
    this.mGameModeStatus = getGameModeStatus();
    boolean bool3 = this.mGameModeStatus;
    setGameModeAuto(bool1);
    if (bool2)
    {
      this.mGameModeStatus = true;
      if (bool1 == this.mGameModeAuto) {
        break label97;
      }
    }
    label97:
    for (int i = 1;; i = 0)
    {
      this.mGameModeManual = bool2;
      this.mGameModeAuto = bool1;
      if (bool3 != this.mGameModeStatus)
      {
        setGameModeStatus(this.mGameModeStatus);
        if (i != 0) {
          notifyGameModeToast(this.mGameModeStatus);
        }
      }
      return;
      this.mGameModeStatus = bool1;
      break;
    }
  }
  
  private void handleGameManualChanged()
  {
    boolean bool1 = getGameModeAuto();
    boolean bool2 = getGameModeManual();
    this.mGameModeStatus = getGameModeStatus();
    boolean bool3 = this.mGameModeStatus;
    setGameModeManual(bool2);
    this.mGameModeStatus = bool2;
    this.mGameModeManual = bool2;
    this.mGameModeAuto = bool1;
    if (bool3 != this.mGameModeStatus)
    {
      setGameModeStatus(this.mGameModeStatus);
      notifyGameModeToast(this.mGameModeStatus);
    }
  }
  
  private void handleReadAutoChanged()
  {
    boolean bool1 = getReadModeAuto();
    boolean bool2 = getReadModeManual();
    this.mReadModeStatus = getReadModeStatus();
    boolean bool3 = this.mReadModeStatus;
    setReadModeAuto(bool1);
    if (bool2) {}
    for (this.mReadModeStatus = true;; this.mReadModeStatus = bool1)
    {
      this.mReadModeManual = bool2;
      this.mReadModeAuto = bool1;
      if (bool3 != this.mReadModeStatus) {
        setReadModeStatus(this.mReadModeStatus);
      }
      return;
    }
  }
  
  private void handleReadManualChanged()
  {
    boolean bool1 = getReadModeAuto();
    boolean bool2 = getReadModeManual();
    this.mReadModeStatus = getReadModeStatus();
    boolean bool3 = this.mReadModeStatus;
    setReadModeManual(bool2);
    this.mReadModeStatus = bool2;
    this.mReadModeManual = bool2;
    this.mReadModeAuto = bool1;
    if (bool3 != this.mReadModeStatus) {
      setReadModeStatus(this.mReadModeStatus);
    }
  }
  
  private boolean isGameModeEverEnabled()
  {
    return "true".equals(SystemProperties.get("persist.sys.oem.gamemode_dirty", ""));
  }
  
  private Toast makeAllUserToastAndShow(int paramInt)
  {
    Toast localToast = Toast.makeText(this.mContext, paramInt, 0);
    WindowManager.LayoutParams localLayoutParams = localToast.getWindowParams();
    localLayoutParams.privateFlags |= 0x10;
    localToast.show();
    return localToast;
  }
  
  private void notifyGameMode(boolean paramBoolean)
  {
    NotificationManager localNotificationManager = (NotificationManager)this.mContext.getSystemService("notification");
    if (paramBoolean)
    {
      PendingIntent localPendingIntent = PendingIntent.getBroadcast(this.mContext, 0, new Intent("com.oem.intent.action.DISABLE_GAME_MODE"), 268435456);
      Object localObject = new Notification.Builder(this.mContext).setSmallIcon(84017152).setContentTitle(this.mContext.getString(84541499)).setContentText(this.mContext.getString(84541500)).setOngoing(true).setAutoCancel(false).setPriority(1).setVisibility(1).setContentIntent(localPendingIntent).setDeleteIntent(localPendingIntent);
      ((Notification.Builder)localObject).setCustomContentView(((Notification.Builder)localObject).createContentView());
      localObject = ((Notification.Builder)localObject).build();
      ((Notification)localObject).contentView.setOnClickPendingIntent(16909240, localPendingIntent);
      localNotificationManager.notifyAsUser("SceneModeController", 5566, (Notification)localObject, UserHandle.CURRENT);
      return;
    }
    localNotificationManager.cancelAsUser("SceneModeController", 5566, UserHandle.CURRENT);
  }
  
  private void notifyGameModeToast(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      if (isGameModeEverEnabled()) {
        makeAllUserToastAndShow(84541497);
      }
      do
      {
        return;
        setGameModeEverEnabled();
        if ((getGameModeHeadUpBlocked()) && (getGameModeButtonBlocked()))
        {
          makeAllUserToastAndShow(84541496);
          return;
        }
        if (getGameModeHeadUpBlocked())
        {
          makeAllUserToastAndShow(84541494);
          return;
        }
      } while (!getGameModeButtonBlocked());
      makeAllUserToastAndShow(84541495);
      return;
    }
    makeAllUserToastAndShow(84541498);
  }
  
  private void notifyReadMode(boolean paramBoolean) {}
  
  private void setGameModeAuto(boolean paramBoolean)
  {
    ContentResolver localContentResolver = this.mResolver;
    if (paramBoolean) {}
    for (String str = "1";; str = "0")
    {
      Settings.System.putStringForUser(localContentResolver, "game_mode_status_auto", str, -2);
      return;
    }
  }
  
  private void setGameModeEverEnabled()
  {
    if (DBG) {
      Log.d("OemSceneModeController", "[scene] setGameModeEverEnabled was called");
    }
    SystemProperties.set("persist.sys.oem.gamemode_dirty", "true");
  }
  
  private void setGameModeManual(boolean paramBoolean)
  {
    ContentResolver localContentResolver = this.mResolver;
    if (paramBoolean) {}
    for (String str = "1";; str = "0")
    {
      Settings.System.putStringForUser(localContentResolver, "game_mode_status_manual", str, -2);
      return;
    }
  }
  
  private boolean setGameModeStatus(boolean paramBoolean)
  {
    ContentResolver localContentResolver = this.mResolver;
    if (paramBoolean) {}
    for (String str = "1";; str = "0")
    {
      Settings.System.putStringForUser(localContentResolver, "game_mode_status", str, -2);
      notifyGameMode(paramBoolean);
      return paramBoolean;
    }
  }
  
  private void setReadModeAuto(boolean paramBoolean)
  {
    ContentResolver localContentResolver = this.mResolver;
    if (paramBoolean) {}
    for (String str = "1";; str = "0")
    {
      Settings.System.putStringForUser(localContentResolver, "rading_mode_status_auto", str, -2);
      return;
    }
  }
  
  private void setReadModeManual(boolean paramBoolean)
  {
    ContentResolver localContentResolver = this.mResolver;
    if (paramBoolean) {}
    for (String str = "1";; str = "0")
    {
      Settings.System.putStringForUser(localContentResolver, "reading_mode_status_manual", str, -2);
      return;
    }
  }
  
  private boolean setReadModeStatus(boolean paramBoolean)
  {
    ContentResolver localContentResolver = this.mResolver;
    if (paramBoolean) {}
    for (String str = "1";; str = "0")
    {
      Settings.System.putStringForUser(localContentResolver, "reading_mode_status", str, -2);
      notifyReadMode(paramBoolean);
      return paramBoolean;
    }
  }
  
  private void showStatusLog()
  {
    if (!DBG) {
      return;
    }
    String str = "[scene] Read Manual: " + this.mReadModeManual + "\t Game Manual: " + this.mGameModeManual + "\n";
    str = str + "[scene] Read Auto: " + this.mReadModeAuto + "\t Game Auto: " + this.mGameModeAuto + "\n";
    str = str + "[scene] Read Status: " + this.mReadModeStatus + "\t Game Status: " + this.mGameModeStatus + "\n";
    str = str + "[scene] Proactive monitoring: " + this.mIsMonitoringProactiveProvider + "\t Passive monitoring: " + this.mIsMonitoringPassiveProvider + "\n";
    str = str + "[scene] -----------";
    Log.d("OemSceneModeController", "[scene] values: \n" + str);
  }
  
  private boolean validateValue(String paramString)
  {
    if (!"force-on".equals(paramString)) {
      return "force-off".equals(paramString);
    }
    return true;
  }
  
  public void handleStartMonitor()
  {
    if (DBG) {
      Log.d("OemSceneModeController", "[scene] start monitoring, " + URI_READ_MANUAL + " " + URI_READ_AUTO + " " + URI_GAME_MANUAL + " " + URI_GAME_AUTO);
    }
    this.mIsMonitoringProactiveProvider = true;
    this.mIsMonitoringPassiveProvider = true;
    this.mOemSceneButtonController.startMonitor();
    notifyReadMode(this.mReadModeStatus);
    notifyGameMode(this.mGameModeStatus);
  }
  
  public void handleStartMonitorPassive()
  {
    if (DBG) {
      Log.d("OemSceneModeController", "[scene] start monitoring passive switcher");
    }
    this.mIsMonitoringPassiveProvider = true;
    this.mOemSceneButtonController.startMonitor();
    this.mHandler.sendEmptyMessage(1);
    this.mHandler.sendEmptyMessage(3);
  }
  
  public void handleStopMonitor()
  {
    if (DBG) {
      Log.d("OemSceneModeController", "[scene] stop monitoring");
    }
    notifyReadMode(setReadModeStatus(false));
    notifyGameMode(setGameModeStatus(false));
    this.mReadModeStatus = false;
    this.mGameModeStatus = false;
    this.mOemSceneButtonController.stopMonitor();
    this.mIsMonitoringProactiveProvider = false;
    this.mIsMonitoringPassiveProvider = false;
  }
  
  public void handleStopMonitorPassive()
  {
    if (DBG) {
      Log.d("OemSceneModeController", "[scene] stop monitoring passive switcher");
    }
    if ((!this.mReadModeStatus) || (this.mReadModeManual)) {
      if ((this.mGameModeStatus) && (!this.mGameModeManual)) {
        break label73;
      }
    }
    for (;;)
    {
      this.mOemSceneButtonController.stopMonitorPassive();
      this.mIsMonitoringPassiveProvider = false;
      return;
      notifyReadMode(setReadModeStatus(false));
      this.mReadModeStatus = false;
      break;
      label73:
      notifyGameMode(setGameModeStatus(false));
      this.mGameModeStatus = false;
    }
  }
  
  public boolean preEvaluateModeStatus(int paramInt1, int paramInt2)
  {
    boolean bool2 = false;
    boolean bool1;
    if (paramInt1 == 0) {
      if (paramInt2 == 1) {
        if (getReadModeAuto()) {
          bool1 = this.mIsMonitoringPassiveProvider;
        }
      }
    }
    for (;;)
    {
      showStatusLog();
      return bool1;
      bool1 = false;
      continue;
      bool1 = bool2;
      if (DBG)
      {
        Log.w("OemSceneModeController", "[scene] Not yet supported");
        bool1 = bool2;
        continue;
        bool1 = bool2;
        if (DBG)
        {
          Log.w("OemSceneModeController", "[scene] Not yet supported");
          bool1 = bool2;
        }
      }
    }
  }
  
  public void startMonitor()
  {
    this.mHandler.sendEmptyMessage(1);
    this.mHandler.sendEmptyMessage(3);
    this.mHandler.sendEmptyMessage(4);
  }
  
  public void startMonitorPassive()
  {
    this.mHandler.sendEmptyMessage(7);
  }
  
  public void stopMonitor()
  {
    this.mHandler.sendEmptyMessage(5);
  }
  
  public void stopMonitorPassive()
  {
    this.mHandler.sendEmptyMessage(6);
  }
  
  public class GameModeAutoContentObserver
    extends ContentObserver
  {
    public GameModeAutoContentObserver(Context paramContext, Handler paramHandler)
    {
      super();
    }
    
    public void onChange(boolean paramBoolean)
    {
      if (!OemSceneModeController.-get2(OemSceneModeController.this)) {
        return;
      }
      if (!OemSceneModeController.-wrap0(OemSceneModeController.this, Settings.System.getStringForUser(OemSceneModeController.-get4(OemSceneModeController.this), "game_mode_status_auto", -2))) {
        return;
      }
      OemSceneModeController.-get1(OemSceneModeController.this).sendEmptyMessage(3);
      if (OemSceneModeController.-get0()) {
        Log.d("OemSceneModeController", "[scene] Game Auto changed!");
      }
    }
  }
  
  public class GameModeManualContentObserver
    extends ContentObserver
  {
    public GameModeManualContentObserver(Context paramContext, Handler paramHandler)
    {
      super();
    }
    
    public void onChange(boolean paramBoolean)
    {
      if (!OemSceneModeController.-get3(OemSceneModeController.this)) {
        return;
      }
      if (!OemSceneModeController.-wrap0(OemSceneModeController.this, Settings.System.getStringForUser(OemSceneModeController.-get4(OemSceneModeController.this), "game_mode_status_manual", -2))) {
        return;
      }
      OemSceneModeController.-get1(OemSceneModeController.this).sendEmptyMessage(2);
      if (OemSceneModeController.-get0()) {
        Log.d("OemSceneModeController", "[scene] Game Manual changed!");
      }
    }
  }
  
  public class ReadModeAutoContentObserver
    extends ContentObserver
  {
    public ReadModeAutoContentObserver(Context paramContext, Handler paramHandler)
    {
      super();
    }
    
    public void onChange(boolean paramBoolean)
    {
      if (!OemSceneModeController.-get2(OemSceneModeController.this)) {
        return;
      }
      if (!OemSceneModeController.-wrap0(OemSceneModeController.this, Settings.System.getStringForUser(OemSceneModeController.-get4(OemSceneModeController.this), "rading_mode_status_auto", -2))) {
        return;
      }
      OemSceneModeController.-get1(OemSceneModeController.this).sendEmptyMessage(1);
      if (OemSceneModeController.-get0()) {
        Log.d("OemSceneModeController", "[scene] Read Auto changed!");
      }
    }
  }
  
  public class ReadModeManualContentObserver
    extends ContentObserver
  {
    public ReadModeManualContentObserver(Context paramContext, Handler paramHandler)
    {
      super();
    }
    
    public void onChange(boolean paramBoolean)
    {
      if (!OemSceneModeController.-get3(OemSceneModeController.this)) {
        return;
      }
      if (!OemSceneModeController.-wrap0(OemSceneModeController.this, Settings.System.getStringForUser(OemSceneModeController.-get4(OemSceneModeController.this), "reading_mode_status_manual", -2))) {
        return;
      }
      OemSceneModeController.-get1(OemSceneModeController.this).sendEmptyMessage(0);
      if (OemSceneModeController.-get0()) {
        Log.d("OemSceneModeController", "[scene] Read Manual changed!");
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/OemSceneModeController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */