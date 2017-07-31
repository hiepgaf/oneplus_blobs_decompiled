package com.amap.api.mapcore2d;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Environment;
import android.util.Log;
import com.amap.api.maps2d.MapsInitializer;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class cj
{
  public static double[] a = { 7453.642D, 3742.9905D, 1873.333D, 936.89026D, 468.472D, 234.239D, 117.12D, 58.56D, 29.28D, 14.64D, 7.32D, 3.66D, 1.829D, 0.915D, 0.4575D, 0.228D, 0.1144D };
  
  public static double a(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6)
  {
    return (paramDouble3 - paramDouble1) * (paramDouble6 - paramDouble2) - (paramDouble5 - paramDouble1) * (paramDouble4 - paramDouble2);
  }
  
  public static double a(LatLng paramLatLng1, LatLng paramLatLng2)
  {
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
    return Math.asin(Math.sqrt((paramLatLng1[0] - paramLatLng2[0]) * (paramLatLng1[0] - paramLatLng2[0]) + (paramLatLng1[1] - paramLatLng2[1]) * (paramLatLng1[1] - paramLatLng2[1]) + (paramLatLng1[2] - paramLatLng2[2]) * (paramLatLng1[2] - paramLatLng2[2])) / 2.0D) * 1.27420015798544E7D;
  }
  
  public static float a(float paramFloat)
  {
    float f;
    if (paramFloat < 0.0F) {
      f = 0.0F;
    }
    do
    {
      return f;
      f = paramFloat;
    } while (paramFloat <= 45.0F);
    return 45.0F;
  }
  
  public static int a(Object[] paramArrayOfObject)
  {
    return Arrays.hashCode(paramArrayOfObject);
  }
  
  public static Bitmap a(Bitmap paramBitmap, float paramFloat)
  {
    if (paramBitmap != null) {
      return Bitmap.createScaledBitmap(paramBitmap, (int)(paramBitmap.getWidth() * paramFloat), (int)(paramBitmap.getHeight() * paramFloat), true);
    }
    return null;
  }
  
  public static Bitmap a(String paramString)
  {
    try
    {
      paramString = BitmapDescriptorFactory.class.getResourceAsStream("/assets/" + paramString);
      Bitmap localBitmap = BitmapFactory.decodeStream(paramString);
      paramString.close();
      return localBitmap;
    }
    catch (Throwable paramString)
    {
      a(paramString, "Util", "fromAsset");
    }
    return null;
  }
  
  public static cu a()
  {
    try
    {
      cu localcu = p.p;
      if (localcu != null) {
        return p.p;
      }
      localcu = new cu.a("2dmap", "2.9.2", "AMAP_SDK_Android_2DMap_2.9.2").a(new String[] { "com.amap.api.maps2d", "com.amap.api.mapcore2d" }).a();
      return localcu;
    }
    catch (Throwable localThrowable) {}
    return null;
  }
  
  public static u a(LatLng paramLatLng)
  {
    if (paramLatLng != null) {
      return new u((int)(paramLatLng.latitude * 1000000.0D), (int)(paramLatLng.longitude * 1000000.0D));
    }
    return null;
  }
  
  public static String a(int paramInt)
  {
    if (paramInt >= 1000) {
      return paramInt / 1000 + "km";
    }
    return paramInt + "m";
  }
  
  public static String a(String paramString, Object paramObject)
  {
    return paramString + "=" + String.valueOf(paramObject);
  }
  
  public static String a(String... paramVarArgs)
  {
    int i = 0;
    StringBuilder localStringBuilder = new StringBuilder();
    int k = paramVarArgs.length;
    int j = 0;
    if (i >= k) {
      return localStringBuilder.toString();
    }
    localStringBuilder.append(paramVarArgs[i]);
    if (j == paramVarArgs.length - 1) {}
    for (;;)
    {
      j += 1;
      i += 1;
      break;
      localStringBuilder.append(",");
    }
  }
  
  public static void a(Throwable paramThrowable, String paramString1, String paramString2)
  {
    try
    {
      db localdb = db.a();
      if (localdb == null) {}
      for (;;)
      {
        paramThrowable.printStackTrace();
        return;
        localdb.c(paramThrowable, paramString1, paramString2);
      }
      return;
    }
    catch (Throwable paramThrowable) {}
  }
  
  public static boolean a(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, double paramDouble7, double paramDouble8)
  {
    boolean bool2 = false;
    double d = (paramDouble3 - paramDouble1) * (paramDouble8 - paramDouble6) - (paramDouble4 - paramDouble2) * (paramDouble7 - paramDouble5);
    boolean bool1 = bool2;
    if (d != 0.0D)
    {
      paramDouble7 = ((paramDouble2 - paramDouble6) * (paramDouble7 - paramDouble5) - (paramDouble1 - paramDouble5) * (paramDouble8 - paramDouble6)) / d;
      paramDouble1 = ((paramDouble2 - paramDouble6) * (paramDouble3 - paramDouble1) - (paramDouble1 - paramDouble5) * (paramDouble4 - paramDouble2)) / d;
      bool1 = bool2;
      if (paramDouble7 >= 0.0D)
      {
        bool1 = bool2;
        if (paramDouble7 <= 1.0D)
        {
          bool1 = bool2;
          if (paramDouble1 >= 0.0D)
          {
            bool1 = bool2;
            if (paramDouble1 <= 1.0D) {
              bool1 = true;
            }
          }
        }
      }
    }
    return bool1;
  }
  
  public static boolean a(int paramInt1, int paramInt2)
  {
    if (paramInt1 <= 0) {}
    while (paramInt2 <= 0)
    {
      Log.w("2dmap", "the map must have a size");
      return false;
    }
    return true;
  }
  
  public static boolean a(Context paramContext)
  {
    if (paramContext != null) {}
    try
    {
      paramContext = (ConnectivityManager)paramContext.getSystemService("connectivity");
      if (paramContext != null)
      {
        paramContext = paramContext.getActiveNetworkInfo();
        if (paramContext == null) {
          break label54;
        }
        paramContext = paramContext.getState();
        if (paramContext != null) {
          break label59;
        }
      }
      label54:
      label59:
      NetworkInfo.State localState;
      do
      {
        do
        {
          return false;
          return false;
          return false;
          return false;
        } while (paramContext == NetworkInfo.State.DISCONNECTED);
        localState = NetworkInfo.State.DISCONNECTING;
      } while (paramContext == localState);
      return true;
    }
    finally {}
  }
  
  public static boolean a(LatLng paramLatLng, List<LatLng> paramList)
  {
    double d1 = paramLatLng.longitude;
    double d2 = paramLatLng.latitude;
    double d3 = paramLatLng.latitude;
    if (paramList.size() >= 3) {
      if (!((LatLng)paramList.get(0)).equals(paramList.get(paramList.size() - 1))) {
        break label87;
      }
    }
    int j;
    int i;
    for (;;)
    {
      j = 0;
      i = 0;
      if (j < paramList.size() - 1) {
        break;
      }
      if (i % 2 != 0) {
        break label325;
      }
      return false;
      return false;
      label87:
      paramList.add(paramList.get(0));
    }
    double d4 = ((LatLng)paramList.get(j)).longitude;
    double d5 = ((LatLng)paramList.get(j)).latitude;
    double d6 = ((LatLng)paramList.get(j + 1)).longitude;
    double d7 = ((LatLng)paramList.get(j + 1)).latitude;
    if (!b(d1, d2, d4, d5, d6, d7)) {
      if (Math.abs(d7 - d5) >= 1.0E-9D) {
        break label215;
      }
    }
    for (;;)
    {
      j += 1;
      break;
      return true;
      label215:
      if (!b(d4, d5, d1, d2, 180.0D, d3))
      {
        if (!b(d6, d7, d1, d2, 180.0D, d3)) {
          if (a(d4, d5, d6, d7, d1, d2, 180.0D, d3)) {
            break label316;
          }
        }
      }
      else
      {
        if (d5 > d7)
        {
          i += 1;
          continue;
        }
        continue;
      }
      if (d7 > d5)
      {
        i += 1;
      }
      else
      {
        continue;
        label316:
        i += 1;
      }
    }
    label325:
    return true;
  }
  
  public static boolean a(File paramFile)
    throws IOException, Exception
  {
    if (paramFile == null) {}
    while (!paramFile.exists()) {
      return false;
    }
    paramFile = paramFile.listFiles();
    if (paramFile == null) {
      return true;
    }
    int i = 0;
    label26:
    if (i < paramFile.length)
    {
      if (paramFile[i].isFile()) {
        break label64;
      }
      if (!a(paramFile[i])) {
        break label75;
      }
      paramFile[i].delete();
    }
    label64:
    while (paramFile[i].delete())
    {
      i += 1;
      break label26;
      break;
    }
    return false;
    label75:
    return false;
  }
  
  public static float b(float paramFloat)
  {
    float f;
    if (paramFloat > p.c) {
      f = p.c;
    }
    do
    {
      return f;
      f = paramFloat;
    } while (paramFloat >= p.d);
    return p.d;
  }
  
  public static String b(Context paramContext)
  {
    if (Environment.getExternalStorageState().equals("mounted")) {
      if (MapsInitializer.sdcardDir != null) {
        break label71;
      }
    }
    label71:
    while (MapsInitializer.sdcardDir.equals(""))
    {
      paramContext = new File(Environment.getExternalStorageDirectory(), "AMap");
      if (!paramContext.exists()) {
        break;
      }
      return paramContext.toString() + "/";
      return paramContext.getFilesDir().getPath();
    }
    paramContext = new File(MapsInitializer.sdcardDir);
    if (paramContext.exists())
    {
      label101:
      paramContext = new File(paramContext, "Amap");
      if (!paramContext.exists()) {
        break label160;
      }
    }
    for (;;)
    {
      return paramContext.toString() + "/";
      paramContext.mkdir();
      break;
      paramContext.mkdirs();
      break label101;
      label160:
      paramContext.mkdir();
    }
  }
  
  public static boolean b(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (Math.abs(a(paramDouble1, paramDouble2, paramDouble3, paramDouble4, paramDouble5, paramDouble6)) < 1.0E-9D)
    {
      bool1 = bool2;
      if ((paramDouble1 - paramDouble3) * (paramDouble1 - paramDouble5) <= 0.0D)
      {
        bool1 = bool2;
        if ((paramDouble2 - paramDouble4) * (paramDouble2 - paramDouble6) <= 0.0D) {
          bool1 = true;
        }
      }
    }
    return bool1;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/cj.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */