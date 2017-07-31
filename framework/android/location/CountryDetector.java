package android.location;

import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import java.util.HashMap;

public class CountryDetector
{
  private static final String TAG = "CountryDetector";
  private final HashMap<CountryListener, ListenerTransport> mListeners;
  private final ICountryDetector mService;
  
  public CountryDetector(ICountryDetector paramICountryDetector)
  {
    this.mService = paramICountryDetector;
    this.mListeners = new HashMap();
  }
  
  public void addCountryListener(CountryListener paramCountryListener, Looper paramLooper)
  {
    synchronized (this.mListeners)
    {
      if (!this.mListeners.containsKey(paramCountryListener)) {
        paramLooper = new ListenerTransport(paramCountryListener, paramLooper);
      }
      try
      {
        this.mService.addCountryListener(paramLooper);
        this.mListeners.put(paramCountryListener, paramLooper);
        return;
      }
      catch (RemoteException paramCountryListener)
      {
        for (;;)
        {
          Log.e("CountryDetector", "addCountryListener: RemoteException", paramCountryListener);
        }
      }
    }
  }
  
  public Country detectCountry()
  {
    try
    {
      Country localCountry = this.mService.detectCountry();
      return localCountry;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("CountryDetector", "detectCountry: RemoteException", localRemoteException);
    }
    return null;
  }
  
  public void removeCountryListener(CountryListener paramCountryListener)
  {
    synchronized (this.mListeners)
    {
      ListenerTransport localListenerTransport = (ListenerTransport)this.mListeners.get(paramCountryListener);
      if (localListenerTransport != null) {}
      try
      {
        this.mListeners.remove(paramCountryListener);
        this.mService.removeCountryListener(localListenerTransport);
        return;
      }
      catch (RemoteException paramCountryListener)
      {
        for (;;)
        {
          Log.e("CountryDetector", "removeCountryListener: RemoteException", paramCountryListener);
        }
      }
    }
  }
  
  private static final class ListenerTransport
    extends ICountryListener.Stub
  {
    private final Handler mHandler;
    private final CountryListener mListener;
    
    public ListenerTransport(CountryListener paramCountryListener, Looper paramLooper)
    {
      this.mListener = paramCountryListener;
      if (paramLooper != null)
      {
        this.mHandler = new Handler(paramLooper);
        return;
      }
      this.mHandler = new Handler();
    }
    
    public void onCountryDetected(final Country paramCountry)
    {
      this.mHandler.post(new Runnable()
      {
        public void run()
        {
          CountryDetector.ListenerTransport.-get0(CountryDetector.ListenerTransport.this).onCountryDetected(paramCountry);
        }
      });
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/location/CountryDetector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */