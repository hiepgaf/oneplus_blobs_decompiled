package android.support.v4.content;

import android.os.AsyncTask;
import java.util.concurrent.Executor;

class ExecutorCompatHoneycomb
{
  public static Executor getParallelExecutor()
  {
    return AsyncTask.THREAD_POOL_EXECUTOR;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/support/v4/content/ExecutorCompatHoneycomb.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */