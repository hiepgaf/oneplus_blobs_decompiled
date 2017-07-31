package android.location;

import android.content.Context;
import android.os.RemoteException;

class GnssNavigationMessageCallbackTransport
  extends LocalListenerHelper<GnssNavigationMessage.Callback>
{
  private final IGnssNavigationMessageListener mListenerTransport = new ListenerTransport(null);
  private final ILocationManager mLocationManager;
  
  public GnssNavigationMessageCallbackTransport(Context paramContext, ILocationManager paramILocationManager)
  {
    super(paramContext, "GnssNavigationMessageCallbackTransport");
    this.mLocationManager = paramILocationManager;
  }
  
  protected boolean registerWithServer()
    throws RemoteException
  {
    return this.mLocationManager.addGnssNavigationMessageListener(this.mListenerTransport, getContext().getPackageName());
  }
  
  protected void unregisterFromServer()
    throws RemoteException
  {
    this.mLocationManager.removeGnssNavigationMessageListener(this.mListenerTransport);
  }
  
  private class ListenerTransport
    extends IGnssNavigationMessageListener.Stub
  {
    private ListenerTransport() {}
    
    public void onGnssNavigationMessageReceived(final GnssNavigationMessage paramGnssNavigationMessage)
    {
      paramGnssNavigationMessage = new LocalListenerHelper.ListenerOperation()
      {
        public void execute(GnssNavigationMessage.Callback paramAnonymousCallback)
          throws RemoteException
        {
          paramAnonymousCallback.onGnssNavigationMessageReceived(paramGnssNavigationMessage);
        }
      };
      GnssNavigationMessageCallbackTransport.this.foreach(paramGnssNavigationMessage);
    }
    
    public void onStatusChanged(final int paramInt)
    {
      LocalListenerHelper.ListenerOperation local2 = new LocalListenerHelper.ListenerOperation()
      {
        public void execute(GnssNavigationMessage.Callback paramAnonymousCallback)
          throws RemoteException
        {
          paramAnonymousCallback.onStatusChanged(paramInt);
        }
      };
      GnssNavigationMessageCallbackTransport.this.foreach(local2);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/location/GnssNavigationMessageCallbackTransport.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */