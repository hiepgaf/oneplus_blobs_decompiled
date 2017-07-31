package com.aps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.GpsStatus.NmeaListener;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Looper;
import android.provider.Settings.Secure;
import android.provider.Settings.System;
import android.telephony.CellLocation;
import android.telephony.NeighboringCellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TreeMap;

public final class ak
{
  private static int D = 10000;
  private static ak u = null;
  private Timer A = null;
  private Thread B = null;
  private Looper C = null;
  private Context a = null;
  private TelephonyManager b = null;
  private LocationManager c = null;
  private WifiManager d = null;
  private SensorManager e = null;
  private String f = "";
  private String g = "";
  private String h = "";
  private boolean i = false;
  private int j = 0;
  private boolean k = false;
  private long l = -1L;
  private String m = "";
  private String n = "";
  private int o = 0;
  private int p = 0;
  private int q = 0;
  private String r = "";
  private long s = 0L;
  private long t = 0L;
  private am v = null;
  private an w = null;
  private CellLocation x = null;
  private ao y = null;
  private List z = new ArrayList();
  
  private ak(Context paramContext)
  {
    if (paramContext != null)
    {
      this.a = paramContext;
      this.f = Build.MODEL;
      this.b = ((TelephonyManager)paramContext.getSystemService("phone"));
      this.c = ((LocationManager)paramContext.getSystemService("location"));
      this.d = ((WifiManager)paramContext.getSystemService("wifi"));
      this.e = ((SensorManager)paramContext.getSystemService("sensor"));
      if (this.b != null) {
        break label235;
      }
    }
    label235:
    while (this.d == null)
    {
      return;
      return;
    }
    try
    {
      this.g = this.b.getDeviceId();
      this.h = this.b.getSubscriberId();
      if (this.d.getConnectionInfo() == null)
      {
        String[] arrayOfString = b(this.b);
        this.o = Integer.parseInt(arrayOfString[0]);
        this.p = Integer.parseInt(arrayOfString[1]);
        this.q = this.b.getNetworkType();
        this.r = paramContext.getPackageName();
        if (this.b.getPhoneType() == 2) {
          break label389;
        }
      }
      label389:
      for (boolean bool = false;; bool = true)
      {
        this.i = bool;
        return;
        this.n = this.d.getConnectionInfo().getMacAddress();
        if ((this.n == null) || (this.n.length() <= 0)) {
          break;
        }
        this.n = this.n.replace(":", "");
        break;
      }
    }
    catch (Exception localException)
    {
      for (;;) {}
    }
  }
  
  private void A()
  {
    if (this.d != null) {
      try
      {
        if (!bd.a) {
          return;
        }
        this.d.startScan();
        return;
      }
      catch (Exception localException) {}
    }
  }
  
  private CellLocation B()
  {
    if (this.b != null) {}
    try
    {
      CellLocation localCellLocation = b((List)ah.a(this.b, "getAllCellInfo", new Object[0]));
      return localCellLocation;
    }
    catch (Exception localException)
    {
      return null;
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      for (;;) {}
    }
    return null;
  }
  
  private static int a(CellLocation paramCellLocation, Context paramContext)
  {
    int i2 = 9;
    int i1 = i2;
    if (Settings.System.getInt(paramContext.getContentResolver(), "airplane_mode_on", 0) != 1)
    {
      i1 = i2;
      if ((paramCellLocation != null) && ((paramCellLocation instanceof GsmCellLocation))) {
        break label42;
      }
    }
    label42:
    try
    {
      Class.forName("android.telephony.cdma.CdmaCellLocation");
      i1 = 2;
      return i1;
    }
    catch (Exception paramCellLocation) {}
    return 1;
    return 9;
  }
  
  protected static ak a(Context paramContext)
  {
    if (u != null) {}
    while (!c(paramContext)) {
      return u;
    }
    Object localObject = (LocationManager)paramContext.getSystemService("location");
    if (localObject == null) {}
    label33:
    label106:
    for (;;)
    {
      int i1 = 0;
      if (i1 != 0)
      {
        u = new ak(paramContext);
        break;
        localObject = ((LocationManager)localObject).getAllProviders().iterator();
      }
      for (;;)
      {
        if (!((Iterator)localObject).hasNext()) {
          break label106;
        }
        String str = (String)((Iterator)localObject).next();
        if (str.equals("passive")) {}
        while (str.equals("gps"))
        {
          i1 = 1;
          break label33;
          break;
        }
      }
    }
  }
  
  private void a(BroadcastReceiver paramBroadcastReceiver)
  {
    if (paramBroadcastReceiver == null) {}
    while (this.a == null) {
      return;
    }
    IntentFilter localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("android.net.wifi.SCAN_RESULTS");
    this.a.registerReceiver(paramBroadcastReceiver, localIntentFilter);
  }
  
  private static void a(List paramList)
  {
    if (paramList == null) {}
    while (paramList.size() <= 0) {
      return;
    }
    HashMap localHashMap = new HashMap();
    int i1 = 0;
    Object localObject;
    Iterator localIterator;
    if (i1 >= paramList.size())
    {
      localObject = new TreeMap(Collections.reverseOrder());
      ((TreeMap)localObject).putAll(localHashMap);
      paramList.clear();
      localIterator = ((TreeMap)localObject).keySet().iterator();
    }
    for (;;)
    {
      if (!localIterator.hasNext())
      {
        localHashMap.clear();
        ((TreeMap)localObject).clear();
        return;
        localObject = (ScanResult)paramList.get(i1);
        if (((ScanResult)localObject).SSID != null) {}
        for (;;)
        {
          localHashMap.put(Integer.valueOf(((ScanResult)localObject).level), localObject);
          i1 += 1;
          break;
          ((ScanResult)localObject).SSID = "null";
        }
      }
      paramList.add(((TreeMap)localObject).get((Integer)localIterator.next()));
    }
  }
  
  private boolean a(CellLocation paramCellLocation)
  {
    boolean bool2 = false;
    boolean bool3;
    boolean bool1;
    if (paramCellLocation != null)
    {
      bool3 = true;
      switch (a(paramCellLocation, this.a))
      {
      default: 
        bool1 = bool3;
      }
    }
    for (;;)
    {
      return bool1;
      return false;
      paramCellLocation = (GsmCellLocation)paramCellLocation;
      bool1 = bool2;
      if (paramCellLocation.getLac() != -1)
      {
        bool1 = bool2;
        if (paramCellLocation.getLac() != 0)
        {
          bool1 = bool2;
          if (paramCellLocation.getLac() <= 65535)
          {
            bool1 = bool2;
            if (paramCellLocation.getCid() != -1)
            {
              bool1 = bool2;
              if (paramCellLocation.getCid() != 0)
              {
                bool1 = bool2;
                if (paramCellLocation.getCid() != 65535)
                {
                  bool1 = bool2;
                  if (paramCellLocation.getCid() < 268435455)
                  {
                    bool1 = bool3;
                    continue;
                    bool1 = bool2;
                    try
                    {
                      if (ah.b(paramCellLocation, "getSystemId", new Object[0]) > 0)
                      {
                        bool1 = bool2;
                        if (ah.b(paramCellLocation, "getNetworkId", new Object[0]) >= 0)
                        {
                          int i1 = ah.b(paramCellLocation, "getBaseStationId", new Object[0]);
                          bool1 = bool3;
                          if (i1 < 0) {
                            bool1 = false;
                          }
                        }
                      }
                    }
                    catch (Exception paramCellLocation)
                    {
                      bool1 = bool3;
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
  
  private static boolean a(Object paramObject)
  {
    try
    {
      Method localMethod = WifiManager.class.getDeclaredMethod("isScanAlwaysAvailable", null);
      if (localMethod != null)
      {
        boolean bool = ((Boolean)localMethod.invoke(paramObject, null)).booleanValue();
        return bool;
      }
    }
    catch (Exception paramObject) {}
    return false;
  }
  
  private static int b(Object paramObject)
  {
    try
    {
      Method localMethod = Sensor.class.getDeclaredMethod("getMinDelay", null);
      if (localMethod != null)
      {
        int i1 = ((Integer)localMethod.invoke(paramObject, null)).intValue();
        return i1;
      }
    }
    catch (Exception paramObject) {}
    return 0;
  }
  
  /* Error */
  private static CellLocation b(List paramList)
  {
    // Byte code:
    //   0: aload_0
    //   1: ifnonnull +5 -> 6
    //   4: aconst_null
    //   5: areturn
    //   6: aload_0
    //   7: invokeinterface 422 1 0
    //   12: ifne -8 -> 4
    //   15: invokestatic 428	java/lang/ClassLoader:getSystemClassLoader	()Ljava/lang/ClassLoader;
    //   18: astore 11
    //   20: iconst_0
    //   21: istore_2
    //   22: aconst_null
    //   23: astore 9
    //   25: iconst_0
    //   26: istore_1
    //   27: aconst_null
    //   28: astore 8
    //   30: iload_2
    //   31: aload_0
    //   32: invokeinterface 315 1 0
    //   37: if_icmplt +11 -> 48
    //   40: iload_1
    //   41: iconst_4
    //   42: if_icmpeq +451 -> 493
    //   45: aload 9
    //   47: areturn
    //   48: aload_0
    //   49: iload_2
    //   50: invokeinterface 349 2 0
    //   55: astore 12
    //   57: aload 12
    //   59: ifnonnull +10 -> 69
    //   62: iload_2
    //   63: iconst_1
    //   64: iadd
    //   65: istore_2
    //   66: goto -36 -> 30
    //   69: aload 11
    //   71: ldc_w 430
    //   74: invokevirtual 433	java/lang/ClassLoader:loadClass	(Ljava/lang/String;)Ljava/lang/Class;
    //   77: astore 13
    //   79: aload 11
    //   81: ldc_w 435
    //   84: invokevirtual 433	java/lang/ClassLoader:loadClass	(Ljava/lang/String;)Ljava/lang/Class;
    //   87: astore 14
    //   89: aload 11
    //   91: ldc_w 437
    //   94: invokevirtual 433	java/lang/ClassLoader:loadClass	(Ljava/lang/String;)Ljava/lang/Class;
    //   97: astore 15
    //   99: aload 11
    //   101: ldc_w 439
    //   104: invokevirtual 433	java/lang/ClassLoader:loadClass	(Ljava/lang/String;)Ljava/lang/Class;
    //   107: astore 16
    //   109: aload 13
    //   111: aload 12
    //   113: invokevirtual 442	java/lang/Class:isInstance	(Ljava/lang/Object;)Z
    //   116: ifne +46 -> 162
    //   119: aload 14
    //   121: aload 12
    //   123: invokevirtual 442	java/lang/Class:isInstance	(Ljava/lang/Object;)Z
    //   126: ifne +41 -> 167
    //   129: aload 15
    //   131: aload 12
    //   133: invokevirtual 442	java/lang/Class:isInstance	(Ljava/lang/Object;)Z
    //   136: ifne +36 -> 172
    //   139: aload 16
    //   141: aload 12
    //   143: invokevirtual 442	java/lang/Class:isInstance	(Ljava/lang/Object;)Z
    //   146: istore 7
    //   148: iload 7
    //   150: ifne +27 -> 177
    //   153: iconst_0
    //   154: istore_1
    //   155: iload_1
    //   156: ifgt +26 -> 182
    //   159: goto -97 -> 62
    //   162: iconst_1
    //   163: istore_1
    //   164: goto -9 -> 155
    //   167: iconst_2
    //   168: istore_1
    //   169: goto -14 -> 155
    //   172: iconst_3
    //   173: istore_1
    //   174: goto -19 -> 155
    //   177: iconst_4
    //   178: istore_1
    //   179: goto -24 -> 155
    //   182: aconst_null
    //   183: astore 10
    //   185: iload_1
    //   186: iconst_1
    //   187: if_icmpeq +104 -> 291
    //   190: iload_1
    //   191: iconst_2
    //   192: if_icmpeq +111 -> 303
    //   195: iload_1
    //   196: iconst_3
    //   197: if_icmpeq +118 -> 315
    //   200: iload_1
    //   201: iconst_4
    //   202: if_icmpeq +125 -> 327
    //   205: aload 10
    //   207: ldc_w 444
    //   210: iconst_0
    //   211: anewarray 4	java/lang/Object
    //   214: invokestatic 219	com/aps/ah:a	(Ljava/lang/Object;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/Object;
    //   217: astore 12
    //   219: aload 12
    //   221: ifnull -62 -> 159
    //   224: iload_1
    //   225: iconst_4
    //   226: if_icmpeq +113 -> 339
    //   229: iload_1
    //   230: iconst_3
    //   231: if_icmpeq +203 -> 434
    //   234: aload 12
    //   236: ldc_w 445
    //   239: iconst_0
    //   240: anewarray 4	java/lang/Object
    //   243: invokestatic 389	com/aps/ah:b	(Ljava/lang/Object;Ljava/lang/String;[Ljava/lang/Object;)I
    //   246: istore_3
    //   247: aload 12
    //   249: ldc_w 446
    //   252: iconst_0
    //   253: anewarray 4	java/lang/Object
    //   256: invokestatic 389	com/aps/ah:b	(Ljava/lang/Object;Ljava/lang/String;[Ljava/lang/Object;)I
    //   259: istore 4
    //   261: new 239	android/telephony/gsm/GsmCellLocation
    //   264: dup
    //   265: invokespecial 447	android/telephony/gsm/GsmCellLocation:<init>	()V
    //   268: astore 10
    //   270: aload 10
    //   272: astore 9
    //   274: aload 10
    //   276: iload_3
    //   277: iload 4
    //   279: invokevirtual 451	android/telephony/gsm/GsmCellLocation:setLacAndCid	(II)V
    //   282: aload 10
    //   284: astore_0
    //   285: aload_0
    //   286: astore 9
    //   288: goto -248 -> 40
    //   291: aload 13
    //   293: aload 12
    //   295: invokevirtual 454	java/lang/Class:cast	(Ljava/lang/Object;)Ljava/lang/Object;
    //   298: astore 10
    //   300: goto -95 -> 205
    //   303: aload 14
    //   305: aload 12
    //   307: invokevirtual 454	java/lang/Class:cast	(Ljava/lang/Object;)Ljava/lang/Object;
    //   310: astore 10
    //   312: goto -107 -> 205
    //   315: aload 15
    //   317: aload 12
    //   319: invokevirtual 454	java/lang/Class:cast	(Ljava/lang/Object;)Ljava/lang/Object;
    //   322: astore 10
    //   324: goto -119 -> 205
    //   327: aload 16
    //   329: aload 12
    //   331: invokevirtual 454	java/lang/Class:cast	(Ljava/lang/Object;)Ljava/lang/Object;
    //   334: astore 10
    //   336: goto -131 -> 205
    //   339: new 456	android/telephony/cdma/CdmaCellLocation
    //   342: dup
    //   343: invokespecial 457	android/telephony/cdma/CdmaCellLocation:<init>	()V
    //   346: astore 10
    //   348: aload 12
    //   350: ldc_w 386
    //   353: iconst_0
    //   354: anewarray 4	java/lang/Object
    //   357: invokestatic 389	com/aps/ah:b	(Ljava/lang/Object;Ljava/lang/String;[Ljava/lang/Object;)I
    //   360: istore_3
    //   361: aload 12
    //   363: ldc_w 391
    //   366: iconst_0
    //   367: anewarray 4	java/lang/Object
    //   370: invokestatic 389	com/aps/ah:b	(Ljava/lang/Object;Ljava/lang/String;[Ljava/lang/Object;)I
    //   373: istore 4
    //   375: aload 12
    //   377: ldc_w 459
    //   380: iconst_0
    //   381: anewarray 4	java/lang/Object
    //   384: invokestatic 389	com/aps/ah:b	(Ljava/lang/Object;Ljava/lang/String;[Ljava/lang/Object;)I
    //   387: istore 5
    //   389: aload 12
    //   391: ldc_w 461
    //   394: iconst_0
    //   395: anewarray 4	java/lang/Object
    //   398: invokestatic 389	com/aps/ah:b	(Ljava/lang/Object;Ljava/lang/String;[Ljava/lang/Object;)I
    //   401: istore 6
    //   403: aload 10
    //   405: iload 5
    //   407: aload 12
    //   409: ldc_w 463
    //   412: iconst_0
    //   413: anewarray 4	java/lang/Object
    //   416: invokestatic 389	com/aps/ah:b	(Ljava/lang/Object;Ljava/lang/String;[Ljava/lang/Object;)I
    //   419: iload 6
    //   421: iload_3
    //   422: iload 4
    //   424: invokevirtual 467	android/telephony/cdma/CdmaCellLocation:setCellLocationData	(IIIII)V
    //   427: aload 10
    //   429: astore 8
    //   431: goto -391 -> 40
    //   434: aload 12
    //   436: ldc_w 469
    //   439: iconst_0
    //   440: anewarray 4	java/lang/Object
    //   443: invokestatic 389	com/aps/ah:b	(Ljava/lang/Object;Ljava/lang/String;[Ljava/lang/Object;)I
    //   446: istore_3
    //   447: aload 12
    //   449: ldc_w 471
    //   452: iconst_0
    //   453: anewarray 4	java/lang/Object
    //   456: invokestatic 389	com/aps/ah:b	(Ljava/lang/Object;Ljava/lang/String;[Ljava/lang/Object;)I
    //   459: istore 4
    //   461: new 239	android/telephony/gsm/GsmCellLocation
    //   464: dup
    //   465: invokespecial 447	android/telephony/gsm/GsmCellLocation:<init>	()V
    //   468: astore 10
    //   470: aload 10
    //   472: astore 9
    //   474: aload 10
    //   476: iload_3
    //   477: iload 4
    //   479: invokevirtual 451	android/telephony/gsm/GsmCellLocation:setLacAndCid	(II)V
    //   482: aload 10
    //   484: astore_0
    //   485: goto -200 -> 285
    //   488: astore 10
    //   490: goto -428 -> 62
    //   493: aload 8
    //   495: areturn
    //   496: astore 10
    //   498: goto -436 -> 62
    //   501: astore 10
    //   503: goto -441 -> 62
    //   506: astore 8
    //   508: aload 10
    //   510: astore 8
    //   512: goto -450 -> 62
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	515	0	paramList	List
    //   26	206	1	i1	int
    //   21	45	2	i2	int
    //   246	231	3	i3	int
    //   259	219	4	i4	int
    //   387	19	5	i5	int
    //   401	19	6	i6	int
    //   146	3	7	bool	boolean
    //   28	466	8	localObject1	Object
    //   506	1	8	localException1	Exception
    //   510	1	8	localObject2	Object
    //   23	450	9	localObject3	Object
    //   183	300	10	localObject4	Object
    //   488	1	10	localException2	Exception
    //   496	1	10	localException3	Exception
    //   501	8	10	localException4	Exception
    //   18	82	11	localClassLoader	ClassLoader
    //   55	393	12	localObject5	Object
    //   77	215	13	localClass1	Class
    //   87	217	14	localClass2	Class
    //   97	219	15	localClass3	Class
    //   107	221	16	localClass4	Class
    // Exception table:
    //   from	to	target	type
    //   274	282	488	java/lang/Exception
    //   474	482	488	java/lang/Exception
    //   69	148	496	java/lang/Exception
    //   205	219	501	java/lang/Exception
    //   234	270	501	java/lang/Exception
    //   291	300	501	java/lang/Exception
    //   303	312	501	java/lang/Exception
    //   315	324	501	java/lang/Exception
    //   327	336	501	java/lang/Exception
    //   339	348	501	java/lang/Exception
    //   434	470	501	java/lang/Exception
    //   348	427	506	java/lang/Exception
  }
  
  private void b(BroadcastReceiver paramBroadcastReceiver)
  {
    if (paramBroadcastReceiver == null) {}
    while (this.a == null) {
      return;
    }
    try
    {
      this.a.unregisterReceiver(paramBroadcastReceiver);
      return;
    }
    catch (Exception paramBroadcastReceiver) {}
  }
  
  protected static boolean b(Context paramContext)
  {
    boolean bool2;
    if (paramContext != null)
    {
      if (Settings.Secure.getString(paramContext.getContentResolver(), "mock_location").equals("0"))
      {
        bool2 = false;
        return bool2;
      }
    }
    else {
      return true;
    }
    PackageManager localPackageManager = paramContext.getPackageManager();
    Object localObject = localPackageManager.getInstalledApplications(128);
    paramContext = paramContext.getPackageName();
    localObject = ((List)localObject).iterator();
    boolean bool1 = false;
    for (;;)
    {
      bool2 = bool1;
      if (!((Iterator)localObject).hasNext()) {
        break;
      }
      ApplicationInfo localApplicationInfo = (ApplicationInfo)((Iterator)localObject).next();
      bool2 = bool1;
      if (bool1) {
        break;
      }
      try
      {
        String[] arrayOfString = localPackageManager.getPackageInfo(localApplicationInfo.packageName, 4096).requestedPermissions;
        if (arrayOfString == null) {
          continue;
        }
        int i2 = arrayOfString.length;
        int i1 = 0;
        while (i1 < i2)
        {
          if (arrayOfString[i1].equals("android.permission.ACCESS_MOCK_LOCATION")) {
            break label149;
          }
          i1 += 1;
        }
        label149:
        bool2 = localApplicationInfo.packageName.equals(paramContext);
        if (bool2) {
          continue;
        }
        bool1 = true;
      }
      catch (Exception localException)
      {
        for (;;) {}
      }
    }
  }
  
  private static String[] b(TelephonyManager paramTelephonyManager)
  {
    String[] arrayOfString = null;
    int i1 = 0;
    if (paramTelephonyManager == null)
    {
      paramTelephonyManager = arrayOfString;
      arrayOfString = new String[2];
      arrayOfString[0] = "0";
      arrayOfString[1] = "0";
      if (TextUtils.isDigitsOnly(paramTelephonyManager)) {
        break label44;
      }
    }
    label44:
    while (paramTelephonyManager.length() <= 4)
    {
      return arrayOfString;
      paramTelephonyManager = paramTelephonyManager.getNetworkOperator();
      break;
    }
    arrayOfString[0] = paramTelephonyManager.substring(0, 3);
    char[] arrayOfChar = paramTelephonyManager.substring(3).toCharArray();
    for (;;)
    {
      if (i1 >= arrayOfChar.length) {}
      while (!Character.isDigit(arrayOfChar[i1]))
      {
        arrayOfString[1] = paramTelephonyManager.substring(3, i1 + 3);
        return arrayOfString;
      }
      i1 += 1;
    }
  }
  
  private static boolean c(Context paramContext)
  {
    PackageManager localPackageManager = paramContext.getPackageManager();
    for (;;)
    {
      int i1;
      try
      {
        paramContext = localPackageManager.getPackageInfo(paramContext.getPackageName(), 4096);
        paramContext = paramContext.requestedPermissions;
        i1 = 0;
        if (i1 >= bd.b.length) {
          return true;
        }
      }
      catch (PackageManager.NameNotFoundException paramContext)
      {
        return false;
      }
      if (!bd.a(paramContext, bd.b[i1])) {
        break;
      }
      i1 += 1;
    }
    return false;
  }
  
  protected final String a(int paramInt)
  {
    new ArrayList();
    List localList;
    if (this.e != null)
    {
      localList = this.e.getSensorList(-1);
      if (localList != null) {
        break label36;
      }
    }
    label36:
    while ((localList.get(paramInt) == null) || (((Sensor)localList.get(paramInt)).getName() == null) || (((Sensor)localList.get(paramInt)).getName().length() <= 0))
    {
      return "null";
      return "null";
    }
    return ((Sensor)localList.get(paramInt)).getName();
  }
  
  protected final List a(float paramFloat)
  {
    ArrayList localArrayList = new ArrayList();
    long l1 = System.currentTimeMillis();
    float f1 = paramFloat;
    if (Math.abs(paramFloat) <= 1.0F) {
      f1 = 1.0F;
    }
    if (!c()) {}
    CellLocation localCellLocation;
    do
    {
      return localArrayList;
      localCellLocation = (CellLocation)j().get(1);
    } while ((localCellLocation == null) || (!(localCellLocation instanceof GsmCellLocation)));
    localArrayList.add(Integer.valueOf(((GsmCellLocation)localCellLocation).getLac()));
    localArrayList.add(Integer.valueOf(((GsmCellLocation)localCellLocation).getCid()));
    if (l1 - ((Long)j().get(0)).longValue() <= 50000.0D / f1)
    {
      localArrayList.add(Integer.valueOf(1));
      return localArrayList;
    }
    localArrayList.add(Integer.valueOf(0));
    return localArrayList;
  }
  
  protected final void a()
  {
    b();
    if (this.C == null) {
      if (this.B != null) {
        break label55;
      }
    }
    for (;;)
    {
      this.B = new al(this, "");
      this.B.start();
      return;
      this.C.quit();
      this.C = null;
      break;
      label55:
      this.B.interrupt();
      this.B = null;
    }
  }
  
  protected final double b(int paramInt)
  {
    new ArrayList();
    List localList;
    if (this.e != null)
    {
      localList = this.e.getSensorList(-1);
      if (localList != null) {
        break label32;
      }
    }
    label32:
    while (localList.get(paramInt) == null)
    {
      return 0.0D;
      return 0.0D;
    }
    return ((Sensor)localList.get(paramInt)).getMaximumRange();
  }
  
  protected final List b(float paramFloat)
  {
    ArrayList localArrayList = new ArrayList();
    long l1 = System.currentTimeMillis();
    float f1 = paramFloat;
    if (Math.abs(paramFloat) <= 1.0F) {
      f1 = 1.0F;
    }
    if (!c()) {}
    do
    {
      return localArrayList;
      localObject = (CellLocation)j().get(1);
    } while ((localObject == null) || (!(localObject instanceof CdmaCellLocation)));
    Object localObject = (CdmaCellLocation)localObject;
    localArrayList.add(Integer.valueOf(((CdmaCellLocation)localObject).getSystemId()));
    localArrayList.add(Integer.valueOf(((CdmaCellLocation)localObject).getNetworkId()));
    localArrayList.add(Integer.valueOf(((CdmaCellLocation)localObject).getBaseStationId()));
    localArrayList.add(Integer.valueOf(((CdmaCellLocation)localObject).getBaseStationLongitude()));
    localArrayList.add(Integer.valueOf(((CdmaCellLocation)localObject).getBaseStationLatitude()));
    if (l1 - ((Long)j().get(0)).longValue() <= 50000.0D / f1)
    {
      localArrayList.add(Integer.valueOf(1));
      return localArrayList;
    }
    localArrayList.add(Integer.valueOf(0));
    return localArrayList;
  }
  
  protected final void b()
  {
    if (this.v == null)
    {
      if (this.w != null) {
        break label68;
      }
      if (this.A != null) {
        break label103;
      }
      label21:
      if (this.C != null) {
        break label118;
      }
    }
    for (;;)
    {
      if (this.B != null) {
        break label133;
      }
      return;
      Object localObject = this.v;
      if (this.b == null) {}
      for (;;)
      {
        this.v = null;
        break;
        this.b.listen((PhoneStateListener)localObject, 0);
      }
      label68:
      localObject = this.w;
      if (this.c == null) {}
      for (;;)
      {
        this.w = null;
        break;
        if (localObject != null) {
          this.c.removeNmeaListener((GpsStatus.NmeaListener)localObject);
        }
      }
      label103:
      this.A.cancel();
      this.A = null;
      break label21;
      label118:
      this.C.quit();
      this.C = null;
    }
    label133:
    this.B.interrupt();
    this.B = null;
  }
  
  protected final int c(int paramInt)
  {
    new ArrayList();
    List localList;
    if (this.e != null)
    {
      localList = this.e.getSensorList(-1);
      if (localList != null) {
        break label32;
      }
    }
    label32:
    while (localList.get(paramInt) == null)
    {
      return 0;
      return 0;
    }
    return b(localList.get(paramInt));
  }
  
  protected final boolean c()
  {
    Object localObject = null;
    if (this.b == null) {
      if (this.b != null) {
        break label38;
      }
    }
    label38:
    do
    {
      return false;
      if ((this.b.getSimState() != 5) || (!this.k)) {
        break;
      }
      return true;
      try
      {
        CellLocation localCellLocation = this.b.getCellLocation();
        localObject = localCellLocation;
      }
      catch (Exception localException)
      {
        for (;;) {}
      }
    } while (localObject == null);
    this.t = System.currentTimeMillis();
    this.x = ((CellLocation)localObject);
    return true;
  }
  
  protected final int d(int paramInt)
  {
    new ArrayList();
    List localList;
    if (this.e != null)
    {
      localList = this.e.getSensorList(-1);
      if (localList != null) {
        break label32;
      }
    }
    label32:
    while (localList.get(paramInt) == null)
    {
      return 0;
      return 0;
    }
    return (int)(((Sensor)localList.get(paramInt)).getPower() * 100.0D);
  }
  
  protected final boolean d()
  {
    if (this.d == null) {
      return false;
    }
    if (this.d.isWifiEnabled()) {}
    while (a(this.d)) {
      return true;
    }
    return false;
  }
  
  protected final double e(int paramInt)
  {
    new ArrayList();
    List localList;
    if (this.e != null)
    {
      localList = this.e.getSensorList(-1);
      if (localList != null) {
        break label32;
      }
    }
    label32:
    while (localList.get(paramInt) == null)
    {
      return 0.0D;
      return 0.0D;
    }
    return ((Sensor)localList.get(paramInt)).getResolution();
  }
  
  protected final boolean e()
  {
    try
    {
      if (this.c == null) {
        return false;
      }
      boolean bool = this.c.isProviderEnabled("gps");
      if (bool) {
        return true;
      }
    }
    catch (Exception localException) {}
    return false;
  }
  
  protected final byte f(int paramInt)
  {
    new ArrayList();
    List localList;
    if (this.e != null)
    {
      localList = this.e.getSensorList(-1);
      if (localList != null) {
        break label34;
      }
    }
    label34:
    while ((localList.get(paramInt) == null) || (((Sensor)localList.get(paramInt)).getType() > 127))
    {
      return Byte.MAX_VALUE;
      return Byte.MAX_VALUE;
    }
    return (byte)((Sensor)localList.get(paramInt)).getType();
  }
  
  protected final String f()
  {
    if (this.f != null) {}
    while (this.f == null)
    {
      return "";
      this.f = Build.MODEL;
    }
    return this.f;
  }
  
  protected final String g()
  {
    if (this.g != null) {}
    while (this.g == null)
    {
      return "";
      if (this.a != null)
      {
        this.b = ((TelephonyManager)this.a.getSystemService("phone"));
        if (this.b != null) {
          try
          {
            this.g = this.b.getDeviceId();
          }
          catch (Exception localException) {}
        }
      }
    }
    return this.g;
  }
  
  protected final String g(int paramInt)
  {
    new ArrayList();
    List localList;
    if (this.e != null)
    {
      localList = this.e.getSensorList(-1);
      if (localList != null) {
        break label36;
      }
    }
    label36:
    while ((localList.get(paramInt) == null) || (((Sensor)localList.get(paramInt)).getVendor() == null) || (((Sensor)localList.get(paramInt)).getVendor().length() <= 0))
    {
      return "null";
      return "null";
    }
    return ((Sensor)localList.get(paramInt)).getVendor();
  }
  
  protected final byte h(int paramInt)
  {
    new ArrayList();
    List localList;
    if (this.e != null)
    {
      localList = this.e.getSensorList(-1);
      if (localList != null) {
        break label34;
      }
    }
    label34:
    while ((localList.get(paramInt) == null) || (((Sensor)localList.get(paramInt)).getType() > 127))
    {
      return Byte.MAX_VALUE;
      return Byte.MAX_VALUE;
    }
    return (byte)((Sensor)localList.get(paramInt)).getVersion();
  }
  
  protected final String h()
  {
    if (this.h != null) {}
    while (this.h == null)
    {
      return "";
      if (this.a != null)
      {
        this.b = ((TelephonyManager)this.a.getSystemService("phone"));
        if (this.b != null) {
          this.h = this.b.getSubscriberId();
        }
      }
    }
    return this.h;
  }
  
  protected final boolean i()
  {
    return this.i;
  }
  
  protected final List j()
  {
    if (Settings.System.getInt(this.a.getContentResolver(), "airplane_mode_on", 0) != 1)
    {
      if (!c()) {
        return new ArrayList();
      }
    }
    else {
      return new ArrayList();
    }
    ArrayList localArrayList = new ArrayList();
    CellLocation localCellLocation;
    if (a(this.x)) {
      localCellLocation = this.x;
    }
    for (;;)
    {
      localArrayList.add(Long.valueOf(this.t));
      localArrayList.add(localCellLocation);
      return localArrayList;
      localCellLocation = B();
      if (!a(localCellLocation)) {
        break;
      }
      this.t = System.currentTimeMillis();
    }
  }
  
  protected final List k()
  {
    int i3 = 1;
    int i2 = 0;
    ArrayList localArrayList1 = new ArrayList();
    if (!d()) {
      return new ArrayList();
    }
    ArrayList localArrayList2 = new ArrayList();
    int i1;
    try
    {
      if (System.currentTimeMillis() - this.s < 3500L) {
        break label152;
      }
      i1 = 1;
    }
    finally {}
    return localArrayList2;
    label139:
    label152:
    label160:
    for (;;)
    {
      localArrayList2.add(Long.valueOf(this.s));
      i1 = i2;
      for (;;)
      {
        if (i1 >= this.z.size())
        {
          localArrayList2.add(localArrayList1);
          break;
        }
        ((List)localObject).add(this.z.get(i1));
        i1 += 1;
      }
      if (i1 == 0) {}
      for (i1 = i3;; i1 = 0)
      {
        if (i1 != 0) {
          break label160;
        }
        break;
        i1 = 0;
        break label139;
      }
    }
  }
  
  protected final byte l()
  {
    if (!c()) {
      return Byte.MIN_VALUE;
    }
    return (byte)this.j;
  }
  
  protected final List m()
  {
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator;
    int i1;
    if (this.b != null)
    {
      if (!c()) {
        break label61;
      }
      if (this.b.getSimState() == 1) {
        break label63;
      }
      localIterator = this.b.getNeighboringCellInfo().iterator();
      i1 = 0;
    }
    for (;;)
    {
      if (!localIterator.hasNext()) {}
      label61:
      label63:
      NeighboringCellInfo localNeighboringCellInfo;
      do
      {
        return localArrayList;
        return localArrayList;
        return localArrayList;
        return localArrayList;
        localNeighboringCellInfo = (NeighboringCellInfo)localIterator.next();
      } while (i1 > 15);
      if ((localNeighboringCellInfo.getLac() != 0) && (localNeighboringCellInfo.getLac() != 65535) && (localNeighboringCellInfo.getCid() != 65535) && (localNeighboringCellInfo.getCid() != 268435455))
      {
        localArrayList.add(localNeighboringCellInfo);
        i1 += 1;
      }
    }
  }
  
  protected final List n()
  {
    int i2 = 1;
    ArrayList localArrayList = new ArrayList();
    long l1 = -1L;
    String str = "";
    if (!e())
    {
      if (l1 <= 0L) {
        break label110;
      }
      i1 = 1;
      label34:
      if (i1 == 0) {
        l1 = System.currentTimeMillis() / 1000L;
      }
      if (l1 > 2147483647L) {
        break label115;
      }
    }
    label110:
    label115:
    for (int i1 = i2;; i1 = 0)
    {
      long l2 = l1;
      if (i1 == 0) {
        l2 = l1 / 1000L;
      }
      localArrayList.add(Long.valueOf(l2));
      localArrayList.add(str);
      return localArrayList;
      l1 = this.l;
      str = this.m;
      break;
      i1 = 0;
      break label34;
    }
  }
  
  protected final long o()
  {
    long l1 = this.l;
    if (l1 > 0L) {}
    for (int i1 = 1; i1 == 0; i1 = 0) {
      return 0L;
    }
    i1 = String.valueOf(l1).length();
    if (i1 == 13) {
      return l1;
    }
    if (i1 <= 13) {}
    for (l1 *= 10L;; l1 /= 10L)
    {
      i1 = String.valueOf(l1).length();
      break;
    }
  }
  
  protected final String p()
  {
    if (this.n != null) {}
    while (this.n == null)
    {
      return "";
      if (this.a != null)
      {
        this.d = ((WifiManager)this.a.getSystemService("wifi"));
        if ((this.d != null) && (this.d.getConnectionInfo() != null))
        {
          this.n = this.d.getConnectionInfo().getMacAddress();
          if ((this.n != null) && (this.n.length() > 0)) {
            this.n = this.n.replace(":", "");
          }
        }
      }
    }
    return this.n;
  }
  
  protected final int q()
  {
    return this.o;
  }
  
  protected final int r()
  {
    return this.p;
  }
  
  protected final int s()
  {
    return this.q;
  }
  
  protected final String t()
  {
    if (this.r != null) {}
    while (this.r == null)
    {
      return "";
      if (this.a != null) {
        this.r = this.a.getPackageName();
      }
    }
    return this.r;
  }
  
  protected final List u()
  {
    int i1 = 0;
    ArrayList localArrayList1 = new ArrayList();
    if (!d()) {}
    Object localObject;
    label87:
    do
    {
      List localList;
      do
      {
        return localArrayList1;
        localObject = k();
        localList = (List)((List)localObject).get(1);
        long l1 = ((Long)((List)localObject).get(0)).longValue();
        a(localList);
        localArrayList1.add(Long.valueOf(l1));
      } while ((localList == null) || (localList.size() <= 0));
      if (i1 >= localList.size()) {
        break;
      }
      localObject = (ScanResult)localList.get(i1);
    } while (localArrayList1.size() - 1 >= 40);
    if (localObject == null) {}
    for (;;)
    {
      i1 += 1;
      break label87;
      break;
      ArrayList localArrayList2 = new ArrayList();
      localArrayList2.add(((ScanResult)localObject).BSSID.replace(":", ""));
      localArrayList2.add(Integer.valueOf(((ScanResult)localObject).level));
      localArrayList2.add(((ScanResult)localObject).SSID);
      localArrayList1.add(localArrayList2);
    }
  }
  
  protected final void v()
  {
    for (;;)
    {
      try
      {
        this.z.clear();
        if (this.y == null)
        {
          if (this.A != null) {
            break label85;
          }
          this.A = new Timer();
          this.y = new ao(this, (byte)0);
          a(this.y);
          A();
          return;
        }
      }
      finally {}
      b(this.y);
      this.y = null;
      continue;
      label85:
      this.A.cancel();
      this.A = null;
    }
  }
  
  protected final void w()
  {
    for (;;)
    {
      try
      {
        this.z.clear();
        if (this.y == null)
        {
          if (this.A != null) {
            break;
          }
          return;
        }
      }
      finally {}
      b(this.y);
      this.y = null;
    }
    this.A.cancel();
    this.A = null;
  }
  
  protected final byte x()
  {
    new ArrayList();
    if (this.e != null)
    {
      List localList = this.e.getSensorList(-1);
      if (localList != null) {
        return (byte)localList.size();
      }
    }
    else
    {
      return 0;
    }
    return 0;
  }
  
  protected final Context y()
  {
    return this.a;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/aps/ak.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */