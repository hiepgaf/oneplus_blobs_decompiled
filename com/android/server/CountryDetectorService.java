package com.android.server;

import android.content.Context;
import android.location.Country;
import android.location.CountryListener;
import android.location.ICountryDetector.Stub;
import android.location.ICountryListener;
import android.os.Handler;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.RemoteException;
import android.util.Slog;
import com.android.internal.os.BackgroundThread;
import com.android.server.location.ComprehensiveCountryDetector;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;

public class CountryDetectorService
  extends ICountryDetector.Stub
  implements Runnable
{
  private static final boolean DEBUG = false;
  private static final String TAG = "CountryDetector";
  private final Context mContext;
  private ComprehensiveCountryDetector mCountryDetector;
  private Handler mHandler;
  private CountryListener mLocationBasedDetectorListener;
  private final HashMap<IBinder, Receiver> mReceivers = new HashMap();
  private boolean mSystemReady;
  
  public CountryDetectorService(Context paramContext)
  {
    this.mContext = paramContext;
  }
  
  private void addListener(ICountryListener paramICountryListener)
  {
    synchronized (this.mReceivers)
    {
      Receiver localReceiver = new Receiver(paramICountryListener);
      try
      {
        paramICountryListener.asBinder().linkToDeath(localReceiver, 0);
        this.mReceivers.put(paramICountryListener.asBinder(), localReceiver);
        if (this.mReceivers.size() == 1)
        {
          Slog.d("CountryDetector", "The first listener is added");
          setCountryListener(this.mLocationBasedDetectorListener);
        }
        return;
      }
      catch (RemoteException paramICountryListener)
      {
        for (;;)
        {
          Slog.e("CountryDetector", "linkToDeath failed:", paramICountryListener);
        }
      }
    }
  }
  
  private void initialize()
  {
    this.mCountryDetector = new ComprehensiveCountryDetector(this.mContext);
    this.mLocationBasedDetectorListener = new CountryListener()
    {
      public void onCountryDetected(final Country paramAnonymousCountry)
      {
        CountryDetectorService.-get1(CountryDetectorService.this).post(new Runnable()
        {
          public void run()
          {
            CountryDetectorService.this.notifyReceivers(paramAnonymousCountry);
          }
        });
      }
    };
  }
  
  private void removeListener(IBinder paramIBinder)
  {
    synchronized (this.mReceivers)
    {
      this.mReceivers.remove(paramIBinder);
      if (this.mReceivers.isEmpty())
      {
        setCountryListener(null);
        Slog.d("CountryDetector", "No listener is left");
      }
      return;
    }
  }
  
  public void addCountryListener(ICountryListener paramICountryListener)
    throws RemoteException
  {
    if (!this.mSystemReady) {
      throw new RemoteException();
    }
    addListener(paramICountryListener);
  }
  
  public Country detectCountry()
  {
    if (!this.mSystemReady) {
      return null;
    }
    return this.mCountryDetector.detectCountry();
  }
  
  protected void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.DUMP", "CountryDetector");
  }
  
  boolean isSystemReady()
  {
    return this.mSystemReady;
  }
  
  protected void notifyReceivers(Country paramCountry)
  {
    synchronized (this.mReceivers)
    {
      Iterator localIterator = this.mReceivers.values().iterator();
      for (;;)
      {
        if (localIterator.hasNext())
        {
          Receiver localReceiver = (Receiver)localIterator.next();
          try
          {
            localReceiver.getListener().onCountryDetected(paramCountry);
          }
          catch (RemoteException localRemoteException)
          {
            Slog.e("CountryDetector", "notifyReceivers failed:", localRemoteException);
          }
        }
      }
    }
  }
  
  public void removeCountryListener(ICountryListener paramICountryListener)
    throws RemoteException
  {
    if (!this.mSystemReady) {
      throw new RemoteException();
    }
    removeListener(paramICountryListener.asBinder());
  }
  
  public void run()
  {
    this.mHandler = new Handler();
    initialize();
    this.mSystemReady = true;
  }
  
  protected void setCountryListener(final CountryListener paramCountryListener)
  {
    this.mHandler.post(new Runnable()
    {
      public void run()
      {
        CountryDetectorService.-get0(CountryDetectorService.this).setCountryListener(paramCountryListener);
      }
    });
  }
  
  void systemRunning()
  {
    BackgroundThread.getHandler().post(this);
  }
  
  private final class Receiver
    implements IBinder.DeathRecipient
  {
    private final IBinder mKey;
    private final ICountryListener mListener;
    
    public Receiver(ICountryListener paramICountryListener)
    {
      this.mListener = paramICountryListener;
      this.mKey = paramICountryListener.asBinder();
    }
    
    public void binderDied()
    {
      CountryDetectorService.-wrap0(CountryDetectorService.this, this.mKey);
    }
    
    public boolean equals(Object paramObject)
    {
      if ((paramObject instanceof Receiver)) {
        return this.mKey.equals(((Receiver)paramObject).mKey);
      }
      return false;
    }
    
    public ICountryListener getListener()
    {
      return this.mListener;
    }
    
    public int hashCode()
    {
      return this.mKey.hashCode();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/CountryDetectorService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */