package com.amap.api.maps2d;

import android.content.Context;
import android.os.RemoteException;
import com.amap.api.mapcore2d.cn;

public final class MapsInitializer
{
  private static boolean a = true;
  public static String sdcardDir = "";
  
  public static boolean getNetworkEnable()
  {
    return a;
  }
  
  public static String getVersion()
  {
    return "2.9.2";
  }
  
  public static void initialize(Context paramContext)
    throws RemoteException
  {
    com.amap.api.mapcore2d.aq.a = paramContext.getApplicationContext();
  }
  
  public static void loadWorldGridMap(boolean paramBoolean)
  {
    int i = 0;
    if (!paramBoolean) {
      i = 1;
    }
    com.amap.api.mapcore2d.p.i = i;
  }
  
  public static void replaceURL(String paramString1, String paramString2)
  {
    if (paramString1 == null) {}
    while (paramString1.equals("")) {
      return;
    }
    com.amap.api.mapcore2d.p.h = paramString1;
    com.amap.api.mapcore2d.p.g = paramString2 + "DIY";
    if (!paramString1.contains("openstreetmap")) {
      return;
    }
    com.amap.api.mapcore2d.p.c = 19;
  }
  
  public static void setApiKey(String paramString)
  {
    cn.a(paramString);
  }
  
  public static void setNetworkEnable(boolean paramBoolean)
  {
    a = paramBoolean;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/maps2d/MapsInitializer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */