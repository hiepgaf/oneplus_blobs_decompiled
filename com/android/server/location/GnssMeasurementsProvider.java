package com.android.server.location;

import android.location.GnssMeasurementsEvent;
import android.location.IGnssMeasurementsListener;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;

public abstract class GnssMeasurementsProvider
  extends RemoteListenerHelper<IGnssMeasurementsListener>
{
  private static final String TAG = "GnssMeasurementsProvider";
  
  protected GnssMeasurementsProvider(Handler paramHandler)
  {
    super(paramHandler, "GnssMeasurementsProvider");
  }
  
  protected RemoteListenerHelper.ListenerOperation<IGnssMeasurementsListener> getHandlerOperation(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      Log.v("GnssMeasurementsProvider", "Unhandled addListener result: " + paramInt);
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
  
  public void onMeasurementsAvailable(final GnssMeasurementsEvent paramGnssMeasurementsEvent)
  {
    foreach(new RemoteListenerHelper.ListenerOperation()
    {
      public void execute(IGnssMeasurementsListener paramAnonymousIGnssMeasurementsListener)
        throws RemoteException
      {
        paramAnonymousIGnssMeasurementsListener.onGnssMeasurementsReceived(paramGnssMeasurementsEvent);
      }
    });
  }
  
  private static class StatusChangedOperation
    implements RemoteListenerHelper.ListenerOperation<IGnssMeasurementsListener>
  {
    private final int mStatus;
    
    public StatusChangedOperation(int paramInt)
    {
      this.mStatus = paramInt;
    }
    
    public void execute(IGnssMeasurementsListener paramIGnssMeasurementsListener)
      throws RemoteException
    {
      paramIGnssMeasurementsListener.onStatusChanged(this.mStatus);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/location/GnssMeasurementsProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */