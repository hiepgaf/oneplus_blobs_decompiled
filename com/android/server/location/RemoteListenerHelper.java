package com.android.server.location;

import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.IInterface;
import android.os.RemoteException;
import android.util.Log;
import com.android.internal.util.Preconditions;
import com.android.server.LocationManagerService;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

abstract class RemoteListenerHelper<TListener extends IInterface>
{
  protected static final int RESULT_GPS_LOCATION_DISABLED = 3;
  protected static final int RESULT_INTERNAL_ERROR = 4;
  protected static final int RESULT_NOT_AVAILABLE = 1;
  protected static final int RESULT_NOT_SUPPORTED = 2;
  protected static final int RESULT_SUCCESS = 0;
  protected static final int RESULT_UNKNOWN = 5;
  private final Handler mHandler;
  private boolean mHasIsSupported;
  private boolean mIsRegistered;
  private boolean mIsSupported;
  private int mLastReportedResult = 5;
  private final Map<IBinder, RemoteListenerHelper<TListener>.LinkedListener> mListenerMap = new HashMap();
  private final String mTag;
  
  protected RemoteListenerHelper(Handler paramHandler, String paramString)
  {
    Preconditions.checkNotNull(paramString);
    this.mHandler = paramHandler;
    this.mTag = paramString;
  }
  
  private int calculateCurrentResultUnsafe()
  {
    if (!isAvailableInPlatform()) {
      return 1;
    }
    if ((!this.mHasIsSupported) || (this.mListenerMap.isEmpty())) {
      return 5;
    }
    if (!this.mIsSupported) {
      return 2;
    }
    if (!isGpsEnabled()) {
      return 3;
    }
    return 0;
  }
  
  private void foreachUnsafe(ListenerOperation<TListener> paramListenerOperation)
  {
    Iterator localIterator = this.mListenerMap.values().iterator();
    while (localIterator.hasNext())
    {
      LinkedListener localLinkedListener = (LinkedListener)localIterator.next();
      if (!LocationManagerService.checkUidBlock(localLinkedListener.getUid())) {
        post(localLinkedListener.getUnderlyingListener(), paramListenerOperation);
      }
    }
  }
  
  private void post(TListener paramTListener, ListenerOperation<TListener> paramListenerOperation)
  {
    if (paramListenerOperation != null) {
      this.mHandler.post(new HandlerRunnable(paramTListener, paramListenerOperation));
    }
  }
  
  private boolean tryRegister()
  {
    if (!this.mIsRegistered) {
      this.mIsRegistered = registerWithService();
    }
    return this.mIsRegistered;
  }
  
  private void tryUnregister()
  {
    if (!this.mIsRegistered) {
      return;
    }
    unregisterFromService();
    this.mIsRegistered = false;
  }
  
  public boolean addListener(TListener paramTListener)
  {
    Preconditions.checkNotNull(paramTListener, "Attempted to register a 'null' listener.");
    IBinder localIBinder = paramTListener.asBinder();
    LinkedListener localLinkedListener = new LinkedListener(paramTListener);
    for (;;)
    {
      synchronized (this.mListenerMap)
      {
        boolean bool = this.mListenerMap.containsKey(localIBinder);
        if (bool) {
          return true;
        }
        try
        {
          localIBinder.linkToDeath(localLinkedListener, 0);
          this.mListenerMap.put(localIBinder, localLinkedListener);
          if (!isAvailableInPlatform())
          {
            i = 1;
            int j = Binder.getCallingUid();
            localLinkedListener.setUid(j);
            LocationManagerService.updateLocationReceiver(j, true, false);
            post(paramTListener, getHandlerOperation(i));
            return true;
          }
        }
        catch (RemoteException paramTListener)
        {
          Log.v(this.mTag, "Remote listener already died.", paramTListener);
          return false;
        }
        if ((!this.mHasIsSupported) || (this.mIsSupported))
        {
          if (!isGpsEnabled())
          {
            i = 3;
            continue;
          }
          if (!tryRegister())
          {
            i = 4;
            continue;
          }
          if (this.mHasIsSupported)
          {
            bool = this.mIsSupported;
            if (bool)
            {
              i = 0;
              continue;
            }
          }
          return true;
        }
      }
      int i = 2;
    }
  }
  
  protected void foreach(ListenerOperation<TListener> paramListenerOperation)
  {
    synchronized (this.mListenerMap)
    {
      foreachUnsafe(paramListenerOperation);
      return;
    }
  }
  
  protected abstract ListenerOperation<TListener> getHandlerOperation(int paramInt);
  
  protected abstract boolean isAvailableInPlatform();
  
  protected abstract boolean isGpsEnabled();
  
  protected abstract boolean registerWithService();
  
  public void removeListener(TListener arg1)
  {
    Preconditions.checkNotNull(???, "Attempted to remove a 'null' listener.");
    IBinder localIBinder = ???.asBinder();
    synchronized (this.mListenerMap)
    {
      LinkedListener localLinkedListener = (LinkedListener)this.mListenerMap.remove(localIBinder);
      if (this.mListenerMap.isEmpty()) {
        tryUnregister();
      }
      if (localLinkedListener != null) {
        localIBinder.unlinkToDeath(localLinkedListener, 0);
      }
      if (localLinkedListener != null) {
        LocationManagerService.updateLocationReceiver(localLinkedListener.getUid(), false, false);
      }
      return;
    }
  }
  
  protected void setSupported(boolean paramBoolean)
  {
    synchronized (this.mListenerMap)
    {
      this.mHasIsSupported = true;
      this.mIsSupported = paramBoolean;
      return;
    }
  }
  
  protected boolean tryUpdateRegistrationWithService()
  {
    synchronized (this.mListenerMap)
    {
      if (!isGpsEnabled())
      {
        tryUnregister();
        return true;
      }
      boolean bool = this.mListenerMap.isEmpty();
      if (bool) {
        return true;
      }
      bool = tryRegister();
      if (bool) {
        return true;
      }
      foreachUnsafe(getHandlerOperation(4));
      return false;
    }
  }
  
  protected abstract void unregisterFromService();
  
  protected void updateResult()
  {
    synchronized (this.mListenerMap)
    {
      int i = calculateCurrentResultUnsafe();
      int j = this.mLastReportedResult;
      if (j == i) {
        return;
      }
      foreachUnsafe(getHandlerOperation(i));
      this.mLastReportedResult = i;
      return;
    }
  }
  
  private class HandlerRunnable
    implements Runnable
  {
    private final TListener mListener;
    private final RemoteListenerHelper.ListenerOperation<TListener> mOperation;
    
    public HandlerRunnable(RemoteListenerHelper.ListenerOperation<TListener> paramListenerOperation)
    {
      this.mListener = paramListenerOperation;
      RemoteListenerHelper.ListenerOperation localListenerOperation;
      this.mOperation = localListenerOperation;
    }
    
    public void run()
    {
      try
      {
        if (RemoteListenerHelper.-get0(RemoteListenerHelper.this).containsKey(this.mListener.asBinder())) {
          this.mOperation.execute(this.mListener);
        }
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Log.v(RemoteListenerHelper.-get1(RemoteListenerHelper.this), "Error in monitored listener.", localRemoteException);
        RemoteListenerHelper.this.removeListener(this.mListener);
      }
    }
  }
  
  private class LinkedListener
    implements IBinder.DeathRecipient
  {
    private boolean mBlock = false;
    private final TListener mListener;
    private int mUid;
    
    public LinkedListener()
    {
      IInterface localIInterface;
      this.mListener = localIInterface;
    }
    
    public void binderDied()
    {
      Log.d(RemoteListenerHelper.-get1(RemoteListenerHelper.this), "Remote Listener died: " + this.mListener);
      RemoteListenerHelper.this.removeListener(this.mListener);
    }
    
    public int getUid()
    {
      return this.mUid;
    }
    
    public TListener getUnderlyingListener()
    {
      return this.mListener;
    }
    
    public void setUid(int paramInt)
    {
      this.mUid = paramInt;
    }
  }
  
  protected static abstract interface ListenerOperation<TListener extends IInterface>
  {
    public abstract void execute(TListener paramTListener)
      throws RemoteException;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/location/RemoteListenerHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */