package com.amap.api.location;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.amap.api.location.core.AMapLocException;
import com.amap.api.location.core.c;
import com.amap.api.location.core.d;
import com.aps.l;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class b
  implements AMapLocationListener
{
  a a = null;
  AMapLocalWeatherListener b;
  a c;
  private Context d;
  private int e;
  private AMapLocalWeatherListener f;
  
  public b(a parama, Context paramContext)
  {
    this.d = paramContext;
    this.c = parama;
    this.a = new a(this, paramContext.getMainLooper());
  }
  
  /* Error */
  private AMapLocalWeatherLive a(String paramString, AMapLocation paramAMapLocation)
    throws JSONException
  {
    // Byte code:
    //   0: new 54	com/amap/api/location/AMapLocalWeatherLive
    //   3: dup
    //   4: invokespecial 55	com/amap/api/location/AMapLocalWeatherLive:<init>	()V
    //   7: astore_3
    //   8: aload_1
    //   9: invokestatic 60	com/amap/api/location/core/d:a	(Ljava/lang/String;)V
    //   12: new 62	org/json/JSONObject
    //   15: dup
    //   16: aload_1
    //   17: invokespecial 64	org/json/JSONObject:<init>	(Ljava/lang/String;)V
    //   20: ldc 66
    //   22: invokevirtual 70	org/json/JSONObject:getJSONArray	(Ljava/lang/String;)Lorg/json/JSONArray;
    //   25: astore_1
    //   26: aload_1
    //   27: ifnonnull +16 -> 43
    //   30: aload_3
    //   31: areturn
    //   32: astore 4
    //   34: aload_3
    //   35: aload 4
    //   37: invokevirtual 73	com/amap/api/location/AMapLocalWeatherLive:a	(Lcom/amap/api/location/core/AMapLocException;)V
    //   40: goto -28 -> 12
    //   43: aload_1
    //   44: invokevirtual 79	org/json/JSONArray:length	()I
    //   47: ifle -17 -> 30
    //   50: aload_1
    //   51: iconst_0
    //   52: invokevirtual 83	org/json/JSONArray:get	(I)Ljava/lang/Object;
    //   55: checkcast 62	org/json/JSONObject
    //   58: astore 8
    //   60: aload_0
    //   61: aload 8
    //   63: ldc 85
    //   65: invokevirtual 88	com/amap/api/location/b:a	(Lorg/json/JSONObject;Ljava/lang/String;)Ljava/lang/String;
    //   68: astore_1
    //   69: aload_0
    //   70: aload 8
    //   72: ldc 90
    //   74: invokevirtual 88	com/amap/api/location/b:a	(Lorg/json/JSONObject;Ljava/lang/String;)Ljava/lang/String;
    //   77: astore 4
    //   79: aload_0
    //   80: aload 8
    //   82: ldc 92
    //   84: invokevirtual 88	com/amap/api/location/b:a	(Lorg/json/JSONObject;Ljava/lang/String;)Ljava/lang/String;
    //   87: astore 5
    //   89: aload_0
    //   90: aload 8
    //   92: ldc 94
    //   94: invokevirtual 88	com/amap/api/location/b:a	(Lorg/json/JSONObject;Ljava/lang/String;)Ljava/lang/String;
    //   97: astore 6
    //   99: aload_0
    //   100: aload 8
    //   102: ldc 96
    //   104: invokevirtual 88	com/amap/api/location/b:a	(Lorg/json/JSONObject;Ljava/lang/String;)Ljava/lang/String;
    //   107: astore 7
    //   109: aload_0
    //   110: aload 8
    //   112: ldc 98
    //   114: invokevirtual 88	com/amap/api/location/b:a	(Lorg/json/JSONObject;Ljava/lang/String;)Ljava/lang/String;
    //   117: astore 8
    //   119: aload_3
    //   120: aload_1
    //   121: invokevirtual 99	com/amap/api/location/AMapLocalWeatherLive:a	(Ljava/lang/String;)V
    //   124: aload_3
    //   125: aload 8
    //   127: invokevirtual 101	com/amap/api/location/AMapLocalWeatherLive:f	(Ljava/lang/String;)V
    //   130: aload_3
    //   131: aload 7
    //   133: invokevirtual 103	com/amap/api/location/AMapLocalWeatherLive:e	(Ljava/lang/String;)V
    //   136: aload_3
    //   137: aload 4
    //   139: invokevirtual 105	com/amap/api/location/AMapLocalWeatherLive:b	(Ljava/lang/String;)V
    //   142: aload_3
    //   143: aload 5
    //   145: invokevirtual 107	com/amap/api/location/AMapLocalWeatherLive:c	(Ljava/lang/String;)V
    //   148: aload_3
    //   149: aload 6
    //   151: invokevirtual 109	com/amap/api/location/AMapLocalWeatherLive:d	(Ljava/lang/String;)V
    //   154: aload_3
    //   155: aload_2
    //   156: invokevirtual 115	com/amap/api/location/AMapLocation:getCity	()Ljava/lang/String;
    //   159: invokevirtual 118	com/amap/api/location/AMapLocalWeatherLive:setCity	(Ljava/lang/String;)V
    //   162: aload_3
    //   163: aload_2
    //   164: invokevirtual 121	com/amap/api/location/AMapLocation:getCityCode	()Ljava/lang/String;
    //   167: invokevirtual 124	com/amap/api/location/AMapLocalWeatherLive:setCityCode	(Ljava/lang/String;)V
    //   170: aload_3
    //   171: aload_2
    //   172: invokevirtual 127	com/amap/api/location/AMapLocation:getProvince	()Ljava/lang/String;
    //   175: invokevirtual 130	com/amap/api/location/AMapLocalWeatherLive:setProvince	(Ljava/lang/String;)V
    //   178: aload_3
    //   179: areturn
    //   180: astore_1
    //   181: aload_1
    //   182: invokevirtual 133	java/lang/Exception:printStackTrace	()V
    //   185: aload_3
    //   186: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	187	0	this	b
    //   0	187	1	paramString	String
    //   0	187	2	paramAMapLocation	AMapLocation
    //   7	179	3	localAMapLocalWeatherLive	AMapLocalWeatherLive
    //   32	4	4	localAMapLocException	AMapLocException
    //   77	61	4	str1	String
    //   87	57	5	str2	String
    //   97	53	6	str3	String
    //   107	25	7	str4	String
    //   58	68	8	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   8	12	32	com/amap/api/location/core/AMapLocException
    //   12	26	180	java/lang/Exception
    //   43	178	180	java/lang/Exception
  }
  
  private String a()
  {
    return "http://restapi.amap.com/v3/weather/weatherInfo?";
  }
  
  private byte[] a(AMapLocation paramAMapLocation, String paramString)
    throws UnsupportedEncodingException
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("output=json&ec=1").append("&extensions=" + paramString).append("&city=").append(paramAMapLocation.getAdCode());
    localStringBuffer.append("&key=" + c.a());
    return com.amap.api.location.core.a.b(com.amap.api.location.core.a.a(localStringBuffer.toString())).getBytes("utf-8");
  }
  
  private AMapLocalWeatherForecast b(String paramString, AMapLocation paramAMapLocation)
    throws JSONException
  {
    AMapLocalWeatherForecast localAMapLocalWeatherForecast = new AMapLocalWeatherForecast();
    ArrayList localArrayList;
    int i;
    try
    {
      d.a(paramString);
      paramString = new JSONObject(paramString).getJSONArray("forecasts");
      if (paramString == null) {
        return localAMapLocalWeatherForecast;
      }
    }
    catch (AMapLocException localAMapLocException)
    {
      do
      {
        do
        {
          for (;;)
          {
            localAMapLocalWeatherForecast.a(localAMapLocException);
            localAMapLocException.printStackTrace();
          }
        } while (paramString.length() <= 0);
        paramString = (JSONObject)paramString.get(0);
        localAMapLocalWeatherForecast.a(a(paramString, "reporttime"));
        paramString = paramString.getJSONArray("casts");
      } while ((paramString == null) || (paramString.length() <= 0));
      localArrayList = new ArrayList();
      i = 0;
    }
    for (;;)
    {
      if (i >= paramString.length())
      {
        localAMapLocalWeatherForecast.a(localArrayList);
        return localAMapLocalWeatherForecast;
      }
      AMapLocalDayWeatherForecast localAMapLocalDayWeatherForecast = new AMapLocalDayWeatherForecast();
      Object localObject = (JSONObject)paramString.get(i);
      String str1 = a((JSONObject)localObject, "date");
      String str2 = a((JSONObject)localObject, "week");
      String str3 = a((JSONObject)localObject, "dayweather");
      String str4 = a((JSONObject)localObject, "nightweather");
      String str5 = a((JSONObject)localObject, "daytemp");
      String str6 = a((JSONObject)localObject, "nighttemp");
      String str7 = a((JSONObject)localObject, "daywind");
      String str8 = a((JSONObject)localObject, "nightwind");
      String str9 = a((JSONObject)localObject, "daypower");
      localObject = a((JSONObject)localObject, "nightpower");
      localAMapLocalDayWeatherForecast.a(str1);
      localAMapLocalDayWeatherForecast.b(str2);
      localAMapLocalDayWeatherForecast.c(str3);
      localAMapLocalDayWeatherForecast.d(str4);
      localAMapLocalDayWeatherForecast.e(str5);
      localAMapLocalDayWeatherForecast.f(str6);
      localAMapLocalDayWeatherForecast.g(str7);
      localAMapLocalDayWeatherForecast.h(str8);
      localAMapLocalDayWeatherForecast.i(str9);
      localAMapLocalDayWeatherForecast.j((String)localObject);
      localAMapLocalDayWeatherForecast.setCity(paramAMapLocation.getCity());
      localAMapLocalDayWeatherForecast.setCityCode(paramAMapLocation.getCityCode());
      localAMapLocalDayWeatherForecast.setProvince(paramAMapLocation.getProvince());
      localArrayList.add(localAMapLocalDayWeatherForecast);
      i += 1;
    }
  }
  
  protected String a(JSONObject paramJSONObject, String paramString)
    throws JSONException
  {
    if (paramJSONObject != null) {
      if (paramJSONObject.has(paramString)) {
        break label20;
      }
    }
    label20:
    while (paramJSONObject.getString(paramString).equals("[]"))
    {
      return "";
      return "";
    }
    return paramJSONObject.optString(paramString);
  }
  
  void a(int paramInt, AMapLocalWeatherListener paramAMapLocalWeatherListener, AMapLocation paramAMapLocation)
  {
    try
    {
      this.e = paramInt;
      this.f = paramAMapLocalWeatherListener;
      if (paramAMapLocation != null)
      {
        if (paramInt != 1) {
          break label69;
        }
      }
      else
      {
        this.c.a(-1L, 10.0F, this, "lbs", true);
        return;
      }
    }
    catch (Throwable paramAMapLocalWeatherListener)
    {
      paramAMapLocalWeatherListener.printStackTrace();
      return;
    }
    a(paramAMapLocation, "base", paramAMapLocalWeatherListener);
    label69:
    while (paramInt == 2)
    {
      a(paramAMapLocation, "all", paramAMapLocalWeatherListener);
      return;
    }
  }
  
  void a(AMapLocation paramAMapLocation, String paramString, AMapLocalWeatherListener paramAMapLocalWeatherListener)
    throws Exception
  {
    this.b = paramAMapLocalWeatherListener;
    Object localObject1;
    Object localObject2;
    if (paramAMapLocation != null)
    {
      localObject1 = a(paramAMapLocation, paramString);
      localObject2 = a();
      paramAMapLocalWeatherListener = new AMapLocException();
    }
    try
    {
      localObject1 = l.a().a(this.d, (String)localObject2, (byte[])localObject1, "sea");
      if (!"base".equals(paramString))
      {
        if ("all".equals(paramString)) {
          break label180;
        }
        return;
      }
    }
    catch (AMapLocException paramAMapLocalWeatherListener)
    {
      for (;;)
      {
        localObject1 = null;
      }
      if (localObject1 == null)
      {
        localObject2 = new AMapLocalWeatherLive();
        paramAMapLocalWeatherListener = new AMapLocException("http连接失败 - ConnectionException");
      }
      for (;;)
      {
        ((AMapLocalWeatherLive)localObject2).a(paramAMapLocalWeatherListener);
        ((AMapLocalWeatherLive)localObject2).setCity(paramAMapLocation.getCity());
        ((AMapLocalWeatherLive)localObject2).setCityCode(paramAMapLocation.getCityCode());
        ((AMapLocalWeatherLive)localObject2).setProvince(paramAMapLocation.getProvince());
        Message localMessage = Message.obtain();
        localMessage.what = 1;
        localMessage.obj = localObject2;
        this.a.sendMessage(localMessage);
        break;
        localObject2 = a((String)localObject1, paramAMapLocation);
      }
    }
    label180:
    if (localObject1 == null)
    {
      paramAMapLocation = new AMapLocalWeatherForecast();
      paramAMapLocalWeatherListener = new AMapLocException("http连接失败 - ConnectionException");
    }
    for (;;)
    {
      paramAMapLocation.a(paramAMapLocalWeatherListener);
      paramString = Message.obtain();
      paramString.what = 2;
      paramString.obj = paramAMapLocation;
      this.a.sendMessage(paramString);
      return;
      paramAMapLocation = b((String)localObject1, paramAMapLocation);
    }
  }
  
  public void onLocationChanged(Location paramLocation) {}
  
  public void onLocationChanged(AMapLocation paramAMapLocation)
  {
    if (paramAMapLocation == null) {}
    Object localObject1;
    for (;;)
    {
      try
      {
        this.c.a(this);
        paramAMapLocation = Message.obtain();
        paramAMapLocation.what = this.e;
        localObject1 = new AMapLocException("定位失败无法获取城市信息");
        if (1 == this.e) {
          break label124;
        }
        if (2 == this.e) {
          break;
        }
        return;
      }
      catch (Throwable paramAMapLocation)
      {
        paramAMapLocation.printStackTrace();
        return;
      }
      if ((paramAMapLocation.getAMapException() != null) && (paramAMapLocation.getAMapException().getErrorCode() == 0) && (paramAMapLocation.getAdCode() != null) && (paramAMapLocation.getAdCode().length() > 0))
      {
        this.c.a(this);
        localObject1 = Message.obtain();
        ((Message)localObject1).what = 3;
        ((Message)localObject1).obj = paramAMapLocation;
        this.a.sendMessage((Message)localObject1);
        return;
        label124:
        localObject2 = new AMapLocalWeatherLive();
        ((AMapLocalWeatherLive)localObject2).a((AMapLocException)localObject1);
        paramAMapLocation.obj = localObject2;
        this.a.sendMessage(paramAMapLocation);
      }
    }
    Object localObject2 = new AMapLocalWeatherForecast();
    ((AMapLocalWeatherForecast)localObject2).a((AMapLocException)localObject1);
    paramAMapLocation.obj = localObject2;
    this.a.sendMessage(paramAMapLocation);
  }
  
  public void onProviderDisabled(String paramString) {}
  
  public void onProviderEnabled(String paramString) {}
  
  public void onStatusChanged(String paramString, int paramInt, Bundle paramBundle) {}
  
  static class a
    extends Handler
  {
    private WeakReference<b> a;
    
    a(b paramb, Looper paramLooper)
    {
      super();
      try
      {
        this.a = new WeakReference(paramb);
        return;
      }
      catch (Throwable paramb)
      {
        paramb.printStackTrace();
      }
    }
    
    public void handleMessage(Message paramMessage)
    {
      final b localb;
      try
      {
        super.handleMessage(paramMessage);
        localb = (b)this.a.get();
        switch (paramMessage.what)
        {
        case 1: 
          if (localb.b == null) {
            return;
          }
          localb.b.onWeatherLiveSearched((AMapLocalWeatherLive)paramMessage.obj);
          return;
        }
      }
      catch (Throwable paramMessage)
      {
        paramMessage.printStackTrace();
        return;
      }
      if (localb.b != null)
      {
        localb.b.onWeatherForecaseSearched((AMapLocalWeatherForecast)paramMessage.obj);
        return;
        try
        {
          new Thread()
          {
            public void run()
            {
              try
              {
                if (b.a(localb) != 1) {}
                while (b.a(localb) != 2)
                {
                  return;
                  localb.a(this.b, "base", b.b(localb));
                }
                localb.a(this.b, "all", b.b(localb));
              }
              catch (Throwable localThrowable)
              {
                localThrowable.printStackTrace();
                return;
              }
            }
          }.start();
          return;
        }
        catch (Throwable paramMessage)
        {
          paramMessage.printStackTrace();
          return;
        }
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/location/b.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */