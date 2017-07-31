package com.amap.api.location;

public abstract interface AMapLocalWeatherListener
{
  public abstract void onWeatherForecaseSearched(AMapLocalWeatherForecast paramAMapLocalWeatherForecast);
  
  public abstract void onWeatherLiveSearched(AMapLocalWeatherLive paramAMapLocalWeatherLive);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/location/AMapLocalWeatherListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */