package com.android.server.location;

import android.location.GnssNavigationMessage;
import android.location.IGnssNavigationMessageListener;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;

public abstract class GnssNavigationMessageProvider
  extends RemoteListenerHelper<IGnssNavigationMessageListener>
{
  private static final String TAG = "GnssNavigationMessageProvider";
  
  protected GnssNavigationMessageProvider(Handler paramHandler)
  {
    super(paramHandler, "GnssNavigationMessageProvider");
  }
  
  protected RemoteListenerHelper.ListenerOperation<IGnssNavigationMessageListener> getHandlerOperation(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      Log.v("GnssNavigationMessageProvider", "Unhandled addListener result: " + paramInt);
      return null;
    case 0: 
      paramInt = 1;
    case 1: 
    case 2: 
    case 4: 
    case 3: 
      for (;;)
      {
        return new StatusChangedOperation(paramInt);
        paramInt = 0;
        continue;
        paramInt = 2;
      }
    }
    return null;
  }
  
  public void onCapabilitiesUpdated(boolean paramBoolean)
  {
    setSupported(paramBoolean);
    updateResult();
  }
  
  public void onGpsEnabledChanged()
  {
    if (tryUpdateRegistrationWithService()) {
      updateResult();
    }
  }
  
  public void onNavigationMessageAvailable(final GnssNavigationMessage paramGnssNavigationMessage)
  {
    foreach(new RemoteListenerHelper.ListenerOperation()
    {
      public void execute(IGnssNavigationMessageListener paramAnonymousIGnssNavigationMessageListener)
        throws RemoteException
      {
        paramAnonymousIGnssNavigationMessageListener.onGnssNavigationMessageReceived(paramGnssNavigationMessage);
      }
    });
  }
  
  private static class StatusChangedOperation
    implements RemoteListenerHelper.ListenerOperation<IGnssNavigationMessageListener>
  {
    private final int mStatus;
    
    public StatusChangedOperation(int paramInt)
    {
      this.mStatus = paramInt;
    }
    
    public void execute(IGnssNavigationMessageListener paramIGnssNavigationMessageListener)
      throws RemoteException
    {
      paramIGnssNavigationMessageListener.onStatusChanged(this.mStatus);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/location/GnssNavigationMessageProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */