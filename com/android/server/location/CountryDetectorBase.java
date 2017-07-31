package com.android.server.location;

import android.content.Context;
import android.location.Country;
import android.location.CountryListener;
import android.os.Handler;

public abstract class CountryDetectorBase
{
  protected final Context mContext;
  protected Country mDetectedCountry;
  protected final Handler mHandler;
  protected CountryListener mListener;
  
  public CountryDetectorBase(Context paramContext)
  {
    this.mContext = paramContext;
    this.mHandler = new Handler();
  }
  
  public abstract Country detectCountry();
  
  protected void notifyListener(Country paramCountry)
  {
    if (this.mListener != null) {
      this.mListener.onCountryDetected(paramCountry);
    }
  }
  
  public void setCountryListener(CountryListener paramCountryListener)
  {
    this.mListener = paramCountryListener;
  }
  
  public abstract void stop();
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/location/CountryDetectorBase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */