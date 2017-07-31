package com.android.server.location;

import android.location.IGnssStatusListener;
import android.os.Handler;
import android.os.RemoteException;

abstract class GnssStatusListenerHelper
  extends RemoteListenerHelper<IGnssStatusListener>
{
  protected GnssStatusListenerHelper(Handler paramHandler)
  {
    super(paramHandler, "GnssStatusListenerHelper");
    setSupported(GnssLocationProvider.isSupported());
  }
  
  protected RemoteListenerHelper.ListenerOperation<IGnssStatusListener> getHandlerOperation(int paramInt)
  {
    return null;
  }
  
  public void onFirstFix(final int paramInt)
  {
    foreach(new Operation()
    {
      public void execute(IGnssStatusListener paramAnonymousIGnssStatusListener)
        throws RemoteException
      {
        paramAnonymousIGnssStatusListener.onFirstFix(paramInt);
      }
    });
  }
  
  public void onNmeaReceived(final long paramLong, String paramString)
  {
    foreach(new Operation()
    {
      public void execute(IGnssStatusListener paramAnonymousIGnssStatusListener)
        throws RemoteException
      {
        paramAnonymousIGnssStatusListener.onNmeaReceived(paramLong, this.val$nmea);
      }
    });
  }
  
  public void onStatusChanged(boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (Object localObject = new Operation()
        {
          public void execute(IGnssStatusListener paramAnonymousIGnssStatusListener)
            throws RemoteException
          {
            paramAnonymousIGnssStatusListener.onGnssStarted();
          }
        };; localObject = new Operation()
        {
          public void execute(IGnssStatusListener paramAnonymousIGnssStatusListener)
            throws RemoteException
          {
            paramAnonymousIGnssStatusListener.onGnssStopped();
          }
        })
    {
      foreach((RemoteListenerHelper.ListenerOperation)localObject);
      return;
    }
  }
  
  public void onSvStatusChanged(final int paramInt, final int[] paramArrayOfInt, final float[] paramArrayOfFloat1, final float[] paramArrayOfFloat2, final float[] paramArrayOfFloat3)
  {
    foreach(new Operation()
    {
      public void execute(IGnssStatusListener paramAnonymousIGnssStatusListener)
        throws RemoteException
      {
        paramAnonymousIGnssStatusListener.onSvStatusChanged(paramInt, paramArrayOfInt, paramArrayOfFloat1, paramArrayOfFloat2, paramArrayOfFloat3);
      }
    });
  }
  
  protected boolean registerWithService()
  {
    return true;
  }
  
  protected void unregisterFromService() {}
  
  private static abstract interface Operation
    extends RemoteListenerHelper.ListenerOperation<IGnssStatusListener>
  {}
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/location/GnssStatusListenerHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */