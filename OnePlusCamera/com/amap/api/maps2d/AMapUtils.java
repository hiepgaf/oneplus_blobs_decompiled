package com.amap.api.maps2d;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import com.amap.api.mapcore2d.ck;
import com.amap.api.mapcore2d.cl;
import com.amap.api.mapcore2d.cn;
import com.amap.api.mapcore2d.cu;
import com.amap.api.mapcore2d.cu.a;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.NaviPara;
import com.amap.api.maps2d.model.PoiPara;
import com.amap.api.maps2d.model.RoutePara;

public class AMapUtils
{
  public static final int BUS_COMFORT = 4;
  public static final int BUS_MONEY_LITTLE = 1;
  public static final int BUS_NO_SUBWAY = 5;
  public static final int BUS_TIME_FIRST = 0;
  public static final int BUS_TRANSFER_LITTLE = 2;
  public static final int BUS_WALK_LITTLE = 3;
  public static final int DRIVING_AVOID_CONGESTION = 4;
  public static final int DRIVING_DEFAULT = 0;
  public static final int DRIVING_NO_HIGHWAY = 3;
  public static final int DRIVING_NO_HIGHWAY_AVOID_CONGESTION = 6;
  public static final int DRIVING_NO_HIGHWAY_AVOID_SHORT_MONEY = 5;
  public static final int DRIVING_NO_HIGHWAY_SAVE_MONEY_AVOID_CONGESTION = 8;
  public static final int DRIVING_SAVE_MONEY = 1;
  public static final int DRIVING_SAVE_MONEY_AVOID_CONGESTION = 7;
  public static final int DRIVING_SHORT_DISTANCE = 2;
  
  private static String a(NaviPara paramNaviPara, Context paramContext)
  {
    return String.format("androidamap://navi?sourceApplication=%s&lat=%f&lon=%f&dev=0&style=%d", new Object[] { cl.b(paramContext), Double.valueOf(paramNaviPara.getTargetPoint().latitude), Double.valueOf(paramNaviPara.getTargetPoint().longitude), Integer.valueOf(paramNaviPara.getNaviStyle()) });
  }
  
  private static String a(PoiPara paramPoiPara, Context paramContext)
  {
    paramContext = String.format("androidamap://arroundpoi?sourceApplication=%s&keywords=%s&dev=0", new Object[] { cl.b(paramContext), paramPoiPara.getKeywords() });
    if (paramPoiPara.getCenter() == null) {
      return paramContext;
    }
    return paramContext + "&lat=" + paramPoiPara.getCenter().latitude + "&lon=" + paramPoiPara.getCenter().longitude;
  }
  
  private static String a(RoutePara paramRoutePara, Context paramContext, int paramInt)
  {
    paramContext = String.format("androidamap://route?sourceApplication=%s&slat=%f&slon=%f&sname=%s&dlat=%f&dlon=%f&dname=%s&dev=0&t=%d", new Object[] { cl.b(paramContext), Double.valueOf(paramRoutePara.getStartPoint().latitude), Double.valueOf(paramRoutePara.getStartPoint().longitude), paramRoutePara.getStartName(), Double.valueOf(paramRoutePara.getEndPoint().latitude), Double.valueOf(paramRoutePara.getEndPoint().longitude), paramRoutePara.getEndName(), Integer.valueOf(paramInt) });
    if (paramInt != 1)
    {
      if (paramInt != 2) {
        return paramContext;
      }
    }
    else {
      return paramContext + "&m=" + paramRoutePara.getTransitRouteStyle();
    }
    return paramContext + "&m=" + paramRoutePara.getDrivingRouteStyle();
  }
  
  private static boolean a(Context paramContext)
  {
    try
    {
      paramContext = paramContext.getPackageManager().getPackageInfo("com.autonavi.minimap", 0);
      if (paramContext == null) {
        return false;
      }
    }
    catch (PackageManager.NameNotFoundException paramContext)
    {
      return false;
    }
    return true;
  }
  
  private static boolean a(RoutePara paramRoutePara)
  {
    if (paramRoutePara.getStartPoint() == null) {}
    while ((paramRoutePara.getEndPoint() == null) || (paramRoutePara.getStartName() == null) || (paramRoutePara.getStartName().trim().length() <= 0) || (paramRoutePara.getEndName() == null) || (paramRoutePara.getEndName().trim().length() <= 0)) {
      return false;
    }
    return true;
  }
  
  private static void b(RoutePara paramRoutePara, Context paramContext, int paramInt)
    throws AMapException
  {
    if (!a(paramContext)) {
      throw new AMapException("移动设备上未安装高德地图或高德地图版本较旧");
    }
    if (!a(paramRoutePara)) {
      throw new AMapException("非法导航参数");
    }
    try
    {
      Intent localIntent = new Intent("android.intent.action.VIEW");
      localIntent.addFlags(276824064);
      localIntent.addCategory("android.intent.category.DEFAULT");
      localIntent.setData(Uri.parse(a(paramRoutePara, paramContext, paramInt)));
      localIntent.setPackage("com.autonavi.minimap");
      new a("oan", paramContext).start();
      paramContext.startActivity(localIntent);
      return;
    }
    catch (Throwable paramRoutePara)
    {
      throw new AMapException("移动设备上未安装高德地图或高德地图版本较旧");
    }
  }
  
  public static float calculateArea(LatLng paramLatLng1, LatLng paramLatLng2)
  {
    if (paramLatLng1 == null) {}
    while (paramLatLng2 == null) {
      try
      {
        throw new AMapException("非法坐标值");
      }
      catch (AMapException paramLatLng1)
      {
        paramLatLng1.printStackTrace();
        return 0.0F;
      }
    }
    double d3 = Math.sin(paramLatLng1.latitude * 3.141592653589793D / 180.0D);
    double d4 = Math.sin(paramLatLng2.latitude * 3.141592653589793D / 180.0D);
    double d2 = (paramLatLng2.longitude - paramLatLng1.longitude) / 360.0D;
    double d1 = d2;
    if (d2 < 0.0D) {
      d1 = d2 + 1.0D;
    }
    return (float)(d1 * ((d3 - d4) * (4.007501668557849E7D * 6378137.0D)));
  }
  
  public static float calculateLineDistance(LatLng paramLatLng1, LatLng paramLatLng2)
  {
    if (paramLatLng1 == null) {}
    while (paramLatLng2 == null) {
      try
      {
        throw new AMapException("非法坐标值");
      }
      catch (AMapException paramLatLng1)
      {
        paramLatLng1.printStackTrace();
        return 0.0F;
      }
    }
    double d4 = paramLatLng1.longitude;
    double d3 = paramLatLng1.latitude;
    double d2 = paramLatLng2.longitude;
    double d1 = paramLatLng2.latitude;
    double d5 = d4 * 0.01745329251994329D;
    double d6 = d3 * 0.01745329251994329D;
    d3 = d2 * 0.01745329251994329D;
    d4 = d1 * 0.01745329251994329D;
    d1 = Math.sin(d5);
    d2 = Math.sin(d6);
    d5 = Math.cos(d5);
    d6 = Math.cos(d6);
    double d7 = Math.sin(d3);
    double d8 = Math.sin(d4);
    d3 = Math.cos(d3);
    d4 = Math.cos(d4);
    paramLatLng1 = new double[3];
    paramLatLng2 = new double[3];
    paramLatLng1[0] = (d5 * d6);
    paramLatLng1[1] = (d6 * d1);
    paramLatLng1[2] = d2;
    paramLatLng2[0] = (d4 * d3);
    paramLatLng2[1] = (d4 * d7);
    paramLatLng2[2] = d8;
    return (float)(Math.asin(Math.sqrt((paramLatLng1[0] - paramLatLng2[0]) * (paramLatLng1[0] - paramLatLng2[0]) + (paramLatLng1[1] - paramLatLng2[1]) * (paramLatLng1[1] - paramLatLng2[1]) + (paramLatLng1[2] - paramLatLng2[2]) * (paramLatLng1[2] - paramLatLng2[2])) / 2.0D) * 1.27420015798544E7D);
  }
  
  public static void getLatestAMapApp(Context paramContext)
  {
    try
    {
      Intent localIntent = new Intent("android.intent.action.VIEW");
      localIntent.addFlags(276824064);
      localIntent.addCategory("android.intent.category.DEFAULT");
      localIntent.setData(Uri.parse("http://wap.amap.com/"));
      new a("glaa", paramContext).start();
      paramContext.startActivity(localIntent);
      return;
    }
    catch (Throwable paramContext)
    {
      paramContext.printStackTrace();
    }
  }
  
  public static void openAMapDrivingRoute(RoutePara paramRoutePara, Context paramContext)
    throws AMapException
  {
    b(paramRoutePara, paramContext, 2);
  }
  
  public static void openAMapNavi(NaviPara paramNaviPara, Context paramContext)
    throws AMapException
  {
    if (!a(paramContext)) {
      throw new AMapException("移动设备上未安装高德地图或高德地图版本较旧");
    }
    if (paramNaviPara.getTargetPoint() == null) {
      throw new AMapException("非法导航参数");
    }
    try
    {
      Intent localIntent = new Intent("android.intent.action.VIEW");
      localIntent.addFlags(276824064);
      localIntent.addCategory("android.intent.category.DEFAULT");
      localIntent.setData(Uri.parse(a(paramNaviPara, paramContext)));
      localIntent.setPackage("com.autonavi.minimap");
      new a("oan", paramContext).start();
      paramContext.startActivity(localIntent);
      return;
    }
    catch (Throwable paramNaviPara)
    {
      throw new AMapException("移动设备上未安装高德地图或高德地图版本较旧");
    }
  }
  
  public static void openAMapPoiNearbySearch(PoiPara paramPoiPara, Context paramContext)
    throws AMapException
  {
    if (!a(paramContext)) {
      throw new AMapException("移动设备上未安装高德地图或高德地图版本较旧");
    }
    if (paramPoiPara.getKeywords() == null) {}
    while (paramPoiPara.getKeywords().trim().length() <= 0) {
      throw new AMapException("非法导航参数");
    }
    try
    {
      Intent localIntent = new Intent("android.intent.action.VIEW");
      localIntent.addFlags(276824064);
      localIntent.addCategory("android.intent.category.DEFAULT");
      localIntent.setData(Uri.parse(a(paramPoiPara, paramContext)));
      localIntent.setPackage("com.autonavi.minimap");
      new a("oan", paramContext).start();
      paramContext.startActivity(localIntent);
      return;
    }
    catch (Throwable paramPoiPara)
    {
      throw new AMapException("移动设备上未安装高德地图或高德地图版本较旧");
    }
  }
  
  public static void openAMapTransitRoute(RoutePara paramRoutePara, Context paramContext)
    throws AMapException
  {
    b(paramRoutePara, paramContext, 1);
  }
  
  public static void openAMapWalkingRoute(RoutePara paramRoutePara, Context paramContext)
    throws AMapException
  {
    b(paramRoutePara, paramContext, 4);
  }
  
  static class a
    extends Thread
  {
    String a = "";
    Context b;
    
    public a(String paramString, Context paramContext)
    {
      this.a = paramString;
      if (paramContext == null) {
        return;
      }
      this.b = paramContext.getApplicationContext();
    }
    
    public void run()
    {
      if (this.b == null) {
        return;
      }
      try
      {
        cu localcu = new cu.a(this.a, "2.9.2", "AMAP_SDK_Android_2DMap_2.9.2").a(new String[] { "com.amap.api.maps" }).a();
        cn.a(this.b, localcu);
        interrupt();
        return;
      }
      catch (ck localck)
      {
        localck.printStackTrace();
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/maps2d/AMapUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */