package android.support.v4.os;

import android.os.AsyncTask;
import android.os.Build.VERSION;

public class AsyncTaskCompat
{
  public static <Params, Progress, Result> AsyncTask<Params, Progress, Result> executeParallel(AsyncTask<Params, Progress, Result> paramAsyncTask, Params... paramVarArgs)
  {
    if (paramAsyncTask != null)
    {
      if (Build.VERSION.SDK_INT < 11)
      {
        paramAsyncTask.execute(paramVarArgs);
        return paramAsyncTask;
      }
    }
    else {
      throw new IllegalArgumentException("task can not be null");
    }
    AsyncTaskCompatHoneycomb.executeParallel(paramAsyncTask, paramVarArgs);
    return paramAsyncTask;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/support/v4/os/AsyncTaskCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */