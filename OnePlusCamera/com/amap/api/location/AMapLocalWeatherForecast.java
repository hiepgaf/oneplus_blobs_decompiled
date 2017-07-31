package com.amap.api.location;

import com.amap.api.location.core.AMapLocException;
import java.util.List;

public class AMapLocalWeatherForecast
{
  private String a;
  private List<AMapLocalDayWeatherForecast> b;
  private AMapLocException c;
  
  void a(AMapLocException paramAMapLocException)
  {
    this.c = paramAMapLocException;
  }
  
  void a(String paramString)
  {
    this.a = paramString;
  }
  
  void a(List<AMapLocalDayWeatherForecast> paramList)
  {
    this.b = paramList;
  }
  
  public AMapLocException getAMapException()
  {
    return this.c;
  }
  
  public String getReportTime()
  {
    return this.a;
  }
  
  public List<AMapLocalDayWeatherForecast> getWeatherForecast()
  {
    return this.b;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/location/AMapLocalWeatherForecast.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */