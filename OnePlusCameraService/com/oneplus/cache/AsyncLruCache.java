package com.oneplus.cache;

import android.os.Message;
import android.os.SystemClock;
import com.oneplus.base.Log;
import com.oneplus.base.Ref;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class AsyncLruCache<TKey, TValue>
  extends LruCache<TKey, TValue>
{
  private static final int MSG_ADD = -10000;
  private static final int MSG_CLEAR = -10004;
  private static final int MSG_GET = -10001;
  private static final int MSG_REMOVE = -10002;
  private static final int MSG_REMOVE_MULTIPLE = -10003;
  private final Runnable m_HandleWorkerThreadMessageRunnable = new Runnable()
  {
    public void run()
    {
      synchronized (AsyncLruCache.-get0(AsyncLruCache.this))
      {
        Message localMessage = (Message)AsyncLruCache.-get0(AsyncLruCache.this).pollFirst();
        if (localMessage != null)
        {
          AsyncLruCache.this.handleWorkerThreadMessage(localMessage);
          localMessage.recycle();
        }
        return;
      }
    }
  };
  private volatile boolean m_IsStatisticEnabled;
  private final Object m_StatisticLock = new Object();
  private volatile long m_TotalGetCount;
  private volatile long m_TotalGetTime;
  private final int m_WorkerThreadCount;
  private volatile ExecutorService m_WorkerThreadExecutor;
  private final Deque<Message> m_WorkerThreadMessageQueue = new LinkedList();
  
  protected AsyncLruCache(long paramLong)
  {
    this(paramLong, 2);
  }
  
  protected AsyncLruCache(long paramLong, int paramInt)
  {
    super(paramLong);
    if (paramInt <= 0) {
      throw new IllegalArgumentException("Invalid thread count : " + paramInt);
    }
    this.m_WorkerThreadCount = paramInt;
  }
  
  private boolean checkWorkerThread()
  {
    if (this.m_WorkerThreadExecutor != null) {
      return true;
    }
    synchronized (this.syncLock)
    {
      if (this.m_WorkerThreadExecutor == null) {
        this.m_WorkerThreadExecutor = Executors.newFixedThreadPool(this.m_WorkerThreadCount);
      }
      return true;
    }
  }
  
  public boolean add(TKey paramTKey, TValue paramTValue)
  {
    if (paramTKey == null)
    {
      Log.e(this.TAG, "add() - No key");
      return false;
    }
    if (paramTValue == null)
    {
      Log.e(this.TAG, "add() - No value");
      return false;
    }
    if (!checkWorkerThread()) {
      return false;
    }
    int i = 0;
    synchronized (this.m_WorkerThreadMessageQueue)
    {
      Iterator localIterator = this.m_WorkerThreadMessageQueue.iterator();
      while (localIterator.hasNext())
      {
        Message localMessage = (Message)localIterator.next();
        if ((localMessage.what == 55536) && (paramTKey.equals(((Object[])localMessage.obj)[0])))
        {
          i += 1;
          localIterator.remove();
        }
      }
    }
    if (i > 0) {
      Log.v(this.TAG, "add() - Cancel ", Integer.valueOf(i), " pending value adding");
    }
    sendMessageToWorkerThread(Message.obtain(null, 55536, new Object[] { paramTKey, paramTValue }));
    return true;
  }
  
  public void clear()
  {
    if (!checkWorkerThread()) {
      return;
    }
    sendMessageToWorkerThread(Message.obtain(null, 55532));
  }
  
  public void close()
  {
    synchronized (this.syncLock)
    {
      if (this.m_WorkerThreadExecutor != null)
      {
        this.m_WorkerThreadExecutor.shutdown();
        this.m_WorkerThreadExecutor = null;
      }
      super.close();
      return;
    }
  }
  
  public void disableStatistic()
  {
    this.m_IsStatisticEnabled = false;
  }
  
  public void enableStatistic()
  {
    synchronized (this.m_StatisticLock)
    {
      if (!this.m_IsStatisticEnabled)
      {
        this.m_IsStatisticEnabled = true;
        this.m_TotalGetCount = 0L;
        this.m_TotalGetTime = 0L;
      }
      return;
    }
  }
  
  protected void executeInWorkerThread(Runnable paramRunnable)
  {
    if (checkWorkerThread()) {
      this.m_WorkerThreadExecutor.equals(paramRunnable);
    }
  }
  
  /* Error */
  public TValue get(TKey paramTKey, TValue paramTValue, long paramLong)
  {
    // Byte code:
    //   0: aload_1
    //   1: ifnonnull +14 -> 15
    //   4: aload_0
    //   5: getfield 102	com/oneplus/cache/AsyncLruCache:TAG	Ljava/lang/String;
    //   8: ldc -58
    //   10: invokestatic 110	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   13: aload_2
    //   14: areturn
    //   15: aload_0
    //   16: invokespecial 114	com/oneplus/cache/AsyncLruCache:checkWorkerThread	()Z
    //   19: ifne +5 -> 24
    //   22: aload_2
    //   23: areturn
    //   24: new 200	com/oneplus/base/SimpleRef
    //   27: dup
    //   28: iconst_0
    //   29: invokestatic 205	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   32: invokespecial 208	com/oneplus/base/SimpleRef:<init>	(Ljava/lang/Object;)V
    //   35: astore 5
    //   37: lload_3
    //   38: lconst_0
    //   39: lcmp
    //   40: ifne +5 -> 45
    //   43: aload_2
    //   44: areturn
    //   45: aload 5
    //   47: monitorenter
    //   48: iconst_4
    //   49: anewarray 52	java/lang/Object
    //   52: astore 6
    //   54: aload 6
    //   56: iconst_0
    //   57: aload_1
    //   58: aastore
    //   59: aload 6
    //   61: iconst_1
    //   62: aload_2
    //   63: aastore
    //   64: aload 6
    //   66: iconst_2
    //   67: aload 5
    //   69: aastore
    //   70: aload 6
    //   72: iconst_3
    //   73: aload_2
    //   74: aastore
    //   75: aload_0
    //   76: aconst_null
    //   77: sipush 55535
    //   80: aload 6
    //   82: invokestatic 164	android/os/Message:obtain	(Landroid/os/Handler;ILjava/lang/Object;)Landroid/os/Message;
    //   85: invokevirtual 168	com/oneplus/cache/AsyncLruCache:sendMessageToWorkerThread	(Landroid/os/Message;)Z
    //   88: pop
    //   89: lload_3
    //   90: lconst_0
    //   91: lcmp
    //   92: ifge +29 -> 121
    //   95: aload 5
    //   97: invokevirtual 211	java/lang/Object:wait	()V
    //   100: aload 5
    //   102: iconst_1
    //   103: invokestatic 205	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   106: invokeinterface 216 2 0
    //   111: aload 6
    //   113: iconst_3
    //   114: aaload
    //   115: astore_1
    //   116: aload 5
    //   118: monitorexit
    //   119: aload_1
    //   120: areturn
    //   121: aload 5
    //   123: lload_3
    //   124: invokevirtual 218	java/lang/Object:wait	(J)V
    //   127: goto -27 -> 100
    //   130: astore_1
    //   131: aload 5
    //   133: iconst_1
    //   134: invokestatic 205	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   137: invokeinterface 216 2 0
    //   142: aload 5
    //   144: monitorexit
    //   145: aload_2
    //   146: areturn
    //   147: astore_1
    //   148: aload 5
    //   150: monitorexit
    //   151: aload_1
    //   152: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	153	0	this	AsyncLruCache
    //   0	153	1	paramTKey	TKey
    //   0	153	2	paramTValue	TValue
    //   0	153	3	paramLong	long
    //   35	114	5	localSimpleRef	com.oneplus.base.SimpleRef
    //   52	60	6	arrayOfObject	Object[]
    // Exception table:
    //   from	to	target	type
    //   95	100	130	java/lang/InterruptedException
    //   121	127	130	java/lang/InterruptedException
    //   48	54	147	finally
    //   75	89	147	finally
    //   95	100	147	finally
    //   100	111	147	finally
    //   121	127	147	finally
    //   131	142	147	finally
  }
  
  protected void handleWorkerThreadMessage(Message paramMessage)
  {
    switch (paramMessage.what)
    {
    default: 
    case -10000: 
    case -10004: 
    case -10001: 
      do
      {
        return;
        paramMessage = (Object[])paramMessage.obj;
        super.add(paramMessage[0], paramMessage[1]);
        return;
        super.clear();
        return;
        ??? = (Object[])paramMessage.obj;
        paramMessage = (Ref)???[2];
      } while (((Boolean)paramMessage.get()).booleanValue());
      long l1;
      if (this.m_IsStatisticEnabled) {
        l1 = SystemClock.elapsedRealtime();
      }
      for (;;)
      {
        ???[3] = super.get(???[0], ???[1], -1L);
        long l2;
        double d;
        if ((this.m_IsStatisticEnabled) && (l1 > 0L))
        {
          l2 = SystemClock.elapsedRealtime();
          d = 0.0D;
        }
        synchronized (this.m_StatisticLock)
        {
          this.m_TotalGetCount += 1L;
          this.m_TotalGetTime += l2 - l1;
          if (this.m_TotalGetCount % 16L == 0L)
          {
            d = this.m_TotalGetTime / this.m_TotalGetCount;
            this.m_TotalGetTime = 0L;
            this.m_TotalGetCount = 0L;
          }
          if (d > 0.0D) {
            Log.d(this.TAG, "[Statistic] Average get time : " + String.format(Locale.US, "%.2f ms", new Object[] { Double.valueOf(d) }));
          }
          try
          {
            paramMessage.notifyAll();
            return;
          }
          finally {}
          l1 = 0L;
        }
      }
    case -10002: 
      super.remove(paramMessage.obj);
      return;
    }
    super.remove((Cache.RemovingPredication)paramMessage.obj);
  }
  
  public void remove(Cache.RemovingPredication<TKey> paramRemovingPredication)
  {
    if (paramRemovingPredication == null) {
      return;
    }
    if (!checkWorkerThread()) {
      return;
    }
    sendMessageToWorkerThread(Message.obtain(null, 55533, paramRemovingPredication));
  }
  
  public boolean remove(TKey paramTKey)
  {
    if (paramTKey == null) {
      return false;
    }
    if (!checkWorkerThread()) {
      return false;
    }
    int i = 0;
    synchronized (this.m_WorkerThreadMessageQueue)
    {
      Iterator localIterator = this.m_WorkerThreadMessageQueue.iterator();
      while (localIterator.hasNext())
      {
        Message localMessage = (Message)localIterator.next();
        if ((localMessage.what == 55536) && (paramTKey.equals(((Object[])localMessage.obj)[0])))
        {
          i += 1;
          localIterator.remove();
        }
      }
    }
    if (i > 0) {
      Log.v(this.TAG, "remove() - Cancel ", Integer.valueOf(i), " pending value adding");
    }
    sendMessageToWorkerThread(Message.obtain(null, 55534, paramTKey));
    return true;
  }
  
  protected void removeWorkerThreadMessages(int paramInt)
  {
    synchronized (this.m_WorkerThreadMessageQueue)
    {
      Iterator localIterator = this.m_WorkerThreadMessageQueue.iterator();
      while (localIterator.hasNext()) {
        if (((Message)localIterator.next()).what == paramInt) {
          localIterator.remove();
        }
      }
    }
  }
  
  protected boolean sendMessageToWorkerThread(Message paramMessage)
  {
    if ((paramMessage != null) && (checkWorkerThread())) {
      synchronized (this.m_WorkerThreadMessageQueue)
      {
        this.m_WorkerThreadMessageQueue.add(paramMessage);
        this.m_WorkerThreadExecutor.execute(this.m_HandleWorkerThreadMessageRunnable);
        return true;
      }
    }
    return false;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/cache/AsyncLruCache.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */