package com.oneplus.gallery2.web;

import com.oneplus.base.BaseThread;
import com.oneplus.base.HandlerUtils;
import com.oneplus.base.Log;
import java.util.concurrent.Callable;

public final class WebAccountThread
  extends BaseThread
{
  private static volatile WebAccountThread m_Current;
  private static final Object m_StartLock = new Object();
  
  private WebAccountThread()
  {
    super("Web Account Thread", null, null, false);
  }
  
  public static WebAccountThread current()
  {
    return m_Current;
  }
  
  public static void startSync()
    throws InterruptedException
  {
    synchronized (m_StartLock)
    {
      if (m_Current == null)
      {
        Log.w("WebAccountThread", "startSync()");
        WebAccountThread localWebAccountThread = new WebAccountThread();
        localWebAccountThread.start();
        m_StartLock.wait();
        m_Current = localWebAccountThread;
        return;
      }
      return;
    }
  }
  
  /* Error */
  public <T> T invoke(final Callable<T> paramCallable)
    throws InterruptedException
  {
    // Byte code:
    //   0: aload_1
    //   1: ifnull +52 -> 53
    //   4: aload_0
    //   5: invokevirtual 62	com/oneplus/gallery2/web/WebAccountThread:isDependencyThread	()Z
    //   8: ifne +55 -> 63
    //   11: iconst_2
    //   12: anewarray 16	java/lang/Object
    //   15: astore_2
    //   16: aload_2
    //   17: monitorenter
    //   18: aload_0
    //   19: new 8	com/oneplus/gallery2/web/WebAccountThread$2
    //   22: dup
    //   23: aload_0
    //   24: aload_2
    //   25: aload_1
    //   26: invokespecial 65	com/oneplus/gallery2/web/WebAccountThread$2:<init>	(Lcom/oneplus/gallery2/web/WebAccountThread;[Ljava/lang/Object;Ljava/util/concurrent/Callable;)V
    //   29: invokestatic 71	com/oneplus/base/HandlerUtils:post	(Lcom/oneplus/base/HandlerObject;Ljava/lang/Runnable;)Z
    //   32: ifne +40 -> 72
    //   35: new 73	java/lang/RuntimeException
    //   38: dup
    //   39: ldc 75
    //   41: invokespecial 78	java/lang/RuntimeException:<init>	(Ljava/lang/String;)V
    //   44: athrow
    //   45: astore_1
    //   46: aload_2
    //   47: monitorexit
    //   48: aload_1
    //   49: athrow
    //   50: astore_1
    //   51: aload_1
    //   52: athrow
    //   53: new 80	java/lang/IllegalArgumentException
    //   56: dup
    //   57: ldc 82
    //   59: invokespecial 83	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   62: athrow
    //   63: aload_1
    //   64: invokeinterface 89 1 0
    //   69: astore_1
    //   70: aload_1
    //   71: areturn
    //   72: aload_2
    //   73: invokevirtual 51	java/lang/Object:wait	()V
    //   76: aload_2
    //   77: iconst_1
    //   78: aaload
    //   79: ifnonnull +11 -> 90
    //   82: aload_2
    //   83: iconst_0
    //   84: aaload
    //   85: astore_1
    //   86: aload_2
    //   87: monitorexit
    //   88: aload_1
    //   89: areturn
    //   90: aload_2
    //   91: iconst_1
    //   92: aaload
    //   93: checkcast 58	java/lang/Exception
    //   96: athrow
    //   97: astore_1
    //   98: new 73	java/lang/RuntimeException
    //   101: dup
    //   102: ldc 91
    //   104: aload_1
    //   105: invokespecial 94	java/lang/RuntimeException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   108: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	109	0	this	WebAccountThread
    //   0	109	1	paramCallable	Callable<T>
    // Exception table:
    //   from	to	target	type
    //   18	45	45	finally
    //   46	48	45	finally
    //   72	76	45	finally
    //   86	88	45	finally
    //   90	97	45	finally
    //   4	18	50	java/lang/InterruptedException
    //   4	18	50	java/lang/ClassCastException
    //   48	50	50	java/lang/InterruptedException
    //   48	50	50	java/lang/ClassCastException
    //   63	70	50	java/lang/InterruptedException
    //   63	70	50	java/lang/ClassCastException
    //   4	18	97	java/lang/Exception
    //   48	50	97	java/lang/Exception
    //   63	70	97	java/lang/Exception
  }
  
  public boolean invoke(final Runnable paramRunnable, long paramLong)
  {
    int i = 1;
    if (paramRunnable != null)
    {
      if (!isDependencyThread())
      {
        if (paramLong != 0L) {
          break label46;
        }
        HandlerUtils.post(this, paramRunnable);
        return false;
      }
    }
    else {
      throw new IllegalArgumentException("No runnable to call");
    }
    paramRunnable.run();
    return true;
    label46:
    synchronized (new boolean[1])
    {
      if (!HandlerUtils.post(this, new Runnable()
      {
        public void run()
        {
          paramRunnable.run();
          synchronized (arrayOfBoolean)
          {
            arrayOfBoolean[0] = true;
            arrayOfBoolean.notifyAll();
            return;
          }
        }
      })) {
        return false;
      }
      if (paramLong >= 0L) {}
      while (i == 0)
      {
        try
        {
          ???.wait();
          int j = ???[0];
          return j;
        }
        catch (InterruptedException paramRunnable)
        {
          return false;
        }
        i = 0;
      }
      ???.wait(paramLong);
    }
  }
  
  protected void onStarted()
  {
    super.onStarted();
    synchronized (m_StartLock)
    {
      m_StartLock.notifyAll();
      return;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/web/WebAccountThread.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */