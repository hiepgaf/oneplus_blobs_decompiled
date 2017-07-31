package android.app;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QueuedWork
{
  private static final ConcurrentLinkedQueue<Runnable> sPendingWorkFinishers = new ConcurrentLinkedQueue();
  private static ExecutorService sSingleThreadExecutor = null;
  
  public static void add(Runnable paramRunnable)
  {
    sPendingWorkFinishers.add(paramRunnable);
  }
  
  public static boolean hasPendingWork()
  {
    return !sPendingWorkFinishers.isEmpty();
  }
  
  public static void remove(Runnable paramRunnable)
  {
    sPendingWorkFinishers.remove(paramRunnable);
  }
  
  public static ExecutorService singleThreadExecutor()
  {
    try
    {
      if (sSingleThreadExecutor == null) {
        sSingleThreadExecutor = Executors.newSingleThreadExecutor();
      }
      ExecutorService localExecutorService = sSingleThreadExecutor;
      return localExecutorService;
    }
    finally {}
  }
  
  public static void waitToFinish()
  {
    for (;;)
    {
      Runnable localRunnable = (Runnable)sPendingWorkFinishers.poll();
      if (localRunnable == null) {
        break;
      }
      localRunnable.run();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/QueuedWork.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */