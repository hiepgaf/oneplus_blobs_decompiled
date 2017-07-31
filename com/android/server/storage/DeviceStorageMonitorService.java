package com.android.server.storage;

import android.app.Notification;
import android.app.Notification.BigTextStyle;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.IPackageDataObserver.Stub;
import android.content.pm.IPackageManager;
import android.content.pm.IPackageManager.Stub;
import android.os.Binder;
import android.os.Environment;
import android.os.FileObserver;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.StatFs;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.storage.StorageManager;
import android.provider.Settings.Global;
import android.text.format.Formatter;
import android.util.EventLog;
import android.util.Slog;
import android.util.TimeUtils;
import com.android.server.EventLogTags;
import com.android.server.SystemService;
import com.android.server.pm.InstructionSets;
import dalvik.system.VMRuntime;
import java.io.File;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public class DeviceStorageMonitorService
  extends SystemService
{
  private static final long BOOT_IMAGE_STORAGE_REQUIREMENT = 262144000L;
  private static final File CACHE_PATH = Environment.getDownloadCacheDirectory();
  private static final File DATA_PATH = ;
  static final boolean DEBUG = false;
  private static final long DEFAULT_CHECK_INTERVAL = 60000L;
  private static final long DEFAULT_DISK_FREE_CHANGE_REPORTING_THRESHOLD = 2097152L;
  private static final int DEFAULT_FREE_STORAGE_LOG_INTERVAL_IN_MINUTES = 720;
  static final int DEVICE_MEMORY_WHAT = 1;
  private static final int LOW_MEMORY_NOTIFICATION_ID = 1;
  private static final int MONITOR_INTERVAL = 1;
  static final String SERVICE = "devicestoragemonitor";
  private static final File SYSTEM_PATH = Environment.getRootDirectory();
  static final String TAG = "DeviceStorageMonitorService";
  private static final int _FALSE = 0;
  private static final int _TRUE = 1;
  static final boolean localLOGV = false;
  private CacheFileDeletedObserver mCacheFileDeletedObserver;
  private final StatFs mCacheFileStats;
  private CachePackageDataObserver mClearCacheObserver;
  boolean mClearSucceeded = false;
  boolean mClearingCache;
  private final StatFs mDataFileStats;
  private long mFreeMem;
  private long mFreeMemAfterLastCacheClear;
  private final Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      boolean bool = true;
      if (paramAnonymousMessage.what != 1)
      {
        Slog.e("DeviceStorageMonitorService", "Will not process invalid message");
        return;
      }
      DeviceStorageMonitorService localDeviceStorageMonitorService = DeviceStorageMonitorService.this;
      if (paramAnonymousMessage.arg1 == 1) {}
      for (;;)
      {
        localDeviceStorageMonitorService.checkMemory(bool);
        return;
        bool = false;
      }
    }
  };
  private final boolean mIsBootImageOnDisk;
  private long mLastReportedFreeMem;
  private long mLastReportedFreeMemTime = 0L;
  private final DeviceStorageMonitorInternal mLocalService = new DeviceStorageMonitorInternal()
  {
    public void checkMemory()
    {
      DeviceStorageMonitorService.this.postCheckMemoryMsg(true, 0L);
    }
    
    public long getMemoryLowThreshold()
    {
      return DeviceStorageMonitorService.this.mMemLowThreshold;
    }
    
    public boolean isMemoryLow()
    {
      return DeviceStorageMonitorService.this.mLowMemFlag;
    }
  };
  boolean mLowMemFlag = false;
  private long mMemCacheStartTrimThreshold;
  private long mMemCacheTrimToThreshold;
  private boolean mMemFullFlag = false;
  private long mMemFullThreshold;
  long mMemLowThreshold;
  private final IBinder mRemoteService = new Binder()
  {
    protected void dump(FileDescriptor paramAnonymousFileDescriptor, PrintWriter paramAnonymousPrintWriter, String[] paramAnonymousArrayOfString)
    {
      if (DeviceStorageMonitorService.this.getContext().checkCallingOrSelfPermission("android.permission.DUMP") != 0)
      {
        paramAnonymousPrintWriter.println("Permission Denial: can't dump devicestoragemonitor from from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
        return;
      }
      DeviceStorageMonitorService.this.dumpImpl(paramAnonymousPrintWriter);
    }
  };
  private final ContentResolver mResolver;
  private final Intent mStorageFullIntent;
  private final Intent mStorageLowIntent;
  private final Intent mStorageNotFullIntent;
  private final Intent mStorageOkIntent;
  private final StatFs mSystemFileStats;
  private long mThreadStartTime = -1L;
  private final long mTotalMemory;
  
  public DeviceStorageMonitorService(Context paramContext)
  {
    super(paramContext);
    this.mResolver = paramContext.getContentResolver();
    this.mIsBootImageOnDisk = isBootImageOnDisk();
    this.mDataFileStats = new StatFs(DATA_PATH.getAbsolutePath());
    this.mSystemFileStats = new StatFs(SYSTEM_PATH.getAbsolutePath());
    this.mCacheFileStats = new StatFs(CACHE_PATH.getAbsolutePath());
    this.mTotalMemory = (this.mDataFileStats.getBlockCount() * this.mDataFileStats.getBlockSize());
    this.mStorageLowIntent = new Intent("android.intent.action.DEVICE_STORAGE_LOW");
    this.mStorageLowIntent.addFlags(67108864);
    this.mStorageOkIntent = new Intent("android.intent.action.DEVICE_STORAGE_OK");
    this.mStorageOkIntent.addFlags(67108864);
    this.mStorageFullIntent = new Intent("android.intent.action.DEVICE_STORAGE_FULL");
    this.mStorageFullIntent.addFlags(67108864);
    this.mStorageNotFullIntent = new Intent("android.intent.action.DEVICE_STORAGE_NOT_FULL");
    this.mStorageNotFullIntent.addFlags(67108864);
  }
  
  private void cancelFullNotification()
  {
    getContext().removeStickyBroadcastAsUser(this.mStorageFullIntent, UserHandle.ALL);
    getContext().sendBroadcastAsUser(this.mStorageNotFullIntent, UserHandle.ALL);
  }
  
  private void cancelNotification()
  {
    Context localContext = getContext();
    ((NotificationManager)localContext.getSystemService("notification")).cancelAsUser(null, 1, UserHandle.ALL);
    localContext.removeStickyBroadcastAsUser(this.mStorageLowIntent, UserHandle.ALL);
    localContext.sendBroadcastAsUser(this.mStorageOkIntent, UserHandle.ALL);
  }
  
  private void clearCache()
  {
    if (this.mClearCacheObserver == null) {
      this.mClearCacheObserver = new CachePackageDataObserver(null);
    }
    this.mClearingCache = true;
    try
    {
      IPackageManager.Stub.asInterface(ServiceManager.getService("package")).freeStorageAndNotify(null, this.mMemCacheTrimToThreshold, this.mClearCacheObserver);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Slog.w("DeviceStorageMonitorService", "Failed to get handle for PackageManger Exception: " + localRemoteException);
      this.mClearingCache = false;
      this.mClearSucceeded = false;
    }
  }
  
  private static boolean isBootImageOnDisk()
  {
    String[] arrayOfString = InstructionSets.getAllDexCodeInstructionSets();
    int j = arrayOfString.length;
    int i = 0;
    while (i < j)
    {
      if (!VMRuntime.isBootClassPathOnDisk(arrayOfString[i])) {
        return false;
      }
      i += 1;
    }
    return true;
  }
  
  private void restatDataDir()
  {
    try
    {
      this.mDataFileStats.restat(DATA_PATH.getAbsolutePath());
      this.mFreeMem = (this.mDataFileStats.getAvailableBlocks() * this.mDataFileStats.getBlockSize());
      String str = SystemProperties.get("debug.freemem");
      if (!"".equals(str)) {
        this.mFreeMem = Long.parseLong(str);
      }
      long l1 = Settings.Global.getLong(this.mResolver, "sys_free_storage_log_interval", 720L);
      long l2 = SystemClock.elapsedRealtime();
      if ((this.mLastReportedFreeMemTime == 0L) || (l2 - this.mLastReportedFreeMemTime >= l1 * 60L * 1000L))
      {
        this.mLastReportedFreeMemTime = l2;
        l1 = -1L;
        l2 = -1L;
      }
      for (;;)
      {
        try
        {
          this.mSystemFileStats.restat(SYSTEM_PATH.getAbsolutePath());
          l3 = this.mSystemFileStats.getAvailableBlocks();
          i = this.mSystemFileStats.getBlockSize();
          l1 = l3 * i;
        }
        catch (IllegalArgumentException localIllegalArgumentException2)
        {
          long l3;
          int i;
          continue;
        }
        try
        {
          this.mCacheFileStats.restat(CACHE_PATH.getAbsolutePath());
          l3 = this.mCacheFileStats.getAvailableBlocks();
          i = this.mCacheFileStats.getBlockSize();
          l2 = l3 * i;
        }
        catch (IllegalArgumentException localIllegalArgumentException1) {}
      }
      EventLog.writeEvent(2746, new Object[] { Long.valueOf(this.mFreeMem), Long.valueOf(l1), Long.valueOf(l2) });
      l1 = Settings.Global.getLong(this.mResolver, "disk_free_change_reporting_threshold", 2097152L);
      l2 = this.mFreeMem - this.mLastReportedFreeMem;
      if ((l2 > l1) || (l2 < -l1))
      {
        this.mLastReportedFreeMem = this.mFreeMem;
        EventLog.writeEvent(2744, this.mFreeMem);
      }
      return;
    }
    catch (IllegalArgumentException localIllegalArgumentException3)
    {
      for (;;) {}
    }
  }
  
  private void sendFullNotification()
  {
    getContext().sendStickyBroadcastAsUser(this.mStorageFullIntent, UserHandle.ALL);
  }
  
  private void sendNotification()
  {
    Context localContext = getContext();
    EventLog.writeEvent(2745, this.mFreeMem);
    Object localObject2 = new Intent("android.os.storage.action.MANAGE_STORAGE");
    ((Intent)localObject2).putExtra("memory", this.mFreeMem);
    ((Intent)localObject2).addFlags(268435456);
    NotificationManager localNotificationManager = (NotificationManager)localContext.getSystemService("notification");
    Object localObject1 = localContext.getText(17040257);
    if (this.mIsBootImageOnDisk) {}
    for (int i = 17040258;; i = 17040259)
    {
      CharSequence localCharSequence = localContext.getText(i);
      localObject2 = PendingIntent.getActivityAsUser(localContext, 0, (Intent)localObject2, 0, null, UserHandle.CURRENT);
      localObject1 = new Notification.Builder(localContext).setSmallIcon(17303255).setTicker((CharSequence)localObject1).setColor(localContext.getColor(17170523)).setContentTitle((CharSequence)localObject1).setContentText(localCharSequence).setContentIntent((PendingIntent)localObject2).setStyle(new Notification.BigTextStyle().bigText(localCharSequence)).setVisibility(1).setCategory("sys").build();
      ((Notification)localObject1).flags |= 0x20;
      localNotificationManager.notifyAsUser(null, 1, (Notification)localObject1, UserHandle.ALL);
      localContext.sendStickyBroadcastAsUser(this.mStorageLowIntent, UserHandle.ALL);
      return;
    }
  }
  
  void checkMemory(boolean paramBoolean)
  {
    if (this.mClearingCache) {
      if (System.currentTimeMillis() - this.mThreadStartTime > 600000L) {
        Slog.w("DeviceStorageMonitorService", "Thread that clears cache file seems to run for ever");
      }
    }
    for (;;)
    {
      postCheckMemoryMsg(true, 60000L);
      return;
      restatDataDir();
      if (this.mFreeMem < this.mMemLowThreshold) {
        if (paramBoolean)
        {
          if ((this.mFreeMem < this.mMemCacheStartTrimThreshold) && (this.mFreeMemAfterLastCacheClear - this.mFreeMem >= (this.mMemLowThreshold - this.mMemCacheStartTrimThreshold) / 4L))
          {
            this.mThreadStartTime = System.currentTimeMillis();
            this.mClearSucceeded = false;
            clearCache();
          }
          label114:
          if ((!this.mLowMemFlag) && (!this.mIsBootImageOnDisk)) {
            break label231;
          }
        }
      }
      for (;;)
      {
        if (this.mFreeMem >= this.mMemFullThreshold) {
          break label263;
        }
        if (this.mMemFullFlag) {
          break;
        }
        sendFullNotification();
        this.mMemFullFlag = true;
        break;
        this.mFreeMemAfterLastCacheClear = this.mFreeMem;
        if (this.mLowMemFlag) {
          break label114;
        }
        Slog.i("DeviceStorageMonitorService", "Running low on memory. Sending notification");
        sendNotification();
        this.mLowMemFlag = true;
        break label114;
        this.mFreeMemAfterLastCacheClear = this.mFreeMem;
        if (!this.mLowMemFlag) {
          break label114;
        }
        Slog.i("DeviceStorageMonitorService", "Memory available. Cancelling notification");
        cancelNotification();
        this.mLowMemFlag = false;
        break label114;
        label231:
        if (this.mFreeMem < 262144000L)
        {
          Slog.i("DeviceStorageMonitorService", "No boot image on disk due to lack of space. Sending notification");
          sendNotification();
          this.mLowMemFlag = true;
        }
      }
      label263:
      if (this.mMemFullFlag)
      {
        cancelFullNotification();
        this.mMemFullFlag = false;
      }
    }
  }
  
  void dumpImpl(PrintWriter paramPrintWriter)
  {
    Context localContext = getContext();
    paramPrintWriter.println("Current DeviceStorageMonitor state:");
    paramPrintWriter.print("  mFreeMem=");
    paramPrintWriter.print(Formatter.formatFileSize(localContext, this.mFreeMem));
    paramPrintWriter.print(" mTotalMemory=");
    paramPrintWriter.println(Formatter.formatFileSize(localContext, this.mTotalMemory));
    paramPrintWriter.print("  mFreeMemAfterLastCacheClear=");
    paramPrintWriter.println(Formatter.formatFileSize(localContext, this.mFreeMemAfterLastCacheClear));
    paramPrintWriter.print("  mLastReportedFreeMem=");
    paramPrintWriter.print(Formatter.formatFileSize(localContext, this.mLastReportedFreeMem));
    paramPrintWriter.print(" mLastReportedFreeMemTime=");
    TimeUtils.formatDuration(this.mLastReportedFreeMemTime, SystemClock.elapsedRealtime(), paramPrintWriter);
    paramPrintWriter.println();
    paramPrintWriter.print("  mLowMemFlag=");
    paramPrintWriter.print(this.mLowMemFlag);
    paramPrintWriter.print(" mMemFullFlag=");
    paramPrintWriter.println(this.mMemFullFlag);
    paramPrintWriter.print(" mIsBootImageOnDisk=");
    paramPrintWriter.print(this.mIsBootImageOnDisk);
    paramPrintWriter.print("  mClearSucceeded=");
    paramPrintWriter.print(this.mClearSucceeded);
    paramPrintWriter.print(" mClearingCache=");
    paramPrintWriter.println(this.mClearingCache);
    paramPrintWriter.print("  mMemLowThreshold=");
    paramPrintWriter.print(Formatter.formatFileSize(localContext, this.mMemLowThreshold));
    paramPrintWriter.print(" mMemFullThreshold=");
    paramPrintWriter.println(Formatter.formatFileSize(localContext, this.mMemFullThreshold));
    paramPrintWriter.print("  mMemCacheStartTrimThreshold=");
    paramPrintWriter.print(Formatter.formatFileSize(localContext, this.mMemCacheStartTrimThreshold));
    paramPrintWriter.print(" mMemCacheTrimToThreshold=");
    paramPrintWriter.println(Formatter.formatFileSize(localContext, this.mMemCacheTrimToThreshold));
  }
  
  public void onStart()
  {
    StorageManager localStorageManager = StorageManager.from(getContext());
    this.mMemLowThreshold = localStorageManager.getStorageLowBytes(DATA_PATH);
    this.mMemFullThreshold = localStorageManager.getStorageFullBytes(DATA_PATH);
    this.mMemCacheStartTrimThreshold = ((this.mMemLowThreshold * 3L + this.mMemFullThreshold) / 4L);
    this.mMemCacheTrimToThreshold = (this.mMemLowThreshold + (this.mMemLowThreshold - this.mMemCacheStartTrimThreshold) * 2L);
    this.mFreeMemAfterLastCacheClear = this.mTotalMemory;
    checkMemory(true);
    this.mCacheFileDeletedObserver = new CacheFileDeletedObserver();
    this.mCacheFileDeletedObserver.startWatching();
    publishBinderService("devicestoragemonitor", this.mRemoteService);
    publishLocalService(DeviceStorageMonitorInternal.class, this.mLocalService);
  }
  
  void postCheckMemoryMsg(boolean paramBoolean, long paramLong)
  {
    this.mHandler.removeMessages(1);
    Handler localHandler1 = this.mHandler;
    Handler localHandler2 = this.mHandler;
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      localHandler1.sendMessageDelayed(localHandler2.obtainMessage(1, i, 0), paramLong);
      return;
    }
  }
  
  private static class CacheFileDeletedObserver
    extends FileObserver
  {
    public CacheFileDeletedObserver()
    {
      super(512);
    }
    
    public void onEvent(int paramInt, String paramString)
    {
      EventLogTags.writeCacheFileDeleted(paramString);
    }
  }
  
  private class CachePackageDataObserver
    extends IPackageDataObserver.Stub
  {
    private CachePackageDataObserver() {}
    
    public void onRemoveCompleted(String paramString, boolean paramBoolean)
    {
      DeviceStorageMonitorService.this.mClearSucceeded = paramBoolean;
      DeviceStorageMonitorService.this.mClearingCache = false;
      DeviceStorageMonitorService.this.postCheckMemoryMsg(false, 0L);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/storage/DeviceStorageMonitorService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */