package com.android.server.location;

import android.content.Context;
import android.location.Country;
import android.location.CountryListener;
import android.location.Geocoder;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings.Global;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Slog;
import java.util.Iterator;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ComprehensiveCountryDetector
  extends CountryDetectorBase
{
  static final boolean DEBUG = false;
  private static final long LOCATION_REFRESH_INTERVAL = 86400000L;
  private static final int MAX_LENGTH_DEBUG_LOGS = 20;
  private static final String TAG = "CountryDetector";
  private int mCountServiceStateChanges;
  private Country mCountry;
  private Country mCountryFromLocation;
  private final ConcurrentLinkedQueue<Country> mDebugLogs = new ConcurrentLinkedQueue();
  private Country mLastCountryAddedToLogs;
  private CountryListener mLocationBasedCountryDetectionListener = new CountryListener()
  {
    public void onCountryDetected(Country paramAnonymousCountry)
    {
      ComprehensiveCountryDetector.-set1(ComprehensiveCountryDetector.this, paramAnonymousCountry);
      ComprehensiveCountryDetector.-wrap0(ComprehensiveCountryDetector.this, true, false);
      ComprehensiveCountryDetector.-wrap2(ComprehensiveCountryDetector.this);
    }
  };
  protected CountryDetectorBase mLocationBasedCountryDetector;
  protected Timer mLocationRefreshTimer;
  private final Object mObject = new Object();
  private PhoneStateListener mPhoneStateListener;
  private long mStartTime;
  private long mStopTime;
  private boolean mStopped = false;
  private final TelephonyManager mTelephonyManager;
  private int mTotalCountServiceStateChanges;
  private long mTotalTime;
  
  public ComprehensiveCountryDetector(Context paramContext)
  {
    super(paramContext);
    this.mTelephonyManager = ((TelephonyManager)paramContext.getSystemService("phone"));
  }
  
  private void addToLogs(Country paramCountry)
  {
    if (paramCountry == null) {
      return;
    }
    synchronized (this.mObject)
    {
      if (this.mLastCountryAddedToLogs != null)
      {
        boolean bool = this.mLastCountryAddedToLogs.equals(paramCountry);
        if (bool) {
          return;
        }
      }
      this.mLastCountryAddedToLogs = paramCountry;
      if (this.mDebugLogs.size() >= 20) {
        this.mDebugLogs.poll();
      }
      this.mDebugLogs.add(paramCountry);
      return;
    }
  }
  
  private void cancelLocationRefresh()
  {
    try
    {
      if (this.mLocationRefreshTimer != null)
      {
        this.mLocationRefreshTimer.cancel();
        this.mLocationRefreshTimer = null;
      }
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  private Country detectCountry(boolean paramBoolean1, boolean paramBoolean2)
  {
    Country localCountry2 = getCountry();
    if (this.mCountry != null) {}
    for (Country localCountry1 = new Country(this.mCountry);; localCountry1 = this.mCountry)
    {
      runAfterDetectionAsync(localCountry1, localCountry2, paramBoolean1, paramBoolean2);
      this.mCountry = localCountry2;
      return this.mCountry;
    }
  }
  
  private Country getCountry()
  {
    Object localObject2 = getNetworkBasedCountry();
    Object localObject1 = localObject2;
    if (localObject2 == null) {
      localObject1 = getLastKnownLocationBasedCountry();
    }
    localObject2 = localObject1;
    if (localObject1 == null) {
      localObject2 = getSimBasedCountry();
    }
    localObject1 = localObject2;
    if (localObject2 == null) {
      localObject1 = getLocaleCountry();
    }
    addToLogs((Country)localObject1);
    return (Country)localObject1;
  }
  
  private boolean isNetworkCountryCodeAvailable()
  {
    return this.mTelephonyManager.getPhoneType() == 1;
  }
  
  private void notifyIfCountryChanged(Country paramCountry1, Country paramCountry2)
  {
    if ((paramCountry2 == null) || (this.mListener == null) || ((paramCountry1 != null) && (paramCountry1.equals(paramCountry2)))) {
      return;
    }
    notifyListener(paramCountry2);
  }
  
  private void scheduleLocationRefresh()
  {
    try
    {
      Timer localTimer = this.mLocationRefreshTimer;
      if (localTimer != null) {
        return;
      }
      this.mLocationRefreshTimer = new Timer();
      this.mLocationRefreshTimer.schedule(new TimerTask()
      {
        public void run()
        {
          ComprehensiveCountryDetector.this.mLocationRefreshTimer = null;
          ComprehensiveCountryDetector.-wrap0(ComprehensiveCountryDetector.this, false, true);
        }
      }, 86400000L);
      return;
    }
    finally {}
  }
  
  private void startLocationBasedDetector(CountryListener paramCountryListener)
  {
    try
    {
      CountryDetectorBase localCountryDetectorBase = this.mLocationBasedCountryDetector;
      if (localCountryDetectorBase != null) {
        return;
      }
      this.mLocationBasedCountryDetector = createLocationBasedCountryDetector();
      this.mLocationBasedCountryDetector.setCountryListener(paramCountryListener);
      this.mLocationBasedCountryDetector.detectCountry();
      return;
    }
    finally {}
  }
  
  private void stopLocationBasedDetector()
  {
    try
    {
      if (this.mLocationBasedCountryDetector != null)
      {
        this.mLocationBasedCountryDetector.stop();
        this.mLocationBasedCountryDetector = null;
      }
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  protected void addPhoneStateListener()
  {
    try
    {
      if (this.mPhoneStateListener == null)
      {
        this.mPhoneStateListener = new PhoneStateListener()
        {
          public void onServiceStateChanged(ServiceState paramAnonymousServiceState)
          {
            paramAnonymousServiceState = ComprehensiveCountryDetector.this;
            ComprehensiveCountryDetector.-set0(paramAnonymousServiceState, ComprehensiveCountryDetector.-get0(paramAnonymousServiceState) + 1);
            paramAnonymousServiceState = ComprehensiveCountryDetector.this;
            ComprehensiveCountryDetector.-set2(paramAnonymousServiceState, ComprehensiveCountryDetector.-get1(paramAnonymousServiceState) + 1);
            if (!ComprehensiveCountryDetector.-wrap1(ComprehensiveCountryDetector.this)) {
              return;
            }
            ComprehensiveCountryDetector.-wrap0(ComprehensiveCountryDetector.this, true, true);
          }
        };
        this.mTelephonyManager.listen(this.mPhoneStateListener, 1);
      }
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  protected CountryDetectorBase createLocationBasedCountryDetector()
  {
    return new LocationBasedCountryDetector(this.mContext);
  }
  
  public Country detectCountry()
  {
    if (this.mStopped) {}
    for (boolean bool = false;; bool = true) {
      return detectCountry(false, bool);
    }
  }
  
  protected Country getLastKnownLocationBasedCountry()
  {
    return this.mCountryFromLocation;
  }
  
  protected Country getLocaleCountry()
  {
    Locale localLocale = Locale.getDefault();
    if (localLocale != null) {
      return new Country(localLocale.getCountry(), 3);
    }
    return null;
  }
  
  protected Country getNetworkBasedCountry()
  {
    if (isNetworkCountryCodeAvailable())
    {
      String str = this.mTelephonyManager.getNetworkCountryIso();
      if (!TextUtils.isEmpty(str)) {
        return new Country(str, 0);
      }
    }
    return null;
  }
  
  protected Country getSimBasedCountry()
  {
    String str = this.mTelephonyManager.getSimCountryIso();
    if (!TextUtils.isEmpty(str)) {
      return new Country(str, 2);
    }
    return null;
  }
  
  protected boolean isAirplaneModeOff()
  {
    boolean bool = false;
    if (Settings.Global.getInt(this.mContext.getContentResolver(), "airplane_mode_on", 0) == 0) {
      bool = true;
    }
    return bool;
  }
  
  protected boolean isGeoCoderImplemented()
  {
    return Geocoder.isPresent();
  }
  
  protected void removePhoneStateListener()
  {
    try
    {
      if (this.mPhoneStateListener != null)
      {
        this.mTelephonyManager.listen(this.mPhoneStateListener, 0);
        this.mPhoneStateListener = null;
      }
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  void runAfterDetection(Country paramCountry1, Country paramCountry2, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramBoolean1) {
      notifyIfCountryChanged(paramCountry1, paramCountry2);
    }
    if ((paramBoolean2) && ((paramCountry2 == null) || (paramCountry2.getSource() > 1)) && (isAirplaneModeOff()) && (this.mListener != null) && (isGeoCoderImplemented())) {
      startLocationBasedDetector(this.mLocationBasedCountryDetectionListener);
    }
    if ((paramCountry2 == null) || (paramCountry2.getSource() >= 1))
    {
      scheduleLocationRefresh();
      return;
    }
    cancelLocationRefresh();
    stopLocationBasedDetector();
  }
  
  protected void runAfterDetectionAsync(final Country paramCountry1, final Country paramCountry2, final boolean paramBoolean1, final boolean paramBoolean2)
  {
    this.mHandler.post(new Runnable()
    {
      public void run()
      {
        ComprehensiveCountryDetector.this.runAfterDetection(paramCountry1, paramCountry2, paramBoolean1, paramBoolean2);
      }
    });
  }
  
  public void setCountryListener(CountryListener paramCountryListener)
  {
    CountryListener localCountryListener = this.mListener;
    this.mListener = paramCountryListener;
    if (this.mListener == null)
    {
      removePhoneStateListener();
      stopLocationBasedDetector();
      cancelLocationRefresh();
      this.mStopTime = SystemClock.elapsedRealtime();
      this.mTotalTime += this.mStopTime;
    }
    while (localCountryListener != null) {
      return;
    }
    addPhoneStateListener();
    detectCountry(false, true);
    this.mStartTime = SystemClock.elapsedRealtime();
    this.mStopTime = 0L;
    this.mCountServiceStateChanges = 0;
  }
  
  public void stop()
  {
    Slog.i("CountryDetector", "Stop the detector.");
    cancelLocationRefresh();
    removePhoneStateListener();
    stopLocationBasedDetector();
    this.mListener = null;
    this.mStopped = true;
  }
  
  public String toString()
  {
    long l2 = SystemClock.elapsedRealtime();
    long l1 = 0L;
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("ComprehensiveCountryDetector{");
    if (this.mStopTime == 0L)
    {
      l1 = l2 - this.mStartTime;
      localStringBuilder.append("timeRunning=").append(l1).append(", ");
    }
    for (;;)
    {
      localStringBuilder.append("totalCountServiceStateChanges=").append(this.mTotalCountServiceStateChanges).append(", ");
      localStringBuilder.append("currentCountServiceStateChanges=").append(this.mCountServiceStateChanges).append(", ");
      localStringBuilder.append("totalTime=").append(this.mTotalTime + l1).append(", ");
      localStringBuilder.append("currentTime=").append(l2).append(", ");
      localStringBuilder.append("countries=");
      Iterator localIterator = this.mDebugLogs.iterator();
      while (localIterator.hasNext())
      {
        Country localCountry = (Country)localIterator.next();
        localStringBuilder.append("\n   ").append(localCountry.toString());
      }
      localStringBuilder.append("lastRunTimeLength=").append(this.mStopTime - this.mStartTime).append(", ");
    }
    localStringBuilder.append("}");
    return localStringBuilder.toString();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/location/ComprehensiveCountryDetector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */