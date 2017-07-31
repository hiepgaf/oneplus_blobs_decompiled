package android.location;

import android.content.Context;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
import com.android.internal.util.Preconditions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

abstract class LocalListenerHelper<TListener>
{
  private final Context mContext;
  private final HashMap<TListener, Handler> mListeners = new HashMap();
  private final String mTag;
  
  protected LocalListenerHelper(Context paramContext, String paramString)
  {
    Preconditions.checkNotNull(paramString);
    this.mContext = paramContext;
    this.mTag = paramString;
  }
  
  private void executeOperation(ListenerOperation<TListener> paramListenerOperation, TListener paramTListener)
  {
    try
    {
      paramListenerOperation.execute(paramTListener);
      return;
    }
    catch (RemoteException paramListenerOperation)
    {
      Log.e(this.mTag, "Error in monitored listener.", paramListenerOperation);
    }
  }
  
  public boolean add(TListener paramTListener, Handler paramHandler)
  {
    Preconditions.checkNotNull(paramTListener);
    synchronized (this.mListeners)
    {
      boolean bool = this.mListeners.isEmpty();
      if (bool) {
        try
        {
          bool = registerWithServer();
          if (!bool)
          {
            Log.e(this.mTag, "Unable to register listener transport.");
            return false;
          }
        }
        catch (RemoteException paramTListener)
        {
          Log.e(this.mTag, "Error handling first listener.", paramTListener);
          return false;
        }
      }
      bool = this.mListeners.containsKey(paramTListener);
      if (bool) {
        return true;
      }
      this.mListeners.put(paramTListener, paramHandler);
      return true;
    }
  }
  
  protected void foreach(final ListenerOperation<TListener> paramListenerOperation)
  {
    for (;;)
    {
      final Object localObject2;
      synchronized (this.mListeners)
      {
        localObject2 = new ArrayList(this.mListeners.entrySet());
        ??? = ((Iterable)localObject2).iterator();
        if (!((Iterator)???).hasNext()) {
          break;
        }
        localObject2 = (Map.Entry)((Iterator)???).next();
        if (((Map.Entry)localObject2).getValue() == null) {
          executeOperation(paramListenerOperation, ((Map.Entry)localObject2).getKey());
        }
      }
    }
  }
  
  protected Context getContext()
  {
    return this.mContext;
  }
  
  protected abstract boolean registerWithServer()
    throws RemoteException;
  
  public void remove(TListener paramTListener)
  {
    Preconditions.checkNotNull(paramTListener);
    synchronized (this.mListeners)
    {
      boolean bool = this.mListeners.containsKey(paramTListener);
      this.mListeners.remove(paramTListener);
      if (bool) {
        bool = this.mListeners.isEmpty();
      }
      for (;;)
      {
        if (bool) {}
        try
        {
          unregisterFromServer();
          return;
          bool = false;
        }
        catch (RemoteException paramTListener)
        {
          for (;;)
          {
            Log.v(this.mTag, "Error handling last listener removal", paramTListener);
          }
        }
      }
    }
  }
  
  protected abstract void unregisterFromServer()
    throws RemoteException;
  
  protected static abstract interface ListenerOperation<TListener>
  {
    public abstract void execute(TListener paramTListener)
      throws RemoteException;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/location/LocalListenerHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */