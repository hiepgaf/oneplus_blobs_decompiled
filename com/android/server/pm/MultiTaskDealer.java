package com.android.server.pm;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class MultiTaskDealer
{
  private static final boolean DEBUG_TASK = false;
  public static final String PACKAGEMANAGER_SCANER = "packagescan";
  public static final String TAG = "MultiTaskDealer";
  private static HashMap<String, WeakReference<MultiTaskDealer>> map = new HashMap();
  private ThreadPoolExecutor mExecutor;
  private ReentrantLock mLock = new ReentrantLock();
  private boolean mNeedNotifyEnd = false;
  private Object mObjWaitAll = new Object();
  private int mTaskCount = 0;
  
  public MultiTaskDealer(final String paramString, int paramInt)
  {
    paramString = new ThreadFactory()
    {
      private final AtomicInteger mCount = new AtomicInteger(1);
      
      public Thread newThread(Runnable paramAnonymousRunnable)
      {
        return new Thread(paramAnonymousRunnable, paramString + "-" + this.mCount.getAndIncrement());
      }
    };
    this.mExecutor = new ThreadPoolExecutor(paramInt, paramInt, 5L, TimeUnit.SECONDS, new LinkedBlockingQueue(), paramString)
    {
      protected void afterExecute(Runnable paramAnonymousRunnable, Throwable paramAnonymousThrowable)
      {
        if (paramAnonymousThrowable != null) {
          paramAnonymousThrowable.printStackTrace();
        }
        MultiTaskDealer.-wrap0(MultiTaskDealer.this, paramAnonymousRunnable);
        super.afterExecute(paramAnonymousRunnable, paramAnonymousThrowable);
      }
      
      protected void beforeExecute(Thread paramAnonymousThread, Runnable paramAnonymousRunnable)
      {
        super.beforeExecute(paramAnonymousThread, paramAnonymousRunnable);
      }
    };
  }
  
  private void TaskCompleteNotify(Runnable arg1)
  {
    synchronized (this.mObjWaitAll)
    {
      this.mTaskCount -= 1;
      if ((this.mTaskCount <= 0) && (this.mNeedNotifyEnd)) {
        this.mObjWaitAll.notify();
      }
      return;
    }
  }
  
  public static MultiTaskDealer getDealer(String paramString)
  {
    Object localObject = null;
    WeakReference localWeakReference = (WeakReference)map.get(paramString);
    paramString = (String)localObject;
    if (localWeakReference != null) {
      paramString = (MultiTaskDealer)localWeakReference.get();
    }
    return paramString;
  }
  
  public static MultiTaskDealer startDealer(String paramString, int paramInt)
  {
    Object localObject2 = getDealer(paramString);
    Object localObject1 = localObject2;
    if (localObject2 == null)
    {
      localObject1 = new MultiTaskDealer(paramString, paramInt);
      localObject2 = new WeakReference(localObject1);
      map.put(paramString, localObject2);
    }
    return (MultiTaskDealer)localObject1;
  }
  
  public void addTask(Runnable paramRunnable)
  {
    synchronized (this.mObjWaitAll)
    {
      this.mTaskCount += 1;
      this.mExecutor.execute(paramRunnable);
      return;
    }
  }
  
  public void endLock()
  {
    this.mLock.unlock();
  }
  
  public void startLock()
  {
    this.mLock.lock();
  }
  
  public void waitAll()
  {
    synchronized (this.mObjWaitAll)
    {
      if (this.mTaskCount > 0) {
        this.mNeedNotifyEnd = true;
      }
    }
    try
    {
      this.mObjWaitAll.wait();
      this.mNeedNotifyEnd = false;
      return;
      localObject2 = finally;
      throw ((Throwable)localObject2);
    }
    catch (Exception localException)
    {
      for (;;) {}
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/pm/MultiTaskDealer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */