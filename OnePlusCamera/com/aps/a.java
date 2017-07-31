package com.aps;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.telephony.CellLocation;
import android.telephony.NeighboringCellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.core.AMapLocException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import org.json.JSONObject;

public class a
  implements k
{
  private l A = l.a();
  private int B = 0;
  private String C = "00:00:00:00:00:00";
  private y D = null;
  private StringBuilder E = new StringBuilder();
  private long F = 0L;
  private long G = 0L;
  private CellLocation H = null;
  private boolean I = false;
  TimerTask a;
  Timer b;
  ag c;
  int d = 0;
  private Context e = null;
  private int f = 9;
  private ConnectivityManager g = null;
  private WifiManager h = null;
  private TelephonyManager i = null;
  private List<e> j = new ArrayList();
  private List<ScanResult> k = new ArrayList();
  private Map<PendingIntent, List<j>> l = new HashMap();
  private Map<PendingIntent, List<j>> m = new HashMap();
  private b n = new b();
  private PhoneStateListener o = null;
  private int p = -113;
  private a q = new a(null);
  private WifiInfo r = null;
  private JSONObject s = null;
  private String t = null;
  private c u = null;
  private long v = 0L;
  private boolean w = false;
  private long x = 0L;
  private long y = 0L;
  private long z = 0L;
  
  private c a(byte[] paramArrayOfByte, boolean paramBoolean)
    throws Exception
  {
    m localm;
    if (this.e != null)
    {
      localm = new m();
      paramArrayOfByte = this.A.a(paramArrayOfByte, this.e, this.s);
    }
    label142:
    label157:
    do
    {
      try
      {
        com.amap.api.location.core.d.a(paramArrayOfByte);
        String[] arrayOfString = l.a(this.s);
        if (paramArrayOfByte == null)
        {
          if (arrayOfString[0].equals("true")) {
            break label142;
          }
          t.a(new Object[] { "aps return pure" });
          paramArrayOfByte = localm.b(paramArrayOfByte);
          if (!t.a(paramArrayOfByte)) {
            break label157;
          }
          if ((paramArrayOfByte.t() == null) && (this.E != null)) {
            continue;
          }
          return paramArrayOfByte;
          return null;
        }
      }
      catch (AMapLocException paramArrayOfByte)
      {
        throw paramArrayOfByte;
      }
      catch (Exception localException)
      {
        for (;;)
        {
          continue;
          if (paramArrayOfByte.indexOf("<saps>") != -1)
          {
            paramArrayOfByte = localm.a(paramArrayOfByte);
            paramArrayOfByte = this.n.a(paramArrayOfByte, "GBK");
            continue;
            t.a(new Object[] { "api return pure" });
          }
        }
      }
      throw new AMapLocException("未知的错误");
    } while (this.E.length() <= 0);
    this.t = this.E.toString();
    return paramArrayOfByte;
  }
  
  private e a(NeighboringCellInfo paramNeighboringCellInfo)
  {
    if (t.b() >= 5) {}
    try
    {
      e locale = new e();
      String[] arrayOfString = t.a(this.i);
      locale.a = arrayOfString[0];
      locale.b = arrayOfString[1];
      locale.c = paramNeighboringCellInfo.getLac();
      locale.d = paramNeighboringCellInfo.getCid();
      locale.j = t.a(paramNeighboringCellInfo.getRssi());
      return locale;
    }
    catch (Throwable paramNeighboringCellInfo)
    {
      paramNeighboringCellInfo.printStackTrace();
    }
    return null;
    return null;
  }
  
  private String a(int paramInt1, int paramInt2, int paramInt3)
    throws Exception
  {
    JSONObject localJSONObject = new JSONObject();
    localJSONObject.put("e", paramInt1);
    localJSONObject.put("d", paramInt2);
    localJSONObject.put("u", paramInt3);
    return localJSONObject.toString();
  }
  
  private void a(CellLocation paramCellLocation)
  {
    Object localObject2 = null;
    Object localObject1;
    if (this.w)
    {
      localObject1 = localObject2;
      if (localObject1 != null) {
        paramCellLocation = (CellLocation)localObject1;
      }
      if (paramCellLocation == null) {
        break label82;
      }
    }
    switch (t.a(paramCellLocation, this.e))
    {
    default: 
    case 1: 
      label82:
      do
      {
        return;
        localObject1 = localObject2;
        if (this.i == null) {
          break;
        }
        localObject1 = localObject2;
        if (this.i == null) {
          break;
        }
        localObject1 = this.i.getCellLocation();
        break;
        return;
      } while (this.i == null);
      c(paramCellLocation);
      return;
    }
    d(paramCellLocation);
  }
  
  private void a(StringBuilder paramStringBuilder)
  {
    int i1 = 0;
    String[] arrayOfString;
    if (paramStringBuilder != null)
    {
      arrayOfString = new String[23];
      arrayOfString[0] = " phnum=\"\"";
      arrayOfString[1] = " nettype=\"\"";
      arrayOfString[2] = " nettype=\"UNKNOWN\"";
      arrayOfString[3] = " inftype=\"\"";
      arrayOfString[4] = "<macs><![CDATA[]]></macs>";
      arrayOfString[5] = "<nb></nb>";
      arrayOfString[6] = "<mmac><![CDATA[]]></mmac>";
      arrayOfString[7] = " gtype=\"0\"";
      arrayOfString[8] = " glong=\"0.0\"";
      arrayOfString[9] = " glat=\"0.0\"";
      arrayOfString[10] = " precision=\"0.0\"";
      arrayOfString[11] = " glong=\"0\"";
      arrayOfString[12] = " glat=\"0\"";
      arrayOfString[13] = " precision=\"0\"";
      arrayOfString[14] = "<smac>null</smac>";
      arrayOfString[15] = "<smac>00:00:00:00:00:00</smac>";
      arrayOfString[16] = "<imei>000000000000000</imei>";
      arrayOfString[17] = "<imsi>000000000000000</imsi>";
      arrayOfString[18] = "<mcc>000</mcc>";
      arrayOfString[19] = "<mcc>0</mcc>";
      arrayOfString[20] = "<lac>0</lac>";
      arrayOfString[21] = "<cellid>0</cellid>";
      arrayOfString[22] = "<key></key>";
      int i2 = arrayOfString.length;
      if (i1 < i2) {
        break label213;
      }
    }
    for (;;)
    {
      if (paramStringBuilder.indexOf("*<") == -1)
      {
        return;
        return;
        label213:
        String str = arrayOfString[i1];
        for (;;)
        {
          if (paramStringBuilder.indexOf(str) == -1)
          {
            i1 += 1;
            break;
          }
          int i3 = paramStringBuilder.indexOf(str);
          paramStringBuilder.delete(i3, str.length() + i3);
        }
      }
      paramStringBuilder.deleteCharAt(paramStringBuilder.indexOf("*<"));
    }
  }
  
  private void a(List<ScanResult> paramList)
  {
    if (paramList == null) {
      return;
    }
    for (;;)
    {
      try
      {
        if (paramList.size() < 1) {
          break;
        }
        HashMap localHashMap = new HashMap();
        i1 = 0;
        Object localObject;
        Iterator localIterator;
        if (i1 >= paramList.size())
        {
          localObject = new TreeMap(Collections.reverseOrder());
          ((TreeMap)localObject).putAll(localHashMap);
          paramList.clear();
          localIterator = ((TreeMap)localObject).entrySet().iterator();
          if (!localIterator.hasNext())
          {
            localHashMap.clear();
            ((TreeMap)localObject).clear();
          }
        }
        else
        {
          localObject = (ScanResult)paramList.get(i1);
          if (paramList.size() <= 20)
          {
            if (((ScanResult)localObject).SSID == null)
            {
              ((ScanResult)localObject).SSID = "null";
              localHashMap.put(Integer.valueOf(((ScanResult)localObject).level * 30 + i1), localObject);
              break label239;
            }
          }
          else
          {
            if (!a(((ScanResult)localObject).level)) {
              break label239;
            }
            continue;
          }
          ((ScanResult)localObject).SSID = ((ScanResult)localObject).SSID.replace("*", ".");
          continue;
        }
        paramList.add(((Map.Entry)localIterator.next()).getValue());
      }
      finally {}
      int i1 = paramList.size();
      if (i1 <= 29)
      {
        continue;
        label239:
        i1 += 1;
      }
    }
  }
  
  private boolean a(int paramInt)
  {
    int i1 = 20;
    boolean bool = true;
    try
    {
      paramInt = WifiManager.calculateSignalLevel(paramInt, 20);
      if (paramInt < 1) {
        bool = false;
      }
      return bool;
    }
    catch (ArithmeticException localArithmeticException)
    {
      for (;;)
      {
        t.a(localArithmeticException);
        paramInt = i1;
      }
    }
  }
  
  private boolean a(long paramLong)
  {
    long l1 = t.a();
    if (l1 - paramLong >= 300L)
    {
      i1 = 1;
      if (i1 == 0)
      {
        paramLong = 0L;
        if (this.u != null) {
          break label52;
        }
        label31:
        if (paramLong > 10000L) {
          break label66;
        }
      }
    }
    label52:
    label66:
    for (int i1 = 1;; i1 = 0)
    {
      if (i1 != 0) {
        break label71;
      }
      return false;
      i1 = 0;
      break;
      paramLong = l1 - this.u.h();
      break label31;
    }
    label71:
    return true;
  }
  
  private boolean a(ScanResult paramScanResult)
  {
    boolean bool1 = true;
    if (paramScanResult == null) {}
    for (;;)
    {
      bool1 = false;
      label8:
      return bool1;
      try
      {
        if (!TextUtils.isEmpty(paramScanResult.BSSID))
        {
          boolean bool2 = paramScanResult.BSSID.equals("00:00:00:00:00:00");
          if (!bool2) {
            break label8;
          }
          return false;
        }
      }
      catch (Exception paramScanResult) {}
    }
    return true;
  }
  
  private boolean a(WifiInfo paramWifiInfo)
  {
    boolean bool = true;
    if (paramWifiInfo == null) {
      bool = false;
    }
    do
    {
      return bool;
      if (paramWifiInfo.getBSSID() == null) {
        break;
      }
      if (paramWifiInfo.getSSID() == null) {
        break label48;
      }
      if (paramWifiInfo.getBSSID().equals("00:00:00:00:00:00")) {
        break label50;
      }
    } while (!TextUtils.isEmpty(paramWifiInfo.getSSID()));
    return false;
    label48:
    return false;
    label50:
    return false;
  }
  
  private byte[] a(Object paramObject)
  {
    o localo;
    Object localObject3;
    StringBuilder localStringBuilder3;
    StringBuilder localStringBuilder2;
    StringBuilder localStringBuilder1;
    Object localObject2;
    label90:
    label126:
    String str;
    label339:
    try
    {
      localo = new o();
      this.E.delete(0, this.E.length());
      f.c = "";
      localObject3 = y.a("version");
      localStringBuilder3 = new StringBuilder();
      localStringBuilder2 = new StringBuilder();
      localStringBuilder1 = new StringBuilder();
      if (this.f != 2)
      {
        localObject2 = "0";
        paramObject = this.i;
        if (paramObject != null) {
          break label887;
        }
        paramObject = null;
      }
    }
    finally {}
    try
    {
      localObject1 = this.g.getActiveNetworkInfo();
      paramObject = localObject1;
    }
    catch (SecurityException localSecurityException)
    {
      for (;;)
      {
        break;
        paramObject = "";
        continue;
        i1 += 1;
        continue;
        int i1 = 0;
        continue;
        i1 += 1;
      }
    }
    if (l.a((NetworkInfo)paramObject) == -1)
    {
      this.r = null;
      localObject1 = "";
      paramObject = "";
      str = l.a(this.s)[1];
      localo.i = ((String)localObject2);
      localo.j = "0";
      localo.k = "0";
      localo.l = "0";
      localo.m = "0";
      localo.c = f.d;
      localo.d = f.e;
      localo.n = str;
      localo.o = f.a;
      localo.r = f.c;
      localo.p = f.b;
      localo.q = this.C;
      localo.s = ((String)localObject1);
      localo.t = ((String)paramObject);
      localo.f = com.amap.api.location.core.c.e();
      localo.g = ("android" + com.amap.api.location.core.c.d());
      localo.h = (com.amap.api.location.core.c.g() + "," + com.amap.api.location.core.c.h());
      localo.B = "V1.3.1";
      localo.C = ((String)localObject3);
    }
    try
    {
      localObject3 = this.k;
      if (localObject3 != null) {
        break label1062;
      }
    }
    catch (Throwable localThrowable)
    {
      for (;;)
      {
        localThrowable.printStackTrace();
      }
    }
    this.E.append("<?xml version=\"1.0\" encoding=\"");
    this.E.append("GBK").append("\"?>");
    this.E.append("<Cell_Req ver=\"3.0\"><HDR version=\"3.0\" cdma=\"");
    this.E.append((String)localObject2);
    this.E.append("\" gtype=\"").append("0");
    this.E.append("\" glong=\"").append("0");
    this.E.append("\" glat=\"").append("0");
    this.E.append("\" precision=\"").append("0");
    this.E.append("\"><src>").append(f.d);
    this.E.append("</src><license>").append(f.e);
    this.E.append("</license><key>").append(str);
    this.E.append("</key><clientid>").append(f.f);
    this.E.append("</clientid><imei>").append(f.a);
    this.E.append("</imei><imsi>").append(f.b);
    this.E.append("</imsi><smac>").append(this.C);
    this.E.append("</smac></HDR><DRR phnum=\"").append(f.c);
    this.E.append("\" nettype=\"").append((String)localObject1);
    this.E.append("\" inftype=\"").append((String)paramObject).append("\">");
    if (this.j.size() <= 0)
    {
      paramObject = "";
      if (s()) {
        break label1641;
      }
      n();
      label655:
      this.E.append((String)paramObject);
      this.E.append(String.format("<nb>%s</nb>", new Object[] { localStringBuilder3 }));
      if (localStringBuilder2.length() == 0) {
        break label1808;
      }
      localStringBuilder2.deleteCharAt(localStringBuilder2.length() - 1);
      this.E.append(String.format("<macs><![CDATA[%s]]></macs>", new Object[] { localStringBuilder2 }));
      label731:
      this.E.append(String.format("<mmac><![CDATA[%s]]></mmac>", new Object[] { localStringBuilder1 }));
      this.E.append("</DRR></Cell_Req>");
      a(this.E);
      if (localStringBuilder2.length() == 0) {
        break label1834;
      }
    }
    label887:
    label1062:
    label1641:
    label1758:
    label1808:
    label1834:
    for (Object localObject1 = localStringBuilder2;; localObject1 = localStringBuilder1)
    {
      localo.v = ((String)paramObject);
      localo.w = localStringBuilder3.toString();
      localo.x = localStringBuilder1.toString();
      localo.y = ((StringBuilder)localObject1).toString();
      localo.u = String.valueOf(this.f);
      localStringBuilder3.delete(0, localStringBuilder3.length());
      ((StringBuilder)localObject1).delete(0, ((StringBuilder)localObject1).length());
      localStringBuilder1.delete(0, localStringBuilder1.length());
      paramObject = localo.a();
      return (byte[])paramObject;
      localObject2 = "1";
      break;
      paramObject = f.a;
      if (paramObject == null) {}
      for (;;)
      {
        try
        {
          f.a = this.i.getDeviceId();
          paramObject = f.a;
          if (paramObject == null) {
            continue;
          }
        }
        catch (SecurityException paramObject)
        {
          boolean bool;
          continue;
          if ("888888888888888".equals(f.b)) {
            continue;
          }
        }
        paramObject = f.b;
        if (paramObject != null) {
          continue;
        }
        try
        {
          f.b = this.i.getSubscriberId();
          if (f.b != null) {
            break;
          }
          f.b = "888888888888888";
        }
        catch (SecurityException paramObject) {}
        break;
        bool = "888888888888888".equals(f.a);
        if (!bool)
        {
          continue;
          f.a = "888888888888888";
        }
      }
      break label90;
      paramObject = l.a(this.i);
      if (!s()) {}
      for (;;)
      {
        if (s())
        {
          localObject1 = paramObject;
          paramObject = "1";
          break;
          if (a(this.r))
          {
            localObject1 = paramObject;
            paramObject = "2";
            break;
          }
        }
      }
      n();
      localObject1 = paramObject;
      paramObject = "1";
      break label126;
      if (this.k.size() <= 0) {
        break label339;
      }
      localo.E = (t.a() - this.z + "");
      break label339;
      localObject1 = new StringBuilder();
      switch (this.f)
      {
      case 1: 
        do
        {
          ((StringBuilder)localObject1).delete(0, ((StringBuilder)localObject1).length());
          break;
          paramObject = (e)this.j.get(0);
          ((StringBuilder)localObject1).delete(0, ((StringBuilder)localObject1).length());
          ((StringBuilder)localObject1).append("<mcc>").append(((e)paramObject).a).append("</mcc>");
          ((StringBuilder)localObject1).append("<mnc>").append(((e)paramObject).b).append("</mnc>");
          ((StringBuilder)localObject1).append("<lac>").append(((e)paramObject).c).append("</lac>");
          ((StringBuilder)localObject1).append("<cellid>").append(((e)paramObject).d);
          ((StringBuilder)localObject1).append("</cellid>");
          ((StringBuilder)localObject1).append("<signal>").append(((e)paramObject).j);
          ((StringBuilder)localObject1).append("</signal>");
          paramObject = ((StringBuilder)localObject1).toString();
          i1 = 0;
        } while (i1 >= this.j.size());
        if (i1 == 0) {
          break;
        }
        localObject2 = (e)this.j.get(i1);
        localStringBuilder3.append(((e)localObject2).c).append(",");
        localStringBuilder3.append(((e)localObject2).d).append(",");
        localStringBuilder3.append(((e)localObject2).j);
        if (i1 == this.j.size() - 1) {
          break;
        }
        localStringBuilder3.append("*");
        break;
      case 2: 
        paramObject = (e)this.j.get(0);
        ((StringBuilder)localObject1).delete(0, ((StringBuilder)localObject1).length());
        ((StringBuilder)localObject1).append("<mcc>").append(((e)paramObject).a).append("</mcc>");
        ((StringBuilder)localObject1).append("<sid>").append(((e)paramObject).g).append("</sid>");
        ((StringBuilder)localObject1).append("<nid>").append(((e)paramObject).h).append("</nid>");
        ((StringBuilder)localObject1).append("<bid>").append(((e)paramObject).i).append("</bid>");
        if (((e)paramObject).f <= 0) {}
        for (;;)
        {
          ((StringBuilder)localObject1).append("<signal>").append(((e)paramObject).j).append("</signal>");
          paramObject = ((StringBuilder)localObject1).toString();
          break;
          if (((e)paramObject).e > 0)
          {
            ((StringBuilder)localObject1).append("<lon>").append(((e)paramObject).f).append("</lon>");
            ((StringBuilder)localObject1).append("<lat>").append(((e)paramObject).e).append("</lat>");
          }
        }
        if (!a(this.r))
        {
          break label1860;
          if (i1 >= this.k.size()) {
            break label655;
          }
          localObject1 = (ScanResult)this.k.get(i1);
          if (a((ScanResult)localObject1)) {
            break label1758;
          }
          break label1865;
        }
        localStringBuilder1.append(this.r.getBSSID()).append(",");
        localStringBuilder1.append(this.r.getRssi()).append(",");
        localStringBuilder1.append(this.r.getSSID().replace("*", "."));
        break label1860;
        localStringBuilder2.append(((ScanResult)localObject1).BSSID).append(",");
        localStringBuilder2.append(((ScanResult)localObject1).level).append(",");
        localStringBuilder2.append(i1).append("*");
        break label1865;
        this.E.append(String.format("<macs><![CDATA[%s]]></macs>", new Object[] { localStringBuilder1 }));
        break label731;
      }
    }
  }
  
  private e b(CellLocation paramCellLocation)
  {
    paramCellLocation = (GsmCellLocation)paramCellLocation;
    e locale = new e();
    String[] arrayOfString = t.a(this.i);
    locale.a = arrayOfString[0];
    locale.b = arrayOfString[1];
    locale.c = paramCellLocation.getLac();
    locale.d = paramCellLocation.getCid();
    locale.j = this.p;
    return locale;
  }
  
  private void b(int paramInt)
  {
    if (paramInt != -113)
    {
      this.p = paramInt;
      switch (this.f)
      {
      }
    }
    do
    {
      return;
      this.p = -113;
      return;
    } while (this.j.size() <= 0);
    ((e)this.j.get(0)).j = this.p;
  }
  
  private void c(final int paramInt)
  {
    int i1 = 0;
    label68:
    do
    {
      try
      {
        if (t.a() - this.F < 45000L) {
          continue;
        }
        i1 = 1;
      }
      catch (Throwable localThrowable)
      {
        localThrowable.printStackTrace();
        return;
      }
      if (!e()) {
        break;
      }
      if (!e())
      {
        x();
        if (this.a == null) {
          break label68;
        }
      }
      for (;;)
      {
        if (this.b != null)
        {
          return;
          if (this.D.f() >= 20) {
            break;
          }
          return;
          this.a = new TimerTask()
          {
            public void run()
            {
              try
              {
                if (!a.i(a.this))
                {
                  a.j(a.this);
                  return;
                }
                a.c(a.this, paramInt);
                if (!a.this.e())
                {
                  a.j(a.this);
                  return;
                }
              }
              catch (Throwable localThrowable)
              {
                localThrowable.printStackTrace();
              }
            }
          };
          continue;
        }
        this.b = new Timer(false);
        this.b.schedule(this.a, 3000L, 3000L);
        return;
      }
    } while (i1 != 0);
    return;
  }
  
  private void c(CellLocation paramCellLocation)
  {
    if (this.j == null) {}
    while ((paramCellLocation == null) || (this.i == null)) {
      return;
    }
    this.j.clear();
    Object localObject = (GsmCellLocation)paramCellLocation;
    int i1;
    if (((GsmCellLocation)localObject).getLac() != -1)
    {
      if (((GsmCellLocation)localObject).getCid() != -1) {
        break label110;
      }
      i1 = 0;
      label51:
      if (i1 == 0) {
        break label174;
      }
      this.f = 1;
      this.j.add(b(paramCellLocation));
      if (this.i != null) {
        break label194;
      }
      paramCellLocation = null;
      label84:
      if (paramCellLocation == null) {
        break label205;
      }
      paramCellLocation = paramCellLocation.iterator();
    }
    label110:
    label164:
    label169:
    label174:
    label194:
    label205:
    label319:
    label324:
    label329:
    label334:
    label339:
    label344:
    label347:
    for (;;)
    {
      if (!paramCellLocation.hasNext())
      {
        return;
        i1 = 0;
        break label51;
        if ((((GsmCellLocation)localObject).getCid() == 65535) || (((GsmCellLocation)localObject).getCid() >= 268435455)) {
          break;
        }
        if (((GsmCellLocation)localObject).getLac() != 0)
        {
          if (((GsmCellLocation)localObject).getLac() > 65535) {
            break label164;
          }
          if (((GsmCellLocation)localObject).getCid() == 0) {
            break label169;
          }
          i1 = 1;
          break label51;
        }
        i1 = 0;
        break label51;
        i1 = 0;
        break label51;
        i1 = 0;
        break label51;
        this.f = 9;
        t.a(new Object[] { "case 2,gsm illegal" });
        return;
        paramCellLocation = this.i.getNeighboringCellInfo();
        break label84;
        return;
      }
      localObject = (NeighboringCellInfo)paramCellLocation.next();
      if (((NeighboringCellInfo)localObject).getCid() != -1)
      {
        if (((NeighboringCellInfo)localObject).getLac() != -1)
        {
          if (((NeighboringCellInfo)localObject).getLac() == 0) {
            break label319;
          }
          if (((NeighboringCellInfo)localObject).getLac() > 65535) {
            break label324;
          }
          if (((NeighboringCellInfo)localObject).getCid() == -1) {
            break label329;
          }
          if (((NeighboringCellInfo)localObject).getCid() == 0) {
            break label334;
          }
          if (((NeighboringCellInfo)localObject).getCid() == 65535) {
            break label339;
          }
          if (((NeighboringCellInfo)localObject).getCid() >= 268435455) {
            break label344;
          }
          i1 = 1;
        }
        for (;;)
        {
          if (i1 == 0) {
            break label347;
          }
          localObject = a((NeighboringCellInfo)localObject);
          if (localObject == null) {
            break;
          }
          this.j.add(localObject);
          break;
          i1 = 0;
          continue;
          i1 = 0;
          continue;
          i1 = 0;
          continue;
          i1 = 0;
          continue;
          i1 = 0;
          continue;
          i1 = 0;
          continue;
          i1 = 0;
        }
      }
    }
  }
  
  private void d(int paramInt)
  {
    int i2 = 70254591;
    if (e()) {}
    for (;;)
    {
      int i1;
      try
      {
        w();
        i1 = i2;
        switch (paramInt)
        {
        case 0: 
          this.D.a(null, a(1, i1, 1));
          this.c = this.D.d();
          if (this.c == null)
          {
            x();
            if (e()) {
              break label225;
            }
            if (this.d >= 3) {
              break label240;
            }
            return;
          }
        case 2: 
          if (!m()) {
            break label258;
          }
          i1 = 2083520511;
          continue;
          Object localObject = this.c.a();
          localObject = this.A.a((byte[])localObject, this.e);
          if (!e()) {
            continue;
          }
          if (TextUtils.isEmpty((CharSequence)localObject))
          {
            this.d += 1;
            this.D.a(this.c, a(1, i1, 0));
            continue;
          }
          if (!localThrowable.equals("true")) {
            continue;
          }
        }
      }
      catch (Throwable localThrowable)
      {
        localThrowable.printStackTrace();
        t.a(localThrowable);
        return;
      }
      this.D.a(this.c, a(1, i1, 1));
      continue;
      label225:
      if (this.D.f() == 0)
      {
        v();
        return;
        label240:
        v();
        return;
        i1 = i2;
        continue;
        return;
        i1 = 674234367;
        continue;
        label258:
        i1 = 674234367;
      }
    }
  }
  
  private void d(CellLocation paramCellLocation)
  {
    this.j.clear();
    if (t.b() >= 5) {
      try
      {
        paramCellLocation = (CdmaCellLocation)paramCellLocation;
        if (paramCellLocation.getSystemId() > 0)
        {
          if (paramCellLocation.getNetworkId() >= 0)
          {
            if (paramCellLocation.getBaseStationId() < 0) {
              break label177;
            }
            this.f = 2;
            String[] arrayOfString = t.a(this.i);
            e locale = new e();
            locale.a = arrayOfString[0];
            locale.b = arrayOfString[1];
            locale.g = paramCellLocation.getSystemId();
            locale.h = paramCellLocation.getNetworkId();
            locale.i = paramCellLocation.getBaseStationId();
            locale.j = this.p;
            locale.e = paramCellLocation.getBaseStationLatitude();
            locale.f = paramCellLocation.getBaseStationLongitude();
            this.j.add(locale);
          }
        }
        else
        {
          this.f = 9;
          t.a(new Object[] { "cdma illegal" });
          return;
        }
        this.f = 9;
        t.a(new Object[] { "cdma illegal" });
        return;
        label177:
        this.f = 9;
        t.a(new Object[] { "cdma illegal" });
        return;
      }
      catch (Throwable paramCellLocation)
      {
        paramCellLocation.printStackTrace();
        return;
      }
    }
  }
  
  private void f()
  {
    this.h = ((WifiManager)t.b(this.e, "wifi"));
    IntentFilter localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
    localIntentFilter.addAction("android.net.wifi.SCAN_RESULTS");
    localIntentFilter.addAction("android.intent.action.SCREEN_ON");
    localIntentFilter.addAction("android.intent.action.SCREEN_OFF");
    localIntentFilter.addAction("android.intent.action.AIRPLANE_MODE");
    localIntentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
    this.e.registerReceiver(this.q, localIntentFilter);
    o();
  }
  
  private void g()
  {
    this.g = ((ConnectivityManager)t.b(this.e, "connectivity"));
    CellLocation.requestLocationUpdate();
    this.y = t.a();
    this.i = ((TelephonyManager)t.b(this.e, "phone"));
    int i1;
    if (this.i == null) {
      i1 = 9;
    }
    for (;;)
    {
      switch (i1)
      {
      default: 
        this.f = 9;
        label82:
        this.o = new PhoneStateListener()
        {
          public void onCellLocationChanged(CellLocation paramAnonymousCellLocation)
          {
            if (paramAnonymousCellLocation != null) {}
            try
            {
              if (!a.a(a.this))
              {
                a.a(a.this, paramAnonymousCellLocation);
                a.a(a.this, t.a());
                a.b(a.this, t.a());
                return;
                return;
              }
              return;
            }
            catch (Throwable paramAnonymousCellLocation)
            {
              paramAnonymousCellLocation.printStackTrace();
            }
          }
          
          public void onServiceStateChanged(ServiceState paramAnonymousServiceState)
          {
            try
            {
              switch (paramAnonymousServiceState.getState())
              {
              case 1: 
                a.c(a.this).clear();
                a.b(a.this, -113);
                return;
              }
            }
            catch (Throwable paramAnonymousServiceState)
            {
              paramAnonymousServiceState.printStackTrace();
              return;
            }
          }
          
          public void onSignalStrengthChanged(int paramAnonymousInt)
          {
            int i = -113;
            for (;;)
            {
              try
              {
                switch (a.b(a.this))
                {
                case 1: 
                  a.a(a.this, paramAnonymousInt);
                  return;
                }
              }
              catch (Throwable localThrowable)
              {
                localThrowable.printStackTrace();
                return;
              }
              paramAnonymousInt = t.a(paramAnonymousInt);
              continue;
              paramAnonymousInt = t.a(paramAnonymousInt);
              continue;
              paramAnonymousInt = i;
            }
          }
          
          public void onSignalStrengthsChanged(SignalStrength paramAnonymousSignalStrength)
          {
            int i = -113;
            for (;;)
            {
              try
              {
                switch (a.b(a.this))
                {
                case 1: 
                  a.a(a.this, i);
                  return;
                }
              }
              catch (Throwable paramAnonymousSignalStrength)
              {
                paramAnonymousSignalStrength.printStackTrace();
                return;
              }
              i = t.a(paramAnonymousSignalStrength.getGsmSignalStrength());
              continue;
              i = paramAnonymousSignalStrength.getCdmaDbm();
            }
          }
        };
        if (t.b() >= 7)
        {
          i1 = 256;
          label106:
          if (i1 == 0) {
            break label152;
          }
        }
        break;
      }
      try
      {
        TelephonyManager localTelephonyManager = this.i;
        if (localTelephonyManager == null)
        {
          label152:
          do
          {
            return;
            i1 = this.i.getPhoneType();
            break;
            this.f = 1;
            break label82;
            this.f = 2;
            break label82;
            i1 = 2;
            break label106;
          } while (this.i == null);
          this.i.listen(this.o, 16);
          return;
        }
        this.i.listen(this.o, i1 | 0x10);
        return;
      }
      catch (SecurityException localSecurityException)
      {
        t.a(localSecurityException);
      }
    }
  }
  
  private String h()
  {
    Object localObject3 = null;
    u();
    if (!s())
    {
      n();
      switch (this.f)
      {
      default: 
        localObject1 = "";
      }
    }
    Object localObject2;
    label413:
    do
    {
      return (String)localObject1;
      this.r = this.h.getConnectionInfo();
      break;
      if (this.j.size() <= 0) {
        return "";
      }
      localObject1 = (e)this.j.get(0);
      localObject2 = new StringBuilder();
      ((StringBuilder)localObject2).append(((e)localObject1).a).append("#");
      ((StringBuilder)localObject2).append(((e)localObject1).b).append("#");
      ((StringBuilder)localObject2).append(((e)localObject1).c).append("#");
      ((StringBuilder)localObject2).append(((e)localObject1).d).append("#");
      ((StringBuilder)localObject2).append("network").append("#");
      if (this.k.size() <= 0) {}
      for (localObject1 = "cell";; localObject1 = "cellwifi")
      {
        ((StringBuilder)localObject2).append((String)localObject1);
        return ((StringBuilder)localObject2).toString();
      }
      if (this.j.size() <= 0) {
        return "";
      }
      localObject1 = (e)this.j.get(0);
      localObject2 = new StringBuilder();
      ((StringBuilder)localObject2).append(((e)localObject1).a).append("#");
      ((StringBuilder)localObject2).append(((e)localObject1).b).append("#");
      ((StringBuilder)localObject2).append(((e)localObject1).g).append("#");
      ((StringBuilder)localObject2).append(((e)localObject1).h).append("#");
      ((StringBuilder)localObject2).append(((e)localObject1).i).append("#");
      ((StringBuilder)localObject2).append("network").append("#");
      if (this.k.size() <= 0) {}
      for (localObject1 = "cell";; localObject1 = "cellwifi")
      {
        ((StringBuilder)localObject2).append((String)localObject1);
        return ((StringBuilder)localObject2).toString();
      }
      localObject2 = String.format("#%s#", new Object[] { "network" });
      if (this.k.size() == 1) {
        break label461;
      }
      localObject1 = localObject3;
    } while (this.k.size() == 0);
    if (this.k.size() != 1) {}
    label461:
    while (!a(this.r))
    {
      return (String)localObject2 + "wifi";
      localObject1 = localObject3;
      if (!a(this.r)) {
        break;
      }
      break label413;
    }
    Object localObject1 = (ScanResult)this.k.get(0);
    if (localObject1 == null) {}
    for (localObject1 = localObject2;; localObject1 = null)
    {
      return (String)localObject1;
      if (!this.r.getBSSID().equals(((ScanResult)localObject1).BSSID)) {
        break;
      }
    }
  }
  
  private StringBuilder i()
  {
    u();
    StringBuilder localStringBuilder = new StringBuilder(700);
    switch (this.f)
    {
    default: 
      if (this.C == null)
      {
        label47:
        if (this.r == null) {
          break label197;
        }
        this.C = this.r.getMacAddress();
        if (this.C == null) {
          break label257;
        }
      }
      break;
    }
    int i1;
    Object localObject1;
    for (;;)
    {
      if (s()) {
        break label266;
      }
      n();
      if (localStringBuilder.length() > 0) {
        break label436;
      }
      return localStringBuilder;
      i1 = 0;
      while (i1 < this.j.size())
      {
        if (i1 != 0)
        {
          localObject1 = (e)this.j.get(i1);
          localStringBuilder.append("#").append(((e)localObject1).b);
          localStringBuilder.append("|").append(((e)localObject1).c);
          localStringBuilder.append("|").append(((e)localObject1).d);
        }
        i1 += 1;
      }
      break;
      if (this.C.equals("00:00:00:00:00:00")) {
        break label47;
      }
      continue;
      label197:
      if (this.h != null)
      {
        this.r = this.h.getConnectionInfo();
        if (this.r != null)
        {
          this.C = this.r.getMacAddress();
          if (this.C != null) {}
          for (;;)
          {
            this.r = null;
            break;
            this.C = "00:00:00:00:00:00";
          }
          label257:
          this.C = "00:00:00:00:00:00";
        }
      }
    }
    label266:
    label281:
    int i2;
    if (!a(this.r))
    {
      localObject1 = "";
      i2 = 0;
      i1 = 0;
    }
    for (;;)
    {
      if (i2 >= this.k.size())
      {
        if ((i1 != 0) || (((String)localObject1).length() <= 0)) {
          break;
        }
        localStringBuilder.append("#").append((String)localObject1);
        localStringBuilder.append(",access");
        break;
        localObject1 = this.r.getBSSID();
        break label281;
      }
      localObject2 = (ScanResult)this.k.get(i2);
      if (a((ScanResult)localObject2)) {
        break label376;
      }
      i2 += 1;
    }
    label376:
    String str = ((ScanResult)localObject2).BSSID;
    Object localObject2 = "nb";
    if (!((String)localObject1).equals(str)) {}
    for (;;)
    {
      localStringBuilder.append(String.format("#%s,%s", new Object[] { str, localObject2 }));
      break;
      localObject2 = "access";
      i1 = 1;
    }
    label436:
    localStringBuilder.deleteCharAt(0);
    return localStringBuilder;
  }
  
  private byte[] j()
  {
    for (;;)
    {
      try
      {
        if (!k())
        {
          if (!l())
          {
            byte[] arrayOfByte = a(null);
            return arrayOfByte;
          }
        }
        else
        {
          CellLocation.requestLocationUpdate();
          this.y = t.a();
          continue;
        }
        o();
      }
      finally {}
    }
  }
  
  private boolean k()
  {
    if ((this.w) || (this.y == 0L)) {}
    for (;;)
    {
      return false;
      if (t.a() - this.y >= f.j) {}
      for (int i1 = 1; i1 != 0; i1 = 0) {
        return true;
      }
    }
  }
  
  private boolean l()
  {
    if ((!s()) || (this.z == 0L)) {}
    for (;;)
    {
      return false;
      if (t.a() - this.z >= f.i) {}
      for (int i1 = 1; i1 != 0; i1 = 0) {
        return true;
      }
    }
  }
  
  private boolean m()
  {
    NetworkInfo localNetworkInfo = null;
    if (this.h != null)
    {
      if (!s()) {
        return false;
      }
    }
    else {
      return false;
    }
    boolean bool;
    try
    {
      if (this.g == null) {}
      while (l.a(localNetworkInfo) == -1)
      {
        return false;
        localNetworkInfo = this.g.getActiveNetworkInfo();
      }
      bool = a(this.h.getConnectionInfo());
      if (!bool) {
        bool = false;
      } else {
        bool = true;
      }
    }
    catch (Throwable localThrowable)
    {
      localThrowable.printStackTrace();
      return false;
    }
    catch (SecurityException localSecurityException)
    {
      return false;
    }
    return bool;
  }
  
  private void n()
  {
    this.k.clear();
    this.r = null;
  }
  
  private void o()
  {
    if (!s()) {
      return;
    }
    try
    {
      this.h.startScan();
      this.z = t.a();
      return;
    }
    catch (SecurityException localSecurityException) {}
  }
  
  private boolean p()
  {
    if (this.x == 0L) {
      return false;
    }
    if (t.a() - this.x >= 2000L) {}
    for (int i1 = 1; i1 == 0; i1 = 0) {
      return true;
    }
    return false;
  }
  
  private void q()
  {
    if (this.u == null) {}
    while (this.l.size() < 1) {
      return;
    }
    Iterator localIterator = this.l.entrySet().iterator();
    if (localIterator == null) {}
    while (!localIterator.hasNext()) {
      return;
    }
    Object localObject1 = (Map.Entry)localIterator.next();
    PendingIntent localPendingIntent = (PendingIntent)((Map.Entry)localObject1).getKey();
    Object localObject2 = (List)((Map.Entry)localObject1).getValue();
    localObject1 = new Intent();
    Bundle localBundle = new Bundle();
    localObject2 = ((List)localObject2).iterator();
    label116:
    label274:
    label277:
    for (;;)
    {
      j localj;
      if (((Iterator)localObject2).hasNext())
      {
        localj = (j)((Iterator)localObject2).next();
        long l1 = localj.a();
        if (l1 != -1L) {
          if (l1 < t.a()) {
            break label274;
          }
        }
      }
      for (int i1 = 1;; i1 = 0)
      {
        if (i1 == 0) {
          break label277;
        }
        float f1 = t.a(new double[] { localj.b, localj.a, this.u.f(), this.u.e() });
        if (f1 >= localj.c) {
          break label116;
        }
        localBundle.putFloat("distance", f1);
        localBundle.putString("fence", localj.b());
        ((Intent)localObject1).putExtras(localBundle);
        try
        {
          localPendingIntent.send(this.e, 0, (Intent)localObject1);
        }
        catch (Throwable localThrowable)
        {
          localThrowable.printStackTrace();
        }
        break label116;
        break;
      }
    }
  }
  
  private void r()
  {
    switch (this.f)
    {
    }
    do
    {
      do
      {
        return;
      } while (this.j.size() != 0);
      this.f = 9;
      return;
    } while (this.j.size() != 0);
    this.f = 9;
  }
  
  private boolean s()
  {
    boolean bool1 = false;
    if (this.h != null) {}
    try
    {
      bool2 = this.h.isWifiEnabled();
      bool1 = bool2;
    }
    catch (Exception localException2)
    {
      boolean bool2;
      for (;;) {}
    }
    if (bool1) {}
    while (t.b() <= 17)
    {
      return bool1;
      return false;
    }
    try
    {
      bool2 = String.valueOf(n.a(this.h, "isScanAlwaysAvailable", new Object[0])).equals("true");
      return bool2;
    }
    catch (Exception localException1)
    {
      return bool1;
    }
  }
  
  private c t()
    throws Exception
  {
    byte[] arrayOfByte = j();
    if (this.E == null) {}
    while ((!this.E.toString().equals(this.t)) || (this.u == null)) {
      return a(arrayOfByte, false);
    }
    this.v = t.a();
    return this.u;
  }
  
  private void u()
  {
    if (!this.w)
    {
      r();
      return;
    }
    this.f = 9;
    this.j.clear();
  }
  
  private void v()
  {
    if (this.b == null) {}
    while (this.a == null)
    {
      return;
      this.b.cancel();
      this.b = null;
    }
    this.a.cancel();
    this.a = null;
  }
  
  private void w()
  {
    if (e()) {}
    try
    {
      this.D.a(768);
      return;
    }
    catch (Throwable localThrowable)
    {
      localThrowable.printStackTrace();
      t.a(localThrowable);
    }
    return;
  }
  
  private void x()
  {
    if ((!e()) || (this.D.f() <= 0)) {}
    try
    {
      boolean bool = this.D.e();
      if (!bool) {}
      return;
    }
    catch (Throwable localThrowable)
    {
      localThrowable.printStackTrace();
    }
    return;
    return;
  }
  
  public int a(boolean paramBoolean, int paramInt)
  {
    if (paramBoolean) {
      c(paramInt);
    }
    while (!e())
    {
      return -1;
      v();
    }
    return this.D.f();
  }
  
  /* Error */
  public c a()
    throws Exception
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 78	com/aps/a:e	Landroid/content/Context;
    //   4: ifnull +234 -> 238
    //   7: getstatic 540	com/aps/f:d	Ljava/lang/String;
    //   10: invokestatic 487	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   13: ifne +235 -> 248
    //   16: getstatic 543	com/aps/f:e	Ljava/lang/String;
    //   19: invokestatic 487	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   22: ifne +237 -> 259
    //   25: ldc_w 1016
    //   28: aload_0
    //   29: getfield 118	com/aps/a:s	Lorg/json/JSONObject;
    //   32: invokestatic 184	com/aps/l:a	(Lorg/json/JSONObject;)[Ljava/lang/String;
    //   35: iconst_0
    //   36: aaload
    //   37: invokevirtual 192	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   40: ifne +230 -> 270
    //   43: aload_0
    //   44: invokespecial 912	com/aps/a:k	()Z
    //   47: ifne +244 -> 291
    //   50: aload_0
    //   51: invokespecial 914	com/aps/a:l	()Z
    //   54: ifne +250 -> 304
    //   57: aload_0
    //   58: aload_0
    //   59: getfield 141	com/aps/a:B	I
    //   62: iconst_1
    //   63: iadd
    //   64: putfield 141	com/aps/a:B	I
    //   67: aload_0
    //   68: getfield 141	com/aps/a:B	I
    //   71: iconst_1
    //   72: if_icmpgt +239 -> 311
    //   75: aload_0
    //   76: getfield 141	com/aps/a:B	I
    //   79: iconst_1
    //   80: if_icmpeq +238 -> 318
    //   83: aload_0
    //   84: getfield 93	com/aps/a:k	Ljava/util/List;
    //   87: ifnull +270 -> 357
    //   90: aload_0
    //   91: getfield 141	com/aps/a:B	I
    //   94: iconst_1
    //   95: if_icmpeq +276 -> 371
    //   98: aload_0
    //   99: aload_0
    //   100: getfield 124	com/aps/a:v	J
    //   103: invokespecial 1018	com/aps/a:a	(J)Z
    //   106: ifne +330 -> 436
    //   109: aload_0
    //   110: aload_0
    //   111: getfield 158	com/aps/a:H	Landroid/telephony/CellLocation;
    //   114: invokespecial 1020	com/aps/a:a	(Landroid/telephony/CellLocation;)V
    //   117: aload_0
    //   118: aload_0
    //   119: getfield 93	com/aps/a:k	Ljava/util/List;
    //   122: invokespecial 1022	com/aps/a:a	(Ljava/util/List;)V
    //   125: aload_0
    //   126: invokespecial 1023	com/aps/a:h	()Ljava/lang/String;
    //   129: astore 6
    //   131: aload 6
    //   133: invokestatic 487	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   136: ifne +329 -> 465
    //   139: aload_0
    //   140: invokespecial 1025	com/aps/a:i	()Ljava/lang/StringBuilder;
    //   143: astore 7
    //   145: aload_0
    //   146: getfield 78	com/aps/a:e	Landroid/content/Context;
    //   149: invokestatic 1030	com/aps/d:a	(Landroid/content/Context;)Lcom/aps/d;
    //   152: aload 6
    //   154: aload 7
    //   156: ldc_w 1032
    //   159: invokevirtual 1035	com/aps/d:a	(Ljava/lang/String;Ljava/lang/StringBuilder;Ljava/lang/String;)Lcom/aps/c;
    //   162: astore 4
    //   164: aload 4
    //   166: ifnonnull +318 -> 484
    //   169: iconst_0
    //   170: istore_1
    //   171: aload 4
    //   173: ifnonnull +350 -> 523
    //   176: aload_0
    //   177: invokespecial 1037	com/aps/a:t	()Lcom/aps/c;
    //   180: astore 5
    //   182: aload_0
    //   183: aload 5
    //   185: putfield 122	com/aps/a:u	Lcom/aps/c;
    //   188: aload_0
    //   189: getfield 78	com/aps/a:e	Landroid/content/Context;
    //   192: invokestatic 1030	com/aps/d:a	(Landroid/content/Context;)Lcom/aps/d;
    //   195: aload 6
    //   197: aload_0
    //   198: getfield 122	com/aps/a:u	Lcom/aps/c;
    //   201: aload 7
    //   203: invokevirtual 1040	com/aps/d:a	(Ljava/lang/String;Lcom/aps/c;Ljava/lang/StringBuilder;)V
    //   206: aload 7
    //   208: iconst_0
    //   209: aload 7
    //   211: invokevirtual 234	java/lang/StringBuilder:length	()I
    //   214: invokevirtual 362	java/lang/StringBuilder:delete	(II)Ljava/lang/StringBuilder;
    //   217: pop
    //   218: aload_0
    //   219: invokestatic 471	com/aps/t:a	()J
    //   222: putfield 124	com/aps/a:v	J
    //   225: aload_0
    //   226: invokespecial 1042	com/aps/a:q	()V
    //   229: aload_0
    //   230: invokevirtual 1044	com/aps/a:d	()V
    //   233: aload_0
    //   234: getfield 122	com/aps/a:u	Lcom/aps/c;
    //   237: areturn
    //   238: new 170	com/amap/api/location/core/AMapLocException
    //   241: dup
    //   242: ldc -28
    //   244: invokespecial 230	com/amap/api/location/core/AMapLocException:<init>	(Ljava/lang/String;)V
    //   247: athrow
    //   248: new 170	com/amap/api/location/core/AMapLocException
    //   251: dup
    //   252: ldc_w 1046
    //   255: invokespecial 230	com/amap/api/location/core/AMapLocException:<init>	(Ljava/lang/String;)V
    //   258: athrow
    //   259: new 170	com/amap/api/location/core/AMapLocException
    //   262: dup
    //   263: ldc_w 1046
    //   266: invokespecial 230	com/amap/api/location/core/AMapLocException:<init>	(Ljava/lang/String;)V
    //   269: athrow
    //   270: ldc_w 1048
    //   273: ldc_w 1046
    //   276: invokestatic 1053	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   279: pop
    //   280: new 170	com/amap/api/location/core/AMapLocException
    //   283: dup
    //   284: ldc_w 1046
    //   287: invokespecial 230	com/amap/api/location/core/AMapLocException:<init>	(Ljava/lang/String;)V
    //   290: athrow
    //   291: invokestatic 867	android/telephony/CellLocation:requestLocationUpdate	()V
    //   294: aload_0
    //   295: invokestatic 471	com/aps/t:a	()J
    //   298: putfield 130	com/aps/a:y	J
    //   301: goto -251 -> 50
    //   304: aload_0
    //   305: invokespecial 859	com/aps/a:o	()V
    //   308: goto -251 -> 57
    //   311: aload_0
    //   312: invokevirtual 1055	com/aps/a:c	()V
    //   315: goto -240 -> 75
    //   318: aload_0
    //   319: invokestatic 1060	java/lang/System:currentTimeMillis	()J
    //   322: putfield 156	com/aps/a:G	J
    //   325: aload_0
    //   326: aload_0
    //   327: getfield 78	com/aps/a:e	Landroid/content/Context;
    //   330: invokestatic 1063	com/aps/t:a	(Landroid/content/Context;)Z
    //   333: putfield 126	com/aps/a:w	Z
    //   336: aload_0
    //   337: getfield 84	com/aps/a:h	Landroid/net/wifi/WifiManager;
    //   340: ifnull -257 -> 83
    //   343: aload_0
    //   344: aload_0
    //   345: getfield 84	com/aps/a:h	Landroid/net/wifi/WifiManager;
    //   348: invokevirtual 1066	android/net/wifi/WifiManager:getScanResults	()Ljava/util/List;
    //   351: putfield 93	com/aps/a:k	Ljava/util/List;
    //   354: goto -271 -> 83
    //   357: aload_0
    //   358: new 88	java/util/ArrayList
    //   361: dup
    //   362: invokespecial 89	java/util/ArrayList:<init>	()V
    //   365: putfield 93	com/aps/a:k	Ljava/util/List;
    //   368: goto -278 -> 90
    //   371: aload_0
    //   372: invokespecial 627	com/aps/a:s	()Z
    //   375: ifeq -277 -> 98
    //   378: aload_0
    //   379: getfield 156	com/aps/a:G	J
    //   382: aload_0
    //   383: getfield 154	com/aps/a:F	J
    //   386: lsub
    //   387: ldc2_w 924
    //   390: lcmp
    //   391: iflt +40 -> 431
    //   394: iconst_1
    //   395: istore_1
    //   396: iload_1
    //   397: ifne -299 -> 98
    //   400: iconst_4
    //   401: istore_1
    //   402: iload_1
    //   403: ifle -305 -> 98
    //   406: aload_0
    //   407: getfield 93	com/aps/a:k	Ljava/util/List;
    //   410: invokeinterface 372 1 0
    //   415: ifne -317 -> 98
    //   418: ldc2_w 1067
    //   421: invokestatic 1074	android/os/SystemClock:sleep	(J)V
    //   424: iload_1
    //   425: iconst_1
    //   426: isub
    //   427: istore_1
    //   428: goto -26 -> 402
    //   431: iconst_0
    //   432: istore_1
    //   433: goto -37 -> 396
    //   436: aload_0
    //   437: getfield 122	com/aps/a:u	Lcom/aps/c;
    //   440: ifnull -331 -> 109
    //   443: aload_0
    //   444: invokestatic 471	com/aps/t:a	()J
    //   447: putfield 124	com/aps/a:v	J
    //   450: aload_0
    //   451: getfield 122	com/aps/a:u	Lcom/aps/c;
    //   454: areturn
    //   455: astore 4
    //   457: aload 4
    //   459: invokevirtual 276	java/lang/Throwable:printStackTrace	()V
    //   462: goto -345 -> 117
    //   465: new 170	com/amap/api/location/core/AMapLocException
    //   468: dup
    //   469: ldc_w 1076
    //   472: invokespecial 230	com/amap/api/location/core/AMapLocException:<init>	(Ljava/lang/String;)V
    //   475: athrow
    //   476: astore 4
    //   478: aconst_null
    //   479: astore 4
    //   481: goto -317 -> 164
    //   484: aload 4
    //   486: invokevirtual 477	com/aps/c:h	()J
    //   489: lstore_2
    //   490: invokestatic 471	com/aps/t:a	()J
    //   493: lload_2
    //   494: lsub
    //   495: ldc2_w 1077
    //   498: lcmp
    //   499: ifgt +14 -> 513
    //   502: iconst_1
    //   503: istore_1
    //   504: iload_1
    //   505: ifne +13 -> 518
    //   508: iconst_1
    //   509: istore_1
    //   510: goto -339 -> 171
    //   513: iconst_0
    //   514: istore_1
    //   515: goto -11 -> 504
    //   518: iconst_0
    //   519: istore_1
    //   520: goto -349 -> 171
    //   523: iload_1
    //   524: ifne -348 -> 176
    //   527: aload_0
    //   528: aload 4
    //   530: putfield 122	com/aps/a:u	Lcom/aps/c;
    //   533: goto -345 -> 188
    //   536: astore 8
    //   538: aload 4
    //   540: astore 5
    //   542: aload 4
    //   544: ifnonnull -362 -> 182
    //   547: aload 8
    //   549: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	550	0	this	a
    //   170	354	1	i1	int
    //   489	5	2	l1	long
    //   162	10	4	localc1	c
    //   455	3	4	localThrowable1	Throwable
    //   476	1	4	localThrowable2	Throwable
    //   479	64	4	localc2	c
    //   180	361	5	localObject	Object
    //   129	67	6	str	String
    //   143	67	7	localStringBuilder	StringBuilder
    //   536	12	8	localAMapLocException	AMapLocException
    // Exception table:
    //   from	to	target	type
    //   109	117	455	java/lang/Throwable
    //   145	164	476	java/lang/Throwable
    //   176	182	536	com/amap/api/location/core/AMapLocException
  }
  
  public void a(PendingIntent paramPendingIntent)
  {
    if (paramPendingIntent != null)
    {
      this.l.remove(paramPendingIntent);
      return;
    }
  }
  
  public void a(Context paramContext)
  {
    if (paramContext != null)
    {
      if (this.e == null)
      {
        this.e = paramContext.getApplicationContext();
        t.a(this.e, "in debug mode, only for test");
        f();
        g();
        this.F = System.currentTimeMillis();
      }
    }
    else {}
  }
  
  public void a(AMapLocation paramAMapLocation)
  {
    if (paramAMapLocation == null) {}
    while (this.m.size() < 1) {
      return;
    }
    Iterator localIterator = this.m.entrySet().iterator();
    if (localIterator == null) {}
    while (!localIterator.hasNext()) {
      return;
    }
    Object localObject1 = (Map.Entry)localIterator.next();
    PendingIntent localPendingIntent = (PendingIntent)((Map.Entry)localObject1).getKey();
    Object localObject2 = (List)((Map.Entry)localObject1).getValue();
    localObject1 = new Intent();
    Bundle localBundle = new Bundle();
    localObject2 = ((List)localObject2).iterator();
    label113:
    label320:
    label323:
    for (;;)
    {
      j localj;
      if (((Iterator)localObject2).hasNext())
      {
        localj = (j)((Iterator)localObject2).next();
        long l1 = localj.a();
        if (l1 != -1L) {
          if (l1 < t.a()) {
            break label320;
          }
        }
      }
      for (int i1 = 1;; i1 = 0)
      {
        if (i1 == 0) {
          break label323;
        }
        float f1 = t.a(new double[] { localj.b, localj.a, paramAMapLocation.getLatitude(), paramAMapLocation.getLongitude() });
        if (f1 >= localj.c)
        {
          if (localj.d == 0) {
            break label113;
          }
          localj.d = 0;
        }
        if (f1 < localj.c)
        {
          if (localj.d == 1) {
            break label113;
          }
          localj.d = 1;
        }
        localBundle.putFloat("distance", f1);
        localBundle.putString("fence", localj.b());
        localBundle.putInt("status", localj.d);
        ((Intent)localObject1).putExtras(localBundle);
        try
        {
          localPendingIntent.send(this.e, 0, (Intent)localObject1);
        }
        catch (Throwable localThrowable)
        {
          localThrowable.printStackTrace();
        }
        break label113;
        break;
      }
    }
  }
  
  public void a(j paramj, PendingIntent paramPendingIntent)
  {
    if (paramPendingIntent == null) {}
    while (paramj == null) {
      return;
    }
    long l1 = paramj.a();
    if (l1 != -1L)
    {
      if (l1 >= t.a()) {}
      for (int i1 = 1; i1 == 0; i1 = 0) {
        return;
      }
    }
    if (this.l.get(paramPendingIntent) == null)
    {
      localObject = new ArrayList();
      ((List)localObject).add(paramj);
      this.l.put(paramPendingIntent, localObject);
      return;
    }
    Object localObject = (List)this.l.get(paramPendingIntent);
    ((List)localObject).add(paramj);
    this.l.put(paramPendingIntent, localObject);
  }
  
  public void a(String paramString)
  {
    if (paramString == null) {}
    while (paramString.indexOf("##") == -1) {
      return;
    }
    paramString = paramString.split("##");
    if (paramString.length == 3)
    {
      f.a(paramString[0]);
      if (!f.e.equals(paramString[1])) {
        break label62;
      }
    }
    for (;;)
    {
      f.b(paramString[1]);
      f.c(paramString[2]);
      return;
      return;
      label62:
      d.a(this.e).a();
    }
  }
  
  public void a(JSONObject paramJSONObject)
  {
    this.s = paramJSONObject;
  }
  
  /* Error */
  public void b()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 147	com/aps/a:D	Lcom/aps/y;
    //   4: astore_1
    //   5: aload_1
    //   6: ifnonnull +104 -> 110
    //   9: aload_0
    //   10: getfield 78	com/aps/a:e	Landroid/content/Context;
    //   13: astore_1
    //   14: aload_1
    //   15: ifnonnull +118 -> 133
    //   18: aload_0
    //   19: aconst_null
    //   20: putfield 114	com/aps/a:q	Lcom/aps/a$a;
    //   23: aload_0
    //   24: invokespecial 808	com/aps/a:v	()V
    //   27: aload_0
    //   28: getfield 86	com/aps/a:i	Landroid/telephony/TelephonyManager;
    //   31: astore_1
    //   32: aload_1
    //   33: ifnonnull +130 -> 163
    //   36: aload_0
    //   37: getfield 78	com/aps/a:e	Landroid/content/Context;
    //   40: ifnonnull +157 -> 197
    //   43: iconst_0
    //   44: invokestatic 1133	com/aps/f:a	(Z)V
    //   47: aload_0
    //   48: lconst_0
    //   49: putfield 124	com/aps/a:v	J
    //   52: aload_0
    //   53: getfield 91	com/aps/a:j	Ljava/util/List;
    //   56: invokeinterface 390 1 0
    //   61: aload_0
    //   62: getfield 98	com/aps/a:l	Ljava/util/Map;
    //   65: invokeinterface 1134 1 0
    //   70: aload_0
    //   71: getfield 100	com/aps/a:m	Ljava/util/Map;
    //   74: invokeinterface 1134 1 0
    //   79: aload_0
    //   80: bipush -113
    //   82: putfield 109	com/aps/a:p	I
    //   85: aload_0
    //   86: invokespecial 629	com/aps/a:n	()V
    //   89: aload_0
    //   90: aconst_null
    //   91: putfield 120	com/aps/a:t	Ljava/lang/String;
    //   94: aload_0
    //   95: aconst_null
    //   96: putfield 122	com/aps/a:u	Lcom/aps/c;
    //   99: aload_0
    //   100: aconst_null
    //   101: putfield 78	com/aps/a:e	Landroid/content/Context;
    //   104: aload_0
    //   105: aconst_null
    //   106: putfield 86	com/aps/a:i	Landroid/telephony/TelephonyManager;
    //   109: return
    //   110: aload_0
    //   111: getfield 147	com/aps/a:D	Lcom/aps/y;
    //   114: invokevirtual 1135	com/aps/y:c	()V
    //   117: aload_0
    //   118: iconst_0
    //   119: putfield 160	com/aps/a:I	Z
    //   122: goto -113 -> 9
    //   125: astore_1
    //   126: aload_1
    //   127: invokevirtual 276	java/lang/Throwable:printStackTrace	()V
    //   130: goto -121 -> 9
    //   133: aload_0
    //   134: getfield 78	com/aps/a:e	Landroid/content/Context;
    //   137: astore_1
    //   138: aload_0
    //   139: getfield 114	com/aps/a:q	Lcom/aps/a$a;
    //   142: astore_2
    //   143: aload_1
    //   144: aload_2
    //   145: invokevirtual 1139	android/content/Context:unregisterReceiver	(Landroid/content/BroadcastReceiver;)V
    //   148: goto -130 -> 18
    //   151: astore_1
    //   152: goto -134 -> 18
    //   155: astore_1
    //   156: aload_0
    //   157: aconst_null
    //   158: putfield 114	com/aps/a:q	Lcom/aps/a$a;
    //   161: aload_1
    //   162: athrow
    //   163: aload_0
    //   164: getfield 107	com/aps/a:o	Landroid/telephony/PhoneStateListener;
    //   167: ifnull -131 -> 36
    //   170: aload_0
    //   171: getfield 86	com/aps/a:i	Landroid/telephony/TelephonyManager;
    //   174: aload_0
    //   175: getfield 107	com/aps/a:o	Landroid/telephony/PhoneStateListener;
    //   178: iconst_0
    //   179: invokevirtual 878	android/telephony/TelephonyManager:listen	(Landroid/telephony/PhoneStateListener;I)V
    //   182: goto -146 -> 36
    //   185: astore_1
    //   186: aload_1
    //   187: invokevirtual 276	java/lang/Throwable:printStackTrace	()V
    //   190: aload_1
    //   191: invokestatic 467	com/aps/t:a	(Ljava/lang/Throwable;)V
    //   194: goto -158 -> 36
    //   197: aload_0
    //   198: getfield 78	com/aps/a:e	Landroid/content/Context;
    //   201: invokestatic 1030	com/aps/d:a	(Landroid/content/Context;)Lcom/aps/d;
    //   204: invokevirtual 1130	com/aps/d:a	()V
    //   207: aload_0
    //   208: getfield 78	com/aps/a:e	Landroid/content/Context;
    //   211: invokestatic 1030	com/aps/d:a	(Landroid/content/Context;)Lcom/aps/d;
    //   214: invokevirtual 1141	com/aps/d:b	()V
    //   217: goto -174 -> 43
    //   220: astore_1
    //   221: goto -203 -> 18
    //   224: astore_1
    //   225: goto -207 -> 18
    //   228: astore_1
    //   229: goto -211 -> 18
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	232	0	this	a
    //   4	29	1	localObject1	Object
    //   125	2	1	localThrowable1	Throwable
    //   137	7	1	localContext	Context
    //   151	1	1	localThrowable2	Throwable
    //   155	7	1	localObject2	Object
    //   185	6	1	localThrowable3	Throwable
    //   220	1	1	localThrowable4	Throwable
    //   224	1	1	localThrowable5	Throwable
    //   228	1	1	localThrowable6	Throwable
    //   142	3	2	locala	a
    // Exception table:
    //   from	to	target	type
    //   0	5	125	java/lang/Throwable
    //   110	122	125	java/lang/Throwable
    //   143	148	151	java/lang/Throwable
    //   9	14	155	finally
    //   133	138	155	finally
    //   138	143	155	finally
    //   143	148	155	finally
    //   27	32	185	java/lang/Throwable
    //   163	182	185	java/lang/Throwable
    //   9	14	220	java/lang/Throwable
    //   133	138	224	java/lang/Throwable
    //   138	143	228	java/lang/Throwable
  }
  
  public void b(PendingIntent paramPendingIntent)
  {
    if (paramPendingIntent != null)
    {
      this.m.remove(paramPendingIntent);
      return;
    }
  }
  
  public void b(j paramj, PendingIntent paramPendingIntent)
  {
    if (paramPendingIntent == null) {}
    while (paramj == null) {
      return;
    }
    long l1 = paramj.a();
    if (l1 != -1L)
    {
      if (l1 >= t.a()) {}
      for (int i1 = 1; i1 == 0; i1 = 0) {
        return;
      }
    }
    if (this.m.get(paramPendingIntent) == null)
    {
      localObject = new ArrayList();
      ((List)localObject).add(paramj);
      this.m.put(paramPendingIntent, localObject);
      return;
    }
    Object localObject = (List)this.m.get(paramPendingIntent);
    ((List)localObject).add(paramj);
    this.m.put(paramPendingIntent, localObject);
  }
  
  public void c()
  {
    try
    {
      if (this.D != null) {}
      while (this.I)
      {
        return;
        this.D = y.a(this.e);
        this.D.a(256);
      }
      this.I = true;
    }
    catch (Throwable localThrowable)
    {
      localThrowable.printStackTrace();
      return;
    }
    this.D.a();
  }
  
  public void d()
  {
    if (this.u == null) {}
    while (this.m.size() < 1) {
      return;
    }
    Iterator localIterator = this.m.entrySet().iterator();
    if (localIterator == null) {}
    while (!localIterator.hasNext()) {
      return;
    }
    Object localObject1 = (Map.Entry)localIterator.next();
    PendingIntent localPendingIntent = (PendingIntent)((Map.Entry)localObject1).getKey();
    Object localObject2 = (List)((Map.Entry)localObject1).getValue();
    localObject1 = new Intent();
    Bundle localBundle = new Bundle();
    localObject2 = ((List)localObject2).iterator();
    label116:
    label326:
    label329:
    for (;;)
    {
      j localj;
      if (((Iterator)localObject2).hasNext())
      {
        localj = (j)((Iterator)localObject2).next();
        long l1 = localj.a();
        if (l1 != -1L) {
          if (l1 < t.a()) {
            break label326;
          }
        }
      }
      for (int i1 = 1;; i1 = 0)
      {
        if (i1 == 0) {
          break label329;
        }
        float f1 = t.a(new double[] { localj.b, localj.a, this.u.f(), this.u.e() });
        if (f1 >= localj.c)
        {
          if (localj.d == 0) {
            break label116;
          }
          localj.d = 0;
        }
        if (f1 < localj.c)
        {
          if (localj.d == 1) {
            break label116;
          }
          localj.d = 1;
        }
        localBundle.putFloat("distance", f1);
        localBundle.putString("fence", localj.b());
        localBundle.putInt("status", localj.d);
        ((Intent)localObject1).putExtras(localBundle);
        try
        {
          localPendingIntent.send(this.e, 0, (Intent)localObject1);
        }
        catch (Throwable localThrowable)
        {
          localThrowable.printStackTrace();
        }
        break label116;
        break;
      }
    }
  }
  
  boolean e()
  {
    return this.D != null;
  }
  
  private class a
    extends BroadcastReceiver
  {
    private a() {}
    
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      label136:
      label225:
      label248:
      label272:
      label285:
      label300:
      if (paramIntent != null)
      {
        try
        {
          paramIntent = paramIntent.getAction();
          if (!paramIntent.equals("android.net.wifi.SCAN_RESULTS"))
          {
            if (paramIntent.equals("android.net.wifi.WIFI_STATE_CHANGED")) {
              break label136;
            }
            if (paramIntent.equals("android.intent.action.SCREEN_ON")) {
              break label225;
            }
            if (paramIntent.equals("android.intent.action.SCREEN_OFF")) {
              break label248;
            }
            if (paramIntent.equals("android.intent.action.AIRPLANE_MODE")) {
              break label272;
            }
            if (paramIntent.equals("android.net.conn.CONNECTIVITY_CHANGE")) {
              break label285;
            }
            return;
          }
          if (a.d(a.this) != null)
          {
            a.a(a.this, a.d(a.this).getScanResults());
            a.c(a.this, t.a());
            if (a.e(a.this) != null) {
              break label300;
            }
            a.a(a.this, new ArrayList());
            return;
          }
        }
        catch (Throwable paramContext)
        {
          paramContext.printStackTrace();
          return;
        }
        return;
        paramContext = a.d(a.this);
        if (paramContext != null)
        {
          int i = 4;
          try
          {
            int j = a.d(a.this).getWifiState();
            i = j;
          }
          catch (SecurityException paramContext)
          {
            for (;;) {}
          }
          switch (i)
          {
          default: 
            return;
          case 0: 
            a.f(a.this);
            return;
          case 1: 
            a.f(a.this);
            return;
          case 4: 
            a.f(a.this);
            return;
            CellLocation.requestLocationUpdate();
            a.g(a.this);
            f.i = 10000L;
            f.j = 30000L;
            return;
            if (a.h(a.this) >= 5)
            {
              f.i = 20000L;
              f.j = 60000L;
              return;
              a.a(a.this, t.a(paramContext));
              return;
              a.this.a(true, 2);
              return;
            }
            break;
          }
        }
      }
      else {}
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/aps/a.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */