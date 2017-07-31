package android.accounts;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public abstract interface AccountManagerFuture<V>
{
  public abstract boolean cancel(boolean paramBoolean);
  
  public abstract V getResult()
    throws OperationCanceledException, IOException, AuthenticatorException;
  
  public abstract V getResult(long paramLong, TimeUnit paramTimeUnit)
    throws OperationCanceledException, IOException, AuthenticatorException;
  
  public abstract boolean isCancelled();
  
  public abstract boolean isDone();
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/accounts/AccountManagerFuture.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */