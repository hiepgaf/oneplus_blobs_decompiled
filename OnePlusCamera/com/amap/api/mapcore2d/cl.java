package com.amap.api.mapcore2d;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.Bundle;
import android.text.TextUtils;
import java.security.MessageDigest;
import java.util.Locale;

public class cl
{
  private static String a = "";
  private static String b = "";
  private static String c = "";
  private static String d = "";
  private static String e = null;
  
  public static String a(Context paramContext)
  {
    try
    {
      paramContext = g(paramContext);
      return paramContext;
    }
    catch (Throwable paramContext)
    {
      paramContext.printStackTrace();
    }
    return d;
  }
  
  static void a(String paramString)
  {
    d = paramString;
  }
  
  public static String b(Context paramContext)
  {
    try
    {
      if ("".equals(a))
      {
        PackageManager localPackageManager = paramContext.getPackageManager();
        a = (String)localPackageManager.getApplicationLabel(localPackageManager.getApplicationInfo(paramContext.getPackageName(), 0));
        return a;
      }
      paramContext = a;
      return paramContext;
    }
    catch (Throwable paramContext)
    {
      for (;;)
      {
        cy.a(paramContext, "AppInfo", "getApplicationName");
      }
    }
  }
  
  public static String c(Context paramContext)
  {
    do
    {
      try
      {
        if (b != null) {
          continue;
        }
        b = paramContext.getPackageName();
      }
      catch (Throwable paramContext)
      {
        for (;;)
        {
          cy.a(paramContext, "AppInfo", "getpckn");
        }
      }
      return b;
    } while ("".equals(b));
    paramContext = b;
    return paramContext;
  }
  
  public static String d(Context paramContext)
  {
    try
    {
      if ("".equals(c))
      {
        c = paramContext.getPackageManager().getPackageInfo(paramContext.getPackageName(), 0).versionName;
        if (c != null) {
          return c;
        }
      }
      else
      {
        paramContext = c;
        return paramContext;
      }
    }
    catch (Throwable paramContext)
    {
      for (;;)
      {
        cy.a(paramContext, "AppInfo", "getApplicationVersion");
      }
    }
    return "";
  }
  
  public static String e(Context paramContext)
  {
    int i = 0;
    for (;;)
    {
      try
      {
        PackageInfo localPackageInfo = paramContext.getPackageManager().getPackageInfo(paramContext.getPackageName(), 64);
        Object localObject = localPackageInfo.signatures[0].toByteArray();
        byte[] arrayOfByte = MessageDigest.getInstance("SHA1").digest((byte[])localObject);
        localObject = new StringBuffer();
        if (i >= arrayOfByte.length)
        {
          if (!TextUtils.isEmpty(b))
          {
            paramContext = c(paramContext);
            ((StringBuffer)localObject).append(paramContext);
            e = ((StringBuffer)localObject).toString();
            return e;
          }
        }
        else
        {
          String str = Integer.toHexString(arrayOfByte[i] & 0xFF).toUpperCase(Locale.US);
          if (str.length() != 1)
          {
            ((StringBuffer)localObject).append(str);
            ((StringBuffer)localObject).append(":");
            i += 1;
            continue;
          }
          ((StringBuffer)localObject).append("0");
          continue;
        }
        paramContext = localPackageInfo.packageName;
      }
      catch (Throwable paramContext)
      {
        cy.a(paramContext, "AppInfo", "getpck");
        return e;
      }
    }
  }
  
  public static String f(Context paramContext)
  {
    try
    {
      paramContext = g(paramContext);
      return paramContext;
    }
    catch (Throwable paramContext)
    {
      cy.a(paramContext, "AppInfo", "getKey");
    }
    return d;
  }
  
  private static String g(Context paramContext)
    throws PackageManager.NameNotFoundException
  {
    if (d == null) {}
    while (d.equals(""))
    {
      paramContext = paramContext.getPackageManager().getApplicationInfo(paramContext.getPackageName(), 128);
      if (paramContext != null) {
        break;
      }
      return d;
    }
    for (;;)
    {
      return d;
      if (paramContext.metaData == null) {
        break;
      }
      d = paramContext.metaData.getString("com.amap.api.v2.apikey");
      if (d == null) {
        d = "";
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/cl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */