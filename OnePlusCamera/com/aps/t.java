package com.aps;

import android.content.Context;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build.VERSION;
import android.provider.Settings.Global;
import android.provider.Settings.System;
import android.telephony.CellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPOutputStream;
import org.apache.http.params.HttpParams;

public class t
{
  static float a(double[] paramArrayOfDouble)
  {
    if (paramArrayOfDouble.length == 4)
    {
      float[] arrayOfFloat = new float[1];
      Location.distanceBetween(paramArrayOfDouble[0], paramArrayOfDouble[1], paramArrayOfDouble[2], paramArrayOfDouble[3], arrayOfFloat);
      return arrayOfFloat[0];
    }
    return 0.0F;
  }
  
  static int a(int paramInt)
  {
    return paramInt * 2 - 113;
  }
  
  static int a(CellLocation paramCellLocation, Context paramContext)
  {
    int i = 1;
    if ((a(paramContext)) || (!(paramCellLocation instanceof GsmCellLocation))) {}
    try
    {
      Class.forName("android.telephony.cdma.CdmaCellLocation");
      i = 2;
      return i;
    }
    catch (Throwable paramCellLocation)
    {
      paramCellLocation.printStackTrace();
      a(paramCellLocation);
    }
    a(new Object[] { "air plane mode on" });
    return 9;
    return 9;
  }
  
  static long a()
  {
    return System.currentTimeMillis();
  }
  
  static void a(Context paramContext, String paramString)
  {
    char[] arrayOfChar = null;
    label39:
    label43:
    int i;
    if (paramString != null)
    {
      if (com.amap.api.location.core.c.j().indexOf("test") != -1) {
        break label56;
      }
      if (f.d.indexOf("test") != -1) {
        break label61;
      }
      if (com.amap.api.location.core.c.j().length() > 0) {
        break label66;
      }
      if (arrayOfChar != null) {
        break label83;
      }
      i = 1;
      label45:
      if (i != 0) {
        break label97;
      }
    }
    label56:
    label61:
    label66:
    label83:
    label97:
    while (paramContext == null)
    {
      return;
      paramString = "null";
      break;
      i = 1;
      break label45;
      i = 1;
      break label45;
      arrayOfChar = com.amap.api.location.core.c.j().substring(7, 8).toCharArray();
      break label39;
      if (!Character.isLetter(arrayOfChar[0])) {
        break label43;
      }
      i = 0;
      break label45;
    }
    Toast.makeText(paramContext, paramString, 0).show();
    a(new Object[] { paramString });
  }
  
  public static void a(Throwable paramThrowable) {}
  
  static void a(HttpParams paramHttpParams, int paramInt)
  {
    paramHttpParams.setIntParameter("http.connection.timeout", paramInt);
    paramHttpParams.setIntParameter("http.socket.timeout", paramInt);
    paramHttpParams.setLongParameter("http.conn-manager.timeout", paramInt);
  }
  
  public static void a(Object... paramVarArgs) {}
  
  static boolean a(Context paramContext)
  {
    if (paramContext != null)
    {
      paramContext = paramContext.getContentResolver();
      if (b() < 17) {
        break label34;
      }
    }
    try
    {
      int i = Settings.Global.getInt(paramContext, "airplane_mode_on", 0);
      if (i != 1)
      {
        return false;
        return false;
        try
        {
          label34:
          i = Settings.System.getInt(paramContext, "airplane_mode_on", 0);
          return i == 1;
        }
        catch (Throwable paramContext)
        {
          paramContext.printStackTrace();
          a(paramContext);
          return false;
        }
      }
      return true;
    }
    catch (Throwable paramContext)
    {
      paramContext.printStackTrace();
      a(paramContext);
    }
    return false;
  }
  
  static boolean a(c paramc)
  {
    double d1;
    double d2;
    float f;
    if ((paramc != null) && (!paramc.j().equals("5")) && (!paramc.j().equals("6")))
    {
      d1 = paramc.e();
      d2 = paramc.f();
      f = paramc.g();
    }
    return (d1 != 0.0D) || (d2 != 0.0D) || (f != 0.0D);
  }
  
  public static boolean a(String paramString)
  {
    if (!TextUtils.isEmpty(paramString))
    {
      if (TextUtils.isDigitsOnly(paramString)) {
        return ",111,123,134,199,202,204,206,208,212,213,214,216,218,219,220,222,225,226,228,230,231,232,234,235,238,240,242,244,246,247,248,250,255,257,259,260,262,266,268,270,272,274,276,278,280,282,283,284,286,288,289,290,292,293,294,295,297,302,308,310,311,312,313,314,315,316,310,330,332,334,338,340,342,344,346,348,350,352,354,356,358,360,362,363,364,365,366,368,370,372,374,376,400,401,402,404,405,406,410,412,413,414,415,416,417,418,419,420,421,422,424,425,426,427,428,429,430,431,432,434,436,437,438,440,441,450,452,454,455,456,457,466,467,470,472,502,505,510,514,515,520,525,528,530,534,535,536,537,539,540,541,542,543,544,545,546,547,548,549,550,551,552,553,555,560,598,602,603,604,605,606,607,608,609,610,611,612,613,614,615,616,617,618,619,620,621,622,623,624,625,626,627,628,629,630,631,632,633,634,635,636,637,638,639,640,641,642,643,645,646,647,648,649,650,651,652,653,654,655,657,659,665,702,704,706,708,710,712,714,716,722,724,730,732,734,736,738,740,742,744,746,748,750,850,901,".contains("," + paramString + ",");
      }
    }
    else {
      return false;
    }
    return false;
  }
  
  public static byte[] a(byte[] paramArrayOfByte)
  {
    Object localObject2 = null;
    Object localObject1 = localObject2;
    try
    {
      ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
      localObject1 = localObject2;
      GZIPOutputStream localGZIPOutputStream = new GZIPOutputStream(localByteArrayOutputStream);
      localObject1 = localObject2;
      localGZIPOutputStream.write(paramArrayOfByte);
      localObject1 = localObject2;
      localGZIPOutputStream.close();
      localObject1 = localObject2;
      paramArrayOfByte = localByteArrayOutputStream.toByteArray();
      localObject1 = paramArrayOfByte;
      localByteArrayOutputStream.close();
      return paramArrayOfByte;
    }
    catch (Throwable paramArrayOfByte)
    {
      paramArrayOfByte.printStackTrace();
    }
    return (byte[])localObject1;
  }
  
  /* Error */
  public static String[] a(android.telephony.TelephonyManager paramTelephonyManager)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_3
    //   2: iconst_1
    //   3: istore_1
    //   4: iconst_2
    //   5: anewarray 62	java/lang/String
    //   8: astore 4
    //   10: aload 4
    //   12: iconst_0
    //   13: ldc -42
    //   15: aastore
    //   16: aload 4
    //   18: iconst_1
    //   19: ldc -42
    //   21: aastore
    //   22: aload_0
    //   23: ifnonnull +48 -> 71
    //   26: aload_3
    //   27: astore_0
    //   28: aload_0
    //   29: invokestatic 168	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   32: ifne +114 -> 146
    //   35: aload_0
    //   36: invokestatic 171	android/text/TextUtils:isDigitsOnly	(Ljava/lang/CharSequence;)Z
    //   39: ifeq +112 -> 151
    //   42: aload_0
    //   43: invokevirtual 76	java/lang/String:length	()I
    //   46: istore_2
    //   47: iload_2
    //   48: iconst_4
    //   49: if_icmple +107 -> 156
    //   52: iload_1
    //   53: ifne +26 -> 79
    //   56: aload 4
    //   58: iconst_0
    //   59: aaload
    //   60: invokestatic 219	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   63: istore_1
    //   64: iload_1
    //   65: ifeq +72 -> 137
    //   68: aload 4
    //   70: areturn
    //   71: aload_0
    //   72: invokevirtual 224	android/telephony/TelephonyManager:getNetworkOperator	()Ljava/lang/String;
    //   75: astore_0
    //   76: goto -48 -> 28
    //   79: aload 4
    //   81: iconst_0
    //   82: aload_0
    //   83: iconst_0
    //   84: iconst_3
    //   85: invokevirtual 82	java/lang/String:substring	(II)Ljava/lang/String;
    //   88: aastore
    //   89: aload_0
    //   90: iconst_3
    //   91: invokevirtual 227	java/lang/String:substring	(I)Ljava/lang/String;
    //   94: invokevirtual 86	java/lang/String:toCharArray	()[C
    //   97: astore_3
    //   98: iconst_0
    //   99: istore_1
    //   100: iload_1
    //   101: aload_3
    //   102: arraylength
    //   103: if_icmplt +18 -> 121
    //   106: aload 4
    //   108: iconst_1
    //   109: aload_0
    //   110: iconst_3
    //   111: iload_1
    //   112: iconst_3
    //   113: iadd
    //   114: invokevirtual 82	java/lang/String:substring	(II)Ljava/lang/String;
    //   117: aastore
    //   118: goto -62 -> 56
    //   121: aload_3
    //   122: iload_1
    //   123: caload
    //   124: invokestatic 230	java/lang/Character:isDigit	(C)Z
    //   127: ifeq -21 -> 106
    //   130: iload_1
    //   131: iconst_1
    //   132: iadd
    //   133: istore_1
    //   134: goto -34 -> 100
    //   137: aload 4
    //   139: iconst_0
    //   140: ldc -42
    //   142: aastore
    //   143: aload 4
    //   145: areturn
    //   146: iconst_0
    //   147: istore_1
    //   148: goto -96 -> 52
    //   151: iconst_0
    //   152: istore_1
    //   153: goto -101 -> 52
    //   156: iconst_0
    //   157: istore_1
    //   158: goto -106 -> 52
    //   161: astore_0
    //   162: aload 4
    //   164: areturn
    //   165: astore_0
    //   166: iconst_0
    //   167: istore_1
    //   168: goto -104 -> 64
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	171	0	paramTelephonyManager	android.telephony.TelephonyManager
    //   3	165	1	i	int
    //   46	4	2	j	int
    //   1	121	3	arrayOfChar	char[]
    //   8	155	4	arrayOfString	String[]
    // Exception table:
    //   from	to	target	type
    //   28	47	161	java/lang/Exception
    //   71	76	161	java/lang/Exception
    //   79	98	161	java/lang/Exception
    //   100	106	161	java/lang/Exception
    //   106	118	161	java/lang/Exception
    //   121	130	161	java/lang/Exception
    //   56	64	165	java/lang/Exception
  }
  
  static int b()
  {
    try
    {
      i = Build.VERSION.SDK_INT;
      return i;
    }
    catch (Throwable localThrowable1)
    {
      int i;
      localThrowable1.printStackTrace();
      try
      {
        i = Integer.parseInt(Build.VERSION.SDK.toString());
        return i;
      }
      catch (Throwable localThrowable2)
      {
        localThrowable2.printStackTrace();
        a(localThrowable2);
      }
    }
    return 0;
  }
  
  static NetworkInfo b(Context paramContext)
  {
    paramContext = (ConnectivityManager)b(paramContext, "connectivity");
    if (paramContext == null) {
      paramContext = null;
    }
    for (;;)
    {
      return paramContext;
      try
      {
        paramContext = paramContext.getActiveNetworkInfo();
      }
      catch (SecurityException paramContext) {}
    }
    return null;
  }
  
  static Object b(Context paramContext, String paramString)
  {
    if (paramContext != null) {
      return paramContext.getApplicationContext().getSystemService(paramString);
    }
    return null;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/aps/t.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */