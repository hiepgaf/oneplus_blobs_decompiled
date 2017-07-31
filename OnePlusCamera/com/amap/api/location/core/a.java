package com.amap.api.location.core;

import android.content.Context;
import android.util.Log;
import com.aps.l;
import java.util.Arrays;
import org.json.JSONObject;

public class a
{
  static String a = "";
  private static int b = -1;
  
  public static int a()
  {
    return b;
  }
  
  public static String a(String paramString)
  {
    Object localObject = paramString.split("&");
    Arrays.sort((Object[])localObject);
    StringBuffer localStringBuffer = new StringBuffer();
    int j = localObject.length;
    int i = 0;
    for (;;)
    {
      if (i >= j)
      {
        localObject = localStringBuffer.toString();
        if (((String)localObject).length() > 1) {
          break;
        }
        return paramString;
      }
      localStringBuffer.append(localObject[i]);
      localStringBuffer.append("&");
      i += 1;
    }
    return (String)((String)localObject).subSequence(0, ((String)localObject).length() - 1);
  }
  
  public static boolean a(Context paramContext)
  {
    for (;;)
    {
      try
      {
        byte[] arrayOfByte = c();
        String str = b();
        paramContext = l.a().a(paramContext, str, arrayOfByte, "loc");
        if (paramContext == null)
        {
          b = 0;
          bool = true;
        }
      }
      catch (Throwable paramContext)
      {
        int i;
        b = 0;
        paramContext.printStackTrace();
        if (b != 1) {
          continue;
        }
      }
      finally
      {
        if (b != 1) {
          continue;
        }
        throw paramContext;
        b = 0;
        continue;
        b = 0;
        continue;
        b = 0;
        continue;
        boolean bool = true;
        continue;
      }
      try
      {
        i = b;
        if (i != 1) {
          continue;
        }
        return bool;
      }
      finally {}
      bool = c(paramContext);
    }
  }
  
  private static String b()
  {
    return "http://apiinit.amap.com/v3/log/init";
  }
  
  public static String b(String paramString)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append(paramString);
    String str = d.a();
    localStringBuffer.append("&ts=" + str);
    localStringBuffer.append("&scode=" + d.a(str, paramString));
    return localStringBuffer.toString();
  }
  
  private static boolean c(String paramString)
  {
    try
    {
      paramString = new JSONObject(paramString);
      if (!paramString.has("status")) {}
      for (;;)
      {
        if (paramString.has("info")) {
          break label86;
        }
        int i = b;
        if (i == 0) {
          break label98;
        }
        if (b == 1) {
          break label110;
        }
        return false;
        i = paramString.getInt("status");
        if (i == 1) {
          break;
        }
        if (i == 0) {
          b = 0;
        }
      }
    }
    catch (Exception paramString)
    {
      for (;;)
      {
        paramString.printStackTrace();
        b = 0;
        continue;
        b = 1;
        continue;
        label86:
        a = paramString.getString("info");
        continue;
        label98:
        Log.i("AuthFailure", a);
      }
    }
    label110:
    return true;
  }
  
  private static byte[] c()
  {
    try
    {
      Object localObject = new StringBuffer();
      ((StringBuffer)localObject).append("resType=json&encode=UTF-8&ec=1");
      localObject = b(a(((StringBuffer)localObject).toString())).toString().getBytes("UTF-8");
      return (byte[])localObject;
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
    return null;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/location/core/a.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */