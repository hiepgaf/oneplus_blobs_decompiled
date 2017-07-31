package android.os;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SynchronousResultReceiver
  extends ResultReceiver
{
  private final CompletableFuture<Result> mFuture = new CompletableFuture();
  
  public SynchronousResultReceiver()
  {
    super((Handler)null);
  }
  
  public Result awaitResult(long paramLong)
    throws TimeoutException
  {
    long l2 = System.currentTimeMillis();
    long l1 = paramLong;
    while (l1 >= 0L) {
      try
      {
        Result localResult = (Result)this.mFuture.get(l1, TimeUnit.MILLISECONDS);
        return localResult;
      }
      catch (InterruptedException localInterruptedException)
      {
        l1 -= l2 + paramLong - System.currentTimeMillis();
      }
      catch (ExecutionException localExecutionException)
      {
        throw new AssertionError("Error receiving response", localExecutionException);
      }
    }
    throw new TimeoutException();
  }
  
  protected final void onReceiveResult(int paramInt, Bundle paramBundle)
  {
    super.onReceiveResult(paramInt, paramBundle);
    this.mFuture.complete(new Result(paramInt, paramBundle));
  }
  
  public static class Result
  {
    public Bundle bundle;
    public int resultCode;
    
    public Result(int paramInt, Bundle paramBundle)
    {
      this.resultCode = paramInt;
      this.bundle = paramBundle;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/SynchronousResultReceiver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */