package com.amap.api.mapcore2d;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build.VERSION;
import android.os.Environment;
import android.provider.Settings.System;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class cp
{
  private static String a = "";
  private static boolean b = false;
  private static String c = "";
  private static String d = "";
  private static String e = "";
  private static String f = "";
  
  public static String a(Context paramContext)
  {
    try
    {
      paramContext = u(paramContext);
      return paramContext;
    }
    catch (Throwable paramContext)
    {
      paramContext.printStackTrace();
    }
    return "";
  }
  
  private static List<ScanResult> a(List<ScanResult> paramList)
  {
    int k = paramList.size();
    int i = 0;
    int j;
    for (;;)
    {
      if (i >= k - 1) {
        return paramList;
      }
      j = 1;
      if (j < k - i) {
        break;
      }
      i += 1;
    }
    if (((ScanResult)paramList.get(j - 1)).level <= ((ScanResult)paramList.get(j)).level) {}
    for (;;)
    {
      j += 1;
      break;
      ScanResult localScanResult = (ScanResult)paramList.get(j - 1);
      paramList.set(j - 1, paramList.get(j));
      paramList.set(j, localScanResult);
    }
  }
  
  public static void a()
  {
    try
    {
      if (Build.VERSION.SDK_INT <= 14) {
        return;
      }
      TrafficStats.class.getDeclaredMethod("setThreadStatsTag", new Class[] { Integer.TYPE }).invoke(null, new Object[] { Integer.valueOf(40964) });
      return;
    }
    catch (Throwable localThrowable)
    {
      cy.a(localThrowable, "DeviceInfo", "setTraficTag");
    }
  }
  
  private static boolean a(Context paramContext, String paramString)
  {
    if (paramContext == null) {}
    while (paramContext.checkCallingOrSelfPermission(paramString) != 0) {
      return false;
    }
    return true;
  }
  
  public static String b(Context paramContext)
  {
    try
    {
      paramContext = x(paramContext);
      return paramContext;
    }
    catch (Throwable paramContext)
    {
      paramContext.printStackTrace();
    }
    return "";
  }
  
  public static int c(Context paramContext)
  {
    try
    {
      int i = y(paramContext);
      return i;
    }
    catch (Throwable paramContext)
    {
      paramContext.printStackTrace();
    }
    return -1;
  }
  
  public static int d(Context paramContext)
  {
    try
    {
      int i = v(paramContext);
      return i;
    }
    catch (Throwable paramContext)
    {
      paramContext.printStackTrace();
    }
    return -1;
  }
  
  public static String e(Context paramContext)
  {
    try
    {
      paramContext = t(paramContext);
      return paramContext;
    }
    catch (Throwable paramContext)
    {
      paramContext.printStackTrace();
    }
    return "";
  }
  
  public static String f(Context paramContext)
  {
    for (;;)
    {
      try
      {
        if (a == null)
        {
          if (a(paramContext, "android.permission.WRITE_SETTINGS")) {
            continue;
          }
          paramContext = a;
          if (paramContext != null) {
            continue;
          }
        }
      }
      catch (Throwable paramContext)
      {
        boolean bool;
        cy.a(paramContext, "DeviceInfo", "getUTDID");
        continue;
        if ("".equals(a)) {
          continue;
        }
        paramContext = a;
        return paramContext;
      }
      try
      {
        bool = "mounted".equals(Environment.getExternalStorageState());
        if (bool) {
          break label105;
        }
      }
      catch (IOException paramContext)
      {
        paramContext.printStackTrace();
        continue;
      }
      catch (Throwable paramContext)
      {
        cy.a(paramContext, "DeviceInfo", "getUTDID");
        continue;
      }
      if (a == null) {
        break;
      }
      return a;
      if (!"".equals(a))
      {
        return a;
        a = Settings.System.getString(paramContext.getContentResolver(), "mqBRboGZkQPcAkyk");
        continue;
        label105:
        paramContext = Environment.getExternalStorageDirectory().getAbsolutePath();
        paramContext = new File(paramContext + "/.UTSystemConfig/Global/Alvin2.xml");
        if (paramContext.exists()) {
          SAXParserFactory.newInstance().newSAXParser().parse(paramContext, new a());
        }
      }
    }
    return "";
  }
  
  static String g(Context paramContext)
  {
    if (paramContext == null) {}
    for (;;)
    {
      return "";
      try
      {
        if (a(paramContext, "android.permission.ACCESS_WIFI_STATE"))
        {
          paramContext = (WifiManager)paramContext.getSystemService("wifi");
          if (paramContext != null)
          {
            if (!paramContext.isWifiEnabled()) {
              return "";
            }
            paramContext = paramContext.getConnectionInfo().getBSSID();
            return paramContext;
          }
        }
      }
      catch (Throwable paramContext)
      {
        cy.a(paramContext, "DeviceInfo", "getWifiMacs");
        return "";
      }
    }
    return "";
  }
  
  static String h(Context paramContext)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    if (paramContext == null) {}
    do
    {
      try
      {
        return localStringBuilder.toString();
      }
      catch (Throwable paramContext)
      {
        for (;;)
        {
          boolean bool;
          int j;
          int i;
          ScanResult localScanResult;
          cy.a(paramContext, "DeviceInfo", "getWifiMacs");
        }
      }
    } while (!a(paramContext, "android.permission.ACCESS_WIFI_STATE"));
    paramContext = (WifiManager)paramContext.getSystemService("wifi");
    if (paramContext != null)
    {
      bool = paramContext.isWifiEnabled();
      if (!bool) {
        return localStringBuilder.toString();
      }
    }
    else
    {
      return "";
    }
    paramContext = paramContext.getScanResults();
    if (paramContext == null) {}
    while (paramContext.size() == 0) {
      return localStringBuilder.toString();
    }
    paramContext = a(paramContext);
    j = 1;
    i = 0;
    label93:
    if ((i < paramContext.size()) && (i < 7))
    {
      localScanResult = (ScanResult)paramContext.get(i);
      if (j != 0) {
        break label151;
      }
      localStringBuilder.append(";");
    }
    for (;;)
    {
      localStringBuilder.append(localScanResult.BSSID);
      i += 1;
      break label93;
      break;
      label151:
      j = 0;
    }
  }
  
  public static String i(Context paramContext)
  {
    do
    {
      try
      {
        if (c != null) {
          continue;
        }
        if (!a(paramContext, "android.permission.ACCESS_WIFI_STATE")) {
          break;
        }
        paramContext = (WifiManager)paramContext.getSystemService("wifi");
        if (paramContext == null) {
          break label62;
        }
        c = paramContext.getConnectionInfo().getMacAddress();
      }
      catch (Throwable paramContext)
      {
        for (;;)
        {
          cy.a(paramContext, "DeviceInfo", "getDeviceMac");
        }
      }
      return c;
    } while ("".equals(c));
    return c;
    return c;
    label62:
    return "";
  }
  
  static String[] j(Context paramContext)
  {
    try
    {
      if (!a(paramContext, "android.permission.READ_PHONE_STATE")) {}
      while (!a(paramContext, "android.permission.ACCESS_COARSE_LOCATION")) {
        return new String[] { "", "" };
      }
      paramContext = (TelephonyManager)paramContext.getSystemService("phone");
      if (paramContext == null) {
        break label87;
      }
      paramContext = paramContext.getCellLocation();
      if ((paramContext instanceof GsmCellLocation)) {
        break label102;
      }
      boolean bool = paramContext instanceof CdmaCellLocation;
      if (bool) {
        break label155;
      }
    }
    catch (Throwable paramContext)
    {
      for (;;)
      {
        label87:
        label102:
        int i;
        int j;
        label155:
        int k;
        cy.a(paramContext, "DeviceInfo", "cellInfo");
      }
    }
    return new String[] { "", "" };
    return new String[] { "", "" };
    paramContext = (GsmCellLocation)paramContext;
    i = paramContext.getCid();
    j = paramContext.getLac();
    return new String[] { j + "||" + i, "gsm" };
    paramContext = (CdmaCellLocation)paramContext;
    i = paramContext.getSystemId();
    j = paramContext.getNetworkId();
    k = paramContext.getBaseStationId();
    if (i < 0) {}
    for (;;)
    {
      paramContext = i + "||" + j + "||" + k;
      return new String[] { paramContext, "cdma" };
      if ((j < 0) || (k < 0)) {}
    }
  }
  
  static String k(Context paramContext)
  {
    try
    {
      if (!a(paramContext, "android.permission.READ_PHONE_STATE")) {
        break label65;
      }
      paramContext = z(paramContext);
      if (paramContext == null) {
        break label68;
      }
      paramContext = paramContext.getNetworkOperator();
      if (TextUtils.isEmpty(paramContext)) {
        return "";
      }
      if (paramContext.length() >= 3)
      {
        paramContext = paramContext.substring(3);
        return paramContext;
      }
    }
    catch (Throwable paramContext)
    {
      cy.a(paramContext, "DeviceInfo", "getMNC");
      return "";
    }
    return "";
    label65:
    return "";
    label68:
    return "";
  }
  
  public static int l(Context paramContext)
  {
    try
    {
      int i = y(paramContext);
      return i;
    }
    catch (Throwable paramContext)
    {
      cy.a(paramContext, "DeviceInfo", "getNetWorkType");
    }
    return -1;
  }
  
  public static int m(Context paramContext)
  {
    try
    {
      int i = v(paramContext);
      return i;
    }
    catch (Throwable paramContext)
    {
      cy.a(paramContext, "DeviceInfo", "getActiveNetWorkType");
    }
    return -1;
  }
  
  public static NetworkInfo n(Context paramContext)
  {
    if (a(paramContext, "android.permission.ACCESS_NETWORK_STATE"))
    {
      paramContext = w(paramContext);
      if (paramContext != null) {
        return paramContext.getActiveNetworkInfo();
      }
    }
    else
    {
      return null;
    }
    return null;
  }
  
  static String o(Context paramContext)
  {
    try
    {
      paramContext = n(paramContext);
      if (paramContext != null)
      {
        paramContext = paramContext.getExtraInfo();
        return paramContext;
      }
      return null;
    }
    catch (Throwable paramContext)
    {
      cy.a(paramContext, "DeviceInfo", "getNetworkExtraInfo");
    }
    return null;
  }
  
  static String p(Context paramContext)
  {
    for (;;)
    {
      try
      {
        if (d != null) {
          continue;
        }
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        paramContext = (WindowManager)paramContext.getSystemService("window");
        if (paramContext == null) {
          break;
        }
        paramContext.getDefaultDisplay().getMetrics(localDisplayMetrics);
        i = localDisplayMetrics.widthPixels;
        j = localDisplayMetrics.heightPixels;
        if (j > i) {
          continue;
        }
        paramContext = j + "*" + i;
        d = paramContext;
      }
      catch (Throwable paramContext)
      {
        int i;
        int j;
        cy.a(paramContext, "DeviceInfo", "getReslution");
        continue;
      }
      return d;
      if (!"".equals(d))
      {
        return d;
        paramContext = i + "*" + j;
      }
    }
    return "";
  }
  
  public static String q(Context paramContext)
  {
    for (;;)
    {
      try
      {
        if (e != null) {
          continue;
        }
        if (!a(paramContext, "android.permission.READ_PHONE_STATE")) {
          continue;
        }
        paramContext = z(paramContext);
        if (paramContext == null) {
          break;
        }
        e = paramContext.getDeviceId();
        paramContext = e;
        if (paramContext == null) {
          continue;
        }
      }
      catch (Throwable paramContext)
      {
        cy.a(paramContext, "DeviceInfo", "getDeviceID");
        continue;
      }
      return e;
      if (!"".equals(e))
      {
        return e;
        return e;
        e = "";
      }
    }
    return "";
  }
  
  public static String r(Context paramContext)
  {
    try
    {
      paramContext = t(paramContext);
      return paramContext;
    }
    catch (Throwable paramContext)
    {
      cy.a(paramContext, "DeviceInfo", "getSubscriberId");
    }
    return "";
  }
  
  static String s(Context paramContext)
  {
    try
    {
      paramContext = u(paramContext);
      return paramContext;
    }
    catch (Throwable paramContext)
    {
      cy.a(paramContext, "DeviceInfo", "getNetworkOperatorName");
    }
    return "";
  }
  
  private static String t(Context paramContext)
  {
    if (f == null)
    {
      if (!a(paramContext, "android.permission.READ_PHONE_STATE")) {
        break label56;
      }
      paramContext = z(paramContext);
      if (paramContext == null) {
        break label60;
      }
      f = paramContext.getSubscriberId();
      if (f == null) {
        break label63;
      }
    }
    for (;;)
    {
      return f;
      if ("".equals(f)) {
        break;
      }
      return f;
      label56:
      return f;
      label60:
      return "";
      label63:
      f = "";
    }
  }
  
  private static String u(Context paramContext)
  {
    if (a(paramContext, "android.permission.READ_PHONE_STATE"))
    {
      paramContext = z(paramContext);
      if (paramContext != null)
      {
        String str = paramContext.getSimOperatorName();
        if (TextUtils.isEmpty(str)) {
          break label37;
        }
        return str;
      }
    }
    else
    {
      return null;
    }
    return "";
    label37:
    return paramContext.getNetworkOperatorName();
  }
  
  private static int v(Context paramContext)
  {
    if (paramContext == null) {}
    while (!a(paramContext, "android.permission.ACCESS_NETWORK_STATE")) {
      return -1;
    }
    paramContext = w(paramContext);
    if (paramContext != null)
    {
      paramContext = paramContext.getActiveNetworkInfo();
      if (paramContext != null) {
        return paramContext.getType();
      }
    }
    else
    {
      return -1;
    }
    return -1;
  }
  
  private static ConnectivityManager w(Context paramContext)
  {
    return (ConnectivityManager)paramContext.getSystemService("connectivity");
  }
  
  private static String x(Context paramContext)
  {
    paramContext = r(paramContext);
    if (paramContext == null) {}
    while (paramContext.length() < 5) {
      return "";
    }
    return paramContext.substring(3, 5);
  }
  
  private static int y(Context paramContext)
  {
    if (a(paramContext, "android.permission.READ_PHONE_STATE"))
    {
      paramContext = z(paramContext);
      if (paramContext != null) {
        return paramContext.getNetworkType();
      }
    }
    else
    {
      return -1;
    }
    return -1;
  }
  
  private static TelephonyManager z(Context paramContext)
  {
    return (TelephonyManager)paramContext.getSystemService("phone");
  }
  
  static class a
    extends DefaultHandler
  {
    public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
      throws SAXException
    {
      if (!cp.b()) {
        return;
      }
      cp.a(new String(paramArrayOfChar, paramInt1, paramInt2));
    }
    
    public void endElement(String paramString1, String paramString2, String paramString3)
      throws SAXException
    {
      cp.a(false);
    }
    
    public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
      throws SAXException
    {
      if (!paramString2.equals("string")) {}
      while (!"UTDID".equals(paramAttributes.getValue("name"))) {
        return;
      }
      cp.a(true);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/cp.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */