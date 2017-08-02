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
  
    public static String a(final String s) {
        final String[] split = s.split("&");
        Arrays.sort(split);
        final StringBuffer sb = new StringBuffer();
        for (int length = split.length, i = 0; i < length; ++i) {
            sb.append(split[i]);
            sb.append("&");
        }
        final String string = sb.toString();
        if (string.length() <= 1) {
            return s;
        }
        return (String)string.subSequence(0, string.length() - 1);
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
  
    private static boolean c(final String s) { //this one took a while
        JSONObject jsonObject = new JSONObject(s);
        if (jsonObject.has("status")) {
                com.amap.api.location.core.a.b = jsonObject.getInt("status");
        }
        if (jsonObject.has("info")) {
            com.amap.api.location.core.a.a = jsonObject.getString("info");
        }
        if (com.amap.api.location.core.a.b == 0) {
                Log.i("AuthFailure", com.amap.api.location.core.a.a);
        }
        
        return (com.amap.api.location.core.a.b == 1);
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