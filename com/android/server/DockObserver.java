package com.android.server;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.os.UEventObserver;
import android.os.UEventObserver.UEvent;
import android.os.UserHandle;
import android.provider.Settings.Global;
import android.util.Log;
import android.util.Slog;
import java.io.FileDescriptor;
import java.io.PrintWriter;

final class DockObserver
  extends SystemService
{
  private static final String DOCK_STATE_PATH = "/sys/class/switch/dock/state";
  private static final String DOCK_UEVENT_MATCH = "DEVPATH=/devices/virtual/switch/dock";
  private static final int MSG_DOCK_STATE_CHANGED = 0;
  private static final String TAG = "DockObserver";
  private int mActualDockState = 0;
  private final boolean mAllowTheaterModeWakeFromDock;
  private final Handler mHandler = new Handler(true)
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      switch (paramAnonymousMessage.what)
      {
      default: 
        return;
      }
      DockObserver.-wrap0(DockObserver.this);
      DockObserver.-get5(DockObserver.this).release();
    }
  };
  private final Object mLock = new Object();
  private final UEventObserver mObserver = new UEventObserver()
  {
    public void onUEvent(UEventObserver.UEvent paramAnonymousUEvent)
    {
      if (Log.isLoggable("DockObserver", 2)) {
        Slog.v("DockObserver", "Dock UEVENT: " + paramAnonymousUEvent.toString());
      }
      try
      {
        synchronized (DockObserver.-get1(DockObserver.this))
        {
          DockObserver.-wrap1(DockObserver.this, Integer.parseInt(paramAnonymousUEvent.get("SWITCH_STATE")));
          return;
        }
        return;
      }
      catch (NumberFormatException localNumberFormatException)
      {
        Slog.e("DockObserver", "Could not parse switch state from event " + paramAnonymousUEvent);
      }
    }
  };
  private final PowerManager mPowerManager;
  private int mPreviousDockState = 0;
  private int mReportedDockState = 0;
  private boolean mSystemReady;
  private boolean mUpdatesStopped;
  private final PowerManager.WakeLock mWakeLock;
  
  public DockObserver(Context paramContext)
  {
    super(paramContext);
    this.mPowerManager = ((PowerManager)paramContext.getSystemService("power"));
    this.mWakeLock = this.mPowerManager.newWakeLock(1, "DockObserver");
    this.mAllowTheaterModeWakeFromDock = paramContext.getResources().getBoolean(17956915);
    init();
    this.mObserver.startObserving("DEVPATH=/devices/virtual/switch/dock");
  }
  
  private void handleDockStateChange()
  {
    for (;;)
    {
      synchronized (this.mLock)
      {
        Slog.i("DockObserver", "Dock state changed from " + this.mPreviousDockState + " to " + this.mReportedDockState);
        int i = this.mPreviousDockState;
        this.mPreviousDockState = this.mReportedDockState;
        ContentResolver localContentResolver = getContext().getContentResolver();
        if (Settings.Global.getInt(localContentResolver, "device_provisioned", 0) == 0)
        {
          Slog.i("DockObserver", "Device not provisioned, skipping dock broadcast");
          return;
        }
        Intent localIntent = new Intent("android.intent.action.DOCK_EVENT");
        localIntent.addFlags(536870912);
        localIntent.putExtra("android.intent.extra.DOCK_STATE", this.mReportedDockState);
        Object localObject1;
        if (Settings.Global.getInt(localContentResolver, "dock_sounds_enabled", 1) == 1)
        {
          localObject1 = null;
          if (this.mReportedDockState != 0) {
            continue;
          }
          if (i == 1) {
            break label297;
          }
          if (i != 3) {
            continue;
          }
          break label297;
          if (localObject1 != null)
          {
            localObject1 = Settings.Global.getString(localContentResolver, (String)localObject1);
            if (localObject1 != null)
            {
              localObject1 = Uri.parse("file://" + (String)localObject1);
              if (localObject1 != null)
              {
                localObject1 = RingtoneManager.getRingtone(getContext(), (Uri)localObject1);
                if (localObject1 != null)
                {
                  ((Ringtone)localObject1).setStreamType(1);
                  ((Ringtone)localObject1).play();
                }
              }
            }
          }
        }
        getContext().sendStickyBroadcastAsUser(localIntent, UserHandle.ALL);
        return;
        if (i != 4)
        {
          if (i != 2) {
            continue;
          }
          localObject1 = "car_undock_sound";
          continue;
          if ((this.mReportedDockState == 1) || (this.mReportedDockState == 3) || (this.mReportedDockState == 4)) {
            break label303;
          }
          if (this.mReportedDockState != 2) {
            continue;
          }
          localObject1 = "car_dock_sound";
        }
      }
      label297:
      String str = "desk_undock_sound";
      continue;
      label303:
      str = "desk_dock_sound";
    }
  }
  
  /* Error */
  private void init()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 50	com/android/server/DockObserver:mLock	Ljava/lang/Object;
    //   4: astore_1
    //   5: aload_1
    //   6: monitorenter
    //   7: sipush 1024
    //   10: newarray <illegal type>
    //   12: astore_3
    //   13: new 248	java/io/FileReader
    //   16: dup
    //   17: ldc 15
    //   19: invokespecial 249	java/io/FileReader:<init>	(Ljava/lang/String;)V
    //   22: astore_2
    //   23: aload_0
    //   24: new 251	java/lang/String
    //   27: dup
    //   28: aload_3
    //   29: iconst_0
    //   30: aload_2
    //   31: aload_3
    //   32: iconst_0
    //   33: sipush 1024
    //   36: invokevirtual 255	java/io/FileReader:read	([CII)I
    //   39: invokespecial 258	java/lang/String:<init>	([CII)V
    //   42: invokevirtual 261	java/lang/String:trim	()Ljava/lang/String;
    //   45: invokestatic 267	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   48: invokespecial 78	com/android/server/DockObserver:setActualDockStateLocked	(I)V
    //   51: aload_0
    //   52: aload_0
    //   53: getfield 45	com/android/server/DockObserver:mActualDockState	I
    //   56: putfield 53	com/android/server/DockObserver:mPreviousDockState	I
    //   59: aload_2
    //   60: invokevirtual 270	java/io/FileReader:close	()V
    //   63: aload_1
    //   64: monitorexit
    //   65: return
    //   66: astore_3
    //   67: aload_2
    //   68: invokevirtual 270	java/io/FileReader:close	()V
    //   71: aload_3
    //   72: athrow
    //   73: astore_2
    //   74: ldc 24
    //   76: ldc_w 272
    //   79: invokestatic 275	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   82: pop
    //   83: goto -20 -> 63
    //   86: astore_2
    //   87: aload_1
    //   88: monitorexit
    //   89: aload_2
    //   90: athrow
    //   91: astore_2
    //   92: ldc 24
    //   94: ldc_w 277
    //   97: aload_2
    //   98: invokestatic 281	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   101: pop
    //   102: goto -39 -> 63
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	105	0	this	DockObserver
    //   22	46	2	localFileReader	java.io.FileReader
    //   73	1	2	localFileNotFoundException	java.io.FileNotFoundException
    //   86	4	2	localObject2	Object
    //   91	7	2	localException	Exception
    //   12	20	3	arrayOfChar	char[]
    //   66	6	3	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   23	59	66	finally
    //   7	23	73	java/io/FileNotFoundException
    //   59	63	73	java/io/FileNotFoundException
    //   67	73	73	java/io/FileNotFoundException
    //   7	23	86	finally
    //   59	63	86	finally
    //   67	73	86	finally
    //   74	83	86	finally
    //   92	102	86	finally
    //   7	23	91	java/lang/Exception
    //   59	63	91	java/lang/Exception
    //   67	73	91	java/lang/Exception
  }
  
  private void setActualDockStateLocked(int paramInt)
  {
    this.mActualDockState = paramInt;
    if (!this.mUpdatesStopped) {
      setDockStateLocked(paramInt);
    }
  }
  
  private void setDockStateLocked(int paramInt)
  {
    if (paramInt != this.mReportedDockState)
    {
      this.mReportedDockState = paramInt;
      if (this.mSystemReady)
      {
        if ((this.mAllowTheaterModeWakeFromDock) || (Settings.Global.getInt(getContext().getContentResolver(), "theater_mode_on", 0) == 0)) {
          this.mPowerManager.wakeUp(SystemClock.uptimeMillis(), "android.server:DOCK");
        }
        updateLocked();
      }
    }
  }
  
  private void updateLocked()
  {
    this.mWakeLock.acquire();
    this.mHandler.sendEmptyMessage(0);
  }
  
  public void onBootPhase(int paramInt)
  {
    if (paramInt == 550) {}
    synchronized (this.mLock)
    {
      this.mSystemReady = true;
      if (this.mReportedDockState != 0) {
        updateLocked();
      }
      return;
    }
  }
  
  public void onStart()
  {
    publishBinderService("DockObserver", new BinderService(null));
  }
  
  private final class BinderService
    extends Binder
  {
    private BinderService() {}
    
    protected void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
    {
      if (DockObserver.this.getContext().checkCallingOrSelfPermission("android.permission.DUMP") != 0)
      {
        paramPrintWriter.println("Permission Denial: can't dump dock observer service from from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
        return;
      }
      long l = Binder.clearCallingIdentity();
      for (;;)
      {
        try
        {
          paramFileDescriptor = DockObserver.-get1(DockObserver.this);
          if (paramArrayOfString != null) {}
          try
          {
            if (paramArrayOfString.length == 0)
            {
              paramPrintWriter.println("Current Dock Observer Service state:");
              if (DockObserver.-get4(DockObserver.this)) {
                paramPrintWriter.println("  (UPDATES STOPPED -- use 'reset' to restart)");
              }
              paramPrintWriter.println("  reported state: " + DockObserver.-get3(DockObserver.this));
              paramPrintWriter.println("  previous state: " + DockObserver.-get2(DockObserver.this));
              paramPrintWriter.println("  actual state: " + DockObserver.-get0(DockObserver.this));
              return;
            }
            if ("-a".equals(paramArrayOfString[0])) {
              continue;
            }
            if ((paramArrayOfString.length != 3) || (!"set".equals(paramArrayOfString[0]))) {
              break label331;
            }
            String str = paramArrayOfString[1];
            paramArrayOfString = paramArrayOfString[2];
            try
            {
              if (!"state".equals(str)) {
                break label304;
              }
              DockObserver.-set0(DockObserver.this, true);
              DockObserver.-wrap2(DockObserver.this, Integer.parseInt(paramArrayOfString));
            }
            catch (NumberFormatException localNumberFormatException)
            {
              paramPrintWriter.println("Bad value: " + paramArrayOfString);
            }
            continue;
            paramFileDescriptor = finally;
          }
          finally {}
          paramPrintWriter.println("Unknown set option: " + localNumberFormatException);
        }
        finally
        {
          Binder.restoreCallingIdentity(l);
        }
        label304:
        continue;
        label331:
        if ((paramArrayOfString.length == 1) && ("reset".equals(paramArrayOfString[0])))
        {
          DockObserver.-set0(DockObserver.this, false);
          DockObserver.-wrap2(DockObserver.this, DockObserver.-get0(DockObserver.this));
        }
        else
        {
          paramPrintWriter.println("Dump current dock state, or:");
          paramPrintWriter.println("  set state <value>");
          paramPrintWriter.println("  reset");
        }
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/DockObserver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */