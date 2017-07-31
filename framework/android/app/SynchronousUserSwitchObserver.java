package android.app;

import android.os.IRemoteCallback;
import android.os.RemoteException;

public abstract class SynchronousUserSwitchObserver
  extends IUserSwitchObserver.Stub
{
  public abstract void onUserSwitching(int paramInt)
    throws RemoteException;
  
  public final void onUserSwitching(int paramInt, IRemoteCallback paramIRemoteCallback)
    throws RemoteException
  {
    try
    {
      onUserSwitching(paramInt);
      return;
    }
    finally
    {
      if (paramIRemoteCallback != null) {
        paramIRemoteCallback.sendResult(null);
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/SynchronousUserSwitchObserver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */