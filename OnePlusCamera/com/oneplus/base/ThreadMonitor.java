package com.oneplus.base;

import android.os.Handler;
import android.os.Process;
import android.os.SystemClock;
import java.util.LinkedList;

public final class ThreadMonitor
{
  private static final String TAG = "ThreadMonitor";
  private static final long THREAD_CHECK_INTERVAL = 3000L;
  private static final LinkedList<ThreadInfo> m_AllThreadInfos = new LinkedList();
  private static final ThreadLocal<ThreadInfo> m_CurrentThreadInfo = new ThreadLocal();
  private static volatile boolean m_IsPrepared;
  private static volatile Thread m_MonitorThread;
  private static final Runnable m_ResponseCallback = new Runnable()
  {
    public void run()
    {
      ThreadMonitor.ThreadInfo localThreadInfo = (ThreadMonitor.ThreadInfo)ThreadMonitor.-get0().get();
      if (localThreadInfo == null) {
        return;
      }
      try
      {
        localThreadInfo.pendingResponseCount -= 1;
        localThreadInfo.lastResponseTime = SystemClock.elapsedRealtime();
        if ((localThreadInfo.notResponding) && (localThreadInfo.pendingResponseCount <= 0))
        {
          localThreadInfo.notResponding = false;
          Log.w("ThreadMonitor", "Get response from thread '" + localThreadInfo.thread.getName() + "' (" + localThreadInfo.threadId + ")");
        }
        return;
      }
      finally
      {
        localObject = finally;
        throw ((Throwable)localObject);
      }
    }
  };
  
  public static void prepare()
  {
    try
    {
      boolean bool = m_IsPrepared;
      if (bool) {
        return;
      }
      Log.w("ThreadMonitor", "prepare()");
      m_MonitorThread = new Thread(new Runnable()
      {
        public void run() {}
      });
      m_MonitorThread.setName("Thread monitor");
      m_MonitorThread.start();
      m_IsPrepared = true;
      return;
    }
    finally {}
  }
  
  private static void printThreadBlockedLogs(ThreadInfo paramThreadInfo)
  {
    long l1 = SystemClock.elapsedRealtime();
    long l2 = paramThreadInfo.lastResponseTime;
    Log.w("ThreadMonitor", String.format("Thread '%s' (%d) is not responding, last response time is %.2f seconds ago. Stack trace :", new Object[] { paramThreadInfo.thread.getName(), Integer.valueOf(paramThreadInfo.threadId), Double.valueOf((l1 - l2) / 1000.0D) }));
    paramThreadInfo = paramThreadInfo.thread.getStackTrace();
    int i = 0;
    while (i < paramThreadInfo.length)
    {
      Log.w("ThreadMonitor", "  -> " + Log.formatStackTraceElement(paramThreadInfo[i]));
      i += 1;
    }
  }
  
  public static void release()
  {
    try
    {
      boolean bool = m_IsPrepared;
      if (!bool) {
        return;
      }
      Log.w("ThreadMonitor", "release()");
      if (m_MonitorThread != null)
      {
        m_MonitorThread.interrupt();
        m_MonitorThread = null;
      }
      m_AllThreadInfos.clear();
      m_IsPrepared = false;
      return;
    }
    finally {}
  }
  
  public static Handle startMonitorCurrentThread()
  {
    try
    {
      boolean bool = m_IsPrepared;
      if (!bool) {
        return null;
      }
      ThreadMonitorHandle localThreadMonitorHandle = new ThreadMonitorHandle();
      ThreadInfo localThreadInfo = (ThreadInfo)m_CurrentThreadInfo.get();
      if (localThreadInfo != null)
      {
        localThreadInfo.activeHandles.add(localThreadMonitorHandle);
        return localThreadMonitorHandle;
      }
      localThreadInfo = new ThreadInfo();
      localThreadInfo.activeHandles.add(localThreadMonitorHandle);
      m_AllThreadInfos.add(localThreadInfo);
      Log.w("ThreadMonitor", "Start monitor '" + localThreadInfo.thread.getName() + "' (" + localThreadInfo.threadId + ")");
      return localThreadMonitorHandle;
    }
    finally {}
  }
  
  private static void stopMonitorCurrentThread(ThreadMonitorHandle paramThreadMonitorHandle)
  {
    try
    {
      boolean bool = m_IsPrepared;
      if (!bool) {
        return;
      }
      ThreadInfo localThreadInfo = (ThreadInfo)m_CurrentThreadInfo.get();
      if (localThreadInfo == null) {
        return;
      }
      localThreadInfo.activeHandles.remove(paramThreadMonitorHandle);
      int i = localThreadInfo.activeHandles.size();
      if (i > 0) {
        return;
      }
      m_AllThreadInfos.remove(localThreadInfo);
      m_CurrentThreadInfo.set(null);
      Log.w("ThreadMonitor", "Stop monitor '" + localThreadInfo.thread.getName() + "' (" + localThreadInfo.threadId + ")");
      return;
    }
    finally {}
  }
  
  /* Error */
  private static void threadMonitorProc()
  {
    // Byte code:
    //   0: ldc 18
    //   2: ldc -51
    //   4: invokestatic 75	com/oneplus/base/Log:w	(Ljava/lang/String;Ljava/lang/String;)V
    //   7: ldc 2
    //   9: monitorenter
    //   10: getstatic 57	com/oneplus/base/ThreadMonitor:m_AllThreadInfos	Ljava/util/LinkedList;
    //   13: invokevirtual 195	java/util/LinkedList:size	()I
    //   16: iconst_1
    //   17: isub
    //   18: istore_0
    //   19: iload_0
    //   20: iflt +87 -> 107
    //   23: getstatic 57	com/oneplus/base/ThreadMonitor:m_AllThreadInfos	Ljava/util/LinkedList;
    //   26: iload_0
    //   27: invokevirtual 208	java/util/LinkedList:get	(I)Ljava/lang/Object;
    //   30: checkcast 10	com/oneplus/base/ThreadMonitor$ThreadInfo
    //   33: astore_1
    //   34: aload_1
    //   35: monitorenter
    //   36: aload_1
    //   37: getfield 211	com/oneplus/base/ThreadMonitor$ThreadInfo:pendingResponseCount	I
    //   40: ifle +21 -> 61
    //   43: aload_1
    //   44: iconst_1
    //   45: putfield 214	com/oneplus/base/ThreadMonitor$ThreadInfo:notResponding	Z
    //   48: aload_1
    //   49: invokestatic 216	com/oneplus/base/ThreadMonitor:printThreadBlockedLogs	(Lcom/oneplus/base/ThreadMonitor$ThreadInfo;)V
    //   52: aload_1
    //   53: monitorexit
    //   54: iload_0
    //   55: iconst_1
    //   56: isub
    //   57: istore_0
    //   58: goto -39 -> 19
    //   61: aload_1
    //   62: getfield 220	com/oneplus/base/ThreadMonitor$ThreadInfo:handler	Landroid/os/Handler;
    //   65: getstatic 63	com/oneplus/base/ThreadMonitor:m_ResponseCallback	Ljava/lang/Runnable;
    //   68: invokevirtual 226	android/os/Handler:post	(Ljava/lang/Runnable;)Z
    //   71: pop
    //   72: aload_1
    //   73: aload_1
    //   74: getfield 211	com/oneplus/base/ThreadMonitor$ThreadInfo:pendingResponseCount	I
    //   77: iconst_1
    //   78: iadd
    //   79: putfield 211	com/oneplus/base/ThreadMonitor$ThreadInfo:pendingResponseCount	I
    //   82: aload_1
    //   83: monitorexit
    //   84: goto -30 -> 54
    //   87: astore_1
    //   88: ldc 2
    //   90: monitorexit
    //   91: aload_1
    //   92: athrow
    //   93: astore_1
    //   94: ldc 18
    //   96: ldc -28
    //   98: invokestatic 75	com/oneplus/base/Log:w	(Ljava/lang/String;Ljava/lang/String;)V
    //   101: return
    //   102: astore_2
    //   103: aload_1
    //   104: monitorexit
    //   105: aload_2
    //   106: athrow
    //   107: ldc 2
    //   109: monitorexit
    //   110: ldc2_w 21
    //   113: invokestatic 232	java/lang/Thread:sleep	(J)V
    //   116: goto -109 -> 7
    //   119: astore_1
    //   120: ldc 18
    //   122: ldc -28
    //   124: invokestatic 75	com/oneplus/base/Log:w	(Ljava/lang/String;Ljava/lang/String;)V
    //   127: aload_1
    //   128: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   18	40	0	i	int
    //   87	5	1	localObject1	Object
    //   93	11	1	localInterruptedException	InterruptedException
    //   119	9	1	localObject2	Object
    //   102	4	2	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   10	19	87	finally
    //   23	36	87	finally
    //   52	54	87	finally
    //   82	84	87	finally
    //   103	107	87	finally
    //   7	10	93	java/lang/InterruptedException
    //   88	93	93	java/lang/InterruptedException
    //   107	116	93	java/lang/InterruptedException
    //   36	52	102	finally
    //   61	82	102	finally
    //   7	10	119	finally
    //   88	93	119	finally
    //   107	116	119	finally
  }
  
  private static final class ThreadInfo
  {
    public final LinkedList<ThreadMonitor.ThreadMonitorHandle> activeHandles = new LinkedList();
    public final Handler handler = new Handler();
    public volatile long lastResponseTime = SystemClock.elapsedRealtime();
    public volatile boolean notResponding;
    public volatile int pendingResponseCount;
    public final Thread thread = Thread.currentThread();
    public final int threadId = Process.myTid();
    
    public ThreadInfo()
    {
      ThreadMonitor.-get0().set(this);
    }
  }
  
  private static final class ThreadMonitorHandle
    extends Handle
  {
    protected ThreadMonitorHandle()
    {
      super();
    }
    
    protected void onClose(int paramInt)
    {
      ThreadMonitor.-wrap0(this);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/base/ThreadMonitor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */