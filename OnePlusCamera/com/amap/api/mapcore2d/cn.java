package com.amap.api.mapcore2d;

import android.content.Context;
import android.util.Log;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

@Deprecated
public class cn
{
  public static int a = -1;
  public static String b = "";
  private static cu c;
  private static String d = "http://apiinit.amap.com/v3/log/init";
  private static String e = null;
  
  private static String a()
  {
    return d;
  }
  
  private static Map<String, String> a(Context paramContext)
  {
    HashMap localHashMap = new HashMap();
    try
    {
      localHashMap.put("resType", "json");
      localHashMap.put("encode", "UTF-8");
      String str = co.a();
      localHashMap.put("ts", str);
      localHashMap.put("key", cl.f(paramContext));
      localHashMap.put("scode", co.a(paramContext, str, cv.d("resType=json&encode=UTF-8&key=" + cl.f(paramContext))));
      return localHashMap;
    }
    catch (Throwable paramContext)
    {
      cy.a(paramContext, "Auth", "gParams");
    }
    return localHashMap;
  }
  
  public static void a(String paramString)
  {
    cl.a(paramString);
  }
  
  @Deprecated
  public static boolean a(Context paramContext, cu paramcu)
  {
    try
    {
      boolean bool = a(paramContext, paramcu, false);
      return bool;
    }
    finally
    {
      paramContext = finally;
      throw paramContext;
    }
  }
  
  private static boolean a(Context paramContext, cu paramcu, boolean paramBoolean)
  {
    c = paramcu;
    try
    {
      paramcu = a();
      HashMap localHashMap = new HashMap();
      localHashMap.put("Content-Type", "application/x-www-form-urlencoded");
      localHashMap.put("Accept-Encoding", "gzip");
      localHashMap.put("Connection", "Keep-Alive");
      localHashMap.put("User-Agent", c.d());
      localHashMap.put("X-INFO", co.a(paramContext, c, null, paramBoolean));
      localHashMap.put("logversion", "2.1");
      localHashMap.put("platinfo", String.format("platform=Android&sdkversion=%s&product=%s", new Object[] { c.b(), c.a() }));
      dy localdy = dy.a();
      cw localcw = new cw();
      localcw.a(cs.a(paramContext));
      localcw.a(localHashMap);
      localcw.b(a(paramContext));
      localcw.a(paramcu);
      paramBoolean = a(localdy.a(localcw));
      return paramBoolean;
    }
    catch (Throwable paramContext)
    {
      cy.a(paramContext, "Auth", "getAuth");
    }
    return true;
  }
  
  private static boolean a(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte != null)
    {
      for (;;)
      {
        try
        {
          paramArrayOfByte = new JSONObject(cv.a(paramArrayOfByte));
          if (!paramArrayOfByte.has("status"))
          {
            if (!paramArrayOfByte.has("info"))
            {
              if (a == 0) {
                break label113;
              }
              if (a == 1) {
                break;
              }
              return false;
            }
          }
          else
          {
            int i = paramArrayOfByte.getInt("status");
            if (i != 1)
            {
              if (i != 0) {
                continue;
              }
              a = 0;
              continue;
            }
          }
        }
        catch (JSONException paramArrayOfByte)
        {
          cy.a(paramArrayOfByte, "Auth", "lData");
          return false;
          a = 1;
          continue;
        }
        catch (Throwable paramArrayOfByte)
        {
          cy.a(paramArrayOfByte, "Auth", "lData");
          return false;
        }
        b = paramArrayOfByte.getString("info");
        continue;
        label113:
        Log.i("AuthFailure", b);
      }
      return true;
    }
    return true;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/cn.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */