package com.android.server.devicepolicy;

import android.app.admin.SecurityLog;
import android.app.admin.SecurityLog.SecurityEvent;
import android.os.Process;
import android.util.Log;
import com.android.internal.annotations.GuardedBy;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class SecurityLogMonitor
  implements Runnable
{
  private static final int BUFFER_ENTRIES_MAXIMUM_LEVEL = 10240;
  private static final int BUFFER_ENTRIES_NOTIFICATION_LEVEL = 1024;
  private static final boolean DEBUG = false;
  private static final long POLLING_INTERVAL_MILLISECONDS = TimeUnit.MINUTES.toMillis(1L);
  private static final long RATE_LIMIT_INTERVAL_MILLISECONDS = TimeUnit.HOURS.toMillis(2L);
  private static final String TAG = "SecurityLogMonitor";
  @GuardedBy("mLock")
  private boolean mAllowedToRetrieve = false;
  private final Lock mLock = new ReentrantLock();
  @GuardedBy("mLock")
  private Thread mMonitorThread = null;
  @GuardedBy("mLock")
  private long mNextAllowedRetrivalTimeMillis = -1L;
  @GuardedBy("mLock")
  private ArrayList<SecurityLog.SecurityEvent> mPendingLogs = new ArrayList();
  private final DevicePolicyManagerService mService;
  
  SecurityLogMonitor(DevicePolicyManagerService paramDevicePolicyManagerService)
  {
    this.mService = paramDevicePolicyManagerService;
  }
  
  /* Error */
  private void notifyDeviceOwnerIfNeeded()
    throws InterruptedException
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore_3
    //   2: aload_0
    //   3: getfield 65	com/android/server/devicepolicy/SecurityLogMonitor:mLock	Ljava/util/concurrent/locks/Lock;
    //   6: invokeinterface 88 1 0
    //   11: aload_0
    //   12: getfield 72	com/android/server/devicepolicy/SecurityLogMonitor:mPendingLogs	Ljava/util/ArrayList;
    //   15: invokevirtual 92	java/util/ArrayList:size	()I
    //   18: istore_1
    //   19: iload_1
    //   20: sipush 1024
    //   23: if_icmplt +43 -> 66
    //   26: iconst_1
    //   27: istore_2
    //   28: aload_0
    //   29: getfield 74	com/android/server/devicepolicy/SecurityLogMonitor:mAllowedToRetrieve	Z
    //   32: ifne +77 -> 109
    //   35: iload_2
    //   36: istore_3
    //   37: aload_0
    //   38: iload_2
    //   39: putfield 74	com/android/server/devicepolicy/SecurityLogMonitor:mAllowedToRetrieve	Z
    //   42: aload_0
    //   43: getfield 65	com/android/server/devicepolicy/SecurityLogMonitor:mLock	Ljava/util/concurrent/locks/Lock;
    //   46: invokeinterface 95 1 0
    //   51: iload_3
    //   52: ifeq +13 -> 65
    //   55: aload_0
    //   56: getfield 80	com/android/server/devicepolicy/SecurityLogMonitor:mService	Lcom/android/server/devicepolicy/DevicePolicyManagerService;
    //   59: ldc 97
    //   61: aconst_null
    //   62: invokevirtual 103	com/android/server/devicepolicy/DevicePolicyManagerService:sendDeviceOwnerCommand	(Ljava/lang/String;Landroid/os/Bundle;)V
    //   65: return
    //   66: iload_3
    //   67: istore_2
    //   68: iload_1
    //   69: ifle -41 -> 28
    //   72: aload_0
    //   73: getfield 78	com/android/server/devicepolicy/SecurityLogMonitor:mNextAllowedRetrivalTimeMillis	J
    //   76: ldc2_w 75
    //   79: lcmp
    //   80: ifeq +24 -> 104
    //   83: invokestatic 109	java/lang/System:currentTimeMillis	()J
    //   86: lstore 4
    //   88: aload_0
    //   89: getfield 78	com/android/server/devicepolicy/SecurityLogMonitor:mNextAllowedRetrivalTimeMillis	J
    //   92: lstore 6
    //   94: iload_3
    //   95: istore_2
    //   96: lload 4
    //   98: lload 6
    //   100: lcmp
    //   101: iflt -73 -> 28
    //   104: iconst_1
    //   105: istore_2
    //   106: goto -78 -> 28
    //   109: iconst_0
    //   110: istore_3
    //   111: goto -74 -> 37
    //   114: astore 8
    //   116: aload_0
    //   117: getfield 65	com/android/server/devicepolicy/SecurityLogMonitor:mLock	Ljava/util/concurrent/locks/Lock;
    //   120: invokeinterface 95 1 0
    //   125: aload 8
    //   127: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	128	0	this	SecurityLogMonitor
    //   18	51	1	i	int
    //   27	79	2	bool1	boolean
    //   1	110	3	bool2	boolean
    //   86	11	4	l1	long
    //   92	7	6	l2	long
    //   114	12	8	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   11	19	114	finally
    //   28	35	114	finally
    //   37	42	114	finally
    //   72	94	114	finally
  }
  
  List<SecurityLog.SecurityEvent> retrieveLogs()
  {
    this.mLock.lock();
    try
    {
      if (this.mAllowedToRetrieve)
      {
        this.mAllowedToRetrieve = false;
        this.mNextAllowedRetrivalTimeMillis = (System.currentTimeMillis() + RATE_LIMIT_INTERVAL_MILLISECONDS);
        ArrayList localArrayList = this.mPendingLogs;
        this.mPendingLogs = new ArrayList();
        return localArrayList;
      }
      return null;
    }
    finally
    {
      this.mLock.unlock();
    }
  }
  
  public void run()
  {
    Process.setThreadPriority(10);
    ArrayList localArrayList = new ArrayList();
    l2 = -1L;
    while (!Thread.currentThread().isInterrupted())
    {
      l1 = l2;
      try
      {
        Thread.sleep(POLLING_INTERVAL_MILLISECONDS);
        if (l2 < 0L)
        {
          l1 = l2;
          SecurityLog.readEvents(localArrayList);
          l3 = l2;
          l1 = l2;
          if (!localArrayList.isEmpty())
          {
            l1 = l2;
            this.mLock.lockInterruptibly();
          }
        }
      }
      catch (IOException localIOException)
      {
        for (;;)
        {
          try
          {
            this.mPendingLogs.addAll(localArrayList);
            if (this.mPendingLogs.size() > 10240) {
              this.mPendingLogs = new ArrayList(this.mPendingLogs.subList(this.mPendingLogs.size() - 5120, this.mPendingLogs.size()));
            }
            l1 = l2;
            this.mLock.unlock();
            l1 = l2;
            long l3 = ((SecurityLog.SecurityEvent)localArrayList.get(localArrayList.size() - 1)).getTimeNanos();
            l1 = l3;
            localArrayList.clear();
            l1 = l3;
            notifyDeviceOwnerIfNeeded();
            l2 = l3;
            break;
          }
          finally
          {
            l1 = l2;
            this.mLock.unlock();
            l1 = l2;
          }
          localIOException = localIOException;
          Log.e("SecurityLogMonitor", "Failed to read security log", localIOException);
          l2 = l1;
          break;
          l1 = l2;
          SecurityLog.readEventsSince(1L + l2, localArrayList);
        }
      }
      catch (InterruptedException localInterruptedException)
      {
        Log.i("SecurityLogMonitor", "Thread interrupted, exiting.", localInterruptedException);
      }
    }
  }
  
  void start()
  {
    this.mLock.lock();
    try
    {
      if (this.mMonitorThread == null)
      {
        this.mPendingLogs = new ArrayList();
        this.mAllowedToRetrieve = false;
        this.mNextAllowedRetrivalTimeMillis = -1L;
        this.mMonitorThread = new Thread(this);
        this.mMonitorThread.start();
      }
      return;
    }
    finally
    {
      this.mLock.unlock();
    }
  }
  
  /* Error */
  void stop()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 65	com/android/server/devicepolicy/SecurityLogMonitor:mLock	Ljava/util/concurrent/locks/Lock;
    //   4: invokeinterface 115 1 0
    //   9: aload_0
    //   10: getfield 67	com/android/server/devicepolicy/SecurityLogMonitor:mMonitorThread	Ljava/lang/Thread;
    //   13: ifnull +54 -> 67
    //   16: aload_0
    //   17: getfield 67	com/android/server/devicepolicy/SecurityLogMonitor:mMonitorThread	Ljava/lang/Thread;
    //   20: invokevirtual 200	java/lang/Thread:interrupt	()V
    //   23: aload_0
    //   24: getfield 67	com/android/server/devicepolicy/SecurityLogMonitor:mMonitorThread	Ljava/lang/Thread;
    //   27: getstatic 203	java/util/concurrent/TimeUnit:SECONDS	Ljava/util/concurrent/TimeUnit;
    //   30: ldc2_w 204
    //   33: invokevirtual 48	java/util/concurrent/TimeUnit:toMillis	(J)J
    //   36: invokevirtual 208	java/lang/Thread:join	(J)V
    //   39: aload_0
    //   40: new 69	java/util/ArrayList
    //   43: dup
    //   44: invokespecial 70	java/util/ArrayList:<init>	()V
    //   47: putfield 72	com/android/server/devicepolicy/SecurityLogMonitor:mPendingLogs	Ljava/util/ArrayList;
    //   50: aload_0
    //   51: iconst_0
    //   52: putfield 74	com/android/server/devicepolicy/SecurityLogMonitor:mAllowedToRetrieve	Z
    //   55: aload_0
    //   56: ldc2_w 75
    //   59: putfield 78	com/android/server/devicepolicy/SecurityLogMonitor:mNextAllowedRetrivalTimeMillis	J
    //   62: aload_0
    //   63: aconst_null
    //   64: putfield 67	com/android/server/devicepolicy/SecurityLogMonitor:mMonitorThread	Ljava/lang/Thread;
    //   67: aload_0
    //   68: getfield 65	com/android/server/devicepolicy/SecurityLogMonitor:mLock	Ljava/util/concurrent/locks/Lock;
    //   71: invokeinterface 95 1 0
    //   76: return
    //   77: astore_1
    //   78: ldc 21
    //   80: ldc -46
    //   82: aload_1
    //   83: invokestatic 181	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   86: pop
    //   87: goto -48 -> 39
    //   90: astore_1
    //   91: aload_0
    //   92: getfield 65	com/android/server/devicepolicy/SecurityLogMonitor:mLock	Ljava/util/concurrent/locks/Lock;
    //   95: invokeinterface 95 1 0
    //   100: aload_1
    //   101: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	102	0	this	SecurityLogMonitor
    //   77	6	1	localInterruptedException	InterruptedException
    //   90	11	1	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   23	39	77	java/lang/InterruptedException
    //   9	23	90	finally
    //   23	39	90	finally
    //   39	67	90	finally
    //   78	87	90	finally
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/devicepolicy/SecurityLogMonitor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */