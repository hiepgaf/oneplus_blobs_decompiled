package android.os;

import android.util.ArrayMap;

public class RemoteCallbackList<E extends IInterface>
{
  private Object[] mActiveBroadcast;
  private int mBroadcastCount = -1;
  ArrayMap<IBinder, RemoteCallbackList<E>.Callback> mCallbacks = new ArrayMap();
  private boolean mKilled = false;
  
  public int beginBroadcast()
  {
    synchronized (this.mCallbacks)
    {
      if (this.mBroadcastCount > 0) {
        throw new IllegalStateException("beginBroadcast() called while already in a broadcast");
      }
    }
    int j = this.mCallbacks.size();
    this.mBroadcastCount = j;
    if (j <= 0) {
      return 0;
    }
    Object[] arrayOfObject = this.mActiveBroadcast;
    Object localObject2;
    if (arrayOfObject != null)
    {
      localObject2 = arrayOfObject;
      if (arrayOfObject.length >= j) {}
    }
    else
    {
      localObject2 = new Object[j];
      this.mActiveBroadcast = ((Object[])localObject2);
    }
    for (;;)
    {
      int i;
      if (i < j)
      {
        localObject2[i] = this.mCallbacks.valueAt(i);
        i += 1;
      }
      else
      {
        return j;
        i = 0;
      }
    }
  }
  
  public void finishBroadcast()
  {
    synchronized (this.mCallbacks)
    {
      if (this.mBroadcastCount < 0) {
        throw new IllegalStateException("finishBroadcast() called outside of a broadcast");
      }
    }
    Object[] arrayOfObject = this.mActiveBroadcast;
    int j;
    int i;
    if (arrayOfObject != null)
    {
      j = this.mBroadcastCount;
      i = 0;
    }
    for (;;)
    {
      this.mBroadcastCount = -1;
      return;
      while (i < j)
      {
        arrayOfObject[i] = null;
        i += 1;
      }
    }
  }
  
  public Object getBroadcastCookie(int paramInt)
  {
    return ((Callback)this.mActiveBroadcast[paramInt]).mCookie;
  }
  
  public E getBroadcastItem(int paramInt)
  {
    return ((Callback)this.mActiveBroadcast[paramInt]).mCallback;
  }
  
  public int getRegisteredCallbackCount()
  {
    synchronized (this.mCallbacks)
    {
      boolean bool = this.mKilled;
      if (bool) {
        return 0;
      }
      int i = this.mCallbacks.size();
      return i;
    }
  }
  
  public void kill()
  {
    synchronized (this.mCallbacks)
    {
      int i = this.mCallbacks.size() - 1;
      while (i >= 0)
      {
        Callback localCallback = (Callback)this.mCallbacks.valueAt(i);
        localCallback.mCallback.asBinder().unlinkToDeath(localCallback, 0);
        i -= 1;
      }
      this.mCallbacks.clear();
      this.mKilled = true;
      return;
    }
  }
  
  public void onCallbackDied(E paramE) {}
  
  public void onCallbackDied(E paramE, Object paramObject)
  {
    onCallbackDied(paramE);
  }
  
  public boolean register(E paramE)
  {
    return register(paramE, null);
  }
  
  public boolean register(E paramE, Object paramObject)
  {
    synchronized (this.mCallbacks)
    {
      boolean bool = this.mKilled;
      if (bool) {
        return false;
      }
      IBinder localIBinder = paramE.asBinder();
      try
      {
        paramE = new Callback(paramE, paramObject);
        localIBinder.linkToDeath(paramE, 0);
        this.mCallbacks.put(localIBinder, paramE);
        return true;
      }
      catch (RemoteException paramE)
      {
        return false;
      }
    }
  }
  
  public boolean unregister(E paramE)
  {
    synchronized (this.mCallbacks)
    {
      paramE = (Callback)this.mCallbacks.remove(paramE.asBinder());
      if (paramE != null)
      {
        paramE.mCallback.asBinder().unlinkToDeath(paramE, 0);
        return true;
      }
      return false;
    }
  }
  
  private final class Callback
    implements IBinder.DeathRecipient
  {
    final E mCallback;
    final Object mCookie;
    
    Callback(Object paramObject)
    {
      this.mCallback = ((IInterface)paramObject);
      Object localObject;
      this.mCookie = localObject;
    }
    
    public void binderDied()
    {
      synchronized (RemoteCallbackList.this.mCallbacks)
      {
        RemoteCallbackList.this.mCallbacks.remove(this.mCallback.asBinder());
        RemoteCallbackList.this.onCallbackDied(this.mCallback, this.mCookie);
        return;
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/RemoteCallbackList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */