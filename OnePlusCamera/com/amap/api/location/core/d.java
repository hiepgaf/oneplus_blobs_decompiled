package com.amap.api.location.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Build.VERSION;
import com.amap.api.location.AMapLocation;
import java.lang.reflect.Method;
import org.json.JSONException;
import org.json.JSONObject;

public class d
{
  public static String a = "";
  public static String b = "";
  static int c = 2048;
  static String d = null;
  private static SharedPreferences e = null;
  private static SharedPreferences.Editor f = null;
  private static Method g;
  
  public static String a()
  {
    try
    {
      String str = String.valueOf(System.currentTimeMillis());
      int i = str.length();
      str = str.substring(0, i - 2) + "1" + str.substring(i - 1);
      return str;
    }
    catch (Throwable localThrowable)
    {
      localThrowable.printStackTrace();
    }
    return null;
  }
  
  public static String a(String paramString1, String paramString2)
  {
    try
    {
      if (d == null) {
        d = c.a(null).i();
      }
      for (;;)
      {
        return g.a(d + ":" + paramString1.substring(0, paramString1.length() - 3) + ":" + paramString2);
        int i = d.length();
        if (i == 0) {
          break;
        }
      }
      return null;
    }
    catch (Throwable paramString1)
    {
      paramString1.printStackTrace();
    }
  }
  
  public static void a(Context paramContext, AMapLocation paramAMapLocation)
  {
    for (;;)
    {
      try
      {
        if (e != null)
        {
          if (f != null)
          {
            f.putString("last_know_lat", String.valueOf(paramAMapLocation.getLatitude()));
            f.putString("last_know_lng", String.valueOf(paramAMapLocation.getLongitude()));
            f.putString("province", paramAMapLocation.getProvince());
            f.putString("city", paramAMapLocation.getCity());
            f.putString("district", paramAMapLocation.getDistrict());
            f.putString("cityCode", paramAMapLocation.getCityCode());
            f.putString("adCode", paramAMapLocation.getAdCode());
            f.putFloat("accuracy", paramAMapLocation.getAccuracy());
            f.putLong("time", paramAMapLocation.getTime());
            a(f);
          }
        }
        else
        {
          e = paramContext.getSharedPreferences("last_know_location", 0);
          continue;
        }
        f = e.edit();
      }
      catch (Throwable paramContext)
      {
        paramContext.printStackTrace();
        return;
      }
    }
  }
  
  private static void a(SharedPreferences.Editor paramEditor)
  {
    if (paramEditor != null)
    {
      if (Build.VERSION.SDK_INT < 9) {
        paramEditor.commit();
      }
    }
    else {
      return;
    }
    for (;;)
    {
      try
      {
        if (g != null)
        {
          g.invoke(paramEditor, new Object[0]);
          return;
        }
      }
      catch (Throwable localThrowable)
      {
        localThrowable.printStackTrace();
        paramEditor.commit();
        return;
      }
      g = SharedPreferences.Editor.class.getDeclaredMethod("apply", new Class[0]);
    }
  }
  
  public static void a(String paramString)
    throws AMapLocException
  {
    try
    {
      Object localObject = new JSONObject(paramString);
      if (!((JSONObject)localObject).has("status")) {
        return;
      }
      if (((JSONObject)localObject).has("info"))
      {
        paramString = ((JSONObject)localObject).getString("status");
        localObject = ((JSONObject)localObject).getString("info");
        if (paramString.equals("1")) {
          break label174;
        }
        if (!paramString.equals("0")) {
          return;
        }
        if (((String)localObject).equals("INVALID_USER_KEY")) {}
        while ((((String)localObject).equals("INSUFFICIENT_PRIVILEGES")) || (((String)localObject).equals("USERKEY_PLAT_NOMATCH")) || (((String)localObject).equals("INVALID_USER_SCODE"))) {
          throw new AMapLocException("key鉴权失败");
        }
        if (((String)localObject).equals("SERVICE_NOT_EXIST")) {}
        while ((((String)localObject).equals("SERVICE_RESPONSE_ERROR")) || (((String)localObject).equals("OVER_QUOTA")) || (((String)localObject).equals("UNKNOWN_ERROR"))) {
          throw new AMapLocException("未知的错误");
        }
        if (((String)localObject).equals("INVALID_PARAMS")) {
          throw new AMapLocException("无效的参数 - IllegalArgumentException");
        }
      }
      else
      {
        return;
      }
      return;
      label174:
      return;
    }
    catch (JSONException paramString) {}
  }
  
  public static boolean a(Context paramContext)
  {
    if (paramContext != null)
    {
      try
      {
        paramContext = (ConnectivityManager)paramContext.getSystemService("connectivity");
        if (paramContext == null) {
          break label68;
        }
        paramContext = paramContext.getActiveNetworkInfo();
        if (paramContext == null) {
          break label70;
        }
        paramContext = paramContext.getState();
        if (paramContext == null) {
          return false;
        }
        if (paramContext != NetworkInfo.State.DISCONNECTED)
        {
          NetworkInfo.State localState = NetworkInfo.State.DISCONNECTING;
          if (paramContext != localState) {
            return true;
          }
        }
      }
      catch (Throwable paramContext)
      {
        paramContext.printStackTrace();
        return false;
      }
      return false;
    }
    return false;
    label68:
    return false;
    label70:
    return false;
  }
  
  public static AMapLocation b(Context paramContext)
  {
    try
    {
      paramContext = paramContext.getSharedPreferences("last_know_location", 0);
      AMapLocation localAMapLocation = new AMapLocation("");
      localAMapLocation.setProvider("lbs");
      double d1 = Double.parseDouble(paramContext.getString("last_know_lat", "0.0"));
      double d2 = Double.parseDouble(paramContext.getString("last_know_lng", "0.0"));
      localAMapLocation.setLatitude(d1);
      localAMapLocation.setLongitude(d2);
      localAMapLocation.setProvince(paramContext.getString("province", ""));
      localAMapLocation.setCity(paramContext.getString("city", ""));
      localAMapLocation.setDistrict(paramContext.getString("district", ""));
      localAMapLocation.setCityCode(paramContext.getString("cityCode", ""));
      localAMapLocation.setAdCode(paramContext.getString("adCode", ""));
      localAMapLocation.setAccuracy(paramContext.getFloat("accuracy", 0.0F));
      localAMapLocation.setTime(paramContext.getLong("time", 0L));
      return localAMapLocation;
    }
    catch (Throwable paramContext)
    {
      paramContext.printStackTrace();
    }
    return null;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/location/core/d.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */