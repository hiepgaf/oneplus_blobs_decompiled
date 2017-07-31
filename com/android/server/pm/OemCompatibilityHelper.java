package com.android.server.pm;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageParser.Activity;
import android.content.pm.PackageParser.Package;
import android.os.Build;
import android.util.Slog;
import android.util.SparseArray;
import com.oneplus.config.ConfigObserver;
import com.oneplus.config.ConfigObserver.ConfigUpdater;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

public class OemCompatibilityHelper
{
  private static final Map<String, Integer> ABI_TO_INT_MAP;
  private static final Map<Integer, String> ABI_TO_STRING_MAP;
  private static final String COMPAT_CONFIG_LIST_NAME = "CompatConfigList";
  private static final String DATA_FILE_DIR = "/data/system/oneplus_cpt_list.xml";
  private static final boolean DEBUG = Build.IS_DEBUGGABLE;
  private static final String FILTER_NAME = "compatibility_config_values";
  public static final int FORCE_CHECK_OP_SDK = 70;
  public static final int FORCE_CHOOSE_ANDROID_WEBVIEW = 1;
  public static final int FORCE_CHOOSING_TARGETSDK = 71;
  public static final int FORCE_DEX2OAT_ROLLBACK = 95;
  public static final int FORCE_DISABLE_HARDWAREACCELERATE_FOR_ACTIVITIES = 78;
  public static final int FORCE_DISABLE_HARDWAREACCELERATE_QCOM = 85;
  public static final int FORCE_ENABLE_DEBUGGER = 107;
  public static final int FORCE_ENABLE_HARDWAREACCELERATE = 111;
  public static final int FORCE_ENABLE_HARDWAREACCELERATE_FOR_ACTIVITIES = 104;
  public static final int FORCE_IN_SAFEMODE_DEX = 211;
  public static final int FORCE_MINI_TRIMMEMORY = 331;
  public static final int FORCE_RUNNING_IN_32_BIT_V5 = 445;
  public static final int FORCE_RUNNING_IN_32_BIT_V7 = 443;
  public static final int FORCE_RUNNING_IN_64_BIT = 444;
  public static final int NOT_ALLOEED_INSTALL_PACKAGE = 0;
  private static final String SYS_FILE_DIR = "/system/etc/oneplus_cpt_list.xml";
  private static final String TAG = "OemCompatibilityHelper";
  private static Context mContext;
  private static int mTotalCount;
  private static Object sConfigLock = new Object();
  private SparseArray<ArrayList<String>> mCompatConfigList = new SparseArray();
  private ConfigObserver mCompatConfigObserver;
  
  static
  {
    mTotalCount = 0;
    ABI_TO_INT_MAP = new HashMap();
    ABI_TO_INT_MAP.put("armeabi", Integer.valueOf(2));
    ABI_TO_INT_MAP.put("armeabi-v7a", Integer.valueOf(1));
    ABI_TO_INT_MAP.put("arm64-v8a", Integer.valueOf(0));
    ABI_TO_STRING_MAP = new HashMap();
    ABI_TO_STRING_MAP.put(Integer.valueOf(2), "armeabi");
    ABI_TO_STRING_MAP.put(Integer.valueOf(1), "armeabi-v7a");
    ABI_TO_STRING_MAP.put(Integer.valueOf(0), "arm64-v8a");
  }
  
  public OemCompatibilityHelper(Context paramContext)
  {
    mContext = paramContext;
    parseContentFromXML();
    if (DEBUG) {
      Slog.i("OemCompatibilityHelper", dumpToString());
    }
  }
  
  private void changeActivitiesHW(ArrayList<PackageParser.Activity> paramArrayList, boolean paramBoolean)
  {
    int i = paramArrayList.size() - 1;
    if (i >= 0)
    {
      ActivityInfo localActivityInfo;
      if (paramBoolean) {
        localActivityInfo = ((PackageParser.Activity)paramArrayList.get(i)).info;
      }
      for (localActivityInfo.flags |= 0x200;; localActivityInfo.flags &= 0xFDFF)
      {
        i -= 1;
        break;
        localActivityInfo = ((PackageParser.Activity)paramArrayList.get(i)).info;
      }
    }
  }
  
  private void changeActivityHW(ArrayList<PackageParser.Activity> paramArrayList, String paramString, boolean paramBoolean)
  {
    int i = paramArrayList.size() - 1;
    if (i >= 0)
    {
      Object localObject = paramString + "/" + ((PackageParser.Activity)paramArrayList.get(i)).className;
      int j;
      if (paramBoolean)
      {
        j = 104;
        label57:
        if (isInConfigList(j, (String)localObject))
        {
          if (!paramBoolean) {
            break label116;
          }
          localObject = ((PackageParser.Activity)paramArrayList.get(i)).info;
        }
      }
      for (((ActivityInfo)localObject).flags |= 0x200;; ((ActivityInfo)localObject).flags &= 0xFDFF)
      {
        i -= 1;
        break;
        j = 78;
        break label57;
        label116:
        localObject = ((PackageParser.Activity)paramArrayList.get(i)).info;
      }
    }
  }
  
  private int char2int(char[] paramArrayOfChar)
  {
    return (paramArrayOfChar[0] - 'a') * 26 + 0 + (paramArrayOfChar[1] - 'a');
  }
  
  private void customizeHardwareAccelerateForActivityIfNeeded(PackageParser.Package paramPackage)
  {
    if (isInConfigList(104, paramPackage.packageName)) {
      changeActivityHW(paramPackage.activities, paramPackage.packageName, true);
    }
    while (!isInConfigList(78, paramPackage.packageName)) {
      return;
    }
    changeActivityHW(paramPackage.activities, paramPackage.packageName, false);
  }
  
  private void customizeHardwareAccelerateIfNeeded(PackageParser.Package paramPackage)
  {
    if (isInConfigList(111, paramPackage.packageName)) {}
    for (paramPackage.baseHardwareAccelerated = true;; paramPackage.baseHardwareAccelerated = false)
    {
      changeActivitiesHW(paramPackage.activities, paramPackage.baseHardwareAccelerated);
      return;
      if (!isInConfigList(85, paramPackage.packageName)) {
        break;
      }
    }
  }
  
  private void customizePrivateFlagsIfNeeded(PackageParser.Package paramPackage)
  {
    ApplicationInfo localApplicationInfo;
    if (isInConfigList(331, paramPackage.packageName))
    {
      localApplicationInfo = paramPackage.applicationInfo;
      localApplicationInfo.privateFlags |= 0x2000;
    }
    if (isInConfigList(107, paramPackage.packageName))
    {
      localApplicationInfo = paramPackage.applicationInfo;
      localApplicationInfo.privateFlags |= 0x4000;
    }
    if (isInConfigList(95, paramPackage.packageName))
    {
      paramPackage = paramPackage.applicationInfo;
      paramPackage.privateFlags |= 0x8000;
    }
  }
  
  private void customizeTargetSdkIfNeeded(PackageParser.Package paramPackage)
  {
    if (isInConfigList(71, paramPackage.packageName)) {
      paramPackage.applicationInfo.targetSdkVersion = 22;
    }
  }
  
  private void customizeVMSafeModeIfNeeded(PackageParser.Package paramPackage)
  {
    if (isInConfigList(211, paramPackage.packageName))
    {
      paramPackage = paramPackage.applicationInfo;
      paramPackage.flags |= 0x4000;
    }
  }
  
  private String int2string(int paramInt)
  {
    return String.valueOf(new char[] { (char)(paramInt / 26 + 97), (char)(paramInt % 26 + 97) });
  }
  
  private boolean isBaiduProtectedApk(long paramLong, String paramString)
  {
    int i = 0;
    if (paramString != null) {
      i = ((Integer)ABI_TO_INT_MAP.get(paramString)).intValue();
    }
    switch (i)
    {
    }
    do
    {
      do
      {
        do
        {
          return false;
        } while (paramLong != 610128L);
        return true;
      } while (paramLong != 367076L);
      return true;
      if ((paramLong == 408028L) || (paramLong == 412124L)) {
        return true;
      }
    } while (paramLong != 416220L);
    return true;
  }
  
  /* Error */
  private void parseContentFromXML()
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore_1
    //   2: new 256	java/io/File
    //   5: dup
    //   6: ldc 19
    //   8: invokespecial 259	java/io/File:<init>	(Ljava/lang/String;)V
    //   11: astore 4
    //   13: aload 4
    //   15: astore_3
    //   16: aload 4
    //   18: invokevirtual 263	java/io/File:exists	()Z
    //   21: ifne +23 -> 44
    //   24: new 256	java/io/File
    //   27: dup
    //   28: ldc 58
    //   30: invokespecial 259	java/io/File:<init>	(Ljava/lang/String;)V
    //   33: astore_3
    //   34: aload_3
    //   35: invokevirtual 263	java/io/File:exists	()Z
    //   38: ifne +4 -> 42
    //   41: return
    //   42: iconst_1
    //   43: istore_1
    //   44: aload_0
    //   45: aload_3
    //   46: invokespecial 267	com/android/server/pm/OemCompatibilityHelper:readFromFile	(Ljava/io/File;)Ljava/lang/String;
    //   49: astore 8
    //   51: aload 8
    //   53: ifnonnull +4 -> 57
    //   56: return
    //   57: getstatic 92	com/android/server/pm/OemCompatibilityHelper:sConfigLock	Ljava/lang/Object;
    //   60: astore 7
    //   62: aload 7
    //   64: monitorenter
    //   65: aconst_null
    //   66: astore 4
    //   68: aconst_null
    //   69: astore 6
    //   71: aload_0
    //   72: getfield 125	com/android/server/pm/OemCompatibilityHelper:mCompatConfigList	Landroid/util/SparseArray;
    //   75: invokevirtual 270	android/util/SparseArray:clear	()V
    //   78: aload 4
    //   80: astore_3
    //   81: invokestatic 276	android/util/Xml:newPullParser	()Lorg/xmlpull/v1/XmlPullParser;
    //   84: astore 5
    //   86: aload 4
    //   88: astore_3
    //   89: new 278	java/io/StringReader
    //   92: dup
    //   93: aload 8
    //   95: invokespecial 279	java/io/StringReader:<init>	(Ljava/lang/String;)V
    //   98: astore 4
    //   100: aload 5
    //   102: aload 4
    //   104: invokeinterface 285 2 0
    //   109: aload 5
    //   111: invokeinterface 288 1 0
    //   116: istore_2
    //   117: goto +356 -> 473
    //   120: aload 5
    //   122: invokeinterface 291 1 0
    //   127: istore_2
    //   128: goto +345 -> 473
    //   131: aload 5
    //   133: invokeinterface 294 1 0
    //   138: invokevirtual 298	java/lang/String:toCharArray	()[C
    //   141: astore_3
    //   142: aload 5
    //   144: invokeinterface 291 1 0
    //   149: pop
    //   150: aload_3
    //   151: arraylength
    //   152: iconst_2
    //   153: if_icmpne -33 -> 120
    //   156: aload_0
    //   157: aload_3
    //   158: invokespecial 300	com/android/server/pm/OemCompatibilityHelper:char2int	([C)I
    //   161: istore_2
    //   162: iload_2
    //   163: iflt -43 -> 120
    //   166: aload_0
    //   167: getfield 125	com/android/server/pm/OemCompatibilityHelper:mCompatConfigList	Landroid/util/SparseArray;
    //   170: iload_2
    //   171: invokevirtual 301	android/util/SparseArray:get	(I)Ljava/lang/Object;
    //   174: checkcast 144	java/util/ArrayList
    //   177: astore_3
    //   178: aload_3
    //   179: ifnonnull +92 -> 271
    //   182: new 144	java/util/ArrayList
    //   185: dup
    //   186: invokespecial 302	java/util/ArrayList:<init>	()V
    //   189: astore_3
    //   190: aload_3
    //   191: aload 5
    //   193: invokeinterface 305 1 0
    //   198: invokevirtual 309	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   201: pop
    //   202: aload_0
    //   203: getfield 125	com/android/server/pm/OemCompatibilityHelper:mCompatConfigList	Landroid/util/SparseArray;
    //   206: iload_2
    //   207: aload_3
    //   208: invokevirtual 312	android/util/SparseArray:put	(ILjava/lang/Object;)V
    //   211: getstatic 94	com/android/server/pm/OemCompatibilityHelper:mTotalCount	I
    //   214: iconst_1
    //   215: iadd
    //   216: putstatic 94	com/android/server/pm/OemCompatibilityHelper:mTotalCount	I
    //   219: goto -99 -> 120
    //   222: astore 5
    //   224: aload 4
    //   226: astore_3
    //   227: ldc 61
    //   229: new 169	java/lang/StringBuilder
    //   232: dup
    //   233: invokespecial 170	java/lang/StringBuilder:<init>	()V
    //   236: ldc_w 314
    //   239: invokevirtual 174	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   242: aload 5
    //   244: invokevirtual 317	java/lang/Exception:getMessage	()Ljava/lang/String;
    //   247: invokevirtual 174	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   250: invokevirtual 182	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   253: invokestatic 320	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   256: pop
    //   257: aload 4
    //   259: ifnull +8 -> 267
    //   262: aload 4
    //   264: invokevirtual 323	java/io/StringReader:close	()V
    //   267: aload 7
    //   269: monitorexit
    //   270: return
    //   271: aload_3
    //   272: aload 5
    //   274: invokeinterface 305 1 0
    //   279: invokevirtual 309	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   282: pop
    //   283: goto -72 -> 211
    //   286: astore_3
    //   287: aload 4
    //   289: ifnull +8 -> 297
    //   292: aload 4
    //   294: invokevirtual 323	java/io/StringReader:close	()V
    //   297: aload_3
    //   298: athrow
    //   299: astore_3
    //   300: aload 7
    //   302: monitorexit
    //   303: aload_3
    //   304: athrow
    //   305: aload 4
    //   307: ifnull +8 -> 315
    //   310: aload 4
    //   312: invokevirtual 323	java/io/StringReader:close	()V
    //   315: aload 7
    //   317: monitorexit
    //   318: iload_1
    //   319: ifeq +21 -> 340
    //   322: getstatic 92	com/android/server/pm/OemCompatibilityHelper:sConfigLock	Ljava/lang/Object;
    //   325: astore_3
    //   326: aload_3
    //   327: monitorenter
    //   328: aload_0
    //   329: ldc 19
    //   331: aload_0
    //   332: getfield 125	com/android/server/pm/OemCompatibilityHelper:mCompatConfigList	Landroid/util/SparseArray;
    //   335: invokespecial 327	com/android/server/pm/OemCompatibilityHelper:writeCompatConfigListXml	(Ljava/lang/String;Landroid/util/SparseArray;)V
    //   338: aload_3
    //   339: monitorexit
    //   340: return
    //   341: astore_3
    //   342: ldc 61
    //   344: new 169	java/lang/StringBuilder
    //   347: dup
    //   348: invokespecial 170	java/lang/StringBuilder:<init>	()V
    //   351: ldc_w 329
    //   354: invokevirtual 174	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   357: aload_3
    //   358: invokevirtual 330	java/io/IOException:getMessage	()Ljava/lang/String;
    //   361: invokevirtual 174	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   364: invokevirtual 182	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   367: invokestatic 333	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   370: pop
    //   371: goto -56 -> 315
    //   374: astore_3
    //   375: goto -75 -> 300
    //   378: astore_3
    //   379: ldc 61
    //   381: new 169	java/lang/StringBuilder
    //   384: dup
    //   385: invokespecial 170	java/lang/StringBuilder:<init>	()V
    //   388: ldc_w 329
    //   391: invokevirtual 174	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   394: aload_3
    //   395: invokevirtual 330	java/io/IOException:getMessage	()Ljava/lang/String;
    //   398: invokevirtual 174	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   401: invokevirtual 182	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   404: invokestatic 333	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   407: pop
    //   408: goto -141 -> 267
    //   411: astore 4
    //   413: ldc 61
    //   415: new 169	java/lang/StringBuilder
    //   418: dup
    //   419: invokespecial 170	java/lang/StringBuilder:<init>	()V
    //   422: ldc_w 329
    //   425: invokevirtual 174	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   428: aload 4
    //   430: invokevirtual 330	java/io/IOException:getMessage	()Ljava/lang/String;
    //   433: invokevirtual 174	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   436: invokevirtual 182	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   439: invokestatic 333	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   442: pop
    //   443: goto -146 -> 297
    //   446: astore 4
    //   448: aload_3
    //   449: monitorexit
    //   450: aload 4
    //   452: athrow
    //   453: astore 5
    //   455: aload_3
    //   456: astore 4
    //   458: aload 5
    //   460: astore_3
    //   461: goto -174 -> 287
    //   464: astore 5
    //   466: aload 6
    //   468: astore 4
    //   470: goto -246 -> 224
    //   473: iload_2
    //   474: iconst_1
    //   475: if_icmpeq -170 -> 305
    //   478: iload_2
    //   479: tableswitch	default:+29->508, 0:+-359->120, 1:+-359->120, 2:+-348->131, 3:+-359->120
    //   508: goto -388 -> 120
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	511	0	this	OemCompatibilityHelper
    //   1	318	1	i	int
    //   116	363	2	j	int
    //   15	257	3	localObject1	Object
    //   286	12	3	localObject2	Object
    //   299	5	3	localObject3	Object
    //   341	17	3	localIOException1	java.io.IOException
    //   374	1	3	localObject5	Object
    //   378	78	3	localIOException2	java.io.IOException
    //   460	1	3	localObject6	Object
    //   11	300	4	localObject7	Object
    //   411	18	4	localIOException3	java.io.IOException
    //   446	5	4	localObject8	Object
    //   456	13	4	localObject9	Object
    //   84	108	5	localXmlPullParser	org.xmlpull.v1.XmlPullParser
    //   222	51	5	localException1	Exception
    //   453	6	5	localObject10	Object
    //   464	1	5	localException2	Exception
    //   69	398	6	localObject11	Object
    //   60	256	7	localObject12	Object
    //   49	45	8	str	String
    // Exception table:
    //   from	to	target	type
    //   100	117	222	java/lang/Exception
    //   120	128	222	java/lang/Exception
    //   131	162	222	java/lang/Exception
    //   166	178	222	java/lang/Exception
    //   182	211	222	java/lang/Exception
    //   211	219	222	java/lang/Exception
    //   271	283	222	java/lang/Exception
    //   100	117	286	finally
    //   120	128	286	finally
    //   131	162	286	finally
    //   166	178	286	finally
    //   182	211	286	finally
    //   211	219	286	finally
    //   271	283	286	finally
    //   71	78	299	finally
    //   262	267	299	finally
    //   292	297	299	finally
    //   297	299	299	finally
    //   379	408	299	finally
    //   413	443	299	finally
    //   310	315	341	java/io/IOException
    //   310	315	374	finally
    //   342	371	374	finally
    //   262	267	378	java/io/IOException
    //   292	297	411	java/io/IOException
    //   328	338	446	finally
    //   81	86	453	finally
    //   89	100	453	finally
    //   227	257	453	finally
    //   81	86	464	java/lang/Exception
    //   89	100	464	java/lang/Exception
  }
  
  /* Error */
  private String readFromFile(File paramFile)
  {
    // Byte code:
    //   0: aload_1
    //   1: ifnonnull +7 -> 8
    //   4: ldc_w 337
    //   7: areturn
    //   8: aconst_null
    //   9: astore_3
    //   10: aconst_null
    //   11: astore_2
    //   12: aconst_null
    //   13: astore 4
    //   15: new 339	java/io/FileInputStream
    //   18: dup
    //   19: aload_1
    //   20: invokespecial 342	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   23: astore_1
    //   24: new 344	java/io/BufferedReader
    //   27: dup
    //   28: new 346	java/io/InputStreamReader
    //   31: dup
    //   32: aload_1
    //   33: invokespecial 349	java/io/InputStreamReader:<init>	(Ljava/io/InputStream;)V
    //   36: invokespecial 351	java/io/BufferedReader:<init>	(Ljava/io/Reader;)V
    //   39: astore_2
    //   40: new 353	java/lang/StringBuffer
    //   43: dup
    //   44: invokespecial 354	java/lang/StringBuffer:<init>	()V
    //   47: astore_3
    //   48: aload_2
    //   49: invokevirtual 357	java/io/BufferedReader:readLine	()Ljava/lang/String;
    //   52: astore 4
    //   54: aload 4
    //   56: ifnull +30 -> 86
    //   59: aload_3
    //   60: aload 4
    //   62: invokevirtual 360	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
    //   65: pop
    //   66: goto -18 -> 48
    //   69: astore_3
    //   70: aload_1
    //   71: astore_2
    //   72: aload_3
    //   73: invokevirtual 363	java/io/FileNotFoundException:printStackTrace	()V
    //   76: aload_1
    //   77: ifnull +7 -> 84
    //   80: aload_1
    //   81: invokevirtual 366	java/io/InputStream:close	()V
    //   84: aconst_null
    //   85: areturn
    //   86: aload_3
    //   87: invokevirtual 367	java/lang/StringBuffer:toString	()Ljava/lang/String;
    //   90: astore_2
    //   91: aload_1
    //   92: ifnull +7 -> 99
    //   95: aload_1
    //   96: invokevirtual 366	java/io/InputStream:close	()V
    //   99: aload_2
    //   100: areturn
    //   101: astore_1
    //   102: aload_1
    //   103: invokevirtual 368	java/io/IOException:printStackTrace	()V
    //   106: aload_2
    //   107: areturn
    //   108: astore_2
    //   109: aload_3
    //   110: astore_1
    //   111: aload_2
    //   112: astore_3
    //   113: aload_1
    //   114: astore_2
    //   115: aload_3
    //   116: invokevirtual 368	java/io/IOException:printStackTrace	()V
    //   119: aload_1
    //   120: ifnull -36 -> 84
    //   123: aload_1
    //   124: invokevirtual 366	java/io/InputStream:close	()V
    //   127: aconst_null
    //   128: areturn
    //   129: astore_1
    //   130: aload_1
    //   131: invokevirtual 368	java/io/IOException:printStackTrace	()V
    //   134: aconst_null
    //   135: areturn
    //   136: astore_1
    //   137: aload_1
    //   138: invokevirtual 368	java/io/IOException:printStackTrace	()V
    //   141: aconst_null
    //   142: areturn
    //   143: astore_1
    //   144: aload_2
    //   145: ifnull +7 -> 152
    //   148: aload_2
    //   149: invokevirtual 366	java/io/InputStream:close	()V
    //   152: aload_1
    //   153: athrow
    //   154: astore_2
    //   155: aload_2
    //   156: invokevirtual 368	java/io/IOException:printStackTrace	()V
    //   159: goto -7 -> 152
    //   162: astore_3
    //   163: aload_1
    //   164: astore_2
    //   165: aload_3
    //   166: astore_1
    //   167: goto -23 -> 144
    //   170: astore_3
    //   171: aload 4
    //   173: astore_1
    //   174: goto -104 -> 70
    //   177: astore_3
    //   178: goto -65 -> 113
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	181	0	this	OemCompatibilityHelper
    //   0	181	1	paramFile	File
    //   11	96	2	localObject1	Object
    //   108	4	2	localIOException1	java.io.IOException
    //   114	35	2	localFile1	File
    //   154	2	2	localIOException2	java.io.IOException
    //   164	1	2	localFile2	File
    //   9	51	3	localStringBuffer	StringBuffer
    //   69	41	3	localFileNotFoundException1	java.io.FileNotFoundException
    //   112	4	3	localObject2	Object
    //   162	4	3	localObject3	Object
    //   170	1	3	localFileNotFoundException2	java.io.FileNotFoundException
    //   177	1	3	localIOException3	java.io.IOException
    //   13	159	4	str	String
    // Exception table:
    //   from	to	target	type
    //   24	48	69	java/io/FileNotFoundException
    //   48	54	69	java/io/FileNotFoundException
    //   59	66	69	java/io/FileNotFoundException
    //   86	91	69	java/io/FileNotFoundException
    //   95	99	101	java/io/IOException
    //   15	24	108	java/io/IOException
    //   123	127	129	java/io/IOException
    //   80	84	136	java/io/IOException
    //   15	24	143	finally
    //   72	76	143	finally
    //   115	119	143	finally
    //   148	152	154	java/io/IOException
    //   24	48	162	finally
    //   48	54	162	finally
    //   59	66	162	finally
    //   86	91	162	finally
    //   15	24	170	java/io/FileNotFoundException
    //   24	48	177	java/io/IOException
    //   48	54	177	java/io/IOException
    //   59	66	177	java/io/IOException
    //   86	91	177	java/io/IOException
  }
  
  private void resolveCompatConfigFromJSON(JSONArray arg1)
  {
    if (??? == null) {
      return;
    }
    int j = 0;
    SparseArray localSparseArray;
    Object localObject2;
    int i2;
    int n;
    int k;
    for (int i = 0;; i = i2)
    {
      int m;
      int i1;
      try
      {
        localSparseArray = new SparseArray();
        m = 0;
        if (m >= ???.length()) {
          break;
        }
        localObject2 = ???.getJSONObject(m);
        i1 = j;
        i2 = i;
        if (!((JSONObject)localObject2).getString("name").equals("compatibility_config_values")) {
          break label259;
        }
        localObject2 = ((JSONObject)localObject2).getJSONArray("value");
        n = 0;
        k = i;
        i = j;
        i1 = i;
        i2 = k;
        if (n >= ((JSONArray)localObject2).length()) {
          break label259;
        }
        Object localObject3 = ((JSONArray)localObject2).getJSONObject(n);
        Object localObject4 = ((JSONObject)localObject3).getString("type");
        if (((String)localObject4).length() != 2) {
          break label371;
        }
        i1 = char2int(((String)localObject4).toCharArray());
        localObject3 = ((JSONObject)localObject3).getString("pkg");
        localObject4 = (ArrayList)localSparseArray.get(i1);
        if (localObject4 == null)
        {
          localObject4 = new ArrayList();
          ((ArrayList)localObject4).add(localObject3);
          localSparseArray.put(i1, localObject4);
        }
        for (;;)
        {
          j = i;
          if (i != 0) {
            break label380;
          }
          if (!isInConfigList(i1, (String)localObject3)) {
            break;
          }
          j = i;
          break label380;
          ((ArrayList)localObject4).add(localObject3);
        }
        return;
      }
      catch (Exception ???)
      {
        Slog.e("OemCompatibilityHelper", "resolveCompatConfigFromJSON, error message:" + ???.getMessage());
      }
      label253:
      j = 1;
      break label380;
      label259:
      m += 1;
      j = i1;
    }
    if (mTotalCount != i)
    {
      k = 1;
      label284:
      j |= k;
      if (DEBUG)
      {
        localObject2 = new StringBuilder().append("compatibility online config ");
        if (j == 0) {
          break label397;
        }
      }
    }
    label371:
    label380:
    label397:
    for (??? = "changed.";; ??? = "not change")
    {
      Slog.i("OemCompatibilityHelper", ???);
      if (j == 0) {
        break label253;
      }
      writeCompatConfigListXml("/data/system/oneplus_cpt_list.xml", localSparseArray);
      synchronized (sConfigLock)
      {
        this.mCompatConfigList = localSparseArray;
        mTotalCount = i;
        return;
      }
      for (;;)
      {
        n += 1;
        break;
        k += 1;
        i = j;
      }
      k = 0;
      break label284;
    }
  }
  
  /* Error */
  private void writeCompatConfigListXml(String paramString, SparseArray<ArrayList<String>> paramSparseArray)
  {
    // Byte code:
    //   0: invokestatic 411	android/util/Xml:newSerializer	()Lorg/xmlpull/v1/XmlSerializer;
    //   3: astore 8
    //   5: new 413	java/io/StringWriter
    //   8: dup
    //   9: invokespecial 414	java/io/StringWriter:<init>	()V
    //   12: astore 7
    //   14: aconst_null
    //   15: astore 6
    //   17: aconst_null
    //   18: astore 5
    //   20: aload 6
    //   22: astore_1
    //   23: new 416	java/text/SimpleDateFormat
    //   26: dup
    //   27: ldc_w 418
    //   30: invokespecial 419	java/text/SimpleDateFormat:<init>	(Ljava/lang/String;)V
    //   33: new 421	java/util/Date
    //   36: dup
    //   37: invokespecial 422	java/util/Date:<init>	()V
    //   40: invokevirtual 426	java/text/SimpleDateFormat:format	(Ljava/util/Date;)Ljava/lang/String;
    //   43: astore 9
    //   45: aload 6
    //   47: astore_1
    //   48: aload 8
    //   50: aload 7
    //   52: invokeinterface 432 2 0
    //   57: aload 6
    //   59: astore_1
    //   60: aload 8
    //   62: ldc_w 434
    //   65: iconst_1
    //   66: invokestatic 439	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   69: invokeinterface 443 3 0
    //   74: aload 6
    //   76: astore_1
    //   77: aload 8
    //   79: ldc_w 445
    //   82: invokeinterface 449 2 0
    //   87: pop
    //   88: aload 6
    //   90: astore_1
    //   91: aload 8
    //   93: aconst_null
    //   94: ldc_w 451
    //   97: invokeinterface 455 3 0
    //   102: pop
    //   103: aload 6
    //   105: astore_1
    //   106: aload 8
    //   108: aconst_null
    //   109: ldc_w 457
    //   112: aload 9
    //   114: invokeinterface 461 4 0
    //   119: pop
    //   120: aload 6
    //   122: astore_1
    //   123: aload 8
    //   125: ldc_w 445
    //   128: invokeinterface 449 2 0
    //   133: pop
    //   134: iconst_0
    //   135: istore_3
    //   136: aload 6
    //   138: astore_1
    //   139: iload_3
    //   140: aload_2
    //   141: invokevirtual 462	android/util/SparseArray:size	()I
    //   144: if_icmpge +140 -> 284
    //   147: aload 6
    //   149: astore_1
    //   150: aload_2
    //   151: iload_3
    //   152: invokevirtual 466	android/util/SparseArray:keyAt	(I)I
    //   155: istore 4
    //   157: aload 6
    //   159: astore_1
    //   160: aload_2
    //   161: iload 4
    //   163: invokevirtual 301	android/util/SparseArray:get	(I)Ljava/lang/Object;
    //   166: checkcast 144	java/util/ArrayList
    //   169: astore 9
    //   171: aload 6
    //   173: astore_1
    //   174: aload_0
    //   175: iload 4
    //   177: invokespecial 468	com/android/server/pm/OemCompatibilityHelper:int2string	(I)Ljava/lang/String;
    //   180: astore 10
    //   182: iconst_0
    //   183: istore 4
    //   185: aload 6
    //   187: astore_1
    //   188: iload 4
    //   190: aload 9
    //   192: invokevirtual 148	java/util/ArrayList:size	()I
    //   195: if_icmpge +369 -> 564
    //   198: aload 6
    //   200: astore_1
    //   201: aload 8
    //   203: ldc_w 470
    //   206: invokeinterface 449 2 0
    //   211: pop
    //   212: aload 6
    //   214: astore_1
    //   215: aload 8
    //   217: aconst_null
    //   218: aload 10
    //   220: invokeinterface 455 3 0
    //   225: pop
    //   226: aload 6
    //   228: astore_1
    //   229: aload 8
    //   231: aload 9
    //   233: iload 4
    //   235: invokevirtual 152	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   238: checkcast 229	java/lang/String
    //   241: invokeinterface 449 2 0
    //   246: pop
    //   247: aload 6
    //   249: astore_1
    //   250: aload 8
    //   252: aconst_null
    //   253: aload 10
    //   255: invokeinterface 473 3 0
    //   260: pop
    //   261: aload 6
    //   263: astore_1
    //   264: aload 8
    //   266: ldc_w 445
    //   269: invokeinterface 449 2 0
    //   274: pop
    //   275: iload 4
    //   277: iconst_1
    //   278: iadd
    //   279: istore 4
    //   281: goto -96 -> 185
    //   284: aload 6
    //   286: astore_1
    //   287: aload 8
    //   289: aconst_null
    //   290: ldc_w 451
    //   293: invokeinterface 473 3 0
    //   298: pop
    //   299: aload 6
    //   301: astore_1
    //   302: aload 8
    //   304: invokeinterface 476 1 0
    //   309: aload 6
    //   311: astore_1
    //   312: new 256	java/io/File
    //   315: dup
    //   316: ldc 19
    //   318: invokespecial 259	java/io/File:<init>	(Ljava/lang/String;)V
    //   321: astore_2
    //   322: aload 6
    //   324: astore_1
    //   325: aload_2
    //   326: invokevirtual 263	java/io/File:exists	()Z
    //   329: ifne +11 -> 340
    //   332: aload 6
    //   334: astore_1
    //   335: aload_2
    //   336: invokevirtual 479	java/io/File:createNewFile	()Z
    //   339: pop
    //   340: aload 6
    //   342: astore_1
    //   343: new 481	java/io/FileOutputStream
    //   346: dup
    //   347: ldc 19
    //   349: invokespecial 482	java/io/FileOutputStream:<init>	(Ljava/lang/String;)V
    //   352: astore_2
    //   353: aload_2
    //   354: aload 7
    //   356: invokevirtual 483	java/io/StringWriter:toString	()Ljava/lang/String;
    //   359: invokevirtual 487	java/lang/String:getBytes	()[B
    //   362: invokevirtual 491	java/io/FileOutputStream:write	([B)V
    //   365: aload_2
    //   366: ifnull +7 -> 373
    //   369: aload_2
    //   370: invokevirtual 492	java/io/FileOutputStream:close	()V
    //   373: return
    //   374: astore_1
    //   375: ldc 61
    //   377: new 169	java/lang/StringBuilder
    //   380: dup
    //   381: invokespecial 170	java/lang/StringBuilder:<init>	()V
    //   384: ldc_w 494
    //   387: invokevirtual 174	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   390: aload_1
    //   391: invokevirtual 330	java/io/IOException:getMessage	()Ljava/lang/String;
    //   394: invokevirtual 174	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   397: invokevirtual 182	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   400: invokestatic 333	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   403: pop
    //   404: aload_1
    //   405: invokevirtual 368	java/io/IOException:printStackTrace	()V
    //   408: goto -35 -> 373
    //   411: astore_1
    //   412: aload 5
    //   414: astore_2
    //   415: aload_1
    //   416: astore 5
    //   418: aload_2
    //   419: astore_1
    //   420: ldc 61
    //   422: new 169	java/lang/StringBuilder
    //   425: dup
    //   426: invokespecial 170	java/lang/StringBuilder:<init>	()V
    //   429: ldc_w 496
    //   432: invokevirtual 174	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   435: aload 5
    //   437: invokevirtual 317	java/lang/Exception:getMessage	()Ljava/lang/String;
    //   440: invokevirtual 174	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   443: invokevirtual 182	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   446: invokestatic 333	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   449: pop
    //   450: aload_2
    //   451: astore_1
    //   452: aload 5
    //   454: invokevirtual 497	java/lang/Exception:printStackTrace	()V
    //   457: aload_2
    //   458: ifnull -85 -> 373
    //   461: aload_2
    //   462: invokevirtual 492	java/io/FileOutputStream:close	()V
    //   465: return
    //   466: astore_1
    //   467: ldc 61
    //   469: new 169	java/lang/StringBuilder
    //   472: dup
    //   473: invokespecial 170	java/lang/StringBuilder:<init>	()V
    //   476: ldc_w 494
    //   479: invokevirtual 174	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   482: aload_1
    //   483: invokevirtual 330	java/io/IOException:getMessage	()Ljava/lang/String;
    //   486: invokevirtual 174	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   489: invokevirtual 182	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   492: invokestatic 333	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   495: pop
    //   496: aload_1
    //   497: invokevirtual 368	java/io/IOException:printStackTrace	()V
    //   500: return
    //   501: astore_2
    //   502: aload_1
    //   503: ifnull +7 -> 510
    //   506: aload_1
    //   507: invokevirtual 492	java/io/FileOutputStream:close	()V
    //   510: aload_2
    //   511: athrow
    //   512: astore_1
    //   513: ldc 61
    //   515: new 169	java/lang/StringBuilder
    //   518: dup
    //   519: invokespecial 170	java/lang/StringBuilder:<init>	()V
    //   522: ldc_w 494
    //   525: invokevirtual 174	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   528: aload_1
    //   529: invokevirtual 330	java/io/IOException:getMessage	()Ljava/lang/String;
    //   532: invokevirtual 174	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   535: invokevirtual 182	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   538: invokestatic 333	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   541: pop
    //   542: aload_1
    //   543: invokevirtual 368	java/io/IOException:printStackTrace	()V
    //   546: goto -36 -> 510
    //   549: astore 5
    //   551: aload_2
    //   552: astore_1
    //   553: aload 5
    //   555: astore_2
    //   556: goto -54 -> 502
    //   559: astore 5
    //   561: goto -143 -> 418
    //   564: iload_3
    //   565: iconst_1
    //   566: iadd
    //   567: istore_3
    //   568: goto -432 -> 136
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	571	0	this	OemCompatibilityHelper
    //   0	571	1	paramString	String
    //   0	571	2	paramSparseArray	SparseArray<ArrayList<String>>
    //   135	433	3	i	int
    //   155	125	4	j	int
    //   18	435	5	str1	String
    //   549	5	5	localObject1	Object
    //   559	1	5	localException	Exception
    //   15	326	6	localObject2	Object
    //   12	343	7	localStringWriter	java.io.StringWriter
    //   3	300	8	localXmlSerializer	org.xmlpull.v1.XmlSerializer
    //   43	189	9	localObject3	Object
    //   180	74	10	str2	String
    // Exception table:
    //   from	to	target	type
    //   369	373	374	java/io/IOException
    //   23	45	411	java/lang/Exception
    //   48	57	411	java/lang/Exception
    //   60	74	411	java/lang/Exception
    //   77	88	411	java/lang/Exception
    //   91	103	411	java/lang/Exception
    //   106	120	411	java/lang/Exception
    //   123	134	411	java/lang/Exception
    //   139	147	411	java/lang/Exception
    //   150	157	411	java/lang/Exception
    //   160	171	411	java/lang/Exception
    //   174	182	411	java/lang/Exception
    //   188	198	411	java/lang/Exception
    //   201	212	411	java/lang/Exception
    //   215	226	411	java/lang/Exception
    //   229	247	411	java/lang/Exception
    //   250	261	411	java/lang/Exception
    //   264	275	411	java/lang/Exception
    //   287	299	411	java/lang/Exception
    //   302	309	411	java/lang/Exception
    //   312	322	411	java/lang/Exception
    //   325	332	411	java/lang/Exception
    //   335	340	411	java/lang/Exception
    //   343	353	411	java/lang/Exception
    //   461	465	466	java/io/IOException
    //   23	45	501	finally
    //   48	57	501	finally
    //   60	74	501	finally
    //   77	88	501	finally
    //   91	103	501	finally
    //   106	120	501	finally
    //   123	134	501	finally
    //   139	147	501	finally
    //   150	157	501	finally
    //   160	171	501	finally
    //   174	182	501	finally
    //   188	198	501	finally
    //   201	212	501	finally
    //   215	226	501	finally
    //   229	247	501	finally
    //   250	261	501	finally
    //   264	275	501	finally
    //   287	299	501	finally
    //   302	309	501	finally
    //   312	322	501	finally
    //   325	332	501	finally
    //   335	340	501	finally
    //   343	353	501	finally
    //   420	450	501	finally
    //   452	457	501	finally
    //   506	510	512	java/io/IOException
    //   353	365	549	finally
    //   353	365	559	java/lang/Exception
  }
  
  public String abiOverride(String paramString1, String paramString2)
  {
    if (paramString2 == null) {
      return paramString1;
    }
    if ((isInConfigList(443, paramString2)) && (Build.SUPPORTED_32_BIT_ABIS.length > 0)) {
      return Build.SUPPORTED_32_BIT_ABIS[0];
    }
    if ((isInConfigList(444, paramString2)) && (Build.SUPPORTED_64_BIT_ABIS.length > 0)) {
      return Build.SUPPORTED_64_BIT_ABIS[0];
    }
    if ((isInConfigList(445, paramString2)) && (Build.SUPPORTED_32_BIT_ABIS.length > 1)) {
      return Build.SUPPORTED_64_BIT_ABIS[1];
    }
    return paramString1;
  }
  
  public int convertAbi2Int(String paramString)
  {
    return ((Integer)ABI_TO_INT_MAP.get(paramString)).intValue();
  }
  
  public String convertAbi2String(int paramInt)
  {
    return (String)ABI_TO_STRING_MAP.get(Integer.valueOf(paramInt));
  }
  
  public void customizeNativeLibrariesIfNeeded(PackageParser.Package paramPackage)
  {
    Object localObject = new File(paramPackage.applicationInfo.nativeLibraryDir);
    if (((File)localObject).isDirectory())
    {
      localObject = ((File)localObject).listFiles();
      int i = 0;
      int j = localObject.length;
      if (i < j)
      {
        ApplicationInfo localApplicationInfo1 = localObject[i];
        String str = localApplicationInfo1.getName();
        if (str == null) {}
        for (;;)
        {
          i += 1;
          break;
          if (("libmg20pbase.so".equals(str)) && (localApplicationInfo1.length() == 42156L))
          {
            ApplicationInfo localApplicationInfo2 = paramPackage.applicationInfo;
            localApplicationInfo2.privateFlags |= 0x4000;
          }
          if (("libbaiduprotect.so".equals(str)) && (isBaiduProtectedApk(localApplicationInfo1.length(), paramPackage.applicationInfo.primaryCpuAbi)))
          {
            localApplicationInfo1 = paramPackage.applicationInfo;
            localApplicationInfo1.privateFlags |= 0x8000;
          }
        }
      }
    }
  }
  
  public void customizePackageIfNeeded(PackageParser.Package paramPackage)
  {
    try
    {
      paramPackage.cpuAbiOverride = abiOverride(paramPackage.cpuAbiOverride, paramPackage.packageName);
      customizeHardwareAccelerateIfNeeded(paramPackage);
      customizeHardwareAccelerateForActivityIfNeeded(paramPackage);
      customizeVMSafeModeIfNeeded(paramPackage);
      customizeTargetSdkIfNeeded(paramPackage);
      customizePrivateFlagsIfNeeded(paramPackage);
      return;
    }
    catch (RuntimeException paramPackage) {}
  }
  
  public String dumpToString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("CompatibilityInfo:\n");
    Object localObject1 = sConfigLock;
    int i = 0;
    try
    {
      while (i < this.mCompatConfigList.size())
      {
        int j = this.mCompatConfigList.keyAt(i);
        localStringBuilder.append("type = ").append(j);
        ArrayList localArrayList = (ArrayList)this.mCompatConfigList.get(j);
        localStringBuilder.append(", value = ").append(localArrayList).append("\n");
        i += 1;
      }
      return localStringBuilder.toString();
    }
    finally
    {
      localObject2 = finally;
      throw ((Throwable)localObject2);
    }
  }
  
  public void initCompatOnlineConfig()
  {
    this.mCompatConfigObserver = new ConfigObserver(mContext, null, new CompatConfigUpdater(), "CompatConfigList");
    this.mCompatConfigObserver.register();
  }
  
  public boolean isInConfigList(int paramInt, String paramString)
  {
    synchronized (sConfigLock)
    {
      if (this.mCompatConfigList.indexOfKey(paramInt) >= 0)
      {
        boolean bool = ((ArrayList)this.mCompatConfigList.get(paramInt)).contains(paramString);
        if (bool) {
          return true;
        }
      }
      return false;
    }
  }
  
  class CompatConfigUpdater
    implements ConfigObserver.ConfigUpdater
  {
    CompatConfigUpdater() {}
    
    public void updateConfig(JSONArray paramJSONArray)
    {
      OemCompatibilityHelper.-wrap0(OemCompatibilityHelper.this, paramJSONArray);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/pm/OemCompatibilityHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */