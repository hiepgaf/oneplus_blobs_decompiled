package com.android.server.twilight;

import android.app.AlarmManager;
import android.app.AlarmManager.OnAlarmListener;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.icu.impl.CalendarAstronomer;
import android.icu.util.Calendar;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings.System;
import android.util.ArrayMap;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.server.SystemService;

public final class TwilightService
  extends SystemService
  implements AlarmManager.OnAlarmListener, Handler.Callback, LocationListener
{
  private static final boolean DEBUG = false;
  private static final int MSG_START_LISTENING = 1;
  private static final int MSG_STOP_LISTENING = 2;
  private static final String TAG = "TwilightService";
  private static Context mContext;
  private static final long mFourHourMillis = 21600000L;
  private static final long mSixHourMillis = 21600000L;
  private static final long mTwuentyHourMillis = 64800000L;
  private AlarmManager mAlarmManager;
  private boolean mBootCompleted;
  private final Handler mHandler;
  private boolean mHasListeners;
  private Boolean mIsGetLocation = Boolean.valueOf(false);
  private Location mLastLocation;
  @GuardedBy("mListeners")
  private TwilightState mLastTwilightState;
  @GuardedBy("mListeners")
  private final ArrayMap<TwilightListener, Handler> mListeners = new ArrayMap();
  private LocationManager mLocationManager;
  private Boolean mPreIsNight = Boolean.valueOf(false);
  private BroadcastReceiver mTimeChangedReceiver;
  
  public TwilightService(Context paramContext)
  {
    super(paramContext);
    mContext = paramContext;
    this.mHandler = new Handler(Looper.getMainLooper(), this);
  }
  
  private static TwilightState calculateTwilightState(Location paramLocation, long paramLong)
  {
    Calendar localCalendar = Calendar.getInstance();
    localCalendar.setTimeInMillis(paramLong);
    localCalendar.set(11, 12);
    localCalendar.set(12, 0);
    localCalendar.set(13, 0);
    localCalendar.set(14, 0);
    boolean bool = false;
    double d2 = 0.0D;
    double d1 = 0.0D;
    Object localObject2 = Boolean.valueOf(false);
    int i;
    int j;
    long l3;
    long l4;
    long l2;
    long l1;
    if (paramLocation != null)
    {
      d2 = paramLocation.getLongitude();
      d1 = paramLocation.getLatitude();
      i = (int)d2 * 100;
      j = (int)d1 * 100;
      if ((i == 10300) && (j == 3000))
      {
        bool = true;
        Slog.i("TwilightService", "isDefaultLocation:" + bool);
      }
    }
    else
    {
      double d4;
      double d3;
      if (paramLocation != null)
      {
        d4 = d1;
        d3 = d2;
        localObject1 = localObject2;
        if (!bool) {}
      }
      else
      {
        i = Settings.System.getIntForUser(mContext.getContentResolver(), "twils-lon", 0, -2);
        j = Settings.System.getIntForUser(mContext.getContentResolver(), "twils-lat", 0, -2);
        d4 = d1;
        d3 = d2;
        localObject1 = localObject2;
        if (i != 0)
        {
          d4 = d1;
          d3 = d2;
          localObject1 = localObject2;
          if (j != 0)
          {
            Slog.i("TwilightService", "used SAVED");
            d3 = i / 1000.0D;
            d4 = j / 1000.0D;
            localObject1 = Boolean.valueOf(true);
          }
        }
      }
      if (((paramLocation == null) || (bool)) && (!((Boolean)localObject1).booleanValue())) {
        break label553;
      }
      j = 0;
      i = (int)(1000.0D * d3);
      int k = (int)(1000.0D * d4);
      Settings.System.putIntForUser(mContext.getContentResolver(), "twils-lon", i, -2);
      Settings.System.putIntForUser(mContext.getContentResolver(), "twils-lat", k, -2);
      Slog.i("TwilightService", "Save loc!");
      paramLocation = new CalendarAstronomer(d3, d4);
      paramLocation.setTime(localCalendar.getTimeInMillis());
      l3 = paramLocation.getSunRiseSet(true);
      l4 = paramLocation.getSunRiseSet(false);
      Object localObject1 = Calendar.getInstance();
      localObject2 = Calendar.getInstance();
      ((Calendar)localObject1).setTimeInMillis(l3);
      ((Calendar)localObject2).setTimeInMillis(l4);
      if (((Calendar)localObject1).get(5) == ((Calendar)localObject2).get(5)) {
        break label651;
      }
      i = 1;
      Slog.e("TwilightService", "Time zone error 0");
      l2 = l4;
      l1 = l3;
      label420:
      paramLocation = new TwilightState(l1, l2);
      Slog.i("TwilightService", "ts:" + paramLocation);
      if (i != 0)
      {
        l1 = localCalendar.getTimeInMillis();
        l2 = l1 - 21600000L;
        l3 = l1 + 21600000L;
        if (paramLong >= l2) {
          break label805;
        }
      }
    }
    label553:
    label651:
    label805:
    for (paramLocation = new TwilightState(l2, l3);; paramLocation = new TwilightState(l1 + 64800000L, l3))
    {
      Slog.i("TwilightService", "Time zone maybe error,Time-fixed:" + paramLocation);
      return paramLocation;
      if ((i == 0) && (j == 0))
      {
        bool = true;
        break;
      }
      bool = false;
      break;
      l1 = localCalendar.getTimeInMillis();
      l2 = l1 - 21600000L;
      l3 = l1 + 21600000L;
      if (paramLong < l2) {}
      for (paramLocation = new TwilightState(l2, l3);; paramLocation = new TwilightState(l1 + 64800000L, l3))
      {
        Slog.i("TwilightService", "isDefaultLocation:" + bool + " Time-fixed:" + paramLocation);
        return paramLocation;
      }
      if (l4 < paramLong)
      {
        localCalendar.add(5, 1);
        paramLocation.setTime(localCalendar.getTimeInMillis());
        l3 = paramLocation.getSunRiseSet(true);
        i = j;
        l1 = l3;
        l2 = l4;
        if (l3 >= paramLong) {
          break label420;
        }
        i = 1;
        Slog.e("TwilightService", "Time zone error 1");
        l1 = l3;
        l2 = l4;
        break label420;
      }
      i = j;
      l1 = l3;
      l2 = l4;
      if (l3 <= paramLong) {
        break label420;
      }
      localCalendar.add(5, -1);
      paramLocation.setTime(localCalendar.getTimeInMillis());
      l4 = paramLocation.getSunRiseSet(false);
      i = j;
      l1 = l3;
      l2 = l4;
      if (l4 <= paramLong) {
        break label420;
      }
      i = 1;
      Slog.e("TwilightService", "Time zone error 2");
      l1 = l3;
      l2 = l4;
      break label420;
    }
  }
  
  private void startListening()
  {
    Slog.d("TwilightService", "startListening");
    this.mLocationManager.requestLocationUpdates("network", 0L, 0.0F, this);
    if (this.mTimeChangedReceiver == null)
    {
      this.mTimeChangedReceiver = new BroadcastReceiver()
      {
        public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
        {
          Slog.d("TwilightService", "onReceive: " + paramAnonymousIntent);
          TwilightService.-wrap1(TwilightService.this, Boolean.valueOf(true));
        }
      };
      IntentFilter localIntentFilter = new IntentFilter("android.intent.action.TIME_SET");
      localIntentFilter.addAction("android.intent.action.TIMEZONE_CHANGED");
      getContext().registerReceiver(this.mTimeChangedReceiver, localIntentFilter);
    }
    updateTwilightState(Boolean.valueOf(true));
  }
  
  private void stopListening()
  {
    Slog.d("TwilightService", "stopListening");
    if (this.mTimeChangedReceiver != null)
    {
      getContext().unregisterReceiver(this.mTimeChangedReceiver);
      this.mTimeChangedReceiver = null;
    }
    if (this.mLastTwilightState != null) {
      this.mAlarmManager.cancel(this);
    }
    this.mLocationManager.removeUpdates(this);
    this.mLastLocation = null;
  }
  
  private void updateTwilightState(final Boolean paramBoolean)
  {
    long l1 = System.currentTimeMillis();
    if (this.mLocationManager.isProviderEnabled("network")) {
      this.mLastLocation = this.mLocationManager.getLastKnownLocation("network");
    }
    final TwilightState localTwilightState = calculateTwilightState(this.mLastLocation, l1);
    if (this.mLastTwilightState == null)
    {
      this.mLastTwilightState = localTwilightState;
      this.mPreIsNight = Boolean.valueOf(this.mLastTwilightState.isNight());
    }
    Slog.i("TwilightService", "1 updateTwilightState: " + localTwilightState + " " + localTwilightState.isNight());
    Slog.i("TwilightService", "2 updateTwilightState: " + this.mLastTwilightState + " " + this.mPreIsNight);
    for (;;)
    {
      long l3;
      long l2;
      synchronized (this.mListeners)
      {
        if ((!paramBoolean.booleanValue()) || ((paramBoolean.booleanValue()) && (this.mPreIsNight.booleanValue() != localTwilightState.isNight())))
        {
          this.mLastTwilightState = localTwilightState;
          this.mPreIsNight = Boolean.valueOf(this.mLastTwilightState.isNight());
          Slog.i("TwilightService", "updateTwilightState:" + paramBoolean + " " + localTwilightState.isNight());
          int i = this.mListeners.size() - 1;
          if (i >= 0)
          {
            paramBoolean = (TwilightListener)this.mListeners.keyAt(i);
            ((Handler)this.mListeners.valueAt(i)).post(new Runnable()
            {
              public void run()
              {
                paramBoolean.onTwilightStateChanged(localTwilightState);
              }
            });
            i -= 1;
            continue;
          }
        }
        this.mLastTwilightState = localTwilightState;
        if (localTwilightState != null)
        {
          l3 = Calendar.getInstance().getTimeInMillis();
          l1 = localTwilightState.sunriseTimeMillis();
          l2 = localTwilightState.sunsetTimeMillis();
          if (l1 >= l2) {
            break label448;
          }
          if (l3 < l1)
          {
            Slog.i("TwilightService", "1 trigger time sunrise:" + localTwilightState);
            this.mAlarmManager.cancel(this);
            if (l1 >= l3) {
              break;
            }
            Slog.e("TwilightService", "time zone maybe error,triggerAtMillis < nowMillis!");
          }
        }
        else
        {
          return;
        }
      }
      l1 = l2;
      Slog.i("TwilightService", "2 trigger time sunset:" + localTwilightState);
      continue;
      label448:
      if (l3 < l2)
      {
        l1 = l2;
        Slog.i("TwilightService", "3 trigger time sunset:" + localTwilightState);
      }
      else
      {
        Slog.i("TwilightService", "4 trigger time sunrise:" + localTwilightState);
      }
    }
    this.mAlarmManager.setExact(1, l1, "TwilightService", this, this.mHandler);
  }
  
  public boolean handleMessage(Message paramMessage)
  {
    switch (paramMessage.what)
    {
    default: 
      return false;
    case 1: 
      if (!this.mHasListeners)
      {
        this.mHasListeners = true;
        if (this.mBootCompleted) {
          startListening();
        }
      }
      return true;
    }
    if (this.mHasListeners)
    {
      this.mHasListeners = false;
      if (this.mBootCompleted) {
        stopListening();
      }
    }
    return true;
  }
  
  public void onAlarm()
  {
    Slog.d("TwilightService", "onAlarm");
    updateTwilightState(Boolean.valueOf(false));
  }
  
  public void onBootPhase(int paramInt)
  {
    if (paramInt == 1000)
    {
      Context localContext = getContext();
      this.mAlarmManager = ((AlarmManager)localContext.getSystemService("alarm"));
      this.mLocationManager = ((LocationManager)localContext.getSystemService("location"));
      this.mBootCompleted = true;
      if (this.mHasListeners) {
        startListening();
      }
    }
  }
  
  public void onLocationChanged(Location paramLocation)
  {
    int i;
    int j;
    if (paramLocation != null)
    {
      i = (int)paramLocation.getLongitude() * 100;
      j = (int)paramLocation.getLatitude() * 100;
      if ((i != 10300) || (j != 3000)) {
        break label153;
      }
      i = 1;
      if (i == 0) {
        break label171;
      }
      Slog.i("TwilightService", "#1 startListening");
      this.mLocationManager.removeUpdates(this);
      this.mLocationManager.requestLocationUpdates("network", 3600000L, 100000.0F, this);
      this.mIsGetLocation = Boolean.valueOf(false);
    }
    for (;;)
    {
      Slog.d("TwilightService", "onLocationChanged: provider=" + paramLocation.getProvider() + " accuracy=" + paramLocation.getAccuracy() + " time=" + paramLocation.getTime());
      this.mLastLocation = paramLocation;
      updateTwilightState(Boolean.valueOf(true));
      return;
      label153:
      if ((i == 0) && (j == 0))
      {
        i = 1;
        break;
      }
      i = 0;
      break;
      label171:
      if (!this.mIsGetLocation.booleanValue())
      {
        Slog.i("TwilightService", "#2 startListening");
        this.mLocationManager.removeUpdates(this);
        this.mLocationManager.requestLocationUpdates("network", 3600000L, 100000.0F, this);
        this.mIsGetLocation = Boolean.valueOf(true);
      }
    }
  }
  
  public void onProviderDisabled(String paramString) {}
  
  public void onProviderEnabled(String paramString) {}
  
  public void onStart()
  {
    publishLocalService(TwilightManager.class, new TwilightManager()
    {
      public TwilightState getLastTwilightState()
      {
        synchronized (TwilightService.-get3(TwilightService.this))
        {
          long l = System.currentTimeMillis();
          TwilightService.-set0(TwilightService.this, TwilightService.-wrap0(TwilightService.-get1(TwilightService.this), l));
          TwilightService.-set1(TwilightService.this, Boolean.valueOf(TwilightService.-get2(TwilightService.this).isNight()));
          Slog.i("TwilightService", "getLastTwilightState:" + TwilightService.-get2(TwilightService.this) + " " + TwilightService.-get4(TwilightService.this));
          TwilightState localTwilightState = TwilightService.-get2(TwilightService.this);
          return localTwilightState;
        }
      }
      
      public void registerListener(TwilightListener paramAnonymousTwilightListener, Handler paramAnonymousHandler)
      {
        synchronized (TwilightService.-get3(TwilightService.this))
        {
          boolean bool = TwilightService.-get3(TwilightService.this).isEmpty();
          TwilightService.-get3(TwilightService.this).put(paramAnonymousTwilightListener, paramAnonymousHandler);
          if (bool)
          {
            bool = TwilightService.-get3(TwilightService.this).isEmpty();
            if (!bool) {}
          }
          else
          {
            return;
          }
          TwilightService.-get0(TwilightService.this).sendEmptyMessage(1);
        }
      }
      
      public void unregisterListener(TwilightListener paramAnonymousTwilightListener)
      {
        synchronized (TwilightService.-get3(TwilightService.this))
        {
          boolean bool = TwilightService.-get3(TwilightService.this).isEmpty();
          TwilightService.-get3(TwilightService.this).remove(paramAnonymousTwilightListener);
          if ((!bool) && (TwilightService.-get3(TwilightService.this).isEmpty())) {
            TwilightService.-get0(TwilightService.this).sendEmptyMessage(2);
          }
          return;
        }
      }
    });
  }
  
  public void onStatusChanged(String paramString, int paramInt, Bundle paramBundle) {}
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/twilight/TwilightService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */