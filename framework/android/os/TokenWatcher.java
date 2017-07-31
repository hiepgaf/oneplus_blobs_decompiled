package android.os;

import android.util.Log;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.WeakHashMap;

public abstract class TokenWatcher
{
  private volatile boolean mAcquired = false;
  private Handler mHandler;
  private int mNotificationQueue = -1;
  private Runnable mNotificationTask = new Runnable()
  {
    public void run()
    {
      int i;
      do
      {
        synchronized (TokenWatcher.-get2(TokenWatcher.this))
        {
          i = TokenWatcher.-get0(TokenWatcher.this);
          TokenWatcher.-set0(TokenWatcher.this, -1);
          if (i == 1)
          {
            TokenWatcher.this.acquired();
            return;
          }
        }
      } while (i != 0);
      TokenWatcher.this.released();
    }
  };
  private String mTag;
  private WeakHashMap<IBinder, Death> mTokens = new WeakHashMap();
  
  public TokenWatcher(Handler paramHandler, String paramString)
  {
    this.mHandler = paramHandler;
    if (paramString != null) {}
    for (;;)
    {
      this.mTag = paramString;
      return;
      paramString = "TokenWatcher";
    }
  }
  
  private ArrayList<String> dumpInternal()
  {
    ArrayList localArrayList = new ArrayList();
    synchronized (this.mTokens)
    {
      Object localObject2 = this.mTokens.keySet();
      localArrayList.add("Token count: " + this.mTokens.size());
      int i = 0;
      localObject2 = ((Iterable)localObject2).iterator();
      while (((Iterator)localObject2).hasNext())
      {
        IBinder localIBinder = (IBinder)((Iterator)localObject2).next();
        localArrayList.add("[" + i + "] " + ((Death)this.mTokens.get(localIBinder)).tag + " - " + localIBinder);
        i += 1;
      }
      return localArrayList;
    }
  }
  
  private void sendNotificationLocked(boolean paramBoolean)
  {
    int i;
    if (paramBoolean)
    {
      i = 1;
      if (this.mNotificationQueue != -1) {
        break label37;
      }
      this.mNotificationQueue = i;
      this.mHandler.post(this.mNotificationTask);
    }
    label37:
    while (this.mNotificationQueue == i)
    {
      return;
      i = 0;
      break;
    }
    this.mNotificationQueue = -1;
    this.mHandler.removeCallbacks(this.mNotificationTask);
  }
  
  public void acquire(IBinder paramIBinder, String paramString)
  {
    synchronized (this.mTokens)
    {
      int i = this.mTokens.size();
      paramString = new Death(paramIBinder, paramString);
      try
      {
        paramIBinder.linkToDeath(paramString, 0);
        this.mTokens.put(paramIBinder, paramString);
        if (i == 0)
        {
          boolean bool = this.mAcquired;
          if (!bool) {}
        }
        else
        {
          return;
        }
      }
      catch (RemoteException paramIBinder)
      {
        return;
      }
      sendNotificationLocked(true);
      this.mAcquired = true;
    }
  }
  
  public abstract void acquired();
  
  public void cleanup(IBinder paramIBinder, boolean paramBoolean)
  {
    synchronized (this.mTokens)
    {
      paramIBinder = (Death)this.mTokens.remove(paramIBinder);
      if ((paramBoolean) && (paramIBinder != null))
      {
        paramIBinder.token.unlinkToDeath(paramIBinder, 0);
        paramIBinder.token = null;
      }
      if ((this.mTokens.size() == 0) && (this.mAcquired))
      {
        sendNotificationLocked(false);
        this.mAcquired = false;
      }
      return;
    }
  }
  
  public void dump()
  {
    Iterator localIterator = dumpInternal().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      Log.i(this.mTag, str);
    }
  }
  
  public void dump(PrintWriter paramPrintWriter)
  {
    Iterator localIterator = dumpInternal().iterator();
    while (localIterator.hasNext()) {
      paramPrintWriter.println((String)localIterator.next());
    }
  }
  
  public boolean isAcquired()
  {
    synchronized (this.mTokens)
    {
      boolean bool = this.mAcquired;
      return bool;
    }
  }
  
  public void release(IBinder paramIBinder)
  {
    cleanup(paramIBinder, true);
  }
  
  public abstract void released();
  
  private class Death
    implements IBinder.DeathRecipient
  {
    String tag;
    IBinder token;
    
    Death(IBinder paramIBinder, String paramString)
    {
      this.token = paramIBinder;
      this.tag = paramString;
    }
    
    public void binderDied()
    {
      TokenWatcher.this.cleanup(this.token, false);
    }
    
    protected void finalize()
      throws Throwable
    {
      try
      {
        if (this.token != null)
        {
          Log.w(TokenWatcher.-get1(TokenWatcher.this), "cleaning up leaked reference: " + this.tag);
          TokenWatcher.this.release(this.token);
        }
        return;
      }
      finally
      {
        super.finalize();
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/TokenWatcher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */