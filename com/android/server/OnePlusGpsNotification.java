package com.android.server;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.StatusBarManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.UserHandle;
import android.util.ArrayMap;
import android.util.Slog;
import java.util.ArrayList;

public class OnePlusGpsNotification
{
  private static final long CANCEL_DELAY = 800L;
  static boolean DEBUG = Build.DEBUG_ONEPLUS;
  private static final int MESSAGE_CANCEL = 56001;
  private static final int MESSAGE_START = 56000;
  private static final long START_DELAY = 500L;
  static ArrayMap<String, Integer> mNotificationIdPackage;
  static ArrayMap<Integer, String> mPendingGpsPackage;
  static ArrayList<Integer> mPendingPackageList = new ArrayList();
  String TAG = "OnePlusGpsNotification";
  int idCount = 0;
  private String mAction = "android.intent.oneplus.gpsforcestop";
  public Context mContext;
  public ArrayList<String> mCurrentGpsLists = new ArrayList();
  public ArrayList<String> mCurrentGpsPackageList = new ArrayList();
  private GpsNotificationHandler mHandler;
  private volatile boolean mLastIsCancel = false;
  private volatile long mLastNotifyTime = -1L;
  final Object mLock = new Object();
  private NotificationManager mNotificationManager;
  PendingIntent mPendingIntent = null;
  BroadcastReceiver mReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if (paramAnonymousIntent.getAction().equals(OnePlusGpsNotification.-get0(OnePlusGpsNotification.this)))
      {
        paramAnonymousContext = paramAnonymousIntent.getStringExtra("forcePackageName");
        if ((paramAnonymousContext != null) && (!paramAnonymousContext.equals(""))) {}
      }
      else
      {
        return;
      }
      if (OnePlusGpsNotification.DEBUG) {
        Slog.i(OnePlusGpsNotification.this.TAG, "mReceiver packageName  = " + paramAnonymousContext);
      }
      OnePlusGpsNotification.-get5(OnePlusGpsNotification.this).forceStopPackage(paramAnonymousContext);
    }
  };
  Intent mSettingLocationIntent = null;
  private String mSlotLocation;
  private StatusBarManager mStatusBarManager;
  private ActivityManager manager;
  
  static
  {
    mNotificationIdPackage = new ArrayMap();
    mPendingGpsPackage = new ArrayMap();
  }
  
  public OnePlusGpsNotification(Context paramContext)
  {
    this.mContext = paramContext;
    this.mNotificationManager = ((NotificationManager)paramContext.getSystemService("notification"));
    this.mStatusBarManager = ((StatusBarManager)paramContext.getSystemService("statusbar"));
    this.manager = ((ActivityManager)paramContext.getSystemService("activity"));
    IntentFilter localIntentFilter = new IntentFilter();
    localIntentFilter.addAction(this.mAction);
    paramContext.registerReceiver(this.mReceiver, localIntentFilter);
    this.mHandler = new GpsNotificationHandler(Looper.getMainLooper());
    this.mSettingLocationIntent = new Intent("android.settings.LOCATION_SOURCE_SETTINGS");
    this.mSettingLocationIntent.addFlags(67108864);
    this.mSettingLocationIntent.addFlags(536870912);
    this.mSlotLocation = paramContext.getString(17039394);
  }
  
  public void notifyPackageNotification()
  {
    Object localObject1 = "";
    int i;
    synchronized (this.mCurrentGpsPackageList)
    {
      this.mCurrentGpsLists.clear();
      this.mCurrentGpsLists.addAll(this.mCurrentGpsPackageList);
      i = this.mCurrentGpsPackageList.size();
      if (i == 0) {
        return;
      }
      this.mStatusBarManager.setIcon(this.mSlotLocation, 84017153, 0, "");
      if (this.mCurrentGpsPackageList.size() > 1) {
        i = 0;
      }
      for (;;)
      {
        int j = this.mCurrentGpsPackageList.size() - 1;
        label86:
        if (j >= 0)
        {
          Object localObject4 = (String)this.mCurrentGpsPackageList.get(j);
          try
          {
            localContext = this.mContext.createPackageContext((String)localObject4, 0);
            localObject4 = this.mContext.getPackageManager().getApplicationInfo((String)localObject4, 0);
            localObject4 = (String)this.mContext.getPackageManager().getApplicationLabel((ApplicationInfo)localObject4);
            if (localContext == null)
            {
              j -= 1;
              break label86;
              i = 1;
            }
          }
          catch (PackageManager.NameNotFoundException localNameNotFoundException)
          {
            for (;;)
            {
              Slog.w(this.TAG, "Unable to create context for heavy notification", localNameNotFoundException);
              ??? = "";
              Context localContext = null;
              continue;
              if (j == 0) {
                localObject1 = (String)localObject1 + (String)???;
              } else {
                localObject1 = (String)localObject1 + (String)??? + "、";
              }
            }
          }
        }
      }
      if (!DEBUG) {}
    }
    for (;;)
    {
      synchronized (this.mCurrentGpsPackageList)
      {
        Slog.i(this.TAG, "notifyPackageNotification mCurrentGpsPackageList = " + this.mCurrentGpsPackageList + " mCurrentGpsList = " + (String)localObject1);
        if (i != 0)
        {
          ??? = String.valueOf(this.mContext.getText(84541504));
          this.mPendingIntent = PendingIntent.getActivityAsUser(this.mContext, 0, this.mSettingLocationIntent, 134217728, null, UserHandle.CURRENT);
          localObject1 = new Notification.Builder(this.mContext).setSmallIcon(17303263).setWhen(0L).setOngoing(true).setTicker((CharSequence)???).setDefaults(0).setPriority(-2).setColor(this.mContext.getColor(17170523)).setContentTitle((CharSequence)???).setContentText((CharSequence)localObject1).setContentIntent(this.mPendingIntent).setVisibility(1).build();
          this.mNotificationManager.notifyAsUser(null, 84541503, (Notification)localObject1, UserHandle.ALL);
          return;
          localObject2 = finally;
          throw ((Throwable)localObject2);
        }
      }
      ??? = String.valueOf(this.mContext.getText(84541503));
    }
  }
  
  public void updateGpsRequstPackage(ArrayList<String> paramArrayList)
  {
    for (;;)
    {
      synchronized (this.mCurrentGpsPackageList)
      {
        if (DEBUG) {
          Slog.i(this.TAG, "updateGpsRequstPackage mLastIsCancel = " + this.mLastIsCancel + " start = " + paramArrayList);
        }
        long l = System.currentTimeMillis();
        if ((this.mCurrentGpsLists.size() > 0) && (this.mCurrentGpsLists.equals(paramArrayList)))
        {
          this.mHandler.removeMessages(56001);
          if (DEBUG) {
            Slog.i(this.TAG, "updateGpsRequstPackage mLastIsCancel = " + this.mLastIsCancel + " start = " + paramArrayList + " mCurrentGpsLists =" + this.mCurrentGpsLists + " same return");
          }
          return;
        }
        if (l - this.mLastNotifyTime < 500L)
        {
          this.idCount += 1;
          if (this.idCount <= 3) {
            this.mHandler.removeMessages(56000);
          }
          if (paramArrayList.size() != 0) {
            break label301;
          }
          if (this.mCurrentGpsPackageList.size() != 0) {
            this.mCurrentGpsPackageList.clear();
          }
          paramArrayList = Message.obtain();
          paramArrayList.what = 56001;
          this.mHandler.sendMessageDelayed(paramArrayList, 800L);
          return;
        }
        if ((this.mLastIsCancel) && (l - this.mLastNotifyTime < 800L))
        {
          this.mHandler.removeMessages(56001);
          this.idCount = 0;
        }
      }
      this.idCount = 0;
      continue;
      label301:
      this.mHandler.removeMessages(56001);
      this.mCurrentGpsPackageList.clear();
      this.mCurrentGpsPackageList.addAll(paramArrayList);
      paramArrayList = Message.obtain();
      paramArrayList.what = 56000;
      paramArrayList.obj = this.mCurrentGpsPackageList;
      this.mHandler.sendMessageDelayed(paramArrayList, 500L);
    }
  }
  
  private class GpsNotificationHandler
    extends Handler
  {
    public GpsNotificationHandler(Looper paramLooper)
    {
      super();
    }
    
    public void handleMessage(Message arg1)
    {
      if (OnePlusGpsNotification.DEBUG) {}
      synchronized (OnePlusGpsNotification.this.mCurrentGpsPackageList)
      {
        Slog.i(OnePlusGpsNotification.this.TAG, "GpsNotificationHander handleMessage mCurrentGpsPackageList = " + OnePlusGpsNotification.this.mCurrentGpsPackageList + " msg.what =" + ???.what);
        switch (???.what)
        {
        default: 
          return;
        }
      }
      OnePlusGpsNotification.-get1(OnePlusGpsNotification.this).removeMessages(56000);
      OnePlusGpsNotification.-set1(OnePlusGpsNotification.this, System.currentTimeMillis());
      OnePlusGpsNotification.-set0(OnePlusGpsNotification.this, false);
      OnePlusGpsNotification.this.notifyPackageNotification();
      return;
      synchronized (OnePlusGpsNotification.this.mCurrentGpsPackageList)
      {
        if (OnePlusGpsNotification.this.mCurrentGpsPackageList.size() == 0)
        {
          OnePlusGpsNotification.this.mCurrentGpsLists.clear();
          OnePlusGpsNotification.-set0(OnePlusGpsNotification.this, true);
          OnePlusGpsNotification.-set1(OnePlusGpsNotification.this, System.currentTimeMillis());
          OnePlusGpsNotification.-get2(OnePlusGpsNotification.this).cancel(84541503);
          OnePlusGpsNotification.-get4(OnePlusGpsNotification.this).removeIcon(OnePlusGpsNotification.-get3(OnePlusGpsNotification.this));
        }
        return;
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/OnePlusGpsNotification.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */