package android.location;

import android.content.Context;
import android.os.RemoteException;

class GnssMeasurementCallbackTransport
  extends LocalListenerHelper<GnssMeasurementsEvent.Callback>
{
  private final IGnssMeasurementsListener mListenerTransport = new ListenerTransport(null);
  private final ILocationManager mLocationManager;
  
  public GnssMeasurementCallbackTransport(Context paramContext, ILocationManager paramILocationManager)
  {
    super(paramContext, "GnssMeasurementListenerTransport");
    this.mLocationManager = paramILocationManager;
  }
  
  protected boolean registerWithServer()
    throws RemoteException
  {
    return this.mLocationManager.addGnssMeasurementsListener(this.mListenerTransport, getContext().getPackageName());
  }
  
  protected void unregisterFromServer()
    throws RemoteException
  {
    this.mLocationManager.removeGnssMeasurementsListener(this.mListenerTransport);
  }
  
  private class ListenerTransport
    extends IGnssMeasurementsListener.Stub
  {
    private ListenerTransport() {}
    
    public void onGnssMeasurementsReceived(final GnssMeasurementsEvent paramGnssMeasurementsEvent)
    {
      paramGnssMeasurementsEvent = new LocalListenerHelper.ListenerOperation()
      {
        public void execute(GnssMeasurementsEvent.Callback paramAnonymousCallback)
          throws RemoteException
        {
          paramAnonymousCallback.onGnssMeasurementsReceived(paramGnssMeasurementsEvent);
        }
      };
      GnssMeasurementCallbackTransport.this.foreach(paramGnssMeasurementsEvent);
    }
    
    public void onStatusChanged(final int paramInt)
    {
      LocalListenerHelper.ListenerOperation local2 = new LocalListenerHelper.ListenerOperation()
      {
        public void execute(GnssMeasurementsEvent.Callback paramAnonymousCallback)
          throws RemoteException
        {
          paramAnonymousCallback.onStatusChanged(paramInt);
        }
      };
      GnssMeasurementCallbackTransport.this.foreach(local2);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/location/GnssMeasurementCallbackTransport.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */