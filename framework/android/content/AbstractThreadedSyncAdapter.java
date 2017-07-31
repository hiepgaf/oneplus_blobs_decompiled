package android.content;

import android.accounts.Account;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.os.Trace;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractThreadedSyncAdapter
{
  @Deprecated
  public static final int LOG_SYNC_DETAILS = 2743;
  private boolean mAllowParallelSyncs;
  private final boolean mAutoInitialize;
  private final Context mContext;
  private final ISyncAdapterImpl mISyncAdapterImpl;
  private final AtomicInteger mNumSyncStarts;
  private final Object mSyncThreadLock = new Object();
  private final HashMap<Account, SyncThread> mSyncThreads = new HashMap();
  
  public AbstractThreadedSyncAdapter(Context paramContext, boolean paramBoolean)
  {
    this(paramContext, paramBoolean, false);
  }
  
  public AbstractThreadedSyncAdapter(Context paramContext, boolean paramBoolean1, boolean paramBoolean2)
  {
    this.mContext = paramContext;
    this.mISyncAdapterImpl = new ISyncAdapterImpl(null);
    this.mNumSyncStarts = new AtomicInteger(0);
    this.mAutoInitialize = paramBoolean1;
    this.mAllowParallelSyncs = paramBoolean2;
  }
  
  private Account toSyncKey(Account paramAccount)
  {
    if (this.mAllowParallelSyncs) {
      return paramAccount;
    }
    return null;
  }
  
  public Context getContext()
  {
    return this.mContext;
  }
  
  public final IBinder getSyncAdapterBinder()
  {
    return this.mISyncAdapterImpl.asBinder();
  }
  
  public abstract void onPerformSync(Account paramAccount, Bundle paramBundle, String paramString, ContentProviderClient paramContentProviderClient, SyncResult paramSyncResult);
  
  public void onSecurityException(Account paramAccount, Bundle paramBundle, String paramString, SyncResult paramSyncResult) {}
  
  public void onSyncCanceled()
  {
    synchronized (this.mSyncThreadLock)
    {
      SyncThread localSyncThread = (SyncThread)this.mSyncThreads.get(null);
      if (localSyncThread != null) {
        localSyncThread.interrupt();
      }
      return;
    }
  }
  
  public void onSyncCanceled(Thread paramThread)
  {
    paramThread.interrupt();
  }
  
  private class ISyncAdapterImpl
    extends ISyncAdapter.Stub
  {
    private ISyncAdapterImpl() {}
    
    public void cancelSync(ISyncContext paramISyncContext)
    {
      Object localObject2 = null;
      synchronized (AbstractThreadedSyncAdapter.-get4(AbstractThreadedSyncAdapter.this))
      {
        Iterator localIterator = AbstractThreadedSyncAdapter.-get5(AbstractThreadedSyncAdapter.this).values().iterator();
        Object localObject1;
        IBinder localIBinder1;
        IBinder localIBinder2;
        do
        {
          localObject1 = localObject2;
          if (!localIterator.hasNext()) {
            break;
          }
          localObject1 = (AbstractThreadedSyncAdapter.SyncThread)localIterator.next();
          localIBinder1 = AbstractThreadedSyncAdapter.SyncThread.-get0((AbstractThreadedSyncAdapter.SyncThread)localObject1).getSyncContextBinder();
          localIBinder2 = paramISyncContext.asBinder();
        } while (localIBinder1 != localIBinder2);
        if (localObject1 != null)
        {
          if (AbstractThreadedSyncAdapter.-get0(AbstractThreadedSyncAdapter.this)) {
            AbstractThreadedSyncAdapter.this.onSyncCanceled((Thread)localObject1);
          }
        }
        else {
          return;
        }
      }
      AbstractThreadedSyncAdapter.this.onSyncCanceled();
    }
    
    public void initialize(Account paramAccount, String paramString)
      throws RemoteException
    {
      Bundle localBundle = new Bundle();
      localBundle.putBoolean("initialize", true);
      startSync(null, paramString, paramAccount, localBundle);
    }
    
    public void startSync(ISyncContext arg1, String paramString, Account paramAccount, Bundle paramBundle)
    {
      SyncContext localSyncContext = new SyncContext(???);
      Account localAccount = AbstractThreadedSyncAdapter.-wrap0(AbstractThreadedSyncAdapter.this, paramAccount);
      synchronized (AbstractThreadedSyncAdapter.-get4(AbstractThreadedSyncAdapter.this))
      {
        if (AbstractThreadedSyncAdapter.-get5(AbstractThreadedSyncAdapter.this).containsKey(localAccount)) {
          break label208;
        }
        if ((AbstractThreadedSyncAdapter.-get1(AbstractThreadedSyncAdapter.this)) && (paramBundle != null))
        {
          boolean bool = paramBundle.getBoolean("initialize", false);
          if (bool) {
            try
            {
              if (ContentResolver.getIsSyncable(paramAccount, paramString) < 0) {
                ContentResolver.setIsSyncable(paramAccount, paramString, 1);
              }
              localSyncContext.onFinished(new SyncResult());
              return;
            }
            finally
            {
              paramString = finally;
              localSyncContext.onFinished(new SyncResult());
              throw paramString;
            }
          }
        }
      }
      paramString = new AbstractThreadedSyncAdapter.SyncThread(AbstractThreadedSyncAdapter.this, "SyncAdapterThread-" + AbstractThreadedSyncAdapter.-get3(AbstractThreadedSyncAdapter.this).incrementAndGet(), localSyncContext, paramString, paramAccount, paramBundle, null);
      AbstractThreadedSyncAdapter.-get5(AbstractThreadedSyncAdapter.this).put(localAccount, paramString);
      paramString.start();
      label208:
      for (int i = 0;; i = 1)
      {
        if (i != 0) {
          localSyncContext.onFinished(SyncResult.ALREADY_IN_PROGRESS);
        }
        return;
      }
    }
  }
  
  private class SyncThread
    extends Thread
  {
    private final Account mAccount;
    private final String mAuthority;
    private final Bundle mExtras;
    private final SyncContext mSyncContext;
    private final Account mThreadsKey;
    
    private SyncThread(String paramString1, SyncContext paramSyncContext, String paramString2, Account paramAccount, Bundle paramBundle)
    {
      super();
      this.mSyncContext = paramSyncContext;
      this.mAuthority = paramString2;
      this.mAccount = paramAccount;
      this.mExtras = paramBundle;
      this.mThreadsKey = AbstractThreadedSyncAdapter.-wrap0(AbstractThreadedSyncAdapter.this, paramAccount);
    }
    
    private boolean isCanceled()
    {
      return Thread.currentThread().isInterrupted();
    }
    
    public void run()
    {
      Process.setThreadPriority(10);
      Trace.traceBegin(128L, this.mAuthority);
      localSyncResult = new SyncResult();
      Object localObject11 = null;
      localContentProviderClient = null;
      ??? = localContentProviderClient;
      Object localObject4 = localObject11;
      try
      {
        boolean bool = isCanceled();
        if (bool)
        {
          Trace.traceEnd(128L);
          if (!isCanceled()) {
            this.mSyncContext.onFinished(localSyncResult);
          }
          synchronized (AbstractThreadedSyncAdapter.-get4(AbstractThreadedSyncAdapter.this))
          {
            AbstractThreadedSyncAdapter.-get5(AbstractThreadedSyncAdapter.this).remove(this.mThreadsKey);
            return;
          }
        }
        ??? = localContentProviderClient;
        localObject6 = localObject11;
        localContentProviderClient = AbstractThreadedSyncAdapter.-get2(AbstractThreadedSyncAdapter.this).getContentResolver().acquireContentProviderClient(this.mAuthority);
        if (localContentProviderClient == null) {
          break label220;
        }
        ??? = localContentProviderClient;
        localObject6 = localContentProviderClient;
        AbstractThreadedSyncAdapter.this.onPerformSync(this.mAccount, this.mExtras, this.mAuthority, localContentProviderClient, localSyncResult);
      }
      catch (SecurityException localSecurityException) {}finally
      {
        synchronized (AbstractThreadedSyncAdapter.-get4(AbstractThreadedSyncAdapter.this))
        {
          Object localObject6;
          Object localObject7;
          AbstractThreadedSyncAdapter.-get5(AbstractThreadedSyncAdapter.this).remove(this.mThreadsKey);
        }
      }
      Trace.traceEnd(128L);
      if (localContentProviderClient != null) {
        localContentProviderClient.release();
      }
      if (!isCanceled()) {
        this.mSyncContext.onFinished(localSyncResult);
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/AbstractThreadedSyncAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */