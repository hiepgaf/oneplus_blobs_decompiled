package android.support.v4.content;

import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.util.TimeUtils;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.concurrent.CountDownLatch;

public abstract class AsyncTaskLoader<D>
  extends Loader<D>
{
  static final boolean DEBUG = false;
  static final String TAG = "AsyncTaskLoader";
  volatile AsyncTaskLoader<D>.LoadTask mCancellingTask;
  Handler mHandler;
  long mLastLoadCompleteTime = -10000L;
  volatile AsyncTaskLoader<D>.LoadTask mTask;
  long mUpdateThrottle;
  
  public AsyncTaskLoader(Context paramContext)
  {
    super(paramContext);
  }
  
  public boolean cancelLoad()
  {
    if (this.mTask == null) {
      return false;
    }
    boolean bool;
    if (this.mCancellingTask == null)
    {
      if (this.mTask.waiting) {
        break label85;
      }
      bool = this.mTask.cancel(false);
      if (bool) {
        break label111;
      }
    }
    for (;;)
    {
      this.mTask = null;
      return bool;
      if (!this.mTask.waiting) {}
      for (;;)
      {
        this.mTask = null;
        return false;
        this.mTask.waiting = false;
        this.mHandler.removeCallbacks(this.mTask);
      }
      label85:
      this.mTask.waiting = false;
      this.mHandler.removeCallbacks(this.mTask);
      this.mTask = null;
      return false;
      label111:
      this.mCancellingTask = this.mTask;
    }
  }
  
  void dispatchOnCancelled(AsyncTaskLoader<D>.LoadTask paramAsyncTaskLoader, D paramD)
  {
    onCanceled(paramD);
    if (this.mCancellingTask != paramAsyncTaskLoader) {
      return;
    }
    rollbackContentChanged();
    this.mLastLoadCompleteTime = SystemClock.uptimeMillis();
    this.mCancellingTask = null;
    executePendingTask();
  }
  
  void dispatchOnLoadComplete(AsyncTaskLoader<D>.LoadTask paramAsyncTaskLoader, D paramD)
  {
    if (this.mTask == paramAsyncTaskLoader)
    {
      if (!isAbandoned())
      {
        commitContentChanged();
        this.mLastLoadCompleteTime = SystemClock.uptimeMillis();
        this.mTask = null;
        deliverResult(paramD);
      }
    }
    else
    {
      dispatchOnCancelled(paramAsyncTaskLoader, paramD);
      return;
    }
    onCanceled(paramD);
  }
  
  public void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    super.dump(paramString, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
    if (this.mTask == null) {
      if (this.mCancellingTask != null) {
        break label112;
      }
    }
    for (;;)
    {
      if (this.mUpdateThrottle != 0L)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mUpdateThrottle=");
        TimeUtils.formatDuration(this.mUpdateThrottle, paramPrintWriter);
        paramPrintWriter.print(" mLastLoadCompleteTime=");
        TimeUtils.formatDuration(this.mLastLoadCompleteTime, SystemClock.uptimeMillis(), paramPrintWriter);
        paramPrintWriter.println();
      }
      return;
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mTask=");
      paramPrintWriter.print(this.mTask);
      paramPrintWriter.print(" waiting=");
      paramPrintWriter.println(this.mTask.waiting);
      break;
      label112:
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mCancellingTask=");
      paramPrintWriter.print(this.mCancellingTask);
      paramPrintWriter.print(" waiting=");
      paramPrintWriter.println(this.mCancellingTask.waiting);
    }
  }
  
  void executePendingTask()
  {
    int j = 0;
    if (this.mCancellingTask != null) {}
    while (this.mTask == null) {
      return;
    }
    if (!this.mTask.waiting) {
      if (this.mUpdateThrottle > 0L) {
        break label118;
      }
    }
    label118:
    for (int i = 1;; i = 0)
    {
      if (i != 0) {
        break label123;
      }
      i = j;
      if (SystemClock.uptimeMillis() >= this.mLastLoadCompleteTime + this.mUpdateThrottle) {
        i = 1;
      }
      if (i != 0) {
        break label123;
      }
      this.mTask.waiting = true;
      this.mHandler.postAtTime(this.mTask, this.mLastLoadCompleteTime + this.mUpdateThrottle);
      return;
      this.mTask.waiting = false;
      this.mHandler.removeCallbacks(this.mTask);
      break;
    }
    label123:
    this.mTask.executeOnExecutor(ModernAsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
  }
  
  public abstract D loadInBackground();
  
  public void onCanceled(D paramD) {}
  
  protected void onForceLoad()
  {
    super.onForceLoad();
    cancelLoad();
    this.mTask = new LoadTask();
    executePendingTask();
  }
  
  protected D onLoadInBackground()
  {
    return (D)loadInBackground();
  }
  
  public void setUpdateThrottle(long paramLong)
  {
    this.mUpdateThrottle = paramLong;
    if (paramLong != 0L) {
      this.mHandler = new Handler();
    }
  }
  
  public void waitForLoader()
  {
    LoadTask localLoadTask = this.mTask;
    if (localLoadTask == null) {
      return;
    }
    try
    {
      localLoadTask.done.await();
      return;
    }
    catch (InterruptedException localInterruptedException) {}
  }
  
  final class LoadTask
    extends ModernAsyncTask<Void, Void, D>
    implements Runnable
  {
    private CountDownLatch done = new CountDownLatch(1);
    D result;
    boolean waiting;
    
    LoadTask() {}
    
    protected D doInBackground(Void... paramVarArgs)
    {
      this.result = AsyncTaskLoader.this.onLoadInBackground();
      return (D)this.result;
    }
    
    protected void onCancelled()
    {
      try
      {
        AsyncTaskLoader.this.dispatchOnCancelled(this, this.result);
        return;
      }
      finally
      {
        this.done.countDown();
      }
    }
    
    protected void onPostExecute(D paramD)
    {
      try
      {
        AsyncTaskLoader.this.dispatchOnLoadComplete(this, paramD);
        return;
      }
      finally
      {
        this.done.countDown();
      }
    }
    
    public void run()
    {
      this.waiting = false;
      AsyncTaskLoader.this.executePendingTask();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/support/v4/content/AsyncTaskLoader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */